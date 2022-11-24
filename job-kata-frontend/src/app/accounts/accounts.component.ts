import { OperationService } from './../operation.service';
import { WithdrawalDialogComponent } from './withdrawal-dialog/withdrawal-dialog.component';
import { DepositDialogComponent } from './deposit-dialog/deposit-dialog.component';
import { OperationDto } from './../entities/OperationDto';
import { AccountService } from './../account.service';
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AccountDto } from '../entities/AccountDto';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.css']
})
export class AccountsComponent implements OnInit {
  displayedColumns: string[] = ["accountNumber", "ownerName", "currentBalance", "createdAt", "actions"];
  items: AccountDto[] = [];
  operationHistory: OperationDto[] = [];

  constructor(private accountService: AccountService,
              private operationService: OperationService,
              private dialog: MatDialog) { }

  ngOnInit(): void {
    this.refreshList();
  }

  refreshList() {
    this.accountService.loadAllAccounts()
      .subscribe({
        next: (data) => this.items = data,
        error: (error: HttpErrorResponse) => console.error(error.message)
      });
  }

  openDepositDialog(element: AccountDto) {
    let dialogRef = this.dialog.open(DepositDialogComponent, {disableClose: true, data: element});
    dialogRef.afterClosed().subscribe({
      next: (accountDto) => this.refreshList()
    });
  }

  openWithdrawalDialog(element: AccountDto) {
    let dialogRef = this.dialog.open(WithdrawalDialogComponent, {disableClose: true, data: element});
    dialogRef.afterClosed().subscribe({
      next: (accountDto) => this.refreshList()
    });
  }

  showOperationHistory(element: AccountDto) {
    this.operationService.loadHistoryFor(element.accountNumber)
      .subscribe({
        next: (data) => {
          this.operationHistory = data;
          console.log(data);
        },
        error: (error: HttpErrorResponse) => console.error(error.message)
    })
  }

}
