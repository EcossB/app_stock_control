package com.alura.jdbc.DAO;

import com.alura.jdbc.modelo.Categoria;
import com.alura.jdbc.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    final private Connection con;
    public CategoriaDAO(Connection con ) {
        this.con = con;
    }

    public List<Categoria> listar() {
        List<Categoria> resultado = new ArrayList<>();

        try {
            final PreparedStatement statement = con.prepareStatement("Select id, nombre from categoria");
            try(statement) {
                final ResultSet resultSet = statement.executeQuery();
                try (resultSet) {
                    while (resultSet.next()) {
                        var fila = new Categoria(resultSet.getInt("id"),
                                resultSet.getString("nombre"));

                        resultado.add(fila);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultado;
    }

    public List<Categoria> listarConProductos() {
        List<Categoria> resultado = new ArrayList<>();

        try {
            var querySelect = "SELECT C.ID, C.NOMBRE, P.ID, P.NOMBRE, P.CANTIDAD " +
                    "FROM CATEGORIA C " +
                    "INNER JOIN PRODUCTO P ON C.ID = P.CATEGORIA_ID";

            final PreparedStatement statement = con.prepareStatement(
                    querySelect);
            try(statement) {
                final ResultSet resultSet = statement.executeQuery();
                try (resultSet) {
                    while (resultSet.next()) {
                        //agregando filtros
                        Integer categoriaId = resultSet.getInt("c.id");
                        String categoriaNombre = resultSet.getString("c.nombre");

                        var categoria = resultado
                                .stream()
                                .filter(cat -> cat.getId().equals(categoriaId))
                                .findAny().orElseGet(() -> {
                                    Categoria cat = new Categoria(categoriaId,
                                             categoriaNombre);

                                    resultado.add(cat);

                                    return cat;
                                });

                        var producto = new Producto(resultSet.getInt("p.id"),
                                resultSet.getString("p.nombre"),
                                resultSet.getInt("p.cantidad"));

                        categoria.agregar(producto);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultado;
    }
}
