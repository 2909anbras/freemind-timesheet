import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FreemindTimesheetSharedModule } from 'app/shared/shared.module';
import { AppUserComponent } from './app-user.component';
import { AppUserDetailComponent } from './app-user-detail.component';
import { AppUserUpdateComponent } from './app-user-update.component';
import { AppUserDeleteDialogComponent } from './app-user-delete-dialog.component';
import { appUserRoute } from './app-user.route';

@NgModule({
  imports: [FreemindTimesheetSharedModule, RouterModule.forChild(appUserRoute)],
  declarations: [AppUserComponent, AppUserDetailComponent, AppUserUpdateComponent, AppUserDeleteDialogComponent],
  entryComponents: [AppUserDeleteDialogComponent],
})
export class FreemindTimesheetAppUserModule {}
