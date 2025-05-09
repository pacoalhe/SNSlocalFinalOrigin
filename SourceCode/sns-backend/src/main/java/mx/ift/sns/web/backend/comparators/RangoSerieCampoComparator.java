package mx.ift.sns.web.backend.comparators;

import java.util.Comparator;

import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.series.IRangoSerie;
import mx.ift.sns.utils.date.FechasUtils;

import org.primefaces.model.SortOrder;

/** Comparador para campos específicos de Clase RangoSerie. */
public class RangoSerieCampoComparator implements Comparator<IRangoSerie> {

    /** Campo a comparar entre clases. */
    private String campoComparador;

    /** Tipo de ordenación PrimeFaces. */
    private SortOrder tipoOrdenacion;

    /**
     * Constructor específico.
     * @param pCampoComparador Campo a comparar entre clases.
     * @param pTipoOrdenacion Tipo de Ordenación PrimeFaces
     */
    public RangoSerieCampoComparator(String pCampoComparador, SortOrder pTipoOrdenacion) {
        this.campoComparador = pCampoComparador;
        this.tipoOrdenacion = pTipoOrdenacion;
    }

    @Override
    public int compare(IRangoSerie arg0, IRangoSerie arg1) {
        try {
            String valueA;
            String valueB;
            if (arg0 instanceof RangoSerie) {
                valueA = this.getFieldValue((RangoSerie) arg0, campoComparador);
                valueB = this.getFieldValue((RangoSerie) arg1, campoComparador);
            } else {
                valueA = this.getFieldValue((RangoSerieNng) arg0, campoComparador);
                valueB = this.getFieldValue((RangoSerieNng) arg1, campoComparador);
            }

            int value = valueA.compareTo(valueB);
            return SortOrder.ASCENDING.equals(tipoOrdenacion) ? value : -1 * value;

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * Recupera el valor de un campo específico de un rango de numeración geográfica.
     * @param pRango Información del Rango.
     * @param pCampo Valor del campo a recuperar.
     * @return String con el valor del campo a recuperar.
     */
    public String getFieldValue(RangoSerie pRango, String pCampo) {
        String fieldValue = "";
        try {
            if (pCampo.equals("serie.nir.abn.codigoAbn")) {
                fieldValue = String.valueOf(pRango.getSerie().getNir().getAbn().getCodigoAbn());
            } else if (pCampo.equals("id.sna")) {
                fieldValue = pRango.getId().getSna().toString();
            } else if (pCampo.equals("serie.nir.codigo")) {
                fieldValue = String.valueOf(pRango.getSerie().getNir().getCodigo());
            } else if (pCampo.equals("numInicio")) {
                fieldValue = pRango.getNumInicio();
            } else if (pCampo.equals("numFinal")) {
                fieldValue = pRango.getNumFinal();
            } else if (pCampo.equals("tipoRed.descripcion")) {
                fieldValue = pRango.getTipoRed().getDescripcion();
            } else if (pCampo.equals("centralOrigen.nombre")) {
                fieldValue = pRango.getCentralOrigen().getNombre();
            } else if (pCampo.equals("centralDestino.nombre")) {
                fieldValue = pRango.getCentralDestino().getNombre();
            } else if (pCampo.equals("poblacion.inegi")) {
                fieldValue = pRango.getPoblacion().getInegi();
            } else if (pCampo.equals("poblacion.municipio.estado.nombre")) {
                fieldValue = pRango.getPoblacion().getMunicipio().getEstado().getNombre();
            } else if (pCampo.equals("poblacion.municipio.nombre")) {
                fieldValue = pRango.getPoblacion().getMunicipio().getNombre();
            } else if (pCampo.equals("poblacion.nombre")) {
                fieldValue = pRango.getPoblacion().getNombre();
            } else if (pCampo.equals("asignatario.nombre")) {
                fieldValue = pRango.getAsignatario().getNombre();
            } else if (pCampo.equals("idoPnn")) {
                fieldValue = pRango.getIdoPnn().toString();
            } else if (pCampo.equals("idaPnn")) {
                fieldValue = pRango.getIdaPnn().toString();
            } else if (pCampo.equals("consecutivoAsignacion")) {
                fieldValue = String.valueOf(pRango.getConsecutivoAsignacion());
            } else if (pCampo.equals("estadoRango.descripcion")) {
                fieldValue = pRango.getEstadoRango().getDescripcion();
            } else if (pCampo.equals("concesionario.nombre")) {
                fieldValue = pRango.getConcesionario().getNombre();
            } else if (pCampo.equals("concesionario.ido")) {
                fieldValue = pRango.getConcesionario().getIdo().toString();
            } else if (pCampo.equals("arrendatario.nombre")) {
                fieldValue = pRango.getArrendatario().getNombre();
            } else if (pCampo.equals("oficioAsignacion")) {
                fieldValue = pRango.getOficioAsignacion();
            } else if (pCampo.equals("fechaAsignacion")) {
                if (pRango.getFechaAsignacion() != null) {
                    fieldValue = FechasUtils.fechaToString(pRango.getFechaAsignacion());
                }
            }
        } catch (NullPointerException npe) {
            fieldValue = "";
        }

        if (fieldValue == null) {
            fieldValue = "";
        }

        return fieldValue;
    }

    /**
     * Recupera el valor de un campo específico de un rango de numeración no geográfica.
     * @param pRango Información del Rango.
     * @param pCampo Valor del campo a recuperar.
     * @return String con el valor del campo a recuperar.
     */
    public String getFieldValue(RangoSerieNng pRango, String pCampo) {
        String fieldValue = "";
        try {
            if (pCampo.equals("claveServicio.codigo")) {
                fieldValue = String.valueOf(pRango.getClaveServicio().getCodigo());
            } else if (pCampo.equals("serie.id.sna")) {
                fieldValue = pRango.getId().getSna().toString();
            } else if (pCampo.equals("numInicio")) {
                fieldValue = pRango.getNumInicio();
            } else if (pCampo.equals("numFinal")) {
                fieldValue = pRango.getNumFinal();
            } else if (pCampo.equals("asignatario.nombre")) {
                fieldValue = pRango.getAsignatario().getNombre();
            } else if (pCampo.equals("arrendatario.nombre")) {
                fieldValue = pRango.getArrendatario().getNombre();
            } else if (pCampo.equals("estatus.descripcion")) {
                fieldValue = pRango.getEstatus().getDescripcion();
            } else if (pCampo.equals("concesionario.nombre")) {
                fieldValue = pRango.getConcesionario().getNombre();
            } else if (pCampo.equals("arrendatario.nombre")) {
                fieldValue = pRango.getArrendatario().getNombre();
            } else if (pCampo.equals("consecutivoAsignacion")) {
                fieldValue = String.valueOf(pRango.getConsecutivoAsignacion());
            } else if (pCampo.equals("fechaAsignacion")) {
                if (pRango.getFechaAsignacion() != null) {
                    fieldValue = FechasUtils.fechaToString(pRango.getFechaAsignacion());
                }
            } else if (pCampo.equals("oficioAsignacion")) {
                fieldValue = pRango.getOficioAsignacion();
            } else if (pCampo.equals("cliente")) {
                fieldValue = pRango.getCliente();
            } else if (pCampo.equals("abc")) {
                if (pRango.getAbc() != null) {
                    fieldValue = String.valueOf(pRango.getAbc().intValue());
                }
            } else if (pCampo.equals("bcd")) {
                if (pRango.getBcd() != null) {
                    fieldValue = String.valueOf(pRango.getBcd().intValue());
                }
            } else if (pCampo.equals("asignatario.ida")) {
                if (pRango.getAsignatario().getIda() != null) {
                    fieldValue = String.valueOf(pRango.getAsignatario().getIda());
                }
            }
        } catch (NullPointerException npe) {
            fieldValue = "";
        }

        if (fieldValue == null) {
            fieldValue = "";
        }

        return fieldValue;
    }

}
