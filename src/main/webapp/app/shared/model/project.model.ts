import { IJob } from 'app/shared/model/job.model';

export interface IProject {
  id?: number;
  name?: string;
  enable?: boolean;
  jobs?: IJob[];
  customerId?: number;
}

export class Project implements IProject {
  constructor(public id?: number, public name?: string, public enable?: boolean, public jobs?: IJob[], public customerId?: number) {
    this.enable = this.enable || false;
  }
}
