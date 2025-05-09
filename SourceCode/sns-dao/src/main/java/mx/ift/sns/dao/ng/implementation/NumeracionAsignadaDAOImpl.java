package mx.ift.sns.dao.ng.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.INumeracionAsignadaDAO;
import mx.ift.sns.modelo.ng.NumeracionAsignada;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.Nir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO de acceso a Numeracion Asignada.
 */
@Named
public class NumeracionAsignadaDAOImpl extends BaseDAO<NumeracionAsignada> implements INumeracionAsignadaDAO {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumeracionAsignadaDAOImpl.class);

    @Override
    public NumeracionAsignada findNumeracionArrendada(Nir nir, Serie serie, String inicioRango, String finRango,
            Proveedor arrendador) {

        String strQuery = "SELECT n FROM NumeracionAsignada n where n.sna = :sna and n.nir.codigoNir=:nir "
                + " and n.inicioRango=:ini and n.finRango=:fin and n.estatus.codigo := asignada";

        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("sna", serie.getId().getSna());
        query.setParameter("nir", serie.getNir().getCodigo());
        query.setParameter("ini", inicioRango);
        query.setParameter("fin", finRango);
        query.setParameter("asignada", EstadoRango.ASIGNADO);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        NumeracionAsignada res = (NumeracionAsignada) query.getSingleResult();

        return res;
    }

    @Override
    public List<NumeracionAsignada> findAllNumeracionAsignadaBySolicitud(SolicitudAsignacion pSolicitud) {

        String strQuery = "SELECT na FROM NumeracionAsignada na WHERE na.solicitudAsignacion = :solicitud";
        TypedQuery<NumeracionAsignada> tQuery = getEntityManager().createQuery(strQuery, NumeracionAsignada.class);
        tQuery.setParameter("solicitud", pSolicitud);
        return tQuery.getResultList();
    }

    @Override
    public boolean existNumeracionAsignadaBySolicita(NumeracionSolicitada numeracionSolicitada) {
        String strQuery = "SELECT COUNT(na) FROM NumeracionAsignada na WHERE na.numeracionSolicitada = :numSol";
        TypedQuery<Long> tQuery = getEntityManager().createQuery(strQuery, Long.class);
        tQuery.setParameter("numSol", numeracionSolicitada);
        return tQuery.getSingleResult() > 0;
    }
}
