package BusinessLogic;

import DataAccessComponent.DAO.UsuarioDAO;
import DataAccessComponent.DTO.*;
import java.util.*;
import java.util.stream.Collectors;

public class FiltroPreferencias extends FiltroRecomendador {

    private final Set<Genero> preferidos;

    public FiltroPreferencias(Recomendador siguiente) {
        this(siguiente, Sesion.getSesion().obtenerUsuarioActual());
    }

    public FiltroPreferencias(Recomendador siguiente, PerfilDTO perfil) {
        super(siguiente);

        if (perfil == null) {
            this.preferidos = Set.of();
            return;
        }

        List<GeneroDTO> generosDTO;

        if (perfil instanceof UsuarioDTO u && u.getPreferenciasMusicales() != null)
            generosDTO = u.getPreferenciasMusicales();
        else
            generosDTO = new UsuarioDAO().obtenerPreferencias(perfil);

        this.preferidos = generosDTO.stream()
                                    .map(GeneroDTO::getNombreGenero)
                                    .map(Genero::valueOf)
                                    .collect(Collectors.toSet());
    }

    @Override
    public List<CancionDTO> recomendar() {
        if (preferidos.isEmpty())
            return siguiente.recomendar();
        return siguiente.recomendar().stream()
                .filter(c -> c.getGeneros() != null &&
                             c.getGeneros().stream().anyMatch(preferidos::contains))
                .collect(Collectors.toList());
    }
}
