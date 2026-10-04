//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package servidor;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServicioAutenticacionInterface extends Remote {
    public int getNuevoIdCliente()throws RemoteException;
    public int getNuevoIdRepositorio()throws RemoteException;

    public boolean registrarCliente(String nombre) throws MalformedURLException, NotBoundException, RemoteException;
    public int autenticarCliente(String nombre) throws MalformedURLException, NotBoundException, RemoteException;
    public boolean registrarRepositorio(String nombre) throws MalformedURLException, NotBoundException, RemoteException;
    public int autenticarRepositorio(String nombre) throws RemoteException;
    public void desautenticarCliente(int id) throws RemoteException;
    public void desautenticarRepositorio(int id) throws RemoteException;

}