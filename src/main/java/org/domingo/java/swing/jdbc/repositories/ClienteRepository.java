package org.domingo.java.swing.jdbc.repositories;

import org.domingo.java.swing.jdbc.models.Cliente;

import java.util.List;

public interface ClienteRepository {

    List<Cliente> findAll();
    Cliente findById(Long id);
    Cliente save(Cliente cliente);
    void delete(Long id);



}
