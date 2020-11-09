import { Moment } from 'moment';
import { IProject } from 'app/shared/model/project.model';
import { IToolUser } from 'app/shared/model/tool-user.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface IJob {
  id?: number;
  name?: string;
  description?: string;
  status?: Status;
  startDate?: Moment;
  endDate?: Moment;
  enable?: boolean;
  project?: IProject;
  toolUsers?: IToolUser[];
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
    public project?: IProject,
    public toolUsers?: IToolUser[]
  ) {
    this.enable = this.enable || false;
  }
}
