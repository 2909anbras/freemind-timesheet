import { Pipe, PipeTransform } from '@angular/core';
import { IJob } from 'app/shared/model/job.model';
@Pipe({
  name: 'jobFilter',
})
export class JobFilterPipe implements PipeTransform {
  transform(jobs: IJob[], arg: string, isEnable: string): IJob[] {
    if (!jobs) return [];
    if (!arg && !isEnable) return jobs;

    arg = arg.toLocaleLowerCase();
    jobs = [...jobs.filter(c => c.name?.toLocaleLowerCase().includes(arg))];

    if (!isEnable || isEnable === 'All') return jobs;
    const bool = isEnable === 'Enable';
    jobs = [...jobs.filter(c => c.enable === bool)];
    console.log('jobs Pipe');
    console.log(jobs);
    return jobs;
  }
}
