package br.com.brunosansp.springauthwithjwt.service;

import br.com.brunosansp.springauthwithjwt.data.UserDetailData;
import br.com.brunosansp.springauthwithjwt.models.UserModel;
import br.com.brunosansp.springauthwithjwt.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailServiceImpl implements UserDetailsService {
    
    private final UserRepository repository;
    
    public UserDetailServiceImpl(UserRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> optUser = repository.findByLogin(username);
        if (optUser.isEmpty()) {
            throw new UsernameNotFoundException("Usuário [" + username + "] não encontrado.");
        }
        return new UserDetailData(optUser);
    }
    
}
