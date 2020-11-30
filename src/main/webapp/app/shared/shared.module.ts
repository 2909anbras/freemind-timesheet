import { NgModule } from '@angular/core';
import { FreemindTimesheetSharedLibsModule } from './shared-libs.module';
import { FindLanguageFromKeyPipe } from './language/find-language-from-key.pipe';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';
import { LoginModalComponent } from './login/login.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { JobFilterPipe } from './pipe/filter/jobFilterPipe';
import { ProjectFilterPipe } from './pipe/filter/projectFilterPipe';
import { CustomerFilterPipe } from './pipe/filter/customerFilterPipe';
import { CompanyFilterPipe } from './pipe/filter/companyFilterPipe';

@NgModule({
  imports: [FreemindTimesheetSharedLibsModule],
  declarations: [
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    JobFilterPipe,
    ProjectFilterPipe,
    CustomerFilterPipe,
    CompanyFilterPipe,
  ],
  entryComponents: [LoginModalComponent],
  exports: [
    FreemindTimesheetSharedLibsModule,
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
  ],
})
export class FreemindTimesheetSharedModule {}
