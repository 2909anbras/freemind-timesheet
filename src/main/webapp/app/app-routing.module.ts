import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';
import { navbarRoute } from './layouts/navbar/navbar.route';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import { Authority } from 'app/shared/constants/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

const LAYOUT_ROUTES = [navbarRoute, ...errorRoute];

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'admin',
          data: {
            authorities: [Authority.CUSTOMER_ADMIN, Authority.ADMIN, Authority.INSPECTOR],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./admin/admin-routing.module').then(m => m.AdminRoutingModule),
        },
        {
          path: 'account',
          loadChildren: () => import('./account/account.module').then(m => m.AccountModule),
        },
        {
          path: 'registry',
          data: {
            authorities: [Authority.ADMIN, Authority.CUSTOMER_ADMIN, Authority.INSPECTOR],
          },
          loadChildren: () => import('./registry/registry-routing.module').then(m => m.RegistryRoutingModule),
        },
        {
          path: 'timesheet',
          data: {
            authorities: [Authority.USER],
          },
          loadChildren: () => import('./timesheet/timesheet.module').then(m => m.FreemindTimesheetTimesheetModule),
        },
        {
          path: 'login',
          data: {
            authorities: [],
          },
          loadChildren: () => import('./shared/login/login-page.module').then(m => m.LoginPageModule),
        },
        ...LAYOUT_ROUTES,
      ],
      { enableTracing: DEBUG_INFO_ENABLED }
    ),
  ],
  exports: [RouterModule],
})
export class FreemindTimesheetAppRoutingModule {}
