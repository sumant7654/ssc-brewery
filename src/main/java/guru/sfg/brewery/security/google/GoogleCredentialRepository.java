package guru.sfg.brewery.security.google;


import com.warrenstrange.googleauth.ICredentialRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.security.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleCredentialRepository implements ICredentialRepository {

    private final UserRepository userRepository;
    @Override
    public String getSecretKey(String userName) {
        log.debug("Getting secret for : "+userName);
        User user = userRepository.findByUserName(userName).orElseThrow();
        return user.getGoogle2FaSecret();
    }

    @Override
    public void saveUserCredentials(String userName, String secretKey, int verificationCode, List<Integer> scratchCode) {
        log.debug("Setting secret for : "+userName);
        User user = userRepository.findByUserName(userName).orElseThrow();
        user.setGoogle2FaSecret(secretKey);
        user.setUseGoogle2fa(true);
        userRepository.save(user);
    }
}
