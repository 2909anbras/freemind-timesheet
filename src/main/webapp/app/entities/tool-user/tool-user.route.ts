import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IToolUser, ToolUser } from 'app/shared/model/tool-user.model';
import { ToolUserService } from './tool-user.service';
import { ToolUserComponent } from './tool-user.component';
import { ToolUserDetailComponent } from './tool-user-detail.component';
import { ToolUserUpdateComponent } from './tool-user-update.component';

@Injectable({ providedIn: 'root' })
export class ToolUserResolve implements Resolve<IToolUser> {
  constructor(private service: ToolUserService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IToolUser> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((toolUser: HttpResponse<ToolUser>) => {
          if (toolUser.body) {
            return of(toolUser.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ToolUser());
  }
}

export const toolUserRoute: Routes = [
  {
    path: '',
    component: ToolUserComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'freemindTimesheetApp.toolUser.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ToolUserDetailComponent,
    resolve: {
      toolUser: ToolUserResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'freemindTimesheetApp.toolUser.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ToolUserUpdateComponent,
    resolve: {
      toolUser: ToolUserResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'freemindTimesheetApp.toolUser.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ToolUserUpdateComponent,
    resolve: {
      toolUser: ToolUserResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'freemindTimesheetApp.toolUser.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
