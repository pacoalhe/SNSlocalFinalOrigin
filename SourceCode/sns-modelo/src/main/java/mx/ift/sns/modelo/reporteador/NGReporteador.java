package mx.ift.sns.modelo.reporteador;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity implementation class for Entity: NGReporteador.
 */
@Entity
public class NGReporteador implements Serializable {

    /**
     * Serialización.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificador de la vista.
     */
    @Id
    private Integer lineaConsulta;
    /**
     * Identificador del Proveedor.
     */
    private Integer idPst;
    /**
     * Descripción de Proveedor.
     */
    private String pst;
    /**
     * Descripción del Estado.
     */
    private String estado;
    /**
     * Descripción de Municipio.
     */
    private String municipio;
    /**
     * Descripción de Poblacion.
     */
    private String descPoblacion;
    /**
     * Descripción de abn.
     */
    private String abn;
    /**
     * Código de abn.
     */
    private String idAbn;

    /**
     * Total de líneas asignadas.
     */
    private Integer totalAsignadas;
    /**
     * Total de líneas fijas asignadas.
     */
    private Integer asignadasFijas;
    /**
     * Total de líneas móviles asignadas.
     */
    private Integer asignadasMoviles;
    /**
     * Total de líneas móviles CPP asignadas.
     */
    private Integer asignadasMovilesCPP;
    /**
     * Total de líneas móviles MPP asignadas.
     */
    private Integer asignadasMovilesMPP;
    /**
     * Total de líneas activas.
     */
    private Integer totalActivas;
    /**
     * Porcentaje de líneas activas sobre el total de asignadas.
     */
    private Integer totalPorcentajeActivas;
    /**
     * Total de líneas fijas activas.
     */
    private Integer activasFijas;
    /**
     * Porcentaje de líneas activas fijas sobre el total de asignadas fijas.
     */
    private Integer porcentajeActivasFijas;
    /**
     * Total de líneas móviles activas.
     */
    private Integer activasMoviles;
    /**
     * Porcentaje de líneas activas móviles sobre el total de asignadas móviles.
     */
    private Integer porcentajeActivasMoviles;
    /**
     * Total de líneas móviles CPP activas.
     */
    private Integer activasMovilesCPP;
    /**
     * Porcentaje de líneas activas móviles CPP sobre el total de asignadas móviles CPP.
     */
    private Integer porcentajeActivasMovilesCPP;
    /**
     * Total de líneas móviles MPP activas.
     */
    private Integer activasMovilesMPP;
    /**
     * Porcentaje de líneas activas móviles MPP sobre el total de asignadas móviles MPP.
     */
    private Integer porcentajeActivasMovilesMPP;

    /**
     * Constructor.
     */
    public NGReporteador() {
        super();
    }

    /**
     * Identificador de la vista.
     * @return Integer
     */
    public Integer getLineaConsulta() {
        return lineaConsulta;
    }

    /**
     * Identificador de la vista.
     * @param lineaConsulta Integer
     */
    public void setLineaConsulta(Integer lineaConsulta) {
        this.lineaConsulta = lineaConsulta;
    }

    /**
     * Descripción de Proveedor.
     * @return String
     */
    public String getPst() {
        return pst;
    }

    /**
     * Descripción de Proveedor.
     * @param pst String
     */
    public void setPst(String pst) {
        this.pst = pst;
    }

    /**
     * Identificador del Proveedor.
     * @return Integer
     */
    public Integer getIdPst() {
        return this.idPst;
    }

    /**
     * Identificador del Proveedor.
     * @param idPst Integer
     */
    public void setIdPst(Integer idPst) {
        this.idPst = idPst;
    }

    /**
     * Descripción del Estado.
     * @return String
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Descripción del Estado.
     * @param estado String
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Descripción de Municipio.
     * @return String
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * Descripción de Municipio.
     * @param municipio String
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    /**
     * Descripción de Poblacion.
     * @return String
     */
    public String getDescPoblacion() {
        return descPoblacion;
    }

    /**
     * Descripción de Poblacion.
     * @param descPoblacion String
     */
    public void setDescPoblacion(String descPoblacion) {
        this.descPoblacion = descPoblacion;
    }

    /**
     * Descripción de abn.
     * @return String
     */
    public String getAbn() {
        return abn;
    }

    /**
     * Descripción de abn.
     * @param abn String
     */
    public void setAbn(String abn) {
        this.abn = abn;
    }

    /**
     * Código de abn.
     * @return String
     */
    public String getIdAbn() {
        return idAbn;
    }

    /**
     * Código de abn.
     * @param idAbn String
     */
    public void setIdAbn(String idAbn) {
        this.idAbn = idAbn;
    }

    /**
     * Total de líneas asignadas.
     * @return Integer
     */
    public Integer getTotalAsignadas() {
        return totalAsignadas;
    }

    /**
     * Total de líneas asignadas.
     * @param totalAsignadas Integer
     */
    public void setTotalAsignadas(Integer totalAsignadas) {
        this.totalAsignadas = totalAsignadas;
    }

    /**
     * Total de líneas fijas asignadas.
     * @return Integer
     */
    public Integer getAsignadasFijas() {
        return asignadasFijas;
    }

    /**
     * Total de líneas fijas asignadas.
     * @param asignadasFijas the asignadasFijas to set
     */
    public void setAsignadasFijas(Integer asignadasFijas) {
        this.asignadasFijas = asignadasFijas;
    }

    /**
     * Total de líneas móviles asignadas.
     * @return Integer
     */
    public Integer getAsignadasMoviles() {
        return asignadasMoviles;
    }

    /**
     * Total de líneas móviles asignadas.
     * @param asignadasMoviles Integer
     */
    public void setAsignadasMoviles(Integer asignadasMoviles) {
        this.asignadasMoviles = asignadasMoviles;
    }

