import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
/* jhipster-needle-add-admin-module-import - JHipster will add admin modules imports here */

@NgModule({
  imports: [
    /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
    RouterModule.forChild([
      {
        path: 'full-view',
        loadChildren: () => import('./full-view/full-view.module').then(m => m.FullViewModule),
      },
      {
        path: 'full-report',
        loadChildren: () => import('./full-report/full-report.module').then(m => m.FullReportModule),
      },
    ]),
  ],
})
export class RegistryRoutingModule {}
