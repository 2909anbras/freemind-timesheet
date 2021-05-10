// import { NGB_DATEPICKER_18N_FACTORY } from '@ng-bootstrap/ng-bootstrap/datepicker/datepicker-i18n';
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
    if (startDate === undefined) {
      startDate = moment().date(1);
      endDate = startDate;
      tmp.push(startDate);
    } else {
      startDate = moment().date(1).month(startDate.month()).year(startDate.year());
      if (endDate !== undefined) endDate = moment().date(1).month(endDate.month()).year(endDate.year());
      tmp.push(moment(startDate));
      startDate.add(1, 'months');
    }
    if (startDate !== endDate && endDate !== undefined) {
      while (startDate <= endDate) {
        const tmpDate = moment(startDate);
        tmp.push(tmpDate);
        startDate.add(1, 'months');
      }
    }
    this.dates = tmp;
  }
}
