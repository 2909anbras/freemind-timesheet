import { IProject } from 'app/shared/model/project.model';
import { ICompany } from 'app/shared/model/company.model';

export interface ICustomer {
  id?: number;
  name?: string;
  enable?: boolean;
  projects?: IProject[];
  companies?: ICompany[];
}

export class Customer implements ICustomer {
  constructor(
    public id?: number,
    public name?: string,
    public enable?: boolean,
    public projects?: IProject[],
    public companies?: ICompany[]
  ) {
    this.enable = this.enable || false;
  }
}
