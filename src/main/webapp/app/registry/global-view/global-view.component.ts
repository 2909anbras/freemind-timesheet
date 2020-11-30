import { Component, OnInit, OnDestroy } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { HttpResponse, HttpHeaders } from '@angular/common/http';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { Subscription, combineLatest } from 'rxjs';

import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer/customer.service';
import { IJob } from 'app/shared/model/job.model';
import { JobService } from 'app/entities/job/job.service';
import { IProject } from 'app/shared/model/project.model';
import { ProjectService } from 'app/entities/project/project.service';
import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company/company.service';

import { Status } from 'app/shared/model/enumerations/status.model';

import { AppUserService } from 'app/entities/app-user/app-user.service';
import { IAppUser, AppUser } from 'app/shared/model/app-user.model';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  templateUrl: './global-view.component.html',
})
export class GlobalViewComponent implements OnInit {
  showCustomers?: ICustomer[];
  allCustomers?: ICustomer[];
  showCompanies?: ICompany[];
  allCompanies?: ICompany[];
  showJobs?: IJob[];
  allJobs?: IJob[];
  showProjects?: IProject[];
  allProjects?: IProject[];

  // status:Status;

  companyHidden = false;
  customerHidden = false;
  jobHidden = false;
  projectHidden = false;

  searchCompany = '';
  searchCustomer = '';
  searchJob = '';
  searchProject = '';

  searchCustomerState = '';
  searchProjectState = '';
  searchJobState = '';

  enabledStateList = ['Enable', 'Disabled', 'All'];

  currentAccount: Account | null = null;
  //pour plus tard
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor(
    protected status: Status,
    protected projectService: ProjectService,
    protected jobService: JobService,
    protected companyService: CompanyService,
    protected customerService: CustomerService,
    protected appUserService: AppUserService,
    protected accountService: AccountService,
    protected activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  ngOnInit(): void {
    //get currentAccount
    this.setCurrentAccount();
    if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
      //set and show the full company list.
      this.fillTheFullTable();
    } else {
      this.setCompanyForTheCurrentAccount();
    }
  }

  setCurrentAccount(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));
    if (this.currentAccount && !this.accountService.hasAnyAuthority('ROLE_ADMIN'))
      this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
        this.currentAccount && res.body ? (this.currentAccount.companyId = res.body.companyId) : null;
      });
  }

  fillTheFullTable(): void {
    this.setAllCompanies();
  }

  setCompanyForTheCurrentAccount(): void {
    //hidden the filter in the html (jhHasAnyAuthority)
    if (this.currentAccount?.companyId)
      this.companyService.find(this.currentAccount?.companyId).subscribe((res: HttpResponse<ICompany>) => {
        res.body ? this.allCompanies?.push(res.body) : null;
        this.showCompanies = this.allCompanies;
      });
  }

  setAllCompanies(): void {
    //juste fill pour le moment
    this.companyService.query().subscribe((res: HttpResponse<ICompany[]>) => {
      if (res.body) {
        this.showCompanies = res.body;
        this.allCompanies = res.body;
      }
    });
  }

  companyFilter(filterValue: string): void {
    // this.showCompanies=this.allCompanies;
    const filterValueLower = filterValue.toLowerCase();
    if (filterValue === '') {
      this.showCompanies = this.allCompanies;
    } else {
      this.showCompanies = this.allCompanies?.filter(c => c.name?.toLowerCase().includes(filterValueLower));
    }
  }

  customerFilter(filterValue: string): void {
    //
    const filterValueLower = filterValue.toLowerCase();
    if (filterValue === '') {
      this.showCompanies = this.allCompanies;
    } else {
      this.showCompanies = this.allCompanies?.filter(c => c.name?.toLowerCase().includes(filterValueLower));
    }
  }

  newCompany(): any {}
}
