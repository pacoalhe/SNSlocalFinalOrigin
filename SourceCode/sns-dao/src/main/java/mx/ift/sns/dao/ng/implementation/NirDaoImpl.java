package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.INirDao;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.series.Nir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Identificadores de Región.
 */
@Named
public class NirDaoImpl extends BaseDAO<Nir> implements INirDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NirDaoImpl.class);

    @Override
    public Nir getNir(BigDecimal id) {
        Nir nir = getEntityManager().find(Nir.class, id);
        if (nir != null && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Nir encontrado con id {}", nir);
        }
        return nir;
    }

    @Override
    public List<Nir> findAllNirByAbn(BigDecimal numAbn) {
        String query = "SELECT n FROM Nir n WHERE n.abn.codigoAbn = :numAbn AND n.estatus.cdg = :estatusActivo";

        TypedQuery<Nir> tQuery = getEntityManager().createQuery(query, Nir.class);
        tQuery.setParameter("numAbn", numAbn);
        tQuery.setParameter("estatusActivo", Estatus.ACTIVO);

        List<Nir> listaNir = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} NIRs activos para el ABN {}", listaNir.size(), numAbn);
        }

        return listaNir;
    }

    @Override
    public Nir getNirByCodigo(int cdgNir) {
        String sql = "SELECT n FROM Nir n WHERE n.codigo=:cdgNir";
        try {
            TypedQuery<Nir> tQuery = getEntityManager().createQuery(sql, Nir.class);
            tQuery.setParameter("cdgNir", cdgNir);

            Nir nir = tQuery.getSingleResult();
            return nir;

        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Nir> findAllNirByParteCodigo(int pCodigoNir) {

        int digitos = String.valueOf(pCodigoNir).length();
        boolean maxNirLength = (String.valueOf(Nir.MAX_NIR).length() == digitos);

        String query;
        if (maxNirLength) {
            // Si los dígitos corresponden con el máximo de NIR buscamos directamente por el código.
            query = "SELECT n FROM Nir n WHERE n.codigo = :cdgNir AND n.estatus.cdg = :estatusActivo ORDER BY n.codigo";
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Buscando NIRs con código {}", pCodigoNir);
            }
        } else {
            StringBuilder sbQuery = new StringBuilder();
            sbQuery.append("SELECT n FROM Nir n ");
            sbQuery.append("WHERE n.codigo LIKE '").append(pCodigoNir).append("%' ");
            sbQuery.append("AND n.estatus.cdg = :estatusActivo ");
            sbQuery.append("ORDER BY n.codigo");
            query = sbQuery.toString();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Buscando NIRs que empiecen con el código {}", pCodigoNir);
            }
        }

        TypedQuery<Nir> tQuery = getEntityManager().createQuery(query, Nir.class);
        if (maxNirLength) {
            tQuery.setParameter("cdgNir", pCodigoNir);
        }
        tQuery.setParameter("estatusActivo", Estatus.ACTIVO);

        List<Nir> lista = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            // LOGGER.debug(tQuery.toString());
            LOGGER.debug("Se han encontrado {} NIRs activos", lista.size());
        }

        return lista;
    }

    @Override
    public int findAllNirByAbnCount(BigDecimal numAbn) {
        String query = "SELECT COUNT(DISTINCT n.id) "
                + "FROM Nir n WHERE n.abn.codigoAbn=:numAbn";
        TypedQuery<Long> nativeQuery = getEntityManager().createQuery(query, Long.class);
        nativeQuery.setParameter("numAbn", numAbn);
        int rowCount = ((Long) nativeQuery.getSingleResult()).intValue();
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sbTrazas = new StringBuffer();
            sbTrazas.append("Se han contado ").append(rowCount).append(" nirs ");
            sbTrazas.append("con código ABN: ").append(numAbn);
        }
        return rowCount;
    }

    @Override
    public List<Nir> findAllNirs() throws Exception {
        String query = "SELECT n FROM Nir n ORDER BY n.codigo";
        TypedQuery<Nir> tQuery = getEntityManager().createQuery(query, Nir.class);

        List<Nir> listaNir = tQuery.getResultList();
        return listaNir;
    }

    @Override
    public boolean existsNir(String nir) {
        String squery = "SELECT COUNT(n) FROM Nir n WHERE n.codigo = :nir";
        TypedQuery<Long> query = getEntityManager().createQuery(squery, Long.class);
        query.setParameter("nir", Integer.parseInt(nir));
        Long resultado = query.getSingleResult();

        return (resultado != null && resultado > 0);
    }

    @Override
    public boolean existsNirWithAbn(String abn, String nir) {
        String squery = "SELECT COUNT(n) FROM Nir n WHERE n.abn.codigoAbn = :abn AND n.codigo = :nir";
        TypedQuery<Long> query = getEntityManager().createQuery(squery, Long.class);
        query.setParameter("nir", Integer.parseInt(nir));
        query.setParameter("abn", new BigDecimal(abn));
        Long resultado = query.getSingleResult();

        return (resultado != null && resultado > 0);
    }

    @Override
    public List<Nir> finAllNirInMunicipio(String municipio, String estado) {
        String squery = "SELECT DISTINCT p.abn.nirs FROM PoblacionAbn p "
                + "WHERE p.inegi.municipio.id.codMunicipio = :municipio AND p.inegi.municipio.id.codEstado = :estado";
        TypedQuery<Nir> consulta = getEntityManager().createQuery(squery, Nir.class);
        consulta.setParameter("municipio", municipio);
        consulta.setParameter("estado", estado);

        List<Nir> resultado = consulta.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} Nirs en el municipio {}{}.", resultado.size(), estado, municipio);
        }
        return resultado;
    }

    @Override
    public List<Nir> findNirsByDigitos(int cdgNir) {

        String query;

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT n FROM Nir n ");
        sbQuery.append("WHERE n.codigo LIKE '").append(cdgNir).append("_' ");
        sbQuery.append("AND n.estatus.cdg = :estatusActivo ");
        sbQuery.append("ORDER BY n.codigo");
        query = sbQuery.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Buscando NIRs que empiecen con el código {}", cdgNir);
        }

        TypedQuery<Nir> tQuery = getEntityManager().createQuery(query, Nir.class);

        tQuery.setParameter("estatusActivo", Estatus.ACTIVO);

        List<Nir> listaNir = tQuery.getResultList();
        return listaNir;
    }

    @Override
    public List<Nir> findAllNirByPoblacion(Poblacion poblacion) {
        String squery = "SELECT DISTINCT p.abn.nirs FROM PoblacionAbn p "
                + "WHERE p.inegi = :poblacion";
        TypedQuery<Nir> consulta = getEntityManager().createQuery(squery, Nir.class);
        consulta.setParameter("poblacion", poblacion);

        List<Nir> resultado = consulta.getResultList();
        return resultado;
    }

    @Override
    public List<Nir> findAllNirByEstado(Estado estado) {
        String squery = "SELECT DISTINCT p.abn.nirs FROM PoblacionAbn p "
                + "WHERE p.inegi.municipio.estado = :estado";
        TypedQuery<Nir> consulta = getEntityManager().createQuery(squery, Nir.class);
        consulta.setParameter("estado", estado);

        List<Nir> resultado = consulta.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} Nirs en el estado {}.", resultado, estado.getNombre());
        }
        return resultado;
    }
}
