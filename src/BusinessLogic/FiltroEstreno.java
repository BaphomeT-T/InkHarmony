package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroEstreno extends FiltroRecomendador<CancionDTO> {
    private static final int DIAS_ESTRENO = 7;

    public FiltroEstreno(Recomendador<CancionDTO> siguiente) {
        super(siguiente);
    }

    @Override
    protected List<CancionDTO> filtrar(List<CancionDTO> canciones) {
        LocalDateTime limite = LocalDateTime.now().minusDays(DIAS_ESTRENO);
        return canciones.stream()
                .filter(c -> c.getFechaRegistro() != null && c.getFechaRegistro().isAfter(limite))
                .collect(Collectors.toList());
    }
}