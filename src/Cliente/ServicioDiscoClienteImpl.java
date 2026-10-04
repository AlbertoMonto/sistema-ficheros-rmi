//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package Cliente;

import Fichero.Fichero;
import servidor.ServicioGestorInterface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class ServicioDiscoClienteImpl extends UnicastRemoteObject implements ServicioDiscoClienteInterface {

    private static ServicioGestorInterface ServicioGestor;
    protected ServicioDiscoClienteImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean listarFicherosCliente(int id, String nombreCliente) throws MalformedURLException, NotBoundException, RemoteException {
        ServicioGestor=(ServicioGestorInterface) Naming.lookup("rmi://localhost:2000/gestor");
        return ServicioGestor.listarFicherosporCliente(id, nombreCliente);
    }


    @Override
    public boolean descargarFicheroFinal(Fichero fichero) {
        System.out.println("Recibiendo fichero: " + fichero.obtenerNombre());

        try (OutputStream os = new FileOutputStream(fichero.obtenerNombre())) {

            if (!fichero.escribirEn(os)) {
                System.err.println("Error al escribir los datos (checksum incorrecto o datos vacíos)");
                return false;
            }

            System.out.println("Fichero " + fichero.obtenerNombre() + " recibido y guardado correctamente");
            return true;

        } catch (FileNotFoundException | NullPointerException e) {
            System.err.println("Fichero no encontrado o nulo: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al guardar el fichero: " + e.getMessage());
        }

        return false;

    }

}
