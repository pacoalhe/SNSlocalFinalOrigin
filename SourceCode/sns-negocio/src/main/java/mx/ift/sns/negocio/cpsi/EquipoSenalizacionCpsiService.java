package mx.ift.sns.negocio.cpsi;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.cpsi.ICodigoCpsiDao;
import mx.ift.sns.dao.cpsi.IEquipoSenalizacionCpsiDao;
import mx.ift.sns.dao.ng.IAbnDao;
import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.dao.ot.IPoblacionDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.EquipoSenalCpsi;
import mx.ift.sns.modelo.cpsi.EquipoSenalCpsiWarn;
import mx.ift.sns.modelo.cpsi.EstudioEquipoCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaEquipoSenal;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.utils.LocalizacionUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Servicio de Equipos de Señalización.
 */
@Stateless(name = "EquipoSenalizacionCpsiService", mappedName = "EquipoSenalizacionCpsiService")
@Remote(IEquipoSenalizacionCpsiService.class)
public class EquipoSenalizacionCpsiService implements IEquipoSenalizacionCpsiService {

    /** Máximo número de cps decimal. */
    private static final int MAX_CPS = 16383;

    /** Dao de equipos de señalización. */
    @Inject
    private IEquipoSenalizacionCpsiDao equipoSenalCpsiDao;

    /** Dao de códigos cps nacionales. */
    @Inject
    private ICodigoCpsiDao codigoCPSIDao;

    /** DAO de parametros. */
    @Inject
    private IParametroDao parametroDao;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** DAO de abn. */
    @Inject
    private IAbnDao abnDao;

    /** DAO de poblacion. */
    @Inject
    private IPoblacionDao poblacionDao;

    @Override
    public List<EquipoSenalCpsi> findAllEquiposSenal(FiltroBusquedaEquipoSenal pFiltros) throws Exception {
        return equipoSenalCpsiDao.findAllEquiposSenal(pFiltros);
    }

    @Override
    public EquipoSenalCpsi validaEquipoSenal(EquipoSenalCpsi equipoSenal, boolean modoEdicion) {
        if (!validaCps(equipoSenal)) {
            equipoSenal.setErrorValidacion("cpsn.equiposSenalizacion.CPSN.error.cps");
        } else if (!LocalizacionUtil.validarLongitud(equipoSenal.getLongitud())) {
            equipoSenal.setErrorValidacion("cpsn.equiposSenalizacion.CPSN.error.longitud");
        } else if (!LocalizacionUtil.validarLatitud(equipoSenal.getLatitud())) {
            equipoSenal.setErrorValidacion("cpsn.equiposSenalizacion.CPSN.error.latitud");
        } else if (!cpsAsignadoAPst(equipoSenal.getCps(), equipoSenal.getProveedor())) {
            equipoSenal.setErrorValidacion("cpsn.equiposSenalizacion.validacion.cpsNoAsignadoAPst");
        } else if (equipoSenalCpsiDao.existeEquipo(equipoSenal)) {
            equipoSenal.setErrorValidacion("cpsn.equiposSenalizacion.validacion.existe");
        }
        return equipoSenal;
    }

    @Override
    public EquipoSenalCpsi guardar(EquipoSenalCpsi equipo, EquipoSenalCpsi equipoTemp, boolean modoEdicion) {
        // Se hace la Auditoría del registro
        Usuario usuario = usuariosService.getCurrentUser();
        equipo.updateAuditableValues(usuario);

        equipo = equipoSenalCpsiDao.saveOrUpdate(equipo);

        if (modoEdicion) {
            actualizarRestoEquipos(equipo, equipoTemp, modoEdicion, usuario);
        }

        return equipo;
    }

    @Override
    public void eliminarEquipo(EquipoSenalCpsi equipo) {
        equipoSenalCpsiDao.delete(equipo);
    }

