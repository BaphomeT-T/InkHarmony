package BusinessLogic;

import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import java.util.List;

public class RecomendadorCanciones implements Recomendador {

    private final CancionDAO cancionDAO = new CancionDAO();

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
