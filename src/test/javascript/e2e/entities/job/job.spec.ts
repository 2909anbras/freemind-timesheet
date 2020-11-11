import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { JobComponentsPage, JobDeleteDialog, JobUpdatePage } from './job.page-object';

const expect = chai.expect;

describe('Job e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let jobComponentsPage: JobComponentsPage;
  let jobUpdatePage: JobUpdatePage;
  let jobDeleteDialog: JobDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Jobs', async () => {
    await navBarPage.goToEntity('job');
    jobComponentsPage = new JobComponentsPage();
    await browser.wait(ec.visibilityOf(jobComponentsPage.title), 5000);
    expect(await jobComponentsPage.getTitle()).to.eq('freemindTimesheetApp.job.home.title');
    await browser.wait(ec.or(ec.visibilityOf(jobComponentsPage.entities), ec.visibilityOf(jobComponentsPage.noResult)), 1000);
  });

  it('should load create Job page', async () => {
    await jobComponentsPage.clickOnCreateButton();
    jobUpdatePage = new JobUpdatePage();
    expect(await jobUpdatePage.getPageTitle()).to.eq('freemindTimesheetApp.job.home.createOrEditLabel');
    await jobUpdatePage.cancel();
  });

  it('should create and save Jobs', async () => {
    const nbButtonsBeforeCreate = await jobComponentsPage.countDeleteButtons();

    await jobComponentsPage.clickOnCreateButton();

    await promise.all([
      jobUpdatePage.setNameInput('name'),
      jobUpdatePage.setDescriptionInput('description'),
      jobUpdatePage.statusSelectLastOption(),
      jobUpdatePage.setStartDateInput('2000-12-31'),
      jobUpdatePage.setEndDateInput('2000-12-31'),
      jobUpdatePage.projectSelectLastOption(),
    ]);

    expect(await jobUpdatePage.getNameInput()).to.eq('name', 'Expected Name value to be equals to name');
    expect(await jobUpdatePage.getDescriptionInput()).to.eq('description', 'Expected Description value to be equals to description');
    expect(await jobUpdatePage.getStartDateInput()).to.eq('2000-12-31', 'Expected startDate value to be equals to 2000-12-31');
    expect(await jobUpdatePage.getEndDateInput()).to.eq('2000-12-31', 'Expected endDate value to be equals to 2000-12-31');
    const selectedEnable = jobUpdatePage.getEnableInput();
    if (await selectedEnable.isSelected()) {
      await jobUpdatePage.getEnableInput().click();
      expect(await jobUpdatePage.getEnableInput().isSelected(), 'Expected enable not to be selected').to.be.false;
    } else {
      await jobUpdatePage.getEnableInput().click();
      expect(await jobUpdatePage.getEnableInput().isSelected(), 'Expected enable to be selected').to.be.true;
    }

    await jobUpdatePage.save();
    expect(await jobUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await jobComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Job', async () => {
    const nbButtonsBeforeDelete = await jobComponentsPage.countDeleteButtons();
    await jobComponentsPage.clickOnLastDeleteButton();

    jobDeleteDialog = new JobDeleteDialog();
    expect(await jobDeleteDialog.getDialogTitle()).to.eq('freemindTimesheetApp.job.delete.question');
    await jobDeleteDialog.clickOnConfirmButton();

    expect(await jobComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
