import { Component, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { HttpResponse } from '@angular/common/http';

import { CompanyService } from 'app/entities/company/company.service';
import { AppUserService } from 'app/entities/app-user/app-user.service';
import { JobService } from 'app/entities/job/job.service';
import { ProjectService } from 'app/entities/project/project.service';
import { CustomerService } from 'app/entities/customer/customer.service';

import { ICompany } from 'app/shared/model/company.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { IJob } from 'app/shared/model/job.model';
import { IProject } from 'app/shared/model/project.model';
import { IAppUser } from 'app/shared/model/app-user.model';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-timesheet',
  templateUrl: './timesheet.component.html',
})
export class TimesheetComponent implements OnInit {
  currentAccount: Account | null = null;
  companies: ICompany[] = [];
  customers: ICustomer[] = [];
  projects: IProject[] = [];
  jobs: IJob[] = [];
  employeesList: IUser[] = [];
  selectedEmployee: IUser | null = null;
  company: ICompany | null = null;
  currentEmployee: IUser | null = null;

  searchCustomer = '';
  searchJob = '';
  searchProject = '';

  date: Date = new Date();
  dateCopy: Date = new Date();
  nbrOfColumns = 0; //on init prend le nombre de jour dans le mois.
  monthName = '';
  months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];

  constructor(
    protected jobService: JobService,
    protected customerService: CustomerService,
    protected projectService: ProjectService,
    protected companyService: CompanyService,
    protected appUserService: AppUserService,
    protected userService: UserService,
    protected accountService: AccountService,
    protected eventManager: JhiEventManager
  ) {}

  ngOnInit(): void {
    this.setNumberOfColumns(this.date.getMonth(), this.date.getFullYear());

    this.accountService.identity().subscribe(account => (this.currentAccount = account));

    //config le reste

    if (this.currentAccount) {
      //compelete the currentAccount.

      //define the currentEMployee
      //if admin|current_admin=> allusers et currentEmployee
      if (this.isAdmin()) {
        //getAllCompanies
      } else {
        this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
          this.currentAccount && res.body ? (this.currentAccount.companyId = res.body.companyId) : null;
        });
        //employee define, allemployees,
        this.currentEmployee = this.currentAccount;
        //current employee = account
        if (this.isCustomerAdmin()) {
          this.currentAccount.companyId //not working
            ? this.userService.findAllByCompany(this.currentAccount.companyId).subscribe((res: HttpResponse<IUser[]>) => {
                res.body ? (this.employeesList = res.body) : null;
              })
            : null;
          console.log(this.employeesList);
        }
        this.jobService.findJobsByUserId(this.currentEmployee?.id).subscribe((res: HttpResponse<IJob[]>) => {
          res.body ? (this.jobs = res.body) : null;
          console.log('jobs');
          console.log(this.jobs);
        });
        console.log('project');

        // this.projectService.findProjectsByUserId(this.currentEmployee?.id).subscribe((res: HttpResponse<IProject[]>) => {
        //   res.body ? (this.projects = res.body) : null;
        //   console.log(this.projects);
        // });

        console.log('customer');
        this.customerService.findCustomersByUserId(this.currentEmployee?.id).subscribe((res: HttpResponse<ICustomer[]>) => {
          res.body ? (this.customers = res.body) : null;
          console.log(this.customers);
          this.getProjects(this.customers);
        });

        this.currentEmployee.companyId ? this.setCompany(this.currentEmployee.companyId) : null;

        //getAllUsersByCompany=>changer model, rajouter les performances
        // else{
        //   //company=find company By id
        // }
        console.log(this.company);
      }
    }
  }

  public getProjects(customers: ICustomer[]): IProject[] {
    let projects: IProject[] = [];
    projects = [
      ...projects.filter(p => {
        p.jobs.some(j => j.appUsers?.some(ap => ap.id === this.currentEmployee?.id));
      }),
    ];
    console.log(projects);
    return projects;
  }

  private getAllCompanies(): void {
    //admin
    //appel service
  }

  private getAllEmployeesByCompany(): void {
    //admin && customer admin
    this.currentEmployee?.companyId
      ? this.userService.findAllByCompany(this.currentEmployee.companyId).subscribe((res: HttpResponse<IUser[]>) => {
          res.body ? (this.employeesList = res.body) : null;
        })
      : null;
    console.log(this.employeesList);
  }

  public setCompany(companyId: number): void {
    this.companyService.find(companyId).subscribe((res: HttpResponse<ICompany>) => {
      res.body ? (this.company = res.body) : null;
      console.log(this.company);
    });
  }

  private isAdmin(): boolean {
    return this.accountService.hasAnyAuthority('ROLE_ADMIN');
  }

  private isCustomerAdmin(): boolean {
    return this.accountService.hasAnyAuthority('ROLE_CUSTOMER_ADMIN') && !this.isAdmin();
  }

  private setNumberOfColumns(mounth: number, year: number): void {
    this.nbrOfColumns = this.getNumberOfDays(mounth, year);
    console.log(this.nbrOfColumns);
  }

  private getNumberOfDays(mounth: number, year: number): number {
    return new Date(year, mounth, 0).getDate();
  }

  public nextMouth(): void {
    this.dateCopy.setMonth(this.date.getMonth() + 1);
    this.setNumberOfColumns(this.dateCopy.getMonth(), this.dateCopy.getFullYear());
  }

  public previousMouth(): void {
    this.dateCopy.setMonth(this.date.getMonth() - 1);
    this.setNumberOfColumns(this.dateCopy.getMonth(), this.dateCopy.getFullYear());
  }

  private setMonthName(): void {
    this.monthName = 'Date: ' + this.months[this.dateCopy.getMonth()] + ' ' + this.dateCopy.getFullYear();
  }
}
