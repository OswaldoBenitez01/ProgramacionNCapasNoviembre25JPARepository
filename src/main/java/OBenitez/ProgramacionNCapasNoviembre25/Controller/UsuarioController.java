package OBenitez.ProgramacionNCapasNoviembre25.Controller;

import OBenitez.ProgramacionNCapasNoviembre25.Configuration.SpringSecurityConfiguration;
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
import OBenitez.ProgramacionNCapasNoviembre25.Service.SeguridadControllerService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    @Autowired
    private SpringSecurityConfiguration springSecurityConfiguration;
    @Autowired
    private SeguridadControllerService seguridadControllerService;

    @GetMapping
    public String GetAll(Model model) {
        try {
            Result result = usuarioService.GetAll();
            model.addAttribute("Usuarios", result != null ? result.Objects : new ArrayList<>());
            model.addAttribute("usuarioBusqueda", new Usuario());

            Result resultRoles = rolService.GetAll();
            model.addAttribute("Roles", resultRoles != null ? resultRoles.Objects : new ArrayList<>());
        } catch (Exception ex) {
            model.addAttribute("Usuarios", new ArrayList<>());
            model.addAttribute("Roles", new ArrayList<>());
            model.addAttribute("errorGeneral", "Error al cargar usuarios: " + ex.getMessage());
        }
        return "Index";
    }

    @PostMapping("busqueda")
    public String Busqueda(@ModelAttribute("usuario") Usuario usuario, Model model) {
        try {
            Result result = usuarioService.BusquedaAbierta(usuario);
            model.addAttribute("Usuarios", result != null ? result.Objects : new ArrayList<>());

            if (result == null || result.Objects == null || result.Objects.isEmpty()) {
                model.addAttribute("mensajeBusqueda", true);
            }

            model.addAttribute("usuarioBusqueda", usuario);

            Result resultRoles = rolService.GetAll();
            model.addAttribute("Roles", resultRoles != null ? resultRoles.Objects : new ArrayList<>());
        } catch (Exception ex) {
            model.addAttribute("Usuarios", new ArrayList<>());
            model.addAttribute("errorGeneral", "Error en la búsqueda: " + ex.getMessage());
        }
        return "Index";
    }

    @GetMapping("form")
    public String Form(Model model) {
        try {
            Result resultRol = rolService.GetAll();
            model.addAttribute("Roles", resultRol != null ? resultRol.Objects : new ArrayList<>());

            Result resultPais = paisService.GetAll();
            model.addAttribute("Paises", resultPais != null ? resultPais.Objects : new ArrayList<>());
        } catch (Exception ex) {
            model.addAttribute("Roles", new ArrayList<>());
            model.addAttribute("Paises", new ArrayList<>());
            model.addAttribute("errorGeneral", "Error al cargar datos: " + ex.getMessage());
        }

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(0);
        model.addAttribute("Usuario", usuario);
        return "UsuarioForm";
    }

    @PostMapping("add")
    public String Add(@Valid @ModelAttribute("Usuario") Usuario usuario,
            BindingResult bindingResult,
            @RequestParam("imagenUsuario") MultipartFile imagenUsuario,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("Usuario", usuario);
            try {
                model.addAttribute("Roles", rolService.GetAll().Objects);
                model.addAttribute("Paises", paisService.GetAll().Objects);
            } catch (Exception ignored) {
            }
            return "UsuarioForm";
        }

        Result result = new Result();
        result.Correct = false;
        result.Object = "Error inesperado";

        try {
            if (imagenUsuario.isEmpty()) {
                usuario.setImagen(null);
            } else {
                String encodedString = Base64.getEncoder().encodeToString(imagenUsuario.getBytes());
                usuario.setImagen(encodedString);
            }

            usuario.setStatus(1);
            usuario.setPassword(springSecurityConfiguration.passwordEncoder().encode(usuario.getPassword()));

            ModelMapper modelMapper = new ModelMapper();
            OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario usuarioJPA
                    = modelMapper.map(usuario, OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario.class);
            result = usuarioService.Add(usuarioJPA);

            if (result.Correct) {
                result.Object = "El usuario se agregó correctamente";
            } else {
                result.Object = "No fue posible agregar al usuario :c";
            }

        } catch (IOException ex) {
            result.ErrorMessage = ex.getLocalizedMessage();
            result.Object = "Error al procesar la imagen del usuario";
            result.ex = ex;
        } catch (Exception ex) {
            result.ErrorMessage = ex.getLocalizedMessage();
            result.Object = "Ocurrió un error inesperado al agregar el usuario";
            result.ex = ex;
        }

        redirectAttributes.addFlashAttribute("resultAddUserFull", result);
        return "redirect:/usuario";
    }

    @GetMapping("detail/{IdUsuario}")
    public String Detail(@PathVariable("IdUsuario") int IdUsuario, Model model) {
        if (!seguridadControllerService.esPropioOAdmin(IdUsuario)) {
            return "redirect:/login?error=forbidden";
        }

        try {
            Result result = usuarioService.GetById(IdUsuario);
            model.addAttribute("Usuario", result != null ? result.Object : null);

            Result resultPais = paisService.GetAll();
            model.addAttribute("Paises", resultPais != null ? resultPais.Objects : new ArrayList<>());

            Result resultRoles = rolService.GetAll();
            model.addAttribute("Roles", resultRoles != null ? resultRoles.Objects : new ArrayList<>());
        } catch (Exception ex) {
            model.addAttribute("errorGeneral", "Error al cargar detalle: " + ex.getMessage());
        }

        return "UsuarioDetail";
    }

    @GetMapping("delete/{IdUsuario}")
    public String Delete(@PathVariable("IdUsuario") int IdUsuario, RedirectAttributes redirectAttributes) {
        Result result = new Result();
        result.Correct = false;
        result.Object = "Error inesperado";

        try {
            result = usuarioService.DeleteById(IdUsuario);

            if (result.Correct) {
                result.Object = "El usuario con ID " + IdUsuario + " fue eliminado";
            } else {
                result.Object = "No fue posible eliminar al usuario :c";
            }
        } catch (Exception ex) {
            result.ErrorMessage = ex.getMessage();
            result.Object = "Error al eliminar: " + ex.getMessage();
        }

        redirectAttributes.addFlashAttribute("resultDelete", result);
        return "redirect:/usuario";
    }

    @GetMapping("toogleStatus/{IdUsuario}/{Status}")
    @ResponseBody
    public Result ToggleStatus(@PathVariable("IdUsuario") int IdUsuario,
            @PathVariable("Status") int Status,
            RedirectAttributes redirectAttributes) {
        Result result = new Result();
        result.Correct = false;
        result.Object = "Error inesperado";

        try {
            result = usuarioService.UpdateStatus(IdUsuario, Status);
            redirectAttributes.addFlashAttribute("resultDeleteSoft", result);
            return result;
        } catch (Exception ex) {
            result.ErrorMessage = ex.getMessage();
            result.Object = "Error al cambiar status: " + ex.getMessage();
            return result;
        }
    }

    @PostMapping("/updatePhoto")
    public String updatePhoto(@ModelAttribute Usuario usuario,
            @RequestParam("imagenUsuario") MultipartFile imagenUsuario,
            RedirectAttributes redirectAttributes) {

        Result result = new Result();
        result.Correct = false;
        result.Object = "Error inesperado";

        try {
            if (usuario == null || usuario.getIdUsuario() == 0) {
                result.Object = "Usuario inválido";
            } else if (imagenUsuario.isEmpty()) {
                result.Object = "No se seleccionó ninguna imagen";
            } else {
                String encodedString = Base64.getEncoder().encodeToString(imagenUsuario.getBytes());
                result = usuarioService.UpdatePhoto(usuario.getIdUsuario(), encodedString);
                if (result.Correct) {
                    result.Object = "Se actualizó correctamente la foto";
                } else {
                    result.Object = "No se pudo actualizar la foto :c";
                }
            }
        } catch (IOException ex) {
            result.ErrorMessage = ex.getLocalizedMessage();
            result.Object = "No se pudo actualizar la foto :c";
            result.ex = ex;
        } catch (Exception ex) {
            result.ErrorMessage = ex.getMessage();
            result.Object = "Error inesperado al actualizar foto";
            result.ex = ex;
        }

        redirectAttributes.addFlashAttribute("resultUpdatePhoto", result);
        return "redirect:/usuario/detail/" + (usuario != null ? usuario.getIdUsuario() : 0);
    }

    @PostMapping("/deletePhoto/{IdUsuario}")
    public String deletePhoto(@PathVariable Integer IdUsuario, RedirectAttributes redirectAttributes) {
        Result result = new Result();
        result.Correct = false;
        result.Object = "Error inesperado";

        try {
            if (IdUsuario == null || IdUsuario == 0) {
                result.Object = "ID de usuario inválido";
            } else {
                result = usuarioService.UpdatePhoto(IdUsuario, null);
                if (result.Correct) {
                    result.Object = "Se eliminó correctamente la foto";
                } else {
                    result.Object = "No se pudo eliminar la foto :c";
                }
            }
        } catch (Exception ex) {
            result.ErrorMessage = ex.getLocalizedMessage();
            result.Object = "No se pudo eliminar la foto :c";
            result.ex = ex;
        }

        redirectAttributes.addFlashAttribute("resultUpdatePhoto", result);
        return "redirect:/usuario/detail/" + IdUsuario;
    }

    @GetMapping("deleteAddress/{IdDireccion}/{IdUsuario}")
    public String DeleteAddress(@PathVariable("IdDireccion") int IdDireccion,
            @PathVariable("IdUsuario") int IdUsuario,
            RedirectAttributes redirectAttributes) {
        Result result = new Result();
        result.Correct = false;
        result.Object = "Error inesperado";

        try {
            result = direccionService.DeleteAddressById(IdDireccion);

            if (result.Correct) {
                result.Object = "La dirección fue eliminada";
            } else {
                result.Object = "No fue posible eliminar la dirección :c";
            }
        } catch (Exception ex) {
            result.ErrorMessage = ex.getMessage();
            result.Object = "Error al eliminar dirección: " + ex.getMessage();
        }

        redirectAttributes.addFlashAttribute("resultDeleteAddress", result);
        return "redirect:/usuario/detail/" + IdUsuario;
    }

    @GetMapping("getEstadosByPais/{idPais}")
    @ResponseBody
    public Result EstadosByPais(@PathVariable("idPais") int idPais) {
        try {
            return estadoService.GetEstadosByPais(idPais);
        } catch (Exception ex) {
            Result result = new Result();
            result.Correct = false;
            result.Object = "Error al obtener estados";
            result.ErrorMessage = ex.getMessage();
            return result;
        }
    }

    @GetMapping("getMunicipiosByEstado/{idEstado}")
    @ResponseBody
    public Result MunicipiosByEstado(@PathVariable("idEstado") int idEstado) {
        try {
            return municipioService.GetMunicipiosByEstado(idEstado);
        } catch (Exception ex) {
            Result result = new Result();
            result.Correct = false;
            result.Object = "Error al obtener municipios";
            result.ErrorMessage = ex.getMessage();
            return result;
        }
    }

    @GetMapping("getColoniasByMunicipio/{idMunicipio}")
    @ResponseBody
    public Result ColoniasByMunicipio(@PathVariable("idMunicipio") int idMunicipio) {
        try {
            return coloniaService.GetColoniasByMunicipio(idMunicipio);
        } catch (Exception ex) {
            Result result = new Result();
            result.Correct = false;
            result.Object = "Error al obtener colonias";
            result.ErrorMessage = ex.getMessage();
            return result;
        }
    }

    @PostMapping("formEditable")
    public String Form(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {

        if (usuario == null || usuario.getIdUsuario() == 0) {
            Result result = new Result();
            result.Correct = false;
            result.Object = "Usuario inválido";
            redirectAttributes.addFlashAttribute("resultEditUserBasic", result);
            return "redirect:/usuario";
        }

        if (usuario.Direcciones == null || usuario.Direcciones.isEmpty()) {
            Result result = new Result();
            result.Correct = false;
            result.Object = "No se recibió información de dirección";
            redirectAttributes.addFlashAttribute("resultEditUserBasic", result);
            return "redirect:/usuario/detail/" + usuario.getIdUsuario();
        }

        int idDireccion = usuario.Direcciones.get(0).getIdDireccion();

        try {
            if (idDireccion == -1) {
                // ACTUALIZAR USUARIO
                Result resultUsuarioActual = usuarioService.GetById(usuario.getIdUsuario());
                if (!resultUsuarioActual.Correct || resultUsuarioActual.Object == null) {
                    Result result = new Result();
                    result.Correct = false;
                    result.Object = "No se encontró el usuario en la base de datos";
                    redirectAttributes.addFlashAttribute("resultEditUserBasic", result);
                    return "redirect:/usuario/detail/" + usuario.getIdUsuario();
                }

                OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario usuarioActual = (OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario) resultUsuarioActual.Object;

                if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                    usuario.setPassword(usuarioActual.getPassword());
                } else {
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    String passwordHasheada = passwordEncoder.encode(usuario.getPassword());
                    usuario.setPassword(passwordHasheada);
                }

                ModelMapper modelMapper = new ModelMapper();
                OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario usuarioJPA = modelMapper.map(usuario, OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario.class);

                Result result = usuarioService.UpdateUser(usuarioJPA);
                if (result.Correct) {
                    result.Object = "El usuario se actualizó correctamente";
                } else {
                    result.Object = "No fue posible actualizar al usuario :c";
                }
                redirectAttributes.addFlashAttribute("resultEditUserBasic", result);

            } else if (idDireccion == 0) {
                //  AGREGAR DIRECCIÓN 
                ModelMapper modelMapper = new ModelMapper();
                OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion direccionJPA
                        = modelMapper.map(usuario.Direcciones.get(0), OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion.class);
                direccionJPA.usuario = new OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario();
                direccionJPA.usuario.setIdUsuario(usuario.getIdUsuario());

                Result result = direccionService.AddAddress(direccionJPA);
                if (result.Correct) {
                    result.Object = "La dirección se agregó correctamente";
                } else {
                    result.Object = "No fue posible agregar la dirección :c";
                }
                redirectAttributes.addFlashAttribute("resultAddAddress", result);

            } else {
                //  EDITAR DIRECCIÓN 
                ModelMapper modelMapper = new ModelMapper();
                OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion direccionJPA
                        = modelMapper.map(usuario.Direcciones.get(0), OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion.class);

                Result result = direccionService.UpdateAddressById(direccionJPA);
                if (result.Correct) {
                    result.Object = "La dirección se actualizó correctamente";
                } else {
                    result.Object = "No fue posible actualizar la dirección :c";
                }
                redirectAttributes.addFlashAttribute("resultEditAddress", result);
            }
        } catch (Exception ex) {
            Result result = new Result();
            result.Correct = false;
            result.Object = "Error en operación: " + ex.getMessage();
            result.ErrorMessage = ex.getMessage();
            redirectAttributes.addFlashAttribute("resultEditUserBasic", result);
        }

        return "redirect:/usuario/detail/" + usuario.getIdUsuario();
    }



    @GetMapping("cargaMasiva")
    public String CargaMsiva() {
        return "CargaMasiva";
    }

    @PostMapping("CargaMasiva")
    public String CargaMasiva(@ModelAttribute MultipartFile archivo, Model model, HttpSession sesion) {

        if (archivo == null || archivo.isEmpty()) {
            model.addAttribute("tieneErrores", true);
            model.addAttribute("erroresGenerales", "Debe seleccionar un archivo");
            return "CargaMasiva";
        }

        String nombreArchivo = archivo.getOriginalFilename();
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            model.addAttribute("tieneErrores", true);
            model.addAttribute("erroresGenerales", "Archivo sin extensión válida");
            return "CargaMasiva";
        }

        String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1).toLowerCase();
        if (!extension.equals("txt") && !extension.equals("xlsx")) {
            model.addAttribute("tieneErrores", true);
            model.addAttribute("erroresGenerales", "Solo se aceptan archivos .txt o .xlsx");
            return "CargaMasiva";
        }

        try {
            String path = System.getProperty("user.dir");
            String pathArchivo = "src/main/resources/archivos";
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            String rutaAbsoluta = path + "/" + pathArchivo + "/" + fecha + nombreArchivo;

            archivo.transferTo(new File(rutaAbsoluta));

            List<Usuario> usuarios = extension.equals("txt")
                    ? LecturaArchivo(new File(rutaAbsoluta))
                    : LecturaArchivoExcel(new File(rutaAbsoluta));

            if (usuarios == null) {
                model.addAttribute("tieneErrores", true);
                model.addAttribute("erroresGenerales", "No fue posible leer el archivo");
                return "CargaMasiva";
            }

            List<ErrorCarga> errores = ValidarDatos(usuarios);

            if (!errores.isEmpty()) {
                model.addAttribute("errores", errores);
                model.addAttribute("tieneErrores", true);
            } else {
                model.addAttribute("mensajeExito", "Carga exitosa. Se cargaron " + usuarios.size() + " usuario(s) correctamente");
                model.addAttribute("tieneErrores", false);
                sesion.setAttribute("archivoCargaMasiva", rutaAbsoluta);
            }

        } catch (IOException ex) {
            model.addAttribute("tieneErrores", true);
            model.addAttribute("erroresGenerales", "Error al guardar archivo: " + ex.getMessage());
        } catch (Exception ex) {
            model.addAttribute("tieneErrores", true);
            model.addAttribute("erroresGenerales", "Error inesperado: " + ex.getMessage());
        }

        return "CargaMasiva";
    }

    private List<Usuario> LecturaArchivo(File archivo) {
        List<Usuario> usuarios = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(archivo))) {
            bufferedReader.readLine(); // saltar header
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] datos = line.split("\\|");
                if (datos.length < 16) {
                    continue; // validar longitud mínima
                }
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

                usuario.Rol = new Rol();
                usuario.Rol.setIdRol(Integer.parseInt(datos[11]));

                usuario.Direcciones = new ArrayList<>();
                Direccion direccion = new Direccion();
                direccion.setCalle(datos[12]);
                direccion.setNumeroExterior(datos[13]);
                direccion.setNumeroInterior(datos[14]);
                usuario.Direcciones.add(direccion);

                direccion.Colonia = new Colonia();
                direccion.Colonia.setIdColonia(Integer.parseInt(datos[15]));

                usuarios.add(usuario);
            }
        } catch (Exception ex) {
            usuarios = null;
        }

        return usuarios;
    }

    private List<Usuario> LecturaArchivoExcel(File archivo) {
        List<Usuario> usuarios = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(archivo)) {
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getCell(0) == null) {
                    continue;
                }

                Usuario usuario = new Usuario();
                usuario.setUsername(getCellValue(row.getCell(0)));
                usuario.setNombre(getCellValue(row.getCell(1)));
                usuario.setApellidoPaterno(getCellValue(row.getCell(2)));
                usuario.setApellidoMaterno(getCellValue(row.getCell(3)));
                usuario.setEmail(getCellValue(row.getCell(4)));
                usuario.setPassword(getCellValue(row.getCell(5)));

                if (row.getCell(6) != null) {
                    java.util.Date utilDate = row.getCell(6).getDateCellValue();
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                    usuario.setFechaNacimiento(sqlDate);
                }

                usuario.setSexo(getCellValue(row.getCell(7)));
                usuario.setCelular(getCellValue(row.getCell(8)));
                usuario.setTelefono(getCellValue(row.getCell(9)));
                usuario.setCurp(getCellValue(row.getCell(10)));

                usuario.Rol = new Rol();
                if (row.getCell(11) != null) {
                    usuario.Rol.setIdRol((int) row.getCell(11).getNumericCellValue());
                }

                usuario.Direcciones = new ArrayList<>();
                Direccion direccion = new Direccion();
                direccion.setCalle(getCellValue(row.getCell(12)));
                direccion.setNumeroExterior(getCellValue(row.getCell(13)));
                direccion.setNumeroInterior(getCellValue(row.getCell(14)));
                usuario.Direcciones.add(direccion);

                direccion.Colonia = new Colonia();
                if (row.getCell(15) != null) {
                    direccion.Colonia.setIdColonia((int) row.getCell(15).getNumericCellValue());
                }

                usuarios.add(usuario);
            }
        } catch (Exception ex) {
            usuarios = null;
        }
        return usuarios;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return cell.toString().trim();
    }

    private List<ErrorCarga> ValidarDatos(List<Usuario> usuarios) {
        List<ErrorCarga> erroresCarga = new ArrayList<>();

        if (usuarios == null || usuarios.isEmpty()) {
            return erroresCarga;
        }

        int LineaError = 0;

        for (Usuario usuario : usuarios) {
            LineaError++;
            BindingResult bindingResult = validationService.validateObjects(usuario);
            List<ObjectError> errors = bindingResult.getAllErrors();

            if (usuario.Rol == null) {
                usuario.Rol = new Rol();
            }
            if (usuario.Direcciones == null || usuario.Direcciones.isEmpty()) {
                usuario.Direcciones = new ArrayList<>();
                Direccion direccion = new Direccion();
                usuario.Direcciones.add(direccion);
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
                if (error instanceof FieldError fieldError) {
                    ErrorCarga errorCarga = new ErrorCarga();
                    errorCarga.Linea = LineaError;
                    errorCarga.Campo = fieldError.getField();
                    errorCarga.Descripcion = fieldError.getDefaultMessage();
                    erroresCarga.add(errorCarga);
                }
            }
        }

        return erroresCarga;
    }

    @GetMapping("CargaMasiva/procesar")
    public String ProcesarArchivo(HttpSession sesion, RedirectAttributes redirectAttributes) {

        Object rutaArchivoObj = sesion.getAttribute("archivoCargaMasiva");
        if (rutaArchivoObj == null) {
            Result result = new Result();
            result.Correct = false;
            result.Object = "No hay archivo pendiente por procesar";
            redirectAttributes.addFlashAttribute("resultCargaMasiva", result);
            return "redirect:/usuario/CargaMasiva";
        }

        String rutaArchivo = rutaArchivoObj.toString();
        File archivo = new File(rutaArchivo);

        if (!archivo.exists() || !archivo.isFile()) {
            sesion.removeAttribute("archivoCargaMasiva");
            Result result = new Result();
            result.Correct = false;
            result.Object = "El archivo de carga masiva no existe o es inválido";
            redirectAttributes.addFlashAttribute("resultCargaMasiva", result);
            return "redirect:/usuario/CargaMasiva";
        }

        String nombreArchivo = archivo.getName();
        String extension = nombreArchivo.split("\\.")[1];
        List<Usuario> usuarios = new ArrayList<>();

        if (extension.equals("txt")) {
            usuarios = LecturaArchivo(archivo);
        } else {
            usuarios = LecturaArchivoExcel(archivo);
        }

        if (usuarios != null && !usuarios.isEmpty()) {
            try {
                List<OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario> usuariosJPA = new ArrayList<>();
                for (Usuario usuario : usuarios) {
                    ModelMapper modelMapper = new ModelMapper();
                    OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario usuarioJPA
                            = modelMapper.map(usuario, OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario.class);
                    usuariosJPA.add(usuarioJPA);
                }

                Result result = usuarioService.AddAll(usuariosJPA);
                sesion.removeAttribute("archivoCargaMasiva");

                if (result.Correct) {
                    result.Object = "Se agregó " + usuarios.size() + " usuario(s) nuevo(s)";
                } else {
                    result.Object = "No fue posible agregar a los usuarios :c";
                }

                redirectAttributes.addFlashAttribute("resultCargaMasiva", result);
            } catch (Exception ex) {
                Result result = new Result();
                result.Correct = false;
                result.Object = "Error al procesar usuarios: " + ex.getMessage();
                result.ErrorMessage = ex.getMessage();
                redirectAttributes.addFlashAttribute("resultCargaMasiva", result);
            }
            return "redirect:/usuario";
        } else {
            return "redirect:/usuario/CargaMasiva";
        }
    }
}
