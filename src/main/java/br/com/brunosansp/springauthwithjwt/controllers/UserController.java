package br.com.brunosansp.springauthwithjwt.controllers;

import br.com.brunosansp.springauthwithjwt.models.UserModel;
import br.com.brunosansp.springauthwithjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
public class UserController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    
    public UserController(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }
    
    @GetMapping("/listar-todos")
    public ResponseEntity<List<UserModel>> listAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
    }
    
    @PostMapping("/salvar")
    public ResponseEntity<UserModel> save(@RequestBody UserModel user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(user));
    }
    
    @GetMapping("/validar-senha")
    public ResponseEntity<Boolean> passwordValidate(@RequestParam String login,
                                                    @RequestParam String password) {
        Optional<UserModel> optUser = userRepository.findByLogin(login);
        if (optUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        
        UserModel user = optUser.get();
        boolean valid = encoder.matches(password, user.getPassword());
        HttpStatus status = valid ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(valid);
    }
    
}
