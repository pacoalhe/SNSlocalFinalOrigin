package mx.ift.sns.web.frontend.consultas;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.IConfiguracionFacade;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.web.frontend.util.PaginatorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Muestra la tabla de proveedores de una población con numeración asignada dentro del dialog correspondiene.
 * @author X51461MO
 */
@ManagedBean(name = "listaEmpresaTelefoniaBean")
@ViewScoped
public class ListaEmpresaTelefoniaBean implements Serializable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ListaEmpresaTelefoniaBean.class);

    /** Serial ID. */
    private static final long serialVersionUID = 1L;
    /**
     * Lista de prestadores de servicio de una poblacion.
     */
    private List<Proveedor> prestadoresServicioPoblacionNumero;

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Servicio de configuración Publica. */
    @EJB(mappedName = "ConfiguracionFacade")
    private IConfiguracionFacade ngConfiguracionService;

    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     */
    private String numeroRegistros = "5, 10, 15, 20, 25";

    // /////////////////////////////////////MÉTODOS//////////////////////////////////////////////
    /**
     * Setea la lista de proveedores para una PoblacionNumeracion.
     * @param poblacionNumero Poblacion
     */
    public void setPrestadorNumero(Poblacion poblacionNumero) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion("FORM_concesionarioSL:DT_proceedor", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("lista de rows {}", numeroRegistros);
            }
            this.setPrestadoresServicioPoblacionNumero(ngPublicService
                    .findAllPrestadoresServicioBy(null, null, poblacionNumero, null, null));
        } catch (Exception e) {
            LOGGER.error("Error al carga los datos de proveedores" + e.getMessage());
        }
    }

    // /////////////////////////////////////////GETTERS Y SETTERS///////////////////////////////////
    /**
     * Lista de prestadores de servicio de una poblacion.
     * @return the prestadoresServicioPoblacionNumero
     */
    public List<Proveedor> getPrestadoresServicioPoblacionNumero() {
        return this.prestadoresServicioPoblacionNumero;
    }

    /**
     * Lista de prestadores de servicio de una poblacion.
     * @param prestadoresServicioPoblacionNumero the prestadoresServicioPoblacionNumero to set
     */
    public void setPrestadoresServicioPoblacionNumero(List<Proveedor> prestadoresServicioPoblacionNumero) {
        this.prestadoresServicioPoblacionNumero = prestadoresServicioPoblacionNumero;
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
