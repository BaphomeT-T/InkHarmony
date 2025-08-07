package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;

/**
 * Servicio central encargado de construir y ejecutar la cadena de filtros
 * para generar recomendaciones personalizadas de canciones.
 *
 * <p>Utiliza el patrón Decorator para aplicar distintos filtros en función
 * de los parámetros proporcionados.</p>
 *
 * @author Grupo F - InkHarmony Team
 */
public class ServicioRecomendaciones {

    /**
     * Generará una lista de canciones recomendadas aplicando los filtros
     * correspondientes según los parámetros proporcionados.
     *
     * @param usarPreferencias si se debe aplicar el filtro basado en preferencias del usuario
     * @param genero género específico a filtrar (puede ser null)
     * @param artista nombre del artista a filtrar (puede ser null o vacío)
     * @param estrenos si se debe incluir solo canciones recientes
     * @return lista filtrada de canciones recomendadas
     */
    public List<CancionDTO> recomendar(boolean usarPreferencias,
                                       Genero genero,
                                       String artista,
                                       boolean estrenos) {

        Recomendador cadena = new RecomendadorCanciones();

        if (usarPreferencias)
            cadena = new FiltroPreferencias(cadena);
        if (genero != null)
            cadena = new FiltroGenero(cadena, genero);
        if (artista != null && !artista.isBlank())
            cadena = new FiltroArtista(cadena, artista);
        if (estrenos)
            cadena = new FiltroEstreno(cadena);

        return cadena.recomendar();
    }
}
