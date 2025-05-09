package mx.ift.sns.dao.ng;

import mx.ift.sns.modelo.ng.SolicitudLineasActivas;

/**
 * Clase interfaz solicitudes de lineas activas.
 */
public interface ISolicitudLineasActivasDao {

    /**
     * Persiste una SolicitudLineasActivas.
     * @param solicitud SolicitudLineasActivas
     * @return SolicitudLineasActiva solicitudLineasActivass
     */
    SolicitudLineasActivas saveSolicitudLineasActivas(SolicitudLineasActivas solicitud);

}
