import { IJob } from 'app/shared/model/job.model';
import { ICustomer } from 'app/shared/model/customer.model';

export interface IProject {
  id?: number;
  name?: string;
  enable?: boolean;
  jobs?: IJob[];
  customer?: ICustomer;
}

export class Project implements IProject {
  constructor(public id?: number, public name?: string, public enable?: boolean, public jobs?: IJob[], public customer?: ICustomer) {
    this.enable = this.enable || false;
  }
}