    /**
     * Total de líneas móviles CPP asignadas.
     * @return Integer
     */
    public Integer getAsignadasMovilesCPP() {
        return asignadasMovilesCPP;
    }

    /**
     * Total de líneas móviles CPP asignadas.
     * @param asignadasMovilesCPP Integer
     */
    public void setAsignadasMovilesCPP(Integer asignadasMovilesCPP) {
        this.asignadasMovilesCPP = asignadasMovilesCPP;
    }

    /**
     * Total de líneas móviles MPP asignadas.
     * @return Integer
     */
    public Integer getAsignadasMovilesMPP() {
        return asignadasMovilesMPP;
    }

    /**
     * Total de líneas móviles MPP asignadas.
     * @param asignadasMovilesMPP Integer
     */
    public void setAsignadasMovilesMPP(Integer asignadasMovilesMPP) {
        this.asignadasMovilesMPP = asignadasMovilesMPP;
    }

    /**
     * Total de líneas activas.
     * @return Integer
     */
    public Integer getTotalActivas() {
        return totalActivas;
    }

    /**
     * Total de líneas activas.
     * @param totalActivas Integer
     */
    public void setTotalActivas(Integer totalActivas) {
        this.totalActivas = totalActivas;
    }

    /**
     * Porcentaje de líneas activas sobre el total de asignadas.
     * @return Integer
     */
    public Integer getTotalPorcentajeActivas() {
        return totalPorcentajeActivas;
    }

    /**
     * Porcentaje de líneas activas sobre el total de asignadas.
     * @param totalPorcentajeActivas Integer
     */
    public void setTotalPorcentajeActivas(Integer totalPorcentajeActivas) {
        this.totalPorcentajeActivas = totalPorcentajeActivas;
    }

    /**
     * Total de líneas fijas activas.
     * @return Integer
     */
    public Integer getActivasFijas() {
        return activasFijas;
    }

    /**
     * Total de líneas fijas activas.
     * @param activasFijas Integer
     */
    public void setActivasFijas(Integer activasFijas) {
        this.activasFijas = activasFijas;
    }

    /**
     * Porcentaje de líneas activas fijas sobre el total de asignadas fijas.
     * @return Integer
     */
    public Integer getPorcentajeActivasFijas() {
        return porcentajeActivasFijas;
    }

    /**
     * Porcentaje de líneas activas fijas sobre el total de asignadas fijas.
     * @param porcentajeActivasFijas Integer
     */
    public void setPorcentajeActivasFijas(Integer porcentajeActivasFijas) {
        this.porcentajeActivasFijas = porcentajeActivasFijas;
    }

    /**
     * Total de líneas móviles activas.
     * @return Integer
     */
    public Integer getActivasMoviles() {
        return activasMoviles;
    }

    /**
     * Total de líneas móviles activas.
     * @param activasMoviles Integer
     */
    public void setActivasMoviles(Integer activasMoviles) {
        this.activasMoviles = activasMoviles;
    }

    /**
     * Porcentaje de líneas activas móviles sobre el total de asignadas móviles.
     * @return Integer
     */
    public Integer getPorcentajeActivasMoviles() {
        return porcentajeActivasMoviles;
    }

    /**
     * Porcentaje de líneas activas móviles sobre el total de asignadas móviles.
     * @param porcentajeActivasMoviles Integer
     */
    public void setPorcentajeActivasMoviles(Integer porcentajeActivasMoviles) {
        this.porcentajeActivasMoviles = porcentajeActivasMoviles;
    }

    /**
     * Total de líneas móviles CPP activas.
     * @return Integer
     */
    public Integer getActivasMovilesCPP() {
        return activasMovilesCPP;
    }

    /**
     * Total de líneas móviles CPP activas.
     * @param activasMovilesCPP Integer
     */
    public void setActivasMovilesCPP(Integer activasMovilesCPP) {
        this.activasMovilesCPP = activasMovilesCPP;
    }

    /**
     * Porcentaje de líneas activas móviles CPP sobre el total de asignadas móviles CPP.
     * @return Integer
     */
    public Integer getPorcentajeActivasMovilesCPP() {
        return porcentajeActivasMovilesCPP;
    }

    /**
     * Porcentaje de líneas activas móviles CPP sobre el total de asignadas móviles CPP.
     * @param porcentajeActivasMovilesCPP Integer
     */
    public void setPorcentajeActivasMovilesCPP(Integer porcentajeActivasMovilesCPP) {
        this.porcentajeActivasMovilesCPP = porcentajeActivasMovilesCPP;
    }

    /**
     * Total de líneas móviles MPP activas.
     * @return Integer
     */
    public Integer getActivasMovilesMPP() {
        return activasMovilesMPP;
    }

    /**
     * Total de líneas móviles MPP activas.
     * @param activasMovilesMPP Integer
     */
    public void setActivasMovilesMPP(Integer activasMovilesMPP) {
        this.activasMovilesMPP = activasMovilesMPP;
    }

    /**
     * Porcentaje de líneas activas móviles MPP sobre el total de asignadas móviles MPP.
     * @return the porcentajeActivasMovilesMPP
     */
    public Integer getPorcentajeActivasMovilesMPP() {
        return porcentajeActivasMovilesMPP;
    }

    /**
     * Porcentaje de líneas activas móviles MPP sobre el total de asignadas móviles MPP.
     * @param porcentajeActivasMovilesMPP the porcentajeActivasMovilesMPP to set
     */
    public void setPorcentajeActivasMovilesMPP(Integer porcentajeActivasMovilesMPP) {
        this.porcentajeActivasMovilesMPP = porcentajeActivasMovilesMPP;
    }

}
