package mx.ift.sns.negocio.cpsn;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.cpsn.ICodigoCPSNDao;
import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.modelo.cpsn.CPSNUtils;
import mx.ift.sns.modelo.cpsn.CesionSolicitadaCPSN;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.LiberacionSolicitadaCpsn;
import mx.ift.sns.modelo.cpsn.NumeracionAsignadaCpsn;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.cpsn.VEstudioCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSN;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;

/**
 * Servicio de Codigos CPS Nacionales.
 */
@Stateless(name = "CodigoCPSNService", mappedName = "CodigoCPSNService")
@Remote(ICodigoCPSNService.class)
public class CodigoCPSNService implements ICodigoCPSNService {

    /** Logger de la clase . */
    // private static final Logger LOGGER = LoggerFactory.getLogger(CodigoCPSNService.class);

    /** Dao de equipos de señalización. */
    @Inject
    private ICodigoCPSNDao codigoCPSNDao;

    /** DAO de parametros. */
    @Inject
    private IParametroDao parametroDao;

    /** Servicio de Bitácora. */
    @EJB(mappedName = "BitacoraService")
    private IBitacoraService bitacoraService;

    /** Servicio de liberacion cpsn. */
    @EJB
    private ISolicitudesCpsnService solicitudesService;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /********************************************************************************************/

    @Override
    public TipoBloqueCPSN getTipoBloqueCPSNById(String id) throws Exception {
        return codigoCPSNDao.getTipoBloqueCPSNById(id);
    }

    @Override
    public EstatusCPSN getEstatusCPSNById(String id) throws Exception {
        return codigoCPSNDao.getEstatusCPSNById(id);
    }

    @Override
    public List<TipoBloqueCPSN> findAllTiposBloqueCPSN() {
        return codigoCPSNDao.findAllTiposBloqueCPSN();
    }

    @Override
    public List<EstatusCPSN> findAllEstatusCPSN() {
        return codigoCPSNDao.findAllEstatusCPSN();
    }

    @Override
    public List<CodigoCPSN> findCodigosCPSN(FiltroBusquedaCodigosCPSN pFiltro) {
        return codigoCPSNDao.findCodigosCPSN(pFiltro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] exportarCodigosCPSN(FiltroBusquedaCodigosCPSN pFiltros) throws Exception {
        List<CodigoCPSN> listado = codigoCPSNDao.findCodigosCPSN(pFiltros);
        ExportConsultaCatalogoCodigosCPSN eccn = new ExportConsultaCatalogoCodigosCPSN(listado);
        ExportarExcel export = new ExportarExcel(parametroDao);
        return export.generarReporteExcel(ExportConsultaCatalogoCodigosCPSN.TXT_CODIGOS_CPSN, eccn);
    }

    @Override
    public String liberarCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados) {
        String error = validarEstado(codigosCPSNSeleccionados, "liberar", EstatusCPSN.RESERVADO);
        if (error != null) {
            return error;
        }
        EstatusCPSN estatusCPSN = new EstatusCPSN();
        estatusCPSN.setId(EstatusCPSN.LIBRE);
        for (CodigoCPSN codigo : codigosCPSNSeleccionados) {
            codigo.setEstatusCPSN(estatusCPSN);
            this.saveCodigoCpsn(codigo);
        }

        return null;
    }

