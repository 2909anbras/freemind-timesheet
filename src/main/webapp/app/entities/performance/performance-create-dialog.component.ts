import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICustomer } from 'app/shared/model/customer.model';
import { IProject } from 'app/shared/model/project.model';
import { IJob } from 'app/shared/model/job.model';
import { IPerformance } from 'app/shared/model/performance.model';
import { addConsoleHandler } from 'selenium-webdriver/lib/logging';

@Component({
  templateUrl: './a.component.html',
})
export class PerformanceCreateDialogComponent implements OnInit {
  customer?: ICustomer;
  project?: IProject;
  job?: IJob;
  performance?: IPerformance;
  date?: Date;
  months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
  days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  dateToString = '';
  customerToString = '';
  projectToString = '';
  jobToString = '';

  constructor(public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  ngOnInit(): void {
    console.log('@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@é');
    console.log(this.date);
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

  confirmCreation(): void {
    //faire nouvelle perf et envoyer dans service.
    // this.customerService.delete(id).subscribe(() => {
    //   this.eventManager.broadcast('customerListModification');
    //   this.activeModal.close();
    // });
  }
}
