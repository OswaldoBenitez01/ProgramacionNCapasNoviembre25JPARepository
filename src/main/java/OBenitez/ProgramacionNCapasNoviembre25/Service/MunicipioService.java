
package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IMunicipio;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Municipio;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MunicipioService {
    @Autowired
    private IMunicipio municipioRepository;
    
    public Result GetMunicipiosByEstado(int idEstado){
        Result result = new Result();
        
        try {
            List<Municipio> municipios = municipioRepository.findByEstadoIdEstado(idEstado);
            
            if (municipios == null || municipios.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontraron municipios para este estado";
                return result;
            }
            
            result.Objects = new ArrayList<>(municipios);
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
}
