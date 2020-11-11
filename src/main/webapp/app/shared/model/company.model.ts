import { IAppUser } from 'app/shared/model/app-user.model';
import { ICustomer } from 'app/shared/model/customer.model';

export interface ICompany {
  id?: number;
  name?: string;
  appUsers?: IAppUser[];
  customers?: ICustomer[];
}

export class Company implements ICompany {
  constructor(public id?: number, public name?: string, public appUsers?: IAppUser[], public customers?: ICustomer[]) {}
}
