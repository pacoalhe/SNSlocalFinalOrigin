package mx.ift.sns.web.frontend.consultas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

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
 * Bean que controla el formulario de consultas publicas.
 */
@ManagedBean(name = "detalleRepresentanteBean")
@ViewScoped
public class DetalleRepresentanteBean implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetalleRepresentanteBean.class);

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Servicio de configuración Publica. */
    @EJB(mappedName = "ConfiguracionFacade")
    private IConfiguracionFacade ngConfiguracionService;

    /** Prestador del servicio del numero consultado. **/
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
     * Lista de poblaciones y numeracion de un proveedor de servicio.
     */
    private List<PoblacionNumeracion> poblacionesNumeracionProveedor;
    /**
     * Nirs por poblacion.
     */
    private List<Nir> nirsPoblacion;

    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     */
    private String numeroRegistros = "5, 10, 15, 20, 25";

    // /////////////////////////////////////////////MÉTODOS///////////////////////////////////////////////////////////
    /**
     * Obtiene los estados y poblaciones de la numeracion asignada del proveedor del numero consultado.
     * @param prestadorNumero Proveedor
     */
    public void setEstadosNumeracion(Proveedor prestadorNumero) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion("FORM_detalleproveedor:TBL_estadosProveedor", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));

            this.setPrestadorNumero(prestadorNumero);
            this.setEstadosNumeracionProveedor(ngPublicService.getEstadosNumeracionByProveedor(prestadorNumero));
            this.setPoblacionesProveedorTabla(false);
            this.setEstadosProveedorTabla(true);
            this.setNirsProveedorTabla(false);
        } catch (Exception e) {
            LOGGER.error("Error insesperado de carga de datos de proveedore población" + e.getMessage());
        }
    }

    /**
     * Convierte a formato numero USA un número.Ejemplo: 1,000,000.
     * @param num BigDecimal
     * @return String
     */
    public String formatoNumeracionAsignada(BigDecimal num) {
        NumberFormat numFormato;
        String numeroStr = "";
        try {
            numFormato = NumberFormat.getNumberInstance(Locale.US);
            numeroStr = numFormato.format(num);
        } catch (Exception e) {
            LOGGER.error("Error insesperado al dar formato a la numeración asignada" + e.getMessage());
        }
        return numeroStr;
    }

    /**
     * Obtiene los nirs y la numeracion asignada del proveedor del numero consultado.
     * @param proveedor Proveedor
     */
    public void setNirsNumeracion(Proveedor proveedor) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion("FORM_detalleproveedor:TBL_nirsProveedor", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));

            this.setPrestadorNumero(proveedor);
            this.setNirsNumeracionProveedor(ngPublicService.getNirsNumeracionByProveedor(proveedor));
            this.setPoblacionesProveedorTabla(false);
            this.setEstadosProveedorTabla(false);
            this.setNirsProveedorTabla(true);
        } catch (Exception e) {
            LOGGER.error("Error insesperado de carga de datos de nirs y numeración proveedor" + e.getMessage());
        }

    }

    /**
     * Obtiene las poblaciones de un nir ligadas a un proveedor.
     * @param pst Proveedor
     * @param nir Nir
     */
    public void getInfoNirProveedor(Proveedor pst, Nir nir) {
        try {
            this.setPoblacionNumeracionProveedorEstado(ngPublicService.
                    findALLPoblacionesNumeracionByProveedorNir(pst, nir));
            this.setPoblacionesProveedorTabla(true);
            this.setEstadosProveedorTabla(false);
            this.setNirsProveedorTabla(false);
        } catch (Exception e) {
            LOGGER.error("Error insesperado de carga de datos de población" + e.getMessage());
        }
    }

    /**
     * Obtiene las poblaciones y la numeracion asignada del proveedor del numero consultado.
     * @param proveedor Proveedor
     */
    public void setPoblacionesNumeracion(Proveedor proveedor) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion("FORM_detalleproveedor:TBL_poblacionesProveedor", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));

            this.setPrestadorNumero(proveedor);
            this.setPoblacionesNumeracionProveedor(
                    ngPublicService.getPoblacionesNumeracionByProveedor(
                            proveedor));
            this.setPoblacionesProveedorTabla(true);
            this.setEstadosProveedorTabla(false);
            this.setNirsProveedorTabla(false);
        } catch (Exception e) {
            LOGGER.error("Error insesperado de carga de datos de población" + e.getMessage());
        }

    }

    /**
     * Busca y setea los nirs de una poblacion.
     * @param poblacion Poblacion
     * @return List<Nir>
     */
    public List<Nir> buscaNirsPoblacion(Poblacion poblacion) {
        try {
            this.setNirsPoblacion(ngPublicService.findAllNirByPoblacion(poblacion));
        } catch (Exception e) {
            LOGGER.error("Error insesperado de carga de datos de nirs población" + e.getMessage());
        }
        return this.nirsPoblacion;
    }

    // ////////////////////////////////////GETTERS Y SETTERS////////////////////////////////////////

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
     * Lista de poblaciones y numeracion de un proveedor de servicio.
     * @return the poblacionesNumeracionProveedor
     */
    public List<PoblacionNumeracion> getPoblacionesNumeracionProveedor() {
        return poblacionesNumeracionProveedor;
    }

    /**
     * Lista de poblaciones y numeracion de un proveedor de servicio.
     * @param poblacionesNumeracionProveedor the poblacionesNumeracionProveedor to set
     */
    public void setPoblacionesNumeracionProveedor(List<PoblacionNumeracion> poblacionesNumeracionProveedor) {
        this.poblacionesNumeracionProveedor = poblacionesNumeracionProveedor;
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
     * Prestador del servicio del numero consultado.
     * @return the prestadorNumero
     */
    public Proveedor getPrestadorNumero() {
        return prestadorNumero;
    }

    /**
     * Prestador del servicio del numero consultado.
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
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     * @return the numeroRegistros
     */
    public String getNumeroRegistros() {
        return numeroRegistros;
    }

    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     * @param numeroRegistros the numeroRegistros to set
     */
    public void setNumeroRegistros(String numeroRegistros) {
        this.numeroRegistros = numeroRegistros;
    }

}
