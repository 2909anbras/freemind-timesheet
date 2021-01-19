import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { JhiLanguageService } from 'ng-jhipster';
import { SessionStorageService } from 'ngx-webstorage';
import { HttpResponse } from '@angular/common/http';
import { Directive, ElementRef, Renderer2, Input } from '@angular/core';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { Subscription, combineLatest } from 'rxjs';

import { IAppUser } from 'app/shared/model/app-user.model';
import { Account } from 'app/core/user/account.model';
import { IProject } from 'app/shared/model/project.model';
import { Report, IReport } from 'app/shared/model/report.model';
import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company/company.service';
import { ICustomer, Customer } from 'app/shared/model/customer.model';
import { IJob, Job } from 'app/shared/model/job.model';
import { IUser, User } from 'app/core/user/user.model';

import { JobFilterPipe } from 'app/shared/pipe/filter/jobFilterPipe';
import { ProjectFilterPipe } from 'app/shared/pipe/filter/projectFilterPipe';
import { CustomerFilterPipe } from 'app/shared/pipe/filter/customerFilterPipe';
import { CompanyFilterPipe } from 'app/shared/pipe/filter/companyFilterPipe';
import { KeyvaluePipe } from 'app/shared/pipe/filter/keyvaluePipe';
import { UserFilterPipe } from 'app/shared/pipe/filter/userFilterPipe';

import { UserService } from 'app/core/user/user.service';
import { AppUserService } from 'app/entities/app-user/app-user.service';
import { AccountService } from 'app/core/auth/account.service';
import { FullReportService } from './full-report.service';
import { TimesheetService } from 'app/timesheet/timesheet.service';
import { Moment } from 'moment';

@Component({
  selector: 'jhi-full-report',
  templateUrl: './full-report.component.html',
  // changeDetection: ChangeDetectionStrategy.OnPush
})
export class FullReportComponent implements OnInit {
  showCompanies: ICompany[] = [];
  allCompanies: ICompany[] = [];
  endDate: any;
  startDate: any;

  projectsReport: IProject[] = [];
  projectsFiltered: IProject[] = [];
  customers: ICustomer[] = [];
  customersFiltered: ICustomer[] = [];
  jobs: IJob[] = [];
  jobsFiltered: IJob[] = [];
  users: IUser[] = [];
  usersFiltered: IUser[] = [];

  reportCustomers: ICustomer[] = [];
  reportProjects: IProject[] = [];
  reportJobs: IJob[] = [];
  reportUsers: IUser[] = [];

  userHidden = false;
  companyHidden = false;
  customerHidden = false;
  jobHidden = false;
  projectHidden = false;

  searchUser = '';
  searchCompany = '';
  searchCustomer = '';
  searchJob = '';
  searchProject = '';

  searchCustomerState = 'All';
  searchUserState = 'All';
  searchProjectState = 'All';
  searchJobState = 'All';
  searchCompanyState = 'All';

  enabledStateList = ['Enable', 'Disabled', 'All'];

  currentAccount: Account | null = null;
  report: Report = new Report();

