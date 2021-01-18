import { Route } from '@angular/router';

import { FullReportComponent } from './full-report.component';

export const FullReportRoute: Route = {
  path: '',
  component: FullReportComponent,
  data: {
    pageTitle: 'full-report.title',
  },
};
