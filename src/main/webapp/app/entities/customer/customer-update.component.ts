import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';

import { AppUserService } from '../app-user/app-user.service';
import { IAppUser, AppUser } from 'app/shared/model/app-user.model';

import { ICustomer, Customer } from 'app/shared/model/customer.model';
import { CustomerService } from './customer.service';
import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company/company.service';

@Component({
  selector: 'jhi-customer-update',
  templateUrl: './customer-update.component.html',
})
export class CustomerUpdateComponent implements OnInit {
  isSaving = false;
  companies: ICompany[] = [];
  isAdmin = false;

  account?: Account;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3)]],
    enable: [null, [Validators.required]],
    companyId: [],
  });

  constructor(
    protected appUserService: AppUserService,
    protected accountService: AccountService,
    protected customerService: CustomerService,
    protected companyService: CompanyService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customer }) => {
      this.activatedRoute.data.subscribe(({ company }) => {
        if (company !== undefined) {
          this.patchCompanyId(company.id);
        }
      });

      if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
        this.isAdmin = true;
        this.companyService.query().subscribe((res: HttpResponse<ICompany[]>) => (this.companies = res.body || []));
        customer !== undefined ? this.updateForm(customer) : null;
      } else {
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
            customer !== undefined ? this.updateForm(customer) : null;
          }
        });
      }
    });
  }

  patchCompanyId(id: number): void {
    this.editForm.patchValue({
      companyId: id,
    });
    this.editForm.get('companyId')?.disable();
  }
  updateForm(customer: ICustomer): void {
    let id = 0;
    if (this.account?.companyId) id = this.account?.companyId;
    else id = customer.companyId!;
    this.editForm.patchValue({
      id: customer.id,
      name: customer.name,
      enable: customer.enable,
      companyId: id,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customer = this.createFromForm();
    if (customer.id !== undefined) {
      this.subscribeToSaveResponse(this.customerService.update(customer));
    } else {
      this.subscribeToSaveResponse(this.customerService.create(customer));
    }
  }

  private createFromForm(): ICustomer {
    return {
      ...new Customer(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      enable: this.editForm.get(['enable'])!.value,
      companyId: this.editForm.get(['companyId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomer>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: ICompany): any {
    return item.id;
  }

  // getSelected(selectedVals: ICompany[], option: ICompany): ICompany {
  //   if (selectedVals) {
  //     for (let i = 0; i < selectedVals.length; i++) {
  //       if (option.id === selectedVals[i].id) {
  //         return selectedVals[i];
  //       }
  //     }
  //   }
  //   return option;
  // }
}
