package OBenitez.ProgramacionNCapasNoviembre25.Configuration;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IUsuarioJPA;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import OBenitez.ProgramacionNCapasNoviembre25.Service.UserDetailJPAService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {
    
    @Autowired
    private IUsuarioJPA usuarioRepository;

    private final UserDetailJPAService userDetailJPAService;
    
    public SpringSecurityConfiguration(UserDetailJPAService userDetailJPAService){
        this.userDetailJPAService = userDetailJPAService;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(config -> config
                .requestMatchers("/usuario/detail/**", 
                        "/usuario/*", 
                        "/usuario/getEstadosByPais/**", 
                        "/usuario/getMunicipiosByEstado/**",
                        "/usuario/getColoniasByMunicipio/**",
                        "/usuario/deleteAddress/**",
                        "/usuario/deletePhoto/**")
                        .permitAll()
                .requestMatchers("/**").hasAnyRole("Director", "Administrador(a)")
                .anyRequest().authenticated()
        ).formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler())
                .permitAll())
                .userDetailsService(userDetailJPAService)
        .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll());
        
         return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    private AuthenticationSuccessHandler successHandler() {
        return (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) -> {
            String targetUrl = "";
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if(authorities.toString().contains("ROLE_Director")){
                targetUrl = "/usuario";
            } else if(authorities.toString().contains("ROLE_Empleado")){
                String email = authentication.getName();
                int id = usuarioRepository.findByEmail(email).getIdUsuario();
                targetUrl = "/usuario/detail/" + id;
            }
            httpServletResponse.sendRedirect(targetUrl);
        };
    }

}
