import { Route } from '@angular/router';
import { Authority } from 'app/shared/constants/authority.constants';

import { HomeComponent } from './home.component';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
export const HOME_ROUTE: Route = {
  path: '',
  component: HomeComponent,
  data: {
    authorities: [Authority.USER],
    pageTitle: 'home.title',
  },
  canActivate: [UserRouteAccessService],
};
