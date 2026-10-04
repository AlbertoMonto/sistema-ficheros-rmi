//Autor: Alberto Montoya Gutiérrez
//amontoya123@alumno.uned.es

package Repositorio;

import Repositorio.ServicioClOperadorImpl;


import Fichero.Fichero;

import java.io.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServicioClOperadorImpl extends UnicastRemoteObject implements ServicioClOperadorInterface {

    public ServicioClOperadorImpl() throws RemoteException {
    }

    @Override
    public boolean guardarFichero(Fichero fichero) throws RemoteException{

        File repositorio = new File("repositorio", fichero.obtenerPropietario());
        if (!repositorio.exists()) {
            repositorio.mkdirs();
        }
        String nombreFichero = new File(repositorio, fichero.obtenerNombre()).getPath();


        try {
            OutputStream os = new FileOutputStream(nombreFichero);
            if (fichero.escribirEn(os)==false)
            {
                os.close();
                return false;
            }
            os.close();

            System.out.println("Fichero " + fichero.obtenerNombre()  + " de " + fichero.obtenerPropietario() + "  recibido");
        } catch (NullPointerException | IOException fnfe) {
            return false;
        }
        return true;
    }

    @Override
    public boolean borrarFichero(String archivo, String carpeta) throws RemoteException {
        try {
            File carpetaRepo = new File("repositorio", carpeta);
            File fichero = new File(carpetaRepo, archivo);
            return fichero.exists() && fichero.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }





}
