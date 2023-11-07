package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.security.Authority;
import guru.sfg.brewery.security.Role;
import guru.sfg.brewery.security.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;
    private void loadSecurityData() {

        // beer authorities
        Authority createBeer = authorityRepository.save(Authority.builder().permission("beer.create").build());
        Authority readBeer = authorityRepository.save(Authority.builder().permission("beer.read").build());
        Authority updateBear = authorityRepository.save(Authority.builder().permission("beer.update").build());
        Authority deleteBeer = authorityRepository.save(Authority.builder().permission("beer.delete").build());

        // customer authorities
        Authority createCustomer = authorityRepository.save(Authority.builder().permission("customer.create").build());
        Authority readCustomer = authorityRepository.save(Authority.builder().permission("customer.read").build());
        Authority updateCustomer = authorityRepository.save(Authority.builder().permission("customer.update").build());
        Authority deleteCustomer = authorityRepository.save(Authority.builder().permission("customer.delete").build());

        // brewery authorities
        Authority createBrewery = authorityRepository.save(Authority.builder().permission("brewery.create").build());
        Authority readBrewery = authorityRepository.save(Authority.builder().permission("brewery.read").build());
        Authority updateBrewery = authorityRepository.save(Authority.builder().permission("brewery.update").build());
        Authority deleteBrewery = authorityRepository.save(Authority.builder().permission("brewery.delete").build());

        // beer order authorities with admin role
        Authority createOrder = authorityRepository.save(Authority.builder().permission("order.create").build());
        Authority readOrder = authorityRepository.save(Authority.builder().permission("order.read").build());
        Authority updateOrder = authorityRepository.save(Authority.builder().permission("order.update").build());
        Authority deleteOrder = authorityRepository.save(Authority.builder().permission("order.delete").build());

        // beer order authorities with customer role
        Authority createOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.create").build());
        Authority readOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.read").build());
        Authority updateOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.update").build());
        Authority deleteOrderCustomer = authorityRepository.save(Authority.builder().permission("customer.order.delete").build());

        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role customerRole = roleRepository.save(Role.builder().name("CUSTOMER").build());
        Role userRole = roleRepository.save(Role.builder().name("USER").build());

        adminRole.setAuthorities(new HashSet<>(Set.of(createBeer, updateBear, readBeer, deleteBeer,
                createCustomer, readCustomer, updateCustomer, deleteCustomer,
                createBrewery, readBrewery, updateBrewery, deleteBrewery,
                createOrder, readOrder, updateOrder, deleteOrder)));

        customerRole.setAuthorities(new HashSet<>(Set.of(readBeer, readCustomer, readBrewery,
                createOrderCustomer, readOrderCustomer, updateOrderCustomer, deleteOrderCustomer)));

        userRole.setAuthorities(Set.of(readBeer));

        roleRepository.saveAll(new HashSet<>(Arrays.asList(adminRole, customerRole, userRole)));


/*
        Authority admin = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
        Authority user = authorityRepository.save(Authority.builder().role("ROLE_USER").build());
        Authority customer = authorityRepository.save(Authority.builder().role("ROLE_CUSTOMER").build());
*/

        userRepository.save(User.builder()
                .userName("sumantakumar")
                .password(passwordEncoder.encode("sumantakumar"))
                .role(adminRole)
                .build());

        userRepository.save(User.builder()
                .userName("user")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .build());

        userRepository.save(User.builder()
                .userName("scott")
                .password(passwordEncoder.encode("tiger"))
                .role(customerRole)
                .build());


        log.debug("User loaded:"+userRepository.count());

        //user.getAuthorities().forEach(authority -> System.out.println(authority.getPermission()));
    }

    @Override
    public void run(String... args) throws Exception {
        if(authorityRepository.count() == 0){
            loadSecurityData();
        }

    }


}
