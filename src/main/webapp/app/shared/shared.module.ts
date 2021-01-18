import { NgModule } from '@angular/core';
import { FreemindTimesheetSharedLibsModule } from './shared-libs.module';
import { FindLanguageFromKeyPipe } from './language/find-language-from-key.pipe';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';

import { LoginModalComponent } from './login/login.component';
import { PerformanceCreateDialogComponent } from 'app/entities/performance/performance-create-dialog.component';

import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { JobFilterPipe } from './pipe/filter/jobFilterPipe';
import { ProjectFilterPipe } from './pipe/filter/projectFilterPipe';
import { CustomerFilterPipe } from './pipe/filter/customerFilterPipe';
import { CompanyFilterPipe } from './pipe/filter/companyFilterPipe';
import { KeyvaluePipe } from './pipe/filter/keyvaluePipe';
import { UserFilterPipe } from './pipe/filter/userFilterPipe';
// import { RegistryRoutingModule} from '../registry/registry-routing.module';
@NgModule({
  imports: [FreemindTimesheetSharedLibsModule],
  declarations: [
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    PerformanceCreateDialogComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    JobFilterPipe,
    ProjectFilterPipe,
    CustomerFilterPipe,
    UserFilterPipe,
    CompanyFilterPipe,
    KeyvaluePipe,
  ],
  entryComponents: [LoginModalComponent, PerformanceCreateDialogComponent],
  exports: [
    FreemindTimesheetSharedLibsModule,
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    PerformanceCreateDialogComponent,
    HasAnyAuthorityDirective,
    JobFilterPipe,
    UserFilterPipe,
    ProjectFilterPipe,
    CustomerFilterPipe,
    CompanyFilterPipe,
    KeyvaluePipe,
  ],
})
export class FreemindTimesheetSharedModule {}
