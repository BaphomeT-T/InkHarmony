/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Samira Arízaga, Paul Dávila, Sebastián Ramos
Descripción: Objeto de transferencia de datos (DTO) que representa una canción dentro del sistema InkHarmony.
*/

package DataAccessComponent.DTO;

import java.util.List;

/**
 * Clase CancionDTO que representa una canción en el sistema.
 *
 * <p>Contiene información relacionada a una canción como su título, duración,
 * año, archivo de audio, portada, artistas relacionados y géneros musicales.
 * Esta clase se utiliza para transportar datos entre las capas del sistema sin lógica de negocio.</p>
 *
 * @author Grupo A
 * @version 1.0
 * @since 18-07-2025
 */
public class CancionDTO {

    /** Identificador único de la canción (autogenerado por la BD) */
    private int idCancion;

    /** Título de la canción */
    private String titulo;

    /** Duración de la canción en segundos */
    private double duracion;

    /** Año de lanzamiento de la canción */
    private int anio;

    /** Fecha de registro de la canción (formato YYYY-MM-DD HH:MM:SS) */
    private String fechaRegistro;

    /** Archivo de audio en formato MP3 como arreglo de bytes */
    private byte[] archivoMP3;

    /** Imagen de portada de la canción como arreglo de bytes */
    private byte[] portada;

    /** Lista de artistas asociados a la canción */
    private List<ArtistaDTO> artistas;

    /** Lista de géneros musicales asociados a la canción */
    private List<Genero> generos;

    /**
     * Constructor vacío.
     */
    public CancionDTO() {}

    /**
     * Constructor para crear una canción sin ID, útil para inserciones.
     *
     * @param titulo Título de la canción
     * @param duracion Duración en segundos
     * @param anio Año de publicación
     * @param fechaRegistro Fecha de registro
     * @param archivoMP3 Archivo de audio en bytes
     * @param portada Imagen de portada
     * @param artistas Lista de artistas asociados
     * @param generos Lista de géneros asociados
     */
    public CancionDTO(String titulo, double duracion, int anio, String fechaRegistro,
                      byte[] archivoMP3, byte[] portada,
                      List<ArtistaDTO> artistas, List<Genero> generos) {
        this.titulo = titulo;
        this.duracion = duracion;
        this.anio = anio;
        this.fechaRegistro = fechaRegistro;
        this.archivoMP3 = archivoMP3;
        this.portada = portada;
        this.artistas = artistas;
        this.generos = generos;
    }

    // Getters y Setters

    public int getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(int idCancion) {
        this.idCancion = idCancion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public double getDuracion() {
        return duracion;
    }

    public void setDuracion(double duracion) {
        this.duracion = duracion;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public byte[] getArchivoMP3() {
        return archivoMP3;
    }

    public void setArchivoMP3(byte[] archivoMP3) {
        this.archivoMP3 = archivoMP3;
    }

    public byte[] getPortada() {
        return portada;
    }

    public void setPortada(byte[] portada) {
        this.portada = portada;
    }

    public List<ArtistaDTO> getArtistas() {
        return artistas;
    }

    public void setArtistas(List<ArtistaDTO> artistas) {
        this.artistas = artistas;
    }

    public List<Genero> getGeneros() {
        return generos;
    }

    public void setGeneros(List<Genero> generos) {
        this.generos = generos;
    }

    /**
     * Devuelve una representación legible de la canción para fines de depuración.
     *
     * @return Cadena que representa la canción y sus atributos
     */
    @Override
    public String toString() {
        return "\n" + getClass().getName()
                + "\nIdCancion      : " + idCancion
                + "\nTitulo         : " + titulo
                + "\nDuracion       : " + duracion
                + "\nAnio           : " + anio
                + "\nFechaRegistro  : " + fechaRegistro
                + "\nArchivoMP3     : " + (archivoMP3 != null ? "Sí" : "No")
                + "\nPortada        : " + (portada != null ? "Sí" : "No")
                + "\nArtistas       : " + artistas
                + "\nGeneros        : " + generos;
    }
}
