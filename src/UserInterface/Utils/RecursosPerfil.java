package UserInterface.Utils;

import java.util.List;

public class RecursosPerfil {

    private static final List<String> RUTAS_IMAGENES = List.of(   
        "/UserInterface/Resources/img/Perfil/perfilH1.jpg", 
        "/UserInterface/Resources/img/Perfil/perfilH2.jpg",
        "/UserInterface/Resources/img/Perfil/perfilH3.jpg",
        "/UserInterface/Resources/img/Perfil/perfilM1.jpg",
        "/UserInterface/Resources/img/Perfil/perfilM2.jpg",
        "/UserInterface/Resources/img/Perfil/perfilM3.jpg"
    );

    /**
     * Devuelve la lista completa de rutas de imágenes de perfil.
     */
    public static List<String> obtenerRutasImagenes() {
        return RUTAS_IMAGENES;
    }

    /**
     * Devuelve la ruta de imagen correspondiente al índice especificado.
     * Si el índice no es válido, devuelve la imagen por defecto (índice 0).
     */
    public static String obtenerImagenPorIndice(int indice) {
        if (indice >= 0 && indice < RUTAS_IMAGENES.size()) {
            return RUTAS_IMAGENES.get(indice);
        } else {
            return RUTAS_IMAGENES.get(0); // por defecto
        }
    }

    /**
     * Devuelve la cantidad total de imágenes disponibles.
     */
    public static int totalImagenes() {
        return RUTAS_IMAGENES.size();
    }
}
