package com.alura.jdbc.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


//esta clase cumple un patron de diseno el cual es factorydesing
// esta clase encapsula la logica para conectarnos a base de datos hahaha.
public class ConnectionFactory {

    public Connection recuperaConexion() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost/control_de_stock?useTimeZone=true&serverTimeZone=UTC",
                "root",
                "E.cossB2104");
    }
}
