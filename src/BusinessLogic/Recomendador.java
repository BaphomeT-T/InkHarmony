package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;

public interface Recomendador {
    List<CancionDTO> recomendar();
}
