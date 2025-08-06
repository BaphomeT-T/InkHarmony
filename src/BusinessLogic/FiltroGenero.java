package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroGenero extends FiltroRecomendador {

    private final Genero genero;

    public FiltroGenero(Recomendador siguiente, Genero genero) {
        super(siguiente);
        this.genero = genero;
    }

    @Override
    public List<CancionDTO> recomendar() {
        return siguiente.recomendar().stream()
                .filter(c -> c.getGeneros() != null && c.getGeneros().contains(genero))
                .collect(Collectors.toList());
    }
}
