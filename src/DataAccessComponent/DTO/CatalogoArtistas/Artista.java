package DataAccessComponent.DTO.CatalogoArtistas;

import DataAccessComponent.DTO.CatalogoCanciones.Genero;

import java.util.List;

public class Artista {
    private int id;
    private String nombre;

    public Artista(int id, String nombre, List<Genero> generos, String biografia, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.biografia = biografia;
        this.imagen = imagen;
        this.generos = generos;
    }
    //Generos debemos coordinar
    //protected Genero genero; creo que así porque es enum en el modulo de catálogo canciones, yo ya implemente uno
    //cree la carpeta de Catalogo canciones y agregue el enum de Genero, lo cual no se si es correcto porque
    //al hacer merge al main no se si de problemas
    private List<Genero> generos;

    private String biografia;

    private String imagen;

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
    public List<Genero> getGenero() {
        return generos;
    }

    public String getBiografia() {
        return biografia;
    }


    public void setGeneros(List<Genero> generos) {
        this.generos = generos;
    }

    public String getImagen() {
        return imagen;
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
}
