import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { AppUserComponentsPage, AppUserDeleteDialog, AppUserUpdatePage } from './app-user.page-object';

const expect = chai.expect;

describe('AppUser e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let appUserComponentsPage: AppUserComponentsPage;
  let appUserUpdatePage: AppUserUpdatePage;
  let appUserDeleteDialog: AppUserDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load AppUsers', async () => {
    await navBarPage.goToEntity('app-user');
    appUserComponentsPage = new AppUserComponentsPage();
    await browser.wait(ec.visibilityOf(appUserComponentsPage.title), 5000);
    expect(await appUserComponentsPage.getTitle()).to.eq('freemindTimesheetApp.appUser.home.title');
    await browser.wait(ec.or(ec.visibilityOf(appUserComponentsPage.entities), ec.visibilityOf(appUserComponentsPage.noResult)), 1000);
  });

  it('should load create AppUser page', async () => {
    await appUserComponentsPage.clickOnCreateButton();
    appUserUpdatePage = new AppUserUpdatePage();
    expect(await appUserUpdatePage.getPageTitle()).to.eq('freemindTimesheetApp.appUser.home.createOrEditLabel');
    await appUserUpdatePage.cancel();
  });

  it('should create and save AppUsers', async () => {
    const nbButtonsBeforeCreate = await appUserComponentsPage.countDeleteButtons();

    await appUserComponentsPage.clickOnCreateButton();

    await promise.all([
      appUserUpdatePage.setPhoneInput('5'),
      appUserUpdatePage.internalUserSelectLastOption(),
      // appUserUpdatePage.jobSelectLastOption(),
      appUserUpdatePage.companySelectLastOption(),
    ]);

    expect(await appUserUpdatePage.getPhoneInput()).to.eq('5', 'Expected phone value to be equals to 5');

    await appUserUpdatePage.save();
    expect(await appUserUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await appUserComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last AppUser', async () => {
    const nbButtonsBeforeDelete = await appUserComponentsPage.countDeleteButtons();
    await appUserComponentsPage.clickOnLastDeleteButton();

    appUserDeleteDialog = new AppUserDeleteDialog();
    expect(await appUserDeleteDialog.getDialogTitle()).to.eq('freemindTimesheetApp.appUser.delete.question');
    await appUserDeleteDialog.clickOnConfirmButton();

    expect(await appUserComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
