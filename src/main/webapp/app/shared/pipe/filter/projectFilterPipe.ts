import { Pipe, PipeTransform } from '@angular/core';
import { IProject } from 'app/shared/model/project.model';
@Pipe({
  name: 'projectFilter',
})
export class ProjectFilterPipe implements PipeTransform {
  transform(projects: IProject[], arg: string, isEnable: string): IProject[] {
    if (!projects) return [];
    if (!arg && !isEnable) return projects;

    arg = arg.toLocaleLowerCase();
    projects = [...projects.filter(c => c.name?.toLocaleLowerCase().includes(arg))];

    if (!isEnable || isEnable === 'All') return projects;
    const bool = isEnable === 'Enable';
    projects = [...projects.filter(c => c.enable === bool)];
    projects = [...projects.filter(project => project.jobs.length > 0)];

    console.log('projects Pipe');
    console.log(projects);
    return projects;
  }
}
