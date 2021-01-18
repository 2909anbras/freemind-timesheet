import { Component, OnInit, AfterViewChecked, ChangeDetectionStrategy, OnDestroy } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { TimesheetService } from 'app/timesheet/timesheet.service';

import { CompanyService } from 'app/entities/company/company.service';
import { AppUserService } from 'app/entities/app-user/app-user.service';
import { JobService } from 'app/entities/job/job.service';
import { ProjectService } from 'app/entities/project/project.service';
import { CustomerService } from 'app/entities/customer/customer.service';

import { PerformanceCreateDialogComponent } from 'app/entities/performance/performance-create-dialog.component';
import { ChangeDetectorRef } from '@angular/core';

import { ICompany } from 'app/shared/model/company.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { IJob } from 'app/shared/model/job.model';
import { IProject, Project } from 'app/shared/model/project.model';
import { IAppUser } from 'app/shared/model/app-user.model';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IPerformance } from 'app/shared/model/performance.model';

type SelectableEntity = IUser;

@Component({
  selector: 'jhi-timesheet',
  templateUrl: './timesheet.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  // styleUrls: ['timesheet.component.css'],
})
export class TimesheetComponent implements OnInit, AfterViewChecked, OnDestroy {
  currentAccount: Account | null = null;
  companies: ICompany[] = [];
  customers: ICustomer[] = [];
  projects: IProject[] = [];
  jobs: IJob[] = [];
  employees: IUser[] = [];
  selectedEmployee: IUser | null = null;
  company: ICompany | null = null;
  currentEmployee: IUser | null = null;

  companySelected: ICompany | null = null;

  loop: [] = [];

  searchCompany = '';
  searchCustomer = '';
  searchJob = '';
  searchProject = '';

  currentIndex = 0;
  day = 1;
  cptDay = 0;
  cptTd = 0;

  eventSubscriber?: Subscription;

  date: Date = new Date();
  dateCopy: Date = new Date();
  nbrOfColumns = 0; //on init prend le nombre de jour dans le mois.
  monthName = '';
  months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
  days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  disableTd = false;

  constructor(
    protected timesheetService: TimesheetService,
    private cdRef: ChangeDetectorRef,
    protected jobService: JobService,
    protected customerService: CustomerService,
    protected projectService: ProjectService,
    protected companyService: CompanyService,
    protected appUserService: AppUserService,
    protected userService: UserService,
    protected accountService: AccountService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  ngAfterViewChecked(): void {
    // this.nbrOfColumns = this.getNumberOfDays(this.dateCopy.getMonth(), this.dateCopy.getFullYear());
    const firstDay = new Date(this.dateCopy.getFullYear(), this.dateCopy.getMonth(), 1);
    this.cptDay = firstDay.getDay();
    this.cdRef.detectChanges();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  ngOnInit(): void {
    this.timesheetChange();
    this.setNumberOfColumns(this.date.getMonth(), this.date.getFullYear());

    this.dateCopy = new Date(this.date);
    this.cptTd = new Date(this.dateCopy.getFullYear(), this.dateCopy.getMonth(), 1).getDay();
    this.monthName = this.months[this.date.getMonth()];
    this.accountService.identity().subscribe(account => (this.currentAccount = account));

    this.setMonthName();
    if (this.currentAccount) {
      if (this.isAdmin()) {
        this.companyService.query(null).subscribe((res: HttpResponse<ICompany[]>) => {
          res.body ? (this.companies = res.body) : null;
        });
      } else {
        this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
          this.currentAccount && res.body ? (this.currentAccount.companyId = res.body.companyId) : null;
          this.currentEmployee = this.currentAccount;
          if (this.isCustomerAdmin() && this.currentEmployee) {
            this.currentEmployee.companyId
              ? this.userService.findAllByCompany(this.currentEmployee.companyId).subscribe((res: HttpResponse<IUser[]>) => {
                  res.body ? (this.employees = res.body) : null;
                })
              : null;
          }
          this.setCustomers();
          this.currentEmployee?.companyId ? this.setCompany(this.currentEmployee.companyId) : null;
        });
      }
    }
  }

  private setEmployees(companyId?: number): IUser[] {
    if (companyId)
      this.userService.findAllByCompany(companyId).subscribe((res: HttpResponse<IUser[]>) => {
        res.body ? (this.employees = res.body) : null;
        return res.body;
      });
    return [];
  }

  public setCustomers(): void {
    if (this.currentEmployee) {
      this.customerService.findCustomersByUserId(this.currentEmployee?.id).subscribe((res: HttpResponse<ICustomer[]>) => {
        res.body ? (this.customers = res.body) : (this.customers = []);
      });
    }
  }

  public getProjects(customer: ICustomer): IProject[] {
    const projects: IProject[] = [];
    customer.projects.forEach(p => {
      p.jobs.forEach(j => {
        if (j.appUsers?.some(ap => ap.id === this.currentEmployee?.id)) {
          projects.some(x => p.id === x.id) ? null : projects.push(p);
        }
      });
    });
    return projects;
  }

  public getJobs(project: IProject): IJob[] {
    // return project.getJobsByUser(this.currentEmployee?.id);
    const jobs: IJob[] = [];
    project.jobs.forEach(j => {
      if (j.appUsers?.some(ap => ap.id === this.currentEmployee?.id)) {
        jobs.push(j);
      }
    });
    return jobs;
  }

  private getAllCompanies(): void {
    //admin
    //appel service
  }

  private getAllEmployeesByCompany(): void {
    //admin && customer admin
    this.currentEmployee?.companyId
      ? this.userService.findAllByCompany(this.currentEmployee.companyId).subscribe((res: HttpResponse<IUser[]>) => {
          res.body ? (this.employees = res.body) : null;
        })
      : null;
  }

