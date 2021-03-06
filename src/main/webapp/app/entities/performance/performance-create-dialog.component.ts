import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { FormBuilder, Validators, FormGroup } from '@angular/forms'; // , FormControl
// import { numberRangeValidator } from 'app/shared/form-validations/numberRangeValidator.directive';

import { ICustomer } from 'app/shared/model/customer.model';
import { IProject } from 'app/shared/model/project.model';
import { IJob } from 'app/shared/model/job.model';
import { IPerformance } from 'app/shared/model/performance.model';
// import { addConsoleHandler } from 'selenium-webdriver/lib/logging';

import { PerformanceService } from 'app/entities/performance/performance.service';
import { IUser } from 'app/core/user/user.model';

@Component({
  templateUrl: './performance-create-dialog.component.html',
})
export class PerformanceCreateDialogComponent implements OnInit {
  customer?: ICustomer;
  project?: IProject;
  job?: IJob;
  currentEmployee?: IUser;
  performance?: IPerformance;
  date?: Date;
  months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
  days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  isNew = true;
  dateToString = '';
  customerToString = '';
  projectToString = '';
  jobToString = '';
  min = 0;
  max = 10;
  createForm: FormGroup = this.fb.group({
    hours: ['', [Validators.required, Validators.min(0), Validators.max(16)]], // ,[numberRangeValidator(this.min, this.max)]
    description: ['', []],
  });
  constructor(
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager,
    private performanceService: PerformanceService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.date
      ? (this.dateToString = 'Date: ' + '\n' + this.date.getDate() + '/' + (this.date.getMonth() + 1) + '/' + this.date.getFullYear())
      : null;
    this.customerToString = 'Customer: ' + '\n' + this.customer?.name;
    this.projectToString = 'Project: ' + '\n' + this.project?.name;
    this.jobToString = 'Job: ' + '\n' + this.job?.name;
    if (this.performance !== null) {
      this.updateForm();
      this.isNew = false;
    }
  }
  // get f() {
  //   return this.createForm.controls;
  // }

  private updateForm(): void {
    this.createForm.patchValue({
      hours: this.performance!.hours,
      description: this.performance!.description,
    });
  }

  cancel(): void {
    this.createForm?.patchValue({
      hours: '',
    });
    this.activeModal.dismiss('cancel');
  }

  update(): void {
    this.performanceService
      .update({
        id: this.performance?.id,
        hours: this.createForm?.get('hours')!.value,
        description: this.createForm?.get('description')!.value,
        date: this.date!,
        jobId: this.job?.id,
        appUserId: this.currentEmployee?.id,
      })
      .subscribe(() => {
        this.eventManager.broadcast('timesheetModification');
        this.activeModal.close();
      });
  }

  create(): void {
    this.performanceService
      .create({
        hours: this.createForm?.get('hours')!.value,
        description: this.createForm?.get('description')!.value,
        date: this.date!,
        jobId: this.job?.id,
        appUserId: this.currentEmployee?.id,
      })
      .subscribe(() => {
        this.eventManager.broadcast('timesheetModification');
        this.activeModal.close();
      });
  }

  save(): void {
    this.isNew ? this.create() : this.update();
  }
}
