package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.UsuarioDTO;
import DataAccessComponent.DTO.GeneroDTO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FiltroUsuario extends FiltroRecomendador<CancionDTO> {
    private final Set<Genero> preferidos;

    public FiltroUsuario(Recomendador<CancionDTO> siguiente, UsuarioDTO usuario) {
        super(siguiente);
        // Convertir GeneroDTO → Genero (enum) según nombre
        this.preferidos = usuario.getPreferenciasMusicales().stream()
                .map(GeneroDTO::getNombreGenero)
                .map(nombre -> {
                    try {
                        return Genero.valueOf(nombre);
                    } catch (IllegalArgumentException e) {
                        return null; // género inexistente en enum
                    }
                })
                .filter(g -> g != null)
                .collect(Collectors.toSet());
    }

    @Override
    protected List<CancionDTO> filtrar(List<CancionDTO> canciones) {
        return canciones.stream()
                .filter(c -> c.getGeneros() != null &&
                        c.getGeneros().stream().anyMatch(preferidos::contains))
                .collect(Collectors.toList());
    }
}