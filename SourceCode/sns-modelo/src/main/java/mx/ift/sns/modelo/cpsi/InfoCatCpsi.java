package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import mx.ift.sns.modelo.pst.Proveedor;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase encargada de almacenar la información que se va a mostrar en las columnas del catálogo Codigos CPS
 * Internacionales.
 * @author X50880SA
 */
public class InfoCatCpsi implements Serializable {

    /**
     * Serialización.
     */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    private BigDecimal id;

    /** PST. */
    private Proveedor proveedor;

    /** Fecha de cuarentena. */
    private Date fechaFinCuarentena;

    /** Estatus CPSI. */
    private EstatusCPSI estatus;

    /** Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion. */
    private String formatoDecimal;

    /** Representación del código CPSI en binario (con 14 bits). */
    private String binario;

    /** Valor decimal de los 14 bits de cualquier código CPSI. */
    private Integer decimalTotal;

    /** Referencia del código. */
    private String referenciaUit;

    /** Solicitud de Asignación asociada al CPSI Asignado. */
    private SolicitudCpsiUit solicitudUit;

    /** Fecha de asignacion de la solicitud UIT. */
    private Date fechaAsignacion;

    /**
     * Constructor.
     * @param id id CPSI
     * @param proveedor pst por el que filtrar.
     * @param fechaFinCuarentena fin de cuarentena.
     * @param estatus estatus CPSI.
     * @param referenciaUit referencia a UIT.
     * @param solicitudUit solicitud del CPSI a UIT.
     */
    public InfoCatCpsi(BigDecimal id, EstatusCPSI estatus, Proveedor proveedor, Date fechaFinCuarentena,
            String referenciaUit, SolicitudCpsiUit solicitudUit) {
        super();
        this.id = id;
        this.estatus = estatus;
        this.proveedor = proveedor;
        this.referenciaUit = referenciaUit;
        this.solicitudUit = solicitudUit;
        this.fechaFinCuarentena = fechaFinCuarentena;

        if (solicitudUit != null) {
            fechaAsignacion = solicitudUit.getFechaAsignacion();
        } else {
            fechaAsignacion = null;
        }
    }

    // GETTERS & SETTERS
    /**
     * Identificador de CPSI.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador de CPSI.
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Proveedor asociado al CPSI.
     * @return Proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor asociado al CPSI.
     * @param proveedor Proveedor
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Fecha de fin de cuarentena si existe reserva sobre el CPSI.
     * @return Date
     */
    public Date getFechaFinCuarentena() {
        return fechaFinCuarentena;
    }

    /**
     * Fecha de fin de cuarentena si existe reserva sobre el CPSI.
     * @param fechaFinCuarentena Date
     */
    public void setFechaFinCuarentena(Date fechaFinCuarentena) {
        this.fechaFinCuarentena = fechaFinCuarentena;
    }

    /**
     * Estatus del CPSI.
     * @return EstatusCPSI
     */
    public EstatusCPSI getEstatus() {
        return estatus;
    }

    /**
     * Estatus del CPSI.
     * @param estatus EstatusCPSI
     */
    public void setEstatus(EstatusCPSI estatus) {
        this.estatus = estatus;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CPSI = {");
        builder.append("Id: ").append(this.id);
        if (proveedor != null) {
            builder.append(", Pst: ").append(proveedor.getNombre());
        } else {
            builder.append(", Pst: null");
        }
        builder.append("}");
        return builder.toString();
    }

    /**
     * Representación del código CPSI en binario (con 14 bits).
     * @return String codigo binario
     */
    public String getBinario() {
        if (binario == null) {
            // Rellenamos con ceros a la izquierda hasta completar los 14 bits
            binario = StringUtils.leftPad(Integer.toBinaryString(this.id.intValue()), CodigoCPSI.CPSI_MAX_BITS, '0');
        }
        return binario;
    }

    /**
     * Valor decimal de los 14 bits de cualquier código CPSI.
     * @return Integer
     */
    public Integer getDecimalTotal() {
        if (decimalTotal == null) {
            decimalTotal = this.id.intValue();
        }
        return decimalTotal;
    }

    /**
     * Representación decimal de las 3 partes que forman un código CPSI, separadas con un guion.
     * @return String
     */
    public String getFormatoDecimal() {
        if (formatoDecimal == null) {
            String binario = this.getBinario();

            // Id de Región los 3 primers bits (0-3)
            String idRegion = binario.substring(0, CodigoCPSI.ID_REGION_BITS);

            // Id de Red los 8 siguientes bits (3-11)
            String idRed = binario.substring(CodigoCPSI.ID_REGION_BITS,
                    (CodigoCPSI.ID_REGION_BITS + CodigoCPSI.ID_RED_BITS));

            // Ide de codigo CPSI los 3 siguientes bis (11-14)
            String idPunto = binario.substring((CodigoCPSI.ID_REGION_BITS + CodigoCPSI.ID_RED_BITS),
                    (CodigoCPSI.ID_REGION_BITS + CodigoCPSI.ID_RED_BITS + CodigoCPSI.ID_CPSI_BITS));

            // Formato decimal
            StringBuilder sbDecimal = new StringBuilder();
            sbDecimal.append(Integer.parseInt(idRegion, 2)).append("-");
            sbDecimal.append(Integer.parseInt(idRed, 2)).append("-");
            sbDecimal.append(Integer.parseInt(idPunto, 2));

            formatoDecimal = sbDecimal.toString();
        }
        return formatoDecimal;
    }

    /**
     * Referencia del código.
     * @return referenciaUit
     */
    public String getReferenciaUit() {
        return referenciaUit;
    }

    /**
     * Referencia del código.
     * @param referenciaUit referencia UIT
     */
    public void setReferenciaUit(String referenciaUit) {
        this.referenciaUit = referenciaUit;
    }

    /**
     * Solicitud de Asignación asociada al CPSI Asignado.
     * @return solicitud Uit
     */
    public SolicitudCpsiUit getSolicitudUit() {
        return solicitudUit;
    }

    /**
     * Solicitud de Asignación asociada al CPSI Asignado.
     * @return fecha Asignación.
     */
    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    /**
     * @param fechaAsignacion the fechaAsignacion to set
     */
    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

}
