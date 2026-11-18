# Árbol B+ en Java

Segunda práctica de implementación de estructuras de datos (SOFT-10, Universidad CENFOTEC).

Implementación de un árbol B+ de orden configurable con una interfaz de consola para insertar,
eliminar, buscar y recorrer por rango elementos formados por una clave entera y un dato de texto.

## Cómo ejecutarlo

- **IntelliJ IDEA:** abrir la carpeta del proyecto y ejecutar `Main`.
- **Terminal:**
  ```
  javac -d out src/*.java
  java -cp out Main
  ```

Al iniciar, el programa pide el orden del árbol (mínimo 3; con Enter se usa 4). La opción 7 del menú
carga datos de ejemplo para probar rápidamente.

## Estructura

| Archivo | Contenido |
|---|---|
| `src/NodoArbolBMas.java` | Nodo del árbol: `claves`, `esHoja`, `hijos`, `datos` (solo hojas) y `siguiente` (enlace entre hojas). |
| `src/ArbolBMas.java` | El árbol (atributos `raiz` y `orden`): inserción, búsqueda, `eliminar()`, `recorrerRango()` e impresión. |
| `src/Main.java` | `main()` y `menu()` de consola, con validación de entradas. |

## Decisiones de implementación

- **Reglas del árbol:** cada nodo distinto de la raíz tiene entre ⌈m/2⌉ y m hijos (entre ⌈m/2⌉−1 y m−1 claves).
  La raíz tiene entre 2 y m hijos. Los datos solo se guardan en las hojas.
- **Inserción:** la clave y su dato se insertan en la hoja correspondiente. Si un nodo se desborda, se divide
  al regresar de la recursión. Al dividir una hoja, la nueva hoja se intercala en la lista enlazada (`siguiente`).
  No se permiten claves repetidas.
- **Eliminación (`eliminar(clave)`):** se borra la clave de su hoja y, al regresar de la recursión, cada nodo que
  quedó con menos claves del mínimo se rebalancea:
  1. pidiendo prestada una clave al hermano izquierdo o derecho, si alguno tiene claves de sobra;
  2. si no, fusionándose con un hermano (el padre pierde el separador entre ambos).

  Si la raíz interna queda sin claves, su único hijo pasa a ser la raíz y el árbol baja un nivel.
  Por último, si la clave eliminada seguía usándose como separador, se reemplaza por su sucesora.
- **Búsqueda:** siempre desciende hasta una hoja, porque en un B+ una clave en un nodo interno es solo un separador.
- **Recorrido por rango (`recorrerRango(claveInicial, n)`):** busca la hoja de la clave inicial (o de la primera
  mayor que ella) e imprime los siguientes `n` elementos avanzando por los enlaces `siguiente`, sin volver a
  recorrer el árbol desde la raíz.
