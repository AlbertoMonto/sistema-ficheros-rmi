//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package servidor;

import Repositorio.Repositorio;
import Repositorio.ServicioSrOperadorImpl;
import Repositorio.ServicioSrOperadorInterface;
import Repositorio.ServicioSrOperadorImpl;



import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServicioAutenticacionImpl extends UnicastRemoteObject implements ServicioAutenticacionInterface, Remote {

    //Identificador para clientes y repositorios
    private static int idClientes = 0;
    private static int idRepositorios = 1000;


    private static ServicioDatosInterface datos;

    protected ServicioAutenticacionImpl() throws RemoteException {
    super();
    }

    @Override
    public int getNuevoIdCliente() {
        return ++idClientes;
    }

    @Override
    public int getNuevoIdRepositorio() {
        return ++idRepositorios;
    }

    @Override
    public boolean registrarCliente(String nombre) throws MalformedURLException, NotBoundException, RemoteException {
        int id = getNuevoIdCliente();

        datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
        return datos.altaCliente(nombre);
    }

    @Override
    public int autenticarCliente(String nombreCliente) throws MalformedURLException, NotBoundException, RemoteException {
        int id = datos.obtenerIdDeCliente(nombreCliente);
        if (id == -1) return 0;

        try {
            datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
        } catch (NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }

        if (datos.autenticarCliente(nombreCliente, id)) {
            try {
                ServicioSrOperadorImpl operador = new ServicioSrOperadorImpl();
                String direccion = "rmi://localhost:2000/ServicioSrOperador/" + id;
                Naming.rebind(direccion, operador);
                operador.crearCarpeta(nombreCliente);
            } catch (Exception e) {
                System.out.println("Error registrando ServicioSrOperador: " + e.getMessage());
                e.printStackTrace();
                return 0;
            }

            return id;
        }

        return 0;
    }

    @Override
    public boolean registrarRepositorio(String nombre) throws MalformedURLException, NotBoundException, RemoteException {
        int id = getNuevoIdRepositorio();

        datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
        return datos.altaRepositorio(nombre);
    }

    @Override
    public int autenticarRepositorio(String nombre) throws RemoteException {
        System.out.println(nombre + " esta intentando autenticarse.");

        try {
            datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        int id = datos.obtenerIdDeRepositorio(nombre); // Usamos el ID real ya registrado

        if (id == -1) {
            System.out.println("El repositorio '" + nombre + "' no está registrado.");
            return 0;
        }

        System.out.println("Número de sesión de Repositorio = " + id);

        if (datos.autenticarRepositorio(nombre, id)) {
            return id;
        } else {
            return 0;
        }
    }

    @Override
    public void desautenticarCliente(int id) throws RemoteException {
        datos.desautenticarCliente(id);
    }

    @Override
    public void desautenticarRepositorio(int id) throws RemoteException {
        datos.desautenticarRepositorio(id);
    }

}