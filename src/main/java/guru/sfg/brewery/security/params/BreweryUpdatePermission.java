package guru.sfg.brewery.security.params;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('brewery.update')")
public @interface BreweryUpdatePermission {
}
