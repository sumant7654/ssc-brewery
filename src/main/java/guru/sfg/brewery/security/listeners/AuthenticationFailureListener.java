package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.security.LoginFailure;
import guru.sfg.brewery.security.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener {

    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;
    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event){
        log.debug("User Logged in Failed");
        if(event.getSource() instanceof UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();
            LoginFailure.LoginFailureBuilder builder = LoginFailure.builder();
            if(token.getPrincipal() instanceof String){
                log.debug("User name logged in Failed  : "+token.getPrincipal());
                builder.userName((String) token.getPrincipal());
                userRepository.findByUserName((String) token.getPrincipal()).ifPresent(builder::user);
            }

            if(token.getDetails() instanceof WebAuthenticationDetails){
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                log.debug("Source Ip : "+details.getRemoteAddress());
                builder.sourceIp(details.getRemoteAddress());

            }
            LoginFailure failure = loginFailureRepository.save(builder.build());

            log.debug("Failure Event Saved : "+failure);

            if(failure.getUser() != null){
                lockUserAccount(failure.getUser());
            }

        }

    }

    private void lockUserAccount(User user) {
        List<LoginFailure> failures = loginFailureRepository.findAllByUserAndCreatedDateIsAfter(user, Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        if(failures.size() >= 3){
            log.debug("User Account locked : "+user);
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
    }
}
