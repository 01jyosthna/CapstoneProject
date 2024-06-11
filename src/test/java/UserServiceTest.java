import com.banking.authenticator.model.BankingUser;
import com.banking.authenticator.repository.UserRepository;
import com.banking.authenticator.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private BankingUser bankingUser;

    @BeforeEach
    void setUp() {
        bankingUser = new BankingUser();
        bankingUser.setUserId("user123");
        bankingUser.setAccountNumber("1234567890");
        bankingUser.setOtp("123456");
        bankingUser.setPassword("password");
    }
   /* @BeforeEach
    void setUp() {
        bankingUser = new BankingUser(1L, "user123", "1234567890", "password", "123456");
    }*/

    @Test
    void testForgotUserId_ValidOTP() {
        when(userRepository.findByAccountNumber(bankingUser.getAccountNumber())).thenReturn(Mono.just(bankingUser));

        Mono<String> result = userService.forgotUserId(bankingUser.getAccountNumber(), bankingUser.getOtp());

        StepVerifier.create(result)
                .expectNext("Your User ID is: " + bankingUser.getUserId())
                .verifyComplete();
    }

    @Test
    void testForgotUserId_InvalidOTP() {
        when(userRepository.findByAccountNumber(bankingUser.getAccountNumber())).thenReturn(Mono.just(bankingUser));

        Mono<String> result = userService.forgotUserId(bankingUser.getAccountNumber(), "wrongOTP");

        StepVerifier.create(result)
                .expectNext("Invalid OTP. Please try again.")
                .verifyComplete();
    }

    @Test
    void testForgotPassword_ValidOTP() {
        when(userRepository.findByUserId(bankingUser.getUserId())).thenReturn(Mono.just(bankingUser));

        Mono<String> result = userService.forgotPassword(bankingUser.getUserId(), bankingUser.getOtp());

        StepVerifier.create(result)
                .expectNext("OTP verified. Please set a new password.")
                .verifyComplete();
    }

    @Test
    void testForgotPassword_InvalidOTP() {
        when(userRepository.findByUserId(bankingUser.getUserId())).thenReturn(Mono.just(bankingUser));

        Mono<String> result = userService.forgotPassword(bankingUser.getUserId(), "wrongOTP");

        StepVerifier.create(result)
                .expectNext("Invalid OTP. Please try again.")
                .verifyComplete();
    }

    @Test
    void testAccountLocked_UserFound() {
        when(userRepository.findByUserId(bankingUser.getUserId())).thenReturn(Mono.just(bankingUser));
        when(userRepository.save(any(BankingUser.class))).thenReturn(Mono.just(bankingUser));

        Mono<String> result = userService.accountLocked(bankingUser.getUserId());

        StepVerifier.create(result)
                .expectNext("Account locked. Please reset your password.")
                .verifyComplete();
    }

    @Test
    void testAccountLocked_UserNotFound() {
        when(userRepository.findByUserId("nonExistentUser")).thenReturn(Mono.empty());

        Mono<String> result = userService.accountLocked("nonExistentUser");

        StepVerifier.create(result)
                .expectNext("User not found")
                .verifyComplete();
    }

    @Test
    void testSetNewPassword() {
        String newPassword = "newPassword";
        when(userRepository.findByUserId(bankingUser.getUserId())).thenReturn(Mono.just(bankingUser));
        when(userRepository.save(any(BankingUser.class))).thenReturn(Mono.just(bankingUser));

        Mono<String> result = userService.setNewPassword(bankingUser.getUserId(), newPassword);

        StepVerifier.create(result)
                .expectNext("Password changed successfully.")
                .verifyComplete();
    }

    @Test
    void testRegisterUser() {
        List<BankingUser> bankingUserList = Arrays.asList(bankingUser, new BankingUser());
        when(userRepository.saveAll(bankingUserList)).thenReturn(Flux.fromIterable(bankingUserList));

        Flux<BankingUser> result = userService.registerUser(bankingUserList);

        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();
    }
}

