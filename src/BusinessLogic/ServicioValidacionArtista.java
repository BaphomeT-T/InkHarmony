package BusinessLogic;

import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DAO.ArtistaDAO;

import java.util.List;

/**
 * Clase de servicio para validaciones de artistas.
 * Valida nombre único, campos requeridos y asociaciones con canciones o playlists.
 *
 * No accede directamente a la base de datos, sino a través del DAO.
 */
public class ServicioValidacionArtista implements UnicoNombreValidable, AsociacionValidable {

    private ArtistaDAO artistaDAO = new ArtistaDAO();

    /**
     * Verifica si el nombre del artista ya existe.
     *
     * @param nombre Nombre a validar
     * @return true si el nombre no se repite en la base
     */
    @Override
    public boolean esNombreUnico(String nombre) {

        try {
            String nombreNormalizado = validarCampoNombre(nombre);

            List<ArtistaDTO> artistas = artistaDAO.buscarTodo();
            for (ArtistaDTO a : artistas) {
                String nombreExistenteNormalizado = validarCampoNombre(a.getNombre());
                if (nombreExistenteNormalizado.equals(nombreNormalizado)) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    /**
     * Este método toma un nombre de artista y lo transforma en una versión estandarizada
     * para facilitar la comparación, eliminando diferencias como mayúsculas, espacios y
     * caracteres especiales.
     *
     * @param nombre Nombre ingresado para ser comparado
     * @return El nombre del artista estandarizado.
     */
    private String validarCampoNombre(String nombre) {
        if (nombre == null) {
            return "";
        }
        return nombre.toLowerCase()
                .trim() //Elimina los espacios al inicio y al final
                .replaceAll("\\s+", " ")
                .replaceAll("[^a-z0-9 ]", "")
                .replaceAll(" ", "");

    }
}
