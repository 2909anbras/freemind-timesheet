import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FreemindTimesheetSharedModule } from 'app/shared/shared.module';
import { TimesheetComponent } from './timesheet.component';
import { TimesheetRoute } from './timesheet.route';

@NgModule({
  imports: [FreemindTimesheetSharedModule, RouterModule.forChild(TimesheetRoute)],
  declarations: [TimesheetComponent],
})
export class FreemindTimesheetTimesheetModule {}
