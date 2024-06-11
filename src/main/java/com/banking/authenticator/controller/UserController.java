package com.banking.authenticator.controller;

import com.banking.authenticator.model.BankingUser;
import com.banking.authenticator.service.UserService;
//import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private static final Logger log = LoggerFactory.getLogger( UserController.class);
    @Autowired
    private UserService userService;

    @PostMapping("/registerUsers")
    public Flux<BankingUser> registerUser(@RequestBody List<BankingUser> bankingUserList){
        return  userService.registerUser(bankingUserList);
    }

    @PostMapping(value = "/forgotUserId", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> forgotUserId(@RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNumber");
        String otp = payload.get("otp");
        log.info("forgotUserId api invoked with accountNumber: {}, otp: {}", accountNumber, otp);
        return userService.forgotUserId(accountNumber, otp)
                .map(response -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("message", response);
                    return result;
                })
                .defaultIfEmpty(new HashMap<String, String>() {{
                    put("message", "No response from server");
                }});
    }

    @PostMapping(value = "/forgotPassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> forgotPassword(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String otp = payload.get("otp");
        log.info("forgotPassword api invoked with userId: {}, otp: {}", userId, otp);
        return userService.forgotPassword(userId, otp)
                .map(response -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("message", response);
                    return result;
                })
                .defaultIfEmpty(new HashMap<String, String>() {{
                    put("message", "No response from server");
                }});
    }

    @PostMapping(value = "/accountLocked", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> accountLocked(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        log.info("accountLocked api invoked with userId: {}", userId);
        return userService.accountLocked(userId)
                .map(response -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("message", response);
                    return result;
                })
                .defaultIfEmpty(new HashMap<String, String>() {{
                    put("message", "No response from server");
                }});
    }

    @PostMapping(value = "/setNewPassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> setNewPassword(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String newPassword = payload.get("newPassword");
        log.info("setNewPassword api invoked with userId: {}, newPassword: {}", userId, newPassword);
        return userService.setNewPassword(userId, newPassword)
                .map(response -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("message", response);
                    return result;
                })
                .defaultIfEmpty(new HashMap<String, String>() {{
                    put("message", "No response from server");
                }});
    }
}
