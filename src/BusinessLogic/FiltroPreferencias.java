package BusinessLogic;

import DataAccessComponent.DAO.UsuarioDAO;
import DataAccessComponent.DTO.*;
import java.util.*;
import java.util.stream.Collectors;

public class FiltroPreferencias extends FiltroRecomendador {

    private final Set<Genero> preferidos;

    public FiltroPreferencias(Recomendador siguiente) {
        super(Objects.requireNonNull(siguiente, "El filtro siguiente no puede ser null"));

        PerfilDTO perfil = Sesion.getSesion().obtenerUsuarioActual();
        if (perfil == null) {
            throw new IllegalStateException(
                "No hay usuario en sesión: no se pueden cargar preferencias");
        }

        List<GeneroDTO> generosDTO;
        if (perfil instanceof UsuarioDTO u && u.getPreferenciasMusicales() != null) {
            generosDTO = u.getPreferenciasMusicales();
        } else {
            generosDTO = new UsuarioDAO().obtenerPreferencias(perfil);
        }

        this.preferidos = generosDTO.stream()
                                    .map(GeneroDTO::getNombreGenero)
                                    .map(Genero::valueOf)
                                    .collect(Collectors.toSet());
    }

    @Override
    public List<CancionDTO> recomendar() {
        if (preferidos.isEmpty()) {
            return siguiente.recomendar();
        }
        return siguiente.recomendar().stream()
                       .filter(c -> c.getGeneros() != null &&
                                    c.getGeneros().stream().anyMatch(preferidos::contains))
                       .collect(Collectors.toList());
    }
}
