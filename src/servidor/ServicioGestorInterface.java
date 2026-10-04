//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package servidor;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface ServicioGestorInterface extends Remote {

    public boolean listarFicherosporCliente(int idRepositorio, String nombreCliente) throws RemoteException, MalformedURLException, NotBoundException;

    public String getURLClienteOperador(int id) throws MalformedURLException, NotBoundException, RemoteException;

    public String getURLServidorOperador(int id) throws MalformedURLException, NotBoundException, RemoteException;

    public void iniciarDescarga(int id, String archivo, String nombreCliente) throws MalformedURLException, NotBoundException, RemoteException;

    public List<String> listarClientesAutenticados() throws RemoteException;
}

