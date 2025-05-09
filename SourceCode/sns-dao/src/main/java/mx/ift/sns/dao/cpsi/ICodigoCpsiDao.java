package mx.ift.sns.dao.cpsi;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.InfoCatCpsi;
import mx.ift.sns.modelo.cpsi.Linea1EstudioCPSI;
import mx.ift.sns.modelo.cpsi.Linea2EstudioCPSI;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSI;
import mx.ift.sns.modelo.pst.Proveedor;

/** Interfaz del dao de equipos de señalización. */
public interface ICodigoCpsiDao extends IBaseDAO<CodigoCPSI> {

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
     * Método encargado de obtener parte de los datos del estudio de códigos CPS internacionales.
     * @param proveedor proveedor
     * @return Linea1EstudioCPSI
     */
    Linea1EstudioCPSI estudioCPSILinea1(Proveedor proveedor);

    /**
     * Método encargado de obtener parte de los datos del estudio de códigos CPS internacionales.
     * @return Linea2EstudioCPSI
     */
    Linea2EstudioCPSI estudioCPSILinea2();

    /**
     * Recupera la información de CPSI para un Proveedor concreto.
     * @param pProveedor Proveedor.
     * @return Objeto Linea1EstudioCPSI con la información del estudio.
     */
    Linea1EstudioCPSI getEstudioCpsiProveedor(Proveedor pProveedor);

    /**
     * Método encargado de obtener el listado de códigos asignados a un pst.
     * @param pst proveedor
     * @return listado de códigos asignados.
     */
    List<CodigoCPSI> getCodigosAsignadosAProveedor(Proveedor pst);

    /**
     * Recupera un Código CPSI en función de su identificador y el Proveedor asignado.
     * @param pIdCodigo Identificador del Código CPSI.
     * @param pProveedor Proveedor asignatario del CPSI. Puede ser nulo si se buscan códigos libres.
     * @return CodigoCPSI
     */
    CodigoCPSI getCodigoCpsi(BigDecimal pIdCodigo, Proveedor pProveedor);

    /**
     * Consulta de los datos que se muestran en el catalogo CPSI que cumplen el filtro.
     * @param pFiltro Filtro de la busqueda
     * @return InfoCatCpsi
     */
    List<InfoCatCpsi> findAllInfoCatCPSI(FiltroBusquedaCodigosCPSI pFiltro);

}
