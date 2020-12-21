import { Pipe, PipeTransform } from '@angular/core';
import { ICustomer } from 'app/shared/model/customer.model';
@Pipe({
  name: 'customerFilter',
})
export class CustomerFilterPipe implements PipeTransform {
  transform(customers: ICustomer[], arg: string, isEnable: string): ICustomer[] {
    if (!customers) return [];
    if (!arg && !isEnable) return customers;

    arg = arg.toLocaleLowerCase();
    customers = [...customers.filter(c => c.name?.toLocaleLowerCase().includes(arg))];

    if (!isEnable || isEnable === 'All') return customers;
    const bool = isEnable === 'Enable';
    customers = [...customers.filter(c => c.enable === bool)];
    // customers = [...customers.filter(customer => customer.projects.length > 0)];

    return customers;
  }
}
