package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro que restringe las canciones recomendadas a aquellas que coincidan
 * parcialmente con un nombre de artista especificado.
 * 
 * <p>Forma parte de la cadena de filtros basada en el patrón Decorator.
 * Este filtro buscará coincidencias parciales en los nombres de artistas,
 * sin importar mayúsculas o minúsculas.</p>
 *
 * @author Grupo F - InkHarmony Team
 */
public class FiltroArtista extends FiltroRecomendador {

    /** Nombre del artista ingresado por el usuario en minúsculas */
    private final String nombreArtista;

    /**
     * Crea un filtro de artista que se encadena al recomendador recibido.
     *
     * @param siguiente el recomendador al que se delega si este filtro no aplica
     * @param nombreArtista nombre del artista o fragmento ingresado por el usuario
     */
    public FiltroArtista(Recomendador siguiente, String nombreArtista) {
        super(siguiente);
        this.nombreArtista = nombreArtista.toLowerCase();
    }

    /**
     * Devuelve la lista de canciones cuya lista de artistas contiene al menos
     * uno cuyo nombre coincide parcialmente con el texto ingresado.
     *
     * @return lista filtrada de canciones que cumplen con el criterio de artista
     */
    @Override
    public List<CancionDTO> recomendar() {
        return siguiente.recomendar().stream()
                .filter(c -> c.getArtistas() != null &&
                             c.getArtistas().stream()
                                 .anyMatch(a -> a.getNombre().toLowerCase().contains(nombreArtista)))
                .collect(Collectors.toList());
    }
}
