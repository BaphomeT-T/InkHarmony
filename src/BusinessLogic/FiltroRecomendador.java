package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;

public abstract class FiltroRecomendador implements Recomendador {

    protected final Recomendador siguiente;

    protected FiltroRecomendador(Recomendador siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public List<CancionDTO> recomendar() {
        return siguiente.recomendar();
    }
}
