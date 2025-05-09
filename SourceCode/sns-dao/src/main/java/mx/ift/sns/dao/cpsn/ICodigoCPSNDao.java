package mx.ift.sns.dao.cpsn;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.cpsn.VEstudioCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSN;
import mx.ift.sns.modelo.pst.Proveedor;

/** Interfaz del dao de equipos de señalización. */
public interface ICodigoCPSNDao extends IBaseDAO<CodigoCPSN> {

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
     * Método que comprueba si código CPS Nacional se puede agrupar.
     * @param numMin cpsn mínimo del bloque a generar.
     * @param numMax cpsn máximo del bloque a generar
     * @param proveedor que deben tener todos los códigos a agrupar.
     * @return si se permite o no agrupar
     */
    boolean permitirAgrupar(int numMin, int numMax, Proveedor proveedor);

    /**
     * Método que obtiene los códigos CPS Nacionales a agrupar.
     * @param numMin cpsn mínimo del bloque a generar.
     * @param numMax cpsn máximo del bloque a generar.
     * @return List<CodigoCPSN> listado de códigos a agrupar
     */
    List<CodigoCPSN> getCodigosAAgrupar(int numMin, int numMax);

    /**
     * Método encargado de obtenerlos datos del estudio de códigos CPS Nacionales.
     * @return List<VEstudioCPSN> estudio
     */
    List<VEstudioCPSN> estudioCPSN();

    /**
     * Método encargado de obtener el código CPS por un id y el tipo de bloque.
     * @param cps Código CPS
     * @param tipoBloque del cps
     * @return boolean asginado
     */
    CodigoCPSN getCpsnByIdBloqueAsignado(Integer cps, TipoBloqueCPSN tipoBloque);

    /**
     * Método encargado de obtener el listado de códigos asignados a un pst.
     * @param pst proveedor
     * @return listado de c´dogiso asignados.
     */
    List<CodigoCPSN> getCodigosAsignadosAProveedor(Proveedor pst);

    /**
     * Recupera un Código CPSN en función de su tipo de bloque, identificador y el Proveedor asignado.
     * @param pIdTipoBloque Identificador del tipo de bloque.
     * @param pIdCodigo Identificador del Código CPSN.
     * @param pProveedor Proveedor asignatario del CPSN. Puede ser nulo si se buscan códigos libres.
     * @return CodigoCPSN
     */
    CodigoCPSN getCodigoCpsn(String pIdTipoBloque, BigDecimal pIdCodigo, Proveedor pProveedor);

    /**
     * Recupera el listado de Códigos CPSN asignados al Proveedor (Estatus 'A') y todos los que estan libres.
     * @param pProveedor Información del Proveedor.
     * @param tipoBloque tipo de bloque
     * @return List
     */
    List<CodigoCPSN> findAllCodigosCPSN(Proveedor pProveedor, TipoBloqueCPSN tipoBloque);

    /**
     * Método que obtiene el codigo cps nacional a partir de su id.
     * @param id del código cps
     * @return codigo cps nacional
     */
    CodigoCPSN getCodigoCPSNById(String id);
}
