package mx.ift.sns.negocio.cpsi;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.cpsi.ICodigoCpsiDao;
import mx.ift.sns.dao.cpsi.ICpsiUitEntregadoDao;
import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.modelo.cpsi.CesionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.CpsiAsignado;
import mx.ift.sns.modelo.cpsi.CpsiUitEntregado;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.InfoCatCpsi;
import mx.ift.sns.modelo.cpsi.LiberacionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.Linea1EstudioCPSI;
import mx.ift.sns.modelo.cpsi.Linea2EstudioCPSI;
import mx.ift.sns.modelo.cpsi.VEstudioCPSI;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSI;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;

/**
 * Servicio de Codigos CPS Internacionales.
 * @author X50880SA
 */
@Stateless(name = "CodigoCPSIService", mappedName = "CodigoCPSIService")
@Remote(ICodigoCPSIService.class)
public class CodigoCPSIService implements ICodigoCPSIService {

    /** Logger de la clase . */
    // private static final Logger LOGGER = LoggerFactory.getLogger(CodigoCPSIService.class);

    /** Dao de Codigos CPS Internacionales. */
    @Inject
    private ICodigoCpsiDao codigoCPSIDao;

    /** Dao de Codigos CPS Internacionales Entregados. */
    @Inject
    private ICpsiUitEntregadoDao cpsiUitEntregadoDao;

    /** DAO de parametros. */
    @Inject
    private IParametroDao parametroDao;

    /** Servicio de Bitácora. */
    @EJB
    private IBitacoraService bitacoraService;

