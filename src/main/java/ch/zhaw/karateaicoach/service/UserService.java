package ch.zhaw.karateaicoach.service;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Service
public class UserService {

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return authentication.getName();
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }
        return null;
    }

    public String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            String given = jwt.getClaimAsString("given_name");
            String family = jwt.getClaimAsString("family_name");
            if (given != null && family != null) return given + " " + family;
            if (given != null) return given;
            String name = jwt.getClaimAsString("name");
            if (name != null && !name.contains("@")) return name;
            return jwt.getClaimAsString("email");
        }
        return authentication.getName();
    }

    public boolean userHasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        String normalizedRole = role.startsWith("ROLE_")
                ? role.toUpperCase()
                : "ROLE_" + role.toUpperCase();

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            List<String> userRoles = jwt.getClaimAsStringList("user_roles");

            if (userRoles != null && userRoles.stream().anyMatch(userRole -> userRole != null
                    && (userRole.equalsIgnoreCase(role) || ("ROLE_" + userRole).equalsIgnoreCase(normalizedRole)))) {
                return true;
            }
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equalsIgnoreCase(role)
                        || authority.equalsIgnoreCase(normalizedRole));
    }
}
