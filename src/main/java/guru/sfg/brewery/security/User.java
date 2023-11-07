package guru.sfg.brewery.security;

import guru.sfg.brewery.domain.Customer;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User implements UserDetails, CredentialsContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String userName;
    private String password;

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    private Set<Role> roles;


    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;

    @CreationTimestamp
    @Column(name = "created_date", updatable = true)
    private Timestamp createdDate;

    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private Timestamp lastModifiedDate;

   /* @Singular
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "user_authority", joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
    inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})*/
    @Transient
    private Set<Authority> authorities;

    public Set<GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(Role::getAuthorities)
                .flatMap(Set::stream)
                .map(authority -> new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toSet());

    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Builder.Default
    private Boolean accountNonExpired = true;
    @Builder.Default
    private Boolean accountNonLocked = true;
    @Builder.Default
    private Boolean credentialNonExpired = true;
    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean useGoogle2fa = false;

    private String google2FaSecret;

    @Transient
    private Boolean google2FaRequired = true;


    @Override
    public void eraseCredentials() {
        this.password = null;

    }
}
