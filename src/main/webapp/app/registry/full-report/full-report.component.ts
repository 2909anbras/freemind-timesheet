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

  projects: IProject[] = [];
  projectsFiltered: IProject[] = [];
  customers: ICustomer[] = [];
  customersFiltered: ICustomer[] = [];
  jobs: IJob[] = [];
  jobsFiltered: IJob[] = [];
  users: IUser[] = [];
  usersFiltered: IUser[] = [];

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
    this.projects = this.projectFilter.transform(customer.projects, this.searchProject, this.searchProjectState);
    return this.projects;
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

  get filterProject(): string {
    return this.searchProject;
  }
  set filterProject(val: string) {
    this.searchProject = val;
    this.projects = this.projectFilter.transform(this.projects, val, this.searchProjectState);
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
    this.reportService.create(this.report);
  }
  private fillReport(): void {
    this.report.customersId = this.customersFiltered.map(c => c.id!);
    this.report.projectsId = this.projects.map(c => c.id!);
    this.report.usersId = this.usersFiltered.map(c => c.id!);
    this.report.jobsId = this.projects.map(c => c.id!);
    this.report.companiesId = this.showCompanies.map(c => c.id!);
    console.log(this.startDate, '      ' + this.endDate);
    this.report.setDates(this.startDate, this.endDate);
  }
}
