package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IModeloDao;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMarcaModelo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Modelos.
 */
@Named
public class ModeloDaoImpl extends BaseDAO<Modelo> implements IModeloDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ModeloDaoImpl.class);

    @Override
    public Modelo getModeloById(BigDecimal id) {

        String query = "SELECT mo FROM Modelo mo WHERE mo.id=:id";

        Modelo modelo = null;
        try {
            TypedQuery<Modelo> tQuery = getEntityManager().createQuery(query, Modelo.class);
            tQuery.setParameter("id", id);
            modelo = tQuery.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }

        return modelo;
    }

    @Override
    public Modelo getModeloByMarca(BigDecimal id, String tipoModelo) {

        String query = "SELECT mo FROM Modelo mo WHERE mo.marca.id=:id and mo.tipoModelo=:tipoModelo";

        Modelo modelo = null;
        try {
            //Se asegura que solo retorne un registros asi existan mas de una en cat_modelo_central
        	TypedQuery<Modelo> tQuery = getEntityManager().createQuery(query, Modelo.class).setMaxResults(1);
            tQuery.setParameter("id", id);
            tQuery.setParameter("tipoModelo", tipoModelo);
            modelo = tQuery.getSingleResult();

            
        } catch (NoResultException nre) {
            return null;
        }

        return modelo;
    }

    @Override
    public List<Modelo> getModelosByMarca(BigDecimal id) {

        String query = "SELECT mo FROM Modelo mo WHERE mo.marca.id=:id";

        List<Modelo> modelos = null;
        try {
            TypedQuery<Modelo> tQuery = getEntityManager().createQuery(query, Modelo.class);
            tQuery.setParameter("id", id);
            modelos = tQuery.getResultList();

        } catch (NoResultException nre) {
            return null;
        }

        return modelos;
    }

    @Override
    public List<Modelo> findAllModelos(FiltroBusquedaMarcaModelo pFiltros) {

        // Consulta
        StringBuffer sbQuery = new StringBuffer();
        String sQuery = "";
        ArrayList<FiltroBusqueda> listaFiltros = pFiltros.getListaFiltros();
        sbQuery.append("SELECT m FROM Modelo m ");

        for (FiltroBusqueda filtro : listaFiltros) {
            sbQuery.append(" AND m.");
            sbQuery.append(filtro.getCampo()).append(" = :").append(filtro.getCampo().replace(".", ""));
        }

        sQuery = sbQuery.toString();
        if (null != listaFiltros && listaFiltros.size() > 0) {
            sQuery = sQuery.replaceFirst("AND", "WHERE");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + sQuery.toString());
        }

        sQuery = sQuery + " Order by m.id";
        TypedQuery<Modelo> query = getEntityManager().createQuery(sQuery, Modelo.class);
        // parametros
        for (FiltroBusqueda filtro : listaFiltros) {
            query.setParameter(filtro.getCampo().replace(".", ""), filtro.getValor());
        }

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            query.setFirstResult(pFiltros.getNumeroPagina());
            query.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<Modelo> list = query.getResultList();

        return list;
    }

    @Override
    public int findAllModelosCount(FiltroBusquedaMarcaModelo pFiltros) {

        // Consulta
        StringBuffer sbQuery = new StringBuffer();
        String sQuery = "";
        ArrayList<FiltroBusqueda> listaFiltros = pFiltros.getListaFiltros();
        sbQuery.append("SELECT COUNT(m) FROM Modelo m ");

        for (FiltroBusqueda filtro : listaFiltros) {
            sbQuery.append(" AND m.");
            sbQuery.append(filtro.getCampo()).append(" = :").append(filtro.getCampo().replace(".", ""));
        }

        sQuery = sbQuery.toString();
        if (null != listaFiltros && listaFiltros.size() > 0) {
            sQuery = sQuery.replaceFirst("AND", "WHERE");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + sQuery.toString());
        }

        TypedQuery<Long> query = getEntityManager().createQuery(sQuery, Long.class);
        // parametros
        for (FiltroBusqueda filtro : listaFiltros) {
            query.setParameter(filtro.getCampo().replace(".", ""), filtro.getValor());
        }

        int rowCount = (query.getSingleResult()).intValue();
        return rowCount;
    }

}
