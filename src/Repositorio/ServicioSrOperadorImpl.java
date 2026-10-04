//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package Repositorio;

import Cliente.ServicioDiscoClienteInterface;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Fichero.Fichero;

public class ServicioSrOperadorImpl extends UnicastRemoteObject implements ServicioSrOperadorInterface {
    public ServicioSrOperadorImpl() throws RemoteException {
    }

    private final Map<String, Integer> contadorDescargas = new HashMap<>();

    @Override
    public void descargarFicheros(int repositorioCliente, String archivo, String nombreCliente) throws RemoteException {
        try {
            URL sitio = ServicioSrOperadorImpl.class.getProtectionDomain().getCodeSource().getLocation();
            Path path = Paths.get(sitio.toURI());
            if (path.toString().endsWith(".jar")) {
                path = path.getParent();
            }

            Path rutaFinal = Paths.get("repositorio", nombreCliente);
            String ruta = rutaFinal.toString();

            File test = new File(ruta, archivo);
            System.out.println("Preparando envío de: " + archivo);

            System.out.println("Existe en servidor: " + test.exists());

            if (!test.exists()) {
                System.out.println("El fichero no existe. Abortando descarga.");
                return;
            }

            Fichero fichero = new Fichero(ruta, archivo, nombreCliente);

            String url = "rmi://localhost:2000/DiscoCliente/" + repositorioCliente;
            System.out.println("Enviando fichero al cliente: " + nombreCliente);

            ServicioDiscoClienteInterface discoCliente = (ServicioDiscoClienteInterface) Naming.lookup(url);
            boolean ok = discoCliente.descargarFicheroFinal(fichero);
            System.out.println("Resultado de envío: " + ok);

            if (ok) {
                String clave = nombreCliente + "/" + archivo;
                contadorDescargas.put(clave, contadorDescargas.getOrDefault(clave, 0) + 1);
            }

        } catch (URISyntaxException | MalformedURLException | NotBoundException e) {
            throw new RemoteException("Error resolviendo ruta o conexión RMI", e);
        }
    }

    @Override
    public void crearCarpeta(String nombre) throws RemoteException {
        String ruta = "repositorio/" + nombre.toUpperCase();
        File carpeta = new File(ruta);
        if (!carpeta.exists()) {
            boolean creada = carpeta.mkdirs();
            System.out.println("Carpeta " + ruta + (creada ? " creada." : " no pudo crearse."));
        } else {
            System.out.println("Carpeta " + ruta + " ya existe.");
        }
    }

    @Override
    public void listarFicheros(List<String> ficheros) {
        System.out.println();
        for (String fichero : ficheros) {
            System.out.println(fichero);
        }
    }

    @Override
    public void registrarDescarga(String archivo, String propietario) throws RemoteException {
        String clave = propietario + "/" + archivo;
        contadorDescargas.put(clave, contadorDescargas.getOrDefault(clave, 0) + 1);
    }

}
