import { IJob } from 'app/shared/model/job.model';

export interface IPerformance {
  id?: number;
  hours?: number;
  jobId?: number;
  userId?: number;
  date?: Date;
}

export class Performance implements IPerformance {
  constructor(public id?: number, public hours?: number, public jobId?: number, public userId?: number, public date?: Date) {}
}
