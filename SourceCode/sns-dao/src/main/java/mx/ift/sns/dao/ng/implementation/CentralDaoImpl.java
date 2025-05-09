package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.ICentralDao;
import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.ComboCentral;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.central.ReporteCentralVm;
import mx.ift.sns.modelo.central.VCatalogoCentral;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCentrales;
import mx.ift.sns.modelo.filtros.FiltroReporteadorCentrales;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoProveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Centrales.
 */
@Named
public class CentralDaoImpl extends BaseDAO<Central> implements ICentralDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CentralDaoImpl.class);

    @Override
    public Central getCentralById(BigDecimal idCentral) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Buscando Central con Id {}", idCentral.toString());
        }

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT c FROM Central c WHERE c.id=:cdgCentral ");
        sbQuery.append("ORDER BY c.nombre, c.latitud, c.longitud, c.proveedor.nombre");

        Central central = null;
        try {
            TypedQuery<Central> query = getEntityManager().createQuery(sbQuery.toString(), Central.class);
            query.setParameter("cdgCentral", idCentral);
            central = query.getSingleResult();
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Central no encontrada");
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Central encontrada: {}", central.getNombre());
        }
        return central;
    }

    @Override
    public List<Central> getCentralesActivasByModelo(Modelo modelo) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Buscando Central con idModelo {}", modelo);
        }

        Estatus estado = new Estatus();
        estado.setCdg(Estatus.ACTIVO);

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT c FROM Central c WHERE c.modelo=:modelo and c.estatus = :estatus");

        List<Central> centrales = null;
        try {
            TypedQuery<Central> query = getEntityManager().createQuery(sbQuery.toString(), Central.class);
            query.setParameter("modelo", modelo);
            query.setParameter("estatus", estado);
            centrales = query.getResultList();
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Central no encontrada");
            }
        }

        return centrales;

    }

    @Override
    public List<ComboCentral> getComboCentrales() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Buscando centrales");
        }

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT NEW mx.ift.sns.modelo.central.ComboCentral");
        sbQuery.append("(c.id, c.nombre, c.latitud, c.longitud, c.proveedor.nombre) ");
        sbQuery.append("FROM Central c ORDER BY c.nombre, c.latitud, c.longitud, c.proveedor.nombre");

        TypedQuery<ComboCentral> query = getEntityManager().createQuery(sbQuery.toString(), ComboCentral.class);
        List<ComboCentral> list = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Centrales encontradas: {}", list.size());
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Central> findAllCentralesOrigenByName(String name, Proveedor concesionario, Proveedor solicitante) {

        List<Central> list = new ArrayList<Central>();

        String query = "SELECT c FROM Central c "
                + "WHERE (c.proveedor =:concesionario OR c.proveedor = :solicitante) "
                + "AND UPPER(CONCAT(c.nombre, c.latitud, c.longitud)) "
                + "LIKE :name ORDER BY c.nombre, c.latitud, c.longitud";

        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("Query: " + query);
        // }

        Query nativeQuery = getEntityManager().createQuery(query);
        nativeQuery.setParameter("concesionario", concesionario);
        nativeQuery.setParameter("solicitante", solicitante);
        nativeQuery.setParameter("name", name.toUpperCase() + "%");

        list = nativeQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {

            if (list != null) {
                LOGGER.debug("#centrales={}", list.size());
            }
        }

        return list;
    }

    @Override
    public List<Central> findAllCentralesByName(String name) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Buscando centrales por nombre: {}", name);
        }

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT c FROM Central c ");
        sbQuery.append("WHERE UPPER(CONCAT(c.nombre, c.latitud, c.longitud, c.proveedor.nombre)) ");
        sbQuery.append("LIKE :name ORDER BY c.nombre, c.latitud, c.longitud, c.proveedor.nombre");

        TypedQuery<Central> nativeQuery = getEntityManager().createQuery(sbQuery.toString(), Central.class);
        nativeQuery.setParameter("name", name.toUpperCase() + "%");

        List<Central> list = nativeQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Centrales encontradas: {}", list.size());
        }

        return list;
    }

    @Override
    public List<Central> findAllCentralesByProveedor(Proveedor pProveedor, String name) {
        String query;
        if (pProveedor.getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {
            // Si es comercializadora hay que incluir las Centrales de los Concesionarios con los que
            // tiene convenio
            query = "SELECT DISTINCT(c) FROM Central c WHERE c.proveedor = :pst OR c.proveedor IN"
                    + " (SELECT pc.proveedorConcesionario FROM ProveedorConvenio pc"
                    + " WHERE pc.proveedorConvenio = :pst )";
        } else {
            query = "SELECT c FROM Central C WHERE c.proveedor = :pst";
        }

        // Se compara el nombre de central introducido
        query += " AND UPPER(c.nombre) LIKE :name ORDER BY c.nombre, c.latitud, c.longitud";

        TypedQuery<Central> tQuery = getEntityManager().createQuery(query, Central.class);
        tQuery.setParameter("pst", pProveedor);

        tQuery.setParameter("name", name.toUpperCase() + "%");

        List<Central> resultado = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} centrales para el PST {}", resultado.size(), pProveedor.getId());
        }

        return resultado;

    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Central> getCentralByName(String name) {
        List<Central> list = new ArrayList<Central>();

        String query = "SELECT c FROM Central c WHERE UPPER(c.nombre) =:name";

        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("Query: " + query);
        // }

        Query nativeQuery = getEntityManager().createQuery(query);
        nativeQuery.setParameter("name", name.toUpperCase());

        list = nativeQuery.getResultList();

        return list;
    }

    @Override
    public List<Central> findAllCentralesByProveedores(Proveedor pst1, Proveedor pst2) {

        String query = "SELECT c FROM Central c WHERE c.proveedor = :pst1 OR c.proveedor = :pst2 "
                + "ORDER BY c.nombre, c.latitud, c.longitud";

        TypedQuery<Central> tQuery = getEntityManager().createQuery(query, Central.class);
        tQuery.setParameter("pst1", pst1);
        tQuery.setParameter("pst2", pst2);

        return tQuery.getResultList();

    }

    @Override
    public List<VCatalogoCentral> findAllCentrales(FiltroBusquedaCentrales pFiltrosCentral) {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosCentral.getListaFiltros();

        StringBuilder sbQuery = new StringBuilder(
                "SELECT vcc FROM VCatalogoCentral vcc ");
        if (!pFiltrosCentral.getListaFiltros().isEmpty()) {
            sbQuery.append("WHERE ");

            for (int i = 0; i < filtros.size(); i++) {
                if (i > 0) {
                    sbQuery.append(" AND ");
                }

                FiltroBusqueda filter = filtros.get(i);

                if (filter.getCampo().equals("abns")) {
                    sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" LIKE ")
                            .append(":")
                            .append(filter.getCampo());
                } else {
                    sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                            .append(filter.getCampo());
                }
            }
        }

        TypedQuery<VCatalogoCentral> query = getEntityManager().createQuery(sbQuery.toString(), VCatalogoCentral.class);

        if (pFiltrosCentral.isUsarPaginacion()) {
            query.setFirstResult(pFiltrosCentral.getNumeroPagina()).setMaxResults(
                    pFiltrosCentral.getResultadosPagina());
        }

        for (FiltroBusqueda filter : filtros) {
            if (filter.getCampo().equals("abns")) {
                query.setParameter(filter.getCampo(), "%," + filter.getValor().toString().toUpperCase() + ",%");
            } else {
                query.setParameter(filter.getCampo(), filter.getValor());
            }
        }

        // LOGGER.debug(query.toString());
        List<VCatalogoCentral> lista = query.getResultList();

        return lista;

    }

    @Override
    public int findAllCentralesCount(FiltroBusquedaCentrales pFiltrosCentral) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltrosCentral.getListaFiltros();

        StringBuilder sbQuery = new StringBuilder(
                "SELECT COUNT(1) FROM VCatalogoCentral vcc ");
        if (!pFiltrosCentral.getListaFiltros().isEmpty()) {
            sbQuery.append("WHERE ");

            for (int i = 0; i < filtros.size(); i++) {
                if (i > 0) {
                    sbQuery.append(" AND ");
                }

                FiltroBusqueda filter = filtros.get(i);

                if (filter.getCampo().equals("abns")) {
                    sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" LIKE ")
                            .append(":")
                            .append(filter.getCampo());
                } else {
                    sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                            .append(filter.getCampo());
                }
            }
        }

        // LOGGER.debug(sbQuery.toString());

        Query query = getEntityManager().createQuery(sbQuery.toString());

        for (FiltroBusqueda filter : filtros) {
            if (filter.getCampo().equals("abns")) {
                query.setParameter(filter.getCampo(), "%," + filter.getValor().toString().toUpperCase() + ",%");
            } else {
                query.setParameter(filter.getCampo(), filter.getValor());
            }
        }

        Long numResultados = (Long) query.getSingleResult();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Centrales contadas: {}", numResultados);
        }

        return numResultados.intValue();
    }

    @Override
    public List<ReporteCentralVm> findAllCentralesPorLocalidad(FiltroReporteadorCentrales filtro) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = filtro.getListaFiltros();

        StringBuilder sbQuery = new StringBuilder(
                "SELECT rc FROM ReporteCentralVm rc ");
        sbQuery.append("WHERE rc.idEstatus =1 ");
        for (int i = 0; i < filtros.size(); i++) {
            FiltroBusqueda filter = filtros.get(i);
            sbQuery.append(" AND ");
            sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                    .append(filter.getCampo());
        }

        sbQuery.append(" ORDER BY rc.pstNombre ASC ");

        TypedQuery<ReporteCentralVm> query = getEntityManager().createQuery(sbQuery.toString(), ReporteCentralVm.class);

        for (FiltroBusqueda filter : filtros) {
            query.setParameter(filter.getCampo(), filter.getValor());
        }

        if (filtro.isUsarPaginacion()) {
            query.setFirstResult(filtro.getNumeroPagina());
            query.setMaxResults(filtro.getResultadosPagina());
        }

        // LOGGER.debug(query.toString());
        List<ReporteCentralVm> lista = query.getResultList();

        return lista;
    }

    @Override
    public int findAllCentralesPorLocalidadCount(FiltroReporteadorCentrales filtro) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = filtro.getListaFiltros();

        StringBuilder sbQuery = new StringBuilder(
                "SELECT count(rc.id) FROM ReporteCentralVm rc ");
        sbQuery.append("WHERE rc.idEstatus =1 ");
        for (int i = 0; i < filtros.size(); i++) {
            FiltroBusqueda filter = filtros.get(i);
            sbQuery.append(" AND ");
            sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                    .append(filter.getCampo());
        }

        sbQuery.append(" ORDER BY rc.pstNombre ASC ");

        Query query = getEntityManager().createQuery(sbQuery.toString());

        for (FiltroBusqueda filter : filtros) {
            query.setParameter(filter.getCampo(), filter.getValor());
        }

        // LOGGER.debug(query.toString());
        int rowCount = ((Long) query.getSingleResult()).intValue();

        return rowCount;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Central comprobarCentral(Central central) {

        List<Central> resultado = new ArrayList<Central>();

        StringBuilder sbQuery = new StringBuilder("select c.* from CAT_CENTRAL c ");
        sbQuery.append("where c.NOMBRE = ? and c.ID_PST = ? ");
        sbQuery.append("and REPLACE(c.LATITUD,' ','') like ? ");
        sbQuery.append("and REPLACE(c.LONGITUD,' ','') like ? ");

        Query query = getEntityManager().createNativeQuery(sbQuery.toString(), Central.class);
        query.setParameter(1, central.getNombre());
        query.setParameter(2, central.getProveedor().getId());

        // Comparamos las coordenadas hasta el unidades de segundo
        String latitud = central.getLatitud();
        String longitud = central.getLongitud();

        int indexDecimLat = latitud.indexOf(".");
        int indexDecimLong = longitud.indexOf(".");

        if (indexDecimLat > -1) {
            latitud = latitud.substring(0, indexDecimLat);
        }
        if (indexDecimLong > -1) {
            longitud = longitud.substring(0, indexDecimLong);
        }

        query.setParameter(3, latitud + "%");
        query.setParameter(4, longitud + "%");

        resultado = query.getResultList();

        if (!resultado.isEmpty()) {
            return resultado.get(0);
        } else {
            return central;
        }
    }
}
