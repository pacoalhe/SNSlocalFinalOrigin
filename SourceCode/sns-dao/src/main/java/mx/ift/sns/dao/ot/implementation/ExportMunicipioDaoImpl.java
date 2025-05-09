package mx.ift.sns.dao.ot.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ot.IExportMunicipioDao;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.ot.ExportMunicipio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion DAO ExportMunicipio.
 * @author X36155QU
 */
@Named
public class ExportMunicipioDaoImpl extends BaseDAO<ExportMunicipio> implements IExportMunicipioDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportMunicipioDaoImpl.class);

    @Override
    public List<ExportMunicipio> findAllExportMunicipio(FiltroBusquedaMunicipios filtros) {

        StringBuffer sbQuery = new StringBuffer();

        sbQuery.append("SELECT e FROM ExportMunicipio e");
        // Query Dinámica en función de los filtros
        List<FiltroBusqueda> listaFiltros = filtros.getListaFiltrosExport();

        if (!listaFiltros.isEmpty()) {
            sbQuery.append(" WHERE ");
        }
        for (int i = 0; i < listaFiltros.size(); i++) {
            if (i > 0) {
                sbQuery.append(" AND ");
            }

            FiltroBusqueda filter = listaFiltros.get(i);
            sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                    .append(filter.getCampo());
        }

        sbQuery.append(" ORDER BY e.idEstado, e.idMunicipio");
        Query query = getEntityManager().createQuery(sbQuery.toString());

        // parametros
        for (FiltroBusqueda filter : listaFiltros) {
            query.setParameter(filter.getCampo(), filter.getValor());
        }

        @SuppressWarnings("unchecked")
        List<ExportMunicipio> listaDatos = query.getResultList();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontradas {} Poblaciones", listaDatos.size());
        }

        return listaDatos;

    }

    @Override
    public Integer findAllExportMunicipioCount(FiltroBusquedaMunicipios filtros) {

        Long total = new Long(0);

        StringBuffer sbQuery = new StringBuffer();

        sbQuery.append("SELECT COUNT(e) FROM ExportMunicipio e");
        // Query Dinámica en función de los filtros
        List<FiltroBusqueda> listaFiltros = filtros.getListaFiltrosExport();

        if (!listaFiltros.isEmpty()) {
            sbQuery.append(" WHERE ");
        }
        for (int i = 0; i < listaFiltros.size(); i++) {
            if (i > 0) {
                sbQuery.append(" AND ");
            }

            FiltroBusqueda filter = listaFiltros.get(i);
            sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                    .append(filter.getCampo());
        }

        sbQuery.append(" ORDER BY e.idEstado, e.idMunicipio");
        Query query = getEntityManager().createQuery(sbQuery.toString());

        // parametros
        for (FiltroBusqueda filter : listaFiltros) {
            query.setParameter(filter.getCampo(), filter.getValor());
        }

        total = (Long) query.getSingleResult();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontradas {} Poblaciones", total);
        }

        return total.intValue();

    }

}
