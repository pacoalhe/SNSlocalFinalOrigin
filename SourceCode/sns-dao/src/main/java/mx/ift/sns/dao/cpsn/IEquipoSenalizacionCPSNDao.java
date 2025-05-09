package mx.ift.sns.dao.cpsn;

import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsn.EquipoSenalCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaEquipoSenal;
import mx.ift.sns.modelo.pst.Proveedor;

/** Interfaz del dao de equipos de señalización. */
public interface IEquipoSenalizacionCPSNDao extends IBaseDAO<EquipoSenalCPSN> {

    /**
     * Función que busca los equipos de señalización que cumplen con los criterios de búsqueda.
     * @param pFiltros FiltroBusquedaEquipoSenal
     * @return List<EquipoSenal>
     * @throws Exception ex
     */
    List<EquipoSenalCPSN> findAllEquiposSenal(FiltroBusquedaEquipoSenal pFiltros)
            throws Exception;

    /**
     * Método que comprueba si existe el equipo de señalización en bbdd.
     * @param equipoSenalCPSN equipo
     * @return existe
     */
    boolean existeEquipoCPSN(EquipoSenalCPSN equipoSenalCPSN);

    /**
     * Mñetodo que obtiene el listado de equipos de señalización que comparte Nombre, Longitud, Latitud y Pst con el
     * equipo guardado.
     * @param equipoTemp equipo
     * @return listado de equipos
     */
    List<EquipoSenalCPSN> obtenerEquiposAActualizar(EquipoSenalCPSN equipoTemp);

    /**
     * Método que obtiene el listado de equipos a exportar.
     * @param pFiltros filtros de la consulta
     * @return List<EquipoSenalCPSN> listado
     */
    List<EquipoSenalCPSN> findAllEquiposSenalExp(FiltroBusquedaEquipoSenal pFiltros);

    /**
     * Método encargado de obtener los equipos de señalizacion asignados al proveedor.
     * @param pst proveedor
     * @return listado de equipos
     */
    List<EquipoSenalCPSN> getEquiposCPSNByProveedor(Proveedor pst);

    /**
     * Función que elimina el equipo de señalización cpsn.
     * @param equipo cpsn
     */
    void eliminarEquipo(EquipoSenalCPSN equipo);

    /**
     * Método encargado de obtener el equipo de señalización cargando los warnings.
     * @param equipo a cargar
     * @return equipo cargado
     */
    EquipoSenalCPSN getEquipoSenalCPSNEagerLoad(EquipoSenalCPSN equipo);

    /**
     * Función que elimina los equipos asociados a un pst.
     * @param pst del que se eliminarán los equipos
     */
    void eliminarEquiposByPst(Proveedor pst);
}
