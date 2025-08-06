package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;

public class ServicioRecomendaciones {

    public List<CancionDTO> recomendar(boolean usarPreferencias,
                                       Genero genero,
                                       String artista,
                                       boolean estrenos) {

        Recomendador cadena = new RecomendadorCanciones();

        if (usarPreferencias)                  cadena = new FiltroPreferencias(cadena);
        if (genero != null)                    cadena = new FiltroGenero(cadena, genero);
        if (artista != null && !artista.isBlank())
                                              cadena = new FiltroArtista(cadena, artista);
        if (estrenos)                          cadena = new FiltroEstreno(cadena);

        return cadena.recomendar();
    }
}
