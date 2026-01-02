
package OBenitez.ProgramacionNCapasNoviembre25.ML;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


public class Rol {
    @Min(value = 1, message = "El valor del ID debe ser mayot o igual a 1")
    @Max(value = 10, message = "El valor del ID debe ser menor o igual a 10")
    private Integer IdRol;
    private String Nombre;

    public Integer getIdRol() {
        return IdRol;
    }

    public void setIdRol(Integer IdRol) {
        this.IdRol = IdRol;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    
}
