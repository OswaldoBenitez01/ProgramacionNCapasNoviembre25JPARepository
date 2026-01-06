
package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IColonia;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Colonia;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColoniaService {
    @Autowired
    private IColonia coloniaRepository;
    
    public Result GetColoniasByMunicipio(int idMunicipio){
        Result result = new Result();
        
        try {
            List<Colonia> colonias = coloniaRepository.findByMunicipioIdMunicipio(idMunicipio);
            
            if (colonias == null || colonias.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontraron colonias para este municipio";
                return result;
            }
            
            result.Objects = new ArrayList<>(colonias);
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
}
