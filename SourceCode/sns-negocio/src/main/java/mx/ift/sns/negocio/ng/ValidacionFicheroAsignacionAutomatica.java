package mx.ift.sns.negocio.ng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.IAbnCentralDao;
import mx.ift.sns.dao.ng.IAbnDao;
import mx.ift.sns.dao.ng.ICentralDao;
import mx.ift.sns.dao.ng.ICentralesRelacionDao;
import mx.ift.sns.dao.ng.IMarcaDao;
import mx.ift.sns.dao.ng.IModeloDao;
import mx.ift.sns.dao.ng.INirDao;
import mx.ift.sns.dao.ot.IPoblacionDao;
import mx.ift.sns.dao.pst.IProveedorDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.AbnCentral;
import mx.ift.sns.modelo.abn.AbnCentralPK;
import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.CentralesRelacion;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.ng.model.AvisoValidacionCentral;
import mx.ift.sns.negocio.ng.model.ErrorValidacion;
import mx.ift.sns.negocio.ng.model.RetornoProcesaFicheroAsignacion;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

/** Validaciones para el fichero. */
@Stateless
@Remote(IValidacionFicheroAsignacionAutomatica.class)
public class ValidacionFicheroAsignacionAutomatica implements IValidacionFicheroAsignacionAutomatica {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidacionFicheroAsignacionAutomatica.class);

    /** proveedorDAO. */
    @Inject
    private IProveedorDao proveedorDAO;

    /** poblacionDAO. */
    @Inject
    private IPoblacionDao poblacionDAO;

    /** abnDAO. */
    @Inject
    private IAbnDao abnDAO;

    /** nirDAO. */
    @Inject
    private INirDao nirDAO;

    /** centralDAO. */
    @Inject
    private ICentralDao centralDAO;

    /** centralRelacionDAO. */
    @Inject
    private ICentralesRelacionDao centralRelacionDAO;

    /** centralRelacionDAO. */
    @Inject
    private IAbnCentralDao abnCentralDAO;

    /** marcaDao. */
    @Inject
    private IMarcaDao marcaDao;

    /** modeloDao. */
    @Inject
    private IModeloDao modeloDao;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** Maximo numero de columnas permitidas. */
    private static final int MAX_COLUMNAS = 32;

    /** Contante para el patron de coordenadas. */
    private static final String PATTERN_COORDENADA = "^\\d{1,3}°\\d{1,2}'\\d{1,2}(\\.\\d{1,4})?\"$";

    /** Constante con el tipo de central destino. */
    private static final String DESTINO = "destino";

    /** Constante con el tipo de central origen. */
    private static final String ORIGEN = "origen";

    /** Constante con el tipo de modalidad MPP. */
    private static final String MPP = "MPP";

    /** Constante con el tipo de modalidad CPP. */
    private static final String CPP = "CPP";

    /** Constante con el tipo de modalidad FIJO. */
    private static final String FIJO = "FIJO";

    /** Constante con la extension csv. */
    private static final String CSV = "csv";

    /** Constante con la extension xlsx. */
    private static final String XLSX = "xlsx";

    /** Constante con el valor máximo de la longitud. */
    private static final int MAX_LONGITUD = 117;

    /** Constante con el valor mínimo de la longitud. */
    private static final int MIN_LONGITUD = 86;

    /** Constante con el valor máximo de la latitud. */
    private static final int MAX_LATITUD = MAX_COLUMNAS;

    /** Constante con el valor mínimo de la latitud. */
    private static final int MIN_LATITUD = 14;

    /** Constante con el valor máximo de los minutos y segundos. */
    private static final double MAX_MINUTOS = 60.0;

    /** Constante con el valor mínimo de los minutos y segundos. */
    private static final double MIN_MINUTOS = 0.0;

    /** Constante para desconocida. */
    private static final String DESCONOCIDA = "DESCONOCIDA";

    /** Constante con el valor de las coordenadas a 0. */
    private static final String COORDENADA_0 = "0°0'0\"";

    /** lErrores. */
    private List<ErrorValidacion> listaErrores = new ArrayList<ErrorValidacion>(0);

    /** lAviso. */
    private List<AvisoValidacionCentral> listaAvisoCentral = new ArrayList<AvisoValidacionCentral>(0);

    /** Aviso especial. */
    private List<AvisoValidacionCentral> listaAvisoNoObligatorios = new ArrayList<AvisoValidacionCentral>(0);

    /** Lista de datos. */
    private List<List<String>> listaDatos = new ArrayList<List<String>>(0);

    /** Solicitud de asignacion. */
    private SolicitudAsignacion solicitud;

    /**
     * Método que procesa el fichero y recoge los valores necesarios.
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RetornoProcesaFicheroAsignacion validar(File fichero) throws Exception {

        String nombre = fichero.getPath();
        String extension = null;

        int index = nombre.lastIndexOf(".");
        if (index != -1) {
            extension = nombre.substring(index + 1);
        }

        RetornoProcesaFicheroAsignacion procesaFichero = new RetornoProcesaFicheroAsignacion();
        solicitud = new SolicitudAsignacion();
        solicitud.setEstadoSolicitud(new EstadoSolicitud());
        solicitud.getEstadoSolicitud().setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
        solicitud.setNumeracionSolicitadas(new ArrayList<NumeracionSolicitada>());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se comprueba si la extension de fichero xls, xlsx o csv.");
        }

        // Parseamos el fichero a un mapa de datos tipo String.
        if (extension.equalsIgnoreCase(XLSX)) {
            listaDatos = extraerExcelNuevo(fichero);
        } else if (extension.equalsIgnoreCase(CSV)) {
            listaDatos = extraerCsv(fichero);
        }

        // Inicializamos lista de errores y avisos.
        listaErrores = new ArrayList<ErrorValidacion>(0);
        listaAvisoCentral = new ArrayList<AvisoValidacionCentral>(0);
        listaAvisoNoObligatorios = new ArrayList<AvisoValidacionCentral>(0);

        try {

            if (listaDatos != null && !listaDatos.isEmpty()) {

                // Validamos proveedor solicitante
                String strPst = listaDatos.get(1).get(0);
                Proveedor pst = proveedorDAO.getProveedorByNombre(strPst);
                if (pst != null) {
                    if (pst.getTipoProveedor().getCdg().equals(TipoProveedor.CONCESIONARIO) && pst.getIdo() == null) {
                        procesaFichero.setMensajeError("El proveedor " + strPst
                                + " de tipo Concesionario no tiene código IDO.");
                        return procesaFichero;
                    } else if (pst.getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)
                            && pst.getIda() == null) {
                        procesaFichero.setMensajeError("El proveedor " + strPst
                                + " de tipo comercializadora no tiene código IDA.");
                        return procesaFichero;
                    } else if (pst.getTipoProveedor().getCdg().equals(TipoProveedor.AMBOS) && pst.getIdo() == null
                            && pst.getIda() == null) {
                        procesaFichero.setMensajeError("El proveedor " + strPst
                                + " de tipo Ambos no tiene código IDO o código IDA.");
                        return procesaFichero;
                    } else {
                        solicitud.setProveedorSolicitante(pst);
                    }

                } else {
                    procesaFichero.setMensajeError("El proveedor " + strPst + " no esta registrado en el SNS");
                    return procesaFichero;
                }

                // Validamos datos cabecera
                validarCabecera();

                if (listaDatos.get(2).size() > MAX_COLUMNAS) {
                    procesaFichero.setMensajeError("El archivo trae más columnas de las establecidas.");
                    return procesaFichero;
                }

                for (int i = 3; i < listaDatos.size(); i++) {
                    // Validamos cada registro
                    List<String> fila = listaDatos.get(i);

                    NumeracionSolicitada numSol = validarRegistro(fila);
                    // Si no hay errores o centrales nuevas añadimos la numeracion a la solicitud
                    if (listaErrores.isEmpty() && listaAvisoCentral.isEmpty()) {
                        solicitud.addNumeracionSolicitadas(numSol);
                    }

                }
            } else {
                procesaFichero.setMensajeError("Fichero mal formado.");
                return procesaFichero;
            }

        } catch (IOException ex) {
            LOGGER.info("Error Inesperado: " + ex.getMessage());
        }

        procesaFichero.setSolicitudAsignacion(solicitud);

        // Cargamos listas de errores y avisos.
        procesaFichero.setlErrores(listaErrores);
        procesaFichero.setlAviso(listaAvisoCentral);
        procesaFichero.setlAvisoNoOblig(listaAvisoNoObligatorios);

        return procesaFichero;
    }

    /**
     * Valida los datos de un registro.
     * @param fila List<String>
     * @return numeracion solicitada
     */
    private NumeracionSolicitada validarRegistro(List<String> fila) {

        NumeracionSolicitada numSol = new NumeracionSolicitada();
        String registro = fila.get(0);

        // Validamos clave censal
        String inegi = fila.get(1);
        Poblacion poblacion = validarClaveCensal(inegi, registro);
        numSol.setPoblacion(poblacion);

        // Validamos ABN y NIR
        String strAbn = fila.get(2);
        String strNir = fila.get(3);

        validarAbnNir(strAbn, strNir, poblacion, registro);

        // Validamos la cantidad de numeracion
        String strCantidad = fila.get(5);
        if (!(StringUtils.isBlank(strCantidad) || isCero(strCantidad))) {
            if (isNumeric(strCantidad)) {
                BigDecimal cantidad = new BigDecimal(strCantidad);
                if (cantidad.intValue() > 0) {
                    numSol.setCantSolicitada(cantidad);
                    numSol.setCantAsignada(new BigDecimal(0));
                } else {
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error 015");
                    e.setNumRegistro(registro);
                    e.setDescripcion("La cantidad de numeración solicitada en el registro " + registro
                            + " es negativa. ");
                    listaErrores.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setNumRegistro(registro);
                e.setDescripcion("La cantidad de numeración solicitada en el registro " + registro
                        + " debe ser un valor numérico.");
                listaErrores.add(e);
            }
        } else {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 015");
            e.setNumRegistro(registro);
            e.setDescripcion("La cantidad de numeración solicitada en el registro " + registro
                    + " está vacía o en cero.");
            listaErrores.add(e);
        }

        // Validamos la modalidad
        String strModalidad = fila.get(6);
        TipoRed tipoRed = new TipoRed();
        TipoModalidad modalidad = null;

        if (strModalidad.equals(FIJO)) {
            tipoRed.setCdg(TipoRed.FIJA);
        } else if (strModalidad.equals(CPP)) {
            modalidad = new TipoModalidad();
            tipoRed.setCdg(TipoRed.MOVIL);
            modalidad.setCdg(TipoModalidad.CPP);
        } else if (strModalidad.equals(MPP)) {
            modalidad = new TipoModalidad();
            tipoRed.setCdg(TipoRed.MOVIL);
            modalidad.setCdg(TipoModalidad.MPP);
        } else {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 009");
            e.setNumRegistro(registro);
            e.setDescripcion("La modalidad indicada en el registro " + registro
                    + " no corresponde a las registradas en el sistema.");
            listaErrores.add(e);
        }

        if (solicitud.getProveedorSolicitante().getTipoRed().equals(tipoRed)
                || solicitud.getProveedorSolicitante().getTipoRed().getCdg().equals(TipoRed.AMBAS)) {
            numSol.setTipoRed(tipoRed);
            numSol.setTipoModalidad(modalidad);
        } else {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 009");
            e.setNumRegistro(registro);
            e.setDescripcion("La modalidad indicada en el registro " + registro
                    + " no corresponde al PST Solicitante.");
            listaErrores.add(e);
        }

        // Validamos las centrales
        CentralesRelacion relacion = new CentralesRelacion();
        Central centralOrigen = validarCentral(ORIGEN, fila);

        if (centralOrigen != null) {
            centralOrigen = crearCentral(centralOrigen, ORIGEN, poblacion, registro);
            numSol.setCentralOrigen(centralOrigen);
            if (centralOrigen.getId() != null) {
                relacion.setCentralOrigen(centralOrigen);
            }
        }

        Central centralDestino = validarCentral(DESTINO, fila);

        if (centralDestino != null) {
            centralDestino = crearCentral(centralDestino, DESTINO, poblacion, registro);
            numSol.setCentralDestino(centralDestino);
            if (centralDestino.getId() != null) {
                relacion.setCentralDestino(centralDestino);
            }
        }

        if (relacion.getCentralOrigen() != null && relacion.getCentralDestino() != null) {
            centralRelacionDAO.saveCentralesRelacion(relacion);
        }

        // Validamos el concesionario de RED
        String strConcesionario = fila.size() > 31 ? fila.get(31) : "";
        Proveedor concesionario = validarConcesionario(strConcesionario, registro, tipoRed);
        numSol.setConcesionario(concesionario);

        if (listaErrores.isEmpty() && listaAvisoCentral.isEmpty()) {
            // Formateamos IDO e IDA para PNN
            numSol = formatearNumeracionSolictada(numSol);
        }
        return numSol;
    }

    /**
     * Validamos ABN y NIR de un registro. @param strAbn abn
     * @param strNir nir
     * @param registro resgistro
     * @param poblacion poblacion
     */
    private void validarAbnNir(String strAbn, String strNir, Poblacion poblacion, String registro) {

        BigDecimal bdAbn = null;
        Integer intNir = null;

        Nir nir = null;
        Abn abn = null;

        // Validamos ABN
        if (strAbn.length() > Integer.toString(Abn.MAX_ABN).length()) {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 007");
            e.setNumRegistro(registro);
            e.setDescripcion("La longitud del ABN del registro " + registro + " no es correcta.");
            listaErrores.add(e);
        } else {
            if (isNumeric(strAbn)) {
                bdAbn = new BigDecimal(strAbn);
                abn = abnDAO.getAbnById(bdAbn);
                if (abn == null) {
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error 007");
                    e.setNumRegistro(registro);
                    e.setDescripcion("La ABN del registro " + registro + " no existe en el SNS.");
                    listaErrores.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error 007");
                e.setNumRegistro(registro);
                e.setDescripcion("El ABN del registro " + registro + " debe ser númerico.");
                listaErrores.add(e);
            }
        }

        // Validamos NIR
        if (strNir.length() > Integer.toString(Nir.MAX_NIR).length()) {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 008");
            e.setNumRegistro(registro);
            e.setDescripcion("La longitud del NIR del registro " + registro + " no es correcta.");
            listaErrores.add(e);
        } else {
            if (isNumeric(strNir)) {
                intNir = new Integer(strNir);
                nir = nirDAO.getNirByCodigo(intNir);

                if (nir == null) {
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error 008");
                    e.setNumRegistro(registro);
                    e.setDescripcion("El NIR del registro " + registro + " no existe en el SNS.");
                    listaErrores.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error 008");
                e.setNumRegistro(registro);
                e.setDescripcion("El NIR del registro " + registro + " debe ser númerico.");
                listaErrores.add(e);
            }
        }

        // Si existe ABN comprobamos si esta asociado a la poblacion
        if (abn != null && poblacion != null && !poblacion.getAbn().equals(abn)) {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 007");
            e.setNumRegistro(registro);
            e.setDescripcion("La ABN del registro " + registro + " no cubre la población indicada.");
            listaErrores.add(e);
        }

        // Comprobamos si el abn corresponde al NIR
        if (abn != null && nir != null && !nir.getAbn().equals(abn)) {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 007");
            e.setNumRegistro(registro);
            e.setDescripcion("La ABN del registro " + registro + " no corresponde con el NIR indicado.");
            listaErrores.add(e);
            // Si no corresponde tambien comprobamos que nir este asociado a la poblacion.
            // Si corresponde el NIR al ABN estara asoaciodo a la poblacion o no de la misma manera que el abn
            // informado. No es necesario validarlo contra la poblacion tambien.
            if (nir != null && poblacion != null && !poblacion.getAbn().equals(nir.getAbn())) {
                ErrorValidacion enir = new ErrorValidacion();
                enir.setCodigo("Error 008");
                enir.setNumRegistro(registro);
                enir.setDescripcion("El NIR del registro " + registro + " no cubre la población indicada.");
                listaErrores.add(enir);
            }
        }

    }

    /**
     * Validamos la clave censal de un registro y retornarmos la poblacion correspondiente.
     * @param inegi inegi
     * @param registro registro
     * @return poblacion
     */
    private Poblacion validarClaveCensal(String inegi, String registro) {
        Poblacion poblacion = null;
        if (isNumeric(inegi)) {
            if (inegi.length() == 9) {
                poblacion = poblacionDAO.getPoblacionByInegi(inegi);
                if (poblacion == null) {
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error 006");
                    e.setNumRegistro(registro);
                    e.setDescripcion("La clave censal del registro " + registro + " no existe en el SNS.");
                    listaErrores.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error 006");
                e.setNumRegistro(registro);
                e.setDescripcion("La longitud de la clave censal del registro " + registro + ", no es correcta.");
                listaErrores.add(e);
            }
        } else {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 006");
            e.setNumRegistro(registro);
            e.setDescripcion("La clave censal del registro " + registro + " debe ser númerica.");
            listaErrores.add(e);
        }

        return poblacion;
    }

    /**
     * Formateamos el ido e ida del PNN de una numeracion solicitada.
     * @param numSol numeracion solicitada
     * @return numeracion solicitada
     */
    private NumeracionSolicitada formatearNumeracionSolictada(NumeracionSolicitada numSol) {

        if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.CONCESIONARIO)) {
            numSol.setIdoPnn(solicitud.getProveedorSolicitante().getIdo());
            numSol.setIdaPnn(solicitud.getProveedorSolicitante().getIdo());
        } else if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                .equals(TipoProveedor.COMERCIALIZADORA)) {
            numSol.setIdoPnn(numSol.getConcesionario().getIdo());
            numSol.setIdaPnn(solicitud.getProveedorSolicitante().getIda());
        } else {
            numSol.setIdaPnn(solicitud.getProveedorSolicitante().getIdo());
            if (numSol.getConcesionario() != null) {
                numSol.setIdoPnn(numSol.getConcesionario().getIdo());
            } else {
                numSol.setIdoPnn(solicitud.getProveedorSolicitante().getIdo());
            }
        }

        return numSol;
    }

    /**
     * Validamos el concesionario de un registro.
     * @param strConcesionario concesionario
     * @param registro registro
     * @param tipoRed tipo red
     * @return arrendatario
     */
    private Proveedor validarConcesionario(String strConcesionario, String registro, TipoRed tipoRed) {

        Proveedor concesionario = null;
        switch (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()) {
        // Si el solicitante es concesionario no puede tener registro con concesionarios cargados
        case TipoProveedor.CONCESIONARIO:
            if (!StringUtils.isBlank(strConcesionario)) {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error 016");
                e.setNumRegistro(registro);
                e.setDescripcion("El concesionario indicado en el registro " + registro
                        + " debe de estar vacío para un proveedor solicitante de tipo concesionario");
                listaErrores.add(e);
            }
            break;
        // Si el solicitante es comercializadora el concesionario tiene que estar informado
        case TipoProveedor.COMERCIALIZADORA:
            if (StringUtils.isBlank(strConcesionario)) {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error 016");
                e.setNumRegistro(registro);
                e.setDescripcion("El concesionario indicado en el registro " + registro
                        + " es obligatorio para un proveedor solicitante de tipo concesionario");
                listaErrores.add(e);
                break;
            }

        default:
            if (!StringUtils.isBlank(strConcesionario)) {
                concesionario = proveedorDAO.getProveedorByNombre(strConcesionario);
                if (concesionario != null) {
                    // Comprobamos si el solicitante tiene convenio para el tipo de red con el concesionario
                    boolean convenioOk = false;
                    boolean modalidadOk = false;
                    List<ProveedorConvenio> convenios = solicitud.getProveedorSolicitante()
                            .getConveniosComercializador();
                    for (ProveedorConvenio convenio : convenios) {
                        if (convenio.getProveedorConcesionario().equals(concesionario)) {
                            convenioOk = true;
                            if (convenio.getTipoRed().equals(tipoRed)
                                    || convenio.getTipoRed().getCdg().equals(TipoRed.AMBAS)) {
                                modalidadOk = true;
                                break;
                            }
                        }
                    }
                    if (!convenioOk) {
                        ErrorValidacion e = new ErrorValidacion();
                        e.setCodigo("Error 016");
                        e.setNumRegistro(registro);
                        e.setDescripcion("El concesionario indicado en el registro " + registro
                                + " no tiene convenio.");
                        listaErrores.add(e);
                    } else if (!modalidadOk) {
                        ErrorValidacion e = new ErrorValidacion();
                        e.setCodigo("Error 016");
                        e.setNumRegistro(registro);
                        e.setDescripcion("El concesionario indicado en el registro " + registro
                                + " no tiene la  modalidad indicada.");
                        listaErrores.add(e);
                    } else {
                        if (concesionario.getIdo() == null) {
                            concesionario = null;
                            ErrorValidacion e = new ErrorValidacion();
                            e.setCodigo("Error 016");
                            e.setNumRegistro(registro);
                            e.setDescripcion("El concesionario indicado en el registro " + registro
                                    + " no tiene IDO configurado.");
                            listaErrores.add(e);
                        }
                    }

                } else {
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error 016");
                    e.setNumRegistro(registro);
                    e.setDescripcion("El concesionario indicado en el registro " + registro + " no existe.");
                    listaErrores.add(e);
                }
            }
        }

        return concesionario;
    }

    /**
     * Comprueba si una central existe y seteamos los datos necesarios segun el caso.
     * @param central central
     * @param tipoCentral [origen,destino]
     * @param poblacion poblacion
     * @param registro registro
     * @return central
     */
    private Central crearCentral(Central central, String tipoCentral, Poblacion poblacion, String registro) {

        if (central != null && solicitud.getProveedorSolicitante() != null && poblacion != null) {

            central.setProveedor(solicitud.getProveedorSolicitante());
            central.setPoblacion(poblacion);
            central = centralDAO.comprobarCentral(central);

            if (central.getId() != null) {
                // Si la central existe pero el ABN no corresponde al registro persistimos una nueva relacion en
                // AbnCentral.
                if (!poblacion.getAbn().equals(central.getPoblacion().getAbn())) {
                    AbnCentral abnCentral = new AbnCentral();
                    abnCentral.setId(new AbnCentralPK());
                    abnCentral.getId().setIdAbn(central.getId().longValue());
                    abnCentral.getId().setIdAbn(poblacion.getAbn().getCodigoAbn().longValue());

                    AbnCentral abnCentralAux = abnCentralDAO.reload(abnCentral);

                    central.addAbnCentrales(abnCentralAux == null ? abnCentral : abnCentralAux);
                }

            } else {
                // Si no existe la central lo avisamos
                AvisoValidacionCentral e = new AvisoValidacionCentral();
                e.setCodigo("Aviso");
                e.setNumRegistro(registro);
                e.setCentral(central);
                e.setDescripcion("La Central " + tipoCentral + " en el registro " + registro
                        + " no existe en el catálogo.");
                listaAvisoCentral.add(e);
            }
        }
        return central;
    }

    /**
     * Validamos una central.
     * @param tipoCentral [origen,destino]
     * @param fila fila
     * @return central
     */
    private Central validarCentral(String tipoCentral, List<String> fila) {
        Central central = new Central();

        Integer col = 0;
        Boolean formatoOk = true;

        List<String> datos = new ArrayList<String>();
        if (tipoCentral.equals(ORIGEN)) {
            datos = fila.subList(7, 19);
        } else if (tipoCentral.equals(DESTINO)) {
            datos = fila.subList(19, 31);
        }

        String registro = fila.get(0);

        // Validamos el formato de los datos
        String nombre = datos.get(col++);

        if (!(StringUtils.isBlank(nombre) || isCero(nombre))) {
            if (nombre.length() <= 100) {
                central.setNombre(nombre);
            } else {
                formatoOk = false;
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setNumRegistro(registro);
                e.setDescripcion("El campo NOMBRE CENTRAL de la central " + tipoCentral + " indicada en el registro "
                        + registro + " tiene mayor longitud a la permitida");
                listaErrores.add(e);
            }
        } else {
            central.setNombre(DESCONOCIDA);
        }

        String strMarca = datos.get(col++);
        if (!StringUtils.isBlank(strMarca)) {
            if (strMarca.length() > 50) {
                formatoOk = false;
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setNumRegistro(registro);
                e.setDescripcion("El campo MARCA de la central " + tipoCentral + " indicada en el registro " + registro
                        + " tiene mayor longitud a la permitida");
                listaErrores.add(e);
            }
        } else {
            strMarca = DESCONOCIDA;
        }

        String strModelo = datos.get(col++);
        if (!StringUtils.isBlank(strModelo)) {
            if (strModelo.length() > 20) {
                formatoOk = false;
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setNumRegistro(registro);
                e.setDescripcion("El campo MODELO de la central " + tipoCentral + " indicada en el registro " + registro
                        + " tiene mayor longitud a la permitida");
                listaErrores.add(e);
            }
        } else {
            strModelo = "DESCONOCIDO";
        }

        String jerarquia = datos.get(col++);
        if (jerarquia.length() > 100) {
            formatoOk = false;
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error formato");
            e.setNumRegistro(registro);
            e.setDescripcion("El campo JERARQUIA de la central " + tipoCentral + " indicada en el registro " + registro
                    + " tiene mayor longitud a la permitida");
            listaErrores.add(e);
        } else {
            central.setJerarquia(jerarquia);
        }

        String calle = datos.get(col++);
        if (!StringUtils.isBlank(calle)) {
            if (calle.length() > 50) {
                formatoOk = false;
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setNumRegistro(registro);
                e.setDescripcion("El campo CALLE de la central " + tipoCentral + " indicada en el registro " + registro
                        + " tiene mayor longitud a la permitida");
                listaErrores.add(e);
            } else {
                central.setCalle(calle);
            }
        } else {
            central.setCalle("0");
        }

        String numero = datos.get(col++);
        if (!StringUtils.isBlank(numero)) {
            if (numero.length() > 30) {
                formatoOk = false;
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setNumRegistro(registro);
                e.setDescripcion("El campo NUMERO de la central " + tipoCentral + " indicada en el registro " + registro
                        + " tiene mayor longitud a la permitida");
                listaErrores.add(e);
            } else {
                central.setNumero(numero);
            }
        } else {
            central.setNumero("0");
        }

        String colonia = datos.get(col++);
        if (!StringUtils.isBlank(colonia)) {
            if (colonia.length() > 50) {
                formatoOk = false;
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setNumRegistro(registro);
                e.setDescripcion("El campo COLONIA de la central " + tipoCentral + " indicada en el registro "
                        + registro + " tiene mayor longitud a la permitida");
                listaErrores.add(e);
            } else {
                central.setColonia(colonia);
            }
        } else {
            central.setColonia("0");
        }

        String codPostal = datos.get(col++);
        if (!StringUtils.isBlank(codPostal) && !isCero(codPostal)) {
            if (codPostal.length() > 5) {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setNumRegistro(registro);
                e.setDescripcion("El campo CODIGO POSTAL de la central " + tipoCentral + " indicada en el registro "
                        + registro + " tiene mayor longitud a la permitida");
                listaErrores.add(e);
            } else if (codPostal.length() < 5) {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error 018");
                e.setNumRegistro(registro);
                e.setDescripcion("El campo CODIGO POSTAL de la central " + tipoCentral + " indicada en el registro "
                        + registro + " no es correcto.");
                listaErrores.add(e);
            } else {
                central.setCp(codPostal);
            }
        } else {
            central.setCp("0");
        }

        String latitud = datos.get(col++).replace(" ", "").replace("º", "°");
        if (!latitud.isEmpty() && !isCero(latitud) && !latitud.equals(COORDENADA_0)) {
            if (validarCoordenada(latitud, "latitud", registro, tipoCentral)) {
                central.setLatitud(latitud);
            } else {
                formatoOk = false;
            }
        } else {
            central.setLatitud(COORDENADA_0);
        }

        String longitud = datos.get(col++).replace(" ", "").replace("º", "°");
        if (!longitud.isEmpty() && !isCero(longitud) && !longitud.equals(COORDENADA_0)) {
            if (validarCoordenada(longitud, "longitud", registro, tipoCentral)) {
                central.setLongitud(longitud);
            } else {
                formatoOk = false;
            }
        } else {
            central.setLongitud(COORDENADA_0);
        }

        String strCapIni = datos.get(col++);
        String strCapFinal = datos.get(col++);

        BigDecimal capIni = null;
        BigDecimal capFinal = null;

        if (!StringUtils.isBlank(strCapIni)) {
            if (strCapIni.length() > 30) {
                formatoOk = false;
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setNumRegistro(registro);
                e.setDescripcion("El campo CAP INSTALADA de la central " + tipoCentral + " indicada en el registro "
                        + registro + " tiene mayor longitud a la permitida");
                listaErrores.add(e);
            } else {
                if (isNumeric(strCapIni)) {
                    capIni = new BigDecimal(strCapIni);
                } else {
                    formatoOk = false;
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error formato");
                    e.setNumRegistro(registro);
                    e.setDescripcion("El campo CAP INSTALADA de la central " + tipoCentral + " indicada en el registro "
                            + registro + " debe ser numero.");
                    listaErrores.add(e);
                }
            }
        } else {
            capIni = new BigDecimal(0);
        }

        if (!StringUtils.isBlank(strCapFinal)) {
            if (strCapFinal.length() > 30) {
                formatoOk = false;
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setNumRegistro(registro);
                e.setDescripcion("El campo CAP FINAL de la central " + tipoCentral + " indicada en el registro "
                        + registro + " tiene mayor longitud a la permitida");
                listaErrores.add(e);
            } else {
                if (isNumeric(strCapIni)) {
                    capFinal = new BigDecimal(strCapIni);
                } else {
                    formatoOk = false;
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error formato");
                    e.setNumRegistro(registro);
                    e.setDescripcion("El campo CAP FINAL de la central " + tipoCentral + " indicada en el registro "
                            + registro + " debe ser numero.");
                    listaErrores.add(e);
                }
            }
        } else {
            capFinal = new BigDecimal(0);
        }

        if (capIni != null && capFinal != null) {
            central.setCapacidadInicial(capIni);
            central.setCapacidadFinal(capFinal);
            if (capFinal.compareTo(capIni) < 0) {
                AvisoValidacionCentral e = new AvisoValidacionCentral();
                e.setCodigo("Aviso");
                e.setNumRegistro(registro);
                e.setDescripcion("El campo CAP FINAL de la central " + tipoCentral + " indicada en el registro "
                        + registro + " debe ser mayor al campo CAP INSTALADA.");
                listaAvisoNoObligatorios.add(e);
            }
        }

        if (formatoOk) {
            // Validamos marca y modelo
            Marca marca = marcaDao.getMarcaByNombre(strMarca);

            if (marca == null) {
                marca = new Marca();
                marca.setNombre(nombre);
                marca.setEstatus(new Estatus());
                marca.getEstatus().setCdg(Estatus.ACTIVO);
                marca.updateAuditableValues(usuariosService.getCurrentUser());
            }

            Modelo modelo = modeloDao.getModeloByMarca(marca.getId(), strModelo);

            if (modelo == null) {
                modelo = new Modelo();
                modelo.setTipoModelo(strModelo);
                modelo.setDescripcion(strModelo);
                modelo.setEstatus(new Estatus());
                modelo.getEstatus().setCdg(Estatus.ACTIVO);
                modelo.updateAuditableValues(usuariosService.getCurrentUser());
            }

            modelo.setMarca(marca);
            central.setModelo(modelo);
        }

        return formatoOk ? central : null;

    }

    /**
     * Valida el formato y cuadrante de una coordenada.
     * @param coordenada coordenada
     * @param tipo longitud/latitud
     * @param registro registro
     * @param tipoCentral origen/destino
     * @return true/false
     */
    private boolean validarCoordenada(String coordenada, String tipo, String registro, String tipocentral) {

        Pattern patron = Pattern.compile(PATTERN_COORDENADA);

        boolean formatoOk = patron.matcher(coordenada).find();
        boolean cuadranteOk = true;

        if (formatoOk) {
            int indexGrado = coordenada.indexOf("°");
            int indexMin = coordenada.indexOf("'");
            int indexSeg = coordenada.indexOf("\"");

            if (formatoOk) {
                int grado = Integer.parseInt(coordenada.substring(0, indexGrado));
                Double min = Double.parseDouble(coordenada.substring(indexGrado + 1, indexMin));
                Double seg = Double.parseDouble(coordenada.substring(indexMin + 1, indexSeg));
                if (tipo.equals("longitud")) {
                    if (!(MIN_LONGITUD <= grado && grado <= MAX_LONGITUD)) {
                        cuadranteOk = false;
                    }
                } else if (tipo.equals("latitud")) {
                    if (!(MIN_LATITUD <= grado && grado <= MAX_LATITUD)) {
                        cuadranteOk = false;
                    }
                }
                if (!(MIN_MINUTOS <= min && min < MAX_MINUTOS) || !(MIN_MINUTOS <= seg && seg < MAX_MINUTOS)) {
                    cuadranteOk = false;
                }
            }

        }

        if (!formatoOk) {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 010");
            e.setNumRegistro(registro);
            e.setDescripcion("El formato de la " + tipo + " de la central " + tipocentral + " para el registro "
                    + registro + " no cumple con el formato correcto.");
            listaErrores.add(e);
        }

        if (!cuadranteOk) {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 010");
            e.setNumRegistro(registro);
            e.setDescripcion("La " + tipo + " de la central " + tipocentral + " para el registro" + registro
                    + " no se encuentra dentro del cuadrante definido para México. ");
            listaErrores.add(e);
        }

        return formatoOk && cuadranteOk;
    }

    /**
     * Comprueba si una cadena tiene como valor numerico cero.
     * @param cadena cadena
     * @return true/false
     */
    private boolean isCero(String cadena) {
        if (isNumeric(cadena)) {
            return Integer.parseInt(cadena) == 0;
        } else {
            return false;
        }
    }

    /**
     * Valida la cabecera del archivo.
     * @throws Exception error
     */
    private void validarCabecera() throws Exception {

        List<String> cabecera = listaDatos.get(1);

        // Validamos fechas
        validarFechas(cabecera);

        // Validamos referencia
        String strRef = cabecera.get(2);
        if (!(StringUtils.isBlank(strRef) || isCero(strRef))) {
            // Si la referencia tiene un tamaño mayor a 60 truncamos
            if (strRef.length() > 60) {
                strRef = strRef.substring(0, 60);
            }
            solicitud.setReferencia(strRef);
        } else {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 012");
            e.setDescripcion("La referencia de solicitud para el archivo cargado está vacía o en cero.");
            listaErrores.add(e);
        }

        // Validamos totales
        String strTotalReg = cabecera.get(3);
        if (!StringUtils.isBlank(strTotalReg)) {
            if (isNumeric(strTotalReg)) {
                int totalReg = Integer.parseInt(strTotalReg);
                if (totalReg != listaDatos.size() - 3) {
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error 001");
                    e.setDescripcion("El total de registros indicados no coincide "
                            + "con el número de registros en el detalle de la solicitud.");
                    listaErrores.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setDescripcion("El campo TOTAL REG debe ser un número.");
                listaErrores.add(e);
            }
        } else {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error formato");
            e.setDescripcion("El campo TOTAL REG es obligatorio.");
            listaErrores.add(e);
        }

        String strTotalNumSol = cabecera.get(4);
        if (!StringUtils.isBlank(strTotalNumSol)) {
            if (isNumeric(strTotalNumSol)) {
                int totalNumSol = Integer.parseInt(strTotalNumSol);
                if (totalNumSol != sumaTotalNumeraciones("")) {
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error 002");
                    e.setDescripcion("El total de numeraciones solicitadas no coincide "
                            + "con el total de numeración en el detalle de la solicitud.");
                    listaErrores.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setDescripcion("El campo TOTAL NUM debe ser un número.");
                listaErrores.add(e);
            }
        } else {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error formato");
            e.setDescripcion("El campo TOTAL NUM es obligatorio.");
            listaErrores.add(e);
        }

        String strTotalFijo = cabecera.get(5);
        if (!StringUtils.isBlank(strTotalFijo)) {
            if (isNumeric(strTotalFijo)) {
                int totalFijo = Integer.parseInt(strTotalFijo);
                if (totalFijo != sumaTotalNumeraciones(FIJO)) {
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error 003");
                    e.setDescripcion("El total de numeraciones solicitadas en modalidad Fija "
                            + "no coincide con el total de numeración en el detalle de la solicitud.");
                    listaErrores.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setDescripcion("El campo TOTAL FIJO debe ser un número.");
                listaErrores.add(e);
            }
        } else {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error formato");
            e.setDescripcion("El campo TOTAL FIJO es obligatorio.");
            listaErrores.add(e);
        }

        String strTotalCpp = cabecera.get(6);
        if (!StringUtils.isBlank(strTotalCpp)) {
            if (isNumeric(strTotalCpp)) {
                int totalCpp = Integer.parseInt(strTotalCpp);
                if (totalCpp != sumaTotalNumeraciones(CPP)) {
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error 004");
                    e.setDescripcion("El total de numeraciones solicitadas en modalidad CPP "
                            + "no coincide con el total de numeración en el detalle de la solicitud.");
                    listaErrores.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setDescripcion("El campo TOTAL CPP debe ser un número.");
                listaErrores.add(e);
            }
        } else {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error formato");
            e.setDescripcion("El campo TOTAL CPP es obligatorio.");
            listaErrores.add(e);
        }

        String strTotalMpp = cabecera.get(7);
        if (!StringUtils.isBlank(strTotalMpp)) {
            if (isNumeric(strTotalMpp)) {
                int totalMpp = Integer.parseInt(strTotalMpp);
                if (totalMpp != sumaTotalNumeraciones(MPP)) {
                    ErrorValidacion e = new ErrorValidacion();
                    e.setCodigo("Error 005");
                    e.setDescripcion("El total de numeraciones solicitadas en modalidad MPP no coincide"
                            + " con el total de numeración en el detalle de la solicitud.");
                    listaErrores.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setDescripcion("El campo TOTAL MPP debe ser un número.");
                listaErrores.add(e);
            }
        } else {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error formato");
            e.setDescripcion("El campo TOTAL MPP  es obligatorio.");
            listaErrores.add(e);
        }
    }

    /**
     * Valida las fechas de la cabecera.
     * @param cabecera cabecera
     */
    private void validarFechas(List<String> cabecera) {
        Date fechaHoy = FechasUtils.getFechaHoy();

        String strFechaSol = cabecera.get(1);
        Date fechaSol = null;
        if (!(StringUtils.isBlank(strFechaSol) || isCero(strFechaSol))) {
            if (isFechaValida(strFechaSol)) {
                fechaSol = FechasUtils.stringToFecha(strFechaSol);
                solicitud.setFechaSolicitud(fechaSol);
                if (fechaSol.after(fechaHoy)) {
                    AvisoValidacionCentral e = new AvisoValidacionCentral();
                    e.setCodigo("Aviso");
                    e.setDescripcion("La fecha de solicitud es mayor que la fecha del día en curso.");
                    listaAvisoNoObligatorios.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setDescripcion("La fecha de solicitud no cumple el formato dd/MM/YYYY.");
                listaErrores.add(e);
            }
        } else {
            ErrorValidacion e = new ErrorValidacion();
            e.setCodigo("Error 011");
            e.setDescripcion("La fecha de solicitud para el archivo cargado está vacía o en cero.");
            listaErrores.add(e);
        }

        String strFechaPruebas = cabecera.get(9);
        Date fechaPruebas = null;
        if (!(StringUtils.isBlank(strFechaPruebas) || isCero(strFechaPruebas))) {
            if (isFechaValida(strFechaPruebas)) {
                fechaPruebas = FechasUtils.stringToFecha(strFechaPruebas);
                solicitud.setFechaIniPruebas(fechaPruebas);
                if (fechaSol != null && fechaPruebas.before(fechaSol)) {
                    AvisoValidacionCentral e = new AvisoValidacionCentral();
                    e.setCodigo("Aviso");
                    e.setDescripcion("La fecha de pruebas es menor que la fecha de solicitud.");
                    listaAvisoNoObligatorios.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setDescripcion("La fecha de pruebas no cumple el formato dd/MM/YYYY.");
                listaErrores.add(e);
            }
        } else {
            solicitud.setFechaIniPruebas(null);
            AvisoValidacionCentral e = new AvisoValidacionCentral();
            e.setCodigo("Aviso");
            e.setDescripcion("La fecha de pruebas para el archivo cargado está vacía o en cero.");
            listaAvisoNoObligatorios.add(e);
        }

        String strFechaUtiliz = cabecera.get(8);
        if (!(StringUtils.isBlank(strFechaUtiliz) || isCero(strFechaUtiliz))) {
            if (isFechaValida(strFechaUtiliz)) {
                Date fechaUtiliz = FechasUtils.stringToFecha(strFechaUtiliz);
                solicitud.setFechaIniUtilizacion(fechaUtiliz);
                if (fechaPruebas != null && !fechaUtiliz.after(fechaPruebas)) {
                    AvisoValidacionCentral e = new AvisoValidacionCentral();
                    e.setCodigo("Aviso");
                    e.setDescripcion("La fecha de inicio de utilización es menor o igual que la fecha de pruebas.");
                    listaAvisoNoObligatorios.add(e);
                }
            } else {
                ErrorValidacion e = new ErrorValidacion();
                e.setCodigo("Error formato");
                e.setDescripcion("La fecha de inicio de utilización no cumple el formato dd/MM/YYYY.");
                listaErrores.add(e);
            }
        } else {
            solicitud.setFechaIniUtilizacion(null);
            AvisoValidacionCentral e = new AvisoValidacionCentral();
            e.setCodigo("Aviso");
            e.setDescripcion("La fecha de inicio de utilización para el archivo cargado está vacía o en cero.");
            listaAvisoNoObligatorios.add(e);
        }
    }

    /**
     * Suma el total de cantidad de numeracion solicitadad segun la modalidad. Si esta vacia suma todos.
     * @param modalidad modalidad
     * @return total
     */
    private int sumaTotalNumeraciones(String modalidad) {
        int total = 0;
        for (int i = 3; i < listaDatos.size(); i++) {
            List<String> registro = listaDatos.get(i);

            if (!StringUtils.isBlank(modalidad)) {
                if (registro.get(6).equals(modalidad)) {
                    if (isNumeric(registro.get(5))) {
                        total += Integer.parseInt(registro.get(5));
                    }
                }
            } else {
                if (isNumeric(registro.get(5))) {
                    total += Integer.parseInt(registro.get(5));
                }
            }

        }
        return total;
    }

    /**
     * Método que extrae los datos de un formato csv.
     * @param fichero fichero.
     * @return List<List<String>> lista de listas.
     * @throws Exception exception.
     */
    private List<List<String>> extraerCsv(File fichero) throws Exception {
        CsvReader csvReader = null;
        String delimitador = ",";
        FileReader freader = null;
        List<List<String>> listaDatos = new ArrayList<List<String>>();

        try {
            freader = new FileReader(fichero);
            csvReader = new CsvReader(freader, delimitador.charAt(0));
            String[] headers = null;
            String[] valores = null;

            if (csvReader.readHeaders()) {
                headers = csvReader.getHeaders();
                listaDatos.add(Arrays.asList(headers));

                while (csvReader.readRecord()) {
                    valores = csvReader.getValues();
                    List<String> data = new ArrayList<String>(Arrays.asList(valores));
                    if (data.size() < MAX_COLUMNAS) {
                        for (int j = data.size(); j <= MAX_COLUMNAS; j++) {
                            data.add("");
                        }
                    }
                    listaDatos.add(data);
                }
            } else {
                throw new Exception("Fichero mal formado.");
            }
        } catch (IOException e) {
            LOGGER.info("Error Inesperado: " + e.getMessage());
        } finally {

            if (csvReader != null) {
                csvReader.close();
            }

            if (freader != null) {
                freader.close();
            }
        }

        return listaDatos;
    }

    /**
     * Método que extrae los datos de un formato xls.
     * @param fichero fichero.
     * @return List<List<String>> lista de listas.
     * @throws Exception exception.
     */
    @SuppressWarnings({"resource", "unused"})
    private List<List<String>> extraerExcel(File fichero) throws Exception {
        List<List<String>> sheetData = new ArrayList<List<String>>();
        FileInputStream fis = null;
        HSSFWorkbook workbook = null;
        try {
            fis = new FileInputStream(fichero);

            workbook = new HSSFWorkbook(fis);
            workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();

            HSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.rowIterator();
            while (rows.hasNext()) {
                HSSFRow row = (HSSFRow) rows.next();

                List<String> data = new ArrayList<String>();

                int numCells = row.getLastCellNum();
                for (int i = 0; i < numCells; i++) {

                    HSSFCell cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);

                    if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
                            || cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {

                        if (DateUtil.isCellDateFormatted(cell)) {
                            DataFormatter df = new DataFormatter();
                            data.add(df.formatCellValue(cell));
                        } else {

                            data.add(String.valueOf((int) cell.getNumericCellValue()));
                        }

                    } else {
                        data.add(cell.getStringCellValue());
                    }

                }

                if (!isAllBlank(data)) {
                    if (data.size() < MAX_COLUMNAS) {
                        for (int j = data.size(); j <= MAX_COLUMNAS; j++) {
                            data.add("");
                        }
                    }
                    sheetData.add(data);
                }
            }

        } catch (IOException e) {
            LOGGER.info("Error Inesperado: " + e.getMessage());
        } finally {
            workbook = null;
            if (fis != null) {
                fis.close();
            }
        }
        return sheetData;
    }

    /**
     * Método que extrae los datos de un formato xlsx.
     * @param fichero fichero.
     * @return List<List<String>> lista de listas.
     * @throws Exception excepcion.
     */
    @SuppressWarnings("resource")
    private List<List<String>> extraerExcelNuevo(File fichero) throws Exception {
        List<List<String>> sheetData = new ArrayList<List<String>>();
        FileInputStream fis = null;
        XSSFWorkbook workbook = null;

        try {
            fis = new FileInputStream(fichero);

            workbook = new XSSFWorkbook(fis);

            workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();

            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.rowIterator();
            while (rows.hasNext()) {
                XSSFRow row = (XSSFRow) rows.next();

                List<String> data = new ArrayList<String>();

                int numCells = row.getLastCellNum();
                for (int i = 0; i < numCells; i++) {
                    XSSFCell cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);

                    if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                            || cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {

                        if (DateUtil.isCellDateFormatted(cell)) {
                            DataFormatter df = new DataFormatter();
                            data.add(df.formatCellValue(cell));
                        } else {

                            data.add(String.valueOf((int) cell.getNumericCellValue()));
                        }

                    } else {
                        data.add(cell.getStringCellValue());
                    }

                }

                if (!isAllBlank(data)) {
                    if (data.size() < MAX_COLUMNAS) {
                        for (int j = data.size(); j <= MAX_COLUMNAS; j++) {
                            data.add("");
                        }
                    }
                    sheetData.add(data);
                }
            }

        } catch (IOException e) {
            LOGGER.info("Error Inesperado: " + e.getMessage());
        } finally {
            workbook = null;
            if (fis != null) {
                fis.close();

            }
        }
        return sheetData;
    }

    /**
     * Comprueba si en una lista de cadenas estan todas vacias.
     * @param lista cadenas
     * @return boolean
     */
    private boolean isAllBlank(List<String> lista) {
        for (String cadena : lista) {
            if (!StringUtils.isBlank(cadena)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Método para saber si es numerica la cadena.
     * @param cadena cadena de entrada.
     * @return boolean true/false.
     */
    private static boolean isNumeric(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Metodo que comprueba si la fecha esta en el formato dd/MM/yyyy.
     * @param fecha fecha
     * @return boolean
     */
    private static boolean isFechaValida(String fecha) {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            if (fecha.trim().length() != formatoFecha.toPattern().trim().length()) {
                return false;
            }
            formatoFecha.setLenient(false);
            formatoFecha.parse(fecha.trim());
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

}
