import { AbstractControl, ValidatorFn } from '@angular/forms';

export function companyRequired(bool: boolean): ValidatorFn {
  return (control: AbstractControl): { [key: string]: boolean } | null => {
    if (!bool && control.value === undefined) return { companyRequired: true };
    else return null;
  };
}
