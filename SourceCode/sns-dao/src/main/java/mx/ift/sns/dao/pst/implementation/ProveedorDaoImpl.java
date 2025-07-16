package mx.ift.sns.dao.pst.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pst.IProveedorDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaProveedores;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.DetalleProveedor;
import mx.ift.sns.modelo.pst.EstadoConvenio;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Proveedores.
 */
@Named
public class ProveedorDaoImpl extends BaseDAO<Proveedor> implements IProveedorDao {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProveedorDaoImpl.class);

    @Override
    public List<Proveedor> findAllProveedoresActivos() {
        Estatus estatus = new Estatus();
        estatus.setCdg(Estatus.ACTIVO);

        String query = "SELECT p FROM Proveedor p WHERE p.estatus = :estatus ORDER BY p.nombre";

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(query, Proveedor.class);
        tQuery.setParameter("estatus", estatus);

        List<Proveedor> resultado = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Proveedores activos encontrados: {}", resultado.size());
        }
        return resultado;
    }

    @Override
    public List<DetalleProveedor> findAllProveedoresActivosD() {
        Estatus estatus = new Estatus();
        estatus.setCdg(Estatus.ACTIVO);

        String query = "SELECT NEW mx.ift.sns.modelo.pst.DetalleProveedor(p.nombre, p.ido, p.ida, p.abc, p.bcd) "
                + "FROM Proveedor p WHERE p.estatus = :estatus ORDER BY p.nombre";

        TypedQuery<DetalleProveedor> tQuery = getEntityManager().createQuery(query, DetalleProveedor.class);
        tQuery.setParameter("estatus", estatus);

        List<DetalleProveedor> resultado = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Proveedores activos encontrados: {}", resultado.size());
        }
        return resultado;
    }

    @Override
    public List<Proveedor> findAllProveedores() {
        Estatus estatus = new Estatus();
        estatus.setCdg(Estatus.ACTIVO);

        String query = "SELECT p FROM Proveedor p ORDER BY p.nombre";

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(query, Proveedor.class);

        List<Proveedor> resultado = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Proveedores activos encontrados: {}", resultado.size());
        }
        return resultado;
    }

    @Override
    public List<Proveedor> findAllProveedoresByServicio(TipoProveedor pTipoProveedor,
            TipoRed pTipoRed, BigDecimal pIdSolicitante) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Buscando Proveedores de tipo {} con servicio de red {}",
                    pTipoProveedor.getCdg(), pTipoRed.getCdg());
        }

        Estatus estatus = new Estatus();
        estatus.setCdg(Estatus.ACTIVO);

        TipoProveedor tipoAmbos = new TipoProveedor();
        tipoAmbos.setCdg(TipoProveedor.AMBOS);

        TipoRed tipoRedAmbas = new TipoRed();
        tipoRedAmbas.setCdg(TipoRed.AMBAS);

        boolean tipoRedPstAmbas = (pTipoRed.getCdg().equals(TipoRed.AMBAS));
        boolean tipoPstAmbs = (pTipoProveedor.getCdg().equals(TipoProveedor.AMBOS));

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT p FROM Proveedor p WHERE p.estatus = :estatus ");

        // Para PST de tipo Ambos ignoramos el filtro.
        if (!tipoPstAmbs) {
            sbQuery.append("AND (p.tipoProveedor = :tipoProveedor OR p.tipoProveedor = :tipoAmbos) ");
        }

        // Para Tipos de Red de tipo Ambos ignoramos el filtro.
        if (!tipoRedPstAmbas) {
            sbQuery.append("AND (p.tipoRed = :tipoRed OR p.tipoRed = :tipoRedAmbas) ");
        }

        // Si indican código de PST solicitante lo excluimos de los resultados.
        if (pIdSolicitante != null) {
            sbQuery.append("AND p.id <> :idPst ");
        }

        sbQuery.append("ORDER BY p.nombre");

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(sbQuery.toString(), Proveedor.class);
        tQuery.setParameter("estatus", estatus);

        if (!tipoPstAmbs) {
            tQuery.setParameter("tipoProveedor", pTipoProveedor);
            tQuery.setParameter("tipoAmbos", tipoAmbos);
        }

        if (!tipoRedPstAmbas) {
            tQuery.setParameter("tipoRed", pTipoRed);
            tQuery.setParameter("tipoRedAmbas", tipoRedAmbas);
        }

        if (pIdSolicitante != null) {
            tQuery.setParameter("idPst", pIdSolicitante);
        }

        List<Proveedor> resultado = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Proveedores encontrados: {}", resultado.size());
        }

        return resultado;
    }

    @Override
    public List<Proveedor> findAllProveedoresByServicioArrendar(TipoProveedor pTipoProveedor,
            TipoRed pTipoRed, BigDecimal idPst) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Buscando Proveedores de tipo {} con servicio de red {}",
                    pTipoProveedor.getCdg(), pTipoRed.getCdg());
        }

        Estatus estatus = new Estatus();
        estatus.setCdg(Estatus.ACTIVO);

        TipoProveedor tipoAmbos = new TipoProveedor();
        tipoAmbos.setCdg(TipoProveedor.AMBOS);

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT p FROM Proveedor p WHERE p.estatus = :estatus ");

        // Si es Comercializadora buscamos Comercializadoras o Ambos. Si no, se buscan todos.
        if (pTipoProveedor.getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {
            sbQuery.append("AND (p.tipoProveedor = :tipoProveedor OR p.tipoProveedor = :tipoAmbos) ");
        }

        // Para el timpo de Red "Ambas" no hacemos filtro
        if (!pTipoRed.getCdg().equals(TipoRed.AMBAS)) {
            sbQuery.append("AND p.tipoRed = :tipoRed ");
        }

        sbQuery.append("AND p.id <> :id ");
        sbQuery.append("ORDER BY p.nombre");

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(sbQuery.toString(), Proveedor.class);
        tQuery.setParameter("estatus", estatus);

        // Filtro de Comercializadoras
        if (pTipoProveedor.getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {
            tQuery.setParameter("tipoProveedor", pTipoProveedor);
            tQuery.setParameter("tipoAmbos", tipoAmbos);
        }

        // Filtro de tipos de Red
        if (!pTipoRed.getCdg().equals(TipoRed.AMBAS)) {
            tQuery.setParameter("tipoRed", pTipoRed);
        }

        tQuery.setParameter("id", idPst);
        List<Proveedor> resultado = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Proveedores encontrados: {}", resultado.size());
        }

        return resultado;
    }

    @Override
    public Proveedor getProveedorByNombre(String pNombre) {
        Estatus estatus = new Estatus();
        estatus.setCdg(Estatus.ACTIVO);

        String query = "SELECT p FROM Proveedor p WHERE p.nombre = :nombre"
                + " AND p.estatus = :estatus ORDER BY p.cdgPst";

        Proveedor proveedor = null;
        try {
            TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(query, Proveedor.class);
            tQuery.setParameter("nombre", pNombre);
            tQuery.setParameter("estatus", estatus);
            proveedor = tQuery.getSingleResult();
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Proveedor activo con nombre '{}' no encontrado.", pNombre);
            }
            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Proveedor activo encontrado. Id: {}", proveedor.getId());
        }
        return proveedor;
    }

    @Override
    public Proveedor getProveedorById(BigDecimal pIdProveedor) {
        String query = "SELECT p FROM Proveedor p WHERE p.id = :id";

        TypedQuery<Proveedor> consulta = getEntityManager().createQuery(query, Proveedor.class);
        consulta.setParameter("id", pIdProveedor);

        Proveedor pst = consulta.getSingleResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Proveedor con id {} encontrado: {}", pIdProveedor, pst.getNombre());
        }

        return pst;
    }

    @Override
    public List<Proveedor> findAllCesionarios(Proveedor pCedente) {

        // Select p From Proveedor
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Proveedor> cQuery = criteriaBuilder.createQuery(Proveedor.class);
        Root<Proveedor> p = cQuery.from(Proveedor.class);

        // Buscamos también en los convenios del Cedente si existen
        Join<Proveedor, ProveedorConvenio> joinConvenios = p.join("conveniosComercializador", JoinType.LEFT);

        // Parámetros de búsqueda
        ParameterExpression<TipoRed> tipoRedCedente = criteriaBuilder.parameter(TipoRed.class);
        ParameterExpression<Estatus> estatus = criteriaBuilder.parameter(Estatus.class);
        ParameterExpression<TipoRed> tipoRedAmbas = criteriaBuilder.parameter(TipoRed.class);
        ParameterExpression<TipoProveedor> pstConcesionario = criteriaBuilder.parameter(TipoProveedor.class);
        ParameterExpression<TipoProveedor> pstComercializadora = criteriaBuilder.parameter(TipoProveedor.class);
        ParameterExpression<TipoProveedor> pstAmbos = criteriaBuilder.parameter(TipoProveedor.class);
        ParameterExpression<BigDecimal> idPstCedente = criteriaBuilder.parameter(BigDecimal.class);
        ParameterExpression<EstadoConvenio> convenioVigente = criteriaBuilder.parameter(EstadoConvenio.class);

        // Tipo de Red Ambas
        TipoRed redAmbas = new TipoRed();
        redAmbas.setCdg(TipoRed.AMBAS);

        // Tipo de Proveedor Concesionario
        TipoProveedor tipoPstConcesionario = new TipoProveedor();
        tipoPstConcesionario.setCdg(TipoProveedor.CONCESIONARIO);

        // Tipo de Proveedor Comercializadora
        TipoProveedor tipoPstComercializadora = new TipoProveedor();
        tipoPstComercializadora.setCdg(TipoProveedor.COMERCIALIZADORA);

        // Tipo de Proveedor Ambos
        TipoProveedor tipoPstAmbos = new TipoProveedor();
        tipoPstAmbos.setCdg(TipoProveedor.AMBOS);

        // Estado Proveedor Activo
        Estatus estatusActivo = new Estatus();
        estatusActivo.setCdg(Estatus.ACTIVO);

        // Estado Convenio Vigente
        EstadoConvenio statusVigente = new EstadoConvenio();
        statusVigente.setCdg(EstadoConvenio.VIGENTE);

        TypedQuery<Proveedor> typedQuery = null;

        if (pCedente.getTipoRed().getCdg().equals(TipoRed.AMBAS)) {
            // Cuando el cedente tiene ambos tipos de red los cesionarios pueden ser concesionario o
            // cualquier comercializador con convenio con concesionario

            cQuery.where(
                    criteriaBuilder.equal(p.get("estatus"), estatus),
                    criteriaBuilder.and(
                            criteriaBuilder.or(
                                    // Comercializador con cualquier tipo de convenio vigente
                                    criteriaBuilder.and(
                                            criteriaBuilder.equal(p.get("tipoProveedor"), pstComercializadora),
                                            criteriaBuilder.equal(joinConvenios.get("estatus"), convenioVigente)),

                                    // Cualquier Concesionario
                                    criteriaBuilder.or(
                                            criteriaBuilder.equal(p.get("tipoProveedor"), pstConcesionario),
                                            criteriaBuilder.equal(p.get("tipoProveedor"), pstAmbos)))),

                    // Ignoramos el PST Cedente de la búsqueda de Cesionarios
                    criteriaBuilder.and(criteriaBuilder.notEqual(p.get("id"), idPstCedente))

                    // Ordenamos por nombre
                    ).distinct(true).orderBy(criteriaBuilder.asc(p.get("nombre")));

            typedQuery = getEntityManager().createQuery(cQuery.select(p));
            typedQuery.setParameter(pstConcesionario, tipoPstConcesionario);
            typedQuery.setParameter(pstComercializadora, tipoPstComercializadora);
            typedQuery.setParameter(pstAmbos, tipoPstAmbos);
            typedQuery.setParameter(estatus, estatusActivo);
            typedQuery.setParameter(idPstCedente, pCedente.getId());
            typedQuery.setParameter(convenioVigente, statusVigente);

        } else {
            // Filtramos por los cesionarios con el mismo tipo de red del cedente o ambas

            // La misma query sirve tanto si el cedente es Concesionario como Comcercializadora
            cQuery.where(
                    criteriaBuilder.equal(p.get("estatus"), estatus),
                    criteriaBuilder.and(
                            criteriaBuilder.or(
                                    // (Convenio activo con Concesionario con el mismo tipo de red)
                                    // OR
                                    // (Convenio activo con Concesionario con tipo de red Ambos)
                                    criteriaBuilder.or(
                                            criteriaBuilder.and(
                                                    criteriaBuilder.equal(joinConvenios.get("tipoRed"),
                                                            tipoRedCedente),
                                                    criteriaBuilder.equal(joinConvenios.get("estatus"),
                                                            convenioVigente)),
                                            criteriaBuilder.and(
                                                    criteriaBuilder.equal(joinConvenios.get("tipoRed"),
                                                            tipoRedAmbas),
                                                    criteriaBuilder.equal(joinConvenios.get("estatus"),
                                                            convenioVigente))
                                            ),

                                    // (Cesionario Concesionario o Ambos)
                                    // AND
                                    // (Cesionario Mismo tipo de Red o Ambos)
                                    criteriaBuilder.and(
                                            criteriaBuilder.or(
                                                    criteriaBuilder.equal(p.get("tipoProveedor"), pstAmbos),
                                                    criteriaBuilder.equal(p.get("tipoProveedor"), pstConcesionario)),
                                            criteriaBuilder.or(
                                                    criteriaBuilder.equal(p.get("tipoRed"), tipoRedCedente),
                                                    criteriaBuilder.equal(p.get("tipoRed"), tipoRedAmbas))))),

                    // Ignoramos el PST Cedente de la búsqueda de Cesionarios
                    criteriaBuilder.and(criteriaBuilder.notEqual(p.get("id"), idPstCedente))

                    // Ordenamos por nombre
                    ).distinct(true).orderBy(criteriaBuilder.asc(p.get("nombre")));

            typedQuery = getEntityManager().createQuery(cQuery.select(p));
            typedQuery.setParameter(tipoRedCedente, pCedente.getTipoRed());
            typedQuery.setParameter(tipoRedAmbas, redAmbas);
            typedQuery.setParameter(pstConcesionario, tipoPstConcesionario);
            typedQuery.setParameter(pstAmbos, tipoPstAmbos);
            typedQuery.setParameter(estatus, estatusActivo);
            typedQuery.setParameter(idPstCedente, pCedente.getId());
            typedQuery.setParameter(convenioVigente, statusVigente);
        }

        List<Proveedor> listaCesionarios = typedQuery.getResultList();

        // if (LOGGER.isDebugEnabled()) {
        // // Específico para EclipseLink. Hay que habilitar la dependencia con la libería en el POM
        // String sqlString = typedQuery.unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getDatabaseQuery()
        // .getSQLString();
        // LOGGER.debug("Criteria SqlString: " + sqlString);
        // }

        return listaCesionarios;
    }

    @Override
    public List<Proveedor> findAllConcesionarios() {

        Estatus estatus = new Estatus();
        estatus.setCdg(Estatus.ACTIVO);

        String query = "SELECT p FROM Proveedor p WHERE p.estatus = :estatus AND "
                + "(p.tipoProveedor.cdg = :ambos OR p.tipoProveedor.cdg = :concesionario) ORDER BY p.nombre";

        Query consulta = getEntityManager().createQuery(query);
        consulta.setParameter("estatus", estatus);

        consulta.setParameter("ambos", TipoProveedor.AMBOS);
        consulta.setParameter("concesionario", TipoProveedor.CONCESIONARIO);

        @SuppressWarnings("unchecked")
        List<Proveedor> resultado = consulta.getResultList();
        return resultado;
    }

    @Override
    public List<Proveedor> findAllConcesionariosFromConvenios(Proveedor pComercializador, TipoRed pTipoRed) {
        // Proveedores Activos
        Estatus estatus = new Estatus();
        estatus.setCdg(Estatus.ACTIVO);

        // Convenios Activos
        EstadoConvenio estadoConvenio = new EstadoConvenio();
        estadoConvenio.setCdg(EstadoConvenio.VIGENTE);

        // Si el tipo de red solicitado es "Ambas" se omite el filtro de red.
        boolean tipoRedAmbas = (pTipoRed.getCdg().equals(TipoRed.AMBAS));

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT DISTINCT(c.proveedorConcesionario) FROM Proveedor p INNER JOIN ProveedorConvenio c");
        sbQuery.append(" ON (p.id = c.proveedorConvenio.id) WHERE p.estatus = :estatus");
        sbQuery.append(" AND c.proveedorConvenio = :pstComercializador AND c.estatus = :estadoConvenio");
        if (!tipoRedAmbas) {
            sbQuery.append(" AND (c.tipoRed = :tipoRedAmbas OR c.tipoRed = :tipoRed)");
        }
        sbQuery.append(" ORDER BY p.nombre");

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(sbQuery.toString(), Proveedor.class);
        tQuery.setParameter("estatus", estatus);
        tQuery.setParameter("estadoConvenio", estadoConvenio);
        tQuery.setParameter("pstComercializador", pComercializador);

        if (!tipoRedAmbas) {
            TipoRed tipoAmbos = new TipoRed();
            tipoAmbos.setCdg(TipoRed.AMBAS);

            tQuery.setParameter("tipoRedAmbas", tipoAmbos);
            tQuery.setParameter("tipoRed", pTipoRed);
        }

        List<Proveedor> lista = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontrados {} convenios con tipo de red {} para el PST {}",
                    lista.size(), pTipoRed.getCdg(), pComercializador.getId());
        }

        return lista;
    }

    @Override
    public List<Proveedor> findAllConcesionariosByAbn(Abn abn) {

        BigDecimal codigoAbn = abn.getCodigoAbn();

        String query = "SELECT DISTINCT(c.proveedor) FROM Central c"
                + " INNER JOIN PoblacionAbn p ON (p.inegi.inegi = c.poblacion.inegi)"
                + " WHERE p.abn.codigoAbn = :codigoAbn"
                + " AND (c.proveedor.tipoProveedor.cdg = :ambos OR c.proveedor.tipoProveedor.cdg = :concesionario)"
                + " ORDER BY c.proveedor.nombre";

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(query, Proveedor.class);

        tQuery.setParameter("codigoAbn", codigoAbn);
        tQuery.setParameter("ambos", TipoProveedor.AMBOS);
        tQuery.setParameter("concesionario", TipoProveedor.CONCESIONARIO);

        return tQuery.getResultList();

    }

    @Override
    public List<Proveedor> findAllConcesionariosByPoblacion(Poblacion poblacion) {

        String inegi = poblacion.getInegi();

        String query = "SELECT DISTINCT(c.proveedor) FROM Central c"
                + " INNER JOIN PoblacionAbn p ON (p.inegi.inegi = c.poblacion.inegi)"
                + " WHERE p.inegi.inegi = :inegi"
                + " AND (c.proveedor.tipoProveedor.cdg = :ambos OR c.proveedor.tipoProveedor.cdg = :concesionario)"
                + " ORDER BY c.proveedor.nombre";

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(query, Proveedor.class);

        tQuery.setParameter("inegi", inegi);
        tQuery.setParameter("ambos", TipoProveedor.AMBOS);
        tQuery.setParameter("concesionario", TipoProveedor.CONCESIONARIO);

        return tQuery.getResultList();

    }

    @Override
    public List<Proveedor> findAllConcesionariosByEstado(Estado estado) {

        String codEstado = estado.getCodEstado();
        String query = "SELECT DISTINCT(c.proveedor) FROM Central c"
                + " INNER JOIN Poblacion p ON (p.inegi = c.poblacion.inegi)"
                + " AND (c.proveedor.tipoProveedor.cdg = :ambos OR c.proveedor.tipoProveedor.cdg = :concesionario)"
                + "AND p.municipio.estado.codEstado = :estado"
                + " ORDER BY c.proveedor.nombre";

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(query, Proveedor.class);
        tQuery.setParameter("estado", codEstado);
        tQuery.setParameter("ambos", TipoProveedor.AMBOS);
        tQuery.setParameter("concesionario", TipoProveedor.CONCESIONARIO);

        return tQuery.getResultList();

    }

    @Override
    public List<Proveedor> findAllProveedores(FiltroBusquedaProveedores pFiltros)
            throws Exception {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltros.getFiltrosProveedor();

        if (!pFiltros.isPreCarga() && filtros.isEmpty()) {
            return this.findAllProveedoresActivos();
        } else {

            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Proveedor> cQuery = cb.createQuery(Proveedor.class);
            Root<Proveedor> pst = cQuery.from(Proveedor.class);

            Predicate wherePredicate = cb.conjunction();
            for (FiltroBusqueda filtro : filtros) {
                Predicate pred = null;
                pred = this.getPredicateFromFiltro(filtro, pst, cb);

                wherePredicate = cb.and(wherePredicate, pred);
            }
            cQuery.where(wherePredicate).distinct(true).orderBy(cb.asc(pst.get("nombre")));

            TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(cQuery.select(pst));

            // Paginación
            if (pFiltros.isUsarPaginacion()) {
                tQuery.setFirstResult(pFiltros.getNumeroPagina());
                tQuery.setMaxResults(pFiltros.getResultadosPagina());
            }

            List<Proveedor> result = tQuery.getResultList();
            return result;
        }
    }

    @Override
    public int findAllProveedoresCount(FiltroBusquedaProveedores pFiltros)
            throws Exception {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltros.getFiltrosProveedor();

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<Proveedor> pst = cQuery.from(Proveedor.class);

        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = null;
            pred = this.getPredicateFromFiltro(filtro, pst, cb);

            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate).distinct(true).orderBy(cb.asc(pst.get("id")));

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery.select(cb.count(pst)));
        int rowCount = tQuery.getSingleResult().intValue();
        return rowCount;
    }

    /**
     * Traduce un filtro a un Predicado de CriteriaQuery.
     * @param pFiltro Información del Filtro
     * @param pFrom Tabla a la que se aplica el filtro
     * @param pCriteriaBuilder CriteriaBuilder en uso
     * @return Predicate
     */
    @SuppressWarnings("rawtypes")
    private Predicate getPredicateFromFiltro(FiltroBusqueda pFiltro, From pFrom, CriteriaBuilder pCriteriaBuilder) {
        return pCriteriaBuilder.equal(pFrom.get(pFiltro.getCampo()), pFiltro.getValor());
    }

    @Override
    public String existeIdo(Proveedor proveedor) {
        String query = "";
        if (proveedor.getId() != null) {
            query = "SELECT p.estatus.cdg FROM Proveedor p WHERE p.id <> :idProveedor "
                    + "AND p.ido = :ido ORDER BY p.estatus.cdg DESC";
        } else {
            query = "SELECT p.estatus.cdg FROM Proveedor p WHERE p.ido = :ido ORDER BY p.estatus.cdg DESC";
        }

        TypedQuery<String> tQuery = getEntityManager().createQuery(query, String.class);
        if (proveedor.getId() != null) {
            tQuery.setParameter("idProveedor", proveedor.getId());
        }
        tQuery.setParameter("ido", proveedor.getIdo());

        List<String> resultado = tQuery.getResultList();
        if (resultado == null || resultado.size() == 0) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    @Override
    public String existeIda(Proveedor proveedor) {
        String query = "";

        if (proveedor.getId() != null) {
            query = "SELECT p.estatus.cdg FROM Proveedor p WHERE p.id <> :idProveedor "
                    + "AND p.ida = :ida ORDER BY p.estatus.cdg DESC";
        } else {
            query = "SELECT p.estatus.cdg FROM Proveedor p WHERE p.ida = :ida ORDER BY p.estatus.cdg DESC";
        }

        TypedQuery<String> tQuery = getEntityManager().createQuery(query, String.class);
        if (proveedor.getId() != null) {
            tQuery.setParameter("idProveedor", proveedor.getId());
        }
        tQuery.setParameter("ida", proveedor.getIda());

        List<String> resultado = tQuery.getResultList();
        if (resultado == null || resultado.size() == 0) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    @Override
    public String existeAbc(Proveedor proveedor) {
        String query = "";

        if (proveedor.getId() != null) {
            query = "SELECT p.estatus.cdg FROM Proveedor p WHERE p.id <> :idProveedor "
                    + "AND p.abc = :abc ORDER BY p.estatus.cdg DESC";
        } else {
            query = "SELECT p.estatus.cdg FROM Proveedor p WHERE p.abc = :abc ORDER BY p.estatus.cdg DESC";
        }

        TypedQuery<String> tQuery = getEntityManager().createQuery(query, String.class);
        if (proveedor.getId() != null) {
            tQuery.setParameter("idProveedor", proveedor.getId());
        }
        tQuery.setParameter("abc", proveedor.getAbc());

        List<String> resultado = tQuery.getResultList();
        if (resultado == null || resultado.size() == 0) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    @Override
    public String existeBcd(Proveedor proveedor) {
        String query = "";

        if (proveedor.getId() != null) {
            query = "SELECT p.estatus.cdg FROM Proveedor p WHERE p.id <> :idProveedor "
                    + "AND p.bcd = :bcd ORDER BY p.estatus.cdg DESC";
        } else {
            query = "SELECT p.estatus.cdg FROM Proveedor p WHERE p.bcd = :bcd ORDER BY p.estatus.cdg DESC";
        }

        TypedQuery<String> tQuery = getEntityManager().createQuery(query, String.class);
        if (proveedor.getId() != null) {
            tQuery.setParameter("idProveedor", proveedor.getId());
        }
        tQuery.setParameter("bcd", proveedor.getBcd());

        List<String> resultado = tQuery.getResultList();
        if (resultado == null || resultado.size() == 0) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    @Override
    public boolean existeNombreProveedor(Proveedor proveedor) {
        String query = "";

        if (proveedor.getId() != null) {
            query = "SELECT count(1) FROM Proveedor p WHERE p.id <> :idProveedor "
                    + "AND upper(p.nombre) = upper(:nombre)";
        } else {
            query = "SELECT count(1) FROM Proveedor p WHERE upper(p.nombre) = upper(:nombre)";
        }

        TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);
        if (proveedor.getId() != null) {
            tQuery.setParameter("idProveedor", proveedor.getId());
        }
        tQuery.setParameter("nombre", proveedor.getNombre());

        Long resultado = tQuery.getSingleResult();
        if (resultado == null || resultado.longValue() == 0) {
            return Proveedor.N_EXISTE;
        } else {
            return Proveedor.EXISTE;
        }
    }

    @Override
    public boolean existeNombreCortoProveedor(Proveedor proveedor) {
        String query = "";

        if (proveedor.getId() != null) {
            query = "SELECT count(1) FROM Proveedor p WHERE p.id <> :idProveedor "
                    + "AND upper(p.nombreCorto) = upper(:nombreCorto)";
        } else {
            query = "SELECT count(1) FROM Proveedor p WHERE upper(p.nombreCorto) = upper(:nombreCorto)";
        }

        TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);
        if (proveedor.getId() != null) {
            tQuery.setParameter("idProveedor", proveedor.getId());
        }
        tQuery.setParameter("nombreCorto", proveedor.getNombreCorto());

        Long resultado = tQuery.getSingleResult();
        if (resultado == null || resultado.longValue() == 0) {
            return Proveedor.N_EXISTE;
        } else {
            return Proveedor.EXISTE;
        }
    }

    @Override
    public boolean usuarioUtilizado(Proveedor proveedor) {
        String query = "";

        if (proveedor.getId() != null) {
            query = "SELECT count(1) FROM Proveedor p WHERE p.id <> :idProveedor "
                    + "AND upper(p.usuario.id) = upper(:usuario)";
        } else {
            query = "SELECT count(1) FROM Proveedor p WHERE upper(p.usuario.id) = upper(:usuario)";
        } 
        
        TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);
        if (proveedor.getId() != null) {
            tQuery.setParameter("idProveedor", proveedor.getId());
        }
        tQuery.setParameter("usuario", proveedor.getUsuario().getId());

        Long resultado = tQuery.getSingleResult();
        if (resultado == null || resultado.longValue() == 0) {
            return Proveedor.N_EXISTE;
        } else {
            return Proveedor.EXISTE;
        }
    }

    @Override
    public List<Proveedor> findAllProveedoresByTRed(TipoRed tipoRed) {
        String query = "";

        Estatus estatus = new Estatus();
        estatus.setCdg(Estatus.ACTIVO);

        if (TipoRed.AMBAS.equals(tipoRed.getCdg())) {
            query = "SELECT p FROM Proveedor p WHERE p.estatus = :estatus "
                    + "AND p.tipoRed = :tipoRed ORDER BY p.nombre";
        } else {
            query = "SELECT p FROM Proveedor p WHERE p.estatus = :estatus "
                    + "AND (p.tipoRed = :tipoRed OR p.tipoRed = :tipoRedAmbas) ORDER BY p.nombre";
        }

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(query, Proveedor.class);
        tQuery.setParameter("estatus", estatus);
        tQuery.setParameter("tipoRed", tipoRed);

        if (!TipoRed.AMBAS.equals(tipoRed.getCdg())) {
            TipoRed tipoRedAmbas = new TipoRed();
            tipoRedAmbas.setCdg(TipoRed.AMBAS);
            tQuery.setParameter("tipoRedAmbas", tipoRedAmbas);
        }

        List<Proveedor> resultado = tQuery.getResultList();

        return resultado;
    }

    @Override
    public List<Proveedor> findAllArrendatariosNng() {

        StringBuffer sbquery = new StringBuffer("SELECT p FROM Proveedor p ");
        sbquery.append("WHERE ((p.tipoProveedor = :comercializadora AND p.ida IS NOT NULL)");
        sbquery.append("OR (p.tipoProveedor = :concesionario AND p.abc IS NOT NULL)");
        sbquery.append("OR (p.tipoProveedor = :ambos AND p.abc IS NOT NULL))");
        sbquery.append("AND p.estatus = :activo ORDER BY p.nombre");

        TypedQuery<Proveedor> tquery = getEntityManager().createQuery(sbquery.toString(), Proveedor.class);

        TipoProveedor comercializadora = new TipoProveedor();
        comercializadora.setCdg(TipoProveedor.COMERCIALIZADORA);

        TipoProveedor concesionario = new TipoProveedor();
        concesionario.setCdg(TipoProveedor.CONCESIONARIO);

        TipoProveedor ambos = new TipoProveedor();
        ambos.setCdg(TipoProveedor.AMBOS);

        Estatus activo = new Estatus();
        activo.setCdg(Estatus.ACTIVO);

        tquery.setParameter("comercializadora", comercializadora);
        tquery.setParameter("concesionario", concesionario);
        tquery.setParameter("ambos", ambos);
        tquery.setParameter("activo", activo);

        List<Proveedor> resultado = tquery.getResultList();
        LOGGER.debug("Enontradoso {} proveedores", resultado.size());

        return resultado;
    }

    @Override
    public List<Proveedor> findAllConcesionariosByMunicipio(Municipio municipio) {

        MunicipioPK idMunicipio = municipio.getId();
        String query = "SELECT DISTINCT(c.proveedor) FROM Central c"
                + " INNER JOIN Poblacion p ON (p.inegi = c.poblacion.inegi)"
                + " AND (c.proveedor.tipoProveedor.cdg = :ambos OR c.proveedor.tipoProveedor.cdg = :concesionario)"
                + "AND p.municipio.id = :municipio"
                + " ORDER BY c.proveedor.nombre";

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(query, Proveedor.class);
        tQuery.setParameter("municipio", idMunicipio);
        tQuery.setParameter("ambos", TipoProveedor.AMBOS);
        tQuery.setParameter("concesionario", TipoProveedor.CONCESIONARIO);

        return tQuery.getResultList();

    }

    @Override
    public List<Proveedor> findAllArrendatariosByAbc(Proveedor proveedor) {

        // NativeQuery
        /*
         * SELECT DISTINCT(PST.ID_PST) FROM CAT_PST PST LEFT JOIN PST_CONVENIO CONV ON (PST.ID_PST =
         * CONV.ID_PST_CONVENIO) WHERE ((PST.ABC IS NOT NULL) OR (PST.ABC IS NULL AND PST.ID_TIPO_PST = 'Z' AND (SELECT
         * OPST.ABC FROM CAT_PST OPST WHERE OPST.ID_PST = CONV.ID_PST_CONCESIONARIO AND CONV.ID_ESTATUS_CONVENIO = 'V')
         * IS NOT NULL)) AND (PST.ID_PST <> 2000056) AND (PST.ID_ESTATUS = '1');
         */

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT DISTINCT(p) FROM Proveedor p");
        sbQuery.append(" LEFT JOIN ProveedorConvenio pc ON (p = pc.proveedorConvenio)");
        sbQuery.append(" WHERE ((p.abc IS NOT NULL) OR (p.abc IS NULL AND p.tipoProveedor.cdg = :pstComer AND ");
        sbQuery.append(" (SELECT op.abc FROM Proveedor op WHERE op.id = pc.proveedorConcesionario.id");
        sbQuery.append(" AND pc.estatus.cdg = :estatusConvenio) IS NOT NULL))");
        sbQuery.append(" AND (p.id <> :idSolicitante) AND (p.estatus.cdg = :estatus)");
        sbQuery.append(" ORDER BY p.nombre");

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(sbQuery.toString(), Proveedor.class);
        tQuery.setParameter("pstComer", TipoProveedor.COMERCIALIZADORA);
        tQuery.setParameter("idSolicitante", proveedor.getId());
        tQuery.setParameter("estatus", Estatus.ACTIVO);
        tQuery.setParameter("estatusConvenio", EstadoConvenio.VIGENTE);

        List<Proveedor> lista = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} posibles arrendadores para el proveedor {}", lista.size(),
                    proveedor.getNombreCorto());
        }

        return lista;
    }

    @Override
    public List<Proveedor> findAllConcesionariosFromConveniosByAbc(Proveedor pComercializador) {

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT DISTINCT(pc.proveedorConcesionario) FROM ProveedorConvenio pc");
        sbQuery.append(" WHERE pc.proveedorConvenio.id = :idSolicitante AND pc.estatus.cdg = :estatusConvenio");
        sbQuery.append(" AND pc.proveedorConcesionario.estatus.cdg = :estatus");
        sbQuery.append(" AND pc.proveedorConcesionario.abc IS NOT NULL");
        sbQuery.append(" ORDER BY pc.proveedorConcesionario.nombre");

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(sbQuery.toString(), Proveedor.class);
        tQuery.setParameter("estatus", Estatus.ACTIVO);
        tQuery.setParameter("estatusConvenio", EstadoConvenio.VIGENTE);
        tQuery.setParameter("idSolicitante", pComercializador.getId());

        List<Proveedor> lista = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} posibles concesionarios por convenio para el proveedor {}", lista.size(),
                    pComercializador.getNombreCorto());
        }

        return lista;
    }

    @Override
    public boolean existeIdaAbc(String dato) {
        try {
            String query = "SELECT count(1) FROM Proveedor p WHERE p.abc = :dato OR p.ida = :dato";

            TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);
            tQuery.setParameter("dato", new BigDecimal(dato));

            Long resultado = tQuery.getSingleResult();

            return (resultado != null && resultado > 0);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    /**
     * FJAH 17042025
     * Refactorizar metodo
     * @param "IdaProveedor" BigDecimal
     * @return
     */
    @Override
    public List<Proveedor> getProveedorByIDA(BigDecimal idaProveedor) {

        // Obtener la base del query
        StringBuffer sbquery = getNativeQueryBase();
        // Agregar la condición WHERE
        //sbquery.append("WHERE PST.IDA = ?1");
        sbquery.append("WHERE PST.IDA = ?1 AND PST.ID_ESTATUS = 1");

        // Convertir a String para la consulta
        String nativeQuery = sbquery.toString();

        //System.out.println("===> Valor del parametro recibido, Buscando PST por IDA:" + idaProveedor);
        //System.out.println("===> Query a ejecutarse Preparado ...." + nativeQuery);

        // Crear la consulta nativa
        Query query = getEntityManager().createNativeQuery(nativeQuery, Proveedor.class);
        query.setParameter(1, idaProveedor);

        //System.out.println("===> Query a ejecutarse Final " + query);

        // Ejecutar la consulta
        @SuppressWarnings("unchecked")
        List<Proveedor> pstListIda = query.getResultList();

        //System.out.println("===> Proveedores encontrados:" + pstListIda.size());

        if (pstListIda.size() > 1) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Varios proveedores encontrados");
            }

            List<Proveedor> pstListIdaPrincipal = new ArrayList<>();

            for (Proveedor pst : pstListIda) {
                if (pst.getPrincipal() == 1) {
                    pstListIdaPrincipal.add(pst);
                }
            }

            // Comprobamos sí la consulta devuelve más de un proveedor. Si es así, filtramos
            // por proveedor principal.
            // En caso contrario se devolvemos la lista vacía o con un proveedor único.
            if (!pstListIdaPrincipal.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal con IDA {} encontrado: {}", idaProveedor,
                            pstListIdaPrincipal.get(0).getNombre());
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal no especificado para IDA {}", idaProveedor);
                }
            }
            return pstListIdaPrincipal;
        } else {
            if (pstListIda.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal no encontrado para IDA: {}", idaProveedor);
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal con IDA {} encontrado: {}", idaProveedor,
                            pstListIda.get(0).getNombre());
                }
            }
        }
        return pstListIda;
    }

    /**
     * FJAH 17042025
     * Refactorizar metodo
     * @param "IdoProveedor" BigDecimal
     * @return
     */
    @Override
    public List<Proveedor> getProveedorByIDO(BigDecimal idoProveedor) {

        // Obtener la base del query
        StringBuffer sbquery = getNativeQueryBase();
        // Agregar la condición WHERE
        //sbquery.append("WHERE PST.IDO = ?1");
        sbquery.append("WHERE PST.IDO = ?1 AND PST.ID_ESTATUS = 1");

        // Convertir a String para la consulta
        String nativeQuery = sbquery.toString();

        //System.out.println("===> Valor del parametro recibido, Buscando PST por IDO:" + idoProveedor);
        //System.out.println("===> Query a ejecutarse Preparado ...." + nativeQuery);

        // Crear la consulta nativa
        Query query = getEntityManager().createNativeQuery(nativeQuery, Proveedor.class);
        query.setParameter(1, idoProveedor);

        //System.out.println("===> Query a ejecutarse Final " + query);

        // Ejecutar la consulta
        @SuppressWarnings("unchecked")
        List<Proveedor> pstListIdo = query.getResultList();

        //System.out.println("===> Proveedores encontrados:" + pstListIdo.size());

        // Log de la lista recibida
        //LOGGER.info("Lista IDO :::::::::::::::::::::::::::: {}", pstListIdo.size());

        // Si hay más de un proveedor, seleccionamos el activo
        if (pstListIdo.size() > 1) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Varios proveedores encontrados");
            }

            List<Proveedor> pstListIdoPrincipal = new ArrayList<>();

            for (Proveedor pst : pstListIdo) {
                if (pst.getPrincipal() == 1) {
                    pstListIdoPrincipal.add(pst);
                }
            }

            if (!pstListIdoPrincipal.isEmpty()) {
                LOGGER.debug("Proveedor principal con IDO {} encontrado: {}", idoProveedor, pstListIdoPrincipal.get(0).getNombre());
                return pstListIdoPrincipal; //FJAH 01052025
            } else {
                LOGGER.debug("Proveedor principal no especificado para IDO {}", idoProveedor);
                return Collections.singletonList(pstListIdo.get(0)); //FJAH 01052025
            }
            //return pstListIdoPrincipal; //FJAH 01052025
        } else {
            if (pstListIdo.isEmpty()) {
                LOGGER.debug("Proveedor principal no encontrado para IDO: {}", idoProveedor);
            } else {
                LOGGER.debug("Proveedor principal con IDO {} encontrado: {}", idoProveedor, pstListIdo.get(0).getNombre());
            }
        }

        // Retorna la lista final de proveedores
        return pstListIdo;
    }

    /**
     * FJAH 28.05.2025
     * Refactorizar metodo
     * @param "IdoProveedor" BigDecimal
     * @return
     */
    @Override
    public List<Proveedor> getProveedorByABC(BigDecimal abcProveedor) {
        // Usamos el mismo patrón que los otros métodos, pero sobre la columna BCD.
        // Obtener la base del query
        StringBuffer sbquery = getNativeQueryBase();
        // Agregar la condición WHERE
        //sbquery.append("WHERE PST.BCD = ?1");
        sbquery.append("WHERE PST.BCD = ?1 AND PST.ID_ESTATUS = 1");

        // Convertir a String para la consulta
        String nativeQuery = sbquery.toString();

        //System.out.println("===> Valor del parametro recibido, Buscando PST por IDO:" + abcProveedor);
        //System.out.println("===> Query a ejecutarse Preparado ...." + nativeQuery);

        // Crear la consulta nativa
        Query query = getEntityManager().createNativeQuery(nativeQuery, Proveedor.class);
        query.setParameter(1, abcProveedor);

        //System.out.println("===> Query a ejecutarse Final " + query);

        // Ejecutar la consulta
        @SuppressWarnings("unchecked")
        List<Proveedor> pstListBcd = query.getResultList();

        //System.out.println("===> Proveedores encontrados:" + pstListBcd.size());


        //String queryBcd = "SELECT p FROM Proveedor p WHERE p.bcd = :bcd";
        //TypedQuery<Proveedor> consultaBcd = getEntityManager().createQuery(queryBcd, Proveedor.class);
        //consultaBcd.setParameter("bcd", abcProveedor);

        //List<Proveedor> pstListBcd = consultaBcd.getResultList();

        //System.out.println("===> Proveedores encontrados para BCD: " + pstListBcd.size());

        // Si hay más de un proveedor, filtramos por principal

        if (pstListBcd.size() > 1) {
            List<Proveedor> pstListBcdPrincipal = new ArrayList<>();
            for (Proveedor pst : pstListBcd) {
                if (pst.getPrincipal() == 1) {
                    pstListBcdPrincipal.add(pst);
                }
            }

            if (!pstListBcdPrincipal.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal con BCD {} encontrado: {}", abcProveedor, pstListBcdPrincipal.get(0).getNombre());
                }
                return pstListBcdPrincipal;
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal no especificado para BCD {}", abcProveedor);
                }
                // Si no hay principal, regresa el primero o todos (como en getProveedorByIDO)
                return Collections.singletonList(pstListBcd.get(0));
            }
        } else {
            if (pstListBcd.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor no encontrado para BCD: {}", abcProveedor);
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor con BCD {} encontrado: {}", abcProveedor, pstListBcd.get(0).getNombre());
                }
            }
            return pstListBcd;
        }
    }


    /*
    @Override
    public List<Proveedor> getProveedorByIDA(BigDecimal idaProveedor) {

        // Buscamos si existen proveedores por IDA.
        String queryIda = "SELECT p "
                + "FROM Proveedor p "
                + "WHERE p.ida = :ida";

        TypedQuery<Proveedor> consultaIda = getEntityManager().createQuery(queryIda, Proveedor.class);
        consultaIda.setParameter("ida", idaProveedor);

        List<Proveedor> pstListIda = consultaIda.getResultList();

        if (pstListIda.size() > 1) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Varios proveedores encontrados");
            }
            String queryIdaPrincipal = "SELECT p "
                    + "FROM Proveedor p "
                    + "WHERE p.ida = :ida "
                    + "AND p.principal = :principal";

            TypedQuery<Proveedor> consultaIdaPrincipal = getEntityManager().createQuery(queryIdaPrincipal,
                    Proveedor.class);
            consultaIdaPrincipal.setParameter("ida", idaProveedor);
            consultaIdaPrincipal.setParameter("principal", Proveedor.PRINCIPAL);

            List<Proveedor> pstListIdaPrincipal = consultaIdaPrincipal.getResultList();

            // Comprobamos sí la consulta devuelve más de un proveedor. Si es así, filtramos por proveedor principal.
            // En caso contrario se devolvemos la lista vacía o con un proveedor único.
            if (!pstListIdaPrincipal.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal con IDA {} encontrado: {}", idaProveedor,
                            pstListIdaPrincipal.get(0)
                                    .getNombre());
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal no especificado para IDA {}", idaProveedor);
                }
            }
            return pstListIdaPrincipal;
        } else {
            if (pstListIda.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal no encontrado para IDA: {}", idaProveedor);
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal con IDA {} encontrado: {}", idaProveedor, pstListIda.get(0)
                            .getNombre());
                }
            }
        }
        return pstListIda;
    }

    @Override
    public List<Proveedor> getProveedorByIDO(BigDecimal idoProveedor) {
        // Buscamos si existen proveedores por IDO.
        String queryIdo = "SELECT p "
                + "FROM Proveedor p "
                + "WHERE p.ido = :ido "
        		+ "AND p.estatus.cdg = :estatus";	

        TypedQuery<Proveedor> consultaIdo = getEntityManager().createQuery(queryIdo, Proveedor.class);
        consultaIdo.setParameter("ido", idoProveedor);
        consultaIdo.setParameter("estatus", Estatus.ACTIVO);

        List<Proveedor> pstListIdo = consultaIdo.getResultList();
        // Comprobamos sí la consulta devuelve más de un proveedor. Si es así, filtramos por proveedor principal.
        // En caso contrario se devolvemos la lista vacía o con un proveedor único.
        if (pstListIdo.size() > 1) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Varios proveedores encontrados");
            }
            String queryIdoPrincipal = "SELECT p "
                    + "FROM Proveedor p "
                    + "WHERE p.ido = :ido "
                    + "AND p.principal = :principal";
            		

            TypedQuery<Proveedor> consultaIdoPrincipal = getEntityManager().createQuery(queryIdoPrincipal,
                    Proveedor.class);
            consultaIdoPrincipal.setParameter("ido", idoProveedor);
            consultaIdoPrincipal.setParameter("principal", Proveedor.PRINCIPAL);
           

            List<Proveedor> pstListIdoPrincipal = consultaIdoPrincipal.getResultList();
            if (!pstListIdoPrincipal.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal con IDO {} encontrado: {}", idoProveedor,
                            pstListIdoPrincipal.get(0)
                                    .getNombre());
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No se ha definido proveedor principal para el IDO", idoProveedor);
                }
            }

            return pstListIdoPrincipal;

        } else {
            if (pstListIdo.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal no encontrado para IDO: {}", idoProveedor);
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor con IDO {} encontrado: {}", idoProveedor, pstListIdo.get(0).getNombre());
                }
            }
            return pstListIdo;
        }

    }

    @Override
    public List<Proveedor> getProveedorByABC(BigDecimal abcProveedor) {
        //Esto se modifico a partir del cambio de numeracion del 03/08/19
        // Buscamos si existen proveedores por BCD.
        String queryIdo = "SELECT p "
                + "FROM Proveedor p "
                + "WHERE p.bcd = :bcd";

        TypedQuery<Proveedor> consultaIdo = getEntityManager().createQuery(queryIdo, Proveedor.class);
        consultaIdo.setParameter("bcd", abcProveedor);

        List<Proveedor> pstListIdo = consultaIdo.getResultList();
        // Comprobamos sí la consulta devuelve más de un proveedor. Si es así, filtramos por proveedor principal.
        // En caso contrario se devolvemos la lista vacía o con un proveedor único.
        if (pstListIdo.size() > 1) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Varios proveedores encontrados");
            }
            String queryIdoPrincipal = "SELECT p "
                    + "FROM Proveedor p "
                    + "WHERE p.bcd= :ido "
                    + "AND p.principal = :principal";

            TypedQuery<Proveedor> consultaIdoPrincipal = getEntityManager().createQuery(queryIdoPrincipal,
                    Proveedor.class);
            consultaIdoPrincipal.setParameter("ido", abcProveedor);
            consultaIdoPrincipal.setParameter("principal", Proveedor.PRINCIPAL);

            List<Proveedor> pstListIdoPrincipal = consultaIdoPrincipal.getResultList();
            if (!pstListIdoPrincipal.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal con BCD {} encontrado: {}", abcProveedor,
                            pstListIdoPrincipal.get(0)
                                    .getNombre());
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No se ha definido proveedor principal para el BCD", abcProveedor);
                }
            }

            return pstListIdoPrincipal;

        } else {
            if (pstListIdo.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor principal no encontrado para BCD: {}", abcProveedor);
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Proveedor con IDO {} encontrado: {}", abcProveedor, pstListIdo.get(0).getNombre());
                }
            }
            return pstListIdo;
        }

    }

     */

	@Override
	public boolean userIdUsedBySamePST(Proveedor proveedor) {
		
		String query="";
					
			 query = "SELECT count(1) FROM Proveedor p WHERE p.id= :idProveedor "
	                    + "AND p.usuario.userid =:usuario";
			 TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);
		        tQuery.setParameter("idProveedor", proveedor.getId());
		        
		        tQuery.setParameter("usuario", proveedor.getUsuario().getUserid());

		        Long resultado = tQuery.getSingleResult();
		        if (resultado == null || resultado.longValue() == 0) {
		            //El usuario existe per no pertenece al PST actual
		        	return Boolean.FALSE;
		        } else {
		        	//El usuario y pertenece al PST actual
		            return Boolean.TRUE;
		        }

		 }

    private StringBuffer getNativeQueryBase() {
        StringBuffer sbquery = new StringBuffer(
                "SELECT PST.ID_PST, PST.CDG_PST, PST.ID_TIPO_PST, PST.ID_ESTADO_OT, PST.NOMBRE, PST.NOMBRE_CORTO, PST.CALLE, PST.NUM_INT, PST.NUM_EXT, PST.COLONIA, PST.CP, PST.CIUDAD, ");
        sbquery.append(
                "PST.CDG_TIPO_SERVICIO, PST.ID_TIPO_RED_ORIGINAL, PST.ID_TIPO_RED, PST.URL, PST.ABC AS ABC, PST.BCD AS BCD, PST.IDA AS IDA, PST.IDO AS IDO, PST.ID_ESTATUS, ");
        sbquery.append(
                "PST.ID_OPERADOR, PST.ID_USUARIO, PST.ID_USUARIO_CREA, PST.FECHA_CREACION, PST.ID_USUARIO_MOD, PST.FECHA_MODIFICACION, PST.PRINCIPAL, PST.CONSULTA_PUBLICA_SNS ");
        sbquery.append("FROM CAT_PST PST ");

        return sbquery;
    }
		
}
