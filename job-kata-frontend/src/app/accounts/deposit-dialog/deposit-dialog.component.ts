import { OperationRequest } from './../../requests/OperationRequest';
import { AccountRequest } from './../../requests/AccountRequest';
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

  form: FormGroup;
  constructor(private operationService: OperationService,
    private dialogRef: MatDialogRef<DepositDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: AccountDto,
    private fb: FormBuilder) {
      this.form = this.fb.group({
        amountInCents: this.fb.control([1]),
        description: this.fb.control([""]),
        accountNumber: this.fb.control({ value: this.data.accountNumber , disabled: true})
      }, {}); }

  ngOnInit(): void {
  }

  onSubmit() {
    this.operationService.deposit({
      amountInCents: this.form.controls["amountInCents"].value,
      description: this.form.controls["description"].value,
      accountNumber: this.data.accountNumber,
      operation: "DEPOSIT"
    }).subscribe({
      next: (accountDto) => this.dialogRef.close(accountDto),
      error: (error: HttpErrorResponse) => console.error(error.message)
    });
  }
}
