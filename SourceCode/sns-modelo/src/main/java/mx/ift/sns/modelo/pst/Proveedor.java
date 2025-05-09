package mx.ift.sns.modelo.pst;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.PrivateOwned;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.usu.Auditoria;
import mx.ift.sns.modelo.usu.Usuario;

/**
 * Representa un Proveedor de Servicios de Telefonia (PST).
 */
@Entity
@Table(name = "CAT_PST")
@SequenceGenerator(name = "SEQ_ID_PROVEEDOR", sequenceName = "SEQ_ID_PROVEEDOR", allocationSize = 1)
@NamedQuery(name = "Proveedor.findAll", query = "SELECT p FROM Proveedor p")
public class Proveedor extends Auditoria implements Serializable {

    /** Serialización . */
    private static final long serialVersionUID = 1L;

    /** Constante. */
    public static final int NO_EXISTE = 0;

    /** Constante. */
    public static final int EXISTE_ACTIVO = 1;

    /** Constante. */
    public static final int EXISTE_INACTIVO = 2;

    /** Constante. */
    public static final boolean EXISTE = true;

    /** Constante. */
    public static final boolean N_EXISTE = false;

    /** Constante. */
    public static final int PRINCIPAL = 1;

    /** Constante. */
    public static final int NO_PRINCIPAL = 0;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_PROVEEDOR")
    @Column(name = "ID_PST", unique = true, nullable = false)
    private BigDecimal id;

    /** Código ABC. */
    @Column(name = "ABC", precision = 3)
    private BigDecimal abc;

    /** Código BCD. */
    @Column(name = "BCD", precision = 3)
    private BigDecimal bcd;

    /** Calle. */
    @Column(name = "CALLE", length = 50)
    private String calle;

    /** Código de Proveedor. */
    @Column(name = "CDG_PST", nullable = false, precision = 4)
    private BigDecimal cdgPst;

    /** Ciudad. */
    @Column(name = "CIUDAD", length = 50)
    private String ciudad;

    /** Colonia. */
    @Column(name = "COLONIA", length = 50)
    private String colonia;

    /** Código Postal. */
    @Column(name = "CP", length = 10)
    private String cp;

    /** Código IDA. */
    @Column(name = "IDA", precision = 3)
    private BigDecimal ida;

    /** Código IDO. */
    @Column(name = "IDO", precision = 3)
    private BigDecimal ido;

    /** Nombre. */
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    /** Nombre abreviado. */
    @Column(name = "NOMBRE_CORTO", length = 35)
    private String nombreCorto;

    /** Número exterior. */
    @Column(name = "NUM_EXT", length = 50)
    private String numExt;

    /** Número interior. */
    @Column(name = "NUM_INT", length = 50)
    private String numInt;

    /** Url información del Proveedor. */
    @Column(name = "URL", length = 50)
    private String url;

    /** Proveedor principal. */
    @Column(name = "PRINCIPAL", precision = 1)
    private int principal;

    /** Relación: Un proveedor puede tener muchas centrales asociadas. */
    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
    private List<Central> centrales;

