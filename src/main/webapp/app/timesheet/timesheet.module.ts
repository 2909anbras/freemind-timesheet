import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FreemindTimesheetSharedModule } from 'app/shared/shared.module';
import { TimesheetComponent } from './timesheet.component';
import { TimesheetRoute } from './timesheet.route';
import { PerformanceCreateDialogComponent } from 'app/entities/performance/performance-create-dialog.component';

@NgModule({
  imports: [FreemindTimesheetSharedModule, RouterModule.forChild(TimesheetRoute)],
  declarations: [TimesheetComponent],
  // entryComponents: [PerformanceCreateDialogComponent],
})
export class FreemindTimesheetTimesheetModule {}
