/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Doménica Cárdenas, Danna Morales, Salma Morales, Alisson Lita, Génesis Vásconez
Descripción: Clase de lógica de negocio (BL) que gestiona operaciones sobre Artistas dentro del sistema InkHarmony.
*/

package BusinessLogic;

import DataAccessComponent.DAO.ArtistaDAO;
import DataAccessComponent.DTO.ArtistaDTO;

import java.util.List;

/**
 * Clase Artista que representa la lógica de negocio relacionada con artistas.
 *
 * Actúa como intermediaria entre la capa de presentación (UI) y la capa DAO.
 */
public class Artista {

    /** Objeto temporal para almacenar el artista manipulado actualmente */
    private ArtistaDTO artista;

    /** DAO encargado del acceso a datos para artistas */
    private ArtistaDAO artistaDAO = new ArtistaDAO();

    public Artista() {}

    /**
     * Recupera todos los artistas registrados en el sistema.
     *
     * @return Lista de artistas
     * @throws Exception si ocurre un error en DAO
     */
    public List<ArtistaDTO> buscarTodo() throws Exception {
        return artistaDAO.buscarTodo();
    }

    /**
     * Recupera un artista específico por su ID.
     *
     * @param idArtista ID del artista
     * @return Objeto ArtistaDTO encontrado
     * @throws Exception si ocurre un error
     */
    public ArtistaDTO buscarPorId(int idArtista) throws Exception {
        artista = artistaDAO.buscarPorId(idArtista);
        return artista;
    }

    /**
     * Registra un nuevo artista en el sistema.
     *
     * @param nombre Nombre del artista
     * @param generos Lista de géneros musicales asociados
     * @param biografia Biografía del artista
     * @param imagen Imagen del artista (como arreglo de bytes)
     * @return true si se registró correctamente
     * @throws Exception si ocurre un error en DAO
     */
    public boolean registrar(String nombre, List<Genero> generos,
                             String biografia, byte[] imagen) throws Exception {
        ArtistaDTO nuevoArtista = new ArtistaDTO(nombre, generos, biografia, imagen);
        return artistaDAO.registrar(nuevoArtista);
    }

    /**
     * Actualiza un artista ya existente.
     *
     * @param artistaDTO Objeto actualizado
     * @return true si se actualizó correctamente
     * @throws Exception si ocurre un error en DAO
     */
    public boolean actualizar(ArtistaDTO artistaDTO) throws Exception {
        return artistaDAO.actualizar(artistaDTO);
    }

    /**
     * Elimina un artista por su ID.
     *
     * @param idArtista ID del artista a eliminar
     * @return true si se eliminó correctamente
     * @throws Exception si ocurre un error
     */
    public boolean eliminar(int idArtista) throws Exception {
        return artistaDAO.eliminar(idArtista);
    }
}
