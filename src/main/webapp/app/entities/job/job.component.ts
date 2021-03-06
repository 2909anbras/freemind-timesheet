import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
import { Subscription, combineLatest } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IJob } from 'app/shared/model/job.model';
import { IAppUser } from 'app/shared/model/app-user.model'; // , AppUser
import { AppUserService } from '../app-user/app-user.service';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { JobService } from './job.service';
import { JobDeleteDialogComponent } from './job-delete-dialog.component';

@Component({
  selector: 'jhi-job',
  templateUrl: './job.component.html',
})
export class JobComponent implements OnInit, OnDestroy {
  jobs?: IJob[];
  searchJob = '';
  searchJobState = 'All';
  enabledStateList = ['Enable', 'Disabled', 'All'];
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  currentAccount: Account | null = null;

  constructor(
    protected appUserService: AppUserService,
    protected accountService: AccountService,
    protected jobService: JobService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    const pageToLoad: number = page || this.page || 1;
    const req = { page: pageToLoad - 1, size: this.itemsPerPage, sort: this.sort() };
    this.accountService.identity().subscribe(e => {
      e ? (this.currentAccount = e) : null;
      console.log(this.currentAccount);
    });

    if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
      this.jobService.query(req).subscribe(
        (res: HttpResponse<IJob[]>) => this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate),
        () => this.onError()
      );
    } else {
      if (this.currentAccount) {
        this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
          const appUser = res.body;
          if (appUser) {
            this.currentAccount!.companyId = appUser.companyId;
            console.log(this.currentAccount);
            this.jobService.getJobByCompanyId(this.currentAccount!.companyId, req).subscribe((res: HttpResponse<IJob[]>) => {
              const tmp = [...new Set(res.body)];
              const body: IJob[] | undefined = res.body?.filter(function (elem, index, self): boolean {
                return index === self.indexOf(elem);
              });
              console.log(tmp);
              body ? this.onSuccess(body, res.headers, pageToLoad, !dontNavigate) : null, () => this.onError();
            });
          }
        });
      }
    }
  }

  ngOnInit(): void {
    this.handleNavigation();
    this.registerChangeInJobs();
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

  trackId(index: number, item: IJob): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInJobs(): void {
    this.eventSubscriber = this.eventManager.subscribe('jobListModification', () => this.loadPage());
  }

  delete(job: IJob): void {
    if (job.performances?.length === 0) {
      const modalRef = this.modalService.open(JobDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
      modalRef.componentInstance.job = job;
    }
  }

  canDelete(job: IJob): boolean {
    return job.performances?.length === 0;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onSuccess(data: IJob[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/job'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.jobs = data || [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
