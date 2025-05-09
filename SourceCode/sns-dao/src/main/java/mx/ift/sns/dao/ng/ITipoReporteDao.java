package mx.ift.sns.dao.ng;

import java.util.List;

import mx.ift.sns.modelo.lineas.TipoReporte;

/**
 * Interfaz del DAO de TipoReporte.
 * @author X36155QU
 */
public interface ITipoReporteDao {

    /**
     * Recupera los tipos de reporte.
     * @return lista TipoReporte
     */
    List<TipoReporte> findAllTiposReporte();

    /**
     * Obtiene el tipo de reporte por su codigo.
     * @param value codigo
     * @return tipo reporte
     */
    TipoReporte getTipoReporteByCdg(String value);

}
