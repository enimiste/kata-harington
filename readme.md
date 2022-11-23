## Run all tests :

``` shell
mvn clean test
```

## Run backend :

```shell
mvn spring-boot:run -Dspring-boot.run.profiles=development
```

## API :

``` shell
GET /api/v1/accounts (accounts liste)
POST /api/v1/accounts (create new account)
GET /api/v1/accounts/{accountNumber} (account details)
GET /api/v1/accounts/{accountNumber}/transactions (operations history)
POST /api/v1/accounts/transactions (create new operation (deposit or withdrawal)
```