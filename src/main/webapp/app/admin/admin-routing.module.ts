import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Authority } from 'app/shared/constants/authority.constants';

/* jhipster-needle-add-admin-module-import - JHipster will add admin modules imports here */

@NgModule({
  imports: [
    /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
    RouterModule.forChild([
      {
        path: 'user-management',
        loadChildren: () => import('./user-management/user-management.module').then(m => m.UserManagementModule),
        data: {
          authorities: [Authority.CUSTOMER_ADMIN, Authority.ADMIN, Authority.INSPECTOR],
          pageTitle: 'userManagement.home.title',
        },
      },
      {
        path: 'audits',
        loadChildren: () => import('./audits/audits.module').then(m => m.AuditsModule),
        data: {
          authorities: [Authority.ADMIN],
        },
      },
      {
        path: 'configuration',
        loadChildren: () => import('./configuration/configuration.module').then(m => m.ConfigurationModule),
        data: {
          authorities: [Authority.ADMIN],
        },
      },
      {
        path: 'docs',
        loadChildren: () => import('./docs/docs.module').then(m => m.DocsModule),
        data: {
          authorities: [Authority.ADMIN],
        },
      },
      {
        path: 'health',
        loadChildren: () => import('./health/health.module').then(m => m.HealthModule),
        data: {
          authorities: [Authority.ADMIN],
        },
      },
      {
        path: 'logs',
        loadChildren: () => import('./logs/logs.module').then(m => m.LogsModule),
        data: {
          authorities: [Authority.ADMIN],
        },
      },

      {
        path: 'metrics',
        loadChildren: () => import('./metrics/metrics.module').then(m => m.MetricsModule),
        data: {
          authorities: [Authority.ADMIN],
        },
      },
      /* jhipster-needle-add-admin-route - JHipster will add admin routes here */
    ]),
  ],
})
export class AdminRoutingModule {}
