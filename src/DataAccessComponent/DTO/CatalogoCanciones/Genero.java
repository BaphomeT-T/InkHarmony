package DataAccessComponent.DTO.CatalogoCanciones;

public enum Genero {
    GAME_OST(1),
    ROCK(2),
    ROCK_ALTERNATIVO(3),
    VALS(4),
    TANGO(5),
    REGIONAL_MEXICANA(6),
    RAP(7),
    CLASICA(8),
    MOVIE_OST(9),
    JAZZ(10),
    COUNTRY(11),
    POP(12),
    REGGAETON(13),
    TRAP_LATINO(14),
    TRAP(15),
    K_POP(16),
    BOLERO(17),
    PASILLO(18);

    private final int id;

    Genero(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