    /** Relación: Un proveedor puede tener muchos contactos. */
    @OneToMany(mappedBy = "proveedor", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @PrivateOwned
    private List<Contacto> contactos;

    /** Relación: Un proveedor puede tener muchos convenios como comercializador. */
    @OneToMany(mappedBy = "proveedorConvenio", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ProveedorConvenio> conveniosComercializador;

    /** Relación: Un proveedor puede tener muchos convenios como concesionario. */
    @OneToMany(mappedBy = "proveedorConcesionario", fetch = FetchType.LAZY)
    private List<ProveedorConvenio> conveniosConcesionario;

    /** Relación: Muchos proveedores pueden pertenecer al mismo estado (OT). */
    @ManyToOne
    @JoinColumn(name = "ID_ESTADO_OT", nullable = false)
    private Estado estado;

    /** Relación: Muchos proveedores pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS", nullable = false)
    private Estatus estatus;

    /** Relación: Muchos proveedores pueden ser del mismo tipo. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_PST", nullable = false)
    private TipoProveedor tipoProveedor;

    /** Relación: Muchos proveedores pueden tener el mismo tipo de red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_RED", nullable = false)
    private TipoRed tipoRed;

    /** Relación: Muchos proveedores pueden tener el mismo tipo de red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_RED_ORIGINAL", nullable = true)
    private TipoRed tipoRedOriginal;

    /** Relación: Muchos proveedores pueden dar el mismo tipo de servicio. */
    @ManyToOne
    @JoinColumn(name = "CDG_TIPO_SERVICIO", nullable = false)
    private TipoServicio tipoServicio;

    /** Relación: Un proveedor puede tener muchos rangos de series reservadas. */
    @OneToMany(mappedBy = "asignatario", fetch = FetchType.LAZY)
    private List<RangoSerie> rangosSeries;

    /** Relación: Un proveedor puede tener muchos solicitudes. */
    @OneToMany(mappedBy = "proveedorSolicitante", fetch = FetchType.LAZY)
    private List<Solicitud> solicitudes;

    /** Usuario del proveedor. */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_USUARIO", nullable = true)
    private Usuario usuario;

    /** Identificador del operador. */
    @Column(name = "ID_OPERADOR", nullable = true)
    private BigDecimal idOperador;

    /** Identificador del operador. */
    @Column(name = "CONSULTA_PUBLICA_SNS", nullable = true)
    private String consultaPublicaSns;

    /** Mensaje de error de la validación del proveedor. */
    @Transient
    private List<String> erroresValidacion;

    /** Password transient con el texto sin encriptar. */
    @Transient
    private String passSinEncriptar;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, por defecto vacío. */
    public Proveedor() {
    }

    // GETTERS & SETTERS

    /**
     * Identificador interno.
     * 
     * @return BigDecimal
     */
    public BigDecimal getId() {
	return id;
    }

    /**
     * Identificador interno.
     * 
     * @param id BigDecimal
     */
    public void setId(BigDecimal id) {
	this.id = id;
    }

    /**
     * Código ABC.
     * 
     * @return BigDecimal
     */
    public BigDecimal getAbc() {
	return abc;
    }

    /**
     * Código ABC.
     * 
     * @param abc BigDecimal
     */
    public void setAbc(BigDecimal abc) {
	this.abc = abc;
    }

    /**
     * Código BCD.
     * 
     * @return BigDecimal
     */
    public BigDecimal getBcd() {
	return bcd;
    }

    /**
     * Código BCD.
     * 
     * @param bcd BigDecimal
     */
    public void setBcd(BigDecimal bcd) {
	this.bcd = bcd;
    }

    /**
     * Calle.
     * 
     * @return String
     */
    public String getCalle() {
	return calle;
    }

    /**
     * Calle.
     * 
     * @param calle String
     */
    public void setCalle(String calle) {
	this.calle = calle;
    }

    /**
     * Código de Proveedor.
     * 
     * @return BigDecimal
     */
    public BigDecimal getCdgPst() {
	return cdgPst;
    }

    /**
     * Código de Proveedor.
     * 
     * @param cdgPst BigDecimal
     */
    public void setCdgPst(BigDecimal cdgPst) {
	this.cdgPst = cdgPst;
    }

    /**
     * Ciudad.
     * 
     * @return String
     */
    public String getCiudad() {
	return ciudad;
    }

    /**
     * Ciudad.
     * 
     * @param ciudad String
     */
    public void setCiudad(String ciudad) {
	this.ciudad = ciudad;
    }

    /**
     * Colonia.
     * 
     * @return String
     */
    public String getColonia() {
	return colonia;
    }

    /**
     * Colonia.
     * 
     * @param colonia String
     */
    public void setColonia(String colonia) {
	this.colonia = colonia;
    }

    /**
     * Código Postal.
     * 
     * @return String
     */
    public String getCp() {
	return cp;
    }

    /**
     * Código Postal.
     * 
     * @param cp String
     */
    public void setCp(String cp) {
	this.cp = cp;
    }

    /**
     * Código IDA.
     * 
     * @return BigDecimal
     */
    public BigDecimal getIda() {
	return ida;
    }

    /**
     * Código IDA.
     * 
     * @param ida BigDecimal
     */
    public void setIda(BigDecimal ida) {
	this.ida = ida;
    }

    /**
     * Código IDO.
     * 
     * @return BigDecimal
     */
    public BigDecimal getIdo() {
	return ido;
    }

    /**
     * Código IDO.
     * 
     * @param ido BigDecimal
     */
    public void setIdo(BigDecimal ido) {
	this.ido = ido;
    }

    /**
     * Nombre.
     * 
     * @return String
     */
    public String getNombre() {
	return nombre;
    }

    /**
     * Nombre.
     * 
     * @param nombre String
     */
    public void setNombre(String nombre) {
	this.nombre = nombre;
    }

    /**
     * Nombre abreviado.
     * 
     * @return String
     */
    public String getNombreCorto() {
	return nombreCorto;
    }

    /**
     * Nombre abreviado.
     * 
     * @param nombreCorto String
     */
    public void setNombreCorto(String nombreCorto) {
	this.nombreCorto = nombreCorto;
    }

    /**
     * Número exterior.
     * 
     * @return String
     */
    public String getNumExt() {
	return numExt;
    }

    /**
     * Número exterior.
     * 
     * @param numExt String
     */
    public void setNumExt(String numExt) {
	this.numExt = numExt;
    }

    /**
     * Número interior.
     * 
     * @return String
     */
    public String getNumInt() {
	return numInt;
    }

    /**
     * Número interior.
     * 
     * @param numInt String
     */
    public void setNumInt(String numInt) {
	this.numInt = numInt;
    }

    /**
     * Url.
     * 
     * @return String
     */
    public String getUrl() {
	return url;
    }

    /**
     * Url.
     * 
     * @param url String
     */
    public void setUrl(String url) {
	this.url = url;
    }

    /**
     * Lista de centrales asociadas al proveedor.
     * 
     * @return Central
     */
    public List<Central> getCentrales() {
	return centrales;
    }

    /**
     * Lista de centrales asociadas al proveedor.
     * 
     * @param centrales Central
     */
    public void setCentrales(List<Central> centrales) {
	this.centrales = centrales;
    }

    /**
     * Asocia una central con el proveedor.
     * 
     * @param pCentral Central
     * @return Central
     */
    public Central addCentral(Central pCentral) {
	this.getCentrales().add(pCentral);
	pCentral.setProveedor(this);
	return pCentral;
    }

    /**
     * Elimina la relación entre la central y el proveedor.
     * 
     * @param pCentral Central
     * @return Central
     */
    public Central removeCentral(Central pCentral) {
	this.getCentrales().add(pCentral);
	pCentral.setProveedor(null);
	return pCentral;
    }

    /**
     * Lista de contactos asociados al proveedor.
     * 
     * @return List
     */
    public List<Contacto> getContactos() {
	return contactos;
    }

    /**
     * Lista de contactos asociados al proveedor.
     * 
     * @param contactos List
     */
    public void setContactos(List<Contacto> contactos) {
	this.contactos = contactos;
    }

    /**
     * Asocia una contacto con el proveedor.
     * 
     * @param pContacto Contacto
     * @return Contacto
     */
    public Contacto addContacto(Contacto pContacto) {
	this.getContactos().add(pContacto);
	pContacto.setProveedor(this);
	return pContacto;
    }

    /**
     * Elimina la relación entre un contacto y el proveedor.
     * 
     * @param pContacto Contacto
     * @return Contacto
     */
    public Contacto removeContacto(Contacto pContacto) {
	this.getContactos().add(pContacto);
	pContacto.setProveedor(null);
	return pContacto;
    }

    /**
     * Lista de convenios como proveedor asociados al proveedor.
     * 
     * @return List List
     */
    public List<ProveedorConvenio> getConveniosComercializador() {
	return conveniosComercializador;
    }

    /**
     * Lista de convenios como proveedor asociados al proveedor.
     * 
     * @param conveniosComercializador List
     */
    public void setConveniosComercializador(List<ProveedorConvenio> conveniosComercializador) {
	this.conveniosComercializador = conveniosComercializador;
    }

    /**
     * Asocia un convenio de comercializador con el proveedor.
     * 
     * @param pProveedorConvenio ProveedorConvenio
     * @return ProveedorConvenio
     */
    public ProveedorConvenio addConvenioComercializador(ProveedorConvenio pProveedorConvenio) {
	this.getConveniosComercializador().add(pProveedorConvenio);
	pProveedorConvenio.setProveedorConvenio(this);
	return pProveedorConvenio;
    }

    /**
     * Elimina la relación entre un convenio de comercializador y el proveedor.
     * 
     * @param pProveedorConvenio ProveedorConvenio
     * @return ProveedorConvenio
     */
    public ProveedorConvenio removeConvenioComercializador(ProveedorConvenio pProveedorConvenio) {
	this.getConveniosComercializador().add(pProveedorConvenio);
	pProveedorConvenio.setProveedorConvenio(null);
	return pProveedorConvenio;
    }

    /**
     * Lista de convenios concesionarios asociada al proveedor.
     * 
     * @return List
     */
    public List<ProveedorConvenio> getConveniosConcesionario() {
	return conveniosConcesionario;
    }

    /**
     * Lista de convenios concesionarios asociada al proveedor.
     * 
     * @param conveniosConcesionario List
     */
    public void setConveniosConcesionario(List<ProveedorConvenio> conveniosConcesionario) {
	this.conveniosConcesionario = conveniosConcesionario;
    }

    /**
     * Asocia un convenio concesionario con el proveedor.
     * 
     * @param pProveedorConvenio ProveedorConvenio
     * @return ProveedorConvenio
     */
    public ProveedorConvenio addConvenioConcesionario(ProveedorConvenio pProveedorConvenio) {
	this.getConveniosConcesionario().add(pProveedorConvenio);
	pProveedorConvenio.setProveedorConcesionario(this);
	return pProveedorConvenio;
    }

    /**
     * Elimina la relación entre un convenio concesionario y el proveedor.
     * 
     * @param pProveedorConvenio ProveedorConvenio
     * @return ProveedorConvenio
     */
    public ProveedorConvenio removeConvenioConcesionario(ProveedorConvenio pProveedorConvenio) {
	this.getConveniosConcesionario().add(pProveedorConvenio);
	pProveedorConvenio.setProveedorConcesionario(null);
	return pProveedorConvenio;
    }

    /**
     * Estado (OT).
     * 
     * @return Estado
     */
    public Estado getEstado() {
	return estado;
    }

    /**
     * Estado (OT).
     * 
     * @param estado Estado
     */
    public void setEstado(Estado estado) {
	this.estado = estado;
    }

    /**
     * Estatus.
     * 
     * @return Estatus
     */
    public Estatus getEstatus() {
	return estatus;
    }

    /**
     * Estatus.
     * 
     * @param estatus the estatus to set
     */
    public void setEstatus(Estatus estatus) {
	this.estatus = estatus;
    }

    /**
     * Tipo de Proveedor de servicios.
     * 
     * @return TipoProveedor
     */
    public TipoProveedor getTipoProveedor() {
	return tipoProveedor;
    }

    /**
     * Tipo de Proveedor de servicios.
     * 
     * @param tipoProveedor TipoProveedor
     */
    public void setTipoProveedor(TipoProveedor tipoProveedor) {
	this.tipoProveedor = tipoProveedor;
    }

    /**
     * Tipo de red asociada al proveedor.
     * 
     * @return TipoRed
     */
    public TipoRed getTipoRed() {
	return tipoRed;
    }

    /**
     * Tipo de red asociada al proveedor.
     * 
     * @param tipoRed TipoRed
     */
    public void setTipoRed(TipoRed tipoRed) {
	this.tipoRed = tipoRed;
    }

    /**
     * Tipo de red original asociada al proveedor.
     * 
     * @return TipoRed
     */
    public TipoRed getTipoRedOriginal() {
	return tipoRedOriginal;
    }

    /**
     * Tipo de red original asociada al proveedor.
     * 
     * @param tipoRedOriginal TipoRed
     */
    public void setTipoRedOriginal(TipoRed tipoRedOriginal) {
	this.tipoRedOriginal = tipoRedOriginal;
    }

    /**
     * Tipo de servicio prestado.
     * 
     * @return TipoServicio
     */
    public TipoServicio getTipoServicio() {
	return tipoServicio;
    }

    /**
     * Tipo de servicio prestado.
     * 
     * @param tipoServicio TipoServicio
     */
    public void setTipoServicio(TipoServicio tipoServicio) {
	this.tipoServicio = tipoServicio;
    }

    /**
     * @return the consultaPublicaSns
     */
    public String getConsultaPublicaSns() {
	return consultaPublicaSns;
    }

    /**
     * @param consultaPublicaSns the consultaPublicaSns to set
     */
    public void setConsultaPublicaSns(String consultaPublicaSns) {
	this.consultaPublicaSns = consultaPublicaSns;
    }

    /**
     * Lista de rangos de series reservados al proveedor.
     * 
     * @return List
     */
    public List<RangoSerie> getRangosSeries() {
	return rangosSeries;
    }

    /**
     * Lista de rangos de series reservados al proveedor.
     * 
     * @param rangosSeries List
     */
    public void setRangosSeries(List<RangoSerie> rangosSeries) {
	this.rangosSeries = rangosSeries;
    }

    /**
     * Asocia la serie de un rango al proveedor.
     * 
     * @param pRangoSerie RangoSerie
     * @return RangoSerie
     */
    public RangoSerie addRangosSerie(RangoSerie pRangoSerie) {
	this.getRangosSeries().add(pRangoSerie);
	pRangoSerie.setAsignatario(this);
	return pRangoSerie;
    }

    /**
     * Elimina la relación entre un rango de serie y el proveedor.
     * 
     * @param pRangoSerie RangoSerie
     * @return RangoSerie
     */
    public RangoSerie removeRangosSerie(RangoSerie pRangoSerie) {
	this.getRangosSeries().remove(pRangoSerie);
	pRangoSerie.setAsignatario(null);
	return pRangoSerie;
    }

    /**
     * Lista de solicitudes del proveedor.
     * 
     * @return List
     */
    public List<Solicitud> getSolicitudes() {
	return solicitudes;
    }

    /**
     * Lista de solicitudes del proveedor.
     * 
     * @param solicitudes List
     */
    public void setSolicitudes(List<Solicitud> solicitudes) {
	this.solicitudes = solicitudes;
    }

    /**
     * Asocia una solicitud con el proveedor.
     * 
     * @param pSolicitud Solicitud
     * @return Solicitud
     */
    public Solicitud addSolicitud(Solicitud pSolicitud) {
	this.getSolicitudes().add(pSolicitud);
	pSolicitud.setProveedorSolicitante(this);
	return pSolicitud;
    }

    /**
     * Elimina la relación entre una solicitud y el proveedor.
     * 
     * @param pSolicitud Solicitud
     * @return Solicitud
     */
    public Solicitud removeSolicitud(Solicitud pSolicitud) {
	this.getSolicitudes().remove(pSolicitud);
	pSolicitud.setProveedorSolicitante(null);
	return pSolicitud;
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
	return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
	this.usuario = usuario;
    }

    @Override
    public boolean equals(Object other) {
	return (other instanceof Proveedor) && (id != null) ? id.intValue() == ((Proveedor) other).getId().intValue()
		: (other == this);
    }

    @Override
    public int hashCode() {
	return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("pst={id=");
	builder.append(id);

	builder.append(" nombreCorto='");
	builder.append(nombreCorto);
	builder.append("'");

	builder.append(" nombre='");
	builder.append(nombre);
	builder.append("' ");

	builder.append("}");

	return builder.toString();
    }

    /**
     * @return the erroresValidacion
     */
    public List<String> getErroresValidacion() {
	return erroresValidacion;
    }

    /**
     * @param erroresValidacion the erroresValidacion to set
     */
    public void setErroresValidacion(List<String> erroresValidacion) {
	this.erroresValidacion = erroresValidacion;
    }

    /**
     * @return Contacto representante legal.
     */
    public Contacto getRepresentanteLegal() {
	for (Contacto i : contactos) {
	    if (i.getTipoContacto().getCdg().equals("RL")) {
		return i;
	    }
	}
	return null;
    }

    /**
     * @return the idOperador
     */
    public BigDecimal getIdOperador() {
	return idOperador;
    }

    /**
     * @param idOperador the idOperador to set
     */
    public void setIdOperador(BigDecimal idOperador) {
	this.idOperador = idOperador;
    }

    /**
     * Obtiene el ida con 0 delante.
     * 
     * @return ida
     */
    public String getIdaAsString() {
	return String.format("%03d", this.ida.intValue());
    }

    /**
     * Obtiene el ido con 0 delante.
     * 
     * @return ido
     */
    public String getIdoAsString() {
	return String.format("%03d", this.ido.intValue());
    }

    /**
     * Obtiene el abc con 0 delante.
     * 
     * @return abc
     */
    public String getAbcAsString() {
	return String.format("%03d", this.abc.intValue());
    }

    /**
     * Obtiene el bcd con 0 delante.
     * 
     * @return bcd
     */
    public String getBcdAsString() {
	return String.format("%03d", this.bcd.intValue());
    }

    /**
     * Obtiene la password sin encriptar.
     * 
     * @return the passSinEncriptar
     */
    public String getPassSinEncriptar() {
	return passSinEncriptar;
    }

    /**
     * Setea la password sin encriptar.
     * 
     * @param passSinEncriptar the passSinEncriptar to set
     */
    public void setPassSinEncriptar(String passSinEncriptar) {
	this.passSinEncriptar = passSinEncriptar;
    }

    /**
     * Proveedor principal.
     * 
     * @return the princpal
     */
    public Integer getPrincipal() {
	return principal;
    }

    /**
     * Proveedor principal.
     * 
     * @param principal the princpal to set
     */
    public void setPrincpal(Integer principal) {
	this.principal = principal;
    }

}
