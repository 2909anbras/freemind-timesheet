import { Moment } from 'moment';
import { IAppUser } from 'app/shared/model/app-user.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface IJob {
  id?: number;
  name?: string;
  description?: string;
  status?: Status;
  startDate?: Moment;
  endDate?: Moment;
  enable?: boolean;
  projectId?: number;
  appUsers?: IAppUser[];
}

export class Job implements IJob {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string,
    public status?: Status,
    public startDate?: Moment,
    public endDate?: Moment,
    public enable?: boolean,
    public projectId?: number,
    public appUsers?: IAppUser[]
  ) {
    this.enable = this.enable || false;
  }
}
