package mx.ift.sns.negocio.cpsn;

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

import mx.ift.sns.dao.cpsn.ICodigoCPSNDao;
import mx.ift.sns.dao.cpsn.IEquipoSenalizacionCPSNDao;
import mx.ift.sns.dao.ng.IAbnDao;
import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.dao.ot.IPoblacionDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.cpsn.CPSNUtils;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EquipoSenalCPSN;
import mx.ift.sns.modelo.cpsn.EquipoSenalCPSNWarn;
import mx.ift.sns.modelo.cpsn.EstudioEquipoCPSN;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
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
@Stateless(name = "EquipoSenalizacionCPSNService", mappedName = "EquipoSenalizacionCPSNService")
@Remote(IEquipoSenalizacionCPSNService.class)
public class EquipoSenalizacionCPSNService implements IEquipoSenalizacionCPSNService {

    /** Máximo número de cps decimal. */
    private static final int MAX_CPS = 16383;

    /** Logger. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(EquipoSenalizacionCPSNService.class);

    /** Dao de equipos de señalización. */
    @Inject
    private IEquipoSenalizacionCPSNDao equipoSenalCPSNDao;

    /** Dao de códigos cps nacionales. */
    @Inject
    private ICodigoCPSNDao codigoCPSNDao;

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
    public List<EquipoSenalCPSN> findAllEquiposSenal(FiltroBusquedaEquipoSenal pFiltros) throws Exception {
        return equipoSenalCPSNDao.findAllEquiposSenal(pFiltros);
    }

    @Override
    public EquipoSenalCPSN validaEquipoSenalCPSN(EquipoSenalCPSN equipoSenalCPSN, boolean modoEdicion) {
        if (!validaCps(equipoSenalCPSN)) {
            equipoSenalCPSN.setErrorValidacion("cpsn.equiposSenalizacion.CPSN.error.cps");
        } else if (!LocalizacionUtil.validarLongitud(equipoSenalCPSN.getLongitud())) {
            equipoSenalCPSN.setErrorValidacion("cpsn.equiposSenalizacion.CPSN.error.longitud");
        } else if (!LocalizacionUtil.validarLatitud(equipoSenalCPSN.getLatitud())) {
            equipoSenalCPSN.setErrorValidacion("cpsn.equiposSenalizacion.CPSN.error.latitud");
        } else if (!cpsAsignadoAPst(equipoSenalCPSN.getCps(), equipoSenalCPSN.getProveedor())) {
            equipoSenalCPSN.setErrorValidacion("cpsn.equiposSenalizacion.validacion.cpsNoAsignadoAPst");
        } else if (equipoSenalCPSNDao.existeEquipoCPSN(equipoSenalCPSN)) {
            equipoSenalCPSN.setErrorValidacion("cpsn.equiposSenalizacion.validacion.existe");
        }
        return equipoSenalCPSN;
    }

    @Override
    public EquipoSenalCPSN guardar(EquipoSenalCPSN equipo, EquipoSenalCPSN equipoTemp,
            boolean modoEdicion) {

        // Se hace la Auditoría del registro
        Usuario usuario = usuariosService.getCurrentUser();
        equipo.updateAuditableValues(usuario);

        equipo = equipoSenalCPSNDao.saveOrUpdate(equipo);

        if (modoEdicion) {
            actualizarRestoEquipos(equipo, equipoTemp, usuario);
        }

        return equipo;
    }

    @Override
    public void eliminarEquipo(EquipoSenalCPSN equipo) {
        equipoSenalCPSNDao.eliminarEquipo(equipo);
    }

