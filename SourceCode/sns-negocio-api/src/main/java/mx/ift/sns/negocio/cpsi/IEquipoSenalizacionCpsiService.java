package mx.ift.sns.negocio.cpsi;

import java.io.File;
import java.util.List;

import mx.ift.sns.modelo.cpsi.EquipoSenalCpsi;
import mx.ift.sns.modelo.cpsi.EstudioEquipoCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaEquipoSenal;
import mx.ift.sns.modelo.pst.Proveedor;

/** Interfaz de servicios de los equipos de señalización CPSI. */
public interface IEquipoSenalizacionCpsiService {

    /**
     * Función que busca los equipos de señalización que cumplen con los criterios de búsqueda.
     * @param pFiltros FiltroBusquedaEquipoSenal
     * @return List<EquipoSenalCpsi>
     * @throws Exception ex
     */
    List<EquipoSenalCpsi> findAllEquiposSenal(FiltroBusquedaEquipoSenal pFiltros)
            throws Exception;

    /**
     * Función que valida si los datos del equipo de señalización internacional son correctos.
     * @param equipoSenalCPSI equipo
     * @param modoEdicion modo edicion
     * @return equipo de señalizacion
     */
    EquipoSenalCpsi validaEquipoSenal(EquipoSenalCpsi equipoSenalCPSI, boolean modoEdicion);

    /**
     * Método que guarda el equipo de señalización internacional.
     * @param equipo equipo a guardar
     * @param equipoTemp equipo anterior
     * @param modoEdicion boolean
     * @return equipo guardado
     */
    EquipoSenalCpsi guardar(EquipoSenalCpsi equipo, EquipoSenalCpsi equipoTemp, boolean modoEdicion);

    /**
     * Función que busca los equipos de señalización que cumplen con los criterios de búsqueda para la exportación.
     * @param pFiltros FiltroBusquedaEquipoSenal
     * @return byte[]
     * @throws Exception ex
     */
    byte[] exportarEquipos(FiltroBusquedaEquipoSenal pFiltros)
            throws Exception;

    /**
     * Función que elimina el equipo de señalización cpsi.
     * @param equipo cpsi
     */
    void eliminarEquipo(EquipoSenalCpsi equipo);

    /**
     * Método encargado de obtener el equipo de señalización cargando los warnings.
     * @param equipo a cargar
     * @return equipo cargado
     */
    EquipoSenalCpsi getEquipoSenalCpsiEagerLoad(EquipoSenalCpsi equipo);

    /**
     * Método encargado de obtener los datos del estudio de equipos CPS Nacionales.
     * @param pst proveedor
     * @return List<EstudioEquipoCpsi> estudio
     */
    List<EstudioEquipoCpsi> estudioEquipo(Proveedor pst);

    /**
     * Método encargado de procesar el archivo de equipos de señalización internacional.
     * @param archivo excel con los equipos a procesar.
     * @param pst proveedor
     * @return detalle de la importación
     * @throws Exception al procesar
     */
    DetalleImportacionEquiposCpsi procesarArchivoEquipos(File archivo, Proveedor pst) throws Exception;
}
