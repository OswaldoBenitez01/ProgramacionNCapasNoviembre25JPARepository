package OBenitez.ProgramacionNCapasNoviembre25.Configuration;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IUsuarioJPA;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import OBenitez.ProgramacionNCapasNoviembre25.Service.UserDetailJPAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        
        http
               
                .authorizeHttpRequests(config -> config
                .requestMatchers("/login").permitAll()
                .requestMatchers("/usuario/getEstadosByPais/**", 
                                "/usuario/getMunicipiosByEstado/**",
                                "/usuario/getColoniasByMunicipio/**")
                                .authenticated()
                .requestMatchers("/usuario",
                                "/usuario/busqueda",
                                "/usuario/form",
                                "/usuario/add",
                                "usuario/delete/**",
                                "/usuario/toggleStatus/**",
                                "/usuario/cargaMasiva",
                                "/usuario/cargaMasiva/**",
                                "/usuario/CargaMasiva",
                                "/usuario/CargaMasiva/procesar",
                                "/usuario/CargaMasiva/**")
                                .hasAnyRole("Director", "Administrador")
                                //.hasAnyAuthority("Director", "Administrador")
                .requestMatchers("/usuario/updatePhoto",
                                "/usuario/detail/**",
                                "/usuario/deletePhoto/**",
                                "/usuario/deleteAddress/**",
                                "/usuario/formEditable")
                                .hasAnyRole("Director", "Administrador", "Empleado", "Mantenimiento", "Gerente")
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
                .permitAll())
        .exceptionHandling(ex -> ex
                .accessDeniedPage("/login?error=forbidden"));
         return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    private boolean hasRole(Authentication auth, String role){
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_" + role));
    }
    
    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String email = authentication.getName();
            Usuario usuario = usuarioRepository.findByEmail(email);
            request.getSession().setAttribute("statusUsuario", usuario.getStatus());

            String targetUrl = "";
            if(hasRole(authentication, "Director") || hasRole(authentication, "Administrador")){
                targetUrl = "/usuario";
            } else if(hasRole(authentication, "Empleado") || hasRole(authentication, "Mantenimiento") || hasRole(authentication, "Gerente")){
                int id = usuario.getIdUsuario();
                targetUrl = "/usuario/detail/" + id;
            } else {
                targetUrl = "/login?error";
            }
            response.sendRedirect(targetUrl);
        };
    }
    
//    private AuthenticationSuccessHandler successHandler() {
//        return (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) -> {
//            String targetUrl = "";
//            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//            if(authorities.toString().contains("ROLE_Director")){
//                targetUrl = "/usuario";
//            } else if(authorities.toString().contains("ROLE_Empleado")){
//                String email = authentication.getName();
//                int id = usuarioRepository.findByEmail(email).getIdUsuario();
//                targetUrl = "/usuario/detail/" + id;
//            }
//            httpServletResponse.sendRedirect(targetUrl);
//        };
//    }
}
