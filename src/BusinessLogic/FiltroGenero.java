package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroGenero extends FiltroRecomendador<CancionDTO> {
    private final Genero genero;

    public FiltroGenero(Recomendador<CancionDTO> siguiente, Genero genero) {
        super(siguiente);
        this.genero = genero;
    }

    @Override
    protected List<CancionDTO> filtrar(List<CancionDTO> canciones) {
        return canciones.stream()
                .filter(c -> c.getGeneros() != null && c.getGeneros().contains(genero))
                .collect(Collectors.toList());
    }
}