
package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IEstado;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Estado;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstadoService {
    @Autowired
    private IEstado estadoRepository;
    
    public Result GetEstadosByPais(int idPais){
        Result result = new Result();
    
        try {
            List<Estado> estados = estadoRepository.findByPaisIdPais(idPais);
            
            if (estados == null || estados.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontraron estados por ese pais";
                return result;
            }
            
            result.Objects = new ArrayList<>(estados);
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
}
