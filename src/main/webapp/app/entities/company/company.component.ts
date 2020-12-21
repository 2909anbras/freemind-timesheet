import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
import { Subscription, combineLatest } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICompany } from 'app/shared/model/company.model';

import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';

import { AppUserService } from '../app-user/app-user.service';
import { IAppUser, AppUser } from 'app/shared/model/app-user.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { CompanyService } from './company.service';
import { CompanyDeleteDialogComponent } from './company-delete-dialog.component';

@Component({
  selector: 'jhi-company',
  templateUrl: './company.component.html',
})
export class CompanyComponent implements OnInit, OnDestroy {
  companies?: ICompany[];
  searchCompany = '';
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  account?: Account;

  constructor(
    protected appUserService: AppUserService,
    protected accountService: AccountService,
    protected companyService: CompanyService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    const pageToLoad: number = page || this.page || 1;
    if (this.accountService.hasAnyAuthority('ROLE_ADMIN'))
      this.companyService
        .query({
          page: pageToLoad - 1,
          size: this.itemsPerPage,
          sort: this.sort(),
        })
        .subscribe(
          (res: HttpResponse<ICompany[]>) => this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate),
          () => this.onError()
        );
    else {
      this.accountService.identity().subscribe(account => {
        if (account) {
          this.account = account;
          this.appUserService.find(account.id).subscribe((res: HttpResponse<IAppUser>) => {
            const appUser = res.body;
            if (appUser) {
              account.companyId = appUser.companyId;
              console.log(account);
              this.companyService.find(account.companyId).subscribe((res: HttpResponse<ICompany>) => {
                res.body ? (this.companies = [res.body]) : null;
              });
            }
          });
        }
      });
    }
  }

  ngOnInit(): void {
    this.handleNavigation();
    this.registerChangeInCompanies();
  }

  protected handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      const pageNumber = page !== null ? +page : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === 'asc';
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    }).subscribe();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ICompany): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInCompanies(): void {
    this.eventSubscriber = this.eventManager.subscribe('companyListModification', () => this.loadPage());
  }

  delete(company: ICompany): void {
    const modalRef = this.modalService.open(CompanyDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.company = company;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onSuccess(data: ICompany[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/company'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.companies = data || [];
    console.log(this.companies);
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
