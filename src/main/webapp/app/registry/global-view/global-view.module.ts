import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FreemindTimesheetSharedModule } from 'app/shared/shared.module';
import { GlobalViewComponent } from './global-view.component';
import { GlobalViewRoute } from './global-view.route';

@NgModule({
  imports: [FreemindTimesheetSharedModule, RouterModule.forChild([GlobalViewRoute])],
  declarations: [GlobalViewComponent],
})
export class GlobalViewModule {}
