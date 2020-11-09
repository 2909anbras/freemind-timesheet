import { IJob } from 'app/shared/model/job.model';
import { ICompany } from 'app/shared/model/company.model';
import { Language } from 'app/shared/model/enumerations/language.model';

export interface IToolUser {
  id?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  password?: string;
  enable?: boolean;
  language?: Language;
  jobs?: IJob[];
  company?: ICompany;
}

export class ToolUser implements IToolUser {
  constructor(
    public id?: number,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public password?: string,
    public enable?: boolean,
    public language?: Language,
    public jobs?: IJob[],
    public company?: ICompany
  ) {
    this.enable = this.enable || false;
  }
}
