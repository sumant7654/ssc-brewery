package guru.sfg.brewery.security;

import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserUnLockService {
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 1_000 * 60 * 60)
    public void unlockAccount(){
        log.debug("Running Unlock Account");

        List<User> lockedUsers = userRepository.findAllByAccountNonLockedAndLastModifiedDateIsBefore(false,
                Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)));

        if(!lockedUsers.isEmpty()){
            log.debug("Locked Accounts Found");

            for (User user : lockedUsers) {
                user.setAccountNonLocked(true);
                user.setLastModifiedDate(
                        Timestamp.valueOf(LocalDateTime.now())
                );
            }
            userRepository.saveAll(lockedUsers);

        }
    }


}
