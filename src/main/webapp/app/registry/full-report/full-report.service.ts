import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { IReport } from 'app/shared/model/report.model';
type EntityResponseType = HttpResponse<IReport>;

@Injectable({ providedIn: 'root' })
export class FullReportService {
  public resourceUrl = SERVER_API_URL + 'api/report';

  constructor(protected http: HttpClient) {}

  create(report: IReport): Observable<HttpResponse<any>> {
    console.log(report);
    return this.http.post<IReport>(this.resourceUrl, report, { observe: 'response' });
  }
}
