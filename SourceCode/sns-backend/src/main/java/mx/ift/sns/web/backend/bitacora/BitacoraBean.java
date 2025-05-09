package mx.ift.sns.web.backend.bitacora;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.filtros.FiltroBusquedaBitacoraLog;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.ApplicationException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean de Bitacora.
 */
@ManagedBean(name = "bitacoraBean")
@ViewScoped
public class BitacoraBean implements Serializable {

    /** UID Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(BitacoraBean.class);

    /** Id mensajes error. */
    private static final String MSG_ERROR = "MSG_ConsultarBitacoraLog";

    /** Servicio de Bitacora. */
    @EJB(mappedName = "BitacoraService")
    private IBitacoraService bitacoraService;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** Modelo de la tabla resumen. */
    private BitacoraLazyModel model;

    /** Fecha inicio busqueda. */
    private Date fechaInicio;

    /** Fecha fin busqueda. */
    private Date fechaFin;

    /** Usuario seleccionado. */
    private Usuario usuarioSeleccionado;

    /** Lista de usuarios. */
    private List<Usuario> listaUsuarios;

    /** Filtro de busqueda. */
    private FiltroBusquedaBitacoraLog filtro;

    /**
     * Inicializacion del bean.
     */
    @PostConstruct
    public void init() {
        LOGGER.debug("");

        try {
            filtro = new FiltroBusquedaBitacoraLog();
            model = new BitacoraLazyModel();
            model.setService(bitacoraService);
            model.setFiltro(filtro);

            listaUsuarios = usuariosService.findAllUsuarios();

            fechaFin = null;
            fechaInicio = null;
            usuarioSeleccionado = null;

        } catch (Exception e) {
            throw new ApplicationException();
        }
    }

    /**
     * Actualiza la busqueda.
     */
    public void buscar() {
        LOGGER.debug("");

        LOGGER.debug("{} {} {}", fechaInicio, fechaFin, usuarioSeleccionado);
        if (fechaInicio != null) {
            filtro.setFechaInicio(fechaInicio);
        }

        if (fechaFin != null) {
            filtro.setFechaFin(FechasUtils.dateToCalendarLastMomentOfDay(fechaFin).getTime());
        }

        if (usuarioSeleccionado != null) {
            filtro.setUsuario(usuarioSeleccionado);
        }

        if ((fechaInicio != null) && (fechaFin != null) && fechaInicio.after(fechaFin)) {
            MensajesBean.addErrorMsg(MSG_ERROR, Errores.ERROR_0006);
        }
    }

    /**
     * Limpia la busqueda.
     */
    public void limpiar() {
        LOGGER.debug("");

        fechaFin = null;
        fechaInicio = null;
        usuarioSeleccionado = null;

        filtro.clear();
    }

    /**
     * Modelo de la tabla resumen.
     * @return BitacoraLazyModel
     */
    public BitacoraLazyModel getModel() {
        return model;
    }

    /**
     * Modelo de la tabla resumen.
     * @param model BitacoraLazyModel
     */
    public void setModel(BitacoraLazyModel model) {
        this.model = model;
    }

    /**
     * Fecha inicio busqueda.
     * @return Date
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Fecha inicio busqueda.
     * @param fechaInicio Date
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Fecha fin busqueda.
     * @return Date
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * Fecha fin busqueda.
     * @param fechaFin Date
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Usuario seleccionado.
     * @return Usuario
     */
    public Usuario getUsuarioSeleccionado() {
        return usuarioSeleccionado;
    }

    /**
     * Usuario seleccionado.
     * @param usuarioSeleccionado Usuario
     */
    public void setUsuarioSeleccionado(Usuario usuarioSeleccionado) {
        LOGGER.debug(" {}", usuarioSeleccionado);
        this.usuarioSeleccionado = usuarioSeleccionado;
    }

    /**
     * Lista de usuarios.
     * @return List<Usuario>
     */
    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    /**
     * Lista de usuarios.
     * @param listaUsuarios List<Usuario>
     */
    public void setListaUsuarios(List<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }
}
