package mx.ift.sns.negocio.cpsi;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.cpsi.CesionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.CpsiAsignado;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.InfoCatCpsi;
import mx.ift.sns.modelo.cpsi.LiberacionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.Linea1EstudioCPSI;
import mx.ift.sns.modelo.cpsi.VEstudioCPSI;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSI;
import mx.ift.sns.modelo.pst.Proveedor;

/** Interfaz de servicios de los equipos de señalización. */
public interface ICodigoCPSIService {

    /**
     * Devuelve un estatus CPSI por su id.
     * @param id estatus CPSI
     * @return estatus CPSI
     * @throws Exception excepcion
     */
    EstatusCPSI getEstatusCPSIById(String id) throws Exception;

    /**
     * Consulta de todos los estatus de CPSI.
     * @return listado de estatusCPSI
     */
    List<EstatusCPSI> findAllEstatusCPSI();

    /**
     * Consulta de los códigos CPSI que cumplen el filtro.
     * @param pFiltro filtro de la búsqueda
     * @return listado de codigos.
     */
    List<CodigoCPSI> findAllCodigosCPSI(FiltroBusquedaCodigosCPSI pFiltro);

    /**
     * Devuelve el número de códigos que cumplen el filtro.
     * @param pFiltro filtro de la búsqueda
     * @return número de códigos que cumplen el filtro.
     */
    int findAllCodigosCPSICount(FiltroBusquedaCodigosCPSI pFiltro);

    /**
     * Recupera el listado de Códigos CPSI asignados al Proveedor (Estatus 'A') y todos los que estan libres.
     * @param pProveedor Información del Proveedor.
     * @return List
     */
    List<CodigoCPSI> findAllCodigosCPSIForAnalisis(Proveedor pProveedor);

    /**
     * Recupera un Código CPSI en función de su identificador y el Proveedor asignado.
     * @param pIdCodigo Identificador del Código CPSI.
     * @param pProveedor Proveedor asignatario del CPSI. Puede ser nulo si se buscan códigos libres.
     * @return CodigoCPSI
     */
    CodigoCPSI getCodigoCpsi(BigDecimal pIdCodigo, Proveedor pProveedor);

    /**
     * Método encargado de guardar en BD el código CPSI.
     * @param codigo CPSI
     * @return codigo CPSI
     */
    CodigoCPSI saveCodigoCPSI(CodigoCPSI codigo);

    /**
     * Genera un Documento Excel con la información de los códigos CPS Inernacionales.
     * @param pFiltros FiltroBusquedaCodigosCPSI
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] exportarCodigosCPSI(FiltroBusquedaCodigosCPSI pFiltros) throws Exception;

    /**
     * Método encargado de exportar el informe deseado a excel.
     * @param estudio donde están los datos del informe.
     * @return Documento Excel Serializable.
     * @throws Exception En caso de error.s
     */
    byte[] exportarEstudioCPSI(VEstudioCPSI estudio) throws Exception;

    /**
     * Método encargado de validar y liberar los códigos seleccionados.
     * @param codigosCPSISeleccionados listado de códigos a liberar
     * @return error en caso de producirse
     */
    String liberarCodigosCPSI(List<InfoCatCpsi> codigosCPSISeleccionados);

    /**
     * Método encargado de validar y reservar los códigos seleccionados.
     * @param codigosCPSISeleccionados listado de códigos a liberar
     * @return error en caso de producirse
     */
    String reservarCodigosCPSI(List<InfoCatCpsi> codigosCPSISeleccionados);

    /**
     * Método encargado de obtener los datos del estudio de códigos CPS Internacionales.
     * @param proveedor campo por el que se filtra la información
     * @return List<VEstudioCPSI> información del estudio
     */
    VEstudioCPSI estudioCPSI(Proveedor proveedor);

    /**
     * Libera un CPSI desde una Solicitud de Liberación.
     * @param pLibSol Información de la liberación solicitada
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return Código de EstadoLiberacionSolicitada indicando el nuevo estado de la liberación solicitada.
     * @throws Exception en caso de error.
     */
    String liberarCpsi(LiberacionSolicitadaCpsi pLibSol, boolean pInmediata) throws Exception;

    /**
     * Libera los CPSI en cuarentana una vez finalizada la fecha de reserva.
     */
    void liberarCuarentena();

    /**
     * Cede un CPSI.
     * @param pCesSol Información de la cesión solicitada
     * @param pInmediata Indica si la fecha de implementación es inmediata (true) o programada (false)
     * @return Código de EstadoCesionSolicitada indicando el nuevo estado de la cesión solicitada.
     * @throws Exception en caso de error.
     */
    String cederCpsi(CesionSolicitadaCpsi pCesSol, boolean pInmediata) throws Exception;

    /**
     * Recupera la información de CPSI para un Proveedor concreto.
     * @param pProveedor Proveedor por el que filtrar la información.
     * @return Objeto Linea1EstudioCPSI con la información del estudio.
     */
    Linea1EstudioCPSI getEstudioCpsiProveedor(Proveedor pProveedor);

    /**
     * Asigna el CPSI solicitado al Proveedor Solicitante..
     * @param pCpsiSolicitado CPSI para asignar.
     * @param pProveedor Información del proveedor solicitante.
     * @throws Exception en caso de error.
     */
    void asignarCpsi(CpsiAsignado pCpsiSolicitado, Proveedor pProveedor) throws Exception;

    /**
     * Consulta de los códigos CPSI que cumplen el filtro.
     * @param filtros filtro de la búsqueda
     * @return listado de codigos.
     */
    List<InfoCatCpsi> findAllInfoCatCPSI(FiltroBusquedaCodigosCPSI filtros);

    /**
     * Método que elimina el código CPSI indicado.
     * @param codigo codigo
     */
    void removeCodigoCpsi(CodigoCPSI codigo);
}
