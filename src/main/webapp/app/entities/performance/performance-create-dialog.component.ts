import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { FormBuilder, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';

import { ICustomer } from 'app/shared/model/customer.model';
import { IProject } from 'app/shared/model/project.model';
import { IJob } from 'app/shared/model/job.model';
import { IPerformance } from 'app/shared/model/performance.model';
import { addConsoleHandler } from 'selenium-webdriver/lib/logging';

import { PerformanceService } from 'app/entities/performance/performance.service';
import { IUser } from 'app/core/user/user.model';

@Component({
  templateUrl: './a.component.html',
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

  dateToString = '';
  customerToString = '';
  projectToString = '';
  jobToString = '';

  createForm = this.fb.group({
    hours: ['', [Validators.required]],
  });

  constructor(
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager,
    private performanceService: PerformanceService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.date
      ? (this.dateToString = 'Date: ' + this.date.getDate() + '/' + (this.date.getMonth() + 1) + '/' + this.date.getFullYear())
      : null;
    this.customerToString = 'Customer: ' + this.customer?.name;
    this.projectToString = 'Project: ' + this.project?.name;
    this.jobToString = 'Job: ' + this.job?.name;
  }
  cancel(): void {
    this.activeModal.dismiss();
  }

  create(): void {
    this.performanceService
      .create({
        hours: this.createForm.get('hours')!.value,
        date: this.date!,
        jobId: this.job?.id,
        appUserId: this.currentEmployee?.id,
      })
      .subscribe();
    //faire nouvelle perf et envoyer dans service.
    // this.customerService.delete(id).subscribe(() => {
    //   this.eventManager.broadcast('customerListModification');
    //   this.activeModal.close();
    // });
  }
}
