import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { FreemindTimesheetSharedModule } from 'app/shared/shared.module';
import { FreemindTimesheetCoreModule } from 'app/core/core.module';
import { FreemindTimesheetAppRoutingModule } from './app-routing.module';
import { FreemindTimesheetHomeModule } from './home/home.module';
import { FreemindTimesheetEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';
// import { RegistryRoutingModule } from './registry/registry-routing.module';

@NgModule({
  imports: [
    BrowserModule,
    FreemindTimesheetSharedModule,
    FreemindTimesheetCoreModule,
    FreemindTimesheetHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    FreemindTimesheetEntityModule,
    FreemindTimesheetAppRoutingModule,
    // RegistryRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  bootstrap: [MainComponent],
})
export class FreemindTimesheetAppModule {}