    /**
     * Método encargado de actualizar todos los equipos con el mismo nombre, longitud, latitud y pst.
     * @param equipo equipo guardado
     * @param equipoTemp equipo antes del guardado.
     * @param modoEdicion identificador de actualización
     * @param usuario que realiza la operación sobre el equipo
     */
    private void actualizarRestoEquipos(EquipoSenalCpsi equipo, EquipoSenalCpsi equipoTemp, boolean modoEdicion,
            Usuario usuario) {
        List<EquipoSenalCpsi> equipos = equipoSenalCpsiDao.obtenerEquiposAActualizar(equipoTemp);

        for (EquipoSenalCpsi eq : equipos) {
            eq.setAbn(equipo.getAbn());
            eq.setClave(equipo.getClave());
            eq.setLatitud(equipo.getLatitud());
            eq.setLongitud(equipo.getLongitud());
            eq.setNombre(equipo.getNombre());
            eq.setPoblacion(equipo.getPoblacion());
            eq.setProveedor(equipo.getProveedor());
            eq.setTipo(equipo.getTipo());

            // Se hace la Auditoría del registro
            eq.updateAuditableValues(usuario);

            equipoSenalCpsiDao.saveOrUpdate(eq);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] exportarEquipos(FiltroBusquedaEquipoSenal pFiltros) throws Exception {
        List<EquipoSenalCpsi> listado = equipoSenalCpsiDao.findAllEquiposSenalExp(pFiltros);
        ExportConsultaCatalogoEquipoCpsi eesi = new ExportConsultaCatalogoEquipoCpsi(listado);
        ExportarExcel export = new ExportarExcel(parametroDao);
        return export.generarReporteExcel(ExportConsultaCatalogoEquipoCpsi.TXT_EQUIPOS_CPSI, eesi);
    }

    /**
     * Función encargada de comprobar si un código cps esá asignado a un proveedor.
     * @param cps código
     * @param pst proveedor
     * @return si está asignado
     */
    private boolean cpsAsignadoAPst(Integer cps, Proveedor pst) {
        CodigoCPSI codigo = null;
        boolean asignado = false;

        // Se comprueba si el código existe como cps de dicho bloque
        codigo = codigoCPSIDao.getCodigoCpsi(new BigDecimal(cps), pst);
        if (codigo != null) {
            if (codigo.getProveedor() != null && pst.getId().equals(codigo.getProveedor().getId())) {
                asignado = true;
            } else {
                asignado = false;
            }
        }

        return asignado;
    }

    @Override
    public EquipoSenalCpsi getEquipoSenalCpsiEagerLoad(EquipoSenalCpsi equipo) {
        return equipoSenalCpsiDao.getEquipoSenalEagerLoad(equipo);
    }

    @SuppressWarnings("unused")
    @Override
    public List<EstudioEquipoCpsi> estudioEquipo(Proveedor pst) {

        List<EstudioEquipoCpsi> listado = new ArrayList<EstudioEquipoCpsi>();
        EstudioEquipoCpsi equipoSenal = new EstudioEquipoCpsi();

        int valor = 1;

        // Obtenemos todos los códigos CPS Internacionales asignados al proveedor
        List<CodigoCPSI> codigos = codigoCPSIDao.getCodigosAsignadosAProveedor(pst);
        if (codigos.isEmpty()) {
            listado.clear();
            return listado;
        }

        // Obtenemos todos los equipos de señalización asignados al proveedor.
        List<EquipoSenalCpsi> equipos = equipoSenalCpsiDao.getEquiposByProveedor(pst);

        // Recorremos todos los códigos asignados al pst y actualizamos los campos asignados de cada bloque.
        for (CodigoCPSI codigo : codigos) {
            equipoSenal.addNumAsignados(valor);
        }

        // Recorremos los equipos del pst y actualizamos el campo utilizados de cada bloque.
        for (EquipoSenalCpsi equipo : equipos) {
            if (equipo != null) {
                if (equipo.getProveedor() != null && pst.getId().equals(equipo.getProveedor().getId())) {
                    valor = 1;
                    equipoSenal.addNumUtilizados(valor);
                }
            }

        }
        listado.add(equipoSenal);
        return listado;
    }

    @Override
    public DetalleImportacionEquiposCpsi procesarArchivoEquipos(File archivo, Proveedor pst)
            throws Exception {
        CargaEquiposExcel datosExcel = ValidarFicheroEquipos.extraerExcel(archivo);
        int numRegistrosAProcesar = ValidarFicheroEquipos.obtenerNumRegistros(datosExcel);
        int numRegistrosValidos = 0;
        DetalleImportacionEquiposCpsi detalle = null;

        if (numRegistrosAProcesar > 0) {
            // Se inicia la validación de los datos con la cabecera
            if (!ValidarFicheroEquipos.procesarCabecera(datosExcel)) {
                detalle = inicializaDetalle(datosExcel);
                detalle.setMsgError("Error al procesar la cabecera.");
            } else {
                datosExcel = ValidarFicheroEquipos.validarEquipos(datosExcel, pst, abnDao, poblacionDao, codigoCPSIDao);
                numRegistrosValidos = ValidarFicheroEquipos.obtenerNumRegistrosValidos(datosExcel);
                detalle = inicializaDetalle(datosExcel);
                if (numRegistrosValidos > 0) {
                    procesarEquipos(datosExcel, pst);
                } else {
                    detalle.setMsgError("No hay registros válidos para procesar.");
                }
            }
        }

        return detalle;
    }

    /**
     * Método encargado de procesar los equipos válidos.
     * @param equipos a procesar
     * @param pst al que asignar los equipos.
     */
    private void procesarEquipos(CargaEquiposExcel equipos, Proveedor pst) {

        // Se borran los equipos asociados al pst
        equipoSenalCpsiDao.eliminarEquiposByPst(pst);

        // Para cada equipo a procesar, se crea la entidad y se inserta en BBDD.
        for (EquipoCpsiExcel equipoExcel : equipos.getEquiposExcelValidos()) {
            EquipoSenalCpsi equipo = creaEquipoDesdeExcel(equipoExcel, pst);
            equipo.setNombreFichero(equipos.getNombreFichero());
            // Se hace la Auditoría del registro
            equipo.updateAuditableValues(usuariosService.getCurrentUser());

            equipoSenalCpsiDao.saveOrUpdate(equipo);
        }
    }

    /**
     * Método encargado de crear la entidad el equipo de señalización a partir de los datos del excel.
     * @param equipoExcel equipo a convertir
     * @param pst proveedor
     * @return equipo a guardar
     */
    private EquipoSenalCpsi creaEquipoDesdeExcel(EquipoCpsiExcel equipoExcel, Proveedor pst) {
        EquipoSenalCpsi equipo = new EquipoSenalCpsi();

        equipo.setProveedor(pst);
        equipo.setNombre(equipoExcel.getColumnas().get(EquipoCpsiExcel.POS_NOMBRE));
        equipo.setLongitud(LocalizacionUtil.reemplazarGrado(equipoExcel.getColumnas()
                .get(EquipoCpsiExcel.POS_LONGITUD)));
        equipo.setLatitud(LocalizacionUtil.reemplazarGrado(equipoExcel.getColumnas().get(EquipoCpsiExcel.POS_LATITUD)));
        equipo.setCps(Integer.valueOf(equipoExcel.getColumnas().get(EquipoCpsiExcel.POS_CPS)));

        String poblacion = equipoExcel.getColumnas().get(EquipoCpsiExcel.POS_COD_LOCALIDAD);
        String municipio = equipoExcel.getColumnas().get(EquipoCpsiExcel.POS_COD_MUNICIPIO);
        String estado = equipoExcel.getColumnas().get(EquipoCpsiExcel.POS_COD_ESTADO);
        String inegi = StringUtils.leftPad(estado, 2, '0') + StringUtils.leftPad(municipio, 3, '0')
                + StringUtils.leftPad(poblacion, 4, '0');

        Poblacion pob = poblacionDao.getPoblacionByInegi(inegi);
        equipo.setPoblacion(pob);

        Abn asl = abnDao.getAbnById(BigDecimal.valueOf(Long.parseLong(equipoExcel.getColumnas().get(
                EquipoCpsiExcel.POS_ABN))));
        equipo.setAbn(asl);

        equipo.setClave(equipoExcel.getColumnas().get(EquipoCpsiExcel.POS_CLAVE));
        equipo.setTipo(equipoExcel.getColumnas().get(EquipoCpsiExcel.POS_TIPO));

        // Si el equipo tiene algún watning asociado se setean en el listado de warnings
        if (equipoExcel.getErroresValidacion() != null && !equipoExcel.getErroresValidacion().isEmpty()) {
            List<EquipoSenalCpsiWarn> warnings = new ArrayList<EquipoSenalCpsiWarn>();
            for (ErrorRegistro error : equipoExcel.getErroresValidacion()) {
                EquipoSenalCpsiWarn warning = new EquipoSenalCpsiWarn();
                warning.setWarning(error.getDescripcion());
                warning.setEquipoSenalCpsi(equipo);
                warnings.add(warning);
            }

            equipo.setWarnings(warnings);
        }

        return equipo;
    }

    /**
     * Método encargado de inicializar el detalle de la importación.
     * @param equipos de la importación
     * @return detalle inicializado
     */
    private DetalleImportacionEquiposCpsi inicializaDetalle(CargaEquiposExcel equipos) {
        DetalleImportacionEquiposCpsi detalle = new DetalleImportacionEquiposCpsi();

        detalle.setNombreFichero(equipos.getNombreFichero());
        detalle.setNumEquiposLeidos(equipos.getNumEquiposLeidos());
        detalle.setNumEquiposErroneos(equipos.getEquiposExcelErroneos() != null ? equipos.getEquiposExcelErroneos()
                .size() : 0);
        detalle.setNumEquiposProcesados(equipos.getEquiposExcelValidos() != null ? equipos.getEquiposExcelValidos()
                .size() : 0);
        detalle.setErroresValidacion(equipos.getErroresValidacion());

        return detalle;
    }

    /**
     * Método que comprueba que el código cps es correcto.
     * @param equipo a validar
     * @return boolean que indica si es correcto o no el cps
     */
    private boolean validaCps(EquipoSenalCpsi equipo) {

        if (equipo.getCps() == null || equipo.getCps() > MAX_CPS) {
            return false;
        }
        return true;
    }
}
