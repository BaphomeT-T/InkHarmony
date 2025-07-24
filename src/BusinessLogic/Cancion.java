package BusinessLogic;

// Stub de cancion
class Cancion {

    private int idCancion;
    private String tituloCancion;
    private double duracion;

    public Cancion(int id, String titulo, double duracion) {
        this.idCancion = id;
        this.tituloCancion = titulo;
        this.duracion = duracion;
    }

    public int getIdCancion() { return idCancion; }
    public String getTituloCancion() { return tituloCancion; }
    public double getDuracion() { return duracion; }
}
