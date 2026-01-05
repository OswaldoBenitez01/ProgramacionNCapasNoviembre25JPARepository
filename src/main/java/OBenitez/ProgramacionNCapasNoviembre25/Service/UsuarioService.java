
package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IColonia;
import OBenitez.ProgramacionNCapasNoviembre25.DAO.IUsuarioJPA;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Colonia;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion;
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
    private IUsuarioJPA usuarioRepository;
    @Autowired
    private IColonia coloniaRepository;
    
    
    // ======================= GET ======================
    public Result GetAll(){
        Result result = new Result();
        
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
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
            String nombre = (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) ? null : usuario.getNombre();
            String apellidoPaterno = (usuario.getApellidoPaterno() == null || usuario.getApellidoPaterno().trim().isEmpty()) ? null : usuario.getApellidoPaterno();
            String apellidoMaterno = (usuario.getApellidoMaterno() == null || usuario.getApellidoMaterno().trim().isEmpty()) ? null : usuario.getApellidoMaterno();
            Integer idRol = (usuario.getRol() == null || usuario.getRol().getIdRol() == null) ? null : usuario.getRol().getIdRol();
        
            List<Usuario> usuarios = usuarioRepository.busquedaAbierta(
                    nombre,
                    apellidoPaterno,
                    apellidoMaterno,
                    idRol
            );
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
            Optional<Usuario> usuario = usuarioRepository.findById(IdUsuario);
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
    public Result Add(Usuario usuario) {
        Result result = new Result();
        usuario.setIdUsuario(null); //Para qeu no choque con el id 
        try {
            if (usuario.getDirecciones() != null && !usuario.getDirecciones().isEmpty()) {
                for (Direccion direccion : usuario.getDirecciones()) {
                    direccion.setUsuario(usuario);
                    if (direccion.Colonia != null && direccion.Colonia.getIdColonia() != 0) {
                        Colonia coloniadb = coloniaRepository.findById(direccion.getColonia().getIdColonia()).orElse(null);
                        direccion.setColonia(coloniadb);
                    }
                }
            }
            usuarioRepository.save(usuario);
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
    
    // ===================== UPDATE ====================
    public Result UpdateStatus(Integer IdUsuario, Integer Status){
        Result result = new Result();

        try {
            Optional<Usuario> usuarioDB = usuarioRepository.findById(IdUsuario);
            if (!usuarioDB.isPresent()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontro al usuario";
                return result;
            }
            usuarioDB.get().setStatus(Status);
            usuarioRepository.save(usuarioDB.get());
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
            
            Optional<Usuario> usuarioDB = usuarioRepository.findById(IdUsuario);
            if (!usuarioDB.isPresent()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontro al usuario";
                return result;
            }
            
            usuarioDB.get().setImagen(Foto);
            usuarioRepository.save(usuarioDB.get());            
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
            Optional<Usuario> usuarioDB = usuarioRepository.findById(IdUsuario);
            if (!usuarioDB.isPresent()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontro al usuario";
                return result;
            }
            
            usuarioRepository.deleteById(IdUsuario);
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
    
}
