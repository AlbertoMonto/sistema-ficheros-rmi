//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package Cliente;
import Fichero.Fichero;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioDiscoClienteInterface extends Remote {

    public boolean listarFicherosCliente(int id, String nombreCliente) throws MalformedURLException, NotBoundException, RemoteException;
    public boolean descargarFicheroFinal(Fichero fichero)throws MalformedURLException, NotBoundException, RemoteException;
}
