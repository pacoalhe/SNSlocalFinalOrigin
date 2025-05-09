/**
 * Esta clase es propiedad de IECISA
 *
 * (C) Informatica El Corte Inglés
 *
 */

package mx.ift.sns.web.common.error;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * Factoria handler.
 */
public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {

    /** factoria. */
    private ExceptionHandlerFactory parent;

    /**
     * this injection handles jsf.
     * @param parent factoria
     */
    public CustomExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {

        ExceptionHandler handler = new CustomExceptionHandler(parent.getExceptionHandler());

        return handler;
    }
}
