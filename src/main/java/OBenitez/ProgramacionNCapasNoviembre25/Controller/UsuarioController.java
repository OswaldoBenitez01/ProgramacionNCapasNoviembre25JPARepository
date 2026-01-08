
package OBenitez.ProgramacionNCapasNoviembre25.Controller;

import OBenitez.ProgramacionNCapasNoviembre25.ML.Colonia;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Direccion;
import OBenitez.ProgramacionNCapasNoviembre25.ML.ErrorCarga;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Rol;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Usuario;
import OBenitez.ProgramacionNCapasNoviembre25.Service.ColoniaService;
import OBenitez.ProgramacionNCapasNoviembre25.Service.DireccionService;
import OBenitez.ProgramacionNCapasNoviembre25.Service.EstadoService;
import OBenitez.ProgramacionNCapasNoviembre25.Service.MunicipioService;
import OBenitez.ProgramacionNCapasNoviembre25.Service.PaisService;
import OBenitez.ProgramacionNCapasNoviembre25.Service.RolService;
import OBenitez.ProgramacionNCapasNoviembre25.Service.UsuarioService;
import OBenitez.ProgramacionNCapasNoviembre25.Service.ValidationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("usuario")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RolService rolService;
    @Autowired
    private PaisService paisService;
    @Autowired
    private EstadoService estadoService;
    @Autowired
    private MunicipioService municipioService;
    @Autowired
    private ColoniaService coloniaService;
    @Autowired
    private DireccionService direccionService;
    @Autowired
    private ValidationService validationService;
    
    @GetMapping
    public String GetAll(Model model){

        Result result = usuarioService.GetAll();
        model.addAttribute("Usuarios", result.Objects);
        model.addAttribute("usuarioBusqueda", new Usuario());
        
        //Roles
        Result resultRoles = rolService.GetAll();
        model.addAttribute("Roles", resultRoles.Objects);
        return "Index";
    }
    
    @PostMapping("busqueda")
    public String Busqueda(@ModelAttribute("usuario") Usuario usuario, Model model){
        Result result = usuarioService.BusquedaAbierta(usuario);        
        
        if (result.Objects == null || result.Objects.isEmpty()) {
            model.addAttribute("mensajeBusqueda", true);
        }
        
        model.addAttribute("Usuarios", result.Objects);
        model.addAttribute("usuarioBusqueda", usuario);
        
        Result resultRoles = rolService.GetAll();
        model.addAttribute("Roles", resultRoles.Objects);
        
        return "Index";
    }
    
    @GetMapping("form")
    public String Form(Model model){
        Result resultRol = rolService.GetAll();
        model.addAttribute("Roles", resultRol.Objects);
        Result resultPais = paisService.GetAll();
        model.addAttribute("Paises", resultPais.Objects);
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(0);
        model.addAttribute("Usuario", usuario);
     
        return "UsuarioForm";
    }
    
    @PostMapping("add")
    public String Add(@Valid @ModelAttribute("Usuario") Usuario usuario, BindingResult bindingResult, @RequestParam("imagenUsuario") MultipartFile imagenUsuario, Model model, RedirectAttributes redirectAttributes) throws IOException{
//        for (ObjectError errors : bindingResult.getAllErrors()) {
//            System.out.println("Error: " + errors.getDefaultMessage() + " en " + errors.getCode());
//            System.out.println(errors.getCodes());
//            
//            System.out.println("==========================================================================");
//        }
//        System.out.println(bindingResult.getAllErrors());
//        System.out.println(bindingResult.getFieldErrors());
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("Usuario", usuario);
            return "UsuarioForm"; 
        } else {
            // AGREGAR USUARIO FULL INFO
            if (imagenUsuario.isEmpty()) {  
                usuario.setImagen(null);
            } else {
                String encodedString = Base64.getEncoder().encodeToString(imagenUsuario.getBytes());
                usuario.setImagen(encodedString);
            }
            usuario.setStatus(1);

            ModelMapper modelMapper = new ModelMapper();
            OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario usuarioJPA = modelMapper.map(usuario, OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario.class);
            Result result = usuarioService.Add(usuarioJPA);

            if(result.Correct){
                result.Object = "El usuario se agrego correctamente";
            } else{
                result.Object = "No fue posible agregar al usuario :c";
            }
            redirectAttributes.addFlashAttribute("resultAddUserFull", result);
            return "redirect:/usuario";
        }

    }
    
    @GetMapping("detail/{IdUsuario}")
    public String Detail(@PathVariable("IdUsuario") int IdUsuario, Model model){
    
        Result result = usuarioService.GetById(IdUsuario);
        model.addAttribute("Usuario", result.Object);
        
        Result resultPais = paisService.GetAll();
        model.addAttribute("Paises", resultPais.Objects);
        
        Result resultRoles = rolService.GetAll();
        model.addAttribute("Roles", resultRoles.Objects);
        
        return "UsuarioDetail";
    }
    
    @GetMapping("delete/{IdUsuario}")
    public String Delete(@PathVariable("IdUsuario") int IdUsuario, RedirectAttributes redirectAttributes){
    
        Result result = usuarioService.DeleteById(IdUsuario);
        
        if(result.Correct){
            result.Object = "El usuario con ID " + IdUsuario + " fue eliminado";
        } else{
            result.Object = "No fue posible eliminar al usuario :c";
        }
        
        redirectAttributes.addFlashAttribute("resultDelete", result);
        return "redirect:/usuario";
    }
    
    @GetMapping("toogleStatus/{IdUsuario}/{Status}")
    @ResponseBody
    public Result ToggleStatus(@PathVariable("IdUsuario") int IdUsuario, @PathVariable("Status") int Status,RedirectAttributes redirectAttributes){
    
        Result result = usuarioService.UpdateStatus(IdUsuario, Status);
        
        redirectAttributes.addFlashAttribute("resultDeleteSoft", result);
        return result;
    }
    
    @PostMapping("/updatePhoto")
    public String updatePhoto(@ModelAttribute Usuario usuario,
                              @RequestParam("imagenUsuario") MultipartFile imagenUsuario,
                              RedirectAttributes redirectAttributes) {

        Result result = new Result();

        try {
            if (imagenUsuario.isEmpty()) {
                result.Correct = false;
                result.Object = "No se seleccionó ninguna imagen";
            } else {
                String encodedString = Base64.getEncoder().encodeToString(imagenUsuario.getBytes());
                result = usuarioService.UpdatePhoto(usuario.getIdUsuario(), encodedString);
                result.Object = result.Correct ? "Se actualizó correctamente la foto" : "No se pudo actualizar la foto :c";
            }
        } catch (IOException ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.Object = "No se pudo actualizar la foto :c";
            result.ex = ex;
        }

        redirectAttributes.addFlashAttribute("resultUpdatePhoto", result);
        return "redirect:/usuario/detail/" + usuario.getIdUsuario();
    }
    
    @PostMapping("/deletePhoto/{IdUsuario}")
    public String deletePhoto(@PathVariable Integer IdUsuario, RedirectAttributes redirectAttributes) {

        Result result = new Result();

        try {
            result = usuarioService.UpdatePhoto(IdUsuario, null);
            result.Object = result.Correct ? "Se elimino correctamente la foto" : "No se pudo eliminar la foto :c";
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.Object = "No se pudo eliminar la foto :c";
            result.ex = ex;
        }

        redirectAttributes.addFlashAttribute("resultUpdatePhoto", result);
        return "redirect:/usuario/detail/" + IdUsuario;
    }
    
    @GetMapping("deleteAddress/{IdDireccion}/{IdUsuario}")
    public String DeleteAddress(@PathVariable("IdDireccion") int IdDireccion, @PathVariable("IdUsuario") int IdUsuario, RedirectAttributes redirectAttributes){

        Result result = direccionService.DeleteAddressById(IdDireccion);
        
        if(result.Correct){
            result.Object = "La direccion fue eliminada";
        } else{
            result.Object = "No fue posible eliminar la direccion :c";
        }
        
        redirectAttributes.addFlashAttribute("resultDeleteAddress", result);
        return "redirect:/usuario/detail/"+IdUsuario;
    }
     
    @GetMapping("getEstadosByPais/{idPais}")
    @ResponseBody // retorna un dato estructurado
    public Result EstadosByPais(@PathVariable("idPais") int idPais){
        Result result = estadoService.GetEstadosByPais(idPais);
        return result;
    }
    @GetMapping("getMunicipiosByEstado/{idEstado}")
    @ResponseBody // retorna un dato estructurado
    public Result MunicipiosByEstado(@PathVariable("idEstado") int idEstado){
        Result result = municipioService.GetMunicipiosByEstado(idEstado);
        return result;
    }
    @GetMapping("getColoniasByMunicipio/{idMunicipio}")
    @ResponseBody // retorna un dato estructurado
    public Result ColoniasByMunicipio(@PathVariable("idMunicipio") int idMunicipio){
        Result result = coloniaService.GetColoniasByMunicipio(idMunicipio);
        return result;
    }
       
    @PostMapping("formEditable")
    public String Form(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes){
    
        if(usuario.Direcciones.get(0).getIdDireccion() == -1){
            
            ModelMapper modelMapper = new ModelMapper();
            OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario usuarioJPA = modelMapper.map(usuario, OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario.class);
            Result result = usuarioService.UpdateUser(usuarioJPA);
            if(result.Correct){
                result.Object = "El usuario se actualizo correctamente";
            } else{
                result.Object = "No fue posible actualizar al usuario :c";
            }
            redirectAttributes.addFlashAttribute("resultEditUserBasic", result);
            return "redirect:/usuario/detail/"+usuario.getIdUsuario();
            
        }else if(usuario.Direcciones.get(0).getIdDireccion() == 0){
            //AGREGA UNA DIRECCION NUEVA
            ModelMapper modelMapper = new ModelMapper();
            OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion direccionJPA = modelMapper.map(usuario.Direcciones.get(0), OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion.class);
            direccionJPA.usuario = new OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario();
            direccionJPA.usuario.setIdUsuario(usuario.getIdUsuario());
            Result result = direccionService.AddAddress(direccionJPA);
            if(result.Correct){
                result.Object = "La direccion se agrego correctamente";
            } else{
                result.Object = "No fue posible agregar la direccion :c";
            }
            redirectAttributes.addFlashAttribute("resultAddAddress", result);
            return "redirect:/usuario/detail/"+usuario.getIdUsuario();
        }else{
            //ACTUALIZA UNA DIRECCION
            ModelMapper modelMapper = new ModelMapper();
            OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion direccionJPA = modelMapper.map(usuario.Direcciones.get(0), OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion.class);
            Result result = direccionService.UpdateAddressById(direccionJPA);            
            if(result.Correct){
                result.Object = "La direccion se actualizo correctamente";
            } else{
                result.Object = "No fue posible actualizar la direccion :c";
            }
            redirectAttributes.addFlashAttribute("resultEditAddress", result);
            return "redirect:/usuario/detail/"+usuario.getIdUsuario();
        }
    }
    
    @GetMapping("cargaMasiva")
    public String CargaMsiva(){
        return "CargaMasiva";
    }
    
    @PostMapping("CargaMasiva")
    public String CargaMasiva(@ModelAttribute MultipartFile archivo, Model model, HttpSession sesion) throws IOException {
    
        String extension = archivo.getOriginalFilename().split("\\.")[1];
        
        String path = System.getProperty("user.dir");
        String pathArchivo = "src/main/resources/archivos";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        String rutaAbsoluta = path + "/" + pathArchivo + "/" + fecha + archivo.getOriginalFilename();
        
        archivo.transferTo(new File(rutaAbsoluta));
        
        List<Usuario> usuarios = new ArrayList<>();
        
        if (extension.equals("txt")) {
            usuarios = LecturaArchivo(new File(rutaAbsoluta));
        } else {
            usuarios = LecturaArchivoExcel(new File(rutaAbsoluta));    
        }
        
        List<ErrorCarga> errores = ValidarDatos(usuarios);
        
        if (errores != null && !errores.isEmpty()) {
            model.addAttribute("errores", errores);
            model.addAttribute("tieneErrores", true);
        } else {
            model.addAttribute("mensajeExito", "Carga exitosa. Se cargaron " + usuarios.size() + " usuario(s) correctamente");
            model.addAttribute("tieneErrores", false);
            
            sesion.setAttribute("archivoCargaMasiva", rutaAbsoluta);
        }
        
        return "CargaMasiva";
    }

    private List<Usuario> LecturaArchivo(File archivo) {
        
        List<Usuario> usuarios = new ArrayList<>();
        
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(archivo))){
            
            bufferedReader.readLine();
            String line;
            
            while ((line = bufferedReader.readLine()) != null) {                
                
                String[] datos = line.split("\\|");
                
                Usuario usuario = new Usuario();
                usuario.setUsername(datos[0]);
                usuario.setNombre(datos[1]);
                usuario.setApellidoPaterno(datos[2]);
                usuario.setApellidoMaterno(datos[3]);
                usuario.setEmail(datos[4]);
                usuario.setPassword(datos[5]);
                usuario.setFechaNacimiento(java.sql.Date.valueOf(datos[6]));
                usuario.setSexo(datos[7]);
                usuario.setTelefono(datos[8]);
                usuario.setCelular(datos[9]);
                usuario.setCurp(datos[10]);
                
                //Direccion
                usuario.Rol = new Rol();
                usuario.Rol.setIdRol(Integer.parseInt(datos[11]));
                
                //DIRECCION
                usuario.Direcciones = new ArrayList<>();
                Direccion Direccion = new Direccion();
                Direccion.setCalle(datos[12]);
                Direccion.setNumeroExterior(datos[13]);
                Direccion.setNumeroInterior(datos[14]);
                usuario.Direcciones.add(Direccion);
                
                Direccion.Colonia = new Colonia();
                Direccion.Colonia.setIdColonia(Integer.parseInt(datos[15]));
                
                usuarios.add(usuario);
            }
        }
        catch(Exception ex){
            usuarios = null;
        }
        
        return usuarios;
    }

    private List<Usuario> LecturaArchivoExcel(File archivo) {
        
        List<Usuario> usuarios = new ArrayList<>();
        
         try (XSSFWorkbook workbook = new XSSFWorkbook(archivo)) {
             
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                
                Usuario usuario = new Usuario();
                Cell cell0 = row.getCell(0);
                if (cell0 != null) {
                    usuario.setUsername(row.getCell(0).toString());
                } else {
                    continue;
                }
                
                usuario.setNombre(row.getCell(1).toString());
                usuario.setApellidoPaterno(row.getCell(2).toString());
                usuario.setApellidoMaterno(row.getCell(3).toString());
                usuario.setEmail(row.getCell(4).toString());
                usuario.setPassword(row.getCell(5).toString());
                
                java.util.Date utilDate = row.getCell(6).getDateCellValue();
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                usuario.setFechaNacimiento(sqlDate);
                
                usuario.setSexo(row.getCell(7).toString());
                usuario.setCelular(row.getCell(8).toString());
                usuario.setTelefono(row.getCell(9).toString());
                usuario.setCurp(row.getCell(10).toString());
                
                usuario.Rol = new Rol();
                usuario.Rol.setIdRol((int) row.getCell(11).getNumericCellValue());
                //DIRECCION
                usuario.Direcciones = new ArrayList<>();
                Direccion Direccion = new Direccion();
                Direccion.setCalle(row.getCell(12).toString());
                Direccion.setNumeroExterior(row.getCell(13).toString());
                Direccion.setNumeroInterior(row.getCell(14).toString());
                usuario.Direcciones.add(Direccion);
                
                Direccion.Colonia = new Colonia();
                Direccion.Colonia.setIdColonia((int) row.getCell(15).getNumericCellValue());
                
                usuarios.add(usuario);
            }
            
        } catch (Exception ex) {
            usuarios = null;
        }
        
        return usuarios;
    }
    
    private List<ErrorCarga> ValidarDatos(List<Usuario> usuarios) {
        
        List<ErrorCarga> erroresCarga = new ArrayList<>();
        int LineaError = 0;
        
        for (Usuario usuario : usuarios) {
            LineaError++;
            BindingResult bindingResult = validationService.validateObjects(usuario);
            List<ObjectError> errors = bindingResult.getAllErrors();
            
            if (usuario.Rol == null) {
                usuario.Rol = new Rol();
            }
            if (usuario.Direcciones == null) {
                usuario.Direcciones = new ArrayList<>();
                Direccion Direccion = new Direccion();
                usuario.Direcciones.add(Direccion);
            }
            
            BindingResult bindingResultRol = validationService.validateObjects(usuario.Rol);
            List<ObjectError> errorsRol = bindingResultRol.getAllErrors();
            
            BindingResult bindingResultDireccion = validationService.validateObjects(usuario.Direcciones.get(0));
            List<ObjectError> errorsDireccion = bindingResultDireccion.getAllErrors();
            
            List<ObjectError> listaCombinada = new ArrayList<>(errors);
            
            if (!errorsRol.isEmpty()) { 
                listaCombinada.addAll(errorsRol);
            }
            if (!errorsDireccion.isEmpty()) { 
                listaCombinada.addAll(errorsDireccion);
            }
            
            for (ObjectError error : listaCombinada) {
                FieldError fieldError = (FieldError) error;
                ErrorCarga errorCarga = new ErrorCarga();
                errorCarga.Linea = LineaError;
                errorCarga.Campo = fieldError.getField();
                errorCarga.Descripcion = fieldError.getDefaultMessage();
                
                erroresCarga.add(errorCarga);
            }
        }
        
        return erroresCarga;
    }
    
    @GetMapping("CargaMasiva/procesar")
    public String ProcesarArchivo(HttpSession sesion, Model model, RedirectAttributes redirectAttributes){
    
        String rutaArchivo = sesion.getAttribute("archivoCargaMasiva").toString();
        
        File archivo = new File(rutaArchivo);
        String nombreArchivo = archivo.getName();
        String extension = nombreArchivo.split("\\.")[1];
        List<Usuario> usuarios = new ArrayList<>();
        
        if (extension.equals("txt")) {
            usuarios = LecturaArchivo(archivo);
        } else {
            usuarios = LecturaArchivoExcel(archivo);  
        }
        
        if (usuarios != null && !usuarios.isEmpty()) {
            
            List<OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario> usuariosJPA = new ArrayList<>();
            for (Usuario usuario : usuarios) {
                ModelMapper modelMapper = new ModelMapper();
//                modelMapper.typeMap(Usuario.class, OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario.class)
//                        .addMappings(mapper -> mapper.skip(OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario::)); saltar un atributo de una clase
                OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario usuarioJPA = modelMapper.map(usuario, OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario.class);
                usuariosJPA.add(usuarioJPA);
            }
            
            Result result = usuarioService.AddAll(usuariosJPA);
            sesion.removeAttribute("archivoCargaMasiva");
            
            if(result.Correct){
                result.Object = "Se agregó " + usuarios.size() + " usuario(s) nuevo(s)";
            } else{
                result.Object = "No fue posible agregar a los usuarios :c";
            }
            redirectAttributes.addFlashAttribute("resultCargaMasiva", result);
            
            return "redirect:/usuario";
        } else {
            return "redirect:/usuario/CargaMasiva";
        }
    }

}