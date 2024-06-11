package com.banking.authenticator.service;


import com.banking.authenticator.controller.UserController;
import com.banking.authenticator.model.BankingUser;
import com.banking.authenticator.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private static final Logger log = LoggerFactory.getLogger( UserService.class);
    @Autowired
    private UserRepository userRepository;



    public Mono<String> forgotUserId(String accountNumber, String otp) {
        // Verify OTP and retrieve User ID
        return userRepository.findByAccountNumber(accountNumber)
                .flatMap(bankingUser -> {
                    if (bankingUser.getOtp().equals(otp)) {
                        log.info("Your User ID is: {}", bankingUser.getUserId());
                        return Mono.just("Your User ID is: " + bankingUser.getUserId());
                    } else {
                        log.info("Invalid OTP. Please try again.");
                        return Mono.just("Invalid OTP. Please try again.");
                    }
                });
    }

    public Mono<String> forgotPassword(String userId, String otp) {
        // Verify OTP and allow password reset
        return userRepository.findByUserId(userId)
                .flatMap(bankingUser -> {
                    if (bankingUser.getOtp().equals(otp)) {
                        log.info("OTP verified. Please set a new password.");
                        return Mono.just("OTP verified. Please set a new password.");
                    } else {
                        log.info("Invalid OTP. Please try again.");
                        return Mono.just("Invalid OTP. Please try again.");
                    }
                });
    }

    public Mono<String> accountLocked(String userId) {
        // Handle account locking
        return userRepository.findByUserId(userId)
                .flatMap(bankingUser -> {
                    log.info("Setting Password to NULL for resetting");
                    bankingUser.setPassword("null");
                    return userRepository.save(bankingUser)
                            .then(Mono.just("Account locked. Please reset your password."));
                })
                .switchIfEmpty(Mono.just("User not found"));
    }

    public Mono<String> setNewPassword(String userId, String newPassword) {
        // Set a new password
        return userRepository.findByUserId(userId)
                .flatMap(bankingUser -> {
                    bankingUser.setPassword(newPassword);
                    log.info("Changing Password for UserId - {}", userId);
                    return userRepository.save(bankingUser)
                            .then(Mono.just("Password changed successfully."));
                });
    }

    public Flux<BankingUser> registerUser(List<BankingUser> bankingUserList) {
        return userRepository.saveAll(bankingUserList);
    }
}
