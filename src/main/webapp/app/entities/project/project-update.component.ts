import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IProject, Project } from 'app/shared/model/project.model';
import { ProjectService } from './project.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer/customer.service';

import { IAppUser, AppUser } from 'app/shared/model/app-user.model';
import { AppUserService } from '../app-user/app-user.service';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-project-update',
  templateUrl: './project-update.component.html',
})
export class ProjectUpdateComponent implements OnInit {
  isSaving = false;
  customers: ICustomer[] = [];
  currentAccount: Account | null = null;
  isAdmin = false;
  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3)]],
    enable: [null, [Validators.required]],
    customerId: [],
  });

  constructor(
    protected appUserService: AppUserService,
    protected accountService: AccountService,
    protected projectService: ProjectService,
    protected customerService: CustomerService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(e => {
      e ? (this.currentAccount = e) : null;
    });

    this.activatedRoute.data.subscribe(({ customer }) => {
      if (customer !== undefined) {
        this.patchCustomerId(customer.id);
      }
    });
    this.activatedRoute.data.subscribe(({ project }) => {
      project !== undefined ? this.updateForm(project) : null;
      if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
        this.isAdmin = true;
        this.customerService.query().subscribe((res: HttpResponse<ICustomer[]>) => (this.customers = res.body || []));
      } else {
        if (this.currentAccount) {
          this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
            const appUser = res.body;
            if (appUser) {
              this.currentAccount!.companyId = appUser.companyId;
              this.customerService.findAllByCompany(null, this.currentAccount!.companyId).subscribe((res: HttpResponse<ICustomer[]>) => {
                this.customers = res.body || [];
              });
            }
          });
        }
      }
    });
  }

  patchCustomerId(id: number): void {
    this.editForm.patchValue({
      customerId: id,
    });
    this.editForm.get('customerId')?.disable();
  }

  updateForm(project: IProject): void {
    project.id == null ? this.editForm.patchValue({ enable: false }) : this.editForm.patchValue({ enable: project.enable });
    this.editForm.patchValue({
      id: project.id,
      name: project.name,
      // enable: project.enable,
      customerId: project.customerId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const project = this.createFromForm();
    if (project.id == null) project.id = undefined;
    if (project.id !== undefined) {
      this.subscribeToSaveResponse(this.projectService.update(project));
    } else {
      this.subscribeToSaveResponse(this.projectService.create(project));
    }
  }

  private createFromForm(): IProject {
    return {
      ...new Project(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      enable: this.editForm.get(['enable'])!.value,
      customerId: this.editForm.get(['customerId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProject>>): void {
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

  trackById(index: number, item: ICustomer): any {
    return item.id;
  }
}
