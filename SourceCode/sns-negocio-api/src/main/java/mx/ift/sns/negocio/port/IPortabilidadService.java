package mx.ift.sns.negocio.port;

import java.io.File;

import mx.ift.sns.modelo.port.EstatusSincronizacion;
import mx.ift.sns.modelo.port.NumeroCancelado;
import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.exceptions.SincronizacionABDException;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;

/**
 * Interfaz del Servicio de Portabilidad.
 */
public interface IPortabilidadService {

    /**
     * Sincroniza la BDD de Portabilidad con el ABD.
     * @throws Exception error
     */
    void syncBDDPortabilidad() throws Exception;

    /**
     * Sincroniza la BDD de Portabilidad con el ABD MANUALMENTE.
     * @throws Exception error
     */
    void syncManualPortabilidad() throws Exception;

    /**
     * Sincroniza la BDD de Portabilidad con el ABD usando los ficheros.
     * @param file fichero
     * @param tipo tipo de fichero
     * @param usuario que realiza la op
     * @throws SincronizacionABDException error sincronización
     */
    void syncBDDPortabilidad(String file, TipoFicheroPort tipo, Usuario usuario) throws SincronizacionABDException;

    /**
     * Busca el numero indicado.
     * @param numero numero a buscar
     * @return numero portado si esta portado
     */
    NumeroPortado findNumeroPortado(String numero);

    /**
     * Sincronizacion diaria asincrona.
     * @throws Exception
     */
    void syncBDDPortabilidadAsync() throws Exception;

    /**
     * graba el estado.
     * @param status estado de la sincronizacion
     */
    void saveStatus(EstatusSincronizacion status);

    /**
     * Envia el mail de error al sincronizar con el ABD.
     * @param pErrorMsg Descripción del error.
     */
    void enviarMailErrorABD(String pErrorMsg);

    /**
     * Envia el mail de error en el fichero ported.
     * @param nombre del fichero
     */
    void enviarMailFicheroPorted(String nombre);

    /**
     * Envia el mail de error en el fichero deleted.
     * @param nombre del fichero
     */
    void enviarMailFicheroDeleted(String nombre);

    /**
     * @param status estado de la sincronizacion
     */
    void enviarMailSyncOk(EstatusSincronizacion status);

    /**
     * Envía correo de sincronización ABD con detalle de totales procesados.
     * Si se pasa una causa no nula, se incluye en el cuerpo como explicación.
     * En local/QA genera también archivo espejo.
     *
     * @param status   Estatus de la sincronización
     * @param res      Resultado detallado de la validación de archivos
     * @param localMode Indica si se debe generar archivo espejo (true en local/QA, false en Prod)
     * @param causa     Texto explicativo opcional (ej. archivos vacíos/no encontrados). Puede ser null.
     */
    void enviarMailSyncOkDetalle(EstatusSincronizacion status,
                                 ResultadoValidacionCSV res,
                                 boolean localMode,
                                 String causa);

    /**
     * Envía correo de error detallado de la sincronización ABD,
     * incluyendo la causa y opcionalmente un archivo espejo en local/QA.
     *
     * @param causa     Descripción detallada del error (archivos no encontrados, vacíos, etc.)
     * @param localMode Indica si se debe generar archivo espejo (true en local/QA, false en Prod)
     */
    void enviarMailErrorABDDetalle(String causa, boolean localMode);


    /**
     * Parsea fichero de cancelaciones.
     * @param -tmpDeleted fichero
     * @param status estado de la sincronizacion
     * @return EstatusSincronizacion
     * @throws Exception error
     */
    //EstatusSincronizacion parseDeleted(File tmpDeleted, EstatusSincronizacion status) throws Exception;
    ResultadoValidacionCSV parseDeleted(File tmpPorted, EstatusSincronizacion status) throws Exception;

    /**
     * Parsea el fichero de portabilidad.
     * @param tmpPorted fichero portados
     * @param status estado de la sincronizacion
     * @return EstatusSincronizacion
     * @throws Exception errror
     */
    //EstatusSincronizacion parsePortabilidad(File tmpPorted, EstatusSincronizacion status) throws Exception;
    ResultadoValidacionCSV parsePortabilidad(File tmpPorted, EstatusSincronizacion status) throws Exception;


    /**
     * Recupera un número portado por su identificador.
     * @param numero Identificador de Número Portado
     * @return NumeroPortado
     */
    NumeroPortado getNumeroPortado(String numero);

    /**
     * Actualiza un número portado.
     * @param numero Identificador de Número Portado
     * @return NumeroPortado
     */
    NumeroPortado update(NumeroPortado numero);

    /**
     * Actualiza un número cancelado.
     * @param numero Identificador de Número Portado
     * @return NumeroCancelado
     */
    NumeroCancelado update(NumeroCancelado numero);

    /**
     * Elimina un número portado.
     * @param numero Identificador de Número Portado
     */
    void delete(String numero);
}
