import { NGB_DATEPICKER_18N_FACTORY } from '@ng-bootstrap/ng-bootstrap/datepicker/datepicker-i18n';
import { Moment } from 'moment';
import * as moment from 'moment';

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

  public setDates(startDate: Moment, endDate: Moment): void {
    const tmp = [];
    tmp.push(startDate);
    while (startDate < endDate) {
      const tmpDate = moment(startDate.add(1, 'months'));
      tmp.push(tmpDate);
    }
    this.dates = tmp;
    console.log(this.dates);
  }
}
