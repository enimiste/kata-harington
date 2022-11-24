import { environment } from './../environments/environment';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { AccountDto } from './entities/AccountDto';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private ACCOUNT_API_URL = `${environment.apiBaseUrl}/accounts`;
  private HEADERS = { "Content-Type": "application/json" };

  constructor(private http: HttpClient) { }

  loadAllAccounts(): Observable<AccountDto[]> {
    return this.http.get<AccountDto[]>(this.ACCOUNT_API_URL, { headers: this.HEADERS });
  }
}
