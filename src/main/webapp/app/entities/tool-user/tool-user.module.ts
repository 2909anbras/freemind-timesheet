import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FreemindTimesheetSharedModule } from 'app/shared/shared.module';
import { ToolUserComponent } from './tool-user.component';
import { ToolUserDetailComponent } from './tool-user-detail.component';
import { ToolUserUpdateComponent } from './tool-user-update.component';
import { ToolUserDeleteDialogComponent } from './tool-user-delete-dialog.component';
import { toolUserRoute } from './tool-user.route';

@NgModule({
  imports: [FreemindTimesheetSharedModule, RouterModule.forChild(toolUserRoute)],
  declarations: [ToolUserComponent, ToolUserDetailComponent, ToolUserUpdateComponent, ToolUserDeleteDialogComponent],
  entryComponents: [ToolUserDeleteDialogComponent],
})
export class FreemindTimesheetToolUserModule {}