    @Override
    public String reservarCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados) {
        String error = validarEstado(codigosCPSNSeleccionados, "reservar", EstatusCPSN.LIBRE);
        if (error != null) {
            return error;
        }
        EstatusCPSN estatusCPSN = new EstatusCPSN();
        estatusCPSN.setId(EstatusCPSN.RESERVADO);
        for (CodigoCPSN codigo : codigosCPSNSeleccionados) {
            codigo.setEstatusCPSN(estatusCPSN);
            this.saveCodigoCpsn(codigo);
        }

        return null;
    }

    /**
     * Método encargado de validar que todos los códigos seleccionados tenga el estatus correcto.
     * @param codigosCPSNSeleccionados listado de códigos
     * @param accion acción a realizar sobre los registros
     * @param estatus a validar
     * @return error si existe
     */
    private String validarEstado(List<CodigoCPSN> codigosCPSNSeleccionados, String accion, String estatus) {
        String error = null;

        if (codigosCPSNSeleccionados == null || codigosCPSNSeleccionados.isEmpty()) {
            error = "cpsn.codigos.CPSN.seleccion.error";
        } else {
            for (CodigoCPSN codigo : codigosCPSNSeleccionados) {
                if (!estatus.equals(codigo.getEstatusCPSN().getId())) {
                    error = "cpsn.codigos.CPSN." + accion + ".error";
                }
            }
        }

        return error;
    }

    @Override
    public String validarDesagrupacionCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados) {
        String error = validarDesagrupacion(codigosCPSNSeleccionados);
        return error;
    }

    @Override
    public String desagruparCodigoCPSN(List<CodigoCPSN> codigosCPSNSeleccionados) {
        String error = validarDesagrupacion(codigosCPSNSeleccionados);
        if (error != null && !error.isEmpty()) {
            return error;
        }

        // Actualizamos el registro al bloque inferior.
        CodigoCPSN codigo = codigosCPSNSeleccionados.get(0);
        generarCodigosDesagrupados(codigo);

        return null;
    }

    @Override
    public String agruparCodigoCPSN(List<CodigoCPSN> codigosCPSNSeleccionados) {
        String error = validarAgrupacion(codigosCPSNSeleccionados);
        if (error != null && !error.isEmpty()) {
            return error;
        }

        CodigoCPSN codigo = codigosCPSNSeleccionados.get(0);
        generarCodigoAgrupado(codigo);

        return null;
    }

    /**
     * Método que valida que el código seleccionado se puede desagrupar.
     * @param codigosCPSNSeleccionados listado con el código a desagrupar
     * @return error si existe
     */
    private String validarDesagrupacion(List<CodigoCPSN> codigosCPSNSeleccionados) {
        String error = "";

        if (codigosCPSNSeleccionados == null || codigosCPSNSeleccionados.isEmpty()) {
            error = "cpsn.codigos.CPSN.seleccion.error";
        } else if (codigosCPSNSeleccionados.size() > 1) {
            error = "cpsn.codigos.CPSN.seleccion.desagrupar.error";
        } else {
            CodigoCPSN codigo = codigosCPSNSeleccionados.get(0);

            if (TipoBloqueCPSN.INDIVIDUAL.equals(codigo.getTipoBloqueCPSN().getId())) {
                error = "cpsn.codigos.CPSN.seleccion.desagrupar.individual.error";
            } else {
                // Validamos que el código no esté en cuarentena
                Date fechaHoy = FechasUtils.getFechaHoy();
                Date fechaCuarentena = (codigo.getFechaCuarentena() != null)
                        ? FechasUtils.parseFecha(codigo.getFechaCuarentena()) : null;

                if (fechaCuarentena != null && fechaCuarentena.after(fechaHoy)) {
                    error = "cpsn.codigos.CPSN.seleccion.desagrupar.cuarentena.error";
                } else {
                    // Validar que el código no se encuentre en ninguna solicitud de cesión
                    // o liberación pendiente de tramitar
                    if (solicitudesService.cpsnEnTramitePendiente(codigo.getId().intValue(),
                            codigo.getId().intValue())) {
                        error = "cpsn.codigos.CPSN.seleccion.desagrupar.enTramite";
                    } else {
                        error = "";
                    }
                }
            }
        }

        return error;
    }

    /**
     * Método que valida que el código seleccionado se puede agrupar.
     * @param codigosCPSNSeleccionados listado con el código a agrupar
     * @return error si existe
     */
    private String validarAgrupacion(List<CodigoCPSN> codigosCPSNSeleccionados) {
        String error = "";

        if (codigosCPSNSeleccionados == null || codigosCPSNSeleccionados.isEmpty()) {
            error = "cpsn.codigos.CPSN.seleccion.error";
        } else if (codigosCPSNSeleccionados.size() > 1) {
            error = "cpsn.codigos.CPSN.seleccion.agrupar.error";
        } else {
            CodigoCPSN codigo = codigosCPSNSeleccionados.get(0);

            if (TipoBloqueCPSN.BLOQUE_2048.equals(codigo.getTipoBloqueCPSN().getId())) {
                error = "cpsn.codigos.CPSN.seleccion.agrupar.bloque2048.error";
            } else {
                // Validamos que todos los códigos del bloque a agrupar estén libres o reservados.
                String codBloque = codigo.getBinarioAgrupado();
                int numMin = CPSNUtils.valorMinBloque(codBloque);
                int numMax = CPSNUtils.valorMaxBloque(codBloque);

                if (!codigoCPSNDao.permitirAgrupar(numMin, numMax, codigo.getProveedor())) {
                    error = "cpsn.codigos.CPSN.seleccion.agrupar.estatus.error";
                } else {
                    // Validar que el código no se encuentre en ninguna solicitud de cesión
                    // o liberación pendiente de tramitar
                    if (solicitudesService.cpsnEnTramitePendiente(numMin, numMax)) {
                        error = "cpsn.codigos.CPSN.seleccion.agrupar.enTramite";
                    } else {
                        error = "";
                    }
                }
            }
        }

        return error;
    }

    /**
     * Función encargada de crear los codigos CPS Nacionales a partir del agrupado.
     * @param codigo q se desagrupa
     */
    private void generarCodigosDesagrupados(CodigoCPSN codigo) {
        int numCodigos = 0;
        int numMin = 0;
        int numMax = 0;
        int tamBloque = 0;
        TipoBloqueCPSN tipoBloque = new TipoBloqueCPSN();
        String estatus = null;

        if (TipoBloqueCPSN.BLOQUE_2048.equals(codigo.getTipoBloqueCPSN().getId())) {
            numCodigos = TipoBloqueCPSN.NUM_REG_2048;
            tipoBloque.setId(TipoBloqueCPSN.BLOQUE_128);
            tamBloque = 128;
        } else if (TipoBloqueCPSN.BLOQUE_128.equals(codigo.getTipoBloqueCPSN().getId())) {
            numCodigos = TipoBloqueCPSN.NUM_REG_128;
            tipoBloque.setId(TipoBloqueCPSN.BLOQUE_8);
            tamBloque = 8;
        } else if (TipoBloqueCPSN.BLOQUE_8.equals(codigo.getTipoBloqueCPSN().getId())) {
            numCodigos = TipoBloqueCPSN.NUM_REG_8;
            tipoBloque.setId(TipoBloqueCPSN.INDIVIDUAL);
            tamBloque = 1;
        }

        numMin = codigo.getId().intValue();
        numMax = numMin + ((numCodigos - 1) * tamBloque);

        if (EstatusCPSN.LIBRE.equals(codigo.getEstatusCPSN().getId())
                || EstatusCPSN.RESERVADO.equals(codigo.getEstatusCPSN().getId())) {
            estatus = EstatusCPSN.LIBRE;
        }

        for (int i = numMax; i >= numMin; i = i - tamBloque) {
            CodigoCPSN codigoCPSN = null;

            // Si el código a generar es el mínimo, ya existirá en bbdd por lo que se
            // obtiene y modifica el tipo para evitar el error por el versionado
            if (i == numMin) {
                codigoCPSN = codigoCPSNDao.getCodigoCPSNById(String.valueOf(numMin));
            } else {
                codigoCPSN = new CodigoCPSN();
                codigoCPSN.setId(BigDecimal.valueOf(i));
                codigoCPSN.setProveedor(codigo.getProveedor());
            }

            codigoCPSN.setTipoBloqueCPSN(tipoBloque);

            if (estatus == null) {
                codigoCPSN.setEstatusCPSN(codigo.getEstatusCPSN());
            } else {
                if (EstatusCPSN.LIBRE.equals(estatus)) {
                    codigoCPSN.setEstatusCPSN(new EstatusCPSN(EstatusCPSN.LIBRE));
                    estatus = EstatusCPSN.RESERVADO;
                } else {
                    codigoCPSN.setEstatusCPSN(new EstatusCPSN(EstatusCPSN.RESERVADO));
                    estatus = EstatusCPSN.LIBRE;
                }
            }
            this.saveCodigoCpsn(codigoCPSN);
        }

    }

    /**
     * Función encargada de crear el código CPS Nacional a partir del desagrupado eliminando todos los códigos asociados
     * al bloque a generar.
     * @param codigo q se agrupa
     */
    private void generarCodigoAgrupado(CodigoCPSN codigo) {
        int numMin = 0;
        int numMax = 0;
        boolean generarMinimo = true;
        TipoBloqueCPSN tipoBloque = new TipoBloqueCPSN();
        String codBloque = codigo.getBinarioAgrupado();

        numMin = CPSNUtils.valorMinBloque(codBloque);
        numMax = CPSNUtils.valorMaxBloque(codBloque);

        if (TipoBloqueCPSN.BLOQUE_128.equals(codigo.getTipoBloqueCPSN().getId())) {
            tipoBloque.setId(TipoBloqueCPSN.BLOQUE_2048);
        } else if (TipoBloqueCPSN.BLOQUE_8.equals(codigo.getTipoBloqueCPSN().getId())) {
            tipoBloque.setId(TipoBloqueCPSN.BLOQUE_128);
        } else if (TipoBloqueCPSN.INDIVIDUAL.equals(codigo.getTipoBloqueCPSN().getId())) {
            tipoBloque.setId(TipoBloqueCPSN.BLOQUE_8);
        }

        // Se eliminan todos los códigos dentro del bloque a crear.
        List<CodigoCPSN> codigosAEliminar = codigoCPSNDao.getCodigosAAgrupar(numMin, numMax);
        for (CodigoCPSN cod : codigosAEliminar) {
            // Si es el mínimo valor del bloque se actualiza como el código agrupado
            if (cod.getId().intValue() == numMin) {
                cod.setEstatusCPSN(new EstatusCPSN(EstatusCPSN.LIBRE));
                cod.setTipoBloqueCPSN(tipoBloque);
                cod.setFechaCuarentena(null);
                this.saveCodigoCpsn(cod);
                generarMinimo = false;
            } else {
                this.removeCodigoCpsn(cod);
            }
        }

        if (generarMinimo) {
            CodigoCPSN cod = new CodigoCPSN();
            cod.setId(BigDecimal.valueOf(numMin));
            cod.setEstatusCPSN(new EstatusCPSN(EstatusCPSN.LIBRE));
            cod.setProveedor(codigo.getProveedor());
            cod.setTipoBloqueCPSN(tipoBloque);
            this.saveCodigoCpsn(cod);
        }
    }

    @Override
    public String liberarCpsn(LiberacionSolicitadaCpsn pLibSol, boolean pInmediata) throws Exception {
        // StringBuilder sbTraza = new StringBuilder();
        // sbTraza.append("Liberación de Código CPSN.");
        // sbTraza.append(", Solicitud: ").append(pLibSol.getSolicitudLiberacion().getId());
        // sbTraza.append(", CPSN: ").append(pLibSol.getIdCpsn());
        // sbTraza.append(", Fecha Implementación: ");
        // sbTraza.append(FechasUtils.fechaToString(pLibSol.getFechaImplementacion()));
        // sbTraza.append(" (ejecución planificador: ");
        // sbTraza.append(FechasUtils.fechaToString(
        // FechasUtils.getFechaImplementacionReal(pLibSol.getFechaImplementacion()), "dd/MM/yyyy HH:mm:ss"));
        // sbTraza.append("), Fecha Fin Cuarentena: ");
        // sbTraza.append(FechasUtils.fechaToString(pLibSol.getFechaFinCuarentena()));
        // sbTraza.append(" (ejecución planificador: ");
        // sbTraza.append(FechasUtils.fechaToString(
        // FechasUtils.getFechaImplementacionReal(pLibSol.getFechaFinCuarentena()), "dd/MM/yyyy HH:mm:ss"));
        // sbTraza.append("), Ejecución Inmediata: ").append(pInmediata);

        // Tenemos en cuenta los minutos.
        Date fHoy = new Date();
        boolean fFinReservaCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(pLibSol.getFechaFinCuarentena())) >= 0);

        boolean fImplementacionCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(pLibSol.getFechaImplementacion())) >= 0);

        // sbTraza.append(", Fecha Implementación Cumplida: ").append(fImplementacionCumplida);
        // sbTraza.append(", Fecha Fin Cuarentena Cumplida: ").append(fFinReservaCumplida);
        // LOGGER.info(sbTraza.toString());
        // bitacoraService.add(pLibSol.getSolicitudLiberacion(), sbTraza.toString());

        CodigoCPSN cpsn = this.getCodigoCpsn(
                pLibSol.getTipoBloqueCpsn().getId(),
                pLibSol.getIdCpsn(),
                pLibSol.getSolicitudLiberacion().getProveedorSolicitante());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        EstatusCPSN status = new EstatusCPSN();
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Planificado para CPSNs con fecha de implementación no cumplida.
            status.setId(EstatusCPSN.PLANIFICADO);

            cpsn.setEstatusCPSN(status);
            cpsn.setFechaCuarentena(null);

            this.saveCodigoCpsn(cpsn);
            bitacoraService.add(pLibSol.getSolicitudLiberacion(), "CPSN planificado: " + cpsn.toString());

            return EstadoLiberacionSolicitada.PENDIENTE;
        }

        // Control de Cuarentena
        if (fFinReservaCumplida) {
            // Estado Libre para CPSNs con fecha de implementación cumplida
            status.setId(EstatusCPSN.LIBRE);

            cpsn.setEstatusCPSN(status);
            cpsn.setFechaCuarentena(null);
            cpsn.setProveedor(null);

            this.saveCodigoCpsn(cpsn);
            bitacoraService.add(pLibSol.getSolicitudLiberacion(), "CPSN liberado: " + cpsn.toString());

            return EstadoLiberacionSolicitada.LIBERADO;
        } else {
            // Estado Liberado para CPSNs con fecha de implementación cumplida
            status.setId(EstatusCPSN.CUARENTENA);

            cpsn.setEstatusCPSN(status);
            cpsn.setFechaCuarentena(pLibSol.getFechaFinCuarentena());
            cpsn.setProveedor(null);

            this.saveCodigoCpsn(cpsn);
            bitacoraService.add(pLibSol.getSolicitudLiberacion(), "CPSN en Cuarentena: " + cpsn.toString());

            // Devolvemos el estatus liberado para que se finalize el trámite. Es responsabilidad del Scheduler
            // controlar los cpsns en cuarentena.
            return EstadoLiberacionSolicitada.LIBERADO;
        }
    }

    @Override
    public void liberarCuarentena() {

        EstatusCPSN statusCuarentena = new EstatusCPSN();
        statusCuarentena.setId(EstatusCPSN.CUARENTENA);

        EstatusCPSN statusLibre = new EstatusCPSN();
        statusLibre.setId(EstatusCPSN.LIBRE);

        // Localizamos los rangos en cuarentena
        FiltroBusquedaCodigosCPSN filtroCpsn = new FiltroBusquedaCodigosCPSN();
        filtroCpsn.setEstatusCPSN(statusCuarentena);
        filtroCpsn.setFechaCuarentenaHasta(FechasUtils.getFechaHoy());

        List<CodigoCPSN> listaCodigos = this.findCodigosCPSN(filtroCpsn);
        for (CodigoCPSN cpsn : listaCodigos) {
            cpsn.setEstatusCPSN(statusLibre);
            cpsn.setFechaCuarentena(null);
            cpsn.setProveedor(null);

            this.saveCodigoCpsn(cpsn);
            bitacoraService.add("Cuarentena cumplida, CPSN liberado: " + cpsn);
        }
    }

    @Override
    public List<VEstudioCPSN> estudioCPSN() {
        return codigoCPSNDao.estudioCPSN();
    }

    @Override
    public String cederCPSN(CesionSolicitadaCPSN pCesSol, boolean pInmediata) throws Exception {
        // StringBuilder sbTraza = new StringBuilder();
        // sbTraza.append("Cesión de Código CPSN.");
        // sbTraza.append(", Solicitud: ").append(pCesSol.getSolicitudCesionCPSN().getId());
        // sbTraza.append(", CPSN: ").append(pCesSol.getIdCpsn());
        // sbTraza.append(", Fecha Implementación: ");
        // sbTraza.append(FechasUtils.fechaToString(pCesSol.getFechaImplementacion()));
        // sbTraza.append(" (ejecución planificador: ");
        // sbTraza.append(FechasUtils.fechaToString(
        // FechasUtils.getFechaImplementacionReal(pCesSol.getFechaImplementacion()), "dd/MM/yyyy HH:mm:ss"));
        // sbTraza.append("), Ejecución Inmediata: ").append(pInmediata);

        // Tenemos en cuenta los minutos.
        Date fHoy = new Date();

        boolean fImplementacionCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(pCesSol.getFechaImplementacion())) >= 0);

        // sbTraza.append(", Fecha Implementación Cumplida: ").append(fImplementacionCumplida);
        // LOGGER.info(sbTraza.toString());
        // bitacoraService.add(pCesSol.getSolicitudCesionCPSN(), sbTraza.toString());

        CodigoCPSN cpsn = this.getCodigoCpsn(
                pCesSol.getTipoBloqueCpsn().getId(),
                pCesSol.getIdCpsn(),
                pCesSol.getSolicitudCesionCPSN().getProveedorSolicitante());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        EstatusCPSN status = new EstatusCPSN();
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Planificado para CPSNs con fecha de implementación no cumplida.
            status.setId(EstatusCPSN.PLANIFICADO);

            cpsn.setEstatusCPSN(status);
            cpsn.setFechaCuarentena(null);

            this.saveCodigoCpsn(cpsn);
            bitacoraService.add(pCesSol.getSolicitudCesionCPSN(), "CPSN Planificado: " + cpsn.toString());

            return EstadoLiberacionSolicitada.PENDIENTE;
        } else {
            // Estado Cedido para CPSNs con fecha de implementación cumplida
            status.setId(EstatusCPSN.ASIGNADO);

            cpsn.setEstatusCPSN(status);
            cpsn.setProveedor(pCesSol.getSolicitudCesionCPSN().getProveedorCesionario());

            this.saveCodigoCpsn(cpsn);
            bitacoraService.add(pCesSol.getSolicitudCesionCPSN(), "CPSN Cedido: " + cpsn.toString());

            // Devolvemos el estatus asignado para que se finalize el trámite.
            return EstadoCesionSolicitada.CEDIDO;
        }
    }

    @Override
    public CodigoCPSN getCodigoCpsn(String pIdTipoBloque, BigDecimal pIdCodigo, Proveedor pProveedor) {
        return codigoCPSNDao.getCodigoCpsn(pIdTipoBloque, pIdCodigo, pProveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void asignarCpsn(NumeracionAsignadaCpsn pCpsnSolicitado, Proveedor pProveedor) throws Exception {
        // El código debe venir como libre, si no, no se hubiese podido seleccionar en la pestaña de Análisis
        CodigoCPSN cpsn = this.getCodigoCpsn(pCpsnSolicitado.getTipoBloqueCpsn().getId(), pCpsnSolicitado.getIdCpsn(),
                null);

        EstatusCPSN status = new EstatusCPSN();
        status.setId(EstatusCPSN.ASIGNADO);

        // Nuevos valores para el CPSN.
        cpsn.setEstatusCPSN(status);
        cpsn.setProveedor(pProveedor);
        this.saveCodigoCpsn(cpsn);

        // Trazas de Bitácora.
        StringBuilder sbTraza = new StringBuilder();
        sbTraza.append("CPSN ").append(cpsn.getId());
        sbTraza.append(" Asignado al proveedor ").append(pProveedor.getNombre());
        bitacoraService.add(pCpsnSolicitado.getSolicitudAsignacion(), sbTraza.toString());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CodigoCPSN saveCodigoCpsn(CodigoCPSN codigo) {
        // Se hace la Auditoría del registro
        codigo.updateAuditableValues(usuariosService.getCurrentUser());
        return codigoCPSNDao.saveOrUpdate(codigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeCodigoCpsn(CodigoCPSN codigo) {
        codigoCPSNDao.delete(codigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CodigoCPSN> findAllCodigosCPSN(Proveedor pProveedor, TipoBloqueCPSN tipoBloque) {
        return codigoCPSNDao.findAllCodigosCPSN(pProveedor, tipoBloque);
    }
}
