package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;

/** Filtros de búsqueda para ABNs. */
public class FiltroBusquedaABNs implements Serializable {

    /** Serialziación. */
    private static final long serialVersionUID = 1L;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Código de ABN. */
    private BigDecimal codigoAbn;

    /** Municipio Estado (OT) a la población del ABN. */
    private Estado estado;

    /** Población Ancla del ABN. */
    private Poblacion poblacionAncla;

    /** Municipio (OT) a la población del ABN. */
    private Municipio municipio;

    /** Presuscripción. */
    private boolean presuscripcion;

    /** Estatus ABN. */
    private EstadoAbn estatusAbn;

    /** Población del ABN. */
    private Poblacion poblacion;

    /**
     * Genera una lista con los filtros que se han definido sobre la búsqueda de ABNs. Los filtros buscan sobre
     * PoblacionAbn en vez del catálogo de ABNs.
     * @return ArrayList<FiltroBusqueda>
     */
    public ArrayList<FiltroBusqueda> getListaFiltros() {
        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (codigoAbn != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("abn.codigoAbn", "");
            filtro.setValor(codigoAbn, BigDecimal.class);
            filtros.add(filtro);
        }

        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("inegi.municipio.estado", "");
            filtro.setValor(estado, Estado.class);
            filtros.add(filtro);
        }

        if (municipio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("inegi.municipio", "");
            filtro.setValor(municipio, Municipio.class);
            filtros.add(filtro);
        }

        if (poblacionAncla != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("abn.poblacionAncla", "");
            filtro.setValor(poblacionAncla, Poblacion.class);
            filtros.add(filtro);
        }

        if (poblacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("inegi", "");
            filtro.setValor(poblacion, Poblacion.class);
            filtros.add(filtro);
        }

        if (estatusAbn != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("abn.estadoAbn", "");
            filtro.setValor(estatusAbn, EstadoAbn.class);
            filtros.add(filtro);
        }

