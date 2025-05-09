package mx.ift.sns.negocio.utils.csv;

import mx.ift.sns.negocio.ng.arrendamientos.ValidadorArchivoArrendador;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Busca en un archivo CSV la fila con primer campo igual a search.
 */
public class BuscadorArchivoCSV extends ValidadorArchivoCSV {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidadorArchivoArrendador.class);

    /** Valor a buscar en el primer campo. */
    private String search;

    /** Fila encontrada. */
    private String[] fila;

    /** Indica si hemos encontrado el campo buscado. */
    private boolean encontrado = false;

    /**
     * Valida una fila del archivo.
     * @param valores d
     */
    public void checkFila(String[] valores) {

        if (valores.length >= 1) {
            if ((StringUtils.equals(valores[0], search))) {

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("encontrado {}", search);
                }

                encontrado = true;
                fila = valores;
                setTerminar(true);
            }
        }
    }

    /**
     * @return the search
     */
    public String getSearch() {
        return search;
    }

    /**
     * @param search the search to set
     */
    public void setSearch(String search) {
        this.search = search;
    }

    /**
     * @return the encontrado
     */
    public boolean isEncontrado() {
        return encontrado;
    }

    /**
     * @param encontrado the encontrado to set
     */
    public void setEncontrado(boolean encontrado) {
        this.encontrado = encontrado;
    }

    /**
     * @return the fila
     */
    public String[] getFila() {
        return fila;
    }

    /**
     * @param fila the fila to set
     */
    public void setFila(String[] fila) {
        this.fila = fila;
    }
}
