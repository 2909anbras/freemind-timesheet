import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FreemindTimesheetSharedModule } from 'app/shared/shared.module';
import { FullReportComponent } from './full-report.component';
import { FullReportRoute } from './full-report.route';

import { JobFilterPipe } from 'app/shared/pipe/filter/jobFilterPipe';
import { ProjectFilterPipe } from 'app/shared/pipe/filter/projectFilterPipe';
import { CustomerFilterPipe } from 'app/shared/pipe/filter/customerFilterPipe';
import { CompanyFilterPipe } from 'app/shared/pipe/filter/companyFilterPipe';
import { KeyvaluePipe } from 'app/shared/pipe/filter/keyvaluePipe';
import { UserFilterPipe } from 'app/shared/pipe/filter/userFilterPipe';

@NgModule({
  imports: [FreemindTimesheetSharedModule, RouterModule.forChild([FullReportRoute])],
  declarations: [FullReportComponent],
  providers: [JobFilterPipe, UserFilterPipe, ProjectFilterPipe, CustomerFilterPipe, CompanyFilterPipe, KeyvaluePipe],
})
export class FullReportModule {}
