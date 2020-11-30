import { Pipe, PipeTransform } from '@angular/core';
import { ICompany } from 'app/shared/model/company.model';
@Pipe({
  name: 'companyFilter',
})
export class CompanyFilterPipe implements PipeTransform {
  transform(companies: ICompany[], arg: string, isEnable: string): ICompany[] {
    if (!companies) return [];
    if (!arg) return companies;

    arg = arg.toLocaleLowerCase();
    companies = [...companies.filter(company => company.name?.toLocaleLowerCase().includes(arg))];

    if (!isEnable || isEnable === 'All') return companies;
    const bool = isEnable === 'Enable';
    // companies=[...companies.filter(c=>c.)]
    console.log('company Pipe');
    console.log(companies);
    return companies;
  }
}
