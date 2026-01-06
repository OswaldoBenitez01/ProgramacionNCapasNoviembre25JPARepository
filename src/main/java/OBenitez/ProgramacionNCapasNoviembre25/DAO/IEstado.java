
package OBenitez.ProgramacionNCapasNoviembre25.DAO;

import OBenitez.ProgramacionNCapasNoviembre25.JPA.Estado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEstado extends JpaRepository<Estado, Integer>{
    //public Result GetEstadosByPais(int IdPais);
    List<Estado> findByPaisIdPais(Integer idPais);
}
