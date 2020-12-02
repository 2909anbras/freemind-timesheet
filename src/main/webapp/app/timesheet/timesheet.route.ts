import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

import { TimesheetComponent } from 'app/timesheet/timesheet.component';

export const TimesheetRoute: Routes = [
  {
    path: 'timesheet',
    component: TimesheetComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'freemindTimesheetApp.appUser.home.title',
    },
    canActivate: [UserRouteAccessService], //si qq'un de connecté je crois.
  },
];
