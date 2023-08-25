package com.alura.jdbc.DAO;

import com.alura.jdbc.factory.ConnectionFactory;
import com.alura.jdbc.modelo.Producto;

import java.sql.*;

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

    private Connection con;

    public void guardar(Producto producto) throws SQLException {
        //conexion a la base de datos
        final Connection con = new ConnectionFactory().recuperaConexion();

        //agregando try with resources
        try(con) {

            /**
     *             evita que se haga el cambio automatico.
     *             debemos de agregar el comando de comit de manera explicita en nuestro codigo cuando el auto comitt es false.
     *              los comandos de commit y rollback deben de ser explicitos en el codigo. y lo mejor es ponerlo dentro de un try cach
     *              tambien esto se usa para manejar los errores y asi evitar mandar informacion por la mitad a la DB.
             */
            con.setAutoCommit(false);
            //Manera haciendolo con PreparedStatement. lo cual nos ayuda a proteger nuestros querys y la db
            final PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO PRODUCTO(nombre, descripcion, cantidad) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            //logica para la regla de negocio
            try (preparedStatement){
                ejecutarRegistro(preparedStatement, producto);
                con.commit(); // el commit se manda.
                System.out.println("COMMIT"); // aqui se ejecuta el commit y esto manda los cambios a la base de datos.
            } catch (Exception e) {
                con.rollback();
                System.out.println("ROLLBACK");
                // un rollback es que basicamente un comando que no permite que se mande la info a la base de dato si ocurre un error en medio de la transaccion
                // esto evita que la info se tenga que mandar completa siempre.
            }
        }
    }

    private static void ejecutarRegistro(PreparedStatement preparedStatement, Producto producto) throws SQLException {

        preparedStatement.setString(1, producto.getNombre());
        preparedStatement.setString(2, producto.getDescripcion());
        preparedStatement.setInt(3, producto.getCantidad());

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
                System.out.println(String.format(
                        "Fue insertado el producto de ID %s",
                        producto));// aqui le estamos diciendo que nos devuelva el id.
            }
        }
    }
}
