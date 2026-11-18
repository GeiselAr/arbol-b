import java.util.Scanner;

public class Main {

    private static final int ORDEN_POR_DEFECTO = 4;
    private static final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("-----------------------------------------------");
        System.out.println("        SIMULADOR DE ÁRBOL B+ EN JAVA");
        System.out.println("------------------------------------------------");
        System.out.println("Cada nodo del árbol puede tener entre m/2 y m hijos (m = orden).");

        // Creación del árbol con el orden elegido por el usuario
        int orden = leerEnteroConDefecto(
                "Ingrese el orden del árbol (mínimo 3, Enter para usar " + ORDEN_POR_DEFECTO + "): ",
                ORDEN_POR_DEFECTO, 3);
        ArbolBMas arbol = new ArbolBMas(orden);
        System.out.println("Árbol B+ de orden " + orden + " creado.");


        menu(arbol);
        entrada.close();
        System.out.println("¡Hasta luego!");
    }

    // Muestra el menú de opciones de forma repetida hasta que el usuario decida salir.
    private static void menu(ArbolBMas arbol) {
        int opcion;
        do {
            System.out.println();
            System.out.println("----------------- MENÚ PRINCIPAL -----------------");
            System.out.println("  1. Insertar un elemento");
            System.out.println("  2. Eliminar un elemento");
            System.out.println("  3. Buscar un elemento");
            System.out.println("  4. Recorrer un rango de elementos");
            System.out.println("  5. Ver la estructura del árbol");
            System.out.println("  6. Ver las hojas enlazadas");
            System.out.println("  7. Cargar los datos de ejemplo");
            System.out.println("  0. Salir");
            System.out.println("--------------------------------------------------");
            opcion = leerEntero("Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1 -> opcionInsertar(arbol);
                case 2 -> opcionEliminar(arbol);
                case 3 -> opcionBuscar(arbol);
                case 4 -> opcionRecorrerRango(arbol);
                case 5 -> opcionVerArbol(arbol);
                case 6 -> opcionVerHojas(arbol);
                case 7 -> opcionCargarEjemplo(arbol);
                case 0 -> System.out.println("Saliendo del programa...");
                default -> System.out.println("Opción no válida. Elija un número del 0 al 7.");
            }
        } while (opcion != 0);
    }


    private static void opcionInsertar(ArbolBMas arbol) {
        System.out.println(">> INSERTAR ELEMENTO");
        int clave = leerEntero("Clave (número entero): ");
        String dato = leerTextoNoVacio("Dato asociado (texto): ");

        if (arbol.insertar(clave, dato)) {
            System.out.println("Se insertó la clave " + clave + " con el dato \"" + dato + "\".");
            mostrarArbol(arbol);
        } else {
            System.out.println("La clave " + clave + " ya existe (dato: \"" + arbol.obtenerDato(clave)
                    + "\"). No se permiten claves repetidas.");
        }
    }

    private static void opcionEliminar(ArbolBMas arbol) {
        System.out.println(">> ELIMINAR ELEMENTO");
        if (avisarSiVacio(arbol)) {
            return;
        }
        int clave = leerEntero("Clave a eliminar: ");
        String dato = arbol.obtenerDato(clave);

        if (arbol.eliminar(clave)) {
            System.out.println("Se eliminó la clave " + clave + " (dato: \"" + dato + "\").");
            mostrarArbol(arbol);
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void opcionBuscar(ArbolBMas arbol) {
        System.out.println(">> BUSCAR ELEMENTO");
        if (avisarSiVacio(arbol)) {
            return;
        }
        int clave = leerEntero("Clave a buscar: ");
        String dato = arbol.obtenerDato(clave);

        if (dato != null) {
            System.out.println("Encontrado -> Clave: " + clave + " | Dato: " + dato);
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void opcionRecorrerRango(ArbolBMas arbol) {
        System.out.println(">> RECORRER RANGO");
        if (avisarSiVacio(arbol)) {
            return;
        }
        int claveInicial = leerEntero("Clave desde la que se inicia el recorrido: ");
        int n = leerEnteroMinimo("Cantidad de elementos a mostrar (n): ", 1);

        System.out.println("Elementos a partir de la clave " + claveInicial + ":");
        int mostrados = arbol.recorrerRango(claveInicial, n);

        if (mostrados == 0) {
            System.out.println("  No hay elementos con clave mayor o igual a " + claveInicial + ".");
        } else if (mostrados < n) {
            System.out.println("Se llegó al final del árbol: solo había " + mostrados + " elemento(s) disponibles.");
        }
    }

    private static void opcionVerArbol(ArbolBMas arbol) {
        System.out.println(">> ESTRUCTURA DEL ÁRBOL (orden " + arbol.getOrden() + ")");
        if (avisarSiVacio(arbol)) {
            return;
        }
        arbol.imprimirArbol();
    }

    private static void opcionVerHojas(ArbolBMas arbol) {
        System.out.println(">> HOJAS ENLAZADAS (de izquierda a derecha)");
        if (avisarSiVacio(arbol)) {
            return;
        }
        arbol.imprimirHojas();
    }

    // Inserta un conjunto de datos de prueba
    private static void opcionCargarEjemplo(ArbolBMas arbol) {
        System.out.println(">> CARGAR DATOS DE EJEMPLO");
        int[] claves = {10, 20, 5, 6, 15, 30, 25, 40, 45, 50};
        String[] datos = {"Ana", "Bruno", "Carla", "Diego", "Elena", "Fabián", "Gabriela", "Hugo", "Irene", "Jorge"};

        int insertados = 0;
        for (int i = 0; i < claves.length; i++) {
            if (arbol.insertar(claves[i], datos[i])) {
                insertados++;
            }
        }
        System.out.println("Se insertaron " + insertados + " de " + claves.length
                + " elementos (las claves que ya existían se omitieron).");
        mostrarArbol(arbol);
    }

    // Muestra el estado del árbol después de una modificación.
    private static void mostrarArbol(ArbolBMas arbol) {
        System.out.println("Estado actual del árbol:");
        if (arbol.estaVacio()) {
            System.out.println("  (vacío)");
        } else {
            arbol.imprimirArbol();
        }
    }

    // Avisa al usuario si el árbol está vacío ,retorna true en ese caso.
    private static boolean avisarSiVacio(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío. Inserte elementos primero (opción 1 o 7).");
            return true;
        }
        return false;
    }

    // Lee un número entero, repitiendo la pregunta hasta que la entrada sea válida.
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = entrada.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("  Entrada no válida: debe escribir un número entero.");
            }
        }
    }

    // Lee un número entero que debe ser mayor o igual a un mínimo.
    private static int leerEnteroMinimo(String mensaje, int minimo) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= minimo) {
                return valor;
            }
            System.out.println("  El valor debe ser al menos " + minimo + ".");
        }
    }

    // Lee un número entero con un valor por defecto si el usuario solo presiona Enter.
    private static int leerEnteroConDefecto(String mensaje, int porDefecto, int minimo) {
        while (true) {
            System.out.print(mensaje);
            String linea = entrada.nextLine().trim();
            if (linea.isEmpty()) {
                return porDefecto;
            }
            try {
                int valor = Integer.parseInt(linea);
                if (valor >= minimo) {
                    return valor;
                }
                System.out.println("  El valor debe ser al menos " + minimo + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Entrada no válida: debe escribir un número entero.");
            }
        }
    }

    // Lee un texto que no puede quedar vacío.
    private static String leerTextoNoVacio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = entrada.nextLine().trim();
            if (!linea.isEmpty()) {
                return linea;
            }
            System.out.println("  El dato no puede estar vacío.");
        }
    }
}
