//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package Repositorio;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServicioSrOperadorInterface extends Remote {

    public void descargarFicheros(int idRepositorio, String nombreFichero, String nombreCliente) throws RemoteException;
    public void crearCarpeta(String nombre) throws RemoteException;
    public void listarFicheros(List<String> ficheros) throws RemoteException;


    public void registrarDescarga(String archivo, String propietario) throws RemoteException;
}
