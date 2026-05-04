package ch.zhaw.karateaicoach.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_returnsNull_whenNoAuthentication() {
        assertNull(userService.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_returnsNull_whenAnonymous() {
        AnonymousAuthenticationToken anon = new AnonymousAuthenticationToken(
                "key", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        SecurityContextHolder.getContext().setAuthentication(anon);

        assertNull(userService.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_returnsJwtSubject_whenJwtPrincipal() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("auth0|user123");
        Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("auth0|user123", userService.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_returnsName_whenNonJwtPrincipal() {
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("testuser", userService.getCurrentUserId());
    }

    @Test
    void userHasRole_returnsFalse_whenNoAuthentication() {
        assertFalse(userService.userHasRole("admin"));
    }

    @Test
    void userHasRole_returnsFalse_whenAnonymous() {
        AnonymousAuthenticationToken anon = new AnonymousAuthenticationToken(
                "key", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        SecurityContextHolder.getContext().setAuthentication(anon);

        assertFalse(userService.userHasRole("admin"));
    }

    @Test
    void userHasRole_returnsTrue_whenJwtClaimMatches() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsStringList("user_roles")).thenReturn(List.of("admin", "user"));
        Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertTrue(userService.userHasRole("admin"));
    }

    @Test
    void userHasRole_returnsTrue_whenAuthorityMatches() {
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertTrue(userService.userHasRole("admin"));
    }

    @Test
    void userHasRole_returnsFalse_whenNeitherClaimNorAuthorityMatches() {
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertFalse(userService.userHasRole("admin"));
    }

    @Test
    void userHasRole_returnsFalse_whenJwtHasNoMatchingClaim_andNoMatchingAuthority() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsStringList("user_roles")).thenReturn(List.of("user"));
        Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertFalse(userService.userHasRole("admin"));
    }

    @Test
    void userHasRole_returnsTrue_whenRolePrefixedWithROLE_ADMIN() {
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertTrue(userService.userHasRole("ROLE_ADMIN"));
    }

    // getCurrentUserEmail

    @Test
    void getCurrentUserEmail_returnsNull_whenNoAuthentication() {
        assertNull(userService.getCurrentUserEmail());
    }

    @Test
    void getCurrentUserEmail_returnsNull_whenAnonymous() {
        AnonymousAuthenticationToken anon = new AnonymousAuthenticationToken(
                "key", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        SecurityContextHolder.getContext().setAuthentication(anon);

        assertNull(userService.getCurrentUserEmail());
    }

    @Test
    void getCurrentUserEmail_returnsEmailClaim_whenJwtPrincipal() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("email")).thenReturn("user@test.com");
        Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("user@test.com", userService.getCurrentUserEmail());
    }

    @Test
    void getCurrentUserEmail_returnsNull_whenNonJwtPrincipal() {
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertNull(userService.getCurrentUserEmail());
    }

    // getCurrentUserName

    @Test
    void getCurrentUserName_returnsNull_whenNoAuthentication() {
        assertNull(userService.getCurrentUserName());
    }

    @Test
    void getCurrentUserName_returnsFullName_whenJwtHasBothNames() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("given_name")).thenReturn("Max");
        when(jwt.getClaimAsString("family_name")).thenReturn("Muster");
        Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("Max Muster", userService.getCurrentUserName());
    }

    @Test
    void getCurrentUserName_returnsGivenNameOnly_whenFamilyNameNull() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("given_name")).thenReturn("Max");
        when(jwt.getClaimAsString("family_name")).thenReturn(null);
        Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("Max", userService.getCurrentUserName());
    }

    @Test
    void getCurrentUserName_returnsName_whenGivenNameNullAndNameNotEmail() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("given_name")).thenReturn(null);
        when(jwt.getClaimAsString("family_name")).thenReturn(null);
        when(jwt.getClaimAsString("name")).thenReturn("Max Muster");
        Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("Max Muster", userService.getCurrentUserName());
    }

    @Test
    void getCurrentUserName_returnsEmail_whenNameLooksLikeEmail() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("given_name")).thenReturn(null);
        when(jwt.getClaimAsString("family_name")).thenReturn(null);
        when(jwt.getClaimAsString("name")).thenReturn("user@test.com");
        when(jwt.getClaimAsString("email")).thenReturn("user@test.com");
        Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("user@test.com", userService.getCurrentUserName());
    }

    @Test
    void getCurrentUserName_returnsAuthName_whenNonJwtPrincipal() {
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("testuser", userService.getCurrentUserName());
    }
}
