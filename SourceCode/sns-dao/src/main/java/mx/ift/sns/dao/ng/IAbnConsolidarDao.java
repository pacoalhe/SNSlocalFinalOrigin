package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.Date;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ng.AbnConsolidar;
import mx.ift.sns.modelo.ot.Poblacion;

/**
 * Interfaz de definición de los métodos para base de datos de consolidaciones de Abn.
 */
public interface IAbnConsolidarDao {

    /**
     * Método que devuelve el abnConsolidar dependiendo del Id de la Solicitud.
     * @param pIdSol id de la solicitud
     * @return AbnConsolidar
     */
    AbnConsolidar getAbnConsolidarByIdSol(BigDecimal pIdSol);

    /***
     * Método que devuelve el abnConsolidar dependiendo del abn del rango serie.
     * @param rangoAbn Abn el Abn que se pretende buscar la fecha de consolidacion.
     * @return AbnConsolidar.
     */
    String getFechaConsolidacionByRangoAbn(Abn rangoAbn);

    /**
     * Obtiene la fecha de consolidación de una poblacion.
     * @param poblacion poblacion
     * @return fecha consolidacio
     */
    Date getFechaConsolidacionPoblacion(Poblacion poblacion);

}
