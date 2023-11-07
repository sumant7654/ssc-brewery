package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordEncoderTest {
    static final String PASSWORD = "password";

    @Test
    public void testBCrypt(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode(PASSWORD));
        System.out.println(passwordEncoder.encode(PASSWORD));
        System.out.println(passwordEncoder.encode("sumantakumar"));
        assertTrue(passwordEncoder.matches(PASSWORD, passwordEncoder.encode(PASSWORD)));

    }

    @Test
    public void testSHA256(){
        PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
        System.out.println(passwordEncoder.encode(PASSWORD));
        System.out.println(passwordEncoder.encode(PASSWORD));
        assertTrue(passwordEncoder.matches(PASSWORD, passwordEncoder.encode(PASSWORD)));
    }

    @Test
    public void testLDAP(){
        PasswordEncoder passwordEncoder = new LdapShaPasswordEncoder();
        System.out.println(passwordEncoder.encode(PASSWORD));
        System.out.println(passwordEncoder.encode("tiger"));
        assertTrue(passwordEncoder.matches(PASSWORD, passwordEncoder.encode(PASSWORD)));
    }

    @Test
    public void testNoOp(){
        PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
        System.out.println(passwordEncoder.encode(PASSWORD));
    }

    @Test
    public void hashExample(){
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));

        String salted = PASSWORD+"ThisIsMySaltValue";
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes()));
        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes()));

    }
}
