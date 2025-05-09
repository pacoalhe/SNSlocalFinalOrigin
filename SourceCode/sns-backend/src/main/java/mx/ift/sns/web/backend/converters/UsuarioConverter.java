package mx.ift.sns.web.backend.converters;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.usu.IUsuariosService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase converter para usuario.
 */
@ManagedBean(name = "usuarioConverter")
@RequestScoped
public class UsuarioConverter implements Converter {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioConverter.class);

    /** Injection de IProveedoresService. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /**
     * Devuelve la representación Proveedor de JAVA a partir del valor de un componente JSF.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return usuario
     * @throws ConverterException converter
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value)
            throws ConverterException {

        Usuario usuario = null;
        if (StringUtils.isNotEmpty(value)) {
            try {
                usuario = usuariosService.findUsuarioById(value);
            } catch (Exception e) {

                LOGGER.error("error", e);

                throw new ConverterException();
            }
        }

        LOGGER.debug("value {} res {}", value, usuario);
        return usuario;
    }

    /**
     * Devuelve la representacion cadena de un usuario.
     * @param context contexto
     * @param component componente
     * @param value valor
     * @return idUsuario
     * @throws ConverterException converter
     */
    public String getAsString(FacesContext context, UIComponent component, Object value)
            throws ConverterException {

        String resultado = "";
        if (value != null) {
            Usuario u = (Usuario) value;
            resultado = (String) u.getId().toString();
        }

        LOGGER.debug("value {} res {}", value, resultado);
        return resultado;
    }
}
