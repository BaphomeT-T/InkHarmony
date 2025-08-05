package BusinessLogic;

import DataAccessComponent.DTO.PlaylistDTO;

/**
 * Servicio para validar datos de playlists.
 */
public class ServicioValidacionPlaylist {

    /**
     * Valida que los datos de la playlist estén completos.
     */
    public boolean validarDatosCompletos(PlaylistDTO playlist) {
        if (playlist == null) return false;

        return validarTitulo(playlist.getTituloPlaylist()) &&
                validarDescripcion(playlist.getDescripcion()) &&
                playlist.getIdPropietario() > 0;
    }

    /**
     * Valida el título de la playlist.
     */
    public boolean validarTitulo(String titulo) {
        return titulo != null &&
                !titulo.trim().isEmpty() &&
                titulo.length() <= 100 &&
                titulo.length() >= 1;
    }

    /**
     * Valida la descripción de la playlist.
     */
    public boolean validarDescripcion(String descripcion) {
        // La descripción puede estar vacía, pero si existe debe tener longitud válida
        return descripcion == null || descripcion.length() <= 500;
    }

    /**
     * Verifica si una playlist tiene canciones duplicadas.
     */
    public boolean tieneDuplicados(PlaylistDTO playlist) {
        if (playlist == null || playlist.getCancionesIds() == null) {
            return false;
        }

        return playlist.getCancionesIds().size() !=
                playlist.getCancionesIds().stream().distinct().count();
    }
}