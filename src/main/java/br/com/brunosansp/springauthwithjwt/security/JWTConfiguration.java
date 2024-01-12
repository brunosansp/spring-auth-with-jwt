package br.com.brunosansp.springauthwithjwt.security;

import br.com.brunosansp.springauthwithjwt.service.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/***
 * Precisamos habilitar essa classe como um componente de segurança usando @EnableWebSecurity
 */
@EnableWebSecurity
public class JWTConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailServiceImpl userDetailService;
    private final PasswordEncoder passwordEncoder;
    
    public JWTConfiguration(UserDetailServiceImpl userDetailService, PasswordEncoder passwordEncoder) {
        this.userDetailService = userDetailService;
        this.passwordEncoder = passwordEncoder;
    }
    
    /***
     * Precisams informar ao Spring Security para utilizar o nosso serviço de usuário
     * e o password encode para validar a nossa senha
     *
     * Vamos sobrescrever o método configure que contém o AuthenticationManagerBuilder
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);
    }
    
    /***
     * Configurando como o Spring Security deve entender a nossa página
     *
     * Vamos sobrescrever o método configure com HttpSecurity
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // este método csrf() basicamente resolve ataques na aplicação, é importante habilitar em produção
        http.csrf().disable().authorizeRequests()
            /***
             * sempre que adicionamos o Spring Security como dependência no projeto, ele automáticamente cria
             * uma URL chamada /login do tipo POST que não pode ter restrições
             */
            .antMatchers(HttpMethod.POST, "/login").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(new JWTAuthenticateFilter(authenticationManager()))
            .addFilter(new JWTValidateFilter(authenticationManager()))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
    
    /***
     * Agora vamos adicionar uma configuração para gravar o CORS
     * que basicamente permite que outros domínios acessem a aplicação. Caso a aplicação
     * seja usada apenas internamente, o CORS pode ficar desabilitado(basta remover esta parte) para sites externos.
     *
     * Para que esta etapa seja um componente da aplicação é necessário usar a anotação @Bean
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return source;
    }
}
