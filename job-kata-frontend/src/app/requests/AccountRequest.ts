export interface AccountRequest {
    //@Min(0)
    initialBalanceInCents: number;
    //@NotEmpty
    //@Size(min = 3)
    ownerName: string;
}
