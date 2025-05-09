package mx.ift.sns.web.frontend.consultas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoNumeracion;
import mx.ift.sns.modelo.ot.NirNumeracion;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.PoblacionNumeracion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.IConfiguracionFacade;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.web.frontend.util.PaginatorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean que controla el dialog del detalle de la poblacion/proveedor.
 * @author Daniel
 */
@ManagedBean(name = "detallePoblacionProveedorBean")
@ViewScoped
public class DetallePoblacionProveedorBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Servicio de configuración Publica. */
    @EJB(mappedName = "ConfiguracionFacade")
    private IConfiguracionFacade ngConfiguracionService;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetallePoblacionProveedorBean.class);

    /** Serial ID. */
    private static final long serialVersionUID = 1L;
    /**
     * Proveedor numero.
     */
    private Proveedor prestadorNumero;

    /**
     * Lista de poblaciones y numeracion de un proveedor de servicio.
     */
    private List<EstadoNumeracion> estadosNumeracionProveedor;

    /** Flag para renderizado de tabla poblaciones donde el proveedor tiene numeración. */
    private boolean poblacionesProveedorTabla = false;

    /** Flag para renderizado de tabla estados donde el proveedor tiene numeración. */
    private boolean estadosProveedorTabla = false;

    /** Flag para renderizado de tabla nirs donde el proveedor tiene numeración. */
    private boolean nirsProveedorTabla = false;

    /**
     * Lista de nirs y numeracion de un proveedor de servicio.
     */
    private List<NirNumeracion> nirsNumeracionProveedor;
    /**
     * PoblacionNumeracion por proveedor y estado.
     */
    private List<PoblacionNumeracion> poblacionNumeracionProveedorEstado;
    /**
     * Nirs por poblacion.
     */
    private List<Nir> nirsPoblacion;
    /**
     * Estado con numeración asignada de un proveedor.
     */
    private Estado estadoProveedor;
    /**
     * Nir seleccionado para el detale.
     */
    private Nir nirSeleccionado;
    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     */
    private String numeroRegistros = "5, 10, 15, 20, 25";

    // ///////////////////////////MÉTODOS//////////////////////////
    /**
     * Obtiene los estados y poblaciones de la numeracion asignada del proveedor del numero consultado.
     * @param prestadorNumero Proveedor
     */
    public void getEstadosNumeracion(Proveedor prestadorNumero) {
        try {
            this.setPrestadorNumero(prestadorNumero);
            this.setEstadosNumeracionProveedor(ngPublicService.getEstadosNumeracionByProveedor(prestadorNumero));
            this.setPoblacionesProveedorTabla(false);
            this.setEstadosProveedorTabla(true);
            this.setNirsProveedorTabla(false);
        } catch (Exception e) {
            LOGGER.error("Error inesperado de carga de datos" + e.getMessage());
        }
    }

    /**
     * Convierte la numeración asignada a formato numero USA. Ejemplo: 1,000,000
     * @param num BigDecimal
     * @return String
     */
    public String formatoNumeracionAsignada(BigDecimal num) {
        NumberFormat numFormato;
        String numeroStr = "";
        try {
            numFormato = NumberFormat.getNumberInstance(Locale.US);
            numeroStr = numFormato.format(num);
            this.setNirsProveedorTabla(false);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al dar formato a la numeración asignada" + e.getMessage());
        }
        return numeroStr;
    }

    /**
     * Obtiene los nirs y la numeracion asignada del proveedor del numero consultado.
     * @param proveedor Proveedor
     */
    public void getNirsNumeracion(Proveedor proveedor) {
        try {
            this.setPrestadorNumero(proveedor);
            this.setNirsNumeracionProveedor(ngPublicService.getNirsNumeracionByProveedor(proveedor));

            this.poblacionesProveedorTabla = false;
            this.estadosProveedorTabla = false;
            this.nirsProveedorTabla = true;
        } catch (Exception e) {
            LOGGER.error("Error inesperado de carga de datos" + e.getMessage());
        }
    }

    /**
     * Obtiene la información del las poblaciones por nir y proveedor.
     * @param pst Proveedor
     * @param nir Nir
     */
    public void setInfoNirProveedor(Proveedor pst, Nir nir) {
        try {
            this.setNirSeleccionado(nir);
            this.setPoblacionNumeracionProveedorEstado(ngPublicService.
                    findALLPoblacionesNumeracionByProveedorNir(pst, nir));
        } catch (Exception e) {
            LOGGER.error("Error inesperado de carga de datos" + e.getMessage());
        }
    }

    /**
     * Busca y setea los nirs de una poblacion.
     * @param poblacion Poblacion
     * @return List<Nir>
     */
    public List<Nir> buscaNirsPoblacion(Poblacion poblacion) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil
                    .resetPaginacion("FORM_proveedorPoblacionNir:TBL_poblacionesProveedorNir", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));

            this.setNirsPoblacion(ngPublicService.findAllNirByPoblacion(poblacion));
        } catch (Exception e) {
            LOGGER.error("Error inesperado de carga de datos" + e.getMessage());
        }
        return this.nirsPoblacion;
    }

    /**
     * Obtiene el detalle de una población seleccionada vinculada a un proveedor.
     * @param pst Proveedor
     * @param estado Estado
     */
    public void setInfoPoblacionProveedorEstado(Proveedor pst, Estado estado) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil
                    .resetPaginacion("FORM_proveedorPoblacionEstado:TBL_poblacionesProveedorEstado", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));

            this.setEstadoProveedor(estado);
            this.setPrestadorNumero(pst);
            this.setPoblacionNumeracionProveedorEstado(ngPublicService.findALLPoblacionesNumeracionByProveedorEstado(
                    pst,
                    estado));

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Estado {} y Proveedor {}",
                        pst, estado);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado de carga de datos" + e.getMessage());
        }
    }

    // //////////////////////////////////////////GETTERS Y SETTERS///////////////////////////////////////
    /**
     * Nir seleccionado para el detale.
     * @return the nirSeleccionado
     */
    public Nir getNirSeleccionado() {
        return nirSeleccionado;
    }

    /**
     * Nir seleccionado para el detale.
     * @param nirSeleccionado the nirSeleccionado to set
     */
    public void setNirSeleccionado(Nir nirSeleccionado) {
        this.nirSeleccionado = nirSeleccionado;
    }

    /**
     * Estado con numeración asignada de un proveedor.
     * @return the estadoProveedor
     */
    public Estado getEstadoProveedor() {
        return estadoProveedor;
    }

    /**
     * Estado con numeración asignada de un proveedor.
     * @param estadoProveedor the estadoProveedor to set
     */
    public void setEstadoProveedor(Estado estadoProveedor) {
        this.estadoProveedor = estadoProveedor;
    }

    /**
     * Nirs por poblacion.
     * @return the nirsPoblacion
     */
    public List<Nir> getNirsPoblacion() {
        return nirsPoblacion;
    }

    /**
     * Nirs por poblacion.
     * @param nirsPoblacion the nirsPoblacion to set
     */
    public void setNirsPoblacion(List<Nir> nirsPoblacion) {
        this.nirsPoblacion = nirsPoblacion;
    }

    /**
     * PoblacionNumeracion por proveedor y estado.
     * @return the poblacionNumeracionProveedorEstado
     */
    public List<PoblacionNumeracion> getPoblacionNumeracionProveedorEstado() {
        return poblacionNumeracionProveedorEstado;
    }

    /**
     * PoblacionNumeracion por proveedor y estado.
     * @param poblacionNumeracionProveedorEstado the poblacionNumeracionProveedorEstado to set
     */
    public void setPoblacionNumeracionProveedorEstado(List<PoblacionNumeracion> poblacionNumeracionProveedorEstado) {
        this.poblacionNumeracionProveedorEstado = poblacionNumeracionProveedorEstado;
    }

    /**
     * Lista de nirs y numeracion de un proveedor de servicio.
     * @return the nirsNumeracionProveedor
     */
    public List<NirNumeracion> getNirsNumeracionProveedor() {
        return nirsNumeracionProveedor;
    }

    /**
     * Lista de nirs y numeracion de un proveedor de servicio.
     * @param nirsNumeracionProveedor the nirsNumeracionProveedor to set
     */
    public void setNirsNumeracionProveedor(List<NirNumeracion> nirsNumeracionProveedor) {
        this.nirsNumeracionProveedor = nirsNumeracionProveedor;
    }

    /**
     * Proveedor numero.
     * @return the prestadorNumero
     */
    public Proveedor getPrestadorNumero() {
        return prestadorNumero;
    }

    /**
     * Proveedor numero.
     * @param prestadorNumero the prestadorNumero to set
     */
    public void setPrestadorNumero(Proveedor prestadorNumero) {
        this.prestadorNumero = prestadorNumero;
    }

    /**
     * Lista de poblaciones y numeracion de un proveedor de servicio.
     * @return the estadosNumeracionProveedor
     */
    public List<EstadoNumeracion> getEstadosNumeracionProveedor() {
        return estadosNumeracionProveedor;
    }

    /**
     * Lista de poblaciones y numeracion de un proveedor de servicio.
     * @param estadosNumeracionProveedor the estadosNumeracionProveedor to set
     */
    public void setEstadosNumeracionProveedor(List<EstadoNumeracion> estadosNumeracionProveedor) {
        this.estadosNumeracionProveedor = estadosNumeracionProveedor;
    }

    /**
     * Flag para renderizado de tabla poblaciones donde el proveedor tiene numeración.
     * @return the poblacionesProveedorTabla
     */
    public boolean isPoblacionesProveedorTabla() {
        return poblacionesProveedorTabla;
    }

    /**
     * Flag para renderizado de tabla poblaciones donde el proveedor tiene numeración.
     * @param poblacionesProveedorTabla the poblacionesProveedorTabla to set
     */
    public void setPoblacionesProveedorTabla(boolean poblacionesProveedorTabla) {
        this.poblacionesProveedorTabla = poblacionesProveedorTabla;
    }

    /**
     * Flag para renderizado de tabla estados donde el proveedor tiene numeración.
     * @return the estadosProveedorTabla
     */
    public boolean isEstadosProveedorTabla() {
        return estadosProveedorTabla;
    }

    /**
     * Flag para renderizado de tabla estados donde el proveedor tiene numeración.
     * @param estadosProveedorTabla the estadosProveedorTabla to set
     */
    public void setEstadosProveedorTabla(boolean estadosProveedorTabla) {
        this.estadosProveedorTabla = estadosProveedorTabla;
    }

    /**
     * Flag para renderizado de tabla nirs donde el proveedor tiene numeración.
     * @return the nirsProveedorTabla
     */
    public boolean isNirsProveedorTabla() {
        return nirsProveedorTabla;
    }

    /**
     * Flag para renderizado de tabla nirs donde el proveedor tiene numeración.
     * @param nirsProveedorTabla the nirsProveedorTabla to set
     */
    public void setNirsProveedorTabla(boolean nirsProveedorTabla) {
        this.nirsProveedorTabla = nirsProveedorTabla;
    }

    /**
     * @return the numeroRegistros
     */
    public String getNumeroRegistros() {
        return numeroRegistros;
    }

    /**
     * @param numeroRegistros the numeroRegistros to set
     */
    public void setNumeroRegistros(String numeroRegistros) {
        this.numeroRegistros = numeroRegistros;
    }

}
