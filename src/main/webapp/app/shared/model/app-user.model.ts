import { IJob } from 'app/shared/model/job.model';

export interface IAppUser {
  id?: number;
  phone?: string;
  internalUserId?: number;
  jobs?: IJob[];
  companyId?: number;
}

export class AppUser implements IAppUser {
  constructor(public id?: number, public phone?: string, public internalUserId?: number, public jobs?: IJob[], public companyId?: number) {}
}
