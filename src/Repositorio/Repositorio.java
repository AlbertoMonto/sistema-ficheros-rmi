//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package Repositorio;

import Cliente.ServicioDiscoClienteInterface;
import servidor.ServicioAutenticacionInterface;
import servidor.ServicioDatosInterface;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Repositorio {

    private static ServicioAutenticacionInterface autenticacion;
    private static ServicioDatosInterface datos;
    private static ServicioDiscoClienteInterface SrDiscoCliente;

    public static void main(String[] args) throws Exception {

        autenticacion = (ServicioAutenticacionInterface) Naming.lookup("rmi://localhost:2000/autenticacion");
        datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
        int opcion= 0;

        while (opcion != 3) {
            Scanner sc = new Scanner(System.in);


            System.out.println("---------------------------");
            System.out.println("     MENÚ DE REPOSITORIO   ");
            System.out.println("---------------------------");
            System.out.println("1- Registrar nuevo repositorio");
            System.out.println("2- Autenticarse en el sistema (hacer login)");
            System.out.println("3- Salir");
            System.out.print("Selecciona opcion:");
            System.out.println();

            try {
                opcion = sc.nextInt();
            } catch(InputMismatchException e){
                System.out.println("Debes introducir un número");
                sc.nextLine();
                continue;
            }


            switch (opcion) {
                case 1:
                    System.out.println("Introduce el nombre del nuevo repositorio:");
                    sc.nextLine();
                    String nombre = sc.nextLine();


                    if (autenticacion.registrarRepositorio(nombre)) {
                        System.out.println("Se ha creado el repositorio: " + nombre);
                        System.out.println();
                    } else {
                        System.out.println("No se ha podido crear el repositorio: " + nombre);
                        System.out.println();
                    }
                    break;

                case 2:
                    System.out.println("Introduce el nombre del repositorio a registrar");
                    sc.nextLine();
                    nombre = sc.nextLine();
                    int idSesion = autenticacion.autenticarRepositorio(nombre);

                    if (idSesion == 0) {
                        System.out.println("Fallo de autenticación de repositorio.");
                        System.out.println();
                    } else {

                        ServicioClOperadorImpl clienteOperador = new ServicioClOperadorImpl();
                        String URL_ClienteOperador = "rmi://localhost:2000/ServicioClOperador/"+ idSesion;

                            // Asegurarse de que no esté exportado previamente
                            try {
                                UnicastRemoteObject.unexportObject(clienteOperador, true);
                            } catch (Exception ignored) {}

                        ServicioClOperadorInterface stub = (ServicioClOperadorInterface) UnicastRemoteObject.exportObject(clienteOperador, 0);
                        Naming.rebind(URL_ClienteOperador, stub);


                        System.out.println();
                        System.out.println("Repositorio " + nombre.toUpperCase() +" autenticado con éxito.");
                        ServicioSrOperadorImpl srOp = new ServicioSrOperadorImpl();
                        String urlSrOp = "rmi://localhost:2000/ServicioSrOperador/" + idSesion;
                        Naming.rebind(urlSrOp, srOp);

                        System.out.println();


                        int opcion2 = 0;
                        while(opcion2 != 3) {
                            System.out.println("----------------------------");
                            System.out.println("MENÚ DE REPOSITORIO: " + nombre.toUpperCase());
                            System.out.println("----------------------------");
                            System.out.println("1.- Listar Clientes.");
                            System.out.println("2.- Listar ficheros del Cliente.");
                            System.out.println("3.- Salir.");
                            System.out.print("Selecciona opción: ");


                            opcion2 =sc.nextInt();

                            switch (opcion2) {
                                case 1:
                                    ArrayList<String> clientes = new ArrayList<String>();
                                    for(String cliente: datos.listarClientesRepositorio(idSesion)){
                                    System.out.println(cliente);
                                }
                                    break;

                                case 2:
                                    System.out.println("Introduzca el nombre del cliente:");
                                    sc.nextLine();
                                    String clienteNombre = sc.nextLine().toUpperCase();

                                    // Obtener ID del cliente
                                    int idCliente = datos.obtenerIdDeCliente(clienteNombre);
                                    if (idCliente == -1) {
                                        System.out.println("Cliente no registrado.");
                                        break;
                                    }

                                    // Obtener el ID del repositorio asignado a ese cliente
                                    int idRepoAsignado = datos.buscarRepositorio(idCliente);
                                    if (idRepoAsignado != idSesion) {
                                        System.out.println("Este cliente no pertenece al repositorio autenticado.");
                                        break;
                                    }

                                    try {
                                        SrDiscoCliente = (ServicioDiscoClienteInterface) Naming.lookup("rmi://localhost:2000/DiscoCliente/" + idRepoAsignado);
                                        if (!SrDiscoCliente.listarFicherosCliente(idRepoAsignado, clienteNombre)) {
                                            System.out.println("No existen ficheros o el cliente no está correctamente autenticado.");
                                        }
                                    } catch (NotBoundException nbe) {
                                        System.out.println("No existe ningún cliente autenticado en este repositorio.");
                                    }

                                    break;



                                case 3:
                                    autenticacion.desautenticarRepositorio(idSesion);
                                    break;
                            }
                        }
                    }
                    break;

                case 3:
                    System.out.println("Saliendo del sistema");
                    break;

                default:
                    System.out.println("Opcion no valida");
                    break;
            }
        }
    }
}
