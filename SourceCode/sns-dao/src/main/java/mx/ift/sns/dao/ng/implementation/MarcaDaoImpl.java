package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IMarcaDao;
import mx.ift.sns.dao.ng.IModeloDao;
import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMarcaModelo;

/**
 * Implementación de los métodos para base de datos de Marcas.
 */
@Named
public class MarcaDaoImpl extends BaseDAO<Marca> implements IMarcaDao {

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(MarcaDaoImpl.class);

    /** Dao estados. */
    // @Inject
    // private IEstatusDao iEstatusDao;
    /** Dao modelos. */
    @Inject
    private IModeloDao iModeloDao;

    @Override
    public Marca getMarcaByNombre(String nombre) {

        String query = "SELECT m FROM Marca m WHERE m.nombre=:nombre";

        Marca marca = null;

        try {
            TypedQuery<Marca> nativeQuery = getEntityManager().createQuery(query, Marca.class);
            nativeQuery.setParameter("nombre", nombre);
            marca = nativeQuery.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
        return marca;
    }

    @Override
    public Marca getMarcaById(BigDecimal idMarca) {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Marca> q = cb.createQuery(Marca.class);
        Root<Marca> c = q.from(Marca.class);
        @SuppressWarnings("unused")
        Join<Marca, Modelo> mod = c.join("modelos", JoinType.LEFT);

        // Where
        ParameterExpression<BigDecimal> pIdMarca = cb.parameter(BigDecimal.class);
        q.where(cb.equal(c.get("id"), pIdMarca)).distinct(true);

        TypedQuery<Marca> typedQuery = getEntityManager().createQuery(q.select(c));
        typedQuery.setParameter(pIdMarca, idMarca);

        typedQuery.setHint("eclipselink.refresh", "True");
        typedQuery.setHint("eclipselink.join-fetch", "ma.modelos");

        Marca marca = typedQuery.getSingleResult();
        return marca;
    }

    @Override
    public List<Marca> findAllMarcas() {
        String query = "SELECT m FROM Marca m ORDER BY m.nombre";

        TypedQuery<Marca> nativeQuery = getEntityManager().createQuery(query, Marca.class);
        List<Marca> list = nativeQuery.getResultList();
        return list;
    }

    @Override
    public List<Marca> findAllMarcasEager(FiltroBusquedaMarcaModelo pFiltros) {

        List<Marca> marcas = new ArrayList<Marca>();
        // Si ya nos traen un modelo en el filtro traemos solo ese modelo y su marca (sin el resto de modelos)
        if (pFiltros.getIdModelo() != null) {
            Modelo modelo = iModeloDao.getModeloById(pFiltros.getIdModelo());
            Marca m = new Marca();
            m = modelo.getMarca();

            // List<Modelo> modelos = m.getModelos();
            List<Modelo> modelos = new ArrayList<Modelo>();
            // modelos.clear();
            modelos.add(modelo);
            m.setModelos(modelos);

            // marcas.clear();
            marcas.add(m);

        } else {
            marcas.clear();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

            CriteriaQuery<Marca> qm = cb.createQuery(Marca.class);
            CriteriaQuery<BigDecimal> qIds = cb.createQuery(BigDecimal.class);
            Root<Marca> ma = qm.from(Marca.class);
            // Sentencia Join
            @SuppressWarnings("unused")
            Join<Marca, Modelo> mod = ma.join("modelos", JoinType.LEFT);

            // WHERE
            Predicate wherePredicate = cb.conjunction();
            Predicate pred = null;
            if (pFiltros.getIdMarca() != null) {
                pred = cb.equal(ma.get("id"), pFiltros.getIdMarca());
                wherePredicate = cb.and(wherePredicate, pred);
            }

            qIds.where(wherePredicate);
            qIds.select(ma.<BigDecimal> get("id")).distinct(true);
            qIds.orderBy(cb.asc(ma.get("id")));
            TypedQuery<BigDecimal> tQueryIds = getEntityManager().createQuery(qIds);

            // Lista de Solicitudes que cumplen los criterios de búsqueda.
            List<BigDecimal> listaIds = tQueryIds.getResultList();

            // traemos los resultados seleccionados
            if (!listaIds.isEmpty()) {
                if (pFiltros.isUsarPaginacion()) {
                    // Seleccionamos los Ids de solicitud que entran dentro de la paginación.
                    int from = pFiltros.getNumeroPagina();
                    int to = (from + pFiltros.getResultadosPagina());
                    BigDecimal[] idsPaginados = Arrays.copyOfRange(
                            listaIds.toArray(new BigDecimal[listaIds.size()]), from, to);
                    listaIds = Arrays.asList(idsPaginados);
                }
            }
            // Recogemos la información de las N solicitudes que cumplen el WHERE
            qm.where(wherePredicate).where(ma.get("id").in(listaIds));
            qm.select(ma).distinct(true);

            qm.orderBy(cb.asc(ma.get("id")));
            TypedQuery<Marca> tQueryData = getEntityManager().createQuery(qm);

            // Hacemos los fecth para mostrar la información de las tablas cruzadas, se haya buscado
            // algo en ellas o no.
            tQueryData.setHint("eclipselink.refresh", "True");
            tQueryData.setHint("eclipselink.join-fetch", "ma.modelos");

            marcas = tQueryData.getResultList();

        }

        if (marcas.isEmpty()) {
            return new ArrayList<Marca>(1);

        } else {
            return marcas;
        }
    }

    @Override
    public int findAllMarcasCount(FiltroBusquedaMarcaModelo pFiltros) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Marca> ma = q.from(Marca.class);
        // Sentencia Join
        Join<Marca, Modelo> mod = ma.join("modelos", JoinType.LEFT);
        // WHERE
        Predicate wherePredicate = cb.conjunction();
        Predicate pred = null;
        if (pFiltros.getIdMarca() != null) {
            pred = cb.equal(ma.get("id"), pFiltros.getIdMarca());
            wherePredicate = cb.and(wherePredicate, pred);
        }
        if (pFiltros.getIdModelo() != null) {
            pred = cb.equal(mod.get("id"), pFiltros.getIdModelo());
            wherePredicate = cb.and(wherePredicate, pred);
        }

        q.where(wherePredicate);

        TypedQuery<Long> typedQuery = getEntityManager().createQuery(q.select(cb.countDistinct(ma)));

        int count = typedQuery.getSingleResult().intValue();
        return count;

    }

}