    /**
     * Método encargado de actualizar todos los equipos con el mismo nombre, longitud, latitud y pst.
     * @param equipo equipo guardado
     * @param equipoTemp equipo antes del guardado.
     * @param usuario que realiza la operación
     */
    private void actualizarRestoEquipos(EquipoSenalCPSN equipo, EquipoSenalCPSN equipoTemp, Usuario usuario) {
        List<EquipoSenalCPSN> equipos = equipoSenalCPSNDao.obtenerEquiposAActualizar(equipoTemp);
        for (EquipoSenalCPSN eq : equipos) {
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

            equipoSenalCPSNDao.saveOrUpdate(eq);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] exportarEquiposCPSN(FiltroBusquedaEquipoSenal pFiltros) throws Exception {
        List<EquipoSenalCPSN> listado = equipoSenalCPSNDao.findAllEquiposSenalExp(pFiltros);
        ExportConsultaCatalogoEquipoCPSN eesn = new ExportConsultaCatalogoEquipoCPSN(listado);
        ExportarExcel export = new ExportarExcel(parametroDao);
        return export.generarReporteExcel(ExportConsultaCatalogoEquipoCPSN.TXT_EQUIPOS_CPSN, eesn);
    }

    @Override
    public boolean cpsAsignadoAPst(Integer cps, Proveedor pst) {
        CodigoCPSN codigo = null;
        boolean asignado = false;
        int cpsBloque = 0;

        // Se obtienen todos los tipos de bloque ordenados desde Individual al 2048
        List<TipoBloqueCPSN> listadoTipoBloqueCPSN = codigoCPSNDao.findAllTiposBloqueCPSN();

        // Para cada tipo de bloque se comprueba si el cps de dicho tipo existe y está asignado
        // al proveedor. Si no es así se continúa con el tipo de bloque superior.
        for (TipoBloqueCPSN tipoBloque : listadoTipoBloqueCPSN) {
            cpsBloque = CPSNUtils.valorMinimoBloque(cps, tipoBloque.getId());

            // Se comprueba si el código existe como cps de dicho bloque
            codigo = codigoCPSNDao.getCpsnByIdBloqueAsignado(cpsBloque, tipoBloque);
            if (codigo != null) {
                if (codigo.getProveedor() != null && pst.getId().equals(codigo.getProveedor().getId())) {
                    asignado = true;
                } else {
                    asignado = false;
                }
                break;
            }
        }

        return asignado;
    }

    @Override
    public List<EstudioEquipoCPSN> estudioEquipoCPSN(Proveedor pst) {
        /**
         * Se crea el listado a mostrar (5 bloques) distribuidos de la siguiente forma para la presentación.
         * <ul>
         * <li>Posición 0 - Bloque 2048</li>
         * <li>Posición 1 - Bloque 128</li>
         * <li>Posición 2 - Bloque 8</li>
         * <li>Posición 3 - Bloque Individual</li>
         * <li>Posición 4 - Bloque Totales</li>
         */
        List<EstudioEquipoCPSN> listado = new ArrayList<EstudioEquipoCPSN>(5);
        for (int i = 0; i < 5; i++) {
            EstudioEquipoCPSN estudio = new EstudioEquipoCPSN();
            estudio.setDescripcion(i);
            listado.add(estudio);
        }

        int valor = 0;

        // Se obtienen todos los tipos de bloque ordenados desde Individual al 2048
        List<TipoBloqueCPSN> listadoTipoBloqueCPSN = codigoCPSNDao.findAllTiposBloqueCPSN();

        // Obtenemos todos los códigos CPS Nacionales asignados al proveedor
        List<CodigoCPSN> codigos = codigoCPSNDao.getCodigosAsignadosAProveedor(pst);
        if (codigos.isEmpty()) {
            listado.clear();
            return listado;
        }

        // Obtenemos todos los equipos de señalización asignados al proveedor.
        List<EquipoSenalCPSN> equipos = equipoSenalCPSNDao.getEquiposCPSNByProveedor(pst);

        // Recorremos todos los códigos asignados al pst y actualizamos los campos asignados de cada bloque.
        for (CodigoCPSN codigo : codigos) {
            if (TipoBloqueCPSN.BLOQUE_2048.equals(codigo.getTipoBloqueCPSN().getId())) {
                valor = 2048;
                listado.get(EstudioEquipoCPSN.POSICION_BLOQUE_2048).addNumAsignados(valor);
            } else if (TipoBloqueCPSN.BLOQUE_128.equals(codigo.getTipoBloqueCPSN().getId())) {
                valor = 128;
                listado.get(EstudioEquipoCPSN.POSICION_BLOQUE_128).addNumAsignados(valor);
            } else if (TipoBloqueCPSN.BLOQUE_8.equals(codigo.getTipoBloqueCPSN().getId())) {
                valor = 8;
                listado.get(EstudioEquipoCPSN.POSICION_BLOQUE_8).addNumAsignados(valor);
            } else if (TipoBloqueCPSN.INDIVIDUAL.equals(codigo.getTipoBloqueCPSN().getId())) {
                valor = 1;
                listado.get(EstudioEquipoCPSN.POSICION_BLOQUE_INDIVIDUAL).addNumAsignados(valor);
            }

            listado.get(EstudioEquipoCPSN.POSICION_BLOQUE_TOTALES).addNumAsignados(valor);
        }

        // Recorremos los equipos del pst y actualizamos el campo utilizados de cada bloque.
        for (EquipoSenalCPSN equipo : equipos) {

            for (TipoBloqueCPSN tipoBloque : listadoTipoBloqueCPSN) {
                int cpsBloque = CPSNUtils.valorMinimoBloque(equipo.getCps(), tipoBloque.getId());

                // Se comprueba si el código existe como cps de dicho bloque
                CodigoCPSN codigo = codigoCPSNDao.getCpsnByIdBloqueAsignado(cpsBloque, tipoBloque);
                if (codigo != null) {
                    if (codigo.getProveedor() != null && pst.getId().equals(codigo.getProveedor().getId())) {
                        valor = 1;
                        if (TipoBloqueCPSN.BLOQUE_2048.equals(codigo.getTipoBloqueCPSN().getId())) {
                            listado.get(EstudioEquipoCPSN.POSICION_BLOQUE_2048).addNumUtilizados(valor);
                        } else if (TipoBloqueCPSN.BLOQUE_128.equals(codigo.getTipoBloqueCPSN().getId())) {
                            listado.get(EstudioEquipoCPSN.POSICION_BLOQUE_128).addNumUtilizados(valor);
                        } else if (TipoBloqueCPSN.BLOQUE_8.equals(codigo.getTipoBloqueCPSN().getId())) {
                            listado.get(EstudioEquipoCPSN.POSICION_BLOQUE_8).addNumUtilizados(valor);
                        } else if (TipoBloqueCPSN.INDIVIDUAL.equals(codigo.getTipoBloqueCPSN().getId())) {
                            listado.get(EstudioEquipoCPSN.POSICION_BLOQUE_INDIVIDUAL).addNumUtilizados(valor);
                        }

                        listado.get(EstudioEquipoCPSN.POSICION_BLOQUE_TOTALES).addNumUtilizados(valor);
                        break;
                    }
                }
            }
        }

        return listado;
    }

    @Override
    public EquipoSenalCPSN getEquipoSenalCPSNEagerLoad(EquipoSenalCPSN equipo) {
        return equipoSenalCPSNDao.getEquipoSenalCPSNEagerLoad(equipo);
    }

    @Override
    public DetalleImportacionEquiposCpsn procesarArchivoEquipos(File archivo, Proveedor pst)
            throws Exception {
        CargaEquiposExcel datosExcel = ValidarFicheroEquipos.extraerExcel(archivo);
        int numRegistrosAProcesar = ValidarFicheroEquipos.obtenerNumRegistros(datosExcel);
        int numRegistrosValidos = 0;
        DetalleImportacionEquiposCpsn detalle = null;

        if (numRegistrosAProcesar > 0) {
            // Se inicia la validación de los datos con la cabecera
            if (!ValidarFicheroEquipos.procesarCabecera(datosExcel)) {
                detalle = inicializaDetalle(datosExcel);
                detalle.setMsgError("Error al procesar la cabecera.");
            } else {
                datosExcel = ValidarFicheroEquipos.validarEquipos(datosExcel, pst, abnDao, poblacionDao, codigoCPSNDao);
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
        equipoSenalCPSNDao.eliminarEquiposByPst(pst);

        // Para cada equipo a procesar, se crea la entidad y se inserta en BBDD.
        for (EquipoCpsnExcel equipoExcel : equipos.getEquiposExcelValidos()) {
            EquipoSenalCPSN equipo = creaEquipoDesdeExcel(equipoExcel, pst);
            equipo.setNombreFichero(equipos.getNombreFichero());
            // Se hace la Auditoría del registro
            equipo.updateAuditableValues(usuariosService.getCurrentUser());
            equipoSenalCPSNDao.saveOrUpdate(equipo);
        }
    }

    /**
     * Método encargado de crear la entidad el equipo de señalización a partir de los datos del excel.
     * @param equipoExcel equipo a convertir
     * @param pst proveedor
     * @return equipo a guardar
     */
    private EquipoSenalCPSN creaEquipoDesdeExcel(EquipoCpsnExcel equipoExcel, Proveedor pst) {
        EquipoSenalCPSN equipo = new EquipoSenalCPSN();

        equipo.setProveedor(pst);
        equipo.setNombre(equipoExcel.getColumnas().get(EquipoCpsnExcel.POS_NOMBRE));
        equipo.setLongitud(LocalizacionUtil.reemplazarGrado(
                equipoExcel.getColumnas().get(EquipoCpsnExcel.POS_LONGITUD)));
        equipo.setLatitud(LocalizacionUtil.reemplazarGrado(equipoExcel.getColumnas().get(EquipoCpsnExcel.POS_LATITUD)));
        equipo.setCps(Integer.valueOf(equipoExcel.getColumnas().get(EquipoCpsnExcel.POS_CPS)));

        String poblacion = equipoExcel.getColumnas().get(EquipoCpsnExcel.POS_COD_LOCALIDAD);
        String municipio = equipoExcel.getColumnas().get(EquipoCpsnExcel.POS_COD_MUNICIPIO);
        String estado = equipoExcel.getColumnas().get(EquipoCpsnExcel.POS_COD_ESTADO);
        String inegi = StringUtils.leftPad(estado, 2, '0') + StringUtils.leftPad(municipio, 3, '0')
                + StringUtils.leftPad(poblacion, 4, '0');

        Poblacion pob = poblacionDao.getPoblacionByInegi(inegi);
        equipo.setPoblacion(pob);

        Abn asl = abnDao.getAbnById(BigDecimal.valueOf(Long.parseLong(equipoExcel.getColumnas().get(
                EquipoCpsnExcel.POS_ABN))));
        equipo.setAbn(asl);

        equipo.setClave(equipoExcel.getColumnas().get(EquipoCpsnExcel.POS_CLAVE));
        equipo.setTipo(equipoExcel.getColumnas().get(EquipoCpsnExcel.POS_TIPO));

        // Si el equipo tiene algún watning asociado se setean en el listado de warnings
        if (equipoExcel.getErroresValidacion() != null && !equipoExcel.getErroresValidacion().isEmpty()) {
            List<EquipoSenalCPSNWarn> warnings = new ArrayList<EquipoSenalCPSNWarn>();
            for (ErrorRegistro error : equipoExcel.getErroresValidacion()) {
                EquipoSenalCPSNWarn warning = new EquipoSenalCPSNWarn();
                warning.setWarning(error.getDescripcion());
                warning.setEquipoSenalCPSN(equipo);
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
    private DetalleImportacionEquiposCpsn inicializaDetalle(CargaEquiposExcel equipos) {
        DetalleImportacionEquiposCpsn detalle = new DetalleImportacionEquiposCpsn();

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
    private boolean validaCps(EquipoSenalCPSN equipo) {

        if (equipo.getCps() == null || equipo.getCps() > MAX_CPS) {
            return false;
        }
        return true;
    }
}
