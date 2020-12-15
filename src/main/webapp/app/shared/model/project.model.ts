import { IJob } from 'app/shared/model/job.model';

export interface IProject {
  id?: number;
  name?: string;
  enable?: boolean;
  jobs: IJob[];
  customerId?: number;
  // getJobsByUser:(id:number)=>IJob[];
}

export class Project implements IProject {
  constructor(public id?: number, public name?: string, public enable?: boolean, public jobs: IJob[] = []!, public customerId?: number) {
    this.enable = this.enable || false;
  }

  // public getJobsByUser(id:number):IJob[]{
  //   const jobs: IJob[] = [];
  //   this.jobs.forEach(j => {
  //     if (j.appUsers?.some(ap => ap.id === id)) {
  //       jobs.push(j);
  //       console.log(j);
  //     }
  //   });
  //   return jobs;
  // }
}
