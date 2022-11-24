import { OperationRequest } from './requests/OperationRequest';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OperationDto } from './entities/OperationDto';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OperationService {
  private TX_API_URL = `${environment.apiBaseUrl}/accounts/transactions`;
  private ACCOUNT_API_URL = `${environment.apiBaseUrl}/accounts`;
  private HEADERS = { "Content-Type": "application/json" };

  constructor(private http: HttpClient) { }

  loadHistoryFor(accountNumber: string): Observable<OperationDto[]>{
    return this.http.get<OperationDto[]>(`${this.ACCOUNT_API_URL}/${accountNumber}/transactions`,
      { headers: this.HEADERS });
  }

  deposit(request: OperationRequest): Observable<OperationDto> {
    request.operation = "DEPOSIT";
    return this.http.post<OperationDto>(`${this.TX_API_URL}`, request, { headers: this.HEADERS });
  }

  withdrawal(request: OperationRequest): Observable<OperationDto> {
    request.operation = "WITHDRAWAL";
    return this.http.post<OperationDto>(`${this.TX_API_URL}`,
      request,
      { headers: this.HEADERS });
  }
}
