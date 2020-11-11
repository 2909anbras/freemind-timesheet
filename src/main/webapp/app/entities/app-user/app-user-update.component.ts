import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IAppUser, AppUser } from 'app/shared/model/app-user.model';
import { AppUserService } from './app-user.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IJob } from 'app/shared/model/job.model';
import { JobService } from 'app/entities/job/job.service';
import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company/company.service';

type SelectableEntity = IUser | IJob | ICompany;

@Component({
  selector: 'jhi-app-user-update',
  templateUrl: './app-user-update.component.html',
})
export class AppUserUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  jobs: IJob[] = [];
  companies: ICompany[] = [];

  editForm = this.fb.group({
    id: [],
    phone: [],
    internalUserId: [],
    jobs: [],
    companyId: [],
  });

  constructor(
    protected appUserService: AppUserService,
    protected userService: UserService,
    protected jobService: JobService,
    protected companyService: CompanyService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appUser }) => {
      this.updateForm(appUser);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));

      this.jobService.query().subscribe((res: HttpResponse<IJob[]>) => (this.jobs = res.body || []));

      this.companyService.query().subscribe((res: HttpResponse<ICompany[]>) => (this.companies = res.body || []));
    });
  }

  updateForm(appUser: IAppUser): void {
    this.editForm.patchValue({
      id: appUser.id,
      phone: appUser.phone,
      internalUserId: appUser.internalUserId,
      jobs: appUser.jobs,
      companyId: appUser.companyId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appUser = this.createFromForm();
    if (appUser.id !== undefined) {
      this.subscribeToSaveResponse(this.appUserService.update(appUser));
    } else {
      this.subscribeToSaveResponse(this.appUserService.create(appUser));
    }
  }

  private createFromForm(): IAppUser {
    return {
      ...new AppUser(),
      id: this.editForm.get(['id'])!.value,
      phone: this.editForm.get(['phone'])!.value,
      internalUserId: this.editForm.get(['internalUserId'])!.value,
      jobs: this.editForm.get(['jobs'])!.value,
      companyId: this.editForm.get(['companyId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppUser>>): void {
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

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }

  getSelected(selectedVals: IJob[], option: IJob): IJob {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