        if (presuscripcion) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("abn.presuscripcion", "");
            filtro.setValor("P", String.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    // /**
    // * Genera una lista con los filtros que se han definido sobre la búsqueda de ABNs.
    // * @return ArrayList<FiltroBusqueda>
    // */
    // public ArrayList<FiltroBusqueda> getListaFiltros() {
    // ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();
    //
    // if (codigoAbn != null) {
    // FiltroBusqueda filtro = new FiltroBusqueda();
    // filtro.setCampo("codigoAbn", "sAbn");
    // filtro.setValor(codigoAbn, BigDecimal.class);
    // filtros.add(filtro);
    // }
    //
    // if (estado != null) {
    // FiltroBusqueda filtro = new FiltroBusqueda();
    // filtro.setCampo("poblacionAncla.municipio.estado", "sAbn");
    // filtro.setValor(estado, Estado.class);
    // filtros.add(filtro);
    // }
    //
    // if (municipio != null) {
    // FiltroBusqueda filtro = new FiltroBusqueda();
    // filtro.setCampo("poblacionAncla.municipio", "sAbn");
    // filtro.setValor(municipio, Municipio.class);
    // filtros.add(filtro);
    // }
    //
    // if (poblacionAncla != null) {
    // FiltroBusqueda filtro = new FiltroBusqueda();
    // filtro.setCampo("poblacionAncla", "sAbn");
    // filtro.setValor(poblacionAncla, Poblacion.class);
    // filtros.add(filtro);
    // }
    //
    // if (estatusAbn != null) {
    // FiltroBusqueda filtro = new FiltroBusqueda();
    // filtro.setCampo("estadoAbn", "sAbn");
    // filtro.setValor(estatusAbn, EstadoAbn.class);
    // filtros.add(filtro);
    // }
    //
    // if (presuscripcion) {
    // FiltroBusqueda filtro = new FiltroBusqueda();
    // filtro.setCampo("presuscripcion", "sAbn");
    // filtro.setValor("P", String.class);
    // filtros.add(filtro);
    // }
    //
    // return filtros;
    // }

    /**
     * Genera una lista con los filtros para la vista del Catálogo de ABN's en función de los filtros definidos para la
     * búsqueda de ABN's en la consulta.
     * @param pFiltrosConsultaAbn Filtros de búsqueda de ABN.
     * @return ArrayList
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosCatalogo(ArrayList<FiltroBusqueda> pFiltrosConsultaAbn) {
        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>(pFiltrosConsultaAbn.size());
        for (FiltroBusqueda filtroAbn : pFiltrosConsultaAbn) {
            if (filtroAbn.getCampo().equals("abn.codigoAbn")) {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("idAbn", "");
                filtro.setValor(filtroAbn.getValor(), null);
                filtros.add(filtro);
            }

            if (filtroAbn.getCampo().equals("inegi.municipio.estado")) {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("idEstado", "");
                filtro.setValor(((Estado) filtroAbn.getValor()).getCodEstado(), null);
                filtros.add(filtro);
            }

            if (filtroAbn.getCampo().equals("inegi.municipio")) {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("idMunicipio", "");
                filtro.setValor(((Municipio) filtroAbn.getValor()).getId().getCodMunicipio(), null);
                filtros.add(filtro);
            }

            if (filtroAbn.getCampo().equals("abn.poblacionAncla")) {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("poblacionAncla", "");
                filtro.setValor(((Poblacion) filtroAbn.getValor()).getNombre(), null);
                filtros.add(filtro);
            }

            if (filtroAbn.getCampo().equals("inegi")) {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("nombreMunicipio", "");
                filtro.setValor(((Poblacion) filtroAbn.getValor()).getNombre(), null);
                filtros.add(filtro);
            }

            if (filtroAbn.getCampo().equals("abn.estadoAbn")) {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("idEstatus", "");
                filtro.setValor(((EstadoAbn) filtroAbn.getValor()).getCodigo(), null);
                filtros.add(filtro);
            }

            if (filtroAbn.getCampo().equals("abn.presuscripcion")) {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("presuscripcion", "");
                filtro.setValor("P", null);
                filtros.add(filtro);
            }
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        this.codigoAbn = null;
        this.estado = null;
        this.estatusAbn = null;
        this.municipio = null;
        this.numeroPagina = 0;
        this.poblacionAncla = null;
        this.presuscripcion = false;
        this.resultadosPagina = 0;
        this.usarPaginacion = false;
        this.poblacion = null;
    }

    @Override
    public String toString() {
        StringBuilder trace = new StringBuilder();
        trace.append("usarPaginacion").append(": ").append(usarPaginacion).append(", ");
        if (usarPaginacion) {
            trace.append("numeroPagina").append(": ").append(numeroPagina).append(", ");
            trace.append("resultadosPagina").append(": ").append(resultadosPagina).append(", ");
        }
        if (codigoAbn != null) {
            trace.append("codigoAbn").append(": ").append(codigoAbn).append(", ");
        }
        if (estado != null) {
            trace.append("estado").append(": ").append(estado.getNombre()).append(", ");
        }
        if (municipio != null) {
            trace.append("municipio").append(": ").append(municipio.getNombre()).append(", ");
        }
        if (poblacionAncla != null) {
            trace.append("poblacionAncla").append(": ").append(poblacionAncla.getNombre()).append(", ");
        }
        if (poblacion != null) {
            trace.append("poblacion").append(": ").append(poblacion.getNombre()).append(", ");
        }
        if (estatusAbn != null) {
            trace.append("estatusAbn").append(": ").append(estatusAbn.getCodigo()).append(", ");
        }
        if (presuscripcion) {
            trace.append("presuscripcion").append(": ").append(presuscripcion);
        }

        return trace.toString();
    }

    // GETTERS & SETTERS

    /**
     * Indica si se usa paginación para modelos Lazy.
     * @return boolean
     */
    public boolean isUsarPaginacion() {
        return usarPaginacion;
    }

    /**
     * Indica si se usa paginación para modelos Lazy.
     * @param usarPaginacion boolean
     */
    public void setUsarPaginacion(boolean usarPaginacion) {
        this.usarPaginacion = usarPaginacion;
    }

    /**
     * Número máximo de resultados por búsqueda para paginación.
     * @return int
     */
    public int getResultadosPagina() {
        return resultadosPagina;
    }

    /**
     * Número máximo de resultados por búsqueda para paginación.
     * @param resultadosPagina int
     */
    public void setResultadosPagina(int resultadosPagina) {
        this.resultadosPagina = resultadosPagina;
    }

    /**
     * Código de ABN.
     * @param codigoAbn BigDecimal
     */
    public void setCodigoAbn(BigDecimal codigoAbn) {
        this.codigoAbn = codigoAbn;
    }

    /**
     * Estado de Organización Territorial.
     * @param estado Estado
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Población Ancla del ABN.
     * @param poblacionAncla Poblacion
     */
    public void setPoblacionAncla(Poblacion poblacionAncla) {
        this.poblacionAncla = poblacionAncla;
    }

    /**
     * Municipio de Organización Territorial.
     * @param municipio Municipio
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Presuscripción de ABN.
     * @param presuscripcion boolean
     */
    public void setPresuscripcion(boolean presuscripcion) {
        this.presuscripcion = presuscripcion;
    }

    /**
     * Estatus de ABN.
     * @param estatusAbn EstadoAbn
     */
    public void setEstatusAbn(EstadoAbn estatusAbn) {
        this.estatusAbn = estatusAbn;
    }

    /**
     * Número de página para pagiación en modelos Lazy.
     * @return int
     */
    public int getNumeroPagina() {
        return numeroPagina;
    }

    /**
     * Número de página para pagiación en modelos Lazy.
     * @param numeroPagina int
     */
    public void setNumeroPagina(int numeroPagina) {
        this.numeroPagina = numeroPagina;
    }

    /**
     * Población del ABN.
     * @param poblacion Poblacion poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

}
