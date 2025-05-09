package mx.ift.sns.negocio.cpsn;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.cpsn.CesionSolicitadaCPSN;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.LiberacionSolicitadaCpsn;
import mx.ift.sns.modelo.cpsn.NumeracionAsignadaCpsn;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.cpsn.VEstudioCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSN;
import mx.ift.sns.modelo.pst.Proveedor;

/** Interfaz de servicios de los equipos de señalización. */
public interface ICodigoCPSNService {

    /**
     * Devuelve un tipo de bloque CPSN por su id.
     * @param id tipo de bloque
     * @return Tipo de bloque CPSN
     * @throws Exception excepcion
     */
    TipoBloqueCPSN getTipoBloqueCPSNById(String id) throws Exception;

    /**
     * Devuelve un estatus CPSN por su id.
     * @param id estatus CPSN
     * @return estatus CPSN
     * @throws Exception excepcion
     */
    EstatusCPSN getEstatusCPSNById(String id) throws Exception;

    /**
     * Consulta de todos los tipos de bloque CPSN.
     * @return listado de los bloques.
     */
    List<TipoBloqueCPSN> findAllTiposBloqueCPSN();

    /**
     * Consulta de todos los estatus de CPSN.
     * @return listado de estatusCPSN
     */
    List<EstatusCPSN> findAllEstatusCPSN();

    /**
     * Consulta de los códigos CPSN que cumplen el filtro.
     * @param pFiltro filtro de la búsqueda
     * @return listado de codigos.
     */
    List<CodigoCPSN> findCodigosCPSN(FiltroBusquedaCodigosCPSN pFiltro);

    /**
     * Genera un Documento Excel con la información de los códigos CPS nacioonales.
     * @param pFiltros FiltroBusquedaCodigosCPSN
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] exportarCodigosCPSN(FiltroBusquedaCodigosCPSN pFiltros) throws Exception;

    /**
     * Método encargado de validar y liberar los códigos seleccionados.
     * @param codigosCPSNSeleccionados listado de códigos a liberar
     * @return error en caso de producirse
     */
    String liberarCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados);

    /**
     * Método encargado de validar y reservar los códigos seleccionados.
     * @param codigosCPSNSeleccionados listado de códigos a liberar
     * @return error en caso de producirse
     */
    String reservarCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados);

    /**
     * Método encargado de validar si el código se puede desagrupar.
     * @param codigosCPSNSeleccionados código a desagrupar
     * @return error en caso de producirse
     */
    String validarDesagrupacionCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados);

    /**
     * Método encargado de desagrupar el código.
     * @param codigosCPSNSeleccionados código a desagrupar
     * @return error si no pasa alguna validación
     */
    String desagruparCodigoCPSN(List<CodigoCPSN> codigosCPSNSeleccionados);

    /**
     * Método encargado de agrupar el código.
     * @param codigosCPSNSeleccionados código a agrupar
     * @return error si no pasa alguna validación
     */
    String agruparCodigoCPSN(List<CodigoCPSN> codigosCPSNSeleccionados);

    /**
     * Libera un cpsn.
     * @param pLibSol Información de la liberación solicitada
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return Código de EstadoLiberacionSolicitada indicando el nuevo estado de la liberación solicitada.
     * @throws Exception en caso de error.
     */
    String liberarCpsn(LiberacionSolicitadaCpsn pLibSol, boolean pInmediata) throws Exception;

    /**
     * Libera los CPSN en cuarentana una vez finalizada la fecha de reserva.
     */
    void liberarCuarentena();

    /**
     * Método encargado de obtenerlos datos del estudio de códigos CPS Nacionales.
     * @return List<VEstudioCPSN> estudio
     */
    List<VEstudioCPSN> estudioCPSN();

    /**
     * Cede un cpsn.
     * @param pCesSol Información de la cesión solicitada
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return Código de EstadoCesionSolicitada indicando el nuevo estado de la cesión solicitada.
     * @throws Exception en caso de error.
     */
    String cederCPSN(CesionSolicitadaCPSN pCesSol, boolean pInmediata) throws Exception;

    /**
     * Recupera un Código CPSN en función de su tipo de bloque, identificador y el Proveedor asignado.
     * @param pIdTipoBloque Identificador del tipo de bloque.
     * @param pIdCodigo Identificador del Código CPSN.
     * @param pProveedor Proveedor asignatario del CPSN. Puede ser nulo si se buscan códigos libres.
     * @return CodigoCPSN
     */
    CodigoCPSN getCodigoCpsn(String pIdTipoBloque, BigDecimal pIdCodigo, Proveedor pProveedor);

    /**
     * Asigna el CPSI solicitado al Proveedor Solicitante..
     * @param pCpsnSolicitado CPSN para asignar.
     * @param pProveedor Información del proveedor solicitante.
     * @throws Exception en caso de error.
     */
    void asignarCpsn(NumeracionAsignadaCpsn pCpsnSolicitado, Proveedor pProveedor) throws Exception;

    /**
     * Método encargado de guardar en BD el código CPSN.
     * @param codigo CPSN
     * @return codigo CPSN
     */
    CodigoCPSN saveCodigoCpsn(CodigoCPSN codigo);

    /**
     * Método encargado de eliminar en BD el código CPSN.
     * @param codigo CPSN
     */
    void removeCodigoCpsn(CodigoCPSN codigo);

    /**
     * Recupera el listado de Códigos CPSN asignados al Proveedor (Estatus 'A') y todos los que estan libres.
     * @param pProveedor Información del Proveedor.
     * @param tipoBloque tipo de bloque
     * @return List
     */
    List<CodigoCPSN> findAllCodigosCPSN(Proveedor pProveedor, TipoBloqueCPSN tipoBloque);
}
