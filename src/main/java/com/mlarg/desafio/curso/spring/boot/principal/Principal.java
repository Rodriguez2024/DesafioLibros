package com.mlarg.desafio.curso.spring.boot.principal;

import com.mlarg.desafio.curso.spring.boot.model.Datos;
import com.mlarg.desafio.curso.spring.boot.model.DatosLibros;
import com.mlarg.desafio.curso.spring.boot.service.ConsumoAPI;
import com.mlarg.desafio.curso.spring.boot.service.ConvierteDatos;

import javax.sql.rowset.spi.SyncFactory;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private static final String URL_BASE = "http://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);


    public void muestraElMenu(){
        var json = consumoAPI.obtenerDatos(URL_BASE );
        System.out.println(json);
        var datos = conversor.obtenerDatos(json, Datos.class);
        System.out.println(datos);

        // top 10 libros mas descargados
        System.out.println("Top 10 libros mas descargados");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .map(l -> l.titulo().toUpperCase())
                .forEach(System.out::println);

        // Busqueda de libros por nombre
        System.out.println("Ingrese el nombre del libro que desea buscar: ");
        var tituloLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "%20"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if(libroBuscado.isPresent()) {
            System.out.println("Libro encontrado");
            System.out.println(libroBuscado.get());
        } else {
            System.out.println("Libro no encontrado");
        }

        //Trabajando con estadisticas
        DoubleSummaryStatistics  est =  datos.resultados().stream()
                .filter(d -> d.numeroDeDescargas() > 0)
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas));
        System.out.println("Promedio de descargas = " + est.getAverage());
        System.out.println("Cantidad Maxima de descargas = " + est.getMax());
        System.out.println("Cantida Minima de descargas  = " + est.getMin());
        System.out.println("Cantidad de registros evaluados para calcular las estadisticas = " + est.getCount());
    }
}
