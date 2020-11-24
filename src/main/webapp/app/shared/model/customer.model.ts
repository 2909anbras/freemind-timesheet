import { IProject } from 'app/shared/model/project.model';

export interface ICustomer {
  id?: number;
  name?: string;
  enable?: boolean;
  projects?: IProject[];
  companyId?: number;
}

export class Customer implements ICustomer {
  constructor(public id?: number, public name?: string, public enable?: boolean, public projects?: IProject[], public companyId?: number) {
    this.enable = this.enable || false;
  }
}
