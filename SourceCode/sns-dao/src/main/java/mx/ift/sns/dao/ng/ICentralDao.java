package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.ComboCentral;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.central.ReporteCentralVm;
import mx.ift.sns.modelo.central.VCatalogoCentral;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCentrales;
import mx.ift.sns.modelo.filtros.FiltroReporteadorCentrales;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz de definición de los métodos para base de datos de centrales.
 */
public interface ICentralDao extends IBaseDAO<Central> {

    /**
     * Método que recupera la central por id.
     * @param idCentral id central
     * @return central
     */
    Central getCentralById(BigDecimal idCentral);

    /**
     * Método que recupera las centrales Activas por Modelo.
     * @param modelo id modelo
     * @return List<Central>
     */
    List<Central> getCentralesActivasByModelo(Modelo modelo);

    /**
     * Busca las posibles centrales origen de un concesionario y un arredatario por nombre.
     * @param name name
     * @param concesionario concesionario
     * @param arrendatario arrendatario
     * @return List<Central>
     */
    List<Central> findAllCentralesOrigenByName(String name, Proveedor concesionario, Proveedor arrendatario);

    /**
     * Busca las centrales por nombre.
     * @param name name
     * @return List<Central>
     */
    List<Central> findAllCentralesByName(String name);

    /**
     * Recupera la lista de centrales que pertenecen al Proveedor.
     * @param pProveedor Proveedor
     * @param name name
     * @return List<Central>
     */
    List<Central> findAllCentralesByProveedor(Proveedor pProveedor, String name);

    /**
     * Método que recupera la central por el nombre.
     * @param name name
     * @return central
     */
    List<Central> getCentralByName(String name);

    /**
     * Recupera las centrales de uno o dos proveedores.
     * @param pst1 Proveedor
     * @param pst2 Proveedor
     * @return lista Central
     */
    List<Central> findAllCentralesByProveedores(Proveedor pst1, Proveedor pst2);

    /**
     * Obtiene el combo del catalogo de centrales.
     * @return combo centrales
     */
    List<ComboCentral> getComboCentrales();

    /**
     * Recupera todas las centrales paginados.
     * @param pFiltrosCentral pFiltrosCentral
     * @return List<VCatalogoCentral>
     * @exception Exception exception
     */
    List<VCatalogoCentral> findAllCentrales(FiltroBusquedaCentrales pFiltrosCentral);

    /**
     * Recupera el número de centrales.
     * @param pFiltrosCentral pFiltrosCentral
     * @return número de centrales
     * @exception Exception exception
     */
    int findAllCentralesCount(FiltroBusquedaCentrales pFiltrosCentral);

    /**
     * Recupera las centrales con localidades a las que da servicio.
     * @param filtro filtro
     * @return ReporteCentralVm
     */
    List<ReporteCentralVm> findAllCentralesPorLocalidad(FiltroReporteadorCentrales filtro);

    /**
     * Count de centrales con localidades a las que da servicio.
     * @param filtro filtro
     * @return count
     */
    int findAllCentralesPorLocalidadCount(FiltroReporteadorCentrales filtro);

    /**
     * Comprueba que una central extiste por su nombre, proveedor y coordenadas. Devuelve la central existente si no
     * retorna la central que se comprueba.
     * @param central central
     * @return central
     */
    Central comprobarCentral(Central central);

}
