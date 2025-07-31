package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.UsuarioDTO;
import java.util.List;

/**
 * Clase de lógica de negocio que gestiona las recomendaciones musicales personalizadas en InkHarmony.
 * <p>
 * Permite generar listas de canciones recomendadas a partir de los géneros preferidos del usuario,
 * filtrando por tipo de género, usuario autenticado y canciones recientemente añadidas al catálogo.
 * </p>
 *
 * @author Grupo E - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class ServicioRecomendaciones {
    
    /**
     * Genera una lista de canciones recomendadas en base al usuario actual, un género específico,
     * y la opción de mostrar solo estrenos recientes.
     *
     * @param usuario  Usuario autenticado cuyas preferencias serán tomadas en cuenta (puede ser null)
     * @param genero   Género musical específico a filtrar (puede ser null para no filtrar)
     * @param estrenos true si se desea mostrar solo canciones añadidas recientemente
     * @return Lista de canciones recomendadas según los filtros aplicados
     */
    public List<CancionDTO> recomendarCanciones(UsuarioDTO usuario,
                                                Genero genero,
                                                boolean estrenos) {
        Recomendador<CancionDTO> cadena = new RecomendadorCanciones();

        if (usuario != null)  cadena = new FiltroUsuario(cadena, usuario);
        if (genero  != null)  cadena = new FiltroGenero(cadena, genero);
        if (estrenos)        cadena = new FiltroEstreno(cadena);

        return cadena.recomendar();
    }
}