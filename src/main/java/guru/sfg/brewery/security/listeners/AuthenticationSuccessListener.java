package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.repositories.security.LoginSuccessRepository;
import guru.sfg.brewery.security.LoginSuccess;
import guru.sfg.brewery.security.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationSuccessListener {

    private final LoginSuccessRepository loginSuccessRepository;
    @EventListener
    public void listen(AuthenticationSuccessEvent event){
        log.debug("User Logged in Okay");
        if(event.getSource() instanceof UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();
            LoginSuccess.LoginSuccessBuilder builder = LoginSuccess.builder();

            if(token.getPrincipal() instanceof User){
                User user = (User) token.getPrincipal();
                builder.user(user);
                log.debug("User name logged in  : "+user.getUsername());
            }

            if(token.getDetails() instanceof WebAuthenticationDetails){
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                builder.sourceIp(details.getRemoteAddress());
                log.debug("Source Ip : "+details.getRemoteAddress());
            }
            LoginSuccess loginSuccess = loginSuccessRepository.save(builder.build());

            log.debug("Login success data saved with id :"+loginSuccess.getId());
        }



    }
}
