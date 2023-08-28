package com.alura.jdbc.controller;

import com.alura.jdbc.DAO.ProductoDao;
import com.alura.jdbc.factory.ConnectionFactory;
import com.alura.jdbc.modelo.Categoria;
import com.alura.jdbc.modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoController {

    private ProductoDao productoDao;

    public ProductoController() {
        this.productoDao = new ProductoDao(new ConnectionFactory().recuperaConexion());
    }

    public int modificar(Integer id, String nombre, String descripcion, Integer cantidad) {
       return productoDao.modificar(nombre, descripcion, cantidad, id);
    }

    public int eliminar(Integer id) {
        return productoDao.eliminar(id);
    }
    public List<Producto> listar() {
         return productoDao.listar();
    }

    public List<Producto> listar(Categoria categoria){
        return productoDao.listar(categoria.getId());
    }

    public void guardar(Producto producto, Integer categoria_id) {
        producto.setCategoriaId(categoria_id);
        productoDao.guardar(producto);
    }


}
