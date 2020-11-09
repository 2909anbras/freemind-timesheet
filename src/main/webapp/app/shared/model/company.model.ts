import { IToolUser } from 'app/shared/model/tool-user.model';
import { ICustomer } from 'app/shared/model/customer.model';

export interface ICompany {
  id?: number;
  name?: string;
  toolUsers?: IToolUser[];
  customers?: ICustomer[];
}

export class Company implements ICompany {
  constructor(public id?: number, public name?: string, public toolUsers?: IToolUser[], public customers?: ICustomer[]) {}
}
