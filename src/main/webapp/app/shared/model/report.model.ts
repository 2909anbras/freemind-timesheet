import { Moment } from 'moment';

export interface IReport {
  usersId?: number[];
  jobsId?: number[];
  projectsId?: number[];
  customersId?: number[];
  dates?: Moment[];
  companiesId?: number[];
}

export class Report implements IReport {
  constructor(
    public companiesId?: number[],
    public jobsId?: number[],
    public customersId?: number[],
    public usersId?: number[],
    public dates?: Moment[],
    public projectsId?: number[]
  ) {}
}
