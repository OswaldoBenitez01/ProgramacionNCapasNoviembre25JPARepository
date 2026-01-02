
package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IRol;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Rol;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService {
    
    @Autowired
    private IRol irol;
    
    public Result GetAll(){
        Result result = new Result();
        
        try {
            List<Rol> roles = irol.findAll();
            if (roles.isEmpty() || roles == null) {
                result.Correct = false;
                result.ErrorMessage = "No se encontraron roles";
                result.Objects = new ArrayList<>();
                return result;
            }
            result.Objects = new ArrayList<>(roles);
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
}
