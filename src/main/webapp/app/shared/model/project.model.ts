import { IJob } from 'app/shared/model/job.model';

export interface IProject {
  id?: number;
  name?: string;
  enable?: boolean;
  jobs: IJob[];
  customerId?: number;
  customerName?: string;
  // hidden?:boolean;
}

export class Project implements IProject {
  constructor(
    public id?: number,
    public name?: string,
    public enable?: boolean,
    public jobs: IJob[] = []!,
    public customerName?: string,
    public customerId?: number
  ) // public hidden:boolean=false,
  {
    this.enable = this.enable || false;
  }
}
