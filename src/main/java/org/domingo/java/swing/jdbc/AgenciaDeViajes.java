package org.domingo.java.swing.jdbc;

import org.domingo.java.swing.jdbc.models.Cliente;
import org.domingo.java.swing.jdbc.repositories.ClienteRepository;
import org.domingo.java.swing.jdbc.repositories.ClienteRepositoryImpl;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class AgenciaDeViajes extends JFrame {

    private Container p;
    private JTextField nombreField = new JTextField();
    private JTextField apellidosField = new JTextField();
    private JTextField emailField = new JTextField();
    private JTextField telefonoField = new JTextField();
    private ClienteTableModel tableModel = new ClienteTableModel();
    private ClienteRepository clienteRepository;


    private long id;
    private int row;

    public AgenciaDeViajes() {
        super("Agencia de viajes: Lista de clientes");
        p = getContentPane();
        p.setLayout(new BorderLayout(20, 10));
        clienteRepository = new ClienteRepositoryImpl();

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 20, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JButton buttonSave = new JButton("Guardar");

        formPanel.add(new JLabel("Nombre: "));
        formPanel.add(nombreField);

        formPanel.add(new JLabel("Apellidos: "));
        formPanel.add(apellidosField);

        formPanel.add(new JLabel("Email: "));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Telefono: "));
        formPanel.add(telefonoField);

        formPanel.add(new JLabel(""));
        formPanel.add(buttonSave);

        //Esta función de validación de datos.

        buttonSave.addActionListener( e -> {
            String nombre = nombreField.getText();
            String apellidos = apellidosField.getText();
            String email = emailField.getText();
            int telefono = 0;

            try {
                telefono = Integer.parseInt(telefonoField.getText());
            } catch (NumberFormatException numberFormatException){ }

            List<String> errors = new ArrayList<>();
            if(nombre.isBlank()){
                errors.add("¡Debe ingresar el nombre!");
            }
            if(apellidos.isBlank()){
                errors.add("¡Debe ingresar los apellidos!");
            }
            if(email.isBlank()){
                errors.add("¡Debe ingresar el email!");
            }

            if(telefono == 0){
                errors.add("Debe de ingresar un numero de telefono");
            }
            if(errors.size() > 0 ){
                JOptionPane.showMessageDialog(null, errors.toArray(),
                        "¡Error en la validacion!", JOptionPane.ERROR_MESSAGE);
            } else {
                Cliente clienteDb = clienteRepository.save(new Cliente(id==0?null:id, nombre,apellidos,email,telefono));

                if (id == 0) {
                    Object[] product = new Object[]{clienteDb.getId(), nombre, apellidos, email, telefono, "eliminar"};
                    tableModel.getRows().add(product);
                    tableModel.fireTableDataChanged();
                    System.out.println(product[0]);
                    System.out.println(product[1]);
                    System.out.println(product[2]);
                    System.out.println(product[3]);
                } else if (id > 0) {
                    tableModel.setValueAt(id, row, 0);
                    tableModel.setValueAt(nombre, row, 1);
                    tableModel.setValueAt(apellidos, row, 2);
                    tableModel.setValueAt(email, row, 3);
                    tableModel.setValueAt(telefono, row, 4);
                }
                }
                reset();

        });

        JPanel tablePanel = new JPanel(new FlowLayout());

        JTable jTable = new JTable();
        jTable.setModel(this.tableModel);

        // Función es para eliminar los datos.

        jTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                row = jTable.rowAtPoint(e.getPoint());
                int column = jTable.columnAtPoint(e.getPoint());

                if(row > -1 && column == 5){
                    int option = JOptionPane.showConfirmDialog(null, "Esta seguro que desea eliminar el registro " +
                            tableModel.getValueAt(row, 1).toString() +
                            "?", "Cuidado Eliminar Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if(option == JOptionPane.OK_OPTION){
                        clienteRepository.delete( (long) tableModel.getValueAt(row, 0));
                        tableModel.getRows().remove(row);
                        tableModel.fireTableDataChanged();
                    }
                    reset();
                } else if(row > -1 && column > -1){
                    id = (long) tableModel.getValueAt(row, 0);
                    nombreField.setText(tableModel.getValueAt(row, 1).toString());
                    apellidosField.setText(tableModel.getValueAt(row, 2).toString());
                    emailField.setText(tableModel.getValueAt(row, 3).toString());
                    telefonoField.setText(tableModel.getValueAt(row, 4).toString());

                }
            }
        });

        //Visibilidad y estilos del formulario y la tabla.
        JScrollPane scroll = new JScrollPane(jTable);

        tablePanel.add(scroll);
        p.add(tablePanel, BorderLayout.SOUTH);
        p.add(formPanel, BorderLayout.NORTH);
        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private void reset() {
        id = 0;
        row = -1;
        nombreField.setText("");
        apellidosField.setText("");
        emailField.setText("");
        telefonoField.setText("");

    }

    public static void main(String[] args) {

        new AgenciaDeViajes();
    }

    private class ClienteTableModel extends AbstractTableModel {

        private String[] columns = new String[]{"Id", "Nombre", "Apellidos", "Email","Telefono" ,"Eliminar"};
        private List<Object[]> rows = new ArrayList<>();

        public ClienteTableModel() {

            ClienteRepository clienteRepository = new ClienteRepositoryImpl();
            List<Cliente> clientes = clienteRepository.findAll();
            for (Cliente cliente : clientes){
             Object[] row ={cliente.getId(), cliente.getNombre(), cliente.getApellidos(), cliente.getEmail(),
                     cliente.getTelefono() , "eliminar"};
             rows.add(row);

            }

        }

        public List<Object[]> getRows() {
            return rows;
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rows.get(rowIndex)[columnIndex];
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            rows.get(rowIndex)[columnIndex] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }
    }
}
