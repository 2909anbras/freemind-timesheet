import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FreemindTimesheetSharedModule } from 'app/shared/shared.module';
import { FullViewComponent } from './full-view.component';
import { FullViewRoute } from './full-view.route';

@NgModule({
  imports: [FreemindTimesheetSharedModule, RouterModule.forChild([FullViewRoute])],
  declarations: [FullViewComponent],
})
export class FullViewModule {}
