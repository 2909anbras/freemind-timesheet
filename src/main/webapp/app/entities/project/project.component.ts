import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
import { Subscription, combineLatest } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { IProject } from 'app/shared/model/project.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { ProjectService } from './project.service';
import { ProjectDeleteDialogComponent } from './project-delete-dialog.component';

import { IAppUser, AppUser } from 'app/shared/model/app-user.model';
import { AppUserService } from '../app-user/app-user.service';

@Component({
  selector: 'jhi-project',
  templateUrl: './project.component.html',
})
export class ProjectComponent implements OnInit, OnDestroy {
  projects?: IProject[];
  searchProject = '';
  searchProjectState = 'All';
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
    protected projectService: ProjectService,
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
      this.projectService
        .query({
          page: pageToLoad - 1,
          size: this.itemsPerPage,
          sort: this.sort(),
        })
        .subscribe(
          (res: HttpResponse<IProject[]>) => this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate),
          () => this.onError()
        );
    } else {
      if (this.currentAccount) {
        this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
          const appUser = res.body;
          if (appUser) {
            this.currentAccount!.companyId = appUser.companyId;
            console.log(this.currentAccount);
            this.projectService.getProjectByCompanyId(this.currentAccount!.companyId, req).subscribe(
              (res: HttpResponse<IProject[]>) => {
                console.log(res.body);
                this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
              },
              () => this.onError()
            );
          }
        });
      }
    }
  }

  ngOnInit(): void {
    this.handleNavigation();
    this.registerChangeInProjects();
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

  trackId(index: number, item: IProject): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInProjects(): void {
    this.eventSubscriber = this.eventManager.subscribe('projectListModification', () => this.loadPage());
  }

  delete(project: IProject): void {
    const modalRef = this.modalService.open(ProjectDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.project = project;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onSuccess(data: IProject[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/project'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.projects = data || [];
    this.ngbPaginationPage = this.page;
  }

  public canDelete(project: IProject): boolean {
    if (
      project.jobs.some(j => {
        if (j.performances) j.performances.length > 0;
      })
    )
      return false;
    else return true;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
