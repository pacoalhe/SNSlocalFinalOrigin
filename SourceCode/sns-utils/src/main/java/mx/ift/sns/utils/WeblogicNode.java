package mx.ift.sns.utils;

/**
 * Utilidades para ver el nodo.
 */
public final class WeblogicNode {

    /**
     * Constructor privado.
     */
    private WeblogicNode() {

    }

    /**
     * Devuelve el nombre del nodo.
     * @return nombre
     */
    public static String getName() {
        String nodo = null;
        try {
            nodo = System.getProperty("weblogic.Name");
        } catch (Exception e) {
            ;
        }

        return nodo;
    }
}
