package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroArtista extends FiltroRecomendador {

    private final String nombreArtista;

    public FiltroArtista(Recomendador siguiente, String nombreArtista) {
        super(siguiente);
        this.nombreArtista = nombreArtista.toLowerCase();
    }

    @Override
    public List<CancionDTO> recomendar() {
        return siguiente.recomendar().stream()
                .filter(c -> c.getArtistas() != null &&
                             c.getArtistas().stream()
                                 .anyMatch(a -> a.getNombre().toLowerCase().contains(nombreArtista)))
                .collect(Collectors.toList());
    }
}
