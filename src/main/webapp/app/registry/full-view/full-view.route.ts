import { Route } from '@angular/router';

import { FullViewComponent } from './full-view.component';

export const FullViewRoute: Route = {
  path: '',
  component: FullViewComponent,
  data: {
    pageTitle: 'full-view.title',
  },
};
