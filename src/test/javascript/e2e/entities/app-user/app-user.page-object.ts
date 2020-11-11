import { element, by, ElementFinder } from 'protractor';

export class AppUserComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-app-user div table .btn-danger'));
  title = element.all(by.css('jhi-app-user div h2#page-heading span')).first();
  noResult = element(by.id('no-result'));
  entities = element(by.id('entities'));

  async clickOnCreateButton(): Promise<void> {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(): Promise<void> {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons(): Promise<number> {
    return this.deleteButtons.count();
  }

  async getTitle(): Promise<string> {
    return this.title.getAttribute('jhiTranslate');
  }
}

export class AppUserUpdatePage {
  pageTitle = element(by.id('jhi-app-user-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  internalUserSelect = element(by.id('field_internalUser'));
  jobSelect = element(by.id('field_job'));
  companySelect = element(by.id('field_company'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async internalUserSelectLastOption(): Promise<void> {
    await this.internalUserSelect.all(by.tagName('option')).last().click();
  }

  async internalUserSelectOption(option: string): Promise<void> {
    await this.internalUserSelect.sendKeys(option);
  }

  getInternalUserSelect(): ElementFinder {
    return this.internalUserSelect;
  }

  async getInternalUserSelectedOption(): Promise<string> {
    return await this.internalUserSelect.element(by.css('option:checked')).getText();
  }

  async jobSelectLastOption(): Promise<void> {
    await this.jobSelect.all(by.tagName('option')).last().click();
  }

  async jobSelectOption(option: string): Promise<void> {
    await this.jobSelect.sendKeys(option);
  }

  getJobSelect(): ElementFinder {
    return this.jobSelect;
  }

  async getJobSelectedOption(): Promise<string> {
    return await this.jobSelect.element(by.css('option:checked')).getText();
  }

  async companySelectLastOption(): Promise<void> {
    await this.companySelect.all(by.tagName('option')).last().click();
  }

  async companySelectOption(option: string): Promise<void> {
    await this.companySelect.sendKeys(option);
  }

  getCompanySelect(): ElementFinder {
    return this.companySelect;
  }

  async getCompanySelectedOption(): Promise<string> {
    return await this.companySelect.element(by.css('option:checked')).getText();
  }

  async save(): Promise<void> {
    await this.saveButton.click();
  }

  async cancel(): Promise<void> {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class AppUserDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-appUser-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-appUser'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
