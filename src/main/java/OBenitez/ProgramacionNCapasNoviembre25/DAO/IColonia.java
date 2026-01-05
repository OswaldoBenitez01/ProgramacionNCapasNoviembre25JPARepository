
package OBenitez.ProgramacionNCapasNoviembre25.DAO;

import OBenitez.ProgramacionNCapasNoviembre25.JPA.Colonia;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IColonia extends JpaRepository<Colonia, Integer>{
    //public Result GetColoniasByMunicipio(int idMunicipio);
}
