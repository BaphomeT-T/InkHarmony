package BusinessLogic;

import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import java.util.List;

public class RecomendadorCanciones implements Recomendador<CancionDTO> {
    private final CancionDAO cancionDAO;

    public RecomendadorCanciones(CancionDAO cancionDAO) {
        this.cancionDAO = cancionDAO;
    }

    @Override
    public List<CancionDTO> recomendar() {
        try {
            return cancionDAO.buscarTodo();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}