package mx.ift.sns.dao.ot;

import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.ot.ExportMunicipio;

/**
 * Interfaz DAO ExportMunicipio.
 * @author X36155QU
 */
public interface IExportMunicipioDao {

    /**
     * Genera la lista exportable de municipios con sus poblaciones.
     * @param filtros FiltroBusquedaMunicipios
     * @return lista datos
     */
    List<ExportMunicipio> findAllExportMunicipio(FiltroBusquedaMunicipios filtros);

    /**
     * Obtiene el total exportable de municipios con sus poblaciones.
     * @param filtros FiltroBusquedaMunicipios
     * @return total datos
     */
    Integer findAllExportMunicipioCount(FiltroBusquedaMunicipios filtros);

}
