package br.com.brunosansp.springauthwithjwt.security;

import br.com.brunosansp.springauthwithjwt.data.UserDetailData;
import br.com.brunosansp.springauthwithjwt.models.UserModel;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthenticateFilter extends UsernamePasswordAuthenticationFilter {
    
    public static final int TOKEN_EXPIRATION = 600_00;
    public static final String TOKEN_PASSWORD = "e537adad-8c9b-4eb6-a540-b9a26867cecf";
    
    private final AuthenticationManager authenticationManager;
    
    public JWTAuthenticateFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            UserModel user = new ObjectMapper()
                .readValue(request.getInputStream(), UserModel.class);
            
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getLogin(),
                user.getPassword(),
                new ArrayList<>()
            ));
        } catch (IOException e) {
            throw new RuntimeException("Falha na autenticação do usuário", e);
        }
    }
    
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        UserDetailData userDetailData = (UserDetailData) authResult.getPrincipal();
        
        // para esta etapa é necessário adicionar a dependência do auth0 no pom.xml
        String token = JWT.create()
            .withSubject(userDetailData.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
            .sign(Algorithm.HMAC512(TOKEN_PASSWORD));
        
        response.getWriter().write(token);
        response.getWriter().flush();
    }
}
