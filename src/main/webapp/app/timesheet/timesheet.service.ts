import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';

type EntityResponseType = HttpResponse<any>;
// type EntityArrayResponseType = HttpResponse<ICompany[]>;

@Injectable({ providedIn: 'root' })
export class TimesheetService {
  public resourceUrl = SERVER_API_URL + 'api/report';

  constructor(protected http: HttpClient) {}

  create(date: Date, userId: number): Observable<HttpResponse<any>> {
    console.log('DEDANS LE SERVICE');
    console.log(date);

    return this.http.post<any>(`${this.resourceUrl + '/fullReport'}/${userId}`, date, { observe: 'response' });
  }
}
