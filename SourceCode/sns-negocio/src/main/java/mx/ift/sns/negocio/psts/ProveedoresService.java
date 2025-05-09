package mx.ift.sns.negocio.psts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.dao.pst.IContactoDao;
import mx.ift.sns.dao.pst.IProveedorConvenioDao;
import mx.ift.sns.dao.pst.IProveedorDao;
import mx.ift.sns.dao.pst.ITipoModalidadDao;
import mx.ift.sns.dao.pst.ITipoProveedorDao;
import mx.ift.sns.dao.pst.ITipoRedDao;
import mx.ift.sns.dao.usu.IUsuarioDAO;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.filtros.FiltroBusquedaProveedores;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.DetalleProveedor;
import mx.ift.sns.modelo.pst.EstadoConvenio;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoContacto;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.usu.Rol;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.ng.ISeriesService;
import mx.ift.sns.negocio.nng.ISeriesNngService;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Servicio de Proveedores.
 */
@Stateless(name = "ProveedoresService", mappedName = "ProveedoresService")
@Remote(IProveedoresService.class)
public class ProveedoresService implements IProveedoresService {

	/** Logger de la clase . */
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(ProveedoresService.class);

	/** Código de Proveedor. */
	private static final BigDecimal CDG_PST = new BigDecimal(0000);

	/** DAO de Proveedores. */
	@Inject
	private IProveedorDao proveedorDAO;

	/** DAO de Usuario. */
	@Inject
	private IUsuarioDAO usuarioDAO;

	/** DAO de contactos. */
	@Inject
	private IContactoDao contactoDao;

	/** DAO de Convenios de Proveedor. */
	@Inject
	private IProveedorConvenioDao convenioDao;

	/** DAO de Tipo de Proveedor. */
	@Inject
	private ITipoProveedorDao tipoPstDao;

	/** Dao tipo red. */
	@Inject
	private ITipoRedDao tipoRedDao;

	/** Dao tipo modalidad. */
	@Inject
	private ITipoModalidadDao tipoModalidadDao;

	/** Service de Series. */
	@EJB
	private ISeriesService seriesService;

	/** Service de Series Nng. */
	@EJB
	private ISeriesNngService seriesServiceNng;

	/** Servicio de usuarios. */
	@EJB(mappedName = "UsuariosService")
	private IUsuariosService usuariosService;

	/** DAO de parametros. */
	@Inject
	private IParametroDao parametroDao;

