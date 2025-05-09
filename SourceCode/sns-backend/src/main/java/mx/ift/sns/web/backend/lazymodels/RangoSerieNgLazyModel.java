package mx.ift.sns.web.backend.lazymodels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.comparators.RangoSerieCampoComparator;
import mx.ift.sns.web.backend.lazymodels.multiseleccion.ILazyModelRefreshable;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación LazyModel para consulta Lazy de Rangos Serie.
 */
public class RangoSerieNgLazyModel extends LazyDataModel<RangoSerie> {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RangoSerieNgLazyModel.class);

    /** Colección de objetos. */
    private List<RangoSerie> dataSource;

    /** Filtros de búsqueda. */
    private FiltroBusquedaRangos filtros;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Indica si hay que resetear la paginación. */
    private boolean resetPaginacion = false;

    /** Indica que se trata de una búsqueda automática usando los filtros de columna. */
    private boolean busquedaFiltros = false;

    /** Gestor de Selección Múltiple sobre la tabla Lazy. */
    private ILazyModelRefreshable multiSelectionManager;

    /** Constructor. */
    public RangoSerieNgLazyModel() {
        dataSource = new ArrayList<RangoSerie>(1);
    }

    @Override
    public RangoSerie getRowData(String rowKey) {
        for (RangoSerie rango : dataSource) {
            if (rango.getId().getId().toString().equals(rowKey)) {
                return rango;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(RangoSerie pRango) {
        return pRango.getId().getId().toString();
    }

    @Override
    public List<RangoSerie> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        if (filtros == null || service == null) {
            LOGGER.error("Error en la carga, el servicio o los filtros no se han definido.");
            return new ArrayList<RangoSerie>(1);
        }

        try {
            // Diferentes tipos de búsqueda en función de los filtros de columna.
            boolean usarFiltroColumnas = (filters != null && !filters.isEmpty());
            if (usarFiltroColumnas) {
                // Si se usan los filtros de columna, no se puede paginar en la búsqueda
                // ya que los filtros se aplican sobre todos los resueltados.
                filtros.setUsarPaginacion(false);

                // Al usar filtros de columna, tenemos que cargar todos los rangos que coincidan
                // con los filtros introducidos por el usuario en la búsqueda.
                if (!busquedaFiltros) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(filtros.toString());
                    }
                    // Si es la primera vez que se aplican filtros de columna cargarmos los
                    // rangos solicitados sin paginación.
                    dataSource.clear();
                    dataSource.addAll(service.findAllRangos(filtros));

                    // Indicamos que se ha realizado una búsqueda automática por filtros.
                    busquedaFiltros = true;
                }

                // Contenedor de resultados por filtro de columna sobre la búsqueda
                List<RangoSerie> dataSourceFiltered = new ArrayList<RangoSerie>();

                // Pasamos los filtros para cada Rango
                for (RangoSerie rango : dataSource) {
                    boolean match = true;
                    for (Entry<String, Object> filter : filters.entrySet()) {
                        String filterField = filter.getKey();
                        String filterValue = String.valueOf(filter.getValue());
                        if (filterValue != null) {
                            String fieldValue = this.getFieldValue(rango, filterField);
                            match = fieldValue.startsWith(filterValue);
                            if (!match) {
                                break;
                            }
                        }
                    }
                    if (match) {
                        dataSourceFiltered.add(rango);
                    }
                }

                // Al ordenar con filtros de columna, la ordenación se hace directamente en el array
                // de resultados.
                if (sortField != null) {
                    Collections.sort(dataSourceFiltered, new RangoSerieCampoComparator(sortField, sortOrder));
                }

                // Páginas a mostrar en el navegador de la tabla.
                int dataSize = dataSourceFiltered.size();
                this.setRowCount(dataSize);
                if (dataSize > filtros.getResultadosPagina()) {
                    try {
                        return dataSourceFiltered.subList(first, first + filtros.getResultadosPagina());
                    } catch (IndexOutOfBoundsException e) {
                        return dataSourceFiltered.subList(first, first + (dataSize % filtros.getResultadosPagina()));
                    }
                } else {
                    return dataSourceFiltered;
                }

            } else {
                // Si no se utilizan filtros de columna, se puede paginar.
                filtros.setUsarPaginacion(true);
                // filtros.setResultadosPagina(pageSize);

                // Si la búsqueda anterior era por filtros hay que reiniciar los contadores. Si no,
                // dejamos el valor que tenga "resetPaginacion" por defecto.
                if (busquedaFiltros) {
                    resetPaginacion = true;
                    busquedaFiltros = false;
                }

                // Si la búsqueda es sin filtros de campo, la ordenación se hace en la query
                // al buscar los resueltados.
                if (sortField != null) {
                    filtros.getOrdenCampos().clear();
                    if (sortOrder.name().equals("ASCENDING")) {
                        filtros.setOrdernarPor(sortField, FiltroBusquedaRangos.ORDEN_ASC);
                    } else if (sortOrder.name().equals("DESCENDING")) {
                        filtros.setOrdernarPor(sortField, FiltroBusquedaRangos.ORDEN_DESC);
                    }
                }

                // Solo se resetea la paginación cuando se pulsa en buscar, no cuando se pagina.
                if (resetPaginacion) {
                    filtros.setNumeroPagina(0);
                    this.setRowCount(service.findAllRangosCount(filtros));
                    resetPaginacion = false;
                } else {
                    // this.setRowCount(dataSource.size());
                    filtros.setNumeroPagina(first);
                }

                // Búsqueda normal sin filtros de columna. Solo con filtros de interfaz.
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(filtros.toString());
                }
                dataSource.clear();
                dataSource.addAll(service.findAllRangos(filtros));

                // CallBack para el gestor de selección múltiple que gestiona éste LazyModel. Por cada invocación Ajax
                // al método 'load' del LazyModel se invoca al método 'refreshLazyModelSeleccion' implementado en el
                // gestor para que actualice la selección múltiple que se ha de mostrar en la tabla.
                if (multiSelectionManager != null) {
                    multiSelectionManager.refreshLazyModelSelection(first, filtros.getResultadosPagina(), null);
                }

                return dataSource;
            }

        } catch (Exception e) {
            LOGGER.error("load", e);
            dataSource = new ArrayList<RangoSerie>(1);
            return dataSource;
        }
    }

    /** Resetea los valores de búsqueda y paginación. */
    public void clear() {
        resetPaginacion = true;
        busquedaFiltros = false;
    }

    /**
     * Recupera el valor de un campo específico de un rango.
     * @param pRango Información del Rango.
     * @param pCampo Valor del campo a recuperar.
     * @return String con el valor del campo a recuperar.
     */
    public String getFieldValue(RangoSerie pRango, String pCampo) {

        // Se podría utilizar reflection para obtener los campos pero es
        // muy costoso computacionalmente y no merece la pena ya que sabemos
        // de antemano qué campos se van a requerir y es más rápido usar
        // los if's.

        // En RangoSerieCampoComparator existe el mismo método. Si se modifica
        // éste hay que modificar el homónimo en RangoSerieCampoComparator

        String fieldValue = "";
        try {
            if (pCampo.equals("serie.nir.abn.codigoAbn")) {
                fieldValue = String.valueOf(pRango.getSerie().getNir().getAbn().getCodigoAbn());
            } else if (pCampo.equals("serie.id.sna")) {
                fieldValue = pRango.getId().getSna().toString();
            } else if (pCampo.equals("serie.nir.codigo")) {
                fieldValue = String.valueOf(pRango.getSerie().getNir().getCodigo());
            } else if (pCampo.equals("numInicio")) {
                fieldValue = pRango.getNumInicio();
            } else if (pCampo.equals("numFinal")) {
                fieldValue = pRango.getNumFinal();
            } else if (pCampo.equals("tipoRed.descripcion")) {
                fieldValue = pRango.getTipoRed().getDescripcion();
            } else if (pCampo.equals("centralOrigen.nombre")) {
                fieldValue = pRango.getCentralOrigen().getNombre();
            } else if (pCampo.equals("centralDestino.nombre")) {
                fieldValue = pRango.getCentralDestino().getNombre();
            } else if (pCampo.equals("poblacion.inegi")) {
                fieldValue = pRango.getPoblacion().getInegi();
            } else if (pCampo.equals("poblacion.municipio.estado.nombre")) {
                fieldValue = pRango.getPoblacion().getMunicipio().getEstado().getNombre();
            } else if (pCampo.equals("poblacion.municipio.nombre")) {
                fieldValue = pRango.getPoblacion().getMunicipio().getNombre();
            } else if (pCampo.equals("poblacion.nombre")) {
                fieldValue = pRango.getPoblacion().getNombre();
            } else if (pCampo.equals("asignatario.nombre")) {
                fieldValue = pRango.getAsignatario().getNombre();
            } else if (pCampo.equals("idoPnn")) {
                fieldValue = pRango.getIdoPnn().toString();
            } else if (pCampo.equals("idaPnn")) {
                fieldValue = pRango.getIdaPnn().toString();
            } else if (pCampo.equals("consecutivoAsignacion")) {
                fieldValue = String.valueOf(pRango.getConsecutivoAsignacion());
            } else if (pCampo.equals("estadoRango.descripcion")) {
                fieldValue = pRango.getEstadoRango().getDescripcion();
            } else if (pCampo.equals("concesionario.nombre")) {
                fieldValue = pRango.getConcesionario().getNombre();
            } else if (pCampo.equals("concesionario.ido")) {
                fieldValue = pRango.getConcesionario().getIdo().toString();
            } else if (pCampo.equals("arrendatario.nombre")) {
                fieldValue = pRango.getArrendatario().getNombre();
            } else if (pCampo.equals("oficioAsignacion")) {
                fieldValue = pRango.getOficioAsignacion();
            } else if (pCampo.equals("fechaAsignacion")) {
                if (pRango.getFechaAsignacion() != null) {
                    fieldValue = FechasUtils.fechaToString(pRango.getFechaAsignacion());
                }
            }
        } catch (NullPointerException npe) {
            fieldValue = "";
        }

        if (fieldValue == null) {
            fieldValue = "";
        }

        return fieldValue;
    }

    // GETTERS & SETTERS

    /**
     * Asocia los filtros que se usarán en las búsquedas.
     * @param filtros FiltroBusquedaSeries
     */
    public void setFiltros(FiltroBusquedaRangos filtros) {
        this.filtros = filtros;
    }

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param service INumeracionGeograficaService
     */
    public void setService(INumeracionGeograficaService service) {
        this.service = service;
    }

    /**
     * Gestor de Selección Múltiple sobre la tabla Lazy.
     * @param multiSelectionManager ILazyModelRefreshable
     */
    public void setMultiSelectionManager(ILazyModelRefreshable multiSelectionManager) {
        this.multiSelectionManager = multiSelectionManager;
    }

}
