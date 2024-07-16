package com.challenge.baseliteratura.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String nombre;
    private Integer nacimiento;
    private Integer fallecimiento;
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Libro> libros;

    public Autor(){

    }

    public Autor(DatosAutor datosAutor){
        this.nombre = datosAutor.nombre();
        this.nacimiento = datosAutor.nacimiento();
        this.fallecimiento = datosAutor.fallecimiento();
    }

    public String getNombre() {
        return nombre;
    }


    public Integer getNacimiento() {
        return nacimiento;
    }


    public Integer getFallecimiento() {
        return fallecimiento;
    }


    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        libros.forEach(l -> l.setAutor(this));
        this.libros = libros;
    }

    @Override
    public String toString() {
        return "\n************ Autor ************\n" +
                "id=" + id +
                "Nombre='" + nombre + '\n' +
                "Año de nacimiento=" + nacimiento +
                "Año de fallecimiento=" + fallecimiento +
                "\n***********************************\n";
    }
}