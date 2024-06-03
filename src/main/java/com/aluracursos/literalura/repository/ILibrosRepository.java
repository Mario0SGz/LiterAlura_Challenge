package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Libros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ILibrosRepository extends JpaRepository<Libros, Long> {

    List<Libros> findByIdiomasIn(List<String> idiomas);

    @Query("SELECT l FROM Libros l ORDER BY l.numeroDescargas DESC LIMIT 10")
    List<Libros> obtener10MasDescargados();

    @Query("SELECT COUNT(l) FROM Libros l")
    long contarTotalLibros();

    @Query("SELECT AVG(l.numeroDescargas) FROM Libros l")
    double promedioDescargasPorLibro();

    @Query("SELECT l.numeroDescargas FROM Libros l")
    List<Double> obtenerNumeroDescargas();

}
