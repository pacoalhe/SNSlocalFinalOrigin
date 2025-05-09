package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IAbnDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.AbnCodeUpdater;
import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.abn.VCatalogoAbn;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.ot.Poblacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos para áreas de numeración.
 */
@Named
public class AbnDaoImpl extends BaseDAO<Abn> implements IAbnDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbnDaoImpl.class);

    /**
     * Recupera la ruta de un campo específico de PoblacionAbn. Al usar criterias, no se puede acceder directamente al
     * atributo de un objeto compuesto (objeto.id.sna x ej). Para ello es necesario encadenar llamadas a cada atributo
     * usando un get que nos devuelva el Path correspondiente.
     * @param pFiled Atributo que se quiere obtener de un objeto JPA.
     * @param pRoot Contenedor
     * @return Path del atributo dentro de la clase JPA.
     */
    private Path<Object> getPobAbnPath(String pFiled, Root<PoblacionAbn> pRoot) {
        // Cada campo de cada objeto viene separado por el "."
        String[] fields = pFiled.split("\\.");
        Path<Object> path = pRoot.get(fields[0]);

        // Recuperamos el path anidando las llamadas.
        for (int i = 1; i < fields.length; i++) {
            path = path.get(fields[i]);
        }

        return path;
    }

    @Override
    public List<Abn> findAllAbns() {
        // ABNs con estatus Activo
        EstadoAbn estadoAbn = new EstadoAbn();
        estadoAbn.setCodigo(EstadoAbn.ACTIVO);

        String query = "SELECT a FROM Abn a WHERE a.estadoAbn = :estadoAbn ORDER by a.codigoAbn";

        TypedQuery<Abn> consulta = getEntityManager().createQuery(query, Abn.class);
        consulta.setParameter("estadoAbn", estadoAbn);

        List<Abn> resultado = consulta.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} ABNs activos.", resultado.size());
        }
        return resultado;
    }

    @Override
    public List<Abn> findAllAbns(FiltroBusquedaABNs pFiltros) {
        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        // SELECT a.abn FROM PoblacionAbn
        CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Abn> cQuery = cBuilder.createQuery(Abn.class);
        Root<PoblacionAbn> abn = cQuery.from(PoblacionAbn.class);

        // WHERE
        Predicate wherePredicate = cBuilder.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = cBuilder.equal(this.getPobAbnPath(filtro.getCampo(), abn), filtro.getValor());
            wherePredicate = cBuilder.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate).distinct(true);
        cQuery.select(abn.<Abn> get("abn"));
        cQuery.orderBy(cBuilder.asc(abn.get("abn").get("codigoAbn")));

        TypedQuery<Abn> tQuery = getEntityManager().createQuery(cQuery);

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<Abn> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sbTraza = new StringBuffer();
            sbTraza.append("ABNs encontrados: ").append(result.size());
            sbTraza.append(", paginación: ").append(pFiltros.isUsarPaginacion());
            if (pFiltros.isUsarPaginacion()) {
                sbTraza.append(" (Página: ").append(pFiltros.getNumeroPagina());
                sbTraza.append(", MaxResult: ").append(pFiltros.getResultadosPagina()).append(")");
            }
            LOGGER.debug(sbTraza.toString());
        }

        return result;
    }

    @Override
    public List<VCatalogoAbn> findAllAbnsForCatalog(FiltroBusquedaABNs pFiltros) {
        // Filtros de Búsqueda definidos sobre los ABNs.
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltrosCatalogo(pFiltros.getListaFiltros());

        // SELECT a FROM Abn
        CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<VCatalogoAbn> cQuery = cBuilder.createQuery(VCatalogoAbn.class);
        Root<VCatalogoAbn> vAbnCat = cQuery.from(VCatalogoAbn.class);

        // WHERE
        Predicate wherePredicate = cBuilder.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = cBuilder.equal(vAbnCat.get(filtro.getCampo()), filtro.getValor());
            wherePredicate = cBuilder.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate).distinct(true);
        cQuery.select(vAbnCat);

        cQuery.orderBy(cBuilder.asc(vAbnCat.get("idAbn")), cBuilder.asc(vAbnCat.get("idEstado")),
                cBuilder.asc(vAbnCat.get("idMunicipio")), cBuilder.asc(vAbnCat.get("idPoblacion")));

        TypedQuery<VCatalogoAbn> tQuery = getEntityManager().createQuery(cQuery);

        List<VCatalogoAbn> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han recuperado {} registros de la vista.", result.size());
            // LOGGER.debug(tQuery.toString());
        }
        return result;
    }

    @Override
    public int findAllAbnsCount(FiltroBusquedaABNs pFiltros) {
        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        // SELECT a FROM Abn
        CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cBuilder.createQuery(Long.class);
        Root<PoblacionAbn> abn = cQuery.from(PoblacionAbn.class);

        // WHERE
        Predicate wherePredicate = cBuilder.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = cBuilder.equal(this.getPobAbnPath(filtro.getCampo(), abn), filtro.getValor());
            wherePredicate = cBuilder.and(wherePredicate, pred);
        }
        cQuery.select(cBuilder.countDistinct((abn.get("abn"))));
        cQuery.where(wherePredicate);

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery);
        int rowCount = tQuery.getSingleResult().intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ABNs contados: " + rowCount);
            // LOGGER.debug(tQuery.toString());
        }

        return rowCount;
    }

    @Override
    public Abn getAbnById(BigDecimal pCodigo) {
        Abn abn = null;
        try {
            abn = getEntityManager().find(Abn.class, pCodigo);
        } catch (NoResultException nre) {
            return null;
        }
        return abn;
    }

    @Override
    public Poblacion getPoblacionAnclaByCodigoAbn(BigDecimal codigo) {
        try {
            String query = "SELECT a.poblacionAncla FROM Abn a where a.codigoAbn = :codigo";
            TypedQuery<Poblacion> consulta = getEntityManager().createQuery(query, Poblacion.class);
            consulta.setParameter("codigo", codigo);

            Poblacion poblacion = consulta.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Se ha encontrado la población ancla '{}' para el código ABN {}",
                        poblacion.getNombre(), codigo);
            }
            return poblacion;
        } catch (NoResultException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se ha encontrado población ancla para el código ABN {}", codigo);
            }
            return null;
        }
    }

    @Override
    public Abn getAbnByCodigoNir(String codigoNir) {
        String query = "SELECT n.abn FROM Nir n LEFT JOIN FETCH n.abn.nirs where n.codigo = :codigoNir";
        TypedQuery<Abn> consulta = getEntityManager().createQuery(query, Abn.class);
        consulta.setParameter("codigoNir", Integer.parseInt(codigoNir));

        Abn abnresult = consulta.getSingleResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ABN con código {} encontrado.", codigoNir);
        }

        // Es necesario hacer una llamada explicita a la lista indirecta para que se creen las instancias en la
        // sesion de jsf
        if (abnresult != null) {
            abnresult.getNirs().size();
        }

        return abnresult;
    }

    @Override
    public Abn changeAbnCode(Abn pViejoAbn, Abn pNuevoAbn) throws Exception {

        // Las relaciones de ABN con el resto de tablas son LAZY y no se utiliza ningún tipo de actualización por
        // cascada. Por lo tanto, hay que hacer un update manual en todas las tablas donde se utilize el código
        // de ABN. Esto se debe a que la relación entre ABN y el resto de tablas no es lógica, es decir, las entidades
        // pueden existir aunque no exista el ABN.

        // Además, existen muchas tablas, como las de LiberaciónSolicitada, etc que usan el códigoABN pero no está
        // directamente relacionada con la entidad ABN, por lo que un Cascade.Update no funcionaría.

        // Lista de Querys a ejecutar con NIR como objeto relacionado.
        List<AbnCodeUpdater> updateTablesNirRelation = new ArrayList<AbnCodeUpdater>();

        // Lista de Querys a ejecutar con ID_NIR como id relacionado.
        List<AbnCodeUpdater> updateTablesIdNirRelation = new ArrayList<AbnCodeUpdater>();

        // ----------------------------------------
        // Tablas cuya referencia a codigoAbn es FK
        // ----------------------------------------

        // Tabla CAT_NIR
        AbnCodeUpdater acuCatNir = new AbnCodeUpdater("Nir", "abn");
        updateTablesNirRelation.add(acuCatNir);

        // Tabla POBLACION_ABN (PoblacionAbn)
        AbnCodeUpdater acuPoblacionAbn = new AbnCodeUpdater("PoblacionAbn", "abn");
        updateTablesNirRelation.add(acuPoblacionAbn);

        // Tabla NG_SOLICITUD_CONSOLIDACION (SolicitudConsolidacion) -> ABN Entrega
        AbnCodeUpdater acuSolConsEntrega = new AbnCodeUpdater("SolicitudConsolidacion", "abnEntrega");
        updateTablesNirRelation.add(acuSolConsEntrega);

        // Tabla NG_SOLICITUD_CONSOLIDACION (SolicitudConsolidacion) -> ABN Recibe
        AbnCodeUpdater acuSolConsRecibe = new AbnCodeUpdater("SolicitudConsolidacion", "abnRecibe");
        updateTablesNirRelation.add(acuSolConsRecibe);

        // Tabla REP_DET_LINEA_ACTIVA_DET (DetLineaActivaDet)
        AbnCodeUpdater acuDetLineaActivaDet = new AbnCodeUpdater("DetLineaActivaDet", "abn");
        updateTablesNirRelation.add(acuDetLineaActivaDet);

        // Tabla REP_DET_LINEA_ARRENDADOR (DetLineaArrendada)
        AbnCodeUpdater acuDetLineaArrendada = new AbnCodeUpdater("DetLineaArrendada", "abn");
        updateTablesNirRelation.add(acuDetLineaArrendada);

        // Tabla REP_DET_LINEA_ARRENDATARIO (DetLineaArrendatario)
        AbnCodeUpdater acuDetLineaArrendatario = new AbnCodeUpdater("DetLineaArrendatario", "abn");
        updateTablesNirRelation.add(acuDetLineaArrendatario);

        // Tabla CAT_EQUIPO_SENAL_CPSI (EquipoSenalCpsi)
        AbnCodeUpdater acuEquipoSenalCpsi = new AbnCodeUpdater("EquipoSenalCpsi", "abn");
        updateTablesNirRelation.add(acuEquipoSenalCpsi);

        // Tabla CAT_EQUIPO_SENAL_CPSN (EquipoSenalCPSN)
        AbnCodeUpdater acuEquipoSenalCpsn = new AbnCodeUpdater("EquipoSenalCPSN", "abn");
        updateTablesNirRelation.add(acuEquipoSenalCpsn);

        // -------------------------------------------
        // Tablas cuya referencia a codigoAbn no es FK
        // -------------------------------------------

        // Tabla ABN_CENTRAL (AbnCentral)
        AbnCodeUpdater acuAbnCentral = new AbnCodeUpdater("AbnCentral", "id.idAbn");
        updateTablesIdNirRelation.add(acuAbnCentral);

        // Tabla REP_ABD_SERIE_ARRENDADA_PADRE (SerieArrendadaPadre)
        AbnCodeUpdater acuSerieArrendadaPadre = new AbnCodeUpdater("SerieArrendadaPadre", "id.idAbn");
        updateTablesIdNirRelation.add(acuSerieArrendadaPadre);

        // Tabla NG_CESION_SOLICITADA (CesionSolicitadaNg)
        AbnCodeUpdater acuCesionSolicitada = new AbnCodeUpdater("CesionSolicitadaNg", "idAbn");
        updateTablesIdNirRelation.add(acuCesionSolicitada);

        // Tabla NG_LIBERACION_SOLICITADA (LiberacionSolicitadaNg)
        AbnCodeUpdater acuLiberacionSolicitada = new AbnCodeUpdater("LiberacionSolicitadaNg", "idAbn");
        updateTablesIdNirRelation.add(acuLiberacionSolicitada);

        // Tabla NG_REDISTRIBUCION_SOL (RedistribucionSolicitadaNg)
        AbnCodeUpdater acuRedistribucionSolicitada = new AbnCodeUpdater("RedistribucionSolicitadaNg", "idAbn");
        updateTablesIdNirRelation.add(acuRedistribucionSolicitada);

        // Actualización de Tablas con objetos ABN en cadena
        for (AbnCodeUpdater acu : updateTablesNirRelation) {
            Query updateQuery = getEntityManager().createQuery(acu.getUpdateQuery("viejoCodigoAbn", "nuevoCodigoAbn"));
            updateQuery.setParameter("nuevoCodigoAbn", pNuevoAbn);
            updateQuery.setParameter("viejoCodigoAbn", pViejoAbn);

            int updateQueryResult = updateQuery.executeUpdate();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Actualizado ABN {} por {} en {}. {} registros actualizados.",
                        pViejoAbn.getCodigoAbn(), pNuevoAbn.getCodigoAbn(), acu.getEntidad(), updateQueryResult);
            }
        }

        // Actualización de Tablas con IdNir en cadena
        for (AbnCodeUpdater acu : updateTablesIdNirRelation) {
            Query updateQuery = getEntityManager().createQuery(acu.getUpdateQuery("viejoCodigoAbn", "nuevoCodigoAbn"));
            updateQuery.setParameter("nuevoCodigoAbn", pNuevoAbn.getCodigoAbn());
            updateQuery.setParameter("viejoCodigoAbn", pViejoAbn.getCodigoAbn());

            int updateQueryResult = updateQuery.executeUpdate();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Actualizado ABN {} por {} en {}. {} registros actualizados.",
                        pViejoAbn.getCodigoAbn(), pNuevoAbn.getCodigoAbn(), acu.getEntidad(), updateQueryResult);
            }
        }

        return pNuevoAbn;
    }

    @Override
    public boolean existAbn(String abn) {
        String squery = "SELECT COUNT(a) FROM Abn a WHERE a.codigoAbn = :abn";
        TypedQuery<Long> query = getEntityManager().createQuery(squery, Long.class);
        query.setParameter("abn", new BigDecimal(abn));
        Long resultado = query.getSingleResult();

        return (resultado != null && resultado > 0);
    }

    @Override
    public List<Abn> findAbnInEstado(String estado) {
        String squery = "SELECT DISTINCT p.abn FROM PoblacionAbn p "
                + "WHERE p.inegi.municipio.id.codEstado = :estado";
        TypedQuery<Abn> consulta = getEntityManager().createQuery(squery, Abn.class);
        consulta.setParameter("estado", estado);

        List<Abn> resultado = consulta.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} ABNs en el estado {}.", resultado.size(), estado);
        }
        return resultado;
    }

    @Override
    public List<Abn> findAbnInMunicipio(String municipio, String estado) {
        String squery = "SELECT DISTINCT p.abn FROM PoblacionAbn p "
                + "WHERE p.inegi.municipio.id.codMunicipio = :municipio AND p.inegi.municipio.id.codEstado = :estado";
        TypedQuery<Abn> consulta = getEntityManager().createQuery(squery, Abn.class);
        consulta.setParameter("municipio", municipio);
        consulta.setParameter("estado", estado);

        List<Abn> resultado = consulta.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} ABNs en el municipio {}{}.", resultado.size(), estado, municipio);
        }
        return resultado;
    }

    @Override
    public Abn getAbnByPoblacion(Poblacion poblacion) {
        String squery = "SELECT p.abn FROM PoblacionAbn p "
                + "WHERE p.inegi = :poblacion";
        TypedQuery<Abn> consulta = getEntityManager().createQuery(squery, Abn.class);
        consulta.setParameter("poblacion", poblacion);

        Abn resultado = consulta.getSingleResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se ha encontrado el abn {}  en la población {}.", resultado, poblacion.getNombre());
        }
        return resultado;
    }
}
