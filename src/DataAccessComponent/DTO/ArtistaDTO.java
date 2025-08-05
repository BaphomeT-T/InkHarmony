package DataAccessComponent.DTO;

import BusinessLogic.Genero;

import java.util.List;

/**
 * Objeto de transferencia de datos (DTO) que representa un artista.
 * Contiene únicamente atributos y sus respectivos getters y setters.
 *
 * No contiene lógica de negocio ni acceso a base de datos.
 *
 * © 2025 EPN-FIS, Todos los derechos reservados
 * Autores: Tu equipo de CatalogoArtistas
 */
public class ArtistaDTO {

    private int id;
    private String nombre;
    private List<Genero> generos;
    private String biografia;
    private byte[] imagen;

    /**
     * Constructor vacío.
     */
    public ArtistaDTO() {}

    /**
     * Constructor para creación completa de un artista (sin ID).
     */
    public ArtistaDTO(String nombre, List<Genero> generos, String biografia, byte[] imagen) {
        this.nombre = nombre;
        this.generos = generos;
        this.biografia = biografia;
        this.imagen = imagen;
    }

    /**
     * Constructor completo con ID.
     */
    public ArtistaDTO(int id, String nombre, List<Genero> generos, String biografia, byte[] imagen) {
        this.id = id;
        this.nombre = nombre;
        this.generos = generos;
        this.biografia = biografia;
        this.imagen = imagen;
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Genero> getGenero() {
        return generos;
    }

    public void setGeneros(List<Genero> generos) {
        this.generos = generos;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "\n" + getClass().getSimpleName()
                + "\nId           : " + id
                + "\nNombre       : " + nombre
                + "\nBiografía    : " + biografia
                + "\nGeneros      : " + generos
                + "\nImagen       : " + (imagen != null ? "Sí" : "No");
    }
}
