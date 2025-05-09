package mx.ift.sns.web.frontend.consultas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.IConsultaPublicaFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean que controla el formulario de consultas publicas.
 */
@ManagedBean(name = "representanteBean")
@ViewScoped
public class RepresentanteBean implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;
    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RepresentanteBean.class);
    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Prestador del servicio del numero consultado. **/

    private Proveedor prestadorNumero;
    /**
     * Numeración asignada por proveedor.
     */
    private Integer numeracionAsignadaProveedor;

    // ////////////////////////////////MÉTODOS/////////////////////////////////////
    /**
     * Setea el representante legar y su numeración asignada.
     * @param proveedor Proveedor
     */
    public void setDetalleRepresentante(Proveedor proveedor) {
        try {
            this.setPrestadorNumero(proveedor);
            this.setNumeracionAsignadaProveedor(ngPublicService.getTotalNumeracionAginadaProveedor(proveedor
                    .getId()));
        } catch (Exception e) {
            LOGGER.error("Error en la carga de datos del representante legal" + e.getMessage());
        }
    }

    /**
     * Convierte a formato numero USA un número. Ejemplo:100,000
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
            LOGGER.error("Error en la carga de datos del representante legal" + e.getMessage());
        }
        return numeroStr;
    }

    // /////////////////////////////////////////////GETTERS Y SETTERS/////////////////////////////////

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
     * Numeración asignada por proveedor.
     * @return the numeracionAsignadaProveedor
     */
    public Integer getNumeracionAsignadaProveedor() {
        return numeracionAsignadaProveedor;
    }

    /**
     * Numeración asignada por proveedor.
     * @param numeracionAsignadaProveedor the numeracionAsignadaProveedor to set
     */
    public void setNumeracionAsignadaProveedor(Integer numeracionAsignadaProveedor) {
        this.numeracionAsignadaProveedor = numeracionAsignadaProveedor;
    }
}
