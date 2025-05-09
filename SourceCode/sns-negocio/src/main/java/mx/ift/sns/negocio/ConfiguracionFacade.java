package mx.ift.sns.negocio;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import mx.ift.sns.negocio.conf.IParametrosService;

/**
 * Adminstración de Configuración.
 */
@Stateless(name = "ConfiguracionFacade", mappedName = "ConfiguracionFacade")
@Remote(IConfiguracionFacade.class)
public class ConfiguracionFacade implements IConfiguracionFacade {

    /** Servicio parametros. */
    @EJB
    private IParametrosService parametroService;

    @Override
    public String getParamByName(String name) {
        return parametroService.getParamByName(name);
    }
}
