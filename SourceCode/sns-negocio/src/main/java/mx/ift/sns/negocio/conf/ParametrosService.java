package mx.ift.sns.negocio.conf;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import mx.ift.sns.dao.ng.IParametroDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de parametros.
 */
@Stateless(name = "ParametrosService", mappedName = "ParametrosService")
@Remote(IParametrosService.class)
public class ParametrosService implements IParametrosService {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ParametrosService.class);

    /** DAO de Parametros. */
    @Inject
    private IParametroDao paramDao;

    @Override
    public String getParamByName(String name) {
        try {
            String res = paramDao.getParamByName(name);
            //System.out.println("**********parametro:"+res)
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("param '{}'='{}'", name, res);
            }
            return res;
        } catch (NoResultException nre) {
            LOGGER.error("No se ha encontrado nungún parámetro con el nombre: " + name);
            return null;
        }

    }
}
