package mx.ift.sns.negocio.num.model;

import java.io.Serializable;

import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.series.Nir;

/**
 * Representa un numero o numeracion.
 */
// NIR SNA NUM
// xxx xxxx xxxx
// xx xxxx xxx

// CLAVE SNA NUM
// xxx xxxx xxxx
// xx xxxx xxx
public class Numero implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Longitud maxima de un numero. */
    public static final int MAX_LONG_NUMERO = 10;

    /** Posicion del campo NumberFrom. */
    public static final int POS_NUMBER_FROM = 0;
    /** Posicion del campo NumberTo. */
    public static final int POS_NUMBER_TO = 1;
    /** Posicion del campo arrendador en el fichero de arrendatario. */
    public static final int POS_IDO_ARRDOR_ARRTARIO = 2;
    /** Posicion del campo arrendatario en el fichero de arrendador. */
    public static final int POS_IDO_ARRTARIO_ARRDOR = 2;
    /** Posicion del campo arrendador en el fichero de arrendador. */
    public static final int POS_IDO_ARRDOR_ARRDOR = 3;
    /** Posicion del campo arrendatario en el fichero de arrendatario. */
    public static final int POS_IDO_ARRTARIO_ARRTARIO = 3;

    /** Numeracion. */
    private String numero;

    /** Nir del numero. */
    private Nir nir;

    /** Codigo del nir. */
    private String codigoNir;

    /** Serie del numero. */
    private Serie serie;

    /** SNA de la serie. */
    private String sna;

    /** Rango. */
    private RangoSerie rango;

    /** Clave de servicio. */
    private ClaveServicio clave;

    /** Numero interno central. */
    private String numeroInterno;

    /** Es numero local (7 digitos). */
    private boolean local;

    /** Es numero nacional (10 digitos). */
    private boolean nacional;

    private String zona;

    /**
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * @return the nir
     */
    public Nir getNir() {
        return nir;
    }

    /**
     * @param nir the nir to set
     */
    public void setNir(Nir nir) {
        this.nir = nir;
    }

    /**
     * @return the serie
     */
    public Serie getSerie() {
        return serie;
    }

    /**
     * @param serie the serie to set
     */
    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    /**
     * @return the numeroLocal
     */
    public String getNumeroInterno() {
        return numeroInterno;
    }

    /**
     * @param numeroInterno the numeroLocal to set
     */
    public void setNumeroInterno(String numeroInterno) {
        this.numeroInterno = numeroInterno;
    }

    /**
     * @return the codigoNir
     */
    public String getCodigoNir() {
        return codigoNir;
    }

    /**
     * @param codigoNir the codigoNir to set
     */
    public void setCodigoNir(String codigoNir) {
        this.codigoNir = codigoNir;
    }

    /**
     * @return the sna
     */
    public String getSna() {
        return sna;
    }

    /**
     * @param sna the sna to set
     */
    public void setSna(String sna) {
        this.sna = sna;
    }

    /**
     * @return the local
     */
    public boolean isLocal() {
        return local;
    }

    /**
     * @param local the local to set
     */
    public void setLocal(boolean local) {
        this.local = local;
    }

    /**
     * @return the nacional
     */
    public boolean isNacional() {
        return nacional;
    }

    /**
     * @param nacional the nacional to set
     */
    public void setNacional(boolean nacional) {
        this.nacional = nacional;
    }

    /**
     * @return the clave
     */
    public ClaveServicio getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(ClaveServicio clave) {
        this.clave = clave;
    }

    /**
     * @return the rango
     */
    public RangoSerie getRango() {
        return rango;
    }

    /**
     * @param rango the rango to set
     */
    public void setRango(RangoSerie rango) {
        this.rango = rango;
    }

    /**
     * Devuelve true si el numero existe.
     * @return true/false
     */
    public boolean existe() {
        return (nir != null) || (clave != null);
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("numero ");

        if (isNacional()) {
            builder.append("nacional ");
        } else if (isLocal()) {
            builder.append("local ");
        }

        builder.append("={numero=");
        builder.append(numero);

        if (isNacional()) {

            if (nir != null) {
                builder.append(" nir=");
                builder.append(nir);
            }

            if (clave != null) {
                builder.append(" clave=");
                builder.append(clave);
            }
        }

        builder.append(" sna=");
        builder.append(serie);

        builder.append(" num_interno=");
        builder.append(numeroInterno);

        builder.append("}");

        return builder.toString();
    }
}
