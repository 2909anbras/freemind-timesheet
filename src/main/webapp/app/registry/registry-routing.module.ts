import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
/* jhipster-needle-add-admin-module-import - JHipster will add admin modules imports here */

@NgModule({
  imports: [
    /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
    RouterModule.forChild([
      {
        path: 'global-view',
        loadChildren: () => import('./global-view/global-view.module').then(m => m.GlobalViewModule),
      },
      {
        path: 'full-view',
        loadChildren: () => import('./full-view/full-view.module').then(m => m.FullViewModule),
      },
    ]),
  ],
})
export class RegistryRoutingModule {}
