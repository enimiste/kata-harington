import { DashboardComponent } from './dashboard/dashboard.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { AccountsComponent } from './accounts/accounts.component';
import { AppComponent } from './app.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {path: "", redirectTo: "/dashboard", pathMatch: 'full'},
  {path: "dashboard", component: DashboardComponent, title: "Kata : Bank Account"},
  {path: "accounts", component: AccountsComponent, title: "Kata : Accounts"},
  {path: "**", component: NotFoundComponent, title: "404"},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
