export interface OperationRequest {
    accountNumber: string;
    //@Min(1)
    amountInCents: number;
    description: string;
    operation: string;
}
