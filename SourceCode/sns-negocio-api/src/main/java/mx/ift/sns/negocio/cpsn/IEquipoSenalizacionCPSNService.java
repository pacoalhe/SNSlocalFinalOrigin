package mx.ift.sns.negocio.cpsn;

import java.io.File;
import java.util.List;

import mx.ift.sns.modelo.cpsn.EquipoSenalCPSN;
import mx.ift.sns.modelo.cpsn.EstudioEquipoCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaEquipoSenal;
import mx.ift.sns.modelo.pst.Proveedor;

/** Interfaz de servicios de los equipos de señalización. */
public interface IEquipoSenalizacionCPSNService {

    /**
     * Función que busca los equipos de señalización que cumplen con los criterios de búsqueda.
     * @param pFiltros FiltroBusquedaEquipoSenal
     * @return List<EquipoSenal>
     * @throws Exception ex
     */
    List<EquipoSenalCPSN> findAllEquiposSenal(FiltroBusquedaEquipoSenal pFiltros)
            throws Exception;

    /**
     * Función que valida si los datos del equipo de señalización nacional son correctos.
     * @param equipoSenalCPSN equipo
     * @param modoEdicion modo edicion
     * @return equipo de señalizacion
     */
    EquipoSenalCPSN validaEquipoSenalCPSN(EquipoSenalCPSN equipoSenalCPSN, boolean modoEdicion);

    /**
     * Método que guarda el equipo de señalización nacional.
     * @param equipo equipo a guardar
     * @param equipoTemp equipo anterior
     * @param modoEdicion boolean
     * @return equipo guardado
     */
    EquipoSenalCPSN guardar(EquipoSenalCPSN equipo, EquipoSenalCPSN equipoTemp, boolean modoEdicion);

    /**
     * Función que busca los equipos de señalización que cumplen con los criterios de búsqueda para la exportación.
     * @param pFiltros FiltroBusquedaEquipoSenal
     * @return byte[]
     * @throws Exception ex
     */
    byte[] exportarEquiposCPSN(FiltroBusquedaEquipoSenal pFiltros)
            throws Exception;

    /**
     * Método encargado de obtener los datos del estudio de equipos CPS Nacionales.
     * @param pst proveedor
     * @return List<EstudioEquipoCPSN> estudio
     */
    List<EstudioEquipoCPSN> estudioEquipoCPSN(Proveedor pst);

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
     * Función encargada de comprobar si un código cps esá asignado a un proveedor.
     * @param cps código
     * @param pst proveedor
     * @return si está asignado
     */
    boolean cpsAsignadoAPst(Integer cps, Proveedor pst);

    /**
     * Método encargado de procesar el archivo de equipos de señalización nacional.
     * @param archivo excel con los equipos a procesar.
     * @param pst proveedor
     * @return detalle de la importación
     * @throws Exception al procesar
     */
    DetalleImportacionEquiposCpsn procesarArchivoEquipos(File archivo, Proveedor pst) throws Exception;
}
