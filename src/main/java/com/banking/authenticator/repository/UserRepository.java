package com.banking.authenticator.repository;


import com.banking.authenticator.model.BankingUser;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<BankingUser, Long> {
    Mono<BankingUser> findByAccountNumber(String accountNumber);
    Mono<BankingUser> findByUserId(String userId);
    Mono<Void> deleteByUserId(String userId);
}
