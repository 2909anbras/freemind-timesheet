import { IProject } from 'app/shared/model/project.model';

export interface ICustomer {
  id?: number;
  name?: string;
  enable?: boolean;
  projects: IProject[];
  companyId?: number;
  companyName?: string;
  // hidden?:boolean;
}

export class Customer implements ICustomer {
  constructor(
    public id?: number,
    public name?: string,
    public enable?: boolean,
    public projects: IProject[] = []!,
    public companyId?: number,
    public companyName?: string
  ) // public hidden:boolean=false,

  {
    this.enable = this.enable || false;
  }
}
