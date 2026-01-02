
package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IUsuarioJPA;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    private IUsuarioJPA iUsuarioJPA;
    
    // ======================= GET ======================
    public Result GetAll(){
        Result result = new Result();
        
        try {
            List<Usuario> usuarios = iUsuarioJPA.findAll();
            if (usuarios.isEmpty() || usuarios == null) {
                result.Correct = false;
                result.ErrorMessage = "No se encontraron los usuarios";
                result.Objects = new ArrayList<>();
                return result;
            }
            result.Objects = new ArrayList<>(usuarios);
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
    
    public Result BusquedaAbierta(OBenitez.ProgramacionNCapasNoviembre25.ML.Usuario usuario){
        Result result = new Result();
        try {
            List<Usuario> usuarios = iUsuarioJPA.findByNombreLike(usuario.getNombre());
            if (usuarios.isEmpty() || usuarios == null) {
                result.Correct = false;
                result.ErrorMessage = "No se encontraron usuarios";
                result.Objects = new ArrayList<>();
                return result;
            }
            result.Objects = new ArrayList<>(usuarios);
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
    
    public Result GetById(Integer IdUsuario){
        Result result = new Result();
        
        try {
            Optional<Usuario> usuario = iUsuarioJPA.findById(IdUsuario);
            if (!usuario.isPresent() || usuario == null) {
                result.Correct = false;
                result.ErrorMessage = "No se encontro al usuario";
                return result;
            }
            result.Object = usuario.get();
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
    // ===================== ADD ====================
    // ===================== UPDATE ====================
    public Result UpdateStatus(Integer IdUsuario, Integer Status){
        Result result = new Result();

        try {
            Optional<Usuario> usuarioDB = iUsuarioJPA.findById(IdUsuario);
            if (!usuarioDB.isPresent()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontro al usuario";
                return result;
            }
            usuarioDB.get().setStatus(Status);
            iUsuarioJPA.save(usuarioDB.get());
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
    public Result UpdatePhoto(Integer IdUsuario, String Foto) {
        Result result = new Result();
        
        try {
            
            Optional<Usuario> usuarioDB = iUsuarioJPA.findById(IdUsuario);
            if (!usuarioDB.isPresent()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontro al usuario";
                return result;
            }
            
            usuarioDB.get().setImagen(Foto);
            iUsuarioJPA.save(usuarioDB.get());            
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        
        return result;
    }
    // ===================== DELETE ====================
    public Result DeleteById(Integer IdUsuario){
        Result result = new Result();

        try {
            Optional<Usuario> usuarioDB = iUsuarioJPA.findById(IdUsuario);
            if (!usuarioDB.isPresent()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontro al usuario";
                return result;
            }
            
            iUsuarioJPA.deleteById(IdUsuario);
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
    
}
