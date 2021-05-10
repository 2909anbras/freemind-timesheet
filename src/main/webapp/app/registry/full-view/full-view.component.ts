import { Component, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
// import { JhiLanguageService } from 'ng-jhipster';
// import { SessionStorageService } from 'ngx-webstorage';
import { HttpResponse } from '@angular/common/http';
// import { Directive, ElementRef, Renderer2, Input } from '@angular/core';

// import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
// import { Subscription, combineLatest } from 'rxjs';
import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company/company.service';
// import { ICustomer } from 'app/shared/model/customer.model';

import { AppUserService } from 'app/entities/app-user/app-user.service';
import { IAppUser } from 'app/shared/model/app-user.model';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';
// import { IProject } from 'app/shared/model/project.model';
@Component({
  selector: 'jhi-full-view',
  templateUrl: './full-view.component.html',
  styleUrls: ['full-view.scss'],
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

  searchCustomerState = 'All';
  searchProjectState = 'All';
  searchJobState = 'All';
  searchCompanyState = 'All';

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
          console.log(this.currentAccount);
          if (this.currentAccount?.companyId)
            this.companyService.find(this.currentAccount?.companyId).subscribe((res: HttpResponse<ICompany>) => {
              res.body ? this.allCompanies?.push(res.body) : null;
              this.showCompanies = this.allCompanies;
              console.log(this.showCompanies);
            });
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

  public switchCompanyHidden(): void {
    this.companyHidden = !this.companyHidden;
  }
  public switchCustomerHidden(): void {
    this.customerHidden = !this.customerHidden;
  }
  public switchProjectHidden(): void {
    this.projectHidden = !this.projectHidden;
  }

  // showHide(id?:number):void {
  //   console.log("coucou "+id);
  //   const el:HTMLElement |null= document.getElementById(`${id}`);
  //   if(el){
  //   if (el.classList.contains('hidden')) {
  //     el.classList.remove('hidden');
  //   } else {
  //     el.classList.add('hidden');
  //   }
  // }
  // }

  // showHide(id: number): void {
  //   const el = <HTMLElement>document.querySelector(`#${id}`);
  //   if (el.classList.contains('hidden')) el.classList.remove('hidden');
  //   else {
  //     el.classList.add('hidden');
  //   }
  // }

  public newCompany(): void {}
}
