
package OBenitez.ProgramacionNCapasNoviembre25.DAO;

import OBenitez.ProgramacionNCapasNoviembre25.JPA.Municipio;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMunicipio extends JpaRepository<Municipio, Integer>{
    //public Result GetMunicipiosByEstado(int idEstado);
    List<Municipio> findByEstadoIdEstado(Integer idEstado);
}
