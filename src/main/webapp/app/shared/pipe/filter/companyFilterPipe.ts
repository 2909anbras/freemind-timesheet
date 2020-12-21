import { Pipe, PipeTransform } from '@angular/core';
import { ICompany } from 'app/shared/model/company.model';
@Pipe({
  name: 'companyFilter',
})
export class CompanyFilterPipe implements PipeTransform {
  transform(companies: ICompany[], arg: string): ICompany[] {
    if (!companies) return [];
    if (!arg) return companies;

    arg = arg.toLocaleLowerCase();
    companies = [...companies.filter(company => company.name?.toLocaleLowerCase().includes(arg))];
    // companies = [...companies.filter(company => company.customers.length > 0)];
    this.sortingCompanies(companies);
    return companies;
  }

  private sortingCompanies(companies: ICompany[]): void {
    companies.sort((a, b) => {
      if (a.customers.length > 0 && a.name && b.name) {
        if (a.name.localeCompare(b.name) > 1) {
          return 1;
        } else if (a.name.localeCompare(b.name) < 1) return -1;
        else return 0;
      } else return -1;
    });
  }
}
