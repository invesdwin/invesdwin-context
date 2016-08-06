package de.invesdwin.context.security.kerberos.internal;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.invesdwin.context.security.kerberos.KerberosProperties;

@NotThreadSafe
public class RoleAddingKerberosUserDetailsService implements UserDetailsService {

    private static final String DUMMY_PASSWORD = "_DUMMY_";

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return new User(
                username,
                DUMMY_PASSWORD,
                AuthorityUtils.createAuthorityList(KerberosProperties.ROLE_KERBEROS_AUTHENTICATED));
    }

}