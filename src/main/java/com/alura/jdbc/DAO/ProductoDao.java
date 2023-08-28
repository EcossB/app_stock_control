package com.alura.jdbc.DAO;

import com.alura.jdbc.factory.ConnectionFactory;
import com.alura.jdbc.modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * la idea de esta clase es dar acceso a las operaciones de base de datos
 *  para la entidad de producto. su finalidad es acceder a los datos del objeto.
 *  su nombre especifico es Data access Object o mejor (DAO).
 *  este tipo de clases trabajan con las operaciones de acceso de datos de una identidad
 *  so basicamente esta clase es un DAO de la entidad/modelo objeto
 *  esto es un patron de diseno y su finalidad es tener un objeto que tenga la responsabilidad
 *  acceder a la DB y realizar las operaciones necessarias de una identidad.
 */

public class ProductoDao {

    final private Connection con;

    public ProductoDao(Connection con){
        this.con = con;
    }

    public void guardar(Producto producto)  {
        //conexion a la base de datos
        //final Connection con = new ConnectionFactory().recuperaConexion();

        //agregando try with resources
        Connection con = new ConnectionFactory().recuperaConexion();
        try(con) {
            /*
                  evita que se haga el cambio automatico.
                  debemos de agregar el comando de comit de manera explicita en nuestro codigo cuando el auto comitt es false.
                   los comandos de commit y rollback deben de ser explicitos en el codigo. y lo mejor es ponerlo dentro de un try cach
                   tambien esto se usa para manejar los errores y asi evitar mandar informacion por la mitad a la DB.
             */
            con.setAutoCommit(false);
            //Manera haciendolo con PreparedStatement. lo cual nos ayuda a proteger nuestros querys y la db
            final PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO PRODUCTO(nombre, descripcion, cantidad, categoria_id) VALUES (?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS);

            //logica para la regla de negocio
                try (preparedStatement) {
                    ejecutarRegistro(preparedStatement, producto);
                    con.commit(); // el commit se manda.
                    //System.out.println("COMMIT"); // aqui se ejecuta el commit y esto manda los cambios a la base de datos.
            } catch (SQLException e){
                    con.rollback();
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
                //.rollback();
                //System.out.println("ROLLBACK");
                // un rollback es que basicamente un comando que no permite que se mande la info a la base de dato si ocurre un error en medio de la transaccion
                // esto evita que la info se tenga que mandar completa siempre.
            }
        }

    private static void ejecutarRegistro(PreparedStatement preparedStatement, Producto producto) throws SQLException {

        preparedStatement.setString(1, producto.getNombre());
        preparedStatement.setString(2, producto.getDescripcion());
        preparedStatement.setInt(3, producto.getCantidad());
        preparedStatement.setInt(4, producto.getCategoriaId());

        preparedStatement.execute();

        final ResultSet resultSet = preparedStatement.getGeneratedKeys();

//        Statement statement = con.createStatement();
//
//         va a retornar un false por que no es un resultSet es decir no es un listado
//         para que no nos retorne un false, sobrecargamos el metodo y le agregamos
//         como siguiente parametro el retornar llaves generadas.
//
//         asi lo podemos correr dentro de un while.
//        statement.execute("INSERT INTO PRODUCTO(nombre, descripcion, cantidad)" +
//                "VALUES ('" + producto.get("Nombre") + "', '"
//                + producto.get("Descripcion") + "', " +
//                producto.get("Cantidad") + ")", Statement.RETURN_GENERATED_KEYS); // con este valor aca, tomamos el id como resultado de la ejecucion del insert
//        es decir este metodo va a retornar el id del registro creado.
//
//        Cuando el execute nos genera nuestro id. podemos utilizar el metodo
//        getGeneratedKeys que basicamente nos da una lista de los id creados.
//        esto lo podemos tambien iterar
//        ResultSet resultSet = statement.getGeneratedKeys();


        // ahora vamos a joder con try with resources
        try(resultSet) {
            while (resultSet.next()) {
                // imprimiendo en consola el id que acaba de crear.
                producto.setId(resultSet.getInt(1));
                System.out.printf(
                        "Fue insertado el producto de ID %s",
                        producto);// aqui le estamos diciendo que nos devuelva el id.
            }
        }
    }

    public List<Producto> listar(){
        List<Producto> resultado = new ArrayList<>();
        Connection con = new ConnectionFactory().recuperaConexion();
        try(con) {
            // para crear un statement hay que usar el metodo statement.
            // y devuelve un Statement.
            // con el statement es que podemos ejecutar querys para la base de datos.
            //Statement statement = con.createStatement(); // con statement normal
                final PreparedStatement statement = con.prepareStatement("SELECT id, nombre, descripcion, cantidad FROM PRODUCTO"); // con prepareStatement
                try(statement) {
                    //para poder ejectutar el statement tenemos que usar otro metodo:
                    // En el metodo execute pasamos el query que queremos que se ejecute en la base datos
                    // en este caso seleccionara todo de una tabla.
                    // este metodo retorna un boolean. para indicar que el statement es un listado o no
                    // si es un listado devuelve true.
                    // si no es un listado devuelve false y nos indica que el query que ejecutamos es de una setencia insert O update o delete.
                    // antes del prepared statement -> boolean result = statement.execute("SELECT id, nombre, descripcion, cantidad FROM PRODUCTO");
                    statement.execute();

                    // para poder tomar el resultado del statement que justamente acabamos de ejecutar
                    //utilizamos el metodo getResultSet();
                    //este metodo devuelve un objeto del tipo resultSet.
                    final ResultSet resultSet = statement.getResultSet();

                    try (resultSet) {
                        // un list en el cual solo aceptara objetos  map del tipo string string
                        // ya que no tenemos nada para representar el producto aun.


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
                            Producto fila = new Producto(resultSet.getInt("id"),
                                    resultSet.getString("Nombre"),
                                    resultSet.getString("descripcion"),
                                    resultSet.getInt("cantidad"));
                            //utilizamos el metodo put del map y dentro de el le asignamos metodo get int si la columna es int o getString si la columnda es string
                            // como sabemos en el map el valor del key es un string, so al final tenemos que utilizar el metodo
                            // String.valueOf para poder tomar ese valor y asi pueda ser anadido al Map.


                            resultado.add(fila); // agregamos la fila a la lista.
                        }
                    }
                }
                return resultado;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

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

    public int eliminar(Integer id){
        Connection con = new ConnectionFactory().recuperaConexion();
        try (con) {

            /*
            manera tradicional donde el sql inyection es posible
            Statement statement = con.createStatement(); statement normal
            statement.execute("DELETE FROM PRODUCTO where id = " id);
            */

            final PreparedStatement statement = con.prepareStatement("DELETE FROM PRODUCTO where id = ?"); //prepared statement
            try (statement) {
                statement.setInt(1, id);
                statement.execute();
                 int updatedcount = statement.getUpdateCount();
                //Nos devuelve un int
                // el numero nos devuelve cuantas filas fueron modificadas luego del excute.
                return updatedcount;
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public int modificar(String nombre, String descripcion, Integer cantidad ,Integer id) {
        //creando conexion con la base de datos
        Connection con = new ConnectionFactory().recuperaConexion();
        try(con){

            //creando el statement
            //Statement statement = con.createStatement();
            //ejecutando el query, como es un update no devolvera true, sino false.
            //statement.execute("UPDATE producto SET nombre = '" + nombre+ "', descripcion = '"+ descripcion + "', cantidad = " +cantidad+ " where id = " + id);

            //haciendo lo mismo pero con PreparedStatement.
            final PreparedStatement statement = con.prepareStatement("UPDATE producto SET nombre = ?, descripcion = ?, cantidad = ? where id = ?");
            try(statement) {
                statement.setString(1, nombre);
                statement.setString(2, descripcion);
                statement.setInt(3, cantidad);
                statement.setInt(4, id);

                //utilizamos el metodo getUpdateCount asi sabemos sabemos cuantos registro modifico
                int updated = statement.getUpdateCount();

                //cerrando conexion
                //con.close();

                //retornando el id, asi se pueda mostrar en el front.
                return updated;
            }
        } catch (SQLException e){
            throw  new RuntimeException(e);
        }
    }

    public List<Producto> listar(Integer categoriaId) {
        List<Producto> resultado = new ArrayList<>();
        Connection con = new ConnectionFactory().recuperaConexion();
        try(con) {
            final PreparedStatement statement = con.prepareStatement("SELECT id, nombre, descripcion, cantidad FROM PRODUCTO where categoria_id = ?"); // con prepareStatement
            try(statement) {
                statement.setInt(1,categoriaId);
                statement.execute();

                final ResultSet resultSet = statement.getResultSet();
                try (resultSet) {
                    while (resultSet.next()) {

                        Producto fila = new Producto(resultSet.getInt("id"),
                                resultSet.getString("Nombre"),
                                resultSet.getString("descripcion"),
                                resultSet.getInt("cantidad"));

                        resultado.add(fila); // agregamos la fila a la lista.
                    }
                }
            }

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return resultado;
    }
}


