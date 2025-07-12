package DataAccessComponent.DTO;

import java.util.Date;

public class Artista {
    private int id;
    private String nombre;

    //Generos debemos coordinar
    //protected Genero genero; creo que así porque es enum en el modulo de catálogo canciones
    private String genero;

    private String biografia;

    private String imagen;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getNombre() {
        return nombre;
    }
    public String getGenero() {
        return genero;
    }

    public String getBiografia() {
        return biografia;
    }


    public void setGeneros(String genero) {
        this.genero = genero;
    }

    public String getImagen() {
        return imagen;
    }
}
