import java.util.ArrayList;
import java.util.List;

public class ArbolBMas {

    // Atributos
    private final int orden;
    private NodoArbolBMas raiz;

    // Constructor
    public ArbolBMas(int orden) {
        if (orden < 3) {
            throw new IllegalArgumentException("El orden del árbol B+ debe ser al menos 3.");
        }
        this.orden = orden;
        raiz = new NodoArbolBMas(true);
    }

    public int getOrden() {
        return orden;
    }

    public boolean estaVacio() {
        return raiz.getClaves().isEmpty();
    }

    // Inserción de una clave con su dato en el árbol B+.
    public boolean insertar(int clave, String dato) {
        if (buscar(clave)) {
            return false;
        }

        insertarRecursivo(this.raiz, clave, dato);

        if (this.raiz.getClaves().size() == orden) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false);
            nuevaRaiz.getHijos().add(this.raiz);
            dividirHijo(nuevaRaiz, 0);
            this.raiz = nuevaRaiz;
        }
        return true;
    }

    //insercion recursiva
    private void insertarRecursivo(NodoArbolBMas nodo, int clave, String dato) {
        if (nodo.esHoja()) {
            int posicion = 0;
            while (posicion < nodo.getClaves().size() && nodo.getClaves().get(posicion) < clave) {
                posicion++;
            }
            nodo.insertarEnHoja(posicion, clave, dato);
        } else {
            int i = indiceHijo(nodo, clave);
            NodoArbolBMas hijo = nodo.getHijos().get(i);

            // Llamada recursiva para insertar la clave primero en el hijo
            insertarRecursivo(hijo, clave, dato);

            // Evaluación de desbordamiento
            if (hijo.getClaves().size() == orden) {
                dividirHijo(nodo, i);
            }
        }
    }

    // Dividir un nodo que está lleno
    private void dividirHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas nodoLleno = padre.getHijos().get(indice);
        NodoArbolBMas nuevoNodo = new NodoArbolBMas(nodoLleno.esHoja());
        int mitad = orden / 2;

        if (nodoLleno.esHoja()) {
            int total = nodoLleno.getClaves().size();
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad, total)));
            nuevoNodo.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(mitad, total)));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));
            nodoLleno.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(0, mitad)));

            nuevoNodo.setSiguiente(nodoLleno.getSiguiente());
            nodoLleno.setSiguiente(nuevoNodo);

            // El padre recibe como separador una copia de la primera clave del nuevo nodo
            int claveMedia = nuevoNodo.getClaves().get(0);
            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        } else {
            // En nodos internos, la clave media sube y se elimina del hijo
            int claveMedia = nodoLleno.getClaves().get(mitad);
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad + 1, nodoLleno.getClaves().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            // Mover los hijos del nodo dividido
            nuevoNodo.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(mitad + 1, nodoLleno.getHijos().size())));
            nodoLleno.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(0, mitad + 1)));

            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
    }

    // Búsqueda de una clave como parte  pública del árbol.
    public boolean buscar(int clave) {
        return buscarHoja(clave).getClaves().contains(clave);
    }

    // Retorna el dato hacia una clave, o null si la clave no existe.
    public String obtenerDato(int clave) {
        NodoArbolBMas hoja = buscarHoja(clave);
        int posicion = hoja.getClaves().indexOf(clave);
        return posicion == -1 ? null : hoja.getDatos().get(posicion);
    }

    private NodoArbolBMas buscarHoja(int clave) {
        NodoArbolBMas actual = raiz;
        while (!actual.esHoja()) {
            actual = actual.getHijos().get(indiceHijo(actual, clave));
        }
        return actual;
    }

    private int indiceHijo(NodoArbolBMas nodo, int clave) {
        int i = 0;
        while (i < nodo.getClaves().size() && clave >= nodo.getClaves().get(i)) {
            i++;
        }
        return i;
    }


    // Eliminación de una clave (y su dato) como parte pública del árbol.
    // Retorna false si la clave no existía.
    public boolean eliminar(int clave) {
        boolean eliminada = eliminarRecursivo(raiz, clave);

        // Si la raíz interna se quedó sin claves, su único hijo pasa a ser la nueva raíz
        // y el árbol pierde un nivel de altura.
        if (!raiz.esHoja() && raiz.getClaves().isEmpty()) {
            raiz = raiz.getHijos().get(0);
        }

        // Con el árbol ya balanceado, se limpia el separador que pudo quedar con la clave eliminada
        if (eliminada) {
            actualizarSeparador(clave);
        }
        return eliminada;
    }

    // Eliminación recursiva de una clave.
    private boolean eliminarRecursivo(NodoArbolBMas nodo, int clave) {
        if (nodo.esHoja()) {
            int posicion = nodo.getClaves().indexOf(clave);
            if (posicion == -1) {
                return false;
            }
            nodo.eliminarDeHoja(posicion);
            return true;
        }

        int i = indiceHijo(nodo, clave);
        NodoArbolBMas hijo = nodo.getHijos().get(i);

        // Llamada recursiva para eliminar la clave primero en el hijo
        if (!eliminarRecursivo(hijo, clave)) {
            return false;
        }

        // Evaluación de subdesbordamiento al retornar de la recursividad
        if (hijo.getClaves().size() < minimoClaves()) {
            rebalancearHijo(nodo, i);
        }
        return true;
    }

    private void actualizarSeparador(int claveEliminada) {
        NodoArbolBMas actual = raiz;
        while (!actual.esHoja()) {
            int posicion = actual.getClaves().indexOf(claveEliminada);
            if (posicion != -1) {
                actual.getClaves().set(posicion, menorClave(actual.getHijos().get(posicion + 1)));
                return;
            }
            actual = actual.getHijos().get(indiceHijo(actual, claveEliminada));
        }
    }

    // Cantidad mínima de claves de cualquier nodo
    private int minimoClaves() {
        return (orden + 1) / 2 - 1;
    }

    // Menor clave de un subárbol
    private int menorClave(NodoArbolBMas nodo) {
        while (!nodo.esHoja()) {
            nodo = nodo.getHijos().get(0);
        }
        return nodo.getClaves().get(0);
    }

    // Corrige al hijo en la posición indice del padre
    private void rebalancearHijo(NodoArbolBMas padre, int indice) {
        List<NodoArbolBMas> hijos = padre.getHijos();
        NodoArbolBMas hermanoIzquierdo = indice > 0 ? hijos.get(indice - 1) : null;
        NodoArbolBMas hermanoDerecho = indice < hijos.size() - 1 ? hijos.get(indice + 1) : null;

        if (hermanoIzquierdo != null && hermanoIzquierdo.getClaves().size() > minimoClaves()) {
            prestarDeIzquierdo(padre, indice);
        } else if (hermanoDerecho != null && hermanoDerecho.getClaves().size() > minimoClaves()) {
            prestarDeDerecho(padre, indice);
        } else if (hermanoIzquierdo != null) {
            fusionarHijos(padre, indice - 1);
        } else {
            fusionarHijos(padre, indice);
        }
    }

    // El hijo en la posición indice recibe la última clave de su hermano izquierdo.
    private void prestarDeIzquierdo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas izquierdo = padre.getHijos().get(indice - 1);
        int ultima = izquierdo.getClaves().size() - 1;

        if (hijo.esHoja()) {
            hijo.insertarEnHoja(0, izquierdo.getClaves().get(ultima), izquierdo.getDatos().get(ultima));
            izquierdo.eliminarDeHoja(ultima);
            padre.getClaves().set(indice - 1, hijo.getClaves().get(0));
        } else {
            hijo.getClaves().add(0, padre.getClaves().get(indice - 1));
            padre.getClaves().set(indice - 1, izquierdo.getClaves().remove(ultima));
            hijo.getHijos().add(0, izquierdo.getHijos().remove(izquierdo.getHijos().size() - 1));
        }
    }

    private void prestarDeDerecho(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);

        if (hijo.esHoja()) {
            hijo.insertarEnHoja(hijo.getClaves().size(), derecho.getClaves().get(0), derecho.getDatos().get(0));
            derecho.eliminarDeHoja(0);
            padre.getClaves().set(indice, derecho.getClaves().get(0));
        } else {
            hijo.getClaves().add(padre.getClaves().get(indice));
            padre.getClaves().set(indice, derecho.getClaves().remove(0));
            hijo.getHijos().add(derecho.getHijos().remove(0));
        }
    }

    private void fusionarHijos(NodoArbolBMas padre, int indice) {
        NodoArbolBMas izquierdo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);

        if (izquierdo.esHoja()) {
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getDatos().addAll(derecho.getDatos());
            izquierdo.setSiguiente(derecho.getSiguiente());
        } else {
            izquierdo.getClaves().add(padre.getClaves().get(indice));
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getHijos().addAll(derecho.getHijos());
        }

        padre.getClaves().remove(indice);
        padre.getHijos().remove(indice + 1);
    }
    //recorrer
    public int recorrerRango(int claveInicial, int n) {
        if (n <= 0) {
            return 0;
        }

        // Búsqueda
        NodoArbolBMas hoja = buscarHoja(claveInicial);
        int posicion = 0;
        while (posicion < hoja.getClaves().size() && hoja.getClaves().get(posicion) < claveInicial) {
            posicion++;
        }

        // Recorrido
        int mostrados = 0;
        while (hoja != null && mostrados < n) {
            while (posicion < hoja.getClaves().size() && mostrados < n) {
                mostrados++;
                System.out.printf("  %3d. Clave: %-6d Dato: %s%n",mostrados, hoja.getClaves().get(posicion), hoja.getDatos().get(posicion));
                 posicion++;
            }
            hoja = hoja.getSiguiente();
            posicion = 0;
        }
        return mostrados;
    }

    // Impresión de la lista enlazada de hojas, de izquierda a derecha.
    public void imprimirHojas() {
        NodoArbolBMas hoja = raiz;
        while (!hoja.esHoja()) {
            hoja = hoja.getHijos().get(0);
        }

        StringBuilder salida = new StringBuilder("  ");
        while (hoja != null) {
            salida.append(hoja.getClaves());
            hoja = hoja.getSiguiente();
            if (hoja != null) {
                salida.append(" -> ");
            }
        }
        salida.append(" -> null");
        System.out.println(salida);
    }

    // Impresión del árbol como parte de la interfaz pública del árbol
    public void imprimirArbol() {
        imprimirNodo(raiz, "", true);
    }

    // Impresión recursiva privada de los nodos a partir de uno inicial
    private void imprimirNodo(NodoArbolBMas nodo, String indentacion, boolean esUltimo) {
        System.out.println(indentacion + "+- " + (nodo.esHoja() ? "Hoja > " : "Interno > ") + nodo.getClaves());
        indentacion += esUltimo ? "   " : "|  ";
        for (int i = 0; i < nodo.getHijos().size(); i++) {
            imprimirNodo(nodo.getHijos().get(i), indentacion, i == (nodo.getHijos().size() - 1));
        }
    }
}
