//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package servidor;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServicioDatosImpl extends UnicastRemoteObject implements ServicioDatosInterface {

    private int siguienteIdRepositorio = 1000;

    //Diccionarios de clientes
    private Map<Integer, String> registroClientes = new HashMap<Integer, String>();
    private Map<Integer, String> autenticacionClientes = new HashMap<Integer, String>();

    //Diccionarios de repositorios
    private Map<Integer, String> registroRepositorios = new HashMap<Integer, String>();
    private Map<Integer, String> autenticacionRepositorios = new HashMap<Integer, String>();

    //Relaciona los clientes con los repositorios. EL primero son los clientes y el segundo los repositorios
    private Map<Integer, Integer> clienteRepositorio = new HashMap<Integer, Integer>();

    //Relacion un fichero con el numero de veces que ha sido descargado
    private Map<String, Integer> contadorDescargas = new HashMap<>();


    //Constructor
    protected ServicioDatosImpl() throws RemoteException {
        super();

    }

    @Override
    public List<String> listarClientes() {
        return new ArrayList<>(registroClientes.values());
    }

    public Map<Integer, String> listarClientesconID() {
        return registroClientes;
    }

    @Override
    public Map<Integer, String> listarRepositoriosconID() {
        return registroRepositorios;
    }

    @Override
    public List<String> listarRepositorios() {
        return new ArrayList<>(registroRepositorios.values());
    }

    @Override
    public List<String> listarClientesAutenticados() {
        return new ArrayList<>(autenticacionClientes.values());

    }

    @Override
    public List<String> listarRepositoriosAutenticados() {
        return (List<String>) autenticacionRepositorios.values();

    }

    @Override
    public void listarClientesRepositorios() {
        for (Map.Entry<Integer, Integer> entry : clienteRepositorio.entrySet()) {
            int idCliente = entry.getKey();
            int idRepositorio = entry.getValue();

            String nombreCliente = registroClientes.get(idCliente);
            String nombreRepositorio = registroRepositorios.get(idRepositorio);

            System.out.println(nombreCliente + " - " + nombreRepositorio);
        }
    }

    @Override
    public boolean altaCliente(String nombreCliente) {

        if (registroClientes.containsValue(nombreCliente.toUpperCase())) {
            return false;
        } else {
            registroClientes.put(registroClientes.size() + 1, nombreCliente.toUpperCase());
            return true;
        }
    }

    public boolean altaRepositorio(String nombreRepositorio) {
        nombreRepositorio = nombreRepositorio.toUpperCase();

        if (registroRepositorios.containsValue(nombreRepositorio)) {
            return false;
        } else {
            registroRepositorios.put(siguienteIdRepositorio++, nombreRepositorio);
            return true;
        }
    }

    @Override
    public boolean autenticarCliente(String nombreCliente, int id) {

        if(registroClientes.containsValue(nombreCliente.toUpperCase())) {
            if(autenticacionClientes.containsValue(nombreCliente.toUpperCase())) {
                return false; //Si ya esta autenticado en el sistema
            }else{
                if (autenticacionRepositorios.keySet().isEmpty()) {
                    return false;//Si no hay repositorios autenticados
                }else{
                    autenticacionClientes.put(id, nombreCliente.toUpperCase()); //Autenticamos el cliente
                    int repositorio = (int) (Math.random() * autenticacionRepositorios.keySet().size());
                    clienteRepositorio.put(id, autenticacionRepositorios.keySet().stream().toList().get(repositorio));//Enlazamos el cliente al repositorio

                    return true;
                }
            }
        }else{
            return false;
        }
    }

    @Override
    public boolean autenticarRepositorio(String nombreRepositorio, int id) {


        if (registroRepositorios.containsValue(nombreRepositorio.toUpperCase())) {
            if (autenticacionRepositorios.containsValue(nombreRepositorio.toUpperCase())) {
                return false; // ya autenticado
            } else {
                autenticacionRepositorios.put(id, nombreRepositorio.toUpperCase());
                return true;
            }
        } else {
            return false; //
        }
    }

    @Override
    public boolean desautenticarCliente(int id) {
        if(autenticacionClientes.containsKey(id)) {
            autenticacionClientes.remove(id);
            return true;
        }

        return false;
    }

    @Override
    public boolean desautenticarRepositorio(int id) {
        if(autenticacionRepositorios.containsKey(id)) {
            autenticacionRepositorios.remove(id);
            return true;
        }

        return false;
    }

    @Override
    public int buscarRepositorio(int id) {
        if (clienteRepositorio.containsKey(id)) {
            return clienteRepositorio.get(id);
        } else {
            return -1;
        }

    }

    @Override
    public boolean verificarRepositorio(int idRepositorio, String nombreCliente) throws RemoteException {

        return listarClientesRepositorio(idRepositorio).contains(nombreCliente.toUpperCase());
    }

    @Override
    public List<String> listarClientesRepositorio(int id) throws RemoteException {
        List<String> clientesRepositorio = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : clienteRepositorio.entrySet()) {
            int idCliente = entry.getKey();
            int idRepo = entry.getValue();

            if (idRepo == id) {
                String nombreCliente = registroClientes.get(idCliente);
                if (nombreCliente != null) {
                    clientesRepositorio.add(nombreCliente);
                }
            }
        }

        return clientesRepositorio;
    }


    @Override
    public List<String> listarFicherosCliente(String nombreCliente) {
        File dir = new File("repositorio/" + nombreCliente.toUpperCase());
        File[] ficheros = dir.listFiles();

        List<String> ficherosCliente = new ArrayList<>();

        if (ficheros != null) {
            for (File fichero : ficheros) {
                if (fichero.isFile()) {
                    ficherosCliente.add(fichero.getName());
                }
            }
        }

        return ficherosCliente;
    }

    @Override
    public int obtenerIdDeCliente(String nombre) {
        for (Map.Entry<Integer, String> entry : registroClientes.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(nombre)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @Override
    public List<String> listarHistorialClientesRepositorio(int idRepositorio) throws RemoteException {
        List<String> resultado = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : clienteRepositorio.entrySet()) {
            int idCliente = entry.getKey();
            int repoAsociado = entry.getValue();

            if (repoAsociado == idRepositorio) {
                String nombreCliente = registroClientes.get(idCliente);
                if (nombreCliente != null && !resultado.contains(nombreCliente)) {
                    resultado.add(nombreCliente);
                }
            }
        }

        return resultado;
    }


    @Override
    public void incrementarContadorDescargas(String nombreRepositorio, String nombreFichero) throws RemoteException {
        String clave = nombreRepositorio.toUpperCase() + ":" + nombreFichero;
        contadorDescargas.put(clave, contadorDescargas.getOrDefault(clave, 0) + 1);
    }

    @Override
    public int obtenerNumeroDescargas(String clave) throws RemoteException {
        return contadorDescargas.getOrDefault(clave, 0);
    }

    @Override
    public int obtenerIdDeRepositorio(String nombreRepositorio) throws RemoteException {
        for (Map.Entry<Integer, String> entry : registroRepositorios.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(nombreRepositorio)) {
                return entry.getKey();
            }
        }
        return -1;
    }

}
