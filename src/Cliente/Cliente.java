//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es


package Cliente;

import Repositorio.ServicioClOperadorInterface;
import Repositorio.ServicioSrOperadorInterface;
import servidor.ServicioAutenticacionInterface;
import servidor.ServicioDatosInterface;
import servidor.ServicioGestorInterface;

import java.io.File;
import java.io.FileInputStream;

import java.rmi.Naming;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import Fichero.Fichero;

public class Cliente {
    private static String nombre;
    private static String archivo;
    private static String dir;
    private static String URL_DiscoCliente;
    private static ServicioAutenticacionInterface autenticacion;
    private static ServicioGestorInterface gestor;
    private static ServicioClOperadorInterface accesoMetodosClOp;
    private static ServicioDatosInterface datos;
    private static ServicioSrOperadorInterface servicioSrOp;
    private static ArrayList<String> clientes ;



    public static void main(String[] args) throws Exception {
        gestor =(ServicioGestorInterface) Naming.lookup("rmi://localhost:2000/gestor");
        autenticacion = (ServicioAutenticacionInterface) Naming.lookup("rmi://localhost:2000/autenticacion");
        datos = (ServicioDatosInterface) Naming.lookup("rmi://localhost:2000/datos");



        int opcion = 0;
        Scanner sc = new Scanner(System.in);
        while (opcion != 3) {
            System.out.println("------------------------");
            System.out.println("MENÚ REGISTRO E INICIO DE CLIENTE");
            System.out.println("------------------------");
            System.out.println("1.- Registrar nuevo cliente");
            System.out.println("2.- Autenticarse en el sistema");
            System.out.println("3.- Salir");
            System.out.print("Selecciona opción: ");

            try {
                opcion = sc.nextInt();
            } catch(InputMismatchException e) {
                System.out.println("Debes introducir un número");
                sc.nextLine();
            }

                switch(opcion){
                    case 1:
                        System.out.println("REGISTRO DE CLIENTES");
                        System.out.println("Introduzca el nombre del cliente a registrar");
                        sc.nextLine();
                        nombre = sc.nextLine();

                        try {

                            if (autenticacion.registrarCliente(nombre)) {
                                System.out.println("Se ha registrado correctamente el cliente: " + nombre);
                            } else {
                                System.out.println("El cliente '" + nombre + "' ya existe.");
                            }
                        } catch (RemoteException e) {
                            System.out.println("Error de conexión con el servidor: " + e.getMessage());
                        }
                        System.out.println();

                        break;

                    case 2:
                        System.out.println("AUTENTICACIÓN DE CLIENTES");
                        System.out.println("Introduzca el nombre del cliente a autenticar");
                        sc.nextLine();
                        nombre = sc.nextLine();

                        int idSesion = autenticacion.autenticarCliente(nombre);

                        if (idSesion == 0) {
                            System.out.println(" Autenticación fallida. El cliente no existe o los repositorios no están disponibles.");
                            break;
                        } else {
                            // Obtener el repositorio asignado
                            int idRepositorioCliente = datos.buscarRepositorio(idSesion);

                            // Crear e iniciar el servicio Disco-Cliente
                            ServicioDiscoClienteImpl disco = new ServicioDiscoClienteImpl();
                            URL_DiscoCliente = "rmi://localhost:2000/DiscoCliente/" + idRepositorioCliente;
                            Naming.rebind(URL_DiscoCliente, disco);

                            // Obtener la URL del servicio Cliente-Operador
                            String direccionServicioClOp = gestor.getURLClienteOperador(idSesion);

                            // Buscar el objeto remoto para acceder a métodos de gestión de archivos
                            accesoMetodosClOp = (ServicioClOperadorInterface) Naming.lookup(direccionServicioClOp);


                            // // Conecta con el servicio del servidor operador
                            String urlSrOp = gestor.getURLServidorOperador(idSesion);
                            servicioSrOp = (ServicioSrOperadorInterface) Naming.lookup(urlSrOp);

                            System.out.println("Conectado al servicio Cliente-Operador.");
                        }




                        int opcion2 = 0;
                        while(opcion2 != 8) {
                            System.out.println("---------------");
                            System.out.println("MENÚ DE CLIENTE");
                            System.out.println("---------------");
                            System.out.println("1.- Subir fichero.");
                            System.out.println("2.- Bajar fichero.");
                            System.out.println("3.- Borrar fichero.");
                            System.out.println("4.- Numero de veces que se ha bajado un fichero.");
                            System.out.println("5.- Compartir fichero(Opcional).");
                            System.out.println("6.- Listar ficheros.");
                            System.out.println("7.- Listar clientes del sistema.");
                            System.out.println("8.- Salir.");
                            System.out.print("Selecciona opción: ");

                            opcion2 = sc.nextInt();

                            switch (opcion2) {

                                case 1: // SUBIR FICHERO
                                    try {
                                        System.out.println("Introduce la ruta donde se encuentra el archivo:");
                                        sc.nextLine();
                                        dir = sc.nextLine();

                                        System.out.println("Introduce el archivo a subir:");
                                        archivo = sc.nextLine();

                                        File testFile = new File(dir, archivo);
                                        if (!testFile.exists()) {
                                            System.out.println("El archivo no existe en: " + testFile.getAbsolutePath());
                                            break;
                                        } else {
                                            System.out.println("Archivo encontrado: " + testFile.getAbsolutePath());
                                        }

                                        Fichero fich = crearFicheroManual(dir, archivo, nombre);

                                        if (fich.obtenerPeso() <= 0) {
                                            System.out.println("El fichero no se ha cargado correctamente (peso 0).");
                                            break;
                                        }

                                        if (fich.obtenerChecksum() == 0) {
                                            System.out.println("Checksum inválido.");
                                            break;
                                        }

                                        boolean subido = accesoMetodosClOp.guardarFichero(fich);

                                        if (subido) {
                                            System.out.println(" Fichero subido correctamente.");
                                        } else {
                                            System.out.println(" No se pudo subir el fichero.");
                                        }

                                        System.out.println();
                                    } catch (Exception e) {
                                        System.out.println(" Error al preparar o subir el fichero:");
                                        e.printStackTrace();
                                        System.out.println();
                                    }
                                    break;



                                case 2: // BAJAR FICHERO
                                    System.out.println("Introduce el nombre del fichero a descargar:");
                                    sc.nextLine();
                                    archivo = sc.nextLine();

                                    try {
                                        gestor.iniciarDescarga(idSesion, archivo, nombre);
                                        System.out.println("Fichero descargado correctamente.");
                                    } catch (RemoteException e) {
                                        System.out.println("Error al contactar con el servidor: " + e.getMessage());
                                    } catch (Exception e) {
                                        System.out.println("Error inesperado: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                    break;


                                case 3: // BORRAR FICHERO
                                    System.out.println("Introduce el fichero a borrar:");
                                    sc.nextLine();
                                    archivo = sc.nextLine();

                                    System.out.println("El fichero es: " + archivo + ", y el cliente es: " + nombre);

                                    if (accesoMetodosClOp.borrarFichero(archivo, nombre)) {
                                        System.out.println("Fichero borrado correctamente.");
                                    } else {
                                        System.out.println("Fichero NO borrado. No encontrado o no existe.");
                                    }

                                    System.out.println();
                                    break;


                                case 4: //NUMERO DE DESCARGAS
                                    System.out.println(" Número de descargas de tus ficheros:");
                                    List<String> nombresFicheros = datos.listarFicherosCliente(nombre.toUpperCase());

                                    int idRepositorio = datos.buscarRepositorio(idSesion);
                                    String nombreRepositorio = datos.listarRepositoriosconID().get(idRepositorio);

                                    for (String fichero : nombresFicheros) {
                                        String clave = nombreRepositorio.toUpperCase() + ":" + fichero;
                                        int descargas = datos.obtenerNumeroDescargas(clave);
                                        System.out.println("-" + fichero + " - Descargas: " + descargas);
                                    }
                                    break;



                                case 5: // COMPARTIR FICHERO
                                    System.out.println("Opción no disponible.");
                                    break;


                                case 6: // LISTAR FICHEROS DE CLIENTE
                                    System.out.println("Ficheros del cliente " + nombre.toUpperCase() + ":");
                                    List<String> ficherosCliente = datos.listarFicherosCliente(nombre.toUpperCase());

                                    if (ficherosCliente.isEmpty()) {
                                        System.out.println("No hay ficheros.");
                                    } else {
                                        for (String f : ficherosCliente) {
                                            System.out.println("- " + f);
                                        }
                                    }
                                    break;


                                case 7: // LISTAR CLIENTES DE REPOSITORIO
                                    try {
                                        idRepositorio = datos.buscarRepositorio(idSesion);
                                        List<String> listadoClientes = datos.listarHistorialClientesRepositorio(idRepositorio);


                                        if (!listadoClientes.isEmpty()) {
                                            System.out.println("Clientes que han estado en este repositorio:");
                                            for (String cliente : listadoClientes) {
                                                System.out.println("- " + cliente);
                                            }
                                            System.out.println();
                                        } else {
                                            System.out.println("No hay clientes registrados en el historial de este repositorio.");
                                        }
                                    } catch (RemoteException e) {
                                        System.out.println("Error al obtener la lista de clientes: " + e.getMessage());
                                    }
                                    break;


                                case 8: // DESAUTENTICAR CLIENTE
                                    autenticacion.desautenticarCliente(idSesion);
                                    break;

                                default:
                                    System.out.println("Opción no correcta.");
                                    System.out.println();
                                    break;
                            }
                        }


                                break;

                    case 3:
                        System.out.println("Cerrando menú");
                        break;

                    default:

                        System.out.println("Opcion no valida");

                        break;
                }


        }

    }


    private static Fichero crearFicheroManual(String ruta, String nombreFichero, String propietario) throws Exception {
        File f = new File(ruta, nombreFichero);
        long peso = f.length();
        byte[] contenido = new byte[(int) peso];
        FileInputStream fis = new FileInputStream(f);
        int leidos = fis.read(contenido);
        fis.close();

        java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        crc.update(contenido);
        long checksum = crc.getValue();


        Fichero fich = new Fichero(ruta, nombreFichero, propietario);

        java.lang.reflect.Field campoPeso = Fichero.class.getDeclaredField("peso");
        campoPeso.setAccessible(true);
        campoPeso.set(fich, peso);

        java.lang.reflect.Field campoChecksum = Fichero.class.getDeclaredField("checksum");
        campoChecksum.setAccessible(true);
        campoChecksum.set(fich, checksum);

        java.lang.reflect.Field campoData = Fichero.class.getDeclaredField("data");
        campoData.setAccessible(true);
        campoData.set(fich, contenido);

        return fich;
    }


}
