
package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IUsuarioJPA;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SeguridadControllerService {
    @Autowired
    private IUsuarioJPA usuarioRepository;
    
    public boolean tieneRol(Authentication auth, String rol) {
        String rolBuscado = "ROLE_" + rol;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (rolBuscado.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean esPropioOAdmin(int idUsuarioActivo){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (tieneRol(auth, "Director") || tieneRol(auth, "Administrador")) {
            return true;
        }
        
        String email = auth.getName();
        Usuario usuarioDB = usuarioRepository.findByEmail(email);
        return usuarioDB != null && usuarioDB.getIdUsuario() == idUsuarioActivo;
    }
}
