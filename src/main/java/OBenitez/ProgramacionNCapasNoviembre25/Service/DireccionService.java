package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IColonia;
import OBenitez.ProgramacionNCapasNoviembre25.DAO.IDireccion;
import OBenitez.ProgramacionNCapasNoviembre25.DAO.IUsuarioJPA;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Colonia;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DireccionService {
    @Autowired
    private IColonia coloniaRepository;
    @Autowired
    private IDireccion direccionRepository;
    @Autowired
    private IUsuarioJPA usuarioRepository;
    
    public Result AddAddress(Direccion direccion) {
        Result result = new Result();
        
        try {  
            if (direccion == null || direccion.usuario == null || direccion.usuario.getIdUsuario() == null) {
                result.Correct = false;
                result.ErrorMessage = "La direccion debe incluir un usuario valido";
                return result;
            }
            
            Optional<Usuario> usuarioDB = usuarioRepository.findById(direccion.usuario.getIdUsuario());
            if (usuarioDB == null) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                return result;
            }
            
            if (direccion.colonia != null && direccion.colonia.getIdColonia() != 0) {
                Optional<Colonia> coloniaDB = coloniaRepository.findById(direccion.colonia.getIdColonia());
                direccion.setColonia(coloniaDB.get());
            }
            
            direccion.setUsuario(usuarioDB.get());
            direccionRepository.save(direccion);
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
    
    public Result UpdateAddressById(Direccion direccion) {
        Result result = new Result();
        
        try {
            Optional<Direccion> direccionDB = direccionRepository.findById(direccion.getIdDireccion());
            
            if (direccionDB == null) {
                result.Correct = false;
                result.ErrorMessage = "Direccion no encontrada";
                return result;
            }
            
            direccionDB.get().setCalle(direccion.getCalle());
            direccionDB.get().setNumeroInterior(direccion.getNumeroInterior());
            direccionDB.get().setNumeroExterior(direccion.getNumeroExterior());
            if (direccion.colonia != null && direccion.colonia.getIdColonia() != 0) {
                Optional<Colonia> coloniaDB = coloniaRepository.findById(direccion.colonia.getIdColonia());
                direccionDB.get().setColonia(coloniaDB.get());
            }
            
            direccionRepository.save(direccionDB.get());
            
            result.Correct = true;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
    
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