    /** Servicio de usuarios. */
    @EJB
    private IUsuariosService usuariosService;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EstatusCPSI getEstatusCPSIById(String id) throws Exception {
        return codigoCPSIDao.getEstatusCPSIById(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<EstatusCPSI> findAllEstatusCPSI() {
        return codigoCPSIDao.findAllEstatusCPSI();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CodigoCPSI> findAllCodigosCPSI(FiltroBusquedaCodigosCPSI pFiltro) {
        return codigoCPSIDao.findAllCodigosCPSI(pFiltro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllCodigosCPSICount(FiltroBusquedaCodigosCPSI pFiltro) {
        return codigoCPSIDao.findAllCodigosCPSICount(pFiltro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<CodigoCPSI> findAllCodigosCPSIForAnalisis(Proveedor pProveedor) {
        return codigoCPSIDao.findAllCodigosCPSIForAnalisis(pProveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public CodigoCPSI getCodigoCpsi(BigDecimal pIdCodigo, Proveedor pProveedor) {
        return codigoCPSIDao.getCodigoCpsi(pIdCodigo, pProveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CodigoCPSI saveCodigoCPSI(CodigoCPSI codigo) {
        codigo.updateAuditableValues(usuariosService.getCurrentUser());
        return codigoCPSIDao.saveOrUpdate(codigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] exportarCodigosCPSI(FiltroBusquedaCodigosCPSI pFiltros) throws Exception {
        List<InfoCatCpsi> listado = codigoCPSIDao.findAllInfoCatCPSI(pFiltros);
        listado = rellenarDatosSolicitudUit(listado);
        ExportConsultaCatalogoCodigosCPSI eccn = new ExportConsultaCatalogoCodigosCPSI(listado);
        ExportarExcel export = new ExportarExcel(parametroDao);
        return export.generarReporteExcel(ExportConsultaCatalogoCodigosCPSI.TXT_CODIGOS_CPSI, eccn);
    }

    @Override
    // @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] exportarEstudioCPSI(VEstudioCPSI estudio) throws Exception {
        ExportConsultaEstudioCodigosCPSI ecen = new ExportConsultaEstudioCodigosCPSI(estudio);
        return ecen.generarReporteExcel();
    }

    @Override
    public VEstudioCPSI estudioCPSI(Proveedor proveedor) {

        VEstudioCPSI vEstudioCPSI = new VEstudioCPSI();

        if (proveedor != null) {

            Linea1EstudioCPSI linea1 = new Linea1EstudioCPSI();
            linea1 = codigoCPSIDao.estudioCPSILinea1(proveedor);
            vEstudioCPSI.setLinea1EstudioCPSI(linea1);
            vEstudioCPSI.setMostrarLinea1(true);

        } else {
            vEstudioCPSI.setMostrarLinea1(false);
        }

        Linea2EstudioCPSI linea2 = new Linea2EstudioCPSI(0L, new BigDecimal(0), new BigDecimal(0),
                new BigDecimal(0), new BigDecimal(0), new BigDecimal(0));
        linea2 = codigoCPSIDao.estudioCPSILinea2();
        vEstudioCPSI.setLinea2EstudioCPSI(linea2);

        return vEstudioCPSI;
    }

    @Override
    public String liberarCodigosCPSI(List<InfoCatCpsi> codigosCPSISeleccionados) {

        String error = validarEstado(codigosCPSISeleccionados, "liberar", EstatusCPSI.RESERVADO);
        if (error != null) {
            return error;
        }
        EstatusCPSI estatusCPSI = new EstatusCPSI();
        estatusCPSI.setId(EstatusCPSI.LIBRE);

        CodigoCPSI codCPSI = new CodigoCPSI();
        for (InfoCatCpsi codigo : codigosCPSISeleccionados) {

            codCPSI = getCodigoCpsi(codigo.getId(), codigo.getProveedor());

            codCPSI.setEstatus(estatusCPSI);
            this.saveCodigoCPSI(codCPSI);
        }

        return null;
    }

    @Override
    public String reservarCodigosCPSI(List<InfoCatCpsi> codigosCPSISeleccionados) {
        String error = validarEstado(codigosCPSISeleccionados, "reservar", EstatusCPSI.LIBRE);
        if (error != null) {
            return error;
        }
        EstatusCPSI estatusCPSI = new EstatusCPSI();
        estatusCPSI.setId(EstatusCPSI.RESERVADO);

        CodigoCPSI codCPSI = new CodigoCPSI();
        for (InfoCatCpsi codigo : codigosCPSISeleccionados) {

            codCPSI = getCodigoCpsi(codigo.getId(), codigo.getProveedor());

            codCPSI.setEstatus(estatusCPSI);
            this.saveCodigoCPSI(codCPSI);
        }

        return null;
    }

    /**
     * Método encargado de validar que todos los códigos seleccionados tenga el estatus correcto.
     * @param codigosCPSISeleccionados listado de códigos
     * @param accion a realizar sobre los registros
     * @param estatus a validar
     * @return error si existe
     */
    private String validarEstado(List<InfoCatCpsi> codigosCPSISeleccionados, String accion,
            String estatus) {
        String error = null;

        if (codigosCPSISeleccionados == null || codigosCPSISeleccionados.isEmpty()) {
            error = "cpsi.codigos.CPSI.seleccion.error";
        } else {
            for (InfoCatCpsi codigo : codigosCPSISeleccionados) {
                if (!estatus.equals(codigo.getEstatus().getId())) {
                    error = "cpsi.codigos.CPSI." + accion + ".error";
                }
            }
        }

        return error;
    }

    @Override
    public String liberarCpsi(LiberacionSolicitadaCpsi pLibSol, boolean pInmediata) throws Exception {
        // StringBuilder sbTraza = new StringBuilder();
        // sbTraza.append("Liberación de Código CPSI.");
        // sbTraza.append(", Solicitud: ").append(pLibSol.getSolicitudLiberacion().getId());
        // sbTraza.append(", CPSI: ").append(pLibSol.getIdCpsi());
        // sbTraza.append("\nFecha Implementación: ");
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

        CodigoCPSI cpsi = this.getCodigoCpsi(
                pLibSol.getIdCpsi(), pLibSol.getSolicitudLiberacion().getProveedorSolicitante());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        EstatusCPSI status = new EstatusCPSI();
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Planificado para CPSIs con fecha de implementación no cumplida.
            status.setId(EstatusCPSI.PLANIFICADO);

            cpsi.setSolicitud(pLibSol.getSolicitudLiberacion());
            cpsi.setEstatus(status);
            cpsi.setFechaFinCuarentena(null);

            this.saveCodigoCPSI(cpsi);
            bitacoraService.add(pLibSol.getSolicitudLiberacion(), "CPSI Planificado: " + cpsi.toString());

            return EstadoLiberacionSolicitada.PENDIENTE;
        }

        // Control de Cuarentena
        if (fFinReservaCumplida) {
            // Estado Libre para CPSIs con fecha de implementación cumplida
            status.setId(EstatusCPSI.LIBRE);

            cpsi.setSolicitud(pLibSol.getSolicitudLiberacion());
            cpsi.setEstatus(status);
            cpsi.setFechaFinCuarentena(null);
            cpsi.setProveedor(null);

            this.saveCodigoCPSI(cpsi);
            bitacoraService.add(pLibSol.getSolicitudLiberacion(), "CPSI liberado: " + cpsi.toString());

            return EstadoLiberacionSolicitada.LIBERADO;
        } else {
            // Estado Liberado para CPSIs con fecha de implementación cumplida
            status.setId(EstatusCPSI.CUARENTENA);

            cpsi.setSolicitud(pLibSol.getSolicitudLiberacion());
            cpsi.setEstatus(status);
            cpsi.setFechaFinCuarentena(pLibSol.getFechaFinCuarentena());
            cpsi.setProveedor(null);

            this.saveCodigoCPSI(cpsi);
            bitacoraService.add(pLibSol.getSolicitudLiberacion(), "CPSI en Cuarentena: " + cpsi.toString());

            // Devolvemos el estatus liberado para que se finalize el trámite. Es responsabilidad del Scheduler
            // controlar los cpsis en cuarentena.
            return EstadoLiberacionSolicitada.LIBERADO;
        }
    }

    @Override
    public void liberarCuarentena() {

        EstatusCPSI statusCuarentena = new EstatusCPSI();
        statusCuarentena.setId(EstatusCPSI.CUARENTENA);

        EstatusCPSI statusLibre = new EstatusCPSI();
        statusLibre.setId(EstatusCPSI.LIBRE);

        // Localizamos los rangos en cuarentena
        FiltroBusquedaCodigosCPSI filtroCpsi = new FiltroBusquedaCodigosCPSI();
        filtroCpsi.setEstatusCPSI(statusCuarentena);
        filtroCpsi.setFechaCuarentenaHasta(FechasUtils.getFechaHoy());

        List<CodigoCPSI> listaCodigos = this.findAllCodigosCPSI(filtroCpsi);
        for (CodigoCPSI cpsi : listaCodigos) {
            cpsi.setEstatus(statusLibre);
            cpsi.setFechaFinCuarentena(null);
            cpsi.setProveedor(null);

            this.saveCodigoCPSI(cpsi);
            bitacoraService.add("Cuarentena cumplida, CPSI liberado: " + cpsi);
        }
    }

    @Override
    public String cederCpsi(CesionSolicitadaCpsi pCesSol, boolean pInmediata) throws Exception {
        // StringBuilder sbTraza = new StringBuilder();
        // sbTraza.append("Cesión de Código CPSI.");
        // sbTraza.append(", Solicitud: ").append(pCesSol.getSolicitudCesion().getId());
        // sbTraza.append(", CPSI: ").append(pCesSol.getIdCpsi());
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
        // bitacoraService.add(pCesSol.getSolicitudCesion(), sbTraza.toString());

        CodigoCPSI cpsi = this.getCodigoCpsi(
                pCesSol.getIdCpsi(),
                pCesSol.getSolicitudCesion().getProveedorSolicitante());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        EstatusCPSI status = new EstatusCPSI();
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Planificado para CPSIs con fecha de implementación no cumplida.
            status.setId(EstatusCPSI.PLANIFICADO);

            cpsi.setSolicitud(pCesSol.getSolicitudCesion());
            cpsi.setEstatus(status);
            cpsi.setFechaFinCuarentena(null);

            this.saveCodigoCPSI(cpsi);
            bitacoraService.add(pCesSol.getSolicitudCesion(), "CPSI Planificado: " + cpsi.toString());

            return EstadoLiberacionSolicitada.PENDIENTE;
        } else {
            // Estado Cedido para CPSIs con fecha de implementación cumplida
            status.setId(EstatusCPSI.ASIGNADO);

            cpsi.setSolicitud(pCesSol.getSolicitudCesion());
            cpsi.setEstatus(status);
            cpsi.setProveedor(pCesSol.getSolicitudCesion().getProveedorCesionario());

            this.saveCodigoCPSI(cpsi);
            bitacoraService.add(pCesSol.getSolicitudCesion(), "CPSI Cedido: " + cpsi.toString());

            // Devolvemos el estatus asignado para que se finalize el trámite.
            return EstadoCesionSolicitada.CEDIDO;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void asignarCpsi(CpsiAsignado pCpsiSolicitado, Proveedor pProveedor) throws Exception {
        // El código debe venir como libre, si no, no se hubiese podido seleccionar en la pestaña de Análisis
        CodigoCPSI cpsi = this.getCodigoCpsi(pCpsiSolicitado.getIdCpsi(), null);

        EstatusCPSI status = new EstatusCPSI();
        status.setId(EstatusCPSI.ASIGNADO);

        // Nuevos valores para el CPSI.
        cpsi.setSolicitud(pCpsiSolicitado.getSolicitudAsignacion());
        cpsi.setEstatus(status);
        cpsi.setProveedor(pProveedor);
        this.saveCodigoCPSI(cpsi);

        // Trazas de Bitácora.
        StringBuilder sbTraza = new StringBuilder();
        sbTraza.append("CPSI ").append(cpsi.getId());
        sbTraza.append(" Asignado al proveedor ").append(pProveedor.getNombre());
        bitacoraService.add(pCpsiSolicitado.getSolicitudAsignacion(), sbTraza.toString());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Linea1EstudioCPSI getEstudioCpsiProveedor(Proveedor pProveedor) {
        return codigoCPSIDao.getEstudioCpsiProveedor(pProveedor);
    }

    @Override
    public List<InfoCatCpsi> findAllInfoCatCPSI(FiltroBusquedaCodigosCPSI filtros) {
        List<InfoCatCpsi> listaInfoCatCpasi = codigoCPSIDao.findAllInfoCatCPSI(filtros);
        listaInfoCatCpasi = rellenarDatosSolicitudUit(listaInfoCatCpasi);
        return listaInfoCatCpasi;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeCodigoCpsi(CodigoCPSI codigo) {
        codigoCPSIDao.delete(codigo);
    }

    /**
     * Método que recoge el resultado de la búsqueda y rellena los datos que faltan para las solicitudes UIT.
     * @param plistaInfoCatCpasi lista que devuelve los valores de la búsqueda
     * @return List<InfoCatCpsi>
     */
    private List<InfoCatCpsi> rellenarDatosSolicitudUit(List<InfoCatCpsi> plistaInfoCatCpasi) {
        CpsiUitEntregado cpsiUitEntregadoOrigenSerie = null;
        Integer numeroDecimalInicialAnterior = -1;

        for (Iterator iterator = plistaInfoCatCpasi.iterator(); iterator.hasNext();) {
            InfoCatCpsi infoCatCpsi = (InfoCatCpsi) iterator.next();

            // Se calcula el el perimer codigo de la serie
            // es el unico que guarda refrencia a la solicitud
            // se copian sus datos en todos los elementos de la serie (0-->7)
            String binario = infoCatCpsi.getBinario();
            String binarioInicial = binario.substring(0, 11) + "000";
            Integer numeroDecimalInicial = Integer.parseInt(binarioInicial, 2);
            if (numeroDecimalInicial.intValue() != numeroDecimalInicialAnterior.intValue()) {
                try {
                    cpsiUitEntregadoOrigenSerie = cpsiUitEntregadoDao.getByIdCpsi(numeroDecimalInicial);
                } catch (Exception e) {
                    cpsiUitEntregadoOrigenSerie = null;
                }
            }
            if (null != cpsiUitEntregadoOrigenSerie) {
                infoCatCpsi.setReferenciaUit(cpsiUitEntregadoOrigenSerie.getReferenciaUit());
                if (null != cpsiUitEntregadoOrigenSerie.getSolicitudUit()) {
                    infoCatCpsi.setFechaAsignacion(cpsiUitEntregadoOrigenSerie.getSolicitudUit().getFechaAsignacion());
                }
            }
            numeroDecimalInicialAnterior = numeroDecimalInicial;

        }
        return plistaInfoCatCpasi;
    }

}
