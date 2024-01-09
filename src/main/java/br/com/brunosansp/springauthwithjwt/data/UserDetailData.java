package br.com.brunosansp.springauthwithjwt.data;

import br.com.brunosansp.springauthwithjwt.models.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class UserDetailData implements UserDetails {
    
    private final Optional<UserModel> optUser;
    
    public UserDetailData(Optional<UserModel> optUser) {
        this.optUser = optUser;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }
    
    @Override
    public String getPassword() {
        return optUser.orElse(new UserModel()).getPassword();
    }
    
    @Override
    public String getUsername() {
        return optUser.orElse(new UserModel()).getLogin();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}
