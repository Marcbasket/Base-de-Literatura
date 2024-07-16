package com.challenge.baseliteratura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResultadosGenerales(
        @JsonAlias("count") Integer total,
        @JsonAlias("results") List<DatosLibros> libros) {
}
