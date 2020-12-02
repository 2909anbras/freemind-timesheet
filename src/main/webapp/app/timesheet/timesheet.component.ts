import { Component, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { HttpResponse } from '@angular/common/http';

import { CompanyService } from 'app/entities/company/company.service';

import { AppUserService } from 'app/entities/app-user/app-user.service';
import { IAppUser } from 'app/shared/model/app-user.model';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';
@Component({
  selector: 'jhi-timesheet',
  templateUrl: './timesheet.component.html',
})
export class TimesheetComponent implements OnInit {
  currentAccount: Account | null = null;

  constructor(
    protected companyService: CompanyService,
    protected appUserService: AppUserService,
    protected accountService: AccountService,
    protected eventManager: JhiEventManager
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));
    if (this.currentAccount && !this.accountService.hasAnyAuthority('ROLE_ADMIN'))
      this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
        this.currentAccount && res.body ? (this.currentAccount.companyId = res.body.companyId) : null;
      });
    if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
    }
  }
}
