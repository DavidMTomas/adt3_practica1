package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Scanner;

public class App {
    private static final Scanner SC = new Scanner(System.in);
    private static final String USER = "root";
    private static final String PWD = "";
    private static final String URL = "jdbc:mysql://localhost/adt3_practica1";

    public static void main(String[] args) {
        boolean salir = false;

        try (Connection conexion = DriverManager.getConnection(URL, USER, PWD)) {
            System.out.println("Se ha conectado correctamente a la base de datos");

            while (!salir) {
                mostrarMenu();
                String entrada = SC.nextLine();
                switch (entrada) {
                    case "1":
                        System.out.println("insertar empleado");
                        insertarEmpleado(conexion);
                        break;
                    case "2":
                        System.out.println("borrar Empleado");
                        borrarEmpleado(conexion);
                        break;
                    case "3":
                        System.out.println("listar empleados");
                        listarEmpleados(conexion);
                        break;
                    case "4":
                        System.out.println("salir");
                        salir = true;
                        break;
                    default:
                        System.out.println("Opcion no valida");
                        break;

                }
            }
            System.out.println("Programa terminado");

        } catch (SQLException e) {
            System.out.println("Error en la base de datos " + e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void listarEmpleados(Connection conexion) {
        String sql = "Select * from empleado";

        try {
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();


            while (resultado.next()) {
                System.out.println("dni: " + resultado.getString(1));
                System.out.println("Nombre: " + resultado.getString(2));
                System.out.println("Apellidos: " + resultado.getString(3));
                System.out.println("Edad: " + resultado.getInt(4));
                System.out.println("Salario: " + resultado.getFloat(5));
                System.out.println("Ruta imagen " + resultado.getString(6));
                System.out.println("----------------------------------------------------");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void borrarEmpleado(Connection conexion) {
        System.out.println("Escribe el dni");
        String dni = SC.nextLine();

        String sql = "delete from empleado where dni=?";

        try {
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, dni);
            int filas = sentencia.executeUpdate();
            if (filas == 1) System.out.println("Usuario con dni:" + dni + " eliminado");
            else System.out.println("No se elimino ningun usuario");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void insertarEmpleado(Connection conexion) throws IOException {

        String dni = "";
        while (true) {
            System.out.println("Escribe el dni:");
            dni = SC.nextLine();
            if (!dni.matches("^[0-9]{8}[a-zA-Z]$")) {
                System.out.println("DNI no Valido debe contener 8 digitos y una letra");
            } else {
                System.out.println(dni + "dni valido");
                break;
            }
        }
        System.out.println("Escribe le nombre: ");
        String nombre = SC.nextLine();

        System.out.println("Escribe apellido");
        String apellidos = SC.nextLine();

        int edad = 0;
        while (true) {
            System.out.println("Escribe la edad");
            String edadValida = SC.nextLine();
            if (!edadValida.matches("^[0-9]{2,3}$")) {
                System.out.println("Edad no valida");
            } else {
                edad = Integer.parseInt(edadValida);
                break;
            }
        }

        float salario = 0;
        while (true) {
            System.out.println("Escribe el salario");
            String salarioValido = SC.nextLine();
            if (!salarioValido.matches("^[0-9]{0,7}[.][0-9]{0,2}$")) {
                System.out.println("Salario no valido");
            } else {
                salario = Float.parseFloat(salarioValido);
                break;
            }
        }

        System.out.println("Seleccionar ruta 1-2-3-4");
        String entrada = SC.nextLine();
        String imagen = entrada.matches("^[1-4]$") ? entrada : "0";
        String rutaImagen = "src/main/resources/perfil" + imagen + ".jpg";

        final byte[] binarioImagen = Files.readAllBytes(Paths.get(rutaImagen));

        String sql = "Insert into empleado values(?,?,?,?,?,?,?)";

        PreparedStatement sentencia = null;
        try {
            sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, dni);
            sentencia.setString(2, nombre);
            sentencia.setString(3, apellidos);
            sentencia.setInt(4, edad);
            sentencia.setFloat(5, salario);
            sentencia.setString(6, rutaImagen);
            sentencia.setBytes(7, binarioImagen);

            sentencia.executeUpdate();
            System.out.println("Empleado insertado correctamente");


        } catch (SQLException e) {
            System.out.println("Error eal insertar el empleado " + e);
        }

    }
    private static void mostrarMenu() {
        String menu = """
                1. Insertar empleado nuevo
                2. Borrar empleado dado su dni
                3. Listar todos los empleados
                4. Salir
                """;
        System.out.println(menu);
    }
}