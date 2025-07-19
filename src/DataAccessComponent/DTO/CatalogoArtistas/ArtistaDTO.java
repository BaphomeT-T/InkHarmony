/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Autores: Doménica Cárdenas, Danna Morales, Salma Morales, Alisson Lita, Génesis Vásconez
Descripción: Objeto de transferencia de datos (DTO) que representa una canción dentro del sistema InkHarmony.
*/
package DataAccessComponent.DTO.CatalogoArtistas;

import BusinessLogic.Genero;

import java.util.List;

public class ArtistaDTO {
    private int id;
    private String nombre;
    private List<Genero> generos;
    private String biografia;
    private byte[] imagen;

    /**
     * Clase AristaDTO que representa una artista en el sistema.
     *
     * <p>Contiene información relacionada a una canción como su id, su nombre
     * biografía, y generos relacionados.
     * Esta clase se utiliza para transportar datos entre las capas del sistema sin lógica de negocio.</p>
     *
     * @author Grupo D
     * @version 1.0
     * @since 19-07-2025
     */
    public ArtistaDTO(String nombre, List<Genero> generos, String biografia, byte[] imagen) {
        this.nombre = nombre;
        this.biografia = biografia;
        this.imagen = imagen;
        this.generos = generos;
    }

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

    public byte[] getImagen() {
        return imagen;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }
    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setIdArtista(int idArtista) {
        this.id = idArtista;
    }
}
