package mx.ift.sns.negocio.pnn;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.pnn.IPlanDAO;
import mx.ift.sns.dao.pnn.IPlanMaestroDAO;
import mx.ift.sns.dao.pnn.ITipoPlanDAO;
import mx.ift.sns.dao.pnn.ITipoPlanRolDAO;
import mx.ift.sns.modelo.pnn.Plan;
import mx.ift.sns.modelo.pnn.PlanMaestroDetalle;
import mx.ift.sns.modelo.pnn.TipoPlan;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.ng.ISeriesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de planes de numeracion.
 */
@Stateless(name = "PlanNumeracionService", mappedName = "PlanNumeracionService")
@Remote(IPlanNumeracionService.class)
public class PlanNumeracionService implements IPlanNumeracionService {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanNumeracionService.class);

    /** DAO de tipos de plan. */
    @Inject
    private ITipoPlanDAO tipoPlanDAO;

    /** DAO de plan. */
    @Inject
    private IPlanDAO planDAO;

    /** DAO de tipo de plan por rol. */
    @Inject
    private ITipoPlanRolDAO tipoPlanRolDAO;

    @Inject
    private IPlanMaestroDAO planMaestroDao;

    /** Servicio series. */
    @EJB
    private ISeriesService seriesService;

    /** Servicio bitacora. */
    @EJB
    private IBitacoraService bitacora;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(Plan plan) {
        planDAO.create(plan);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Plan getUltimoPlan(TipoPlan tipoPlan) {
        return null;

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TipoPlan getTipoPlanbyId(String id) {
        return tipoPlanDAO.getTipoPlanById(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deletePlanesViejos() {

        List<TipoPlan> tipos = tipoPlanDAO.findAll();
        for (TipoPlan tipo : tipos) {
            if (!tipo.getRetencion().equals(BigDecimal.ZERO)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("borrando planes tipo {} {}", tipo.getId(), tipo.getRetencion());
                }

                Calendar fecha = Calendar.getInstance();
                fecha.add(Calendar.DAY_OF_YEAR, -tipo.getRetencion().intValue());
                List<Plan> l = planDAO.findOlderThan(fecha.getTime(), tipo);
                if (l != null) {
                    for (Plan plan : l) {
                        planDAO.delete(plan);
                        bitacora.add("borrado plan " + plan.getNombre());
                    }
                }
            }
        }

    }

    @Override
    public String getNombre(TipoPlan tipoPlan, Calendar fecha) {
        String nombre = tipoPlan.getFormatoNombre();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("nombre {}", nombre);
        }

        int d = fecha.get(Calendar.DAY_OF_MONTH);
        String dd = "";
        if (d <= 9) {
            dd = "0" + d;
        } else {
            dd = "" + d;
        }

        int m = fecha.get(Calendar.MONTH) + 1;
        String mm = "";
        if (m <= 9) {
            mm = "0" + m;
        } else {
            mm = "" + m;
        }

        int y = fecha.get(Calendar.YEAR);
        String yy = "";

        yy = "" + y;

        nombre = nombre.replace("dd", dd);
        nombre = nombre.replace("MM", mm);
        nombre = nombre.replace("YYYY", yy);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("format {}", nombre);
        }

        return nombre;
    }

    @Override
    public List<String> findAllTipoPlanByRol(String idRol) {
        return tipoPlanRolDAO.findAllTipoPlanByRol(idRol);
    }

    @Override
    public List<Plan> findAllPlanByRol(String idRol) {
        return tipoPlanRolDAO.findAllPlanByRol(idRol);
    }

    @Override
    public Plan getPlanByTipo(String idTipo) {
        return tipoPlanRolDAO.getPlanByTipo(idTipo);
    }

    @Override
    public Plan getPlanByTipoAndClaveServicio(String idTipo, BigDecimal claveServicio) {
        return tipoPlanRolDAO.getPlanByTipoAndClaveServicio(idTipo, claveServicio);
    }

    /**
     * FJAH 17042025
     * @param numeroInicial
     * @param numeroFinal
     * @return
     */
    @Override
    public PlanMaestroDetalle getDetalleNumeroConsultaPublica(Long numeroInicial, Long numeroFinal) {
        return planMaestroDao.getDetalleNumeroConsultaPublica(numeroInicial, numeroFinal);
    }

}
