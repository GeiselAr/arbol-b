import java.util.ArrayList;
import java.util.List;


public class NodoArbolBMas {

    // Atributos
    private boolean esHoja;
    private List<Integer> claves;
    private List<NodoArbolBMas> hijos;
    private List<String> datos;
    private NodoArbolBMas siguiente;

    // Constructor
    public NodoArbolBMas(boolean esHoja) {
        this.esHoja = esHoja;
        this.claves = new ArrayList<>();
        this.hijos = new ArrayList<>();
        this.datos = esHoja ? new ArrayList<>() : null;
        this.siguiente = null;
    }

    // Getters
    public boolean esHoja() {
        return esHoja;
    }

    public List<Integer> getClaves() {
        return claves;
    }

    public List<NodoArbolBMas> getHijos() {
        return hijos;
    }

    public List<String> getDatos() {
        return datos;
    }

    public NodoArbolBMas getSiguiente() {
        return siguiente;
    }

    // Setters
    public void setClaves(List<Integer> claves) {
        this.claves = claves;
    }

    public void setHijos(List<NodoArbolBMas> hijos) {
        this.hijos = hijos;
    }

    // solo las hojas almacenan datos
    public void setDatos(List<String> datos) {
        if (esHoja) {
            this.datos = datos;
        }
    }

    // solo las hojas se enlazan entre sí
    public void setSiguiente(NodoArbolBMas siguiente) {
        if (esHoja) {
            this.siguiente = siguiente;
        }
    }

    // Inserta una clave y su dato en la posición indicada de una hoja.
    public void insertarEnHoja(int posicion, int clave, String dato) {
        claves.add(posicion, clave);
        datos.add(posicion, dato);
    }

    // Elimina la clave y el dato que están en la posición indicada de una hoja.
    public void eliminarDeHoja(int posicion) {
        claves.remove(posicion);
        datos.remove(posicion);
    }
}
