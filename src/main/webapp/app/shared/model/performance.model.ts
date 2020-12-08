import { IJob } from 'app/shared/model/job.model';

export interface ICustomer {
  id?: number;
  hours?: number;
  jobId?: number;
  userId?: number;
  date?: Date;
}

export class Customer implements ICustomer {
  constructor(public id?: number, public hours?: number, public jobId?: number, public userId?: number, public date?: Date) {}
}
