# Backend :

## Run all tests :

```shell
cd ./Job-Kata
mvn clean test
```

## Run service :

```shell
cd ./Job-Kata
mvn spring-boot:run -Dspring-boot.run.profiles=development
```

It will run on port 8080

## API :

```shell
GET /api/v1/accounts (accounts liste)
POST /api/v1/accounts (create new account)
GET /api/v1/accounts/{accountNumber} (account details)
GET /api/v1/accounts/{accountNumber}/transactions (operations history)
POST /api/v1/accounts/transactions (create new operation (deposit or withdrawal)
```

# Frontend :

```shell
cd ./
ng serve
```

It will run on port 4200
