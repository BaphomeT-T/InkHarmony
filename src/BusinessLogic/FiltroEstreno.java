package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroEstreno extends FiltroRecomendador {

    private final int diasEstreno = 7;

    public FiltroEstreno(Recomendador siguiente) {
        super(siguiente);
    }

    @Override
    public List<CancionDTO> recomendar() {
        LocalDateTime limite = LocalDateTime.now().minusDays(diasEstreno);
        return siguiente.recomendar().stream()
                .filter(c -> c.getFechaRegistro() != null &&
                             c.getFechaRegistro().isAfter(limite))
                .collect(Collectors.toList());
    }
}
