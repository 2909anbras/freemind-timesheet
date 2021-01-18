import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FreemindTimesheetSharedModule } from 'app/shared/shared.module';
import { FullReportComponent } from './full-report.component';
import { FullReportRoute } from './full-report.route';

@NgModule({
  imports: [FreemindTimesheetSharedModule, RouterModule.forChild([FullReportRoute])],
  declarations: [FullReportComponent],
})
export class FullReportModule {}
