# Sistema distribuido de ficheros con Java RMI

Sistema cliente-servidor en el que varios clientes guardan sus ficheros en repositorios remotos, coordinados por un servidor central. Cada componente es una aplicación independiente y se comunican mediante Java RMI.

## Arquitectura
- **Servidor**: registra y autentica clientes y repositorios, y asigna a cada cliente su repositorio. Expone los servicios de datos, autenticación y gestión.
- **Repositorio**: almacena los ficheros de sus clientes y permite consultarlos.
- **Cliente**: sube, descarga, borra y lista sus ficheros, y consulta cuántas veces se ha descargado cada uno.

## Funcionalidades
- Registro y autenticación de clientes y repositorios con ID de sesión.
- Subida de ficheros con validación de tamaño y checksum.
- Contador de descargas por fichero.
- Registro dinámico de servicios RMI por repositorio.

## Tecnologías
Java · Java RMI (`Registry`, `Naming`, `UnicastRemoteObject`) · IntelliJ IDEA

## Cómo ejecutarlo
Con el proyecto abierto en IntelliJ, ejecuta las tres aplicaciones en este orden:
1. `servidor.Servidor` (puerto 2000)
2. `Repositorio.Repositorio`: registra un repositorio y autentícate
3. `Cliente.Cliente`: registra un cliente y autentícate

## Lo que aprendí
Diseñar un sistema repartido en componentes independientes, exponer y consumir objetos remotos y gestionar sesiones entre procesos.
