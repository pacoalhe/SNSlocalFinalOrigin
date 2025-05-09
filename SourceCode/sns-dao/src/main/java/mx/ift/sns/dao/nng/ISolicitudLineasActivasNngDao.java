package mx.ift.sns.dao.nng;

import mx.ift.sns.modelo.nng.SolicitudLineasActivasNng;

/**
 * Clase interfaz solicitudes de lineas activas.
 */
public interface ISolicitudLineasActivasNngDao {

    /**
     * Persiste una SolicitudLineasActivas.
     * @param solicitud SolicitudLineasActivas
     * @return SolicitudLineasActiva solicitudLineasActivass
     */
    SolicitudLineasActivasNng saveSolicitudLineasActivas(SolicitudLineasActivasNng solicitud);

}
