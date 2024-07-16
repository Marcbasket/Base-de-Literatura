package com.challenge.baseliteratura.principal;

import com.challenge.baseliteratura.model.*;
import com.challenge.baseliteratura.repository.AutorRepository;
import com.challenge.baseliteratura.service.ConsumirAPI;
import com.challenge.baseliteratura.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal{

    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumirAPI consumirAPI = new ConsumirAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    private AutorRepository repository;

public Principal(AutorRepository repository) {this.repository = repository;}

    public void muestraElMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    ******************************************************
                                        MENU PRINCIPAL
                    ******************************************************
                        Bienvenido, Elija la opción deseada:
                        1.- Buscar libro por título
                        2.- Buscar autor por nombre
                        3.- Listar libros registrados
                        4.- Listar autores registrados
                        5.- Listar autores que vivieron en determinado año
                        6.- Top 10 libros más descargados
                    
                    ******************************************************
                               Presione 0 para cerrar applicación
                    ******************************************************
                    
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    buscarAutor();
                    break;
                case 3:
                    listarLibros();
                    break;
                case 4:
                    listarAutores();
                    break;
                case 5:
                    listarAutoresOrdenados();
                    break;
                case 6:
                    top10Libros();
                    break;

                case 0:
                    System.out.println("Cerrando la aplicacion...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }

        }
    }

    public void buscarLibroPorTitulo() {
        System.out.println("Introduzca el nombre del libro que desea buscar:");
        var nombre = teclado.nextLine();
        var json = consumirAPI.obtenerDatos(URL_BASE + "?search=" + nombre.replace(" ", "%20"));

        if (json.isEmpty() || !json.contains("\"count\":0,\"next\":null,\"previous\":null,\"results\":[]")) {
            var datos = conversor.obtenerDatos(json, ResultadosGenerales.class);
            Optional<DatosLibros> libroBuscado = datos.libros().stream()
                    .findFirst();
            System.out.println(
                    "\nTítulo: " + libroBuscado.get().titulo() +
                            "\nAutor: " + libroBuscado.get().autores().stream()
                            .map(a -> a.nombre()).limit(1).collect(Collectors.joining()) +
                            "\nIdioma: " + libroBuscado.get().idiomas().stream().collect(Collectors.joining()) +
                            "\nNúmero de descargas: " + libroBuscado.get().descargas()
            );

            try {
                List<Libro> libroEncontrado = libroBuscado.stream().map(a -> new Libro(a)).collect(Collectors.toList());
                Autor autorAPI = libroBuscado.stream().
                            flatMap(l -> l.autores().stream()
                                    .map(a -> new Autor(a)))
                            .collect(Collectors.toList()).stream().findFirst().get();
                Optional<Autor> autorBD = repository.buscarAutor(libroBuscado.get().autores().stream()
                            .map(a -> a.nombre())
                            .collect(Collectors.joining()));
                Optional<Libro> libroOptional = repository.buscarLibroPorNombre(nombre);
                if (libroOptional.isPresent()) {
                    System.out.println("El libro ya está guardado en nuestra base de datos");
                } else {
                    Autor autor;
                    if (autorBD.isPresent()) {
                            autor = autorBD.get();
                            System.out.println("EL autor ya esta guardado en nuestra base de datos ");
                    } else {
                            autor = autorAPI;
                            repository.save(autor);
                    }
                    autor.setLibros(libroEncontrado);
                    repository.save(autor);
                }
            } catch (Exception e) {
                System.out.println("Error" + e.getMessage());
            }
        } else{
            System.out.println("Libro no encontrado.");
        }
    }

    private void listarLibros() {
        List<Libro> libros = repository.buscarTodosLosLibros();
        libros.forEach(l -> System.out.println(
                        "\nTítulo: " + l.getTitulo() +
                        "\nAutor: " + l.getAutor().getNombre() +
                        "\nIdioma: " + l.getIdioma().getIdioma() +
                        "\nNúmero de descargas: " + l.getDescargas()));
    }

    private void listarAutores() {
        List<Autor> autores = repository.findAll();
        System.out.println();
        autores.forEach(l -> System.out.println(
                "Autor: " + l.getNombre() +
                        "\nFecha de Nacimiento: " + l.getNacimiento() +
                        "\nFecha de Fallecimiento: " + l.getFallecimiento()+"\n"
        ));
    }

    private void listarAutoresOrdenados() {
        System.out.println("¿Qué año desea consultar?");
        try {
            var fecha = Integer.valueOf(teclado.nextLine());
            List<Autor> autores = repository.buscarAutoresVivos(fecha);
            if (!autores.isEmpty()) {
                autores.forEach(a -> System.out.println(
                        "Autor: " + a.getNombre() +
                                "\nFecha de Nacimiento: " + a.getNacimiento() +
                                "\nFecha de Fallecimiento: " + a.getFallecimiento()
                ));
            } else {
                System.out.println("No encontramos ningun autor(a) vivo durante ese año en nuestra base de datos");
            }
        } catch (NumberFormatException e) {
            System.out.println("Año inválido" + e.getMessage());
        }
    }

    private void top10Libros() {
        System.out.println("Top 10 libros más descargados");
        var json = consumirAPI.obtenerDatos(URL_BASE);
        var datosBusqueda = conversor.obtenerDatos(json, ResultadosGenerales.class);
        datosBusqueda.libros().stream()
                .sorted(Comparator.comparing(DatosLibros::descargas).reversed())
                .limit(10)
                .map(DatosLibros::titulo)
                .forEach(System.out::println);
    }

    private void buscarAutor() {
        System.out.println("Ingrese el nombre del autor que deseas buscar:");
        var nombre = teclado.nextLine();
        Optional<Autor> autor = repository.buscarAutor(nombre);
        if (autor.isPresent()) {
            System.out.println("Autor: " + autor.get().getNombre() +
                            "\nFecha de Nacimiento: " + autor.get().getNacimiento() +
                            "\nFecha de Fallecimiento: " + autor.get().getFallecimiento() +
                            "\nLibros: " + autor.get().getLibros().stream()
                            .map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
            );
        } else {
            System.out.println("El autor no aparece aun en la base de datos");
        }
        
    }
}
