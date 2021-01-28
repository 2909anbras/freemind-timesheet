import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpHeaders } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { RequestOptions } from 'http';
import { SERVER_API_URL } from 'app/app.constants';
import { IReport } from 'app/shared/model/report.model';
type EntityResponseType = HttpResponse<IReport>;

@Injectable({ providedIn: 'root' })
export class FullReportService {
  public resourceUrl = SERVER_API_URL + 'api/report';

  constructor(protected http: HttpClient) {}

  create(report: IReport): any {
    console.log(report);
    const HTTPOptions: Object = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      // responseType: 'blob' as 'json'
      responseType: 'blob',
    };
    return this.http.post<any>(this.resourceUrl, report, HTTPOptions).pipe(
      map((res: any) => {
        return new Blob([res], { type: 'application/vnd.ms-excel' });
      })
    );
  }
}
// return this.http.post<IReport>(this.resourceUrl, report, { observe: 'response' });
