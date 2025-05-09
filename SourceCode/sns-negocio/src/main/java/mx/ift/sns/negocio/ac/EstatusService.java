package mx.ift.sns.negocio.ac;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.IEstatusDao;
import mx.ift.sns.modelo.central.Estatus;

/**
 * Servicio de Estado.
 */
@Stateless(name = "EstatusService", mappedName = "EstatusService")
@Remote(IEstatusService.class)
public class EstatusService implements IEstatusService {

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(EstatusService.class);

    /**
     * Dao estatus.
     */
    @Inject
    private IEstatusDao estatusDao;

    @Override
    public List<Estatus> findAllEstatus() {
        return estatusDao.findAllEstatus();
    }

    @Override
    public Estatus getEstatusById(String idEstatus) {
        return estatusDao.getEstatusById(idEstatus);
    }
}
