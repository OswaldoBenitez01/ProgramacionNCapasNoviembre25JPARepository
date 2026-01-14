
package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IUsuarioJPA;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailJPAService implements UserDetailsService{
    
    private final IUsuarioJPA usuarioRepository;
    
    public UserDetailJPAService(IUsuarioJPA usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username);
        
        return User.withUsername(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(usuario.rol.getNombre())
                .build();
    }
    
}
