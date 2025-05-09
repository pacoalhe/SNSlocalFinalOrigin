/**
 * Esta clase es propiedad de IECISA
 *
 * (C) Informatica El Corte Inglés
 *
 */

package mx.ift.sns.web.common.error;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom handler.
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);

    /** Excepcion. */
    private ExceptionHandler wrapped;

    /**
     * .
     * @param exception excepcion
     */
    CustomExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {

        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();

        if (LOGGER.isDebugEnabled()) {
            if (i.hasNext()) {
                LOGGER.debug("");
            }
        }

        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

            // get the exception from context
            Throwable t = context.getException();

            final FacesContext fc = FacesContext.getCurrentInstance();
            final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
            // final NavigationHandler nav = fc.getApplication().getNavigationHandler();

            // here you do what ever you want with exception
            try {
                // log error ?
                // redirect error page
                requestMap.put("exceptionMessage", t.getMessage());
                if (t instanceof ViewExpiredException) {

                    LOGGER.debug("sesion expirada");

                    String errorPageLocation = "/index.xhtml";
                    fc.setViewRoot(fc.getApplication().getViewHandler().createView(fc, errorPageLocation));
                    fc.getPartialViewContext().setRenderAll(true);
                    fc.renderResponse();

                } else if (t instanceof WebApplicationException) {

                    LOGGER.error("Critical Exception! app", t.getMessage());
                    try {
                        fc.getExternalContext().dispatch("/errores/error.xhtml");
                        fc.responseComplete();

                    } catch (IOException e) {
                        LOGGER.error("error", e);
                    }
                } else {
                    LOGGER.error("Critical Exception! error", t);
                    // try {
                    // fc.getExternalContext().dispatch("/errores/error.xhtml");
                    // fc.responseComplete();
                    //
                    // } catch (IOException e) {
                    // LOGGER.error("error", e);
                    // }
                }

                // remove the comment below if you want to report the error in a jsf error message
                // JsfUtil.addErrorMessage(t.getMessage());

            } finally {
                // remove it from queue
                i.remove();
            }
        }
        // parent hanle
        getWrapped().handle();

    }
}
