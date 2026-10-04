//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package Repositorio;
import Fichero.Fichero;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface ServicioClOperadorInterface extends Remote {

    public boolean guardarFichero(Fichero fichero) throws RemoteException;
    public boolean borrarFichero(String archivo,String carpeta) throws RemoteException;


}
