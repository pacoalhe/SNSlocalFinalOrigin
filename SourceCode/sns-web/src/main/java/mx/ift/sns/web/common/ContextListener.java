/**
 * Esta clase es propiedad de IECISA
 *
 * (C) Informatica El Corte Inglés
 *
 */

package mx.ift.sns.web.common;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.utils.Version;
import mx.ift.sns.utils.WeblogicNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inicio y parada de la aplicacion.
 */
@WebListener
public class ContextListener implements ServletContextListener {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextListener.class);

    /** Servicio de bitacora. */
    @EJB(mappedName = "BitacoraService")
    private IBitacoraService bitacoraService;

    @Override
    public void contextInitialized(ServletContextEvent ctx) {

        LOGGER.debug("start");
        StringBuilder b = new StringBuilder();

        String nodo = WeblogicNode.getName();

        if (nodo != null) {
            b.append(nodo);
            b.append(" ");
        }

        b.append("start ");
        b.append(Version.getModulo());
        b.append(" ");
        b.append(Version.getVersion());
        final String v = b.toString();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(v);
        }

        if (bitacoraService != null) {
            bitacoraService.add(v);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent ctx) {
        StringBuilder b = new StringBuilder();

        String nodo = WeblogicNode.getName();

        if (nodo != null) {
            b.append(nodo);
            b.append(" ");
        }

        b.append("stop ");
        b.append(Version.getModulo());
        b.append(" ");
        b.append(Version.getVersion());
        final String v = b.toString();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(v);
        }

        if (bitacoraService != null) {
            bitacoraService.add(v);
        }
    }
}
