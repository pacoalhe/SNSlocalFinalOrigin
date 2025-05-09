package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.util.ArrayList;

import mx.ift.sns.modelo.cpsi.CPSIUtils;
import mx.ift.sns.modelo.cpsn.CPSNUtils;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Clase auxiliar para búsqueda de equipos de señalización. Contiene los filtros que negocio enviará a los DAOS para que
 * construya las querys. Cada variable asignada se considerará un filtro a satisfacer.
 */
public class FiltroBusquedaEquipoSenal implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Pst. */
    private Proveedor proveedor;

    /** Código binario del CPSN. */
    private String codBinario;

    /** Código decimal individul del CPSN. */
    private String decimalIndividual;

    /** Código decimal total. */
    private String decimalTotal;

    /** Código formato decimal. */
    private String formatoDecimal;

    /** Constructor por defecto. */
    public FiltroBusquedaEquipoSenal() {
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getFiltrosEquipoSenal() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(CPSNUtils.FILTRO_PROVEEDOR, CPSNUtils.FILTRO_ALIAS_TABLA);
            filtro.setValor(proveedor, Proveedor.class);
            filtros.add(filtro);
        }

        if (codBinario != null && !codBinario.isEmpty()) {
            int numMin = CPSNUtils.valorMinBloque(codBinario);
            int numMax = CPSNUtils.valorMaxBloque(codBinario);

            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(CPSNUtils.FILTRO_CODIGO_BINARIO, CPSNUtils.FILTRO_ALIAS_TABLA);
            filtro.setValor(numMin, Integer.class);
            filtro.setValorSecundario(numMax, Integer.class);
            filtros.add(filtro);
        }

        if (decimalIndividual != null && !decimalIndividual.isEmpty()) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(CPSNUtils.FILTRO_CODIGO_DECIMAL, CPSNUtils.FILTRO_ALIAS_TABLA);
            filtro.setValor(Integer.valueOf(decimalIndividual), Integer.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getFiltrosEquipoSenalCpsi() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(CPSIUtils.FILTRO_PROVEEDOR, CPSIUtils.FILTRO_ALIAS_TABLA_CPSI);
            filtro.setValor(proveedor, Proveedor.class);
            filtros.add(filtro);
        }

        if (codBinario != null && !codBinario.isEmpty()) {
            int numMin = CPSIUtils.valorMinBloque(codBinario);
            int numMax = CPSIUtils.valorMaxBloque(codBinario);

            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(CPSIUtils.FILTRO_CODIGO_BINARIO, CPSIUtils.FILTRO_ALIAS_TABLA_CPSI);
            filtro.setValor(numMin, Integer.class);
            filtro.setValorSecundario(numMax, Integer.class);
            filtros.add(filtro);
        }

        if (decimalTotal != null && !decimalTotal.isEmpty()) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(CPSIUtils.FILTRO_CODIGO_DECIMAL, CPSIUtils.FILTRO_ALIAS_TABLA_CPSI);
            filtro.setValor(Integer.valueOf(decimalTotal), Integer.class);
            filtros.add(filtro);
        }

        if (formatoDecimal != null && !formatoDecimal.isEmpty()) {
            String binario = CPSIUtils.valorFormatoDecimal(formatoDecimal);
            int numMin = CPSIUtils.valorMinBloque(binario);
            int numMax = CPSIUtils.valorMaxBloque(binario);

            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(CPSIUtils.FILTRO_FORMATO_DECIMAL, CPSIUtils.FILTRO_ALIAS_TABLA_CPSI);
            filtro.setValor(numMin, Integer.class);
            filtro.setValorSecundario(numMax, Integer.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        proveedor = null;
        codBinario = null;
        decimalIndividual = null;
        decimalTotal = null;
        formatoDecimal = null;
    }

    /**
     * Pst.
     * @return the proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Pst.
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Código binario del CPSN.
     * @return the codBinario
     */
    public String getCodBinario() {
        return codBinario;
    }

    /**
     * Código binario del CPSN.
     * @param codBinario the codBinario to set
     */
    public void setCodBinario(String codBinario) {
        this.codBinario = codBinario;
    }

    /**
     * Código decimal individul del CPSN.
     * @return the decimalIndividual
     */
    public String getDecimalIndividual() {
        return decimalIndividual;
    }

    /**
     * Código decimal individul del CPSN.
     * @param decimalIndividual the decimalIndividual to set
     */
    public void setDecimalIndividual(String decimalIndividual) {
        this.decimalIndividual = decimalIndividual;
    }

    /**
     * Código decimal total.
     * @return the decimalTotal
     */
    public String getDecimalTotal() {
        return decimalTotal;
    }

    /**
     * Código decimal total.
     * @param decimalTotal the decimalTotal to set
     */
    public void setDecimalTotal(String decimalTotal) {
        this.decimalTotal = decimalTotal;
    }

    /**
     * Código formato decimal.
     * @return the formatoDecimal
     */
    public String getFormatoDecimal() {
        return formatoDecimal;
    }

    /**
     * Código formato decimal.
     * @param formatoDecimal the formatoDecimal to set
     */
    public void setFormatoDecimal(String formatoDecimal) {
        this.formatoDecimal = formatoDecimal;
    }

}
