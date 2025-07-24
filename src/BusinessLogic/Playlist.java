package BusinessLogic;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Date;

public class Playlist implements ComponentePlaylist {
    private int idPlaylist;
    private String tituloPlaylist;
    private String descripcion;
    private Date fechaCreacion;
    private Date fechaModificacion;
    private Perfil propietario;
    private BufferedImage imagenPortada;
    private List<ComponentePlaylist> componentes;

    public Playlist(String titulo, String descripcion, Perfil propietario) {
        this.tituloPlaylist = titulo;
        this.descripcion = descripcion;
        this.propietario = propietario;
        this.fechaCreacion = new Date();
        this.fechaModificacion = new Date();
        this.componentes = new ArrayList<>();
    }

    public Playlist(int id, String titulo, String descripcion, Perfil propietario,
                    Date fechaCreacion, Date fechaModificacion) {
        this.idPlaylist = id;
        this.tituloPlaylist = titulo;
        this.descripcion = descripcion;
        this.propietario = propietario;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
        this.componentes = new ArrayList<>();
    }

    public int getIdPlaylist() { return idPlaylist; }
    public void setIdPlaylist(int idPlaylist) { this.idPlaylist = idPlaylist; }
    public String getTitulo() { return tituloPlaylist; }
    public void setTitulo(String titulo) {
        this.tituloPlaylist = titulo;
        this.fechaModificacion = new Date();
    }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
        this.fechaModificacion = new Date();
    }
    public Perfil getPropietario() { return propietario; }
    public Date getFechaCreacion() { return fechaCreacion; }
    public Date getFechaModificacion() { return fechaModificacion; }
    public BufferedImage getImagenPortada() { return imagenPortada; }
    public void setImagenPortada(BufferedImage imagenPortada) {
        this.imagenPortada = imagenPortada;
        this.fechaModificacion = new Date();
    }

    public int calcularCantidadCanciones() {
        int contador = 0;
        for (ComponentePlaylist componente : componentes) {
            if (componente instanceof ElementoCancion) {
                contador++;
            } else if (componente instanceof Playlist) {
                contador += ((Playlist) componente).calcularCantidadCanciones();
            }
        }
        return contador;
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("=== PLAYLIST ===");
        System.out.println("Título: " + tituloPlaylist);
        System.out.println("Descripción: " + descripcion);
        System.out.println("Propietario: " + propietario.getNombre());
        System.out.println("Canciones: " + calcularCantidadCanciones());
        System.out.println("Duración total: " + obtenerDuracion() + " min");
        System.out.println("Fecha creación: " + fechaCreacion);
        System.out.println("Componentes:");
        for (ComponentePlaylist componente : componentes) {
            componente.mostrarInformacion();
        }
    }

    @Override
    public double obtenerDuracion() {
        double duracionTotal = 0;
        for (ComponentePlaylist componente : componentes) {
            duracionTotal += componente.obtenerDuracion();
        }
        return duracionTotal;
    }

    @Override
    public void agregar(ComponentePlaylist componente) {
        if (componente != null && !componentes.contains(componente)) {
            componentes.add(componente);
            fechaModificacion = new Date();
        }
    }

    @Override
    public void eliminar(ComponentePlaylist componente) {
        if (componentes.remove(componente)) {
            fechaModificacion = new Date();
        }
    }

    @Override
    public List<ComponentePlaylist> getComponentes() {
        return new ArrayList<>(componentes);
    }
}
