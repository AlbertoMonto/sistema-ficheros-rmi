//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package servidor;

import Repositorio.ServicioSrOperadorInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServicioGestorImpl extends UnicastRemoteObject implements ServicioGestorInterface,Remote {

    protected ServicioGestorImpl() throws RemoteException {
        super();
    }

    private static ServicioDatosInterface datos;
    private static ServicioSrOperadorInterface servicioSrOp;

    @Override
    public boolean listarFicherosporCliente(int idRepositorio, String nombreCliente) throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println();
        System.out.println("Lista de ficheros del cliente " + nombreCliente);

        datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");

        if (!datos.verificarRepositorio(idRepositorio, nombreCliente)) {
            System.out.println("Cliente no pertenece al repositorio con ID: " + idRepositorio);
            return false;
        }

        List<String> listaFicheros = datos.listarFicherosCliente(nombreCliente);

        servicioSrOp = (ServicioSrOperadorInterface) Naming.lookup("rmi://localhost:2000/ServicioSrOperador/" + idRepositorio);
        servicioSrOp.listarFicheros(listaFicheros);

        return true;
    }


    @Override
    public String getURLClienteOperador(int id) throws MalformedURLException, NotBoundException, RemoteException {
        datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
        int idRepositorio = datos.buscarRepositorio(id);
        String url = "rmi://localhost:2000/ServicioClOperador/" + idRepositorio;

        try {
            Naming.lookup(url);
        } catch (NotBoundException e) {
            Repositorio.ServicioClOperadorImpl nuevoClOperador = new Repositorio.ServicioClOperadorImpl();
            Naming.rebind(url, nuevoClOperador);

        }

        return url;
    }



    @Override
    public String getURLServidorOperador(int id) throws MalformedURLException, NotBoundException, RemoteException {
        datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
        int idRepositorio = datos.buscarRepositorio(id); // id = idCliente
        String url = "rmi://localhost:2000/ServicioSrOperador/" + idRepositorio;

        try {
            // Intentamos acceder al servicio para comprobar si ya está registrado
            Naming.lookup(url);
        } catch (NotBoundException e) {
            // Si no está registrado, lo creamos y registramos
            ServicioSrOperadorInterface nuevoOperador = new Repositorio.ServicioSrOperadorImpl();
            Naming.rebind(url, nuevoOperador);

        }

        return url;
    }


    @Override
    public void iniciarDescarga(int id, String archivo, String nombreCliente) throws MalformedURLException, NotBoundException, RemoteException {
        if (datos == null) {
            datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");
        }

        int idRepositorio = datos.buscarRepositorio(id);
        String nombreRepositorio = datos.listarRepositoriosconID().get(idRepositorio);

        if (nombreRepositorio == null) {
            throw new RemoteException("No se pudo obtener el nombre del repositorio para ID: " + idRepositorio);
        }

        String urlRepositorio = getURLServidorOperador(id);
        servicioSrOp = (ServicioSrOperadorInterface) Naming.lookup(urlRepositorio);

        servicioSrOp.descargarFicheros(idRepositorio, archivo, nombreCliente);

        datos.incrementarContadorDescargas(nombreRepositorio, archivo);
    }




    @Override
    public List<String> listarClientesAutenticados() throws RemoteException {

        return datos.listarClientesAutenticados();
    }




}