  constructor(
    private jobFilter: JobFilterPipe,
    private projectFilter: ProjectFilterPipe,
    private customerFilter: CustomerFilterPipe,
    private userFilter: UserFilterPipe,
    private companyFilter: CompanyFilterPipe,

    protected timesheetService: TimesheetService,
    protected reportService: FullReportService,
    protected userService: UserService,
    protected companyService: CompanyService,
    protected appUserService: AppUserService,
    protected accountService: AccountService,
    protected eventManager: JhiEventManager
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));

    // if (this.currentAccount && !this.accountService.hasAnyAuthority('ROLE_ADMIN'))
    if (this.currentAccount)
      if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
        this.companyService.query().subscribe((res: HttpResponse<ICompany[]>) => {
          if (res.body) {
            this.showCompanies = res.body;
            this.allCompanies = res.body;
            this.sortingCompanies();
            console.log(this.showCompanies);
          }
        });
      } else {
        this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
          this.currentAccount && res.body ? (this.currentAccount.companyId = res.body.companyId) : null;
          if (this.currentAccount?.companyId) {
            this.userService.findAllByCompany(this.currentAccount.companyId).subscribe((res: HttpResponse<IUser[]>) => {
              res.body ? (this.users = res.body) : null;
            });
            this.companyService.find(this.currentAccount?.companyId).subscribe((res: HttpResponse<ICompany>) => {
              res.body ? this.allCompanies?.push(res.body) : null;
              this.showCompanies = this.allCompanies;
              console.log(this.showCompanies);
            });
          }
        });
      }
  }
  private sortingCompanies(): void {
    this.showCompanies.sort((a, b) => {
      if (a.name && b.name) {
        if (a.name.localeCompare(b.name) === 1) {
          return 1;
        } else if (a.name.localeCompare(b.name) === -1) {
          return -1;
        } else return 1;
      } else return 1;
    });
  }

  public getCustomers(company: ICompany): ICustomer[] {
    this.customersFiltered = this.customerFilter.transform(company.customers, this.searchCustomer, this.searchCustomerState);
    return this.customersFiltered;
  }

  public getProjects(customer: ICustomer): IProject[] {
    this.projectsFiltered = this.projectFilter.transform(customer.projects, this.searchProject, this.searchProjectState);
    return this.projectsFiltered;
  }
  public getJobs(project: IProject): IJob[] {
    this.jobs = this.jobFilter.transform(project.jobs, this.searchJob, this.searchJobState);
    return this.jobs;
  }
  public getUsers(job: IJob): IUser[] {
    const users: IUser[] = [];
    this.usersFiltered = job.appUsers.filter(ap => {
      this.users.some(user => {
        user.id === ap.id ? users.push(user) : null;
      });
    });
    this.usersFiltered = users;
    this.usersFiltered = this.userFilter.transform(this.usersFiltered, this.searchUser, this.searchUserState);
    return this.usersFiltered;
  }
  public getCompanies(): ICompany[] {
    this.showCompanies = this.companyFilter.transform(this.allCompanies, this.searchCompany);
    return this.showCompanies;
  }

  public switchCompanyHidden(): void {
    this.companyHidden = !this.companyHidden;
  }
  public switchCustomerHidden(): void {
    this.customerHidden = !this.customerHidden;
  }
  public switchProjectHidden(): void {
    this.projectHidden = !this.projectHidden;
  }
  public switchUserHidden(): void {
    this.userHidden = !this.userHidden;
  }
  public makeReport(): void {
    //id? ou object? id pour request, ça sera plus propre.
    this.fillReport();
    this.reportService.create(this.report).subscribe();
  }
  private fillReport(): void {
    this.report.companiesId = this.showCompanies.map(c => c.id!);
    this.fillCustomers();
    this.fillProjects();
    this.fillJobs();
    this.fillUsers();
    // this.report.customersId = this.customersFiltered.map(c => c.id!);
    // this.report.projectsId = this.projectsReport.map(c => c.id!);
    // this.report.usersId = this.usersFiltered.map(c => c.id!);
    // this.report.jobsId = this.jobs.map(c => c.id!);
    // this.report.companiesId = this.showCompanies.map(c => c.id!);
    this.report.setDates(this.startDate, this.endDate);
  }

  private fillCustomers(): void {
    //toujours qu'une company
    //récupérer tous les customers de la company
    this.reportCustomers = this.customerFilter.transform(this.showCompanies[0].customers, this.searchCustomer, this.searchCustomerState);
    this.report.customersId = this.reportCustomers.map(c => c.id!);
  }

  private fillProjects(): void {
    //dans la liste de customer on récupère tous les projects qui sont dans le filtre
    let bool: boolean;
    this.searchProjectState === 'All' ? null : this.searchProjectState === 'Enable' ? (bool = true) : (bool = false);
    this.reportCustomers.forEach(c => {
      c.projects.forEach(p => {
        p.name?.toLocaleLowerCase().includes(this.searchProject) && !this.reportProjects.find(x => x.id === p.id)
          ? this.reportProjects.push(p)
          : null;
      });
    });
    if (this.searchProjectState !== 'All') {
      this.reportProjects = [...this.reportProjects.filter(p => p.enable === bool)];
    }
    this.report.projectsId = this.reportProjects.map(c => c.id!);
  }

  private fillJobs(): void {
    let bool: boolean;
    this.searchJobState === 'All' ? null : this.searchJobState === 'Enable' ? (bool = true) : (bool = false);
    this.reportProjects.forEach(p => {
      p.jobs.forEach(j => {
        j.name?.toLocaleLowerCase().includes(this.searchJob) && !this.reportJobs.find(x => x.id === j.id) ? this.reportJobs.push(j) : null;
      });
    });
    if (this.searchJobState !== 'All') {
      this.reportJobs = [...this.reportJobs.filter(j => j.enable === bool)];
    }
    this.report.jobsId = this.reportJobs.map(c => c.id!);
  }

  private fillUsers(): void {
    //à partir des users, filtrer avec les arguments du filtre
    //et voir si présents dans les jobs.
    let bool: boolean;
    this.searchUserState === 'All' ? null : this.searchUserState === 'Enable' ? (bool = true) : (bool = false);
    //1 liste de users à partir de jobs 2 filtrer
    let tmp: IUser[] = [];
    this.users.forEach(u => {
      this.reportJobs.forEach(j => {
        j.appUsers.forEach(ap => {
          if (ap.id === u.id && !tmp.includes(u)) tmp.push(u);
        });
      });
    });

    this.reportUsers = [...tmp.filter(c => c.login?.toLocaleLowerCase().includes(this.searchUser))];
    if (this.searchUserState !== 'All') {
      this.reportUsers = [...this.reportUsers.filter(u => u.activated === bool)];
    }
    this.report.usersId = this.reportUsers.map(c => c.id!);
  }
}
