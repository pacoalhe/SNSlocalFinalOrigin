package mx.ift.sns.dao.oficios;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.Solicitud;

/**
 * Interfaz de definición de los métodos para base de datos para la generación de Oficios.
 */
public interface IOficioDao extends IBaseDAO<Oficio> {

    /**
     * Recupera un oficio almacenado en BBDD.
     * @param pIdOficio Identificador de Oficio
     * @return Oficio según su ID
     */
    Oficio getOficio(BigDecimal pIdOficio);

    /**
     * Recupera un oficio en función de su Solicitud y Tipo de Destinatario.
     * @param pSolicitud Solicitud a la que pertenece
     * @param pTipoDestinatario Tipo de Destinatario del oficio
     * @return Oficio
     */
    Oficio getOficio(Solicitud pSolicitud, TipoDestinatario pTipoDestinatario);

    /**
     * Recupera un oficio por su número de oficio.
     * @param numOficio Número de Oficio.
     * @param pTipoDestinatario Tipo de Destinatario del oficio
     * @return Oficio
     */
    Oficio getOficioByNumOficio(String numOficio, TipoDestinatario pTipoDestinatario);

    /**
     * Recupera el número de oficios existentes en base de datos.
     * @return número de oficios existentes en base de datos.
     */
    int getOficiosCount();

    /**
     * Comprueba si un numero de oficio ya existe.
     * @param numeroOficio oficio
     * @return boolean
     */
    boolean existeNumeroOficio(String numeroOficio);

    /**
     * Recupera los oficios de una solicitud.
     * @param idSolicitud idSolicitud
     * @return lista de oficios
     */
    List<Oficio> getOficiosBySolicitud(BigDecimal idSolicitud);

}
