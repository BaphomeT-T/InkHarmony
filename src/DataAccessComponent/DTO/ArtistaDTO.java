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
     *
     * @param nombre Nombre del artista
     * @param generos Lista de géneros musicales
     * @param biografia Biografía del artista
     * @param imagen Imagen como arreglo de bytes
     */
    public ArtistaDTO(String nombre, List<Genero> generos, String biografia, byte[] imagen) {
        this.nombre = nombre;
        this.generos = generos;
        this.biografia = biografia;
        this.imagen = imagen;
    }

    /**
     * Constructor completo con ID.
     *
     * @param id Identificar único
     * @param nombre Nombre del artista
     * @param generos Lista de géneros musicales
     * @param biografia Biografía del artista
     * @param imagen Imagen como arreglo de bytes
     */
    public ArtistaDTO(int id, String nombre, List<Genero> generos, String biografia, byte[] imagen) {
        this.id = id;
        this.nombre = nombre;
        this.generos = generos;
        this.biografia = biografia;
        this.imagen = imagen;
    }

    // Getters y setters

    /**
     * Obtiene el ID del artista.
     *
     * @return ID del artista
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del artista.
     *
     * @param id Nuevo ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del artista.
     *
     * @return Nombre del artista
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del artista.
     *
     * @param nombre Nombre del artista
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la lista de géneros musicales del artista.
     *
     * @return Lista de géneros
     */
    public List<Genero> getGenero() {
        return generos;
    }

    /**
     * Establece la lista de géneros musicales del artista.
     *
     * @param generos Lista de géneros
     */
    public void setGeneros(List<Genero> generos) {
        this.generos = generos;
    }

    /**
     * Obtiene la biografía del artista.
     *
     * @return Biografía
     */
    public String getBiografia() {
        return biografia;
    }

    /**
     * Establece la biografía del artista.
     *
     * @param biografia Texto descriptivo
     */
    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    /**
     * Obtiene la imagen del artista como arreglo de bytes.
     *
     * @return Imagen en formato BLOB
     */
    public byte[] getImagen() {
        return imagen;
    }

    /**
     * Establece la imagen del artista.
     *
     * @param imagen Imagen como arreglo de bytes
     */
    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    /**
     * Representación textual del artista para depuración y logs.
     *
     * @return Cadena que describe al artista
     */
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
