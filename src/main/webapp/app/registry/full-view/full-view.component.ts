import { Component, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { HttpResponse } from '@angular/common/http';

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
    //   this.setCurrentAccount();
    this.accountService.identity().subscribe(account => (this.currentAccount = account));
    if (this.currentAccount && !this.accountService.hasAnyAuthority('ROLE_ADMIN'))
      this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
        this.currentAccount && res.body ? (this.currentAccount.companyId = res.body.companyId) : null;
      });
    if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
      this.companyService.query().subscribe((res: HttpResponse<ICompany[]>) => {
        if (res.body) {
          this.showCompanies = res.body;
          this.allCompanies = res.body;
          console.log(this.showCompanies);
        }
      });
    } else {
      if (this.currentAccount?.companyId)
        this.companyService.find(this.currentAccount?.companyId).subscribe((res: HttpResponse<ICompany>) => {
          res.body ? this.allCompanies?.push(res.body) : null;
          this.showCompanies = this.allCompanies;
          console.log(this.showCompanies);
        });
    }
  }

  // setCurrentAccount(): void {
  //   this.accountService.identity().subscribe(account => (this.currentAccount = account));
  //   if (this.currentAccount && !this.accountService.hasAnyAuthority('ROLE_ADMIN'))
  //     this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
  //       this.currentAccount && res.body ? (this.currentAccount.companyId = res.body.companyId) : null;
  //     });
  // }

  // fillTheFullTable(): void {
  //   this.setAllCompanies();
  // }

  // setCompanyForTheCurrentAccount(): void {
  //   //hidden the filter in the html (jhHasAnyAuthority)
  //   if (this.currentAccount?.companyId)
  //     this.companyService.find(this.currentAccount?.companyId).subscribe((res: HttpResponse<ICompany>) => {
  //       res.body ? this.allCompanies?.push(res.body) : null;
  //       this.showCompanies = this.allCompanies;
  //     });
  // }

  // setAllCompanies(): void {
  //   //juste fill pour le moment
  //   this.companyService.query().subscribe((res: HttpResponse<ICompany[]>) => {
  //     if (res.body) {
  //       this.showCompanies = res.body;
  //       this.allCompanies = res.body;
  //     }
  //   });
  // }

  public newCompany(): void {}
}
