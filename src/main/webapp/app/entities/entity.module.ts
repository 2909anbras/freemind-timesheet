import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'tool-user',
        loadChildren: () => import('./tool-user/tool-user.module').then(m => m.FreemindTimesheetToolUserModule),
      },
      {
        path: 'company',
        loadChildren: () => import('./company/company.module').then(m => m.FreemindTimesheetCompanyModule),
      },
      {
        path: 'customer',
        loadChildren: () => import('./customer/customer.module').then(m => m.FreemindTimesheetCustomerModule),
      },
      {
        path: 'project',
        loadChildren: () => import('./project/project.module').then(m => m.FreemindTimesheetProjectModule),
      },
      {
        path: 'job',
        loadChildren: () => import('./job/job.module').then(m => m.FreemindTimesheetJobModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class FreemindTimesheetEntityModule {}
