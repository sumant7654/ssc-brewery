package guru.sfg.brewery.web.controllers;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.security.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    private final GoogleAuthenticator googleAuthenticator;

    @GetMapping("/register2Fa")
    public String register2Fa(Model model){

        User user = getUser();
        String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL("DEV", user.getUsername(),
                googleAuthenticator.createCredentials(user.getUsername()));

        log.debug("Google QR URL: "+ url);
        model.addAttribute("googleurl", url);

        return "user/register2fa";
    }

    @PostMapping("/register2Fa")
    public String confirm2Fa(@RequestParam Integer verifyCode){

        User user = getUser();
        log.debug("Entered Code is: "+verifyCode);
        if(googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)){
            User savedUser = userRepository.findById(user.getId()).orElseThrow();
            savedUser.setUseGoogle2fa(true);
            userRepository.save(savedUser);
            return "index";
        }else{
            // Bad Code
            return "user/register2fa";
        }

    }

    @GetMapping("verify2Fa")
    public String verify2Fa(){
        return "user/verify2fa";
    }

    @PostMapping("verify2Fa")
    public String verifyPostOf2Fa(@RequestParam Integer verifyCode){
        User user = getUser();
        if(googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)){
            ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setGoogle2FaRequired(false);
            return "/index";
        }else {
            return "user/verify2fa";
        }
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
