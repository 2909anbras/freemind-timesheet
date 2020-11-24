import { IAppUser } from 'app/shared/model/app-user.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { IProject } from 'app/shared/model/project.model';

export interface ICompany {
  id?: number;
  name?: string;
  appUsers?: IAppUser[];
  customers?: ICustomer[];
  projects?: IProject[];
}

export class Company implements ICompany {
  constructor(
    public id?: number,
    public name?: string,
    public appUsers?: IAppUser[],
    public customers?: ICustomer[],
    public projects?: IProject[]
  ) {}
}
