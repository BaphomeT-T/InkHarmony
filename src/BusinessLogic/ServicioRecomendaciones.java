package BusinessLogic;

import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.UsuarioDTO;
import java.util.List;

public class ServicioRecomendaciones {
    private final CancionDAO cancionDAO;

    public ServicioRecomendaciones() {
        this(new CancionDAO());
    }
    
    public ServicioRecomendaciones(CancionDAO cancionDAO) {
        this.cancionDAO = cancionDAO;
    }

    public List<CancionDTO> recomendarCanciones(UsuarioDTO usuario,
                                                Genero genero,
                                                boolean estrenos) {
        Recomendador<CancionDTO> cadena = new RecomendadorCanciones(cancionDAO);
        if (usuario != null) {
            cadena = new FiltroUsuario(cadena, usuario);
        }
        if (genero != null) {
            cadena = new FiltroGenero(cadena, genero);
        }
        if (estrenos) {
            cadena = new FiltroEstreno(cadena);
        }
        return cadena.recomendar();
    }
}