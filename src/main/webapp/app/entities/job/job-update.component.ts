import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IUser } from 'app/core/user/user.model';
import { IJob, Job } from 'app/shared/model/job.model';
import { JobService } from './job.service';
import { IProject } from 'app/shared/model/project.model';
import { ProjectService } from 'app/entities/project/project.service';
import { IAppUser, AppUser } from 'app/shared/model/app-user.model';
import { AppUserService } from '../app-user/app-user.service';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { UserService } from 'app/core/user/user.service';

type SelectableEntity = IUser | IProject;
@Component({
  selector: 'jhi-job-update',
  templateUrl: './job-update.component.html',
})
export class JobUpdateComponent implements OnInit {
  isSaving = false;
  projects: IProject[] = [];
  startDateDp: any;
  endDateDp: any;
  currentAccount: Account | null = null;
  users: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3)]],
    description: [null, [Validators.minLength(20)]],
    status: [],
    startDate: [],
    endDate: [],
    enable: [],
    projectId: [],
    users: [],
  });

  constructor(
    protected userService: UserService,
    protected appUserService: AppUserService,
    protected accountService: AccountService,
    protected jobService: JobService,
    protected projectService: ProjectService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(e => {
      e ? (this.currentAccount = e) : null;
    });
    this.activatedRoute.data.subscribe(({ job }) => {
      this.updateForm(job);
      if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
        //by job Company!
        this.projectService.query().subscribe((res: HttpResponse<IProject[]>) => (this.projects = res.body || []));
      } else {
        if (this.currentAccount) {
          this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
            const appUser = res.body;
            if (appUser) {
              this.currentAccount!.companyId = appUser.companyId;
              this.userService.findAllByCompany(this.currentAccount!.companyId).subscribe((users: HttpResponse<IUser[]>) => {
                if (users.body) this.users = users.body;
              });
              this.projectService.getProjectByCompanyId(this.currentAccount!.companyId, null).subscribe((res: HttpResponse<IProject[]>) => {
                this.projects = res.body || [];
              });
            }
          });
        }
      }
    });
  }

  updateForm(job: IJob): void {
    this.editForm.patchValue({
      id: job.id,
      name: job.name,
      description: job.description,
      status: job.status,
      startDate: job.startDate,
      endDate: job.endDate,
      enable: job.enable,
      projectId: job.projectId,
      users: job.appUsers,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const job = this.createFromForm();
    if (job.id !== undefined) {
      this.subscribeToSaveResponse(this.jobService.update(job));
    } else {
      this.subscribeToSaveResponse(this.jobService.create(job));
    }
  }

  private createFromForm(): IJob {
    return {
      ...new Job(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      status: this.editForm.get(['status'])!.value,
      startDate: this.editForm.get(['startDate'])!.value,
      endDate: this.editForm.get(['endDate'])!.value,
      enable: this.editForm.get(['enable'])!.value,
      projectId: this.editForm.get(['projectId'])!.value,
      // appUsers:this.editForm.get(['users'])!.value,//change users by appUsers
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJob>>): void {
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
}
