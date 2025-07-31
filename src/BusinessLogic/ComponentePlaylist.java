package BusinessLogic;

import java.util.*;

public interface ComponentePlaylist {
    void mostrarInformacion();
    double obtenerDuracion();
    void agregar(ComponentePlaylist componente);
    void eliminar(ComponentePlaylist componente);
    List<ComponentePlaylist> getComponentes();
}