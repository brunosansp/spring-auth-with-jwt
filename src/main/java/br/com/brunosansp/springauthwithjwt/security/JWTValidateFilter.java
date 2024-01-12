package br.com.brunosansp.springauthwithjwt.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTValidateFilter extends BasicAuthenticationFilter {
    
    public static final String ATTRIBUTE_HEADER = "Authorization";
    public static final String ATTRIBUTE_PREFIX = "Bearer ";
    
    public JWTValidateFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    
    // a primeira coisa que precisamos é interceptar o header/cabeçalho
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        // pegar o atributo do header
        String attribute = request.getHeader(ATTRIBUTE_HEADER);
        
        // verificar se não é nulo
        if (attribute == null) {
            chain.doFilter(request, response);
            return;
        }
        
        // verificar se tem o prefixo informando o tipo do token
        if (!attribute.startsWith(ATTRIBUTE_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        
//        if (attribute == null && !attribute.startsWith(ATTRIBUTE_PREFIX)) {
//            chain.doFilter(request, response);
//            return;
//        }
        
        // pegar o prefixo do tipo de token
        String token = attribute.replace(ATTRIBUTE_PREFIX, "");
        
        // pegar UsernamePasswordAuthenticationToken - vai devolver os dados do usuário se válido
        UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(token);
        
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }
    
    // pegar UsernamePasswordAuthenticationToken - vai devolver os dados do usuário se válido
    private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
        String user = JWT.require(Algorithm.HMAC512(JWTAuthenticateFilter.TOKEN_PASSWORD))
            .build()
            .verify(token)
            .getSubject();
        
        if (user == null) {
            return null;
        }
        
        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    }
}
