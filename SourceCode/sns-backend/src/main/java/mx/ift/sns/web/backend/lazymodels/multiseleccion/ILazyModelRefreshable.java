package mx.ift.sns.web.backend.lazymodels.multiseleccion;


/**
 * Métodos que han de cumplir las clases invocadas por los Lazy Model para actualizar la selección de la paginación
 * actual.
 * @author X53490DE
 */
public interface ILazyModelRefreshable {

    /**
     * Método lanzado por los LazyModel sobre los gestores de multiselección para que éstos actualicen la selección de
     * registros sobre las tablas cuando existe selección múltiple. Es necesario actualizar las tablas cuando se hace
     * paginación con tablas lazy por que se pierde la selección múltiple entre páginas. Los Tabs deben mantener la
     * lista de registros seleccionados e indicarle a la tabla qué objetos hay seleccionados en cada página.
     * @param pFirstItem Índice del primer elemento de la tabla.
     * @param pPageSize Tamaño de página en la tabla.
     * @param pLazyDataSource Registros cargados por el LazyModel para la página actual.
     * @throws Exception checkeable para que sea capturada y gestionada por los LazyModels.
     */
    void refreshLazyModelSelection(int pFirstItem, int pPageSize, Object[] pLazyDataSource) throws Exception;

}
