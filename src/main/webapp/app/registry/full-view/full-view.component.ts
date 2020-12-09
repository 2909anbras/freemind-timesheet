import { Component, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { JhiLanguageService } from 'ng-jhipster';
import { SessionStorageService } from 'ngx-webstorage';
import { HttpResponse } from '@angular/common/http';
import { Directive, ElementRef, Renderer2, Input } from '@angular/core';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { Subscription, combineLatest } from 'rxjs';
import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company/company.service';

import { AppUserService } from 'app/entities/app-user/app-user.service';
import { IAppUser } from 'app/shared/model/app-user.model';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';
@Component({
  selector: 'jhi-full-view',
  templateUrl: './view.component.html',
})
export class FullViewComponent implements OnInit {
  showCompanies: ICompany[] = [];
  allCompanies: ICompany[] = [];

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
  searchCompanyState = '';

  enabledStateList = ['Enable', 'Disabled', 'All'];

  currentAccount: Account | null = null;
  // pour plus tard
  // eventSubscriber?: Subscription;
  // totalItems = 0;
  // itemsPerPage = ITEMS_PER_PAGE;
  // page!: number;
  // predicate!: string;
  // ascending!: boolean;
  // ngbPaginationPage = 1;

  constructor(
    //   public status: Status,
    protected companyService: CompanyService,
    protected appUserService: AppUserService,
    protected accountService: AccountService,
    protected eventManager: JhiEventManager
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));
    if (this.currentAccount && !this.accountService.hasAnyAuthority('ROLE_ADMIN'))
      this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
        console.log('Complete account');
        this.currentAccount && res.body ? (this.currentAccount.companyId = res.body.companyId) : null;
        if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
          this.companyService.query().subscribe((res: HttpResponse<ICompany[]>) => {
            if (res.body) {
              console.log('Complete admin companies');
              this.showCompanies = res.body;
              this.allCompanies = res.body;
              this.sortingCompanies();
              console.log(this.showCompanies);
            }
          });
        } else {
          console.log(this.currentAccount);
          if (this.currentAccount?.companyId)
            this.companyService.find(this.currentAccount?.companyId).subscribe((res: HttpResponse<ICompany>) => {
              console.log('Complete company');
              res.body ? this.allCompanies?.push(res.body) : null;
              this.showCompanies = this.allCompanies;
              console.log(this.showCompanies);
            });
        }
      });
  }
  private sortingCompanies(): void {
    console.log('in');

    this.showCompanies.sort((a, b) => {
      if (a.name && b.name) {
        if (a.name.localeCompare(b.name) === 1) {
          return -1;
        } else if (a.name.localeCompare(b.name) === -1) {
          return 1;
        } else return -1;
      } else return -1;
    });

    this.showCompanies.sort((a, b) => {
      if (a.customers.length === 0) return 1;
      else return -1;
    });
    console.log(this.showCompanies);
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
  public newCompany(): void {}
}
