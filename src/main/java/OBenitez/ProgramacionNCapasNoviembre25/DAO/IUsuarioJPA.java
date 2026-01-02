
package OBenitez.ProgramacionNCapasNoviembre25.DAO;

import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioJPA extends JpaRepository<Usuario, Integer>{
    List<Usuario> findByNombreLike(String nombre);
}