  public setCompany(companyId: number): void {
    this.companyService.find(companyId).subscribe((res: HttpResponse<ICompany>) => {
      res.body ? (this.company = res.body) : null;
    });
  }

  private isAdmin(): boolean {
    return this.accountService.hasAnyAuthority('ROLE_ADMIN');
  }

  private isCustomerAdmin(): boolean {
    return this.accountService.hasAnyAuthority('ROLE_CUSTOMER_ADMIN') && !this.isAdmin();
  }

  private setNumberOfColumns(mounth: number, year: number): void {
    this.nbrOfColumns = this.getNumberOfDays(mounth + 1, year);
    const tmpArray: [] = [].constructor(this.nbrOfColumns);
    this.loop = tmpArray;
  }

  private getNumberOfDays(mounth: number, year: number): number {
    return new Date(year, mounth, 0).getDate();
  }

  public nextMonth(): void {
    this.dateCopy.setMonth(this.dateCopy.getMonth() + 1);
    this.cptTd = new Date(this.dateCopy.getFullYear(), this.dateCopy.getMonth(), 1).getDay();
    this.setMonthName();
    this.setNumberOfColumns(this.dateCopy.getMonth(), this.dateCopy.getFullYear());
  }

  public previousMonth(): void {
    this.dateCopy.setMonth(this.dateCopy.getMonth() - 1);
    this.cptTd = new Date(this.dateCopy.getFullYear(), this.dateCopy.getMonth(), 1).getDay();
    this.setMonthName();
    this.setNumberOfColumns(this.dateCopy.getMonth(), this.dateCopy.getFullYear());
  }

  public currentMonth(): void {
    this.dateCopy = new Date(this.date);
    this.cptTd = new Date(this.dateCopy.getFullYear(), this.dateCopy.getMonth(), 1).getDay();
    this.setMonthName();
    this.setNumberOfColumns(this.dateCopy.getMonth(), this.dateCopy.getFullYear());
  }

  public getDay(i: number): string {
    let str = '';
    this.day = i + 1;

    if (this.cptDay < this.days.length) {
      str = ' ' + this.days[this.cptDay] + ' \n ' + this.day + ' ';
    } else {
      this.cptDay = 0;
      str = ' ' + this.days[this.cptDay] + '\n ' + this.day + ' ';
    }
    this.cptDay++;
    return str;
  }

  private setMonthName(): void {
    this.monthName = 'Date: ' + this.months[this.dateCopy.getMonth()] + ' ' + this.dateCopy.getFullYear();
  }

  onChangeEmployee(): void {
    this.setCustomers();
  }

  onChangeCompany(): void {
    this.employees = this.setEmployees(this.companySelected?.id);
    if (this.employees.length === 0) {
      this.customers = [];
      this.currentEmployee = null;
    }
  }

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }

  getPerformanceHours(job: IJob, i: number): number {
    const perf: any = this.getPerformance(job, i);
    if (perf) return perf.hours;
    else return 0;
  }

  getPerformance(job: IJob, i: number): any {
    const date: Date = new Date(this.dateCopy.getFullYear(), this.dateCopy.getMonth(), i + 1);
    let perf: any = null;
    let a: any;
    //add some pour améliorer perf. If some alors foreach
    if (job.performances) {
      //vérifier que id corresponde à current.(si current)
      for (const p of job?.performances) {
        if (p.date) {
          a = new Date(p.date.toString());
        }
        if (p.date && this.compareDate(a, date) && p.appUserId === this.currentEmployee?.id) {
          perf = p;
        }
      }
    }
    return perf;
  }

  private compareDate(d: Date, dBis: Date): boolean {
    if (d.getDate() === dBis.getDate() && d.getFullYear() === dBis.getFullYear() && d.getMonth() === dBis.getMonth()) return true;
    else return false;
  }

  timesheetChange(): void {
    this.eventSubscriber = this.eventManager.subscribe('timesheetModification', () => this.refresh());
  }

  refresh(): void {
    this.setCustomers();
  }

  encodeHours(p: IProject, j: IJob, c: ICustomer, i: number): void {
    const modalRef = this.modalService.open(PerformanceCreateDialogComponent, { size: 'lg', backdrop: 'static', windowClass: 'modal-xl' });
    modalRef.componentInstance.job = j;
    modalRef.componentInstance.project = p;
    modalRef.componentInstance.customer = c;
    modalRef.componentInstance.performance = this.getPerformance(j, i);
    modalRef.componentInstance.currentEmployee = this.currentEmployee;
    modalRef.componentInstance.date = new Date(this.dateCopy.getFullYear(), this.dateCopy.getMonth(), i + 1);
  }

  public IsDisable(i: number): boolean {
    const bool = true;
    return !(new Date() < new Date(this.dateCopy.getFullYear(), this.dateCopy.getMonth(), i + 1));
  }

  public makeReport(): void {
    console.log(this.currentEmployee!.id);

    if (this.currentEmployee) {
      console.log('dedans');
      this.timesheetService.create(this.dateCopy, this.currentEmployee.id).subscribe(res => console.log(res));
    }
  }
}
// this.customerService.findCustomersByUserId(this.currentEmployee?.id).subscribe((res: HttpResponse<ICustomer[]>) => {
//   res.body ? (this.customers = res.body) : null;
//   console.log(this.customers);
// });
// if(this.currentEmployee)
// this.currentEmployee.companyId ? this.setCompany(this.currentEmployee.companyId) : null;

//getAllUsersByCompany=>changer model, rajouter les performances
// else{
//   //company=find company By id
// }
// console.log(this.company);

// job.performances?.some(p => {
//   p.date === date;
//   perf = p;
// });
