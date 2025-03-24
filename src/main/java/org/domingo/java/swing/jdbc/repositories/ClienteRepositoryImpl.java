package org.domingo.java.swing.jdbc.repositories;

import org.domingo.java.swing.jdbc.db.ConnectionJdbc;
import org.domingo.java.swing.jdbc.models.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepositoryImpl implements ClienteRepository {

    @Override
    public List<Cliente> findAll() {

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConnectionJdbc.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from clientes")){

                while (rs.next()){

                    Cliente cliente = new Cliente(rs.getLong("id"),
                            rs.getString("nombre"),
                            rs.getString("apellidos"),
                            rs.getString("email"),
                            rs.getInt("telefono"));
                    clientes.add(cliente);

                }

        } catch (SQLException e){

            e.printStackTrace();
        }


        return clientes;
    }

    @Override
    public Cliente findById(Long id) {

        Cliente cliente = null;

        try (Connection conn = ConnectionJdbc.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select * from clientes where id=?");
        ){
            stmt.setLong(1,id);
            try(ResultSet rs = stmt.executeQuery()){
            while (rs.next()){

                 cliente = new Cliente(rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("email"),
                         rs.getInt("telefono"));

            }}

        } catch (SQLException e){

            e.printStackTrace();
        }


        return cliente;
    }

    @Override
    public Cliente save(Cliente cliente) {


    String sql = "";

    if(cliente.getId() != null && cliente.getId() >0){

        sql = "update clientes set nombre=?, apellidos=?, email=?, telefono=? where id=?";

    } else {
        sql = "insert into clientes (nombre,apellidos,email,telefono) values(?,?,?,?)";

    }

    try (Connection conn = ConnectionJdbc.getConnection();
    PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

        stmt.setString(1, cliente.getNombre());
        stmt.setString(2, cliente.getApellidos());
        stmt.setString(3, cliente.getEmail());
        stmt.setInt(4, cliente.getTelefono());

        if (cliente.getId() != null && cliente.getId() >0){
            stmt.setLong(5, cliente.getId());
        }

        int affectedRow = stmt.executeUpdate();

        if (affectedRow > 0 && (cliente.getId() == null || cliente.getId() == 0)){

            try (ResultSet rs = stmt.getGeneratedKeys()){
                if (rs.next()){
                    cliente.setId(rs.getLong(1));
                }

            }
        }


    } catch (SQLException e){

        e.printStackTrace();
    }


        return cliente;
    }

    @Override
    public void delete(Long id) {

        try (Connection conn = ConnectionJdbc.getConnection();
             PreparedStatement stmt = conn.prepareStatement("delete from clientes where id=?");
        ){
            stmt.setLong(1,id);
            stmt.executeLargeUpdate();


        } catch (SQLException e){

            e.printStackTrace();
        }

    }
}
