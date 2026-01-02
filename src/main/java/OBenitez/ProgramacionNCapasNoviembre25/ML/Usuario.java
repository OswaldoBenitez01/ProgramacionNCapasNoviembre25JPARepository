package OBenitez.ProgramacionNCapasNoviembre25.ML;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.sql.Date;
import java.util.List;

public class Usuario {
    private Integer IdUsuario;
    
    //@NotNull(message = "El campo es obligatorio.")
    @NotEmpty(message = "El campo es obligatorio.")
    @Size(min=3,max=30, message = "Debe contener al menos 3 caracteres y 20 como maximo.")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9-_]{3,20}", message = "El nombre de usuario debe empezar con una letra, puede contener numeros y guiones. Debe ser un maximo de 20 caracteres y minimo 3")
    private String Username;
    
    @NotEmpty(message = "El campo es obligatorio.")
    @Size(min=3,max=25, message = "Debe contener al menos 3 caracteres y 25 como maximo.")
    @Pattern(regexp = "(^[A-Za-z]{0,12})([ ]{0,1})([A-Za-z]{0,12})", message = "El nombre no debe contener mas de un espacio, tener maximo 12 letras por nombre y minimo 3")
    private String Nombre;
    
    @NotEmpty(message = "El campo es obligatorio.")
    @Size(min=3,max=25, message = "Debe contener al menos 3 caracteres y 25 como maximo.")
    @Pattern(regexp = "(^[A-Za-z]{0,25})", message = "El apellido no debe contener espacios ni tener mas de 25 caracteres, debe contener minimo 3 caracteres")
    private String ApellidoPaterno;
    
    @NotEmpty(message = "El campo es obligatorio.")
    @Size(min=3,max=25, message = "Debe contener al menos 3 caracteres y 25 como maximo.")
    @Pattern(regexp = "(^[A-Za-z]{0,25})", message = "El apellido no debe contener espacios ni tener mas de 25 caracteres, debe contener minimo 3 caracteres")
    private String ApellidoMaterno;

    @NotEmpty(message = "El campo es obligatorio.")
    @Pattern(regexp = "\\b[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,4}\\b", message = "Ingresa un correo valido")
    private String Email;
    
    @NotEmpty(message = "El campo es obligatorio.")
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W]).{8,64})", message = "La contraseña debe contener; un numero, una letra mayuscula, un caracter especial y una longitud de 8 caracteres")
    private String Password;
    
    @NotNull(message = "El campo es obligatorio.")
    private Date FechaNacimiento;
    
    @NotEmpty(message = "El campo es obligatorio.")
    @Pattern(regexp = "^[HMN]$", message = "Ingresa un caracter valido: H - Hombre/M- Mujer/N - No definido")
    private String Sexo;
    
    @NotEmpty(message = "El campo es obligatorio.")
    @Pattern(regexp = "(\\+?\\d{1})?[-.]?\\(?(\\d{3})\\)?[-.]?(\\d{3})[-.]?(\\d{4})", message = "Ingresa un numero de telefono valido")
    @Size(min=10,max=10, message = "Ingresa un numero de telefono de 10 digitos")
    private String Telefono;
    
    @NotEmpty(message = "El campo es obligatorio.")
    @Pattern(regexp = "(\\+?\\d{1})?[-.]?\\(?(\\d{3})\\)?[-.]?(\\d{3})[-.]?(\\d{4})", message = "Ingresa un numero de celular valido")
    @Size(min=10,max=10, message = "Ingresa un numero de celular de 10 digitos")
    private String Celular;
    
    @NotEmpty(message = "El campo es obligatorio.")
    @Pattern(regexp = "[A-Z]{1}[AEIOU]{1}[A-Z]{2}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])[HM]{1}(AS|BC|BS|CC|CS|CH|CL|CM|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE)[B-DF-HJ-NP-TV-Z]{3}[0-9A-Z]{1}[0-9]{1}", message = "Introduce un CURP Mexicano valido")
    private String Curp;
    
    private String Imagen;
    
    private int Status;

    public Rol Rol;
    public List<Direccion> Direcciones;

    public Integer getIdUsuario() {
        return IdUsuario;
    }

    public void setIdUsuario(Integer IdUsuario) {
        this.IdUsuario = IdUsuario;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getApellidoPaterno() {
        return ApellidoPaterno;
    }

    public void setApellidoPaterno(String ApellidoPaterno) {
        this.ApellidoPaterno = ApellidoPaterno;
    }

    public String getApellidoMaterno() {
        return ApellidoMaterno;
    }

    public void setApellidoMaterno(String ApellidoMaterno) {
        this.ApellidoMaterno = ApellidoMaterno;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public Date getFechaNacimiento() {
        return FechaNacimiento;
    }

    public void setFechaNacimiento(Date FechaNacimiento) {
        this.FechaNacimiento = FechaNacimiento;
    }
    
    public String getSexo() {
        return Sexo;
    }

    public void setSexo(String Sexo) {
        this.Sexo = Sexo;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public String getCelular() {
        return Celular;
    }

    public void setCelular(String Celular) {
        this.Celular = Celular;
    }

    public String getCurp() {
        return Curp;
    }

    public void setCurp(String Curp) {
        this.Curp = Curp;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String Imagen) {
        this.Imagen = Imagen;
    }
    
    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    
    
    public Rol getRol() {
        return Rol;
    }

    public void setRol(Rol Rol) {
        this.Rol = Rol;
    }

    public List<Direccion> getDirecciones() {
        return Direcciones;
    }

    public void setDirecciones(List<Direccion> Direcciones) {
        this.Direcciones = Direcciones;
    }
    
    
}
