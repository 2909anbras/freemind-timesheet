import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { HttpResponse } from '@angular/common/http';

import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company/company.service';

import { IJob } from 'app/shared/model/job.model';
import { JobService } from 'app/entities/job/job.service';

import { IAppUser } from 'app/shared/model/app-user.model';
import { AppUserService } from 'app/entities/app-user/app-user.service';

import { LANGUAGES } from 'app/core/language/language.constants';
import { User } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

type SelectableEntity = ICompany | IJob; //

@Component({
  selector: 'jhi-user-mgmt-update',
  templateUrl: './user-management-update.component.html',
})
export class UserManagementUpdateComponent implements OnInit {
  user!: User;
  languages = LANGUAGES;
  authorities: string[] = [];
  isSaving = false;
  companies: ICompany[] = [];
  jobs: IJob[] = [];
  showJobs = false;
  editForm = this.fb.group({
    id: [],
    login: [
      '',
      [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    ],
    firstName: ['', [Validators.maxLength(50)]],
    lastName: ['', [Validators.maxLength(50)]],
    email: ['', [Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    activated: [],
    langKey: [],
    authorities: [],
    companyId: [],
    jobs: [],
    phone: [],
  });

  constructor(
    private appUserService: AppUserService,
    private userService: UserService,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    protected jobService: JobService,
    protected companyService: CompanyService
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(({ user }) => {
      this.userService.authorities().subscribe(authorities => {
        this.authorities = authorities;
      });
      this.showJobs = !user.authorities.some((x: string) => x === 'ROLE_ADMIN');
      console.log(this.showJobs);
      console.log(user);
      this.jobService.query().subscribe((res: HttpResponse<IJob[]>) => (this.jobs = res.body || []));
      this.companyService.query().subscribe((res: HttpResponse<ICompany[]>) => {
        this.companies = res.body || [];
        console.log(this.companies);
      });
      if (user) {
        this.user = user;
        if (this.user.id === undefined) {
          this.user.activated = true;
        }
        if (this.showJobs) {
          this.appUserService.find(user.id).subscribe((res: HttpResponse<IAppUser>) => {
            const appUser = res.body;
            if (appUser) {
              this.user.companyId = appUser.companyId;
              this.user.phone = appUser.phone;
              this.user.jobs = appUser.jobs;
            }
            this.updateForm(user);
          });
        }
        // this.updateForm(user);
      }
    });
  }

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    this.updateUser(this.user);
    if (this.user.id !== undefined) {
      this.userService.update(this.user).subscribe(
        () => this.onSaveSuccess(),
        () => this.onSaveError()
      );
    } else {
      this.userService.create(this.user).subscribe(
        //this.user.jobs
        () => this.onSaveSuccess(),
        () => this.onSaveError()
      );
    }
  }

  private updateForm(user: User): void {
    //ici biding
    console.log(user);
    this.editForm.patchValue({
      id: user.id,
      login: user.login,
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      activated: user.activated,
      langKey: user.langKey,
      authorities: user.authorities,
      phone: user.phone,
      jobs: user.jobs,
      companyId: user.companyId,
    });
  }

  private updateUser(user: User): void {
    user.login = this.editForm.get(['login'])!.value;
    user.firstName = this.editForm.get(['firstName'])!.value;
    user.lastName = this.editForm.get(['lastName'])!.value;
    user.email = this.editForm.get(['email'])!.value;
    user.activated = this.editForm.get(['activated'])!.value;
    user.langKey = this.editForm.get(['langKey'])!.value;
    user.authorities = this.editForm.get(['authorities'])!.value;
    user.phone = this.editForm.get(['phone'])!.value;
    user.companyId = this.editForm.get(['companyId'])!.value;
    user.jobs = this.editForm.get(['jobs'])!.value;
  }

  private onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  private onSaveError(): void {
    this.isSaving = false;
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
