//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package servidor;

import Repositorio.ServicioSrOperadorImpl;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;


public class Servidor {

    private static final int puerto = 2000;

    public static void main(String[] args) throws IOException, NotBoundException {


        Scanner lector = new Scanner(System.in);

        init(puerto); //Inicia rmiregistry

        ServicioDatosImpl datos=new ServicioDatosImpl();
        ServicioAutenticacionImpl autenticacion = new ServicioAutenticacionImpl();
        ServicioGestorImpl gestor = new ServicioGestorImpl();

        Naming.rebind("rmi://localhost:" + puerto + "/datos", datos);
        Naming.rebind("rmi://localhost:" + puerto + "/autenticacion", autenticacion);
        Naming.rebind("rmi://localhost:" + puerto + "/gestor", gestor);

        // Registro dinámico de ServicioSrOperadorImpl por cada repositorio
        for (Map.Entry<Integer, String> entry : datos.listarRepositoriosconID().entrySet()) {
            int idRepositorio = entry.getKey();

            // SrOperador
            ServicioSrOperadorImpl operador = new ServicioSrOperadorImpl();
            String nombreServicio = "rmi://localhost:" + puerto + "/ServicioSrOperador/" + idRepositorio;
            try {
                Naming.rebind(nombreServicio, operador);

            } catch (Exception e) {
                System.err.println("Error registrando " + nombreServicio + ": " + e.getMessage());
            }

            // ClOperador
            Repositorio.ServicioClOperadorImpl clienteOp = new Repositorio.ServicioClOperadorImpl();
            String nombreServicioCliente = "rmi://localhost:" + puerto + "/ServicioClOperador/" + idRepositorio;
            try {
                Naming.rebind(nombreServicioCliente, clienteOp);

            } catch (Exception e) {
                System.err.println("Error registrando " + nombreServicioCliente + ": " + e.getMessage());
            }
        }





        int opcion = 0;

        while(true){
            System.out.println();
            System.out.println("---------------------------");
            System.out.println("       MENÚ SERVIDOR       ");
            System.out.println("---------------------------");
            System.out.println("1- Listar Clientes");
            System.out.println("2- Listar Repositorios");
            System.out.println("3- Listar Parejas Repositorio-Cliente");
            System.out.println("4-Salir");
            System.out.print("");

            opcion=lector.nextInt();

            switch(opcion){
                case 1:
                    System.out.println();
                    System.out.println("Clientes registrados");

                    for (Map.Entry<Integer, String> entry : datos.listarClientesconID().entrySet()) {
                        System.out.println("Cliente: " + entry.getValue() + " - ID SESIÓN: " + entry.getKey());
                    }

                    break;

                case 2:
                    System.out.println();
                    System.out.println("Repositorios registrados");

                    for (Map.Entry<Integer, String> entry : datos.listarRepositoriosconID().entrySet()) {
                        System.out.println("Repositorio: " + entry.getValue() + " - ID SESIÓN: " + entry.getKey());
                    }

                    break;

                case 3:
                    System.out.println();
                    System.out.println("Lista de clientes y repositorios");
                    datos.listarClientesRepositorios();
                    System.out.println();
                    break;

                case 4:
                    //Finalizamos los servicios
                    Naming.unbind("rmi://localhost:" + puerto + "/autenticacion");
                    Naming.unbind("rmi://localhost:" + puerto + "/datos");
                    Naming.unbind("rmi://localhost:" + puerto + "/gestor");
                    System.out.println("Saliendo del sistema.");
                    System.out.println();
                    System.exit(4);


            }

        }


    }




    private static void init(int puerto) throws RemoteException
    {
        try{
            Registry registro= LocateRegistry.getRegistry(puerto);
            registro.list();
        }
        catch(RemoteException e)
        {

            LocateRegistry.createRegistry(puerto);

        }
    }
}