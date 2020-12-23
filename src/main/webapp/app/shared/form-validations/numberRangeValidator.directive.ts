import { AbstractControl, ValidatorFn } from '@angular/forms';

export function numberRangeValidator(min: number, max: number): ValidatorFn {
  return (control: AbstractControl): { [key: string]: boolean } | null => {
    if (control.value < 0) return { toBig: true };
    else if (control.value > 10) return { toBig: true };
    else return null;
  };
}
