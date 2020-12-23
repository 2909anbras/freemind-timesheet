export interface IPerformance {
  id?: number;
  hours?: number;
  jobId?: number;
  appUserId?: number;
  date?: Date;
  description?: string;
}

export class Performance implements IPerformance {
  constructor(
    public id?: number,
    public hours?: number,
    public jobId?: number,
    public appUserId?: number,
    public date?: Date,
    public description?: string
  ) {}
}
