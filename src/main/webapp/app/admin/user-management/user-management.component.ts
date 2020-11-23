import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse, HttpHeaders } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription, combineLatest } from 'rxjs';
import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';

import { IAppUser } from 'app/shared/model/app-user.model';
import { AppUserService } from 'app/entities/app-user/app-user.service';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { UserService } from 'app/core/user/user.service';
import { User, IUser } from 'app/core/user/user.model';
import { UserManagementDeleteDialogComponent } from './user-management-delete-dialog.component';

//donc, ajouter iappuser

@Component({
  selector: 'jhi-user-mgmt',
  templateUrl: './user-management.component.html',
})
export class UserManagementComponent implements OnInit, OnDestroy {
  currentAccount: Account | null = null;
  users: User[] | null = null;
  userListSubscription?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;

  constructor(
    private appUserService: AppUserService,
    private userService: UserService,
    private accountService: AccountService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private eventManager: JhiEventManager,
    private modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));

    if (this.accountService.hasAnyAuthority('ROLE_ADMIN'))
      this.userListSubscription = this.eventManager.subscribe('userListModification', () => this.loadAll());
    else {
      this.loadAllCompany();
      const reqBis = { page: this.page - 1, size: this.itemsPerPage, sort: this.sortAppUser() };
      if (this.currentAccount) {
        this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
          const appUser = res.body;
          if (this.currentAccount) {
            if (appUser) {
              this.currentAccount.companyId = appUser.companyId;
            }
          }
          this.appUserService.findByCompany(this.currentAccount?.companyId, reqBis).subscribe((res: HttpResponse<IAppUser[]>) => {
            const appUsers = res.body;
            console.log(appUsers);
            if (appUsers) {
              console.log(appUsers);
              const tmp: number[] = [];
              appUsers.forEach(element => {
                element.companyId ? tmp.push(element.companyId) : null;
              });
              const req = {
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.sort(),
                ids: tmp,
              };
              this.userService.findByIds(tmp, req).subscribe((res: HttpResponse<IUser[]>) => {
                const users = res.body;
                let usersShow;
                if (users && appUsers) {
                  for (let i = 0; i < appUsers.length; i++) {
                    users[i].companyId = appUsers[i].companyId;
                    users[i].phone = appUsers[i].phone;
                  }
                  usersShow = users.filter(e => appUsers.some(y => y.id === e.id));
                  console.log(usersShow);
                  this.onSuccess(usersShow, res.headers);
                }
              });
            }
          });
        });
      }
    }
    this.handleNavigation();
  }

  ngOnDestroy(): void {
    if (this.userListSubscription) {
      this.eventManager.destroy(this.userListSubscription);
    }
  }

  setActive(user: User, isActivated: boolean): void {
    this.appUserService.find(user.id).subscribe((res: HttpResponse<IAppUser>) => {
      if (res) {
        const appUser = res.body;
        this.userService
          .update({ ...user, activated: isActivated, companyId: appUser?.companyId, phone: appUser?.phone })
          .subscribe(() => this.loadAll());
      }
    });
    // this.userService.update({ ...user, activated: isActivated }).subscribe(() => this.loadAll());
  }

  trackIdentity(index: number, item: User): any {
    return item.id;
  }

  deleteUser(user: User): void {
    const modalRef = this.modalService.open(UserManagementDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.user = user;
  }

  transition(): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        page: this.page,
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
      },
    });
  }

  private handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      this.page = page !== null ? +page : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      this.predicate = sort[0];
      this.ascending = sort[1] === 'asc';
      this.accountService.hasAnyAuthority('ROLE_ADMIN') ? this.loadAll() : this.loadAllCompany();
      // this.loadAll();
    }).subscribe();
  }

  private loadAllCompany(): void {
    const reqBis = { page: this.page - 1, size: this.itemsPerPage, sort: this.sortAppUser() };
    if (this.currentAccount) {
      this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
        const appUser = res.body;
        if (this.currentAccount) {
          if (appUser) {
            this.currentAccount.companyId = appUser.companyId;
          }
        }
        this.appUserService.findByCompany(this.currentAccount?.companyId, reqBis).subscribe((res: HttpResponse<IAppUser[]>) => {
          const appUsers = res.body;
          console.log(appUsers);
          if (appUsers) {
            console.log(appUsers);
            const tmp: number[] = [];
            appUsers.forEach(element => {
              element.companyId ? tmp.push(element.companyId) : null;
            });
            const req = {
              page: this.page - 1,
              size: this.itemsPerPage,
              sort: this.sort(),
              ids: tmp,
            };
            this.userService.findByIds(tmp, req).subscribe((res: HttpResponse<IUser[]>) => {
              const users = res.body;
              let usersShow;
              if (users && appUsers) {
                for (let i = 0; i < appUsers.length; i++) {
                  users[i].companyId = appUsers[i].companyId;
                  users[i].phone = appUsers[i].phone;
                }
                usersShow = users.filter(e => appUsers.some(y => y.id === e.id));
                console.log(usersShow);
                this.onSuccess(usersShow, res.headers);
              }
            });
          }
        });
      });
    }
  }

  private loadAll(): void {
    this.userService
      .query({
        page: this.page - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe((res: HttpResponse<User[]>) => this.onSuccess(res.body, res.headers));
  }

  private sortAppUser(): string[] {
    const result = ['id' + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  private sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  private onSuccess(users: User[] | null, headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.users = users;
  }
}
