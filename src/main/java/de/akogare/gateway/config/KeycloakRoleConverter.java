package de.akogare.gateway.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {


  @Override
  public Collection<GrantedAuthority> convert(Jwt source) {

    Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");
    if (realmAccess == null || realmAccess.isEmpty()) {
      return List.of();
    }

    Collection<String> roles = (Collection<String>) realmAccess.get("roles");
    if (roles == null || roles.isEmpty()) {
      return List.of();
    }

    return roles.stream()
        .map(roleName -> "ROLE_" + roleName) /*Spring Security erwartet ROLE_ als Präfix*/
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
  }
}
