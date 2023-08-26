package com.alura.jdbc.factory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


//esta clase cumple un patron de diseno el cual es factory
// esta clase encapsula la logica para conectarnos a base de datos hahaha.
public class ConnectionFactory {

   private DataSource dataSource;

    public ConnectionFactory(){
        // clase de c3p0, se utiliza c3p0 para poder hacer el pool de conexiones.
        var poolDataSource = new ComboPooledDataSource();
        // url del jdbc
        poolDataSource.setJdbcUrl("jdbc:mysql://localhost/control_de_stock?useTimeZone=true&serverTimeZone=UTC");

        //usuario
        poolDataSource.setUser("root");

        //Contrasena
        poolDataSource.setPassword("E.cossB2104");

        //especificando el maximo de conexiones que podemos mantener abiertas
        poolDataSource.setMaxPoolSize(10);

        //dentro del atributo privado establecemos el pool de conexiones.
        this.dataSource =poolDataSource;
    }

    public Connection recuperaConexion() {
        //ahora retornamos del atributo datasource el metodo getConnection.
        //esto se puede hacer debido a que ese atributo esa referenciando el pool de conexiones.
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
