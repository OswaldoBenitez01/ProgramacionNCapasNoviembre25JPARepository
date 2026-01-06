
package OBenitez.ProgramacionNCapasNoviembre25.DAO;

import OBenitez.ProgramacionNCapasNoviembre25.JPA.Colonia;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IColonia extends JpaRepository<Colonia, Integer>{
    //public Result GetColoniasByMunicipio(int idMunicipio);
    List<Colonia> findByMunicipioIdMunicipio(Integer idMunicipio);
}
