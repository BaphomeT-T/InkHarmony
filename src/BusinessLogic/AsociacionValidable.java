package BusinessLogic;

import DataAccessComponent.DTO.ArtistaDTO;

public interface AsociacionValidable {

    boolean tieneElementosAsociados(ArtistaDTO artista);

}
