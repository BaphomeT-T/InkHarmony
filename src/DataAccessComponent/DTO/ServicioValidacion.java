package BusinessLogic.CatalogoArtistas;

public class ServicioValidacion implements UnicoNombreValidable, AsociacionValidable{

    public boolean validarCampos(Artista artista) {
        // Implementar validacion de campos
        // Agregar
        return true; // Temporal
    }

    public boolean esNombreUnico(String nombre) {
        // Implementacion para verificar si el nombre es unico
        // Agregar
        return false;
    }

    public boolean tieneElementoAsociado(Artista artista) {
        // Implementacion para verificar si el artista tiene elementos asociados
        // Agregar
        return false;
    }

}
