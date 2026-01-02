
package OBenitez.ProgramacionNCapasNoviembre25.ML;

import jakarta.validation.constraints.NotEmpty;

public class Direccion {
    private int IdDireccion;
    @NotEmpty(message = "Ingresa al menos el nombre de una calle")
    private String Calle;
    private String NumeroInterior;
    @NotEmpty(message = "Por favor ingresa un numero exterior")
    private String NumeroExterior;
    public Colonia Colonia;

    public int getIdDireccion() {
        return IdDireccion;
    }

    public void setIdDireccion(int IdDireccion) {
        this.IdDireccion = IdDireccion;
    }

    public String getCalle() {
        return Calle;
    }

    public void setCalle(String Calle) {
        this.Calle = Calle;
    }

    public String getNumeroInterior() {
        return NumeroInterior;
    }

    public void setNumeroInterior(String NumeroInterior) {
        this.NumeroInterior = NumeroInterior;
    }

    public String getNumeroExterior() {
        return NumeroExterior;
    }

    public void setNumeroExterior(String NumeroExterior) {
        this.NumeroExterior = NumeroExterior;
    }

    public Colonia getColonia() {
        return Colonia;
    }

    public void setColonia(Colonia Colonia) {
        this.Colonia = Colonia;
    }
    
    
}
