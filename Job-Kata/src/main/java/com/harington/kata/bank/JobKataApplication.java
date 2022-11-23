package com.harington.kata.bank;

import com.harington.kata.bank.entity.Account;
import com.harington.kata.bank.entity.Transaction;
import com.harington.kata.bank.repository.AccountRepository;
import com.harington.kata.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class JobKataApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobKataApplication.class, args);
	}

	@Bean
	public CorsFilter corsFilter(@Value("${frontend.app.url}") String  frontendAppUrl) {
		System.out.println(frontendAppUrl);

		var corsConfig = new CorsConfiguration();
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedOrigins(List.of(frontendAppUrl));
		corsConfig.setAllowedHeaders(List.of(
				"Origin", "Access-Control-Allow-Origin",
				"Content-Type", "Accept", "Authorization",
				"Origin, Accept", "X-Request-With",
				"Access-Control-Request-Method",
				"Access-Control-Request-Headers"
		));
		corsConfig.setExposedHeaders(List.of(
				"Origin", "Content-Type", "Accept", "Authorization",
				"Access-Control-Allow-Origin",
				"Access-Control-Allow-Credentials"
		));
		corsConfig.setAllowedMethods(List.of(
				"GET", "POST", "PUT", "DELETE", "OPTIONS"
		));
		var usrCorsConfig = new UrlBasedCorsConfigurationSource();
		usrCorsConfig.registerCorsConfiguration("/**", corsConfig);
		return new CorsFilter(usrCorsConfig);
	}

	@Bean
	@Profile("production")
	public CommandLineRunner testData(AccountRepository accountRepository,
									  TransactionRepository transactionRepository){
		return args -> {
			if (accountRepository.count() != 0) return;
			//Account N° 1
			accountRepository.save(
					Account.builder()
							.accountNumber(UUID.randomUUID())
							.initialBalanceInCents(100_00)
							.currentBalanceInCents(100_00)
							.ownerName("Anis BESSA")
							.createdAt(LocalDateTime.now().minusMonths(1))
							.build());
			//Account N° 2
			Account account2 = accountRepository.save(
					Account.builder()
							.accountNumber(UUID.randomUUID())
							.initialBalanceInCents(0)
							.currentBalanceInCents(100_00)
							.ownerName("NOUNI EL Bachir")
							.createdAt(LocalDateTime.now().minusMonths(1))
							.build());
			List<Transaction> txs = List.of(
					Transaction.builder()
							.txRef(UUID.randomUUID())
							.account(account2)
							.txType(Transaction.TxType.DEPOSIT)
							.amountInCents(40_00)
							.description("Depot d'argent 1")
							.postTxAccountBalanceInCents(40_00)
							.transactionAt(LocalDateTime.now().minusDays(15))
							.build(),
					Transaction.builder()
							.txRef(UUID.randomUUID())
							.account(account2)
							.txType(Transaction.TxType.DEPOSIT)
							.amountInCents(80_00)
							.description("Depot d'argent 2")
							.postTxAccountBalanceInCents(120_00)
							.transactionAt(LocalDateTime.now().minusDays(10))
							.build(),
					Transaction.builder()
							.txRef(UUID.randomUUID())
							.account(account2)
							.txType(Transaction.TxType.WITHDRAWAL)
							.amountInCents(20_00)
							.description("Withdrawal d'argent 1")
							.postTxAccountBalanceInCents(100_00)
							.transactionAt(LocalDateTime.now().minusDays(1))
							.build()
			);
			txs.forEach(account2::addTx);
			transactionRepository.saveAll(txs);
		};
	}
}
