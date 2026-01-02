package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IDireccion;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DireccionService {
    @Autowired
    private IDireccion direccionRepository;
    
    public Result DeleteAddressById(int IdDireccion) {
        Result result = new Result();
        try {
            Optional<Direccion> direccionDB = direccionRepository.findById(IdDireccion);
            
            if (!direccionDB.isPresent()) {
                result.Correct = false;
                result.ErrorMessage = "Direccion no encontrado";
                return result;
            }
            
            direccionRepository.deleteById(IdDireccion);
            result.Correct = true;
            
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        
        return result;
    }
}
