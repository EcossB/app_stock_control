package com.alura.jdbc.controller;

import com.alura.jdbc.factory.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoController {

    public int modificar(String nombre, String descripcion, Integer cantidad, Integer id) throws SQLException {
        //creando conexion con la base de datos
        Connection con = new ConnectionFactory().recuperaConexion();

        //creando el statement
        //Statement statement = con.createStatement();

        //ejecutando el query, como es un update no devolvera true, sino false.
        //statement.execute("UPDATE producto SET nombre = '" + nombre+ "', descripcion = '"+ descripcion + "', cantidad = " +cantidad+ " where id = " + id);

        //haciendo lo mismo pero con PreparedStatement.
        PreparedStatement statement = con.prepareStatement("UPDATE producto SET nombre = ?, descripcion = ?, cantidad = ? where id = ?");
        statement.setString(1, nombre);
        statement.setString(2, descripcion);
        statement.setInt(3, cantidad);
        statement.setInt(4, id);


        //utilizamos el metodo getUpdateCount asi sabemos sabemos cuantos registro modifico
        int updated = statement.getUpdateCount();

        //cerrando conexion
        con.close();

        //retornando el id, asi se pueda mostrar en el front.
        return updated;

    }

    public int eliminar(Integer id) throws SQLException {
        Connection con = new ConnectionFactory().recuperaConexion();

        //manera tradicional donde el sql inyection es posible
        //Statement statement = con.createStatement(); statement normal
        //statement.execute("DELETE FROM PRODUCTO where id = " id);

        PreparedStatement statement = con.prepareStatement("DELETE FROM PRODUCTO where id = ?"); //prepared statement
        statement.setInt(1, id);
        statement.execute();


        //Nos devuelve un int
        // el numero nos devuelve cuantas filas fueron modificadas luego del excute.
        return statement.getUpdateCount();

        //con.close();

    }

    public List<Map<String, String>> listar() throws SQLException {
        Connection con = new ConnectionFactory().recuperaConexion();

        // para crear un statement hay que usar el metodo statement.
        // y devuelve un Statement.
        // con el statement es que podemos ejecutar querys para la base de datos.
        //Statement statement = con.createStatement(); // con statement normal
        PreparedStatement statement = con.prepareStatement("SELECT id, nombre, descripcion, cantidad FROM PRODUCTO"); // con prepareStatement

        //para poder ejectutar el statement tenemos que usar otro metodo:
        // En el metodo execute pasamos el query que queremos que se ejecute en la base datos
        // en este caso seleccionara todo de una tabla.
        // este metodo retorna un boolean. para indicar que el statement es un listado o no
        // si es un listado devuelve true.
        // si no es un listado devuelve false y nos indica que el query que ejecutamos es de una setencia insert O update o delete.
        // antes del prepared statement -> boolean result = statement.execute("SELECT id, nombre, descripcion, cantidad FROM PRODUCTO");
        boolean result = statement.execute();

        // para poder tomar el resultado del statement que justamente acabamos de ejecutar
        //utilizamos el metodo getResultSet();
        //este metodo devuelve un objeto del tipo resultSet.
        ResultSet resultSet = statement.getResultSet();

        // un list en el cual solo aceptara objetos  map del tipo string string
        // ya que no tenemos nada para representar el producto aun.
        List<Map<String, String>> resultado = new ArrayList<>();

        //para poder ver los resultados de nuestro resultset
        //podemos utilizar un metodo llamado next
        // que es un metodo que basicamente va a decir si hay una siguiente fila con elementos
        // y siempre que haya dicha fila, siempre podremos ir iterando.
        // cuando llegamos al ultimo item, el loop se termina.
        // por eso podemos utilizar ese objeto dentro de un while.
        while (resultSet.next()) {

            // el resultSet ahora lo tenemos que agregar a un map
            // para que lo podamos agregar a la list.
            // para cada registro del resultSet podemos utilizar la info de las columnas
            // que son id, nombre, descripcion, cantidad.
            // para id y cantidad que son numericos utilizamos:
            //resultSet.getInt("ID");  nos devuelve la parte numerica del id y la cantida. podemos poner el indice o el nombre la columna ""

            // ese resultSet anterior lo vamos agregar en un Map
            Map<String, String> fila = new HashMap<>();
            //utilizamos el metodo put del map y dentro de el le asignamos metodo get int si la columna es int o getString si la columnda es string
            // como sabemos en el map el valor del key es un string, so al final tenemos que utilizar el metodo
            // String.valueOf para poder tomar ese valor y asi pueda ser anadido al Map.

            fila.put("Id", String.valueOf(resultSet.getInt("id")));
            fila.put("Nombre", String.valueOf(resultSet.getString("nombre")));
            fila.put("Descripcion", String.valueOf(resultSet.getString("descripcion")));
            fila.put("Cantidad", String.valueOf(resultSet.getInt("cantidad")));

            resultado.add(fila); // agregamos la fila a la lista.
        }
        con.close();

        return resultado;

        // en resumen para poder listar los registros de una tabla
        // 1- hay que hacer la conexion.
        // 2- luego crear el metodo statement =  con.CreateStatement
        // 3- crear el execute del statement con el query
        // 4- Luego utilizar el metodo GetResultSet del statement.
        // 5 crear una lista que acepte Map tipo String, String.
        // 6 utilizar el metodo Next del resultSet en un loop
        // 7- Dentro de ese loop declarar un Hashmap tipo String String
        // 8- dentro de ese loop utilizamos el metodo put del hashmap y dentro de ese metodo nombramos La key y el valor de esta sera un resultSet.getInt o getString dependeiendo del tipo de columna
        // 9 dentro del loop agregamos dentro de la lista el resultado del hashmap.
        // 10- fuera de la lista cerramos la conexion.
    }

    public void guardar(Map<String, String> producto) throws SQLException {
        //conexion a la base de datos
        Connection con = new ConnectionFactory().recuperaConexion();

        //Manera haciendolo con PreparedStatement. lo cual nos ayuda a proteger nuestros querys y la db
        PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO PRODUCTO(nombre, descripcion, cantidad) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, producto.get("Nombre"));
        preparedStatement.setString(2, producto.get("Descripcion"));
        preparedStatement.setInt(3, Integer.valueOf(producto.get("Cantidad")));

        preparedStatement.execute();

        ResultSet resultSet = preparedStatement.getGeneratedKeys();

        //Statement statement = con.createStatement();

        // va a retornar un false por que no es un resultSet es decir no es un listado
        // para que no nos retorne un false, sobrecargamos el metodo y le agregamos
        // como siguiente parametro el retornar llaves generadas.

//         asi lo podemos correr dentro de un while.
//        statement.execute("INSERT INTO PRODUCTO(nombre, descripcion, cantidad)" +
//                "VALUES ('" + producto.get("Nombre") + "', '"
//                + producto.get("Descripcion") + "', " +
//                producto.get("Cantidad") + ")", Statement.RETURN_GENERATED_KEYS); // con este valor aca, tomamos el id como resultado de la ejecucion del insert
//        // es decir este metodo va a retornar el id del registro creado.
//
//        // Cuando el execute nos genera nuestro id. podemos utilizar el metodo
//        // getGeneratedKeys que basicamente nos da una lista de los id creados.
//        // esto lo podemos tambien iterar
//        ResultSet resultSet = statement.getGeneratedKeys();

        while (resultSet.next()) {
            // imprimiendo en consola el id que acaba de crear.
            System.out.println(String.format(
                    "Fue insertado el producto de ID %d",
                    resultSet.getInt(1)));// aqui le estamos diciendo que nos devuelva el id.
        }
        con.close();
    }

}