	/**********************************************************************************/

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Proveedor> findAllCesionarios(Proveedor pCedente) {
		return proveedorDAO.findAllCesionarios(pCedente);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Proveedor> findAllProveedoresActivos() throws Exception {
		return proveedorDAO.findAllProveedoresActivos();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<DetalleProveedor> findAllProveedoresActivosD() throws Exception {
		return proveedorDAO.findAllProveedoresActivosD();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Proveedor> findAllProveedores() throws Exception {
		return proveedorDAO.findAllProveedores();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Proveedor> findAllProveedoresByServicio(
			TipoProveedor pTipoProveedor, TipoRed pTipoRed,
			BigDecimal pIdSolicitante) {
		return proveedorDAO.findAllProveedoresByServicio(pTipoProveedor,
				pTipoRed, pIdSolicitante);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Proveedor getProveedorByNombre(String pNombre) throws Exception {
		return proveedorDAO.getProveedorByNombre(pNombre);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Proveedor getProveedorById(BigDecimal pIdProveedor) throws Exception {
		return proveedorDAO.getProveedorById(pIdProveedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateProveedor(Proveedor pProveedor) throws Exception {
		// Se hace la Auditoría del registro
		pProveedor.updateAuditableValues(usuariosService.getCurrentUser());
		// Auditamos tambien los contactos, los convenios y el usuario asociados
		auditarDatosAsociados(pProveedor);

		proveedorDAO.saveOrUpdate(pProveedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Contacto> getRepresentantesLegales(String pTipoContacto,
			BigDecimal pIdProveedor) {
		return contactoDao
				.getRepresentantesLegales(pTipoContacto, pIdProveedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Contacto getRepresentanteLegal(BigDecimal pIdContacto)
			throws Exception {
		return contactoDao.getRepresentanteLegalById(pIdContacto);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<ProveedorConvenio> findAllConveniosByProveedor(
			Proveedor pComercializador, TipoRed pTipoRedConvenio) {
		return convenioDao.findAllConveniosByProveedor(pComercializador,
				pTipoRedConvenio);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<ProveedorConvenio> findAllConveniosByProveedor(
			BigDecimal pCodPstComercializador) throws Exception {
		return convenioDao.findAllConveniosByProveedor(pCodPstComercializador);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Proveedor> findAllConcesionariosByComercializador(
			TipoRed tipoRed, Proveedor comercializador) throws Exception {
		return convenioDao.findAllConcesionariosByComercializador(tipoRed,
				comercializador);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ProveedorConvenio getConvenioById(BigDecimal pId) {
		return convenioDao.getConvenioById(pId);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Proveedor> findAllConcesionariosFromConvenios(
			Proveedor pComercializador, TipoRed pTipoRed) {
		return proveedorDAO.findAllConcesionariosFromConvenios(
				pComercializador, pTipoRed);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<TipoProveedor> findAllTiposProveedor() throws Exception {
		return tipoPstDao.findAllTiposProveedor();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public TipoProveedor getTipoProveedorByCdg(String pCdgTipo) {
		return tipoPstDao.getTipoProveedorByCdg(pCdgTipo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Proveedor> findAllConcesionariosByAbn(Abn abn) {
		return proveedorDAO.findAllConcesionariosByAbn(abn);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Proveedor> findAllConcesionariosByPoblacion(Poblacion poblacion) {
		return proveedorDAO.findAllConcesionariosByPoblacion(poblacion);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Proveedor> findAllProveedores(FiltroBusquedaProveedores pFiltros)
			throws Exception {
		return proveedorDAO.findAllProveedores(pFiltros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public int findAllProveedoresCount(FiltroBusquedaProveedores pFiltros)
			throws Exception {
		return proveedorDAO.findAllProveedoresCount(pFiltros);

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Proveedor guardarProveedor(Proveedor proveedor, boolean encriptarPass) {
		String passGenerada = null;
		if (proveedor.getId() == null) {
			proveedor.setCdgPst(CDG_PST);
			Estatus estatus = new Estatus();
			estatus.setCdg(Estatus.ACTIVO);
			proveedor.setEstatus(estatus);

			passGenerada = generaContrasenna(proveedor);
			proveedor.getUsuario().setContrasenna(
					usuariosService.encriptaContrasena(passGenerada));
		} else {
			if (encriptarPass) {
				proveedor.getUsuario().setContrasenna(
						usuariosService.encriptaContrasena(proveedor
								.getUsuario().getContrasenna()));
			}
		}

		proveedor.getUsuario().setNombre(proveedor.getNombreCorto());
		if (proveedor.getUsuario().getUserid() == null
				|| proveedor.getUsuario().getUserid().isEmpty()) {
			proveedor.getUsuario()
					.setUserid(proveedor.getUsuario().getUserid());
		}

		if (proveedor.getUsuario().getRoles() == null
				|| proveedor.getUsuario().getRoles().isEmpty()) {
			Rol rol = new Rol();
			rol.setId(Rol.PST);
			proveedor.getUsuario().setRoles(new ArrayList<Rol>());
			proveedor.getUsuario().getRoles().add(rol);
		}

		for (Contacto cont : proveedor.getContactos()) {
			if (TipoContacto.REPRESENTANTE_LEGAL.equals(cont.getTipoContacto()
					.getCdg())) {
				proveedor.getUsuario().setEmail(cont.getEmail());
				break;
			}
		}

		// Se hace la Auditoría del registro
		proveedor.updateAuditableValues(usuariosService.getCurrentUser());
		// Auditamos tambien los contactos, los convenios y el usuario asociados
		auditarDatosAsociados(proveedor);

		proveedor = proveedorDAO.saveOrUpdate(proveedor);

		proveedor.setPassSinEncriptar(passGenerada != null ? passGenerada
				: null);

		return proveedor;
	}

	/**
	 * Metodo encargado de auditar los contactos, los convenios y el usuario
	 * asociados en caso de que hayan sido. modificados
	 * 
	 * @param proveedor
	 *            proveedor
	 */
	private void auditarDatosAsociados(Proveedor proveedor) {

		// Contactos
		if (proveedor.getContactos() != null
				&& proveedor.getContactos().size() > 0) {
			for (int i = 0; i < proveedor.getContactos().size(); i++) {
				Contacto contactoModificadoEnPantalla = null;
				Contacto contactoEnBaseDatos = null;
				contactoModificadoEnPantalla = proveedor.getContactos().get(i);
				if (contactoModificadoEnPantalla.getId() != null) {
					contactoEnBaseDatos = contactoDao
							.getContactoById(contactoModificadoEnPantalla
									.getId());
				}
				// Cuando se ha hecho un cambio en un contacto o cuando el
				// contacto es nuevo (contactoEnBaseDatos==null)
				// Entonces se hace la auditoría
				if (!contactoModificadoEnPantalla.equals(contactoEnBaseDatos)) {
					contactoModificadoEnPantalla
							.updateAuditableValues(usuariosService
									.getCurrentUser());
					proveedor.getContactos().set(i,
							contactoModificadoEnPantalla);
				}
			}
		}
		// Convenios
		if (proveedor.getConveniosComercializador() != null
				&& proveedor.getConveniosComercializador().size() > 0) {
			for (ProveedorConvenio convenio : proveedor
					.getConveniosComercializador()) {
				ProveedorConvenio convenioModificadoEnPantalla = null;
				ProveedorConvenio convenioEnBaseDatos = null;
				convenioModificadoEnPantalla = convenio;
				if (convenioModificadoEnPantalla.getId() != null) {
					convenioEnBaseDatos = convenioDao
							.getConvenioById(convenioModificadoEnPantalla
									.getId());
				}
				// Cuando se ha hecho un cambio en un convenio o cuando el
				// convenio es nuevo (convenoEnBaseDatos==null)
				// Entonces se hace la auditoría
				if (!convenioModificadoEnPantalla.equals(convenioEnBaseDatos)) {
					convenioModificadoEnPantalla
							.updateAuditableValues(usuariosService
									.getCurrentUser());
					int indice = proveedor.getConveniosComercializador()
							.indexOf(convenio);
					proveedor.getConveniosComercializador().set(indice,
							convenioModificadoEnPantalla);
				}
			}
		}
		// Usuario
		Usuario usuarioModificadoEnPantalla = proveedor.getUsuario();
		Usuario usuarioEnBaseDatos = null;
		if (usuarioModificadoEnPantalla != null
				&& usuarioModificadoEnPantalla.getId() != null) {
			usuarioEnBaseDatos = usuarioDAO.reload(usuarioModificadoEnPantalla);
		}
		// Cuando se ha hecho un cambio en un contacto o cuando el contacto es
		// nuevo (contactoEnBaseDatos==null)
		// Entonces se hace la auditoría
		if (!usuarioModificadoEnPantalla.equals(usuarioEnBaseDatos)) {
			usuarioModificadoEnPantalla.updateAuditableValues(usuariosService
					.getCurrentUser());
			proveedor.setUsuario(usuarioModificadoEnPantalla);
		}

	}

	/**
	 * Función que genera una contraseña para el pst.
	 * 
	 * @param proveedor
	 *            Proveedor
	 * @return String contraseña
	 */
	private String generaContrasenna(Proveedor proveedor) {
		List<String> caracteres = new ArrayList<String>();

		String contrasenna = "";
		String letras = StringUtils.capitalize(RandomStringUtils
				.randomAlphabetic(5));
		String numeros = RandomStringUtils.randomNumeric(3);
		String especial = "@";
		contrasenna = letras + numeros + especial;
		caracteres.addAll(Arrays.asList(contrasenna.split("")));

		Collections.shuffle(caracteres);
		contrasenna = StringUtils.join(caracteres, "");

		return contrasenna;
	}

	@Override
	public byte[] exportarDatosGenerales(FiltroBusquedaProveedores filtro)
			throws Exception {
		filtro.setUsarPaginacion(false);
		List<Proveedor> listado = findAllProveedores(filtro);

		ExportConsultaCatalogoProveedores excca = new ExportConsultaCatalogoProveedores(
				listado,
				ExportConsultaCatalogoProveedores.ID_DATOS_GENERALES_PST);
		ExportarExcel export = new ExportarExcel(parametroDao);
		return export.generarReporteExcel(
				ExportConsultaCatalogoProveedores.TXT_DATOS_GENERALES, excca);
	}

	@Override
	public byte[] exportarDatosContactos(FiltroBusquedaProveedores filtro)
			throws Exception {
		filtro.setUsarPaginacion(false);
		List<Proveedor> listado = findAllProveedores(filtro);

		ExportConsultaCatalogoProveedores excca = new ExportConsultaCatalogoProveedores(
				listado,
				ExportConsultaCatalogoProveedores.ID_DATOS_CONTACTOS_PST);
		ExportarExcel export = new ExportarExcel(parametroDao);
		return export.generarReporteExcel(
				ExportConsultaCatalogoProveedores.TXT_DATOS_CONTACTOS, excca);
	}

	@Override
	public byte[] exportarDatosConvenios(FiltroBusquedaProveedores filtro)
			throws Exception {
		filtro.setUsarPaginacion(false);
		List<Proveedor> listado = findAllProveedores(filtro);

		ExportConsultaCatalogoProveedores excca = new ExportConsultaCatalogoProveedores(
				listado,
				ExportConsultaCatalogoProveedores.ID_DATOS_CONVENIOS_PST);
		ExportarExcel export = new ExportarExcel(parametroDao);
		return export.generarReporteExcel(
				ExportConsultaCatalogoProveedores.TXT_DATOS_CONVENIOS, excca);
	}

	@Override
	public int existeIdo(Proveedor proveedor) {
		if (proveedor.getIdo() == null) {
			return Proveedor.NO_EXISTE;
		}

		String estatus = proveedorDAO.existeIdo(proveedor);
		if (estatus != null) {
			return (Proveedor.EXISTE_ACTIVO == Integer.parseInt(estatus)) ? Proveedor.EXISTE_ACTIVO
					: Proveedor.EXISTE_INACTIVO;
		} else {
			return Proveedor.NO_EXISTE;
		}
	}

	@Override
	public int existeIda(Proveedor proveedor) {
		if (proveedor.getIda() == null) {
			return Proveedor.NO_EXISTE;
		}

		String estatus = proveedorDAO.existeIda(proveedor);
		if (estatus != null) {
			return (Proveedor.EXISTE_ACTIVO == Integer.parseInt(estatus)) ? Proveedor.EXISTE_ACTIVO
					: Proveedor.EXISTE_INACTIVO;
		} else {
			return Proveedor.NO_EXISTE;
		}
	}

	@Override
	public int existeAbc(Proveedor proveedor) {
		if (proveedor.getAbc() == null) {
			return Proveedor.NO_EXISTE;
		}

		String estatus = proveedorDAO.existeAbc(proveedor);
		if (estatus != null) {
			return (Proveedor.EXISTE_ACTIVO == Integer.parseInt(estatus)) ? Proveedor.EXISTE_ACTIVO
					: Proveedor.EXISTE_INACTIVO;
		} else {
			return Proveedor.NO_EXISTE;
		}
	}

	@Override
	public int existeBcd(Proveedor proveedor) {

		if (proveedor.getBcd() == null) {
			return Proveedor.NO_EXISTE;
		}

		String estatus = proveedorDAO.existeBcd(proveedor);
		if (estatus != null) {
			return (Proveedor.EXISTE_ACTIVO == Integer.parseInt(estatus)) ? Proveedor.EXISTE_ACTIVO
					: Proveedor.EXISTE_INACTIVO;
		} else {
			return Proveedor.NO_EXISTE;
		}
	}

	@Override
	public boolean faltaRepresentanteLegal(Proveedor proveedor) {
		if (proveedor.getContactos() != null
				&& !proveedor.getContactos().isEmpty()) {
			for (Contacto cont : proveedor.getContactos()) {
				if (TipoContacto.REPRESENTANTE_LEGAL.equals(cont
						.getTipoContacto().getCdg())) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean existeNombreProveedor(Proveedor proveedor) {
		return proveedorDAO.existeNombreProveedor(proveedor);
	}

	@Override
	public boolean existeNombreCortoProveedor(Proveedor proveedor) {
		return proveedorDAO.existeNombreCortoProveedor(proveedor);
	}

	@Override
	public Proveedor validaProveedor(Proveedor proveedor,
			String tipoPstInicial, String tipoRedInicial, boolean validaUsuario) {

		List<String> erroresValidacion = new ArrayList<String>();

		if (proveedor.getNombre() == null
				|| proveedor.getNombre().trim().isEmpty()) {
			erroresValidacion
					.add("catalogo.proveedores.validacion.error.nombrePst.obligatorio");
			proveedor.setErroresValidacion(erroresValidacion);
			return proveedor;
		} else {
			if (existeNombreProveedor(proveedor)) {
				erroresValidacion
						.add("catalogo.proveedores.validacion.error.nombrePst");
				proveedor.setErroresValidacion(erroresValidacion);
				return proveedor;
			}
		}

		if (proveedor.getNombreCorto() == null
				|| proveedor.getNombreCorto().trim().isEmpty()) {
			erroresValidacion
					.add("catalogo.proveedores.validacion.error.nombreCortoPst.obligatorio");
			proveedor.setErroresValidacion(erroresValidacion);
			return proveedor;
		}
		// else {
		// if (existeNombreCortoProveedor(proveedor)) {
		// erroresValidacion.add("catalogo.proveedores.validacion.error.nombreCortoPst");
		// proveedor.setErroresValidacion(erroresValidacion);
		// return proveedor;
		// }
		// }

		if (proveedor.getUsuario() != null
				&& proveedor.getUsuario().getUserid().trim().isEmpty()) {
			erroresValidacion
					.add("catalogo.proveedores.validacion.error.usuario.obligatorio");
			proveedor.setErroresValidacion(erroresValidacion);
			return proveedor;
		}

		String error = validaTipoPst(proveedor, tipoPstInicial, tipoRedInicial);
		if (error != null) {
			erroresValidacion.add(error);
			proveedor.setErroresValidacion(erroresValidacion);
			return proveedor;
		}

		if (proveedor.getCalle() == null
				|| proveedor.getCalle().trim().isEmpty()) {
			erroresValidacion
					.add("catalogo.proveedores.validacion.error.calle.obligatorio");
			proveedor.setErroresValidacion(erroresValidacion);
			return proveedor;
		}

		if (proveedor.getNumExt() == null
				|| proveedor.getNumExt().trim().isEmpty()) {
			erroresValidacion
					.add("catalogo.proveedores.validacion.error.numExterior.obligatorio");
			proveedor.setErroresValidacion(erroresValidacion);
			return proveedor;
		}

		if (proveedor.getNumInt() == null
				|| proveedor.getNumInt().trim().isEmpty()) {
			erroresValidacion
					.add("catalogo.proveedores.validacion.error.numInterior.obligatorio");
			proveedor.setErroresValidacion(erroresValidacion);
			return proveedor;
		}

		if (proveedor.getColonia() == null
				|| proveedor.getColonia().trim().isEmpty()) {
			erroresValidacion
					.add("catalogo.proveedores.validacion.error.colonia.obligatorio");
			proveedor.setErroresValidacion(erroresValidacion);
			return proveedor;
		}

		if (proveedor.getCiudad() == null
				|| proveedor.getCiudad().trim().isEmpty()) {
			erroresValidacion
					.add("catalogo.proveedores.validacion.error.ciudad.obligatorio");
			proveedor.setErroresValidacion(erroresValidacion);
			return proveedor;
		}

		erroresValidacion = validaContactosProveedor(proveedor);
		if (!erroresValidacion.isEmpty()) {
			proveedor.setErroresValidacion(erroresValidacion);
			return proveedor;
		}

		// Se consulta si existe el usuario
		Boolean existeUsuario = usuariosService.existeUsuario(proveedor
				.getUsuario().getUserid());

		//Si el usuario existe se valida que este asociado 
		//al PST actual
		if(existeUsuario){
			//Si es una PST nuevo y el usuario existe, 
			//pero el pst no posee id, sera error
			//al ingresar un userId existente en BD
			if(proveedor.getId()!=null){
				if (!proveedorDAO.userIdUsedBySamePST(proveedor)) {
					
					erroresValidacion.add("catalogo.proveedores.validacion.error.usuario");
					proveedor.setErroresValidacion(erroresValidacion);
					return proveedor;
				}	
			}
			else
			{
				erroresValidacion.add("catalogo.proveedores.validacion.error.usuario");
				proveedor.setErroresValidacion(erroresValidacion);
				return proveedor;
			}
		}

		if (proveedor.getId() != null) {
			if (!usuariosService.validaContrasenna(proveedor.getUsuario()
					.getContrasenna())) {
				erroresValidacion
						.add("catalogo.proveedores.validacion.error.password");
				proveedor.setErroresValidacion(erroresValidacion);
				return proveedor;
			}
		}

		proveedor.setErroresValidacion(erroresValidacion);

		return proveedor;
	}

	/**
	 * Función que valida que los datos de los contactos sean correctos.
	 * 
	 * @param proveedor
	 *            Proveedor
	 * @return List<String> errores en la validación de los contactos
	 */
	private List<String> validaContactosProveedor(Proveedor proveedor) {
		boolean tieneRL = false;
		List<String> erroresValidacion = new ArrayList<String>();

		if (proveedor.getContactos() != null
				&& !proveedor.getContactos().isEmpty()) {
			for (Contacto cont : proveedor.getContactos()) {
				if (TipoContacto.REPRESENTANTE_LEGAL.equals(cont
						.getTipoContacto().getCdg())) {
					tieneRL = true;
				}

				erroresValidacion = validarContacto(cont);
				if (!erroresValidacion.isEmpty()) {
					return erroresValidacion;
				}
			}

			if (!tieneRL) {
				erroresValidacion
						.add("catalogo.proveedores.validacion.error.representanteLegal");
			}
		} else {
			erroresValidacion
					.add("catalogo.proveedores.validacion.error.representanteLegal");
		}

		return erroresValidacion;
	}

	@Override
	public List<String> validarContacto(Contacto contacto) {
		List<String> erroresContacto = new ArrayList<String>();

		if (contacto.getTipoContacto() == null) {
			erroresContacto
					.add("catalogo.proveedores.validacion.error.tipoContacto");
		}
		if (contacto.getNombre() == null
				|| contacto.getNombre().trim().isEmpty()) {
			erroresContacto
					.add("catalogo.proveedores.validacion.error.nombreContacto");
		}
		if (contacto.getEmail() == null || contacto.getEmail().trim().isEmpty()) {
			erroresContacto
					.add("catalogo.proveedores.validacion.error.correoE");
		} else {
			if (!validaEmail(contacto.getEmail())) {
				erroresContacto
						.add("catalogo.proveedores.validacion.error.email");
			}
		}

		if ((contacto.getTelefono1() == null || contacto.getTelefono1().trim()
				.isEmpty())
				&& (contacto.getTelefono2() == null || contacto.getTelefono2()
						.trim().isEmpty())
				&& (contacto.getTelefono3() == null || contacto.getTelefono3()
						.trim().isEmpty())) {
			erroresContacto
					.add("catalogo.proveedores.validacion.error.numTelfs");
		}

		return erroresContacto;
	}

	/**
	 * Función que valida el formato del email.
	 * 
	 * @param correo
	 *            String
	 * @return boolean validacion
	 */
	private boolean validaEmail(String correo) {
		boolean valido = true;
		try {
			InternetAddress email = new InternetAddress(correo);
			email.validate();
		} catch (AddressException ex) {
			valido = false;
		}
		return valido;
	}

	/**
	 * Función que valida si todos los datos introducidos en el proveedor son
	 * compatibles con el tipo de proveedor seleccionado.
	 * 
	 * @param proveedor
	 *            Proveedor
	 * @param tipoPstInicial
	 *            String
	 * @param tipoRedInicial
	 *            String
	 * @return String
	 */
	private String validaTipoPst(Proveedor proveedor, String tipoPstInicial,
			String tipoRedInicial) {
		String error = null;

		if (TipoProveedor.AMBOS.equals(proveedor.getTipoProveedor().getCdg())) {
			if (TipoProveedor.CONCESIONARIO.equals(tipoPstInicial)) {
				if (!TipoRed.AMBAS.equals(proveedor.getTipoRed().getCdg())) {
					error = "catalogo.proveedores.validacion.error.tipoPst.tipoRed.ambas";
					return error;
				}

				if (TipoRed.AMBAS.equals(tipoRedInicial)) {
					if (proveedor.getConveniosComercializador() != null) {
						for (ProveedorConvenio conv : proveedor
								.getConveniosComercializador()) {
							if (EstadoConvenio.VIGENTE.equals(conv.getEstatus()
									.getCdg())) {
								error = "catalogo.proveedores.validacion.error.tipoPst.convenios.activos";
								return error;
							}
						}
					}
				}

				if (TipoRed.AMBAS.equals(proveedor.getTipoRed().getCdg())) {
					if (!TipoRed.AMBAS.equals(proveedor.getTipoRedOriginal()
							.getCdg())) {
						if (proveedor.getConveniosComercializador() != null) {
							for (ProveedorConvenio conv : proveedor
									.getConveniosComercializador()) {
								if (TipoRed.AMBAS.equals(conv.getTipoRed()
										.getCdg())
										|| proveedor
												.getTipoRedOriginal()
												.getCdg()
												.equals(conv.getTipoRed()
														.getCdg())) {
									error = "catalogo.proveedores.validacion.error.tipoPst.convenios.tipoPst";
									return error;
								}
							}
						}
					}
				}
			} else if (TipoProveedor.COMERCIALIZADORA.equals(tipoPstInicial)) {
				if (!TipoRed.AMBAS.equals(proveedor.getTipoRed().getCdg())) {
					error = "catalogo.proveedores.validacion.error.tipoPst.tipoRed.ambas";
					return error;
				}

				if (!TipoRed.AMBAS.equals(proveedor.getTipoRedOriginal()
						.getCdg())) {
					for (ProveedorConvenio conv : proveedor
							.getConveniosComercializador()) {
						if (!proveedor.getTipoRedOriginal().getCdg()
								.equals(conv.getTipoRed().getCdg())) {
							error = "catalogo.proveedores.validacion.error.tipoPst.convenios.tipoPst";
							return error;
						}
					}
				}
			}
		} else if (TipoProveedor.CONCESIONARIO.equals(proveedor
				.getTipoProveedor().getCdg())) {
			if (TipoProveedor.AMBOS.equals(tipoPstInicial)) {
				if (proveedor.getConveniosComercializador() != null) {
					for (ProveedorConvenio conv : proveedor
							.getConveniosComercializador()) {
						if (EstadoConvenio.VIGENTE.equals(conv.getEstatus()
								.getCdg())) {
							if (seriesService
									.existeNumeracionAsignadaAlPstByConvenio(conv)) {
								error = "catalogo.proveedores.validacion.error.tipoPst.convenios.concesionario";
								return error;
							}
						}
					}
				}
			} else {
				if (proveedor.getConveniosComercializador() != null) {
					for (ProveedorConvenio conv : proveedor
							.getConveniosComercializador()) {
						if (EstadoConvenio.VIGENTE.equals(conv.getEstatus()
								.getCdg())) {
							error = "catalogo.proveedores.validacion.error.convenio.concesionario";
							return error;
						}
					}
				}
			}
		}

		if (TipoProveedor.COMERCIALIZADORA.equals(proveedor.getTipoProveedor()
				.getCdg()) && TipoProveedor.AMBOS.equals(tipoPstInicial)) {
			error = "catalogo.proveedores.validacion.error.tipoPst.change.AZ";
			return error;
		}

		return error;
	}

	@Override
	public Proveedor bajaProveedor(Proveedor proveedor) {

		if (seriesService.existeNumeracionAsignadaAlPst(proveedor)
				|| seriesServiceNng.existeNumeracionAsignadaAlPst(proveedor)) {
			return null;
		} else {
			Estatus estatus = new Estatus();
			estatus.setCdg(Estatus.INACTIVO);
			proveedor.setEstatus(estatus);

			// Se hace la Auditoría del registro
			proveedor.updateAuditableValues(usuariosService.getCurrentUser());

			return proveedorDAO.saveOrUpdate(proveedor);
		}
	}

	@Override
	public Proveedor activarProveedor(Proveedor proveedor) {

		Estatus estatus = new Estatus();
		estatus.setCdg(Estatus.ACTIVO);
		proveedor.setEstatus(estatus);

		// Se hace la Auditoría del registro
		proveedor.updateAuditableValues(usuariosService.getCurrentUser());

		return proveedorDAO.saveOrUpdate(proveedor);
	}

	@Override
	public List<Proveedor> findAllProveedoresByTRed(TipoRed tipoRed)
			throws Exception {
		return proveedorDAO.findAllProveedoresByTRed(tipoRed);
	}

	@Override
	public List<TipoRed> findAllTiposRed() {
		return tipoRedDao.findAllTiposRed();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public TipoRed getTipoRedById(String id) {
		return tipoRedDao.getTipoRedById(id);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<TipoRed> findAllTiposRedValidos() {
		return tipoRedDao.findAllTiposRedValidos();
	}

	@Override
	public List<TipoRed> findAllTiposRedValidos(TipoRed tipoRed) {
		return tipoRedDao.findAllTiposRedValidos(tipoRed);
	}

	@Override
	public List<TipoModalidad> findAllTiposModalidad() {
		return tipoModalidadDao.findAllTiposModalidad();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public TipoModalidad getTipoModalidadById(String idtipoModalidad) {
		return tipoModalidadDao.getTipoModalidadById(idtipoModalidad);
	}

	@Override
	public boolean contactoNoUsado(Contacto contacto) {
		return contactoDao.contactoNoUsado(contacto);
	}

	@Override
	public boolean existeConcesionarioConvenioConBcd(Proveedor proveedor) {

		return convenioDao.existeConcesionarioConvenioConBcd(proveedor);
	}

	@Override
	public List<Proveedor> findAllArrendatariosNng() {

		return proveedorDAO.findAllArrendatariosNng();
	}

	@Override
	public List<Proveedor> findAllConcesionariosByEstado(Estado estado) {
		return proveedorDAO.findAllConcesionariosByEstado(estado);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<Proveedor> findAllConcesionariosByMunicipio(Municipio municipio) {
		return proveedorDAO.findAllConcesionariosByMunicipio(municipio);

	}

	@Override
	public List<Proveedor> findAllConcesonarioConvenioNng(Proveedor proveedor) {
		return convenioDao.findAllConcesonarioConvenioNng(proveedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<Proveedor> findAllArrendatariosByAbc(Proveedor proveedor) {
		return proveedorDAO.findAllArrendatariosByAbc(proveedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<Proveedor> findAllConcesionariosFromConveniosByAbc(
			Proveedor pComercializador) {
		return proveedorDAO
				.findAllConcesionariosFromConveniosByAbc(pComercializador);
	}

	@Override
	public List<Proveedor> getProveedorByIDA(BigDecimal idaProveedor) {
		return proveedorDAO.getProveedorByIDA(idaProveedor);
	}

	@Override
	public List<Proveedor> getProveedorByIDO(BigDecimal idoProveedor) {
		return proveedorDAO.getProveedorByIDO(idoProveedor);

	}

	@Override
	public List<Proveedor> getProveedorByABC(BigDecimal abcProveedor) {
		return proveedorDAO.getProveedorByABC(abcProveedor);
	}

}
