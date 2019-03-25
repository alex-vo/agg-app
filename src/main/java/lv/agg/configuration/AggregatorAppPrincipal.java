package lv.agg.configuration;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AggregatorAppPrincipal extends User {

    private Long id;

    public AggregatorAppPrincipal(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities){
        this(username, password, authorities);
        this.id = id;
    }

    public AggregatorAppPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public AggregatorAppPrincipal(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public Long getId() {
        return id;
    }
}
