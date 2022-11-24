import { AccountDto } from './../../entities/AccountDto';
import { HttpErrorResponse } from '@angular/common/http';
import { OperationService } from './../../operation.service';
import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-deposit-dialog',
  templateUrl: './deposit-dialog.component.html',
  styleUrls: ['./deposit-dialog.component.css']
})
export class DepositDialogComponent implements OnInit {

  constructor(private operationService: OperationService,
    private dialogRef: MatDialogRef<DepositDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: AccountDto) { }

  ngOnInit(): void {
  }

  onSubmit() {
    this.operationService.deposit({
      amountInCents: 10000,
      description: "Deposit from angular app",
      accountNumber: this.data.accountNumber,
      operation: ""
    }).subscribe({
      next: (accountDto) => this.dialogRef.close(accountDto),
      error: (error: HttpErrorResponse) => console.error(error.message)
    });
  }
}
