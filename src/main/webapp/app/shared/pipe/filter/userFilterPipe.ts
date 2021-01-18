import { Pipe, PipeTransform } from '@angular/core';
import { IUser } from 'app/core/user/user.model';
@Pipe({
  name: 'userFilter',
})
export class UserFilterPipe implements PipeTransform {
  transform(users: IUser[], arg: string, isEnable: string): IUser[] {
    if (!users) return [];
    if (!arg && !isEnable) return users;

    arg = arg.toLocaleLowerCase();
    users = [...users.filter(c => c.login?.toLocaleLowerCase().includes(arg))];

    if (!isEnable || isEnable === 'All') return users;
    const bool = isEnable === 'Enable';
    users = [...users.filter(c => c.activated === bool)];
    return users;
  }
}
