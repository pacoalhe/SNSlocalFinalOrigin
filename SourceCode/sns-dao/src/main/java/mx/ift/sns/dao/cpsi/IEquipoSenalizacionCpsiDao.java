package mx.ift.sns.dao.cpsi;

import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsi.EquipoSenalCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaEquipoSenal;
import mx.ift.sns.modelo.pst.Proveedor;

/** Interfaz del dao de equipos de señalización. */
public interface IEquipoSenalizacionCpsiDao extends IBaseDAO<EquipoSenalCpsi> {

    /**
     * Función que busca los equipos de señalización que cumplen con los criterios de búsqueda.
     * @param pFiltros FiltroBusquedaEquipoSenal
     * @return List<EquipoSenal>
     * @throws Exception ex
     */
    List<EquipoSenalCpsi> findAllEquiposSenal(FiltroBusquedaEquipoSenal pFiltros) throws Exception;

    /**
     * Método que comprueba si existe el equipo de señalización en bbdd.
     * @param equipoSenalCPSN equipo
     * @return existe
     */
    boolean existeEquipo(EquipoSenalCpsi equipoSenalCPSN);

    /**
     * Mñetodo que obtiene el listado de equipos de señalización que comparte Nombre, Longitud, Latitud y Pst con el
     * equipo guardado.
     * @param equipoTemp equipo
     * @return listado de equipos
     */
    List<EquipoSenalCpsi> obtenerEquiposAActualizar(EquipoSenalCpsi equipoTemp);

    /**
     * Método que obtiene el listado de equipos a exportar.
     * @param pFiltros filtros de la consulta
     * @return List<EquipoSenalCPSN> listado
     */
    List<EquipoSenalCpsi> findAllEquiposSenalExp(FiltroBusquedaEquipoSenal pFiltros);

    /**
     * Método encargado de obtener los equipos de señalizacion asignados al proveedor.
     * @param pst proveedor
     * @return listado de equipos
     */
    List<EquipoSenalCpsi> getEquiposByProveedor(Proveedor pst);

    /**
     * Método encargado de obtener el equipo de señalización cargando los warnings.
     * @param equipo a cargar
     * @return equipo cargado
     */
    EquipoSenalCpsi getEquipoSenalEagerLoad(EquipoSenalCpsi equipo);

    /**
     * Función que elimina los equipos asociados a un pst.
     * @param pst del que se eliminarán los equipos
     */
    void eliminarEquiposByPst(Proveedor pst);
}
