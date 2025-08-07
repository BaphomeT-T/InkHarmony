package BusinessLogic;

import DataAccessComponent.DTO.PlaylistDTO;

/**
 * Servicio que contiene las reglas de validación para objetos {@link PlaylistDTO}.
 * <p>
 * Este servicio permite verificar si los datos de una playlist están completos, si
 * su título y descripción cumplen con los criterios esperados, y si contiene canciones duplicadas.
 */
public class ServicioValidacionPlaylist {

    /**
     * Valida que todos los datos requeridos de la playlist estén completos.
     *
     * @param playlist objeto {@code PlaylistDTO} a validar
     * @return {@code true} si todos los campos obligatorios están completos y válidos; {@code false} en caso contrario
     */
    public boolean validarDatosCompletos(PlaylistDTO playlist) {
        if (playlist == null) return false;

        return validarTitulo(playlist.getTituloPlaylist()) &&
                validarDescripcion(playlist.getDescripcion()) &&
                playlist.getIdPropietario() > 0;
    }

    /**
     * Valida el título de la playlist.
     * El título no debe ser nulo, vacío y debe tener una longitud entre 1 y 100 caracteres.
     *
     * @param titulo cadena con el título a validar
     * @return {@code true} si el título es válido; {@code false} si no cumple los criterios
     */
    public boolean validarTitulo(String titulo) {
        return titulo != null &&
                !titulo.trim().isEmpty() &&
                titulo.length() <= 100 &&
                titulo.length() >= 1;
    }

    /**
     * Valida la descripción de la playlist.
     * La descripción puede ser nula o vacía, pero si existe no debe exceder los 500 caracteres.
     *
     * @param descripcion texto a validar
     * @return {@code true} si la descripción es válida; {@code false} si excede el límite
     */
    public boolean validarDescripcion(String descripcion) {
        // La descripción puede estar vacía, pero si existe debe tener longitud válida
        return descripcion == null || descripcion.length() <= 500;
    }

    /**
     * Verifica si una playlist contiene canciones duplicadas.
     *
     * @param playlist objeto {@code PlaylistDTO} a analizar
     * @return {@code true} si hay canciones duplicadas; {@code false} si todas son únicas o la lista es nula
     */
    public boolean tieneDuplicados(PlaylistDTO playlist) {
        if (playlist == null || playlist.getCancionesIds() == null) {
            return false;
        }

        return playlist.getCancionesIds().size() !=
                playlist.getCancionesIds().stream().distinct().count();
    }
}
