package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.IAutoresRepository;
import com.aluracursos.literalura.repository.ILibrosRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Principal {
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    //Constantes
    private final String URL_BASE = "https://gutendex.com/books/?search=";

    private IAutoresRepository autoresRepository;
    private ILibrosRepository librosRepository;

    public Principal(ILibrosRepository libroRepository, IAutoresRepository autoresRepository) {
        this.librosRepository = libroRepository;
        this.autoresRepository = autoresRepository;
    }

    public void muestraMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = Colors.NEGRITA + Colors.VERDE + "\n" + """
             1 - | Buscar libros por su título |
             2 - | Mostrar todos los libros registrados |
             3 - | Mostrar todos los autores registrados |
             4 - | Mostrar autores vivos en un año específico |
             5 - | Mostrar libros por idioma |
             6 - | Mostrar el Top 10 de libros más descargados |
             7 - | Mostrar estadísticas de la biblioteca |
             8 - | Buscar autor por su nombre |
             9 - | Mostrar autores por año de nacimiento |
            10 - | Mostrar autores por año de fallecimiento |
             0 - | Salir del programa |
            """ + Colors.RESET;
            System.out.println(menu);

            while (!teclado.hasNextInt()) {
                System.out.println(Colors.NEGRITA + Colors.ROJO + "Por favor ingresa un número válido." + Colors.RESET);
                teclado.next(); // Descartar la entrada no válida
            }
            opcion = teclado.nextInt();
            teclado.nextLine(); // Consumir la nueva línea pendiente en el buffer de entrada

            switch (opcion) {
                case 1:
                    buscarLibrosPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    filtrarPorAñoAutores();
                    break;
                case 5:
                    filtrarPorIdioma();
                    break;
                case 6:
                    top10Descargas();
                    break;
                case 7:
                    obtenerEstadisticas();
                    break;
                case 8:
                    buscarAutorPorNombre();
                    break;
                case 9:
                    listarAutoresPorAñoNacimiento();
                    break;
                case 10:
                    listarAutoresPorAñoMuerte();
                    break;
                case 0:
                    System.out.println(Colors.NEGRITA + Colors.ROJO + "Cerrando el programa!!!" + Colors.RESET);
                    break;
                default:
                    System.out.println(Colors.NEGRITA + Colors.ROJO + "Opción inválida" + Colors.RESET);
            }
        }
    }



    private Datos obtenerDatos(String query) {
        String busquedaCodificada = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String json = consumoApi.obtenerDatos(URL_BASE + busquedaCodificada);
        return convierteDatos.obtenerDatos(json, Datos.class);
    }

    private void buscarLibrosPorTitulo() {
        System.out.println("Ingresa el nombre del libro a buscar: ");
        String nombreLibro = teclado.nextLine();
        Datos datosLibro = obtenerDatos(nombreLibro);

        if (datosLibro == null || datosLibro.resultados().isEmpty()) {
            System.out.println(Colors.ROJO + "No se encontraron libros con ese título." + Colors.RESET);
            return;
        }

        // Creamos un mapa para almacenar los resultados únicos por título y autor
        Map<String, DatosLibros> librosUnicos = new HashMap<>();
        datosLibro.resultados().forEach(libro -> {
            String clave = libro.titulo() + "-" + libro.autor().get(0).nombre();
            if (!librosUnicos.containsKey(clave) || libro.numeroDeDescargas() > librosUnicos.get(clave).numeroDeDescargas()) {
                librosUnicos.put(clave, libro);
            }
        });

        // Iteramos sobre los libros únicos y los registramos en la base de datos
        librosUnicos.values().forEach(libro -> {
            Optional<Autores> optionalAutor = autoresRepository.findByNombre(libro.autor().get(0).nombre());
            Autores autor;
            if (optionalAutor.isPresent()) {
                autor = optionalAutor.get();
            } else {
                autor = new Autores(libro.autor().get(0));
                autor = autoresRepository.save(autor);
            }

            Libros libroEntity = new Libros(libro, autor);
            librosRepository.save(libroEntity);

            // Mostrar el libro registrado
            System.out.println(Colors.VERDE + "Libro registrado: " + Colors.RESET);
            System.out.println(libroEntity);
        });
    }


    private void listarLibrosRegistrados() {
        List<Libros> libros = librosRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados");
            return;
        }
        System.out.println(Colors.NEGRITA + Colors.AZUL + "----- LIBROS REGISTRADOS: -----\n" + Colors.RESET);
        libros.stream()
                .sorted(Comparator.comparing(Libros::getTitulo))
                .forEach(libro -> System.out.println(Colors.NEGRITA + Colors.ROJO + libro.getTitulo()));
    }

    private void listarAutoresRegistrados() {
        List<Autores> autores = autoresRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados en la base de datos.");
            return;
        }
        System.out.println(Colors.NEGRITA + Colors.AZUL + "----- AUTORES REGISTRADOS: -----\n" + Colors.RESET);
        autores.stream()
                .sorted(Comparator.comparing(Autores::getNombre))
                .forEach(autor -> System.out.println(Colors.NEGRITA + Colors.ROJO + autor.getNombre()));
    }

    private void filtrarPorAñoAutores() {
        System.out.println("Ingresa el año para filtrar autores vivos: ");
        while (!teclado.hasNextInt()) {
            System.out.println(Colors.NEGRITA + Colors.ROJO + "Por favor ingresa un año válido." + Colors.RESET);
            teclado.next(); // Descartar la entrada no válida
        }
        int año = teclado.nextInt();
        teclado.nextLine(); // Consumir la nueva línea pendiente en el buffer de entrada

        List<Autores> autoresVivos = autoresRepository.findByAñoNacimientoLessThanEqualAndAñoMuerteGreaterThanEqual(año, año);

        if (!autoresVivos.isEmpty()) {
            System.out.println("Autores vivos en el año " + año + ":");
            autoresVivos.stream()
                    .forEach(autores -> System.out.println(Colors.NEGRITA + Colors.AMARILLO + autores.getNombre() + Colors.RESET));
        } else {
            System.out.println("No hay autores vivos en el año " + año);
        }
    }


    private void filtrarPorIdioma() {
        System.out.println("""
            en -> Ingles
            es -> Español
            """);
        System.out.println("Ingresa el idioma para filtrar los libros: ");
        String idioma = teclado.nextLine().toLowerCase();

        if (!idioma.equals("en") && !idioma.equals("es")) {
            System.out.println(Colors.ROJO + "Idioma no soportado. Solo se permiten 'en' para Inglés y 'es' para Español." + Colors.RESET);
            return;
        }

        List<Libros> librosPorIdioma = librosRepository.findByIdiomasIn(Collections.singletonList(idioma));

        if (!librosPorIdioma.isEmpty()) {
            System.out.println(Colors.NEGRITA + Colors.AMARILLO + "Los libros encontrados en el idioma " + idioma + " son :" + Colors.RESET);
            librosPorIdioma.forEach(libro -> {
                System.out.println(Colors.NEGRITA + Colors.AZUL + libro.getTitulo() + " - " + libro.getNombreAutor() + Colors.RESET);
            });
        } else {
            System.out.println("No se encontraron libros en el idioma " + idioma);
        }
    }


    private void top10Descargas() {
        List<Libros> librosMasDescargados = librosRepository.obtener10MasDescargados();
        if (librosMasDescargados.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        System.out.println(Colors.NEGRITA + Colors.AZUL + "----- TOP 10 LIBROS MÁS DESCARGADOS: -----\n" + Colors.RESET);
        librosMasDescargados.stream()
                .limit(10)
                .forEach(libro -> System.out.println(Colors.NEGRITA + Colors.ROJO + libro.getTitulo() + " - " + libro.getNumeroDescargas() + " descargas"));
    }

    private void obtenerEstadisticas() {
        long totalLibros = librosRepository.contarTotalLibros();
        long totalAutores = autoresRepository.contarTotalAutores();
        double promedioDescargas = librosRepository.promedioDescargasPorLibro();

        System.out.println(Colors.NEGRITA + Colors.AZUL + "----- ESTADÍSTICAS: -----" + Colors.RESET);
        System.out.println(Colors.NEGRITA + "Total de libros registrados: " + Colors.RESET + Colors.ROJO + totalLibros + Colors.RESET);
        System.out.println(Colors.NEGRITA + "Total de autores registrados: " + Colors.RESET + Colors.ROJO + totalAutores + Colors.RESET);
        System.out.println(Colors.NEGRITA + "Promedio de descargas por libro: " + Colors.RESET + Colors.ROJO + promedioDescargas + Colors.RESET);
    }

    private void buscarAutorPorNombre() {
        System.out.println("Ingresa el nombre del autor a buscar: ");
        String nombreAutor = teclado.nextLine();
        Optional<Autores> autor = autoresRepository.findByNombre(nombreAutor);

        if (autor.isPresent()) {
            System.out.println(Colors.NEGRITA + Colors.AZUL + "----- AUTOR ENCONTRADO: -----" + Colors.RESET);
            System.out.println(Colors.NEGRITA + Colors.ROJO + autor.get() + Colors.RESET);
        } else {
            System.out.println(Colors.ROJO + "No se encontró un autor con ese nombre." + Colors.RESET);
        }
    }


    private void listarAutoresPorAñoNacimiento() {
        System.out.println("Ingresa el año de nacimiento: ");
        while (!teclado.hasNextInt()) {
            System.out.println(Colors.NEGRITA + Colors.ROJO + "Por favor ingresa un año válido." + Colors.RESET);
            teclado.next(); // Descartar la entrada no válida
        }
        int añoNacimiento = teclado.nextInt();
        teclado.nextLine(); // Consumir la nueva línea pendiente en el buffer de entrada

        List<Autores> autores = autoresRepository.findByAñoNacimiento(añoNacimiento);

        if (!autores.isEmpty()) {
            System.out.println("Autores nacidos en el año " + añoNacimiento + ":");
            autores.forEach(autor -> System.out.println(Colors.NEGRITA + Colors.AMARILLO + autor.getNombre() + Colors.RESET));
        } else {
            System.out.println("No hay autores nacidos en el año " + añoNacimiento);
        }
    }


    private void listarAutoresPorAñoMuerte() {
        System.out.println("Ingresa el año de fallecimiento: ");
        while (!teclado.hasNextInt()) {
            System.out.println(Colors.NEGRITA + Colors.ROJO + "Por favor ingresa un año válido." + Colors.RESET);
            teclado.next(); // Descartar la entrada no válida
        }
        int añoMuerte = teclado.nextInt();
        teclado.nextLine(); // Consumir la nueva línea pendiente en el buffer de entrada

        List<Autores> autores = autoresRepository.findByAñoMuerte(añoMuerte);

        if (!autores.isEmpty()) {
            System.out.println("Autores fallecidos en el año " + añoMuerte + ":");
            autores.forEach(autor -> System.out.println(Colors.NEGRITA + Colors.AMARILLO + autor.getNombre() + Colors.RESET));
        } else {
            System.out.println("No hay autores fallecidos en el año " + añoMuerte);
        }
    }


}
