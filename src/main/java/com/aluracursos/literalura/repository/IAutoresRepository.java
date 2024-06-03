package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAutoresRepository extends JpaRepository<Autores, Long> {

    Optional<Autores> findByNombre(String nombre);


    List<Autores> findByAñoNacimientoLessThanEqualAndAñoMuerteGreaterThanEqual(int añoInicial, int añoFinal);

    @Query("SELECT COUNT(a) FROM Autores a")
    long contarTotalAutores();

    @Query("SELECT a FROM Autores a WHERE a.añoNacimiento = :añoNacimiento")
    List<Autores> findByAñoNacimiento(@Param("añoNacimiento") int añoNacimiento);

    @Query("SELECT a FROM Autores a WHERE a.añoMuerte = :añoMuerte")
    List<Autores> findByAñoMuerte(@Param("añoMuerte") int añoMuerte);


}
