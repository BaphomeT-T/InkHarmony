package BusinessLogic;

import DataAccessComponent.DTO.CatalogoArtistas.ArtistaDTO;

public interface AsociacionValidable {

    boolean tieneElementosAsociados(ArtistaDTO artista);

}
