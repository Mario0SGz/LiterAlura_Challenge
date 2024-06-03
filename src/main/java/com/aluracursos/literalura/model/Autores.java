package com.aluracursos.literalura.model;

import com.aluracursos.literalura.principal.Colors;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private int añoNacimiento;
    private int añoMuerte;
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Libros> libros = new ArrayList<>();


    public Autores(DatosAutor datosAutor) {
        this.nombre = datosAutor.nombre();
        this.añoMuerte = datosAutor.fechaMuerte();
        this.añoNacimiento = datosAutor.fechaDeNacimiento();
    }

    public Autores() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAñoNacimiento() {
        return añoNacimiento;
    }

    public void setAñoNacimiento(int añoNacimiento) {
        this.añoNacimiento = añoNacimiento;
    }

    public int getAñoMuerte() {
        return añoMuerte;
    }

    public void setAñoMuerte(int añoMuerte) {
        this.añoMuerte = añoMuerte;
    }

    public List<Libros> getLibros() {
        return libros;
    }

    public void setLibros(List<Libros> libros) {
        this.libros = libros;
    }

    @Override
    public String toString() {
        return Colors.VERDE + "Autor{" + Colors.RESET +
                Colors.AZUL + "Nombre='" + Colors.RESET + Colors.ROJO + nombre + Colors.RESET + '\'' +
                Colors.AZUL + "Fecha de Nacimiento =" + Colors.RESET + Colors.ROJO + añoNacimiento + Colors.RESET +
                Colors.AZUL + "Fecha de Muerte =" + Colors.RESET + Colors.ROJO + (añoMuerte == 0 ? "N/A" : añoMuerte) + Colors.RESET +
                Colors.AZUL + "Libros =" + Colors.RESET + Colors.ROJO + libros + Colors.RESET +
                Colors.VERDE + '}' + Colors.RESET;
    }
}
