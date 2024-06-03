package com.aluracursos.literalura.model;

import com.aluracursos.literalura.principal.Colors;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "libros")
public class Libros {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String titulo;
    private String nombreAutor;
    private List<String> idiomas;
    private double numeroDescargas;
    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private Autores autor;


    public Libros() {

    }

    public Libros(DatosLibros datosLibros, Autores autor) {
        this.nombreAutor = autor.getNombre();
        this.idiomas = datosLibros.idiomas();
        this.numeroDescargas = datosLibros.numeroDeDescargas();
        this.titulo = datosLibros.titulo();
        this.autor = autor;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = idiomas;
    }

    public double getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(double numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public Autores getAutor() {
        return autor;
    }

    public void setAutor(Autores autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return Colors.VERDE + "Libro {" + Colors.RESET +
                Colors.AZUL + "Titulo ='" + Colors.RESET + Colors.ROJO + titulo + Colors.RESET + '\'' +
                Colors.AZUL + ", Autor ='" + Colors.RESET + Colors.ROJO + nombreAutor + Colors.RESET + '\'' +
                Colors.AZUL + ", Idomas ='" + Colors.RESET + Colors.ROJO + String.join(", ", idiomas) + Colors.RESET + '\'' +
                Colors.AZUL + ", Descargas =" + Colors.RESET + Colors.ROJO + numeroDescargas + Colors.RESET +
                Colors.VERDE + '}' + Colors.RESET;
    }
}
