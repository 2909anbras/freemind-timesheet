import { IUser } from 'app/core/user/user.model';
import { IJob } from 'app/shared/model/job.model';
import { ICompany } from 'app/shared/model/company.model';

export interface IAppUser {
  id?: number;
  internalUser?: IUser;
  jobs?: IJob[];
  company?: ICompany;
}

export class AppUser implements IAppUser {
  constructor(public id?: number, public internalUser?: IUser, public jobs?: IJob[], public company?: ICompany) {}
}
