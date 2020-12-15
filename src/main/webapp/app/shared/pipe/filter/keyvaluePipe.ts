import { Pipe, PipeTransform } from '@angular/core';
@Pipe({
  name: 'keyvalue',
})
export class KeyvaluePipe implements PipeTransform {
  transform(nbr: number): [] {
    return [].constructor(nbr);
  }
}
