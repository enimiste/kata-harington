import { HttpErrorResponse } from '@angular/common/http';
import { OperationService } from './../../operation.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-withdrawal-dialog',
  templateUrl: './withdrawal-dialog.component.html',
  styleUrls: ['./withdrawal-dialog.component.css']
})
export class WithdrawalDialogComponent implements OnInit {

  constructor(private operationService:OperationService) { }

  ngOnInit(): void {
  }

  onSubmit() {
  }

}
