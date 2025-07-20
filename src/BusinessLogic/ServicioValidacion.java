package BusinessLogic;

import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DAO.ArtistaDAO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase de servicio para validaciones de artistas.
 * Valida nombre único, campos requeridos y asociaciones con canciones o playlists.
 *
 * No accede directamente a la base de datos, sino a través del DAO.
 */
public class ServicioValidacion implements UnicoNombreValidable, AsociacionValidable {

    private ArtistaDAO artistaDAO = new ArtistaDAO();

    /**
     * Valida campos mínimos del artista.
     * @param artista El artista a validar
     * @return true si todos los campos requeridos están completos
     */
    public boolean validarCampos(ArtistaDTO artista) {
        return artista.getNombre() != null && !artista.getNombre().trim().isEmpty()
                && artista.getBiografia() != null && !artista.getBiografia().trim().isEmpty()
                && artista.getImagen() != null
                && artista.getGenero() != null && !artista.getGenero().isEmpty();
    }

    /**
     * Verifica si el nombre del artista ya existe.
     * @param nombre Nombre a validar
     * @return true si el nombre no se repite en la base
     */
    @Override
    public boolean esNombreUnico(String nombre) {
        try {
            List<ArtistaDTO> artistas = artistaDAO.buscarTodo();
            for (ArtistaDTO a : artistas) {
                if (a.getNombre().equalsIgnoreCase(nombre)) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Puede ser reemplazado con logs
        }
        return true;
    }

    /**
     * Verifica si el artista tiene elementos asociados que impidan su eliminación.
     *
     * @param artista Artista a validar
     * @return true si tiene canciones o playlists asociadas
     */
    @Override
    public boolean tieneElementosAsociados(ArtistaDTO artista) {
        try {
            return artistaDAO.tieneCancionesAsociadas(artista.getId());
        } catch (Exception e) {
            e.printStackTrace(); // Puede ser reemplazado con logs
            return true; // Por precaución, evitar borrar si hay error al verificar
        }
    }
}
