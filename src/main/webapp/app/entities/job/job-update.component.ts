import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IJob, Job } from 'app/shared/model/job.model';
import { JobService } from './job.service';
import { IProject } from 'app/shared/model/project.model';
import { ProjectService } from 'app/entities/project/project.service';
import { IAppUser, AppUser } from 'app/shared/model/app-user.model';
import { AppUserService } from '../app-user/app-user.service';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core/auth/account.service';

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

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3)]],
    description: [null, [Validators.minLength(20)]],
    status: [],
    startDate: [],
    endDate: [],
    enable: [],
    projectId: [],
  });

  constructor(
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
      console.log(this.currentAccount);
    });
    this.activatedRoute.data.subscribe(({ job }) => {
      this.updateForm(job);
      if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
        this.projectService.query().subscribe((res: HttpResponse<IProject[]>) => (this.projects = res.body || []));
      } else {
        if (this.currentAccount) {
          this.appUserService.find(this.currentAccount.id).subscribe((res: HttpResponse<IAppUser>) => {
            const appUser = res.body;
            if (appUser) {
              this.currentAccount!.companyId = appUser.companyId;
              console.log(this.currentAccount);
              this.projectService.getProjectByCompanyId(this.currentAccount!.companyId, null).subscribe((res: HttpResponse<IProject[]>) => {
                console.log(res.body);
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

  trackById(index: number, item: IProject): any {
    return item.id;
  }
}
