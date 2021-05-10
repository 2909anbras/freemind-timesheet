import { Routes } from '@angular/router';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

import { TimesheetComponent } from 'app/timesheet/timesheet.component';

export const TimesheetRoute: Routes = [
  {
    path: '',
    component: TimesheetComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'timesheet.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
