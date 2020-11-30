import { Route } from '@angular/router';

import { GlobalViewComponent } from './global-view.component';

export const GlobalViewRoute: Route = {
  path: '',
  component: GlobalViewComponent,
  data: {
    pageTitle: 'drop.title',
  },
};
