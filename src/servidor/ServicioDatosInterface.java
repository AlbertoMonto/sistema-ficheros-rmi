//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package servidor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface ServicioDatosInterface extends Remote {

    public List<String> listarClientes() throws RemoteException;

    public Map<Integer, String> listarRepositoriosconID()throws RemoteException;

    public List<String> listarRepositorios() throws RemoteException;

    public List<String> listarClientesAutenticados() throws RemoteException;

    public List<String> listarRepositoriosAutenticados() throws RemoteException;

    public void listarClientesRepositorios() throws RemoteException;

    public boolean altaCliente(String nombreCliente) throws RemoteException;

    public boolean altaRepositorio(String nombreRepositorio) throws RemoteException;

    public boolean autenticarCliente(String nombreCliente, int id) throws RemoteException;

    public boolean autenticarRepositorio(String nombreRepositorio, int id) throws RemoteException;

    public boolean desautenticarCliente(int id) throws RemoteException;

    public boolean desautenticarRepositorio(int id) throws RemoteException;

    public int buscarRepositorio(int id) throws RemoteException;

    public boolean verificarRepositorio(int idRepositorio, String nombreCliente) throws RemoteException;

    public List<String> listarClientesRepositorio(int id) throws RemoteException;

    public List<String> listarFicherosCliente(String nombreCliente) throws RemoteException;

    public int obtenerIdDeCliente(String nombre) throws RemoteException;

    List<String> listarHistorialClientesRepositorio(int idRepositorio)throws RemoteException;

    public void incrementarContadorDescargas(String nombreRepositorio, String nombreFichero) throws RemoteException;

    public int obtenerNumeroDescargas(String clave) throws RemoteException;

    public int obtenerIdDeRepositorio(String nombreRepositorio) throws RemoteException;
}