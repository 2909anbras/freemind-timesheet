import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IToolUser, ToolUser } from 'app/shared/model/tool-user.model';
import { ToolUserService } from './tool-user.service';
import { IJob } from 'app/shared/model/job.model';
import { JobService } from 'app/entities/job/job.service';
import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company/company.service';

type SelectableEntity = IJob | ICompany;

@Component({
  selector: 'jhi-tool-user-update',
  templateUrl: './tool-user-update.component.html',
})
export class ToolUserUpdateComponent implements OnInit {
  isSaving = false;
  jobs: IJob[] = [];
  companies: ICompany[] = [];

  editForm = this.fb.group({
    id: [],
    firstName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(10)]],
    lastName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(10)]],
    email: [
      null,
      [
        Validators.required,
        Validators.pattern('^[\\\\w!#$%&amp;’*+/=?`{|}~^-]+(?:\\\\.[\\\\w!#$%&amp;’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,6}$'),
      ],
    ],
    password: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
    enable: [null, [Validators.required]],
    language: [],
    jobs: [],
    company: [],
  });

  constructor(
    protected toolUserService: ToolUserService,
    protected jobService: JobService,
    protected companyService: CompanyService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ toolUser }) => {
      this.updateForm(toolUser);

      this.jobService.query().subscribe((res: HttpResponse<IJob[]>) => (this.jobs = res.body || []));

      this.companyService.query().subscribe((res: HttpResponse<ICompany[]>) => (this.companies = res.body || []));
    });
  }

  updateForm(toolUser: IToolUser): void {
    this.editForm.patchValue({
      id: toolUser.id,
      firstName: toolUser.firstName,
      lastName: toolUser.lastName,
      email: toolUser.email,
      password: toolUser.password,
      enable: toolUser.enable,
      language: toolUser.language,
      jobs: toolUser.jobs,
      company: toolUser.company,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const toolUser = this.createFromForm();
    if (toolUser.id !== undefined) {
      this.subscribeToSaveResponse(this.toolUserService.update(toolUser));
    } else {
      this.subscribeToSaveResponse(this.toolUserService.create(toolUser));
    }
  }

  private createFromForm(): IToolUser {
    return {
      ...new ToolUser(),
      id: this.editForm.get(['id'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      email: this.editForm.get(['email'])!.value,
      password: this.editForm.get(['password'])!.value,
      enable: this.editForm.get(['enable'])!.value,
      language: this.editForm.get(['language'])!.value,
      jobs: this.editForm.get(['jobs'])!.value,
      company: this.editForm.get(['company'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IToolUser>>): void {
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
