import { NgModule } from '@angular/core';
import { FreemindTimesheetSharedModule } from 'app/shared/shared.module';
import { PerformanceCreateDialogComponent } from 'app/entities/performance/performance-create-dialog.component';

@NgModule({
  imports: [FreemindTimesheetSharedModule],
  entryComponents: [PerformanceCreateDialogComponent],
})
export class FreemindTimesheetPerformanceModule {}
