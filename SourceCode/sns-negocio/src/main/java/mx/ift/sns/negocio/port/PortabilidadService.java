package mx.ift.sns.negocio.port;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.persistence.NoResultException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import mx.ift.sns.dao.port.IEstatusSincronizacionDao;
import mx.ift.sns.dao.port.INumerosCanceladosDAO;
import mx.ift.sns.dao.port.INumerosPortadosDAO;
import mx.ift.sns.modelo.port.EstatusSincronizacion;
import mx.ift.sns.modelo.port.NumeroCancelado;
import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.conf.IParametrosService;
import mx.ift.sns.negocio.exceptions.SincronizacionABDException;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;
import mx.ift.sns.negocio.not.IMailService;
import mx.ift.sns.negocio.port.modelo.ResultadoParser;
import mx.ift.sns.negocio.utils.csv.ValidadorArchivoDeletedCSV;
import mx.ift.sns.negocio.utils.csv.ValidadorArchivoDeletedCSV2;
import mx.ift.sns.negocio.utils.csv.ValidadorArchivoPortadosCSV;
import mx.ift.sns.negocio.utils.csv.ValidadorArchivoPortadosCSV2;
import mx.ift.sns.utils.WeblogicNode;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.utils.file.FicheroTemporal;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileNotFoundException;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Servicio de Portabilidad. Mantiene y sincroniza la BBDD de portabilidad con el ABD.
 */
@Stateless(name = "PortabilidadService", mappedName = "PortabilidadService")
@Remote(IPortabilidadService.class)
public class PortabilidadService implements IPortabilidadService {

    //FJAH 26052025
    public PortabilidadService() {
        // Constructor vacío requerido por EJB
    }

    /*
    //FECHA PROCESO
    Date FECHA_PROCESO = FechasUtils.getFechaHoy("dd.MM.yyyy");
    //termina Fecha Proceso}

     */


    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PortabilidadService.class);

    /** Mensaje de bitacora sync ok. */
    private static final String BIT_MSG_SYNC_OK = "Sincronización con ABD correcta";

    /** Mensaje de bitacora sync manual ok. */
    private static final String BIT_MSG_SYNC_MANUAL_OK = "Carga de ficheros manual correcta";

    /** Mensaje de bitacora sync error. */
    private static final String BIT_MSG_SYNC_ERROR = "Sincronización con ABD incorrecta.";

    /** Mensaje de bitacora sync manual error. */
    private static final String BIT_MSG_SYNC_MANUAL_ERROR = "Carga de ficheros manual incorrecta.";

    /** Mensaje de bitacora sync error. */
    private static final String BIT_MSG_ERR_CONFIG = "Error configuracion Sincronización con ABD.";

    /** Numero maximo de reintentos. */
    private static final BigDecimal MAX_REINTENTOS = new BigDecimal(3);

    /** Formato de fecha 1. */
    private static final String FORMATO_FECHA1 = "yyyyMMdd";

    /** Formato de fecha 2. */
    private static final String FORMATO_FECHA2 = "yyyy.MM.dd";

    /** Servicio configuracion. */
    @EJB
    private IParametrosService paramService;

    /** Servicio correo. */
    @EJB
    private IMailService mailService;

    /** Servicio de bitácora. */
    @EJB
    private IBitacoraService bitacoraService;

    //FJAH 24052025
    @EJB
    private ValidadorArchivoDeletedCSV validadorArchivoDeletedCSV;

    @EJB
    private ValidadorArchivoDeletedCSV2 importDeleted;

    @EJB
    private ValidadorArchivoPortadosCSV validadorArchivoPortadosCSV;

    @EJB
    private ValidadorArchivoPortadosCSV2 validadorArchivoPortadosCSV2;

    /** FJAH DAO portados  */
    @EJB
    private PortadosDAO portadosDAO;


    /** DAO de status de portabilidad. */
    @Inject
    private IEstatusSincronizacionDao statusDAO;

    /** DAO de numeros portados. */
    @Inject
    private INumerosPortadosDAO numerosPortadosDAO;

    /** DAO de numeros cancelados. */
    @Inject
    private INumerosCanceladosDAO numerosCanceladosDAO;

    /** host del ABD. */
    private String host;

    /** puerto del ABD. */
    private String port;

    /** usuario del ABD. */
    private String user;

    /** clave del ABD. */
    private String pwd;

    /** path del ABD. */
    private String path;

    /** la lista de mail. */
    private String mailList;

    /** Contexto. */
    @Resource
    private SessionContext sc;

    /** me. */
    private IPortabilidadService me;
    
    private HashMap<String, String> conectionParams;
    
   
    /**
     * Inicialización del ejb.
     */
    @PostConstruct
    public void init() {
        this.me = this.sc.getBusinessObject(IPortabilidadService.class);
    }

    /**
     * Comprueba si todos los parametros estan configurados.
     * @return true si la configuracion es correcta
     */
    private boolean checkConfig() {
        boolean res = true;

        try {
            host = paramService.getParamByName(ParametrosABD.ABD_HOST);
            port = paramService.getParamByName(ParametrosABD.ABD_PORT);
            user = paramService.getParamByName(ParametrosABD.ABD_USER);
            pwd = paramService.getParamByName(ParametrosABD.ABD_PASSWORD);
            path = paramService.getParamByName(ParametrosABD.ABD_PATH);
            mailList = paramService.getParamByName(ParametrosABD.ABD_MAIL_LIST);
        } catch (Exception e) {
            res = false;
        }

        if (StringUtils.isEmpty(host)) {
            LOGGER.error("No esta definido " + ParametrosABD.ABD_HOST);
            res = false;
        }

        if (StringUtils.isEmpty(port)) {
            LOGGER.error("No esta definido " + ParametrosABD.ABD_PORT);
            res = false;
        }

        if (StringUtils.isEmpty(user)) {
            LOGGER.error("No esta definido " + ParametrosABD.ABD_USER);
            res = false;
        }

        if (StringUtils.isEmpty(pwd)) {
            LOGGER.error("No esta definido " + ParametrosABD.ABD_PASSWORD);
            res = false;
        }

        if (StringUtils.isEmpty(path)) {
            LOGGER.error("No esta definido " + ParametrosABD.ABD_PATH);
            res = false;
        }

        if (StringUtils.isEmpty(mailList)) {
            LOGGER.error("No esta definido " + ParametrosABD.ABD_MAIL_LIST);
            res = false;
        }
        
        if(res){
        	conectionParams=new HashMap<String,String>();
        	conectionParams.put("host",host);
        	conectionParams.put("port",port);
        	conectionParams.put("user",user);
        	conectionParams.put("pwd",pwd);
        	conectionParams.put("path",path);
        	        	
        }

        return res;
    }

    /**
     * Construye el nombre del fichero de numeros portados.
     * @param d fecha
     * @return nombre
     */
    private String getNumbersPortedFileName(Date d) throws ParseException {

        String dateToStr = FechasUtils.fechaToString(d, FORMATO_FECHA1);
        String dateToStr2 = FechasUtils.fechaToString(d, FORMATO_FECHA2);

        StringBuilder b = new StringBuilder();

        b.append(path);
        b.append("/");
        b.append(dateToStr2);
        b.append("/");
        b.append("NumbersPorted-");
        b.append(dateToStr);
        b.append(".xml");

        return b.toString();
    }

    /**
     * Construye el nombre del fichero de numeros borrados.
     * @param d fecha
     * @return nombre
     */
    private String getNumbersDeletedFileName(Date d) throws ParseException{

        String dateToStr = FechasUtils.fechaToString(d, FORMATO_FECHA1);
        String dateToStr2 = FechasUtils.fechaToString(d, FORMATO_FECHA2);

        StringBuilder b = new StringBuilder();

        b.append(path);
        b.append("/");
        b.append(dateToStr2);
        b.append("/");

        b.append("NumbersDeleted-");
        b.append(dateToStr);
        b.append(".xml");

        return b.toString();
    }

    /**
     * Obtieene el estado.
     * @return status
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private EstatusSincronizacion getStatus() {
        EstatusSincronizacion status = null;
        try {
            status = statusDAO.get();
        } catch (NoResultException e) {
            status = new EstatusSincronizacion();
            status.setTs(new Date());
            //status.setTs(FECHA_PROCESO);

            status.setReintentos(BigDecimal.ZERO);
        }

        return status;
    }

    /**
     * Guarda el estado.
     * @param status status
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveStatus(EstatusSincronizacion status) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}", status);
        }

        try {
            status.setId(BigDecimal.ZERO);
            statusDAO.update(status);
        } catch (Exception e) {
            LOGGER.error("No se puede actualizar status portabilidad.", e);
        }
    }

    /**
     * formatea el mensaje a bitacora.
     * @param msg cadena
     * @param estatus de la sincronizacion
     * @return cadena formateada
     * @throws Exception Error
     */
    private String formatMensaje(String msg, EstatusSincronizacion estatus) throws Exception {
        StringBuilder b = new StringBuilder();

        b.append(msg);

        b.append(" estatus=").append(estatus.getEstatus());

        b.append(" port=").append(estatus.getPortProcesadas());
        b.append(" / ").append(estatus.getPortProcesar());

        b.append(" / ");
        b.append(FechasUtils.fechaToString(estatus.getProcesadasTs(), FechasUtils.FORMATO_FECHA_HORA));

        b.append(" canceladas=").append(estatus.getPortCanceladas());
        b.append(" / ").append(estatus.getPortCancelar());

        b.append(" / ");
        b.append(FechasUtils.fechaToString(estatus.getCanceladasTs(), FechasUtils.FORMATO_FECHA_HORA));

        return b.toString();
    }
	/**
	 * Realiza la conversion del archivo XML a CSV, valida los campos y realiza la carga a la BD dependiendo de la validación.
	 * 
	 */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    //public EstatusSincronizacion parsePortabilidad(File tmpPorted, EstatusSincronizacion status) throws Exception {
    public ResultadoValidacionCSV parsePortabilidad(File tmpPorted, EstatusSincronizacion status) throws Exception {
        //LOGGER.info("[parsePortabilidad] valor recibido del status hash: {}", System.identityHashCode(status));
        File tmpPortedCSV = null;

        try {
            ResultadoParser res = new ResultadoParser();
            res.setTimestamp(new Date()); // evita NPE si parser no asigna fecha
            int totalOrigen = 0;

            tmpPortedCSV = FicheroTemporal.getTmpFileName();
            LOGGER.info("Parseamos a CSV fichero: " + tmpPortedCSV);
            // parseamos XML -> CSV
            //==================================================
            // Clase refactorizada
            // NumerosPortadosXmlParser parserPort = new NumerosPortadosXmlParser();
            // parserPort.parse(tmpPorted.getCanonicalPath(), tmpPortedCSV, res);
            //==================================================
            // Crear lista para acumular registros inválidos (estructura o CSV)
            List<String> registrosInvalidos = new ArrayList<>();

            NumerosPortadosXmlParser parser = new NumerosPortadosXmlParser(new FileWriter(tmpPortedCSV));
            parser.setRegistrosInvalidosGlobal(registrosInvalidos);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            //usamos streaming (isCancelados = true)
            LOGGER.info("Validamos fichero temporal obtenido para validar integridad: " + tmpPorted+" longitud "+tmpPorted.length());
            totalOrigen = contarPortData(tmpPorted);
            LOGGER.info("Total de registros (origen XML): {}", totalOrigen);

            InputSource sanitizedSource = sanitizeAndValidateXml(tmpPorted, registrosInvalidos, false);

            if (sanitizedSource == null) {
                LOGGER.error("Archivo {} descartado por errores críticos en cabecera/pie", tmpPorted.getName());

                // Generar log de inválidos para evidencias QA/Prod
                if (!registrosInvalidos.isEmpty()) {
                    generarLogInvalidos(registrosInvalidos, "Portados");
                }

                // Retornamos resultado truncado sin tocar BD ni generar XML fallidos
                ResultadoValidacionCSV resultadoFallback = new ResultadoValidacionCSV();
                resultadoFallback.setIdEstatus(status.getId());
                resultadoFallback.setTs(status.getTs());
                resultadoFallback.setEstatus(status.getEstatus());
                resultadoFallback.setReintentos(status.getReintentos());
                resultadoFallback.setPortProcesadas(BigDecimal.ZERO); // nada procesado
                resultadoFallback.setPortProcesar(BigDecimal.ZERO);   // nada por procesar
                resultadoFallback.setProcesadasTs(new Timestamp(System.currentTimeMillis()));
                resultadoFallback.setActionDateLoteDate(new Date());
                resultadoFallback.setTotalErrorEstructura(registrosInvalidos.size());
                resultadoFallback.setTotalOrigen(0); // 🚫 origen inválido
                return resultadoFallback;
            }

            saxParser.parse(sanitizedSource, parser);

            if (parser != null) {
                try {
                    Field writerField = NumerosPortadosXmlParser.class.getDeclaredField("writer");
                    writerField.setAccessible(true);
                    BufferedWriter bw = (BufferedWriter) writerField.get(parser);
                    if (bw != null) {
                        bw.flush();
                        bw.close();
                    }
                    try (BufferedReader br = new BufferedReader(new FileReader(tmpPortedCSV))) {
                        String linea;
                        int contador = 0;
                        while ((linea = br.readLine()) != null && contador < 5) {
                            //LOGGER.info("CSV TEMPORAL [{}]: {}", contador + 1, linea);
                            contador++;
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error leyendo CSV temporal", e);
                    }
                } catch (Exception e) {
                    LOGGER.warn("No se pudo cerrar el writer del parser", e);
                }
            }

            int invalidosXml = 0;
            if (registrosInvalidos != null) {
                for (String reg : registrosInvalidos) {
                    // Contamos solo los bloques PortData inválidos
                    if (reg.startsWith("<PortData")) {
                        invalidosXml++;
                    }
                }
            }
            LOGGER.info("Parse {} completado -> InvalidosXml={}", "Cancelados", invalidosXml);

            // Guardar log de inválidos en archivo físico (si los hay)
            if (!registrosInvalidos.isEmpty()) {
                generarLogInvalidos(registrosInvalidos,"Portados");
            }

            // actualizar status con datos iniciales
            status.setPortProcesadas(res.getNumerosOk());
            status.setPortProcesar(res.getNumerosTotal());
            status.setProcesadasTs(new Timestamp(res.getTimestamp().getTime()));

            //LOGGER.info("***-************- status:"+status.toString());
            LOGGER.info("Validamos fichero: " + tmpPortedCSV+" longitud "+tmpPortedCSV.length());

            // importamos portados
            //FJAH 24052025
            //ValidadorArchivoPortadosCSV importPortados = new ValidadorArchivoPortadosCSV();

            LOGGER.info("<--Inicia el procesamiento del csv de portados {}",FechasUtils.getActualDate());

//            Cargar BD
            //importPortados.validar(tmpPortedCSV.getAbsolutePath());
            //validadorArchivoPortadosCSV.validar(tmpPortedCSV.getAbsolutePath());
            //LOGGER.info("[parsePortabilidad] Llamando a validar para el archivo CSV: {}", tmpPortedCSV.getAbsolutePath());

            // importar portados y validar
            LOGGER.info("<--Ingresa al proceso de validar 7 pasos... {}",FechasUtils.getActualDate());
            ResultadoValidacionCSV resultadoValidacionCSV = validadorArchivoPortadosCSV.validar(tmpPortedCSV.getAbsolutePath());
            //LOGGER.info("[parsePortabilidad] Valor recibido de actionDateLote en resultadoValidacionCSV: '{}'", resultadoValidacionCSV.getActionDateLote());

            // sincronizar actionDateLote en el status
            if (resultadoValidacionCSV.getActionDateLote() != null) {
                //Date fechaActionDateLote = null;
                try {
                    Date fechaActionDateLote =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(resultadoValidacionCSV.getActionDateLote());
                    status.setActionDateLote(fechaActionDateLote);
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // sin .S // formato correcto
                    //fechaActionDateLote = sdf.parse(resultadoValidacionCSV.getActionDateLote());
                    //LOGGER.info("[parsePortabilidad] Fecha parseada de actionDateLote: {}", fechaActionDateLote);
                } catch (ParseException e) {
                    LOGGER.error("[parsePortabilidad] Error al parsear actionDateLote: '{}'. Exception: {}", resultadoValidacionCSV.getActionDateLote(), e.getMessage());
                    status.setActionDateLote(new Date()); // fallback
                }
                //LOGGER.info("[parsePortabilidad] ANTES de asignar valor al actiondate en el status hash: {}", System.identityHashCode(status));
                //status.setActionDateLote(fechaActionDateLote);
                //LOGGER.info("[parsePortabilidad] Se asignó actionDateLote en status: {}", fechaActionDateLote);
                //LOGGER.info("[parsePortabilidad] DESPUES de recibir actiondate status hash: {}", System.identityHashCode(status));
            }else{
                //LOGGER.warn("[parsePortabilidad] No se obtuvo actionDateLote del lote (valor nulo o vacío).");
                status.setActionDateLote(new Date());
                //status.setActionDateLote(FECHA_PROCESO);
            }

            LOGGER.info("<--finaliza el procesamiento del csv de portados {}", FechasUtils.getActualDate());

            //LOGGER.info("[parsePortabilidad] status hash: {}", System.identityHashCode(status));
            //LOGGER.info("[parsePortabilidad] VALOR status.getActionDateLote EN EL RETURN status: {}", status.getActionDateLote());
            //return status;
            resultadoValidacionCSV.setIdEstatus(status.getId());
            resultadoValidacionCSV.setTs(status.getTs());
            resultadoValidacionCSV.setEstatus(status.getEstatus());
            resultadoValidacionCSV.setReintentos(status.getReintentos());
            resultadoValidacionCSV.setPortProcesadas(status.getPortProcesadas());
            resultadoValidacionCSV.setPortProcesar(status.getPortProcesar());
            resultadoValidacionCSV.setProcesadasTs(status.getProcesadasTs());
            resultadoValidacionCSV.setActionDateLoteDate(status.getActionDateLote());

            // Sumar los inválidos detectados en el XML
            int totalErroresEstructura = resultadoValidacionCSV.getTotalErrorEstructura() + invalidosXml;
            resultadoValidacionCSV.setTotalErrorEstructura(totalErroresEstructura);
            resultadoValidacionCSV.setTotalOrigen(totalOrigen);

            return resultadoValidacionCSV;

        } catch (Exception e) {
            LOGGER.info("ERROR INESPERADO: \n" + e.getMessage());
            throw e;
        } finally {
            if (tmpPortedCSV != null) {
                FileUtils.deleteQuietly(tmpPortedCSV);
            }
        }
    }

    private int contarPortData(File xmlFile) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(xmlFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                // Solo cuenta la apertura exacta, no el cierre
                if (trimmed.startsWith("<PortData") && !trimmed.startsWith("</PortData")) {
                    count++;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("No se pudo contar PortData en archivo {}", xmlFile.getName(), e);
        }
        return count;
    }

    /**
     * Genera un archivo .log con los registros inválidos de estructura.
     * Solo se genera cuando hay errores.
     * Si no hay errores, no se crea ningún archivo.
     */

    private void generarLogInvalidos(List<String> registrosInvalidos, String Origen) {
        try {
            // Si no hay errores, no generamos nada
            if (registrosInvalidos == null || registrosInvalidos.isEmpty()) {
                LOGGER.info("No se generó archivo de inválidos para {}: no se detectaron errores de estructura", Origen);
                return;
            }

            String basePath = paramService.getParamByName("port_XMLLOG_path.portados");
            if (StringUtils.isEmpty(basePath)) {
                LOGGER.warn("Parametro port_XMLLOG_path.portados no definido, usando directorio actual");
                basePath = ".";
            }

            File dir = new File(basePath);
            if (!dir.exists()) {
                LOGGER.warn("Directorio {} no existe, verificar configuración de parámetros", basePath);
            }

            // Nombre con solo la fecha
            String fechaStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String fileName = "port_num_" + Origen + "_XML_estructuraInvalidos_" + fechaStr + ".log";

            File logFile = new File(dir, fileName);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile))) {
                for (String linea : registrosInvalidos) {
                    bw.write(linea);
                    bw.newLine();
                }
            }

            LOGGER.info("Archivo de inválidos generado: {}", logFile.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.error("Error al generar archivo de inválidos", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EstatusSincronizacion parsePortabilidadManual(File tmpPorted, EstatusSincronizacion status) throws Exception {

        File tmpPortedCSV = null;

        try {
            ResultadoParser res = new ResultadoParser();
            res.setTimestamp(new Date()); // evita NPE si parser no asigna fecha

            tmpPortedCSV = FicheroTemporal.getTmpFileName();
            LOGGER.debug("Parseamos a CSV fichero: " + tmpPortedCSV);
            // parseamos portabilidades
            //==================================================
            // Clase refactorizada
            // NumerosPortadosXmlParser parserPort = new NumerosPortadosXmlParser();
            // parserPort.parse(tmpPorted.getCanonicalPath(), tmpPortedCSV, res);
            //==================================================
            // Crear lista para acumular registros inválidos (estructura o CSV)
            List<String> registrosInvalidos = new ArrayList<>();

            NumerosPortadosXmlParser parser = new NumerosPortadosXmlParser(new FileWriter(tmpPortedCSV));
            parser.setRegistrosInvalidosGlobal(registrosInvalidos);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            //usamos streaming (isCancelados = false)
            InputSource sanitizedSource = sanitizeAndValidateXml(tmpPorted, registrosInvalidos, false);
            if (sanitizedSource != null) {
                saxParser.parse(sanitizedSource, parser);
                if (parser != null) {
                    try {
                        Field writerField = NumerosPortadosXmlParser.class.getDeclaredField("writer");
                        writerField.setAccessible(true);
                        BufferedWriter bw = (BufferedWriter) writerField.get(parser);
                        if (bw != null) {
                            bw.flush();
                            bw.close();
                        }
                    } catch (Exception e) {
                        LOGGER.warn("No se pudo cerrar el writer del parser", e);
                    }
                }
            } else {
                LOGGER.error("Archivo descartado por estructura inválida. Ver registrosInvalidos.");
                generarLogInvalidos(registrosInvalidos, "Portados"); // guardamos detalle

                // Ajustamos el status para reflejar que no hubo procesamiento
                status.setPortProcesadas(BigDecimal.ZERO);
                status.setPortProcesar(BigDecimal.ZERO);
                status.setProcesadasTs(new Timestamp(System.currentTimeMillis()));
                status.setActionDateLote(new Date());

                // Podrías tener un campo en status para errores, si no, al menos dejamos el log
                LOGGER.warn("PortabilidadManual terminada sin procesar por errores de estructura. Total errores={}", registrosInvalidos.size());

                return status; //salimos aquí mismo
            }

            int invalidosXml = 0;
            if (registrosInvalidos != null) {
                for (String reg : registrosInvalidos) {
                    // Contamos solo los bloques PortData inválidos
                    if (reg.startsWith("<PortData")) {
                        invalidosXml++;
                    }
                }
            }
            LOGGER.info("Parse {} completado -> InvalidosXml={}", "Cancelados", invalidosXml);

            // Guardar log de inválidos en archivo físico (si los hay)
            if (!registrosInvalidos.isEmpty()) {
                generarLogInvalidos(registrosInvalidos,"Portados");
            }

            status.setPortProcesadas(res.getNumerosOk());
            status.setPortProcesar(res.getNumerosTotal());
            status.setProcesadasTs(new Timestamp(res.getTimestamp().getTime()));
            LOGGER.debug("***-************- status:"+status.toString());
            LOGGER.debug("Validamos fichero: " + tmpPortedCSV+" longitud"+tmpPortedCSV.length());
            // importamos portados
            //FJAH 24052025
            //ValidadorArchivoPortadosCSV2 importPortados = new ValidadorArchivoPortadosCSV2();

            LOGGER.info("<--Inicia el procesamiento del csv de portados {}",FechasUtils.getActualDate());

//            Cargar BD


            //Collection<NumeroRequestDTO> list = importPortados.validar(tmpPortedCSV.getAbsolutePath());
            Collection<NumeroRequestDTO> list = validadorArchivoPortadosCSV2.validar(tmpPortedCSV.getAbsolutePath());
            for( NumeroRequestDTO dto : list ) {
                sendPOST(dto, "https://sns.ift.org.mx/pst/insertPortados");
            }

            LOGGER.info("<--finaliza el procesamiento del csv de portados {}", FechasUtils.getActualDate());

            return status;

        } catch (Exception e) {
            LOGGER.info("ERROR INESPERADO: \n" + e.getMessage());
            throw e;
        } finally {

        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ResultadoValidacionCSV parseDeleted(File tmpDeleted, EstatusSincronizacion status) throws Exception {

        File tmpDeletedCSV = null;

        try {
            ResultadoParser res = new ResultadoParser();
            res.setTimestamp(new Date()); // evita NPE si parser no asigna fecha
            tmpDeletedCSV = FicheroTemporal.getTmpFileName();
            LOGGER.debug("Parseamos a CSV fichero: {}", tmpDeletedCSV);

            int totalOrigen = contarPortData(tmpDeleted);
            LOGGER.info("Total de registros (origen XML): {}", totalOrigen);

            // Crear lista para acumular registros inválidos (estructura o CSV)
            List<String> registrosInvalidos = new ArrayList<>();

            NumerosDeletedXmlParser parser = new NumerosDeletedXmlParser(new FileWriter(tmpDeletedCSV));
            parser.setRegistrosInvalidosGlobal(registrosInvalidos);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            InputSource sanitizedSource = sanitizeAndValidateXml(tmpDeleted, registrosInvalidos, false);

            if (sanitizedSource == null) {
                LOGGER.error("Archivo {} descartado por errores críticos en cabecera/pie", tmpDeleted.getName());

                // Generar log de inválidos para evidencias QA/Prod
                if (!registrosInvalidos.isEmpty()) {
                    generarLogInvalidos(registrosInvalidos, "Cancelados");
                }

                // Retornamos resultado truncado sin tocar BD ni generar XML fallidos
                ResultadoValidacionCSV resultadoFallback = new ResultadoValidacionCSV();
                resultadoFallback.setIdEstatus(status.getId());
                resultadoFallback.setTs(status.getTs());
                resultadoFallback.setEstatus(status.getEstatus());
                resultadoFallback.setReintentos(status.getReintentos());
                resultadoFallback.setPortCanceladas(BigDecimal.ZERO);
                resultadoFallback.setPortCancelar(BigDecimal.ZERO);
                resultadoFallback.setCanceladasTs(new Timestamp(System.currentTimeMillis()));
                resultadoFallback.setActionDateLoteDate(new Date());
                resultadoFallback.setTotalErrorEstructuraCanc(registrosInvalidos.size());
                resultadoFallback.setTotalOrigenCanc(0); //origen inválido
                return resultadoFallback;
            }

            saxParser.parse(sanitizedSource, parser);

            if (parser != null) {
                try {
                    Field writerField = NumerosDeletedXmlParser.class.getDeclaredField("writer");
                    writerField.setAccessible(true);
                    BufferedWriter bw = (BufferedWriter) writerField.get(parser);
                    if (bw != null) {
                        bw.flush();
                        bw.close();
                    }
                } catch (Exception e) {
                    LOGGER.warn("No se pudo cerrar el writer del parser", e);
                }
            }

            // Contar inválidos a nivel de bloque
            int invalidosXml = 0;
            if (registrosInvalidos != null) {
                for (String reg : registrosInvalidos) {
                    if (reg.startsWith("<PortData")) {
                        invalidosXml++;
                    }
                }
            }
            LOGGER.info("Parse Cancelados completado -> InvalidosXml={}", invalidosXml);

            // Guardar log de inválidos en archivo físico (si los hay)
            if (!registrosInvalidos.isEmpty()) {
                generarLogInvalidos(registrosInvalidos, "Cancelados");
            }

            // actualizar status con datos iniciales
            status.setPortCanceladas(res.getNumerosOk());
            status.setPortCancelar(res.getNumerosTotal());
            status.setCanceladasTs(new Timestamp(res.getTimestamp().getTime()));

            LOGGER.info("<-- Inicia procesamiento CSV de cancelados {}", FechasUtils.getActualDate());

            // importar y validar cancelados
            ResultadoValidacionCSV resultadoValidacionCSV = validadorArchivoDeletedCSV.validar(tmpDeletedCSV.getAbsolutePath());

            // sincronizar actionDateLote
            if (resultadoValidacionCSV.getActionDateLote() != null) {
                try {
                    Date fechaActionDateLote =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(resultadoValidacionCSV.getActionDateLote());
                    status.setActionDateLote(fechaActionDateLote);
                } catch (ParseException e) {
                    LOGGER.error("Error al parsear actionDateLote: '{}'. Exception: {}", resultadoValidacionCSV.getActionDateLote(), e.getMessage());
                    status.setActionDateLote(new Date());
                }
            } else {
                status.setActionDateLote(new Date());
            }

            LOGGER.info("<-- Finaliza el procesamiento del csv de cancelados {}", FechasUtils.getActualDate());

            // pasar info del status al resultado
            resultadoValidacionCSV.setIdEstatus(status.getId());
            resultadoValidacionCSV.setTs(status.getTs());
            resultadoValidacionCSV.setEstatus(status.getEstatus());
            resultadoValidacionCSV.setReintentos(status.getReintentos());
            resultadoValidacionCSV.setPortCanceladas(status.getPortCanceladas());
            resultadoValidacionCSV.setPortCancelar(status.getPortCancelar());
            resultadoValidacionCSV.setCanceladasTs(status.getCanceladasTs());
            resultadoValidacionCSV.setActionDateLoteDate(status.getActionDateLote());

            // Ajustar origen con lo que declaró el XML
            resultadoValidacionCSV.setTotalOrigenCanc(totalOrigen);

            // Errores de estructura = inválidos CSV + inválidos XML
            int totalErrorEstructuraCanc = (resultadoValidacionCSV.getTotalErrorEstructuraCanc() > 0
                    ? resultadoValidacionCSV.getTotalErrorEstructuraCanc()
                    : 0) + invalidosXml;
            resultadoValidacionCSV.setTotalErrorEstructuraCanc(totalErrorEstructuraCanc);

            return resultadoValidacionCSV;

        } finally {
            if (tmpDeletedCSV != null) {
                FileUtils.deleteQuietly(tmpDeletedCSV);
            }
        }
    }

    /**
     * Limpia un XML potencialmente mal formado (Portados o Cancelados) para que pueda ser parseado por SAX.
     * Valida cabecera, pie, y corrige PortData cuando es posible.
     * Lo que no se pueda corregir se envía a registrosInvalidos y se descarta el bloque.
     *
     * Si la cabecera o pie son inválidos, retorna null y con ello se debe truncar el proceso
     * (sólo se generan logs de estructura inválida).
     */
    private InputSource sanitizeAndValidateXml(final File original,
                                               final List<String> registrosInvalidos,
                                               final boolean isCancelados) throws IOException {

        final StringBuilder sanitized = new StringBuilder();
        boolean headerOk = false;
        boolean footerOk = false;
        boolean messageNameOk = false;
        boolean timestampOk = false;
        boolean numMessagesOk = false;
        boolean portDataListOpen = false;
        boolean portDataListClose = false;

        int expectedNumMessages = -1;
        int countedPortData = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(original))) {
            String line;
            StringBuilder portDataBuffer = null;
            int portDataIndex = 0;

            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();

                // --- Cabecera raíz ---
                if (trimmed.startsWith("<?xml")) {
                    sanitized.append(line).append("\n");
                    continue;
                }
                if (trimmed.startsWith("<NPCData")) {
                    sanitized.append(line).append("\n");
                    headerOk = true;
                    continue;
                }
                if (trimmed.startsWith("</NPCData")) {
                    sanitized.append(line).append("\n");
                    footerOk = true;
                    break; // fin del archivo válido
                }

                // --- Cabecera interna ---
                if (trimmed.startsWith("<MessageName")) {
                    if (trimmed.matches("<MessageName>.*</MessageName>")) {
                        messageNameOk = true;
                    } else {
                        registrosInvalidos.add("Error crítico: Etiqueta <MessageName> mal formada.");
                    }
                    sanitized.append(line).append("\n");
                    continue;
                }

                if (trimmed.startsWith("<Timestamp")) {
                    if (trimmed.matches("<Timestamp>\\d{14}</Timestamp>")) { // yyyyMMddHHmmss
                        timestampOk = true;
                    } else {
                        registrosInvalidos.add("Error crítico: <Timestamp> inválido o mal formado.");
                    }
                    sanitized.append(line).append("\n");
                    continue;
                }

                if (trimmed.startsWith("<NumberOfMessages")) {
                    try {
                        expectedNumMessages = Integer.parseInt(trimmed.replaceAll("\\D+", ""));
                        numMessagesOk = true;
                    } catch (Exception e) {
                        registrosInvalidos.add("Error crítico: No se pudo leer <NumberOfMessages>");
                    }
                    sanitized.append(line).append("\n");
                    continue;
                }

                if (trimmed.startsWith("<PortDataList")) {
                    sanitized.append(line).append("\n");
                    portDataListOpen = true;
                    continue;
                }
                if (trimmed.startsWith("</PortDataList")) {
                    sanitized.append(line).append("\n");
                    portDataListClose = true;
                    continue;
                }

                // --- PortData open ---
                if (trimmed.startsWith("<PortData")) {
                    portDataBuffer = new StringBuilder();
                    portDataBuffer.append(line).append("\n");
                    continue;
                }

                // --- PortData close ---
                if (trimmed.startsWith("</PortData")) {
                    if (portDataBuffer != null) {
                        portDataBuffer.append(line).append("\n");
                        portDataIndex++;
                        countedPortData++;

                        String bloque = portDataBuffer.toString();
                        if (validatePortDataBlock(bloque, portDataIndex, registrosInvalidos, isCancelados)) {
                            sanitized.append(bloque); // válido
                        }
                        portDataBuffer = null;
                    } else {
                        // cierre sin apertura
                        portDataIndex++;
                        registrosInvalidos.add("Error: PortData #" + portDataIndex +
                                " descartado por estructura inválida en la apertura/cierre del bloque.");
                    }
                    continue;
                }

                // --- Dentro de PortData ---
                if (portDataBuffer != null) {
                    portDataBuffer.append(line).append("\n");
                } else {
                    // Texto fuera de PortData pero no cabecera → basura real
                    if (!trimmed.isEmpty()) {
                        registrosInvalidos.add("Error: Texto basura fuera de PortData.");
                    }
                }
            }
        }

        // --- Validación global crítica ---
        if (!headerOk) {
            registrosInvalidos.add(0, "Error crítico: Falta apertura <NPCData>.");
            return null;
        }
        if (!footerOk) {
            registrosInvalidos.add(0, "Error crítico: Falta cierre </NPCData>.");
            return null;
        }
        if (!messageNameOk) {
            registrosInvalidos.add(0, "Error crítico: Falta o mal formada la etiqueta <MessageName>.");
            return null;
        }
        if (!timestampOk) {
            registrosInvalidos.add(0, "Error crítico: Falta o mal formado el <Timestamp>.");
            return null;
        }
        if (!numMessagesOk) {
            registrosInvalidos.add(0, "Error crítico: Falta o mal formado <NumberOfMessages>.");
            return null;
        }
        if (!portDataListOpen) {
            registrosInvalidos.add(0, "Error crítico: Falta apertura <PortDataList>.");
            return null;
        }
        if (!portDataListClose) {
            registrosInvalidos.add(0, "Error crítico: Falta cierre </PortDataList>.");
            return null;
        }

        // Validación de conteo de mensajes → mensaje se inserta al inicio
        if (expectedNumMessages >= 0 && expectedNumMessages != countedPortData) {
            String diffMsg = "Error: Registros encontrados " + countedPortData +
                    " difiere de lo señalado en el XML (" + expectedNumMessages + ")";
            registrosInvalidos.add(0, diffMsg);
        }

        // Generamos InputSource solo si pasó validación completa
        String sanitizedXml = sanitized.toString();
        return new InputSource(new StringReader(sanitizedXml));
    }




    /**
     * Valida un bloque <PortData> contra la estructura esperada.
     * Solo revisa apertura/cierre, orden de tags y que estén presentes.
     */
    /**
     * Valida un bloque <PortData>.
     * - Revisa que tenga los tags obligatorios.
     * - Ignora comentarios y orden.
     * - Si faltan varios tags, acumula todos en un único mensaje.
     */
    private boolean validatePortDataBlock(String portDataXml,
                                          int index,
                                          List<String> registrosInvalidos,
                                          boolean isCancelados) {

        // Tags obligatorios para PORTADOS
        List<String> expectedPortados = Arrays.asList(
                "PortID", "PortType", "Action",
                "NumberRanges", "NumberRange", "NumberFrom", "NumberTo", "isMPP",
                "RIDA", "RCR", "DIDA", "DCR", "ActionDate"
        );

        // Tags obligatorios para CANCELADOS
        List<String> expectedCancelados = Arrays.asList(
                "PortID", "FolioID", "PortType", "Action",
                "NumberRanges", "NumberRange", "NumberFrom", "NumberTo", "isMPP",
                "RIDA", "RCR", "DIDA", "DCR",
                "AssigneeIDA", "AssigneeCR", "ActionDate"
        );

        List<String> expected = isCancelados ? expectedCancelados : expectedPortados;

        // 1. Limpiar comentarios y espacios
        String cleaned = portDataXml.replaceAll("<!--.*?-->", "").trim();

        // 2. Revisar todos los tags obligatorios
        List<String> faltantes = new ArrayList<>();
        for (String tag : expected) {
            String regex = "<" + tag + ">([\\s\\S]*?)</" + tag + ">";
            if (!cleaned.matches("(?s).*" + regex + ".*")) {
                faltantes.add("<" + tag + ">");
            }
        }

        // 3. Si hubo faltantes, acumulamos en un único mensaje
        if (!faltantes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < faltantes.size(); i++) {
                sb.append(faltantes.get(i));
                if (i < faltantes.size() - 1) {
                    sb.append(", ");
                }
            }

            registrosInvalidos.add("Error: PortData #" + index +
                    " descartado por contenido inválido en el bloque. Faltan o están mal cerrados: " +
                    sb.toString());
            registrosInvalidos.add(portDataXml);
            return false;
        }


        // 4. Bloque válido
        return true;
    }


    /**
     * Extrae el valor simple de un tag de un bloque XML.
     */
    private String extractValue(String xml, String tag) {
        Pattern p = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">");
        Matcher m = p.matcher(xml);
        if (m.find()) {
            return m.group(1).trim();
        }
        return null;
    }


    /*
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EstatusSincronizacion parseDeleted(File tmpDeleted, EstatusSincronizacion status) throws Exception {

        File tmpDeletedCSV = null;

        try {
            ResultadoParser res = new ResultadoParser();

            tmpDeletedCSV = FicheroTemporal.getTmpFileName();
            LOGGER.debug("Parseamos a CSV fichero: " + tmpDeletedCSV);
            // parseamos cancelaciones
            NumerosDeletedXmlParser parserDeleted = new NumerosDeletedXmlParser();
            parserDeleted.parse(tmpDeleted.getCanonicalPath(), tmpDeletedCSV, res);
            status.setPortCanceladas(res.getNumerosOk());
            status.setPortCancelar(res.getNumerosTotal());
            status.setCanceladasTs(new Timestamp(res.getTimestamp().getTime()));
            LOGGER.debug("*****-*******-****** status:"+status.toString());
            LOGGER.debug("Validamos fichero: " + tmpDeletedCSV+" longitud"+tmpDeletedCSV.length());
            // importamos cancelaciones
            //FJAH 24052025
            //ValidadorArchivoDeletedCSV importDeleted=new ValidadorArchivoDeletedCSV();
            ResultadoValidacionCSV resultado = validadorArchivoDeletedCSV.validar(tmpDeletedCSV.getAbsolutePath());

            LOGGER.info("<--Inicia el procesamiento del csv de cancelados {}",FechasUtils.getActualDate());

            //importDeleted.validar(tmpDeletedCSV.getAbsolutePath());

            LOGGER.info("<--finaliza el procesamiento del csv de cancelados {}", FechasUtils.getActualDate());

            return status;

        } catch (Exception e) {
            throw e;
        } finally {
//            if (tmpDeleted != null) {
//                FileUtils.deleteQuietly(tmpDeleted);
//            }
//
//            if (tmpDeletedCSV != null) {
//                FileUtils.deleteQuietly(tmpDeletedCSV);
//            }
        }
    }
     */

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EstatusSincronizacion parseDeletedManual(File tmpDeleted, EstatusSincronizacion status) throws Exception {

        File tmpDeletedCSV = null;

        try {
            ResultadoParser res = new ResultadoParser();
            res.setTimestamp(new Date()); // evita NPE si parser no asigna fecha

            tmpDeletedCSV = FicheroTemporal.getTmpFileName();
            LOGGER.debug("Parseamos a CSV fichero: " + tmpDeletedCSV);
            // parseamos cancelaciones
            NumerosDeletedXmlParser parserDeleted = new NumerosDeletedXmlParser();
            parserDeleted.parse(tmpDeleted.getCanonicalPath(), tmpDeletedCSV, res);
            status.setPortCanceladas(res.getNumerosOk());
            status.setPortCancelar(res.getNumerosTotal());
            status.setCanceladasTs(new Timestamp(res.getTimestamp().getTime()));
            LOGGER.debug("*****-*******-****** status:"+status.toString());
            LOGGER.debug("Validamos fichero: " + tmpDeletedCSV+" longitud"+tmpDeletedCSV.length());
            // importamos cancelaciones
            //FJAH 24052025
            //ValidadorArchivoDeletedCSV2 importDeleted=new ValidadorArchivoDeletedCSV2();

            LOGGER.info("<--Inicia el procesamiento del csv de cancelados {}",FechasUtils.getActualDate());

            //Collection<NumeroCanceladoRequestDTO> list = importDeleted.validar(tmpDeletedCSV.getAbsolutePath());
            Collection<NumeroCanceladoRequestDTO> list = importDeleted.validar(tmpDeletedCSV.getAbsolutePath());
            for (NumeroCanceladoRequestDTO dto : list) {
                sendPOST(dto, "https://sns.ift.org.mx/pst/deletePort");
            }

            LOGGER.info("<--finaliza el procesamiento del csv de cancelados {}", FechasUtils.getActualDate());

            return status;

        } catch (Exception e) {
            throw e;
        }
    }

    private String sendPOST(Object dto, String url) throws IOException {
        String result = "";
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                    sslsf).build();

            HttpPost post = new HttpPost(url);

            StringEntity stringEntity = new StringEntity(new Gson().toJson(dto));
            stringEntity.setContentType("application/json");
            post.setEntity(stringEntity);
            CloseableHttpResponse response = httpclient.execute(post);
            result = EntityUtils.toString(response.getEntity());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void syncBDDPortabilidadAsync() {
        LOGGER.info("[syncBDDPortabilidadAsync] INICIO");

        File tmpPorted = null;
        File tmpDeleted = null;
        EstatusSincronizacion status = null;  // <<-- declarado desde el inicio

        try {
            if (!checkConfig()) {
                bitacoraService.add(BIT_MSG_ERR_CONFIG);
                LOGGER.warn("[syncBDDPortabilidadAsync] Configuración inválida, abortando.");
                return;
            }

            // ============================
            // Limpieza de directorio destino (logs/xml/html)
            // ============================
            limpiarDirectorio(paramService.getParamByName("port_XMLLOG_path.portados"));

            status = getStatus();  // <<-- inicializado aquí
            LOGGER.info("[syncBDDPortabilidadAsync] Estatus inicial: {}", status);

            if (FechasUtils.esHoy(status.getTs())) {
                LOGGER.info("[syncBDDPortabilidadAsync] El status es de hoy.");
                if (status.getEstatus() != null &&
                        status.getEstatus().equals(EstatusSincronizacion.ESTATUS_PORT_OK)) {
                    LOGGER.info("[syncBDDPortabilidadAsync] Ya fue ejecutado hoy con OK, no hace nada.");
                    return;
                } else {
                    int r = status.getReintentos().compareTo(MAX_REINTENTOS);
                    LOGGER.info("[syncBDDPortabilidadAsync] Reintentos actuales: {}", status.getReintentos());
                    if (r > 0) {
                        LOGGER.warn("[syncBDDPortabilidadAsync] Máximo de reintentos alcanzado, abortando.");
                        return;
                    } else {
                        LOGGER.info("[syncBDDPortabilidadAsync] Aumentando reintentos.");
                        status.setReintentos(status.getReintentos().add(BigDecimal.ONE));
                    }
                }
            }

            // ============================
            // Descarga de archivos remotos
            // ============================
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1); // día de ayer
            LOGGER.debug("Obtenemos ficheros");

            LOGGER.info("[syncBDDPortabilidadAsync] Fecha de proceso: {}", cal.getTime());
            LOGGER.info("[syncBDDPortabilidadAsync] Obteniendo nombres de archivos remotos...");

            String remoteFilePortedPath = getNumbersPortedFileName(cal.getTime());
            String remoteFileDeletedPath = getNumbersDeletedFileName(cal.getTime());

            LOGGER.info("[syncBDDPortabilidadAsync] nombre archivo PORTADO remoto obtenido: {}", remoteFilePortedPath);
            LOGGER.info("[syncBDDPortabilidadAsync] nombre archivo CANCELADO remoto obtenido: {}", remoteFileDeletedPath);

            tmpPorted = FicheroTemporal.getTmpFileName();
            tmpDeleted = FicheroTemporal.getTmpFileName();

            LOGGER.info("[syncBDDPortabilidadAsync] Archivos temporales: ported={}, deleted={}", tmpPorted, tmpDeleted);

            ObtenerArchivosDiarios ar = new ObtenerArchivosDiarios(
                    tmpPorted.getCanonicalPath(),
                    tmpDeleted.getCanonicalPath(),
                    conectionParams,
                    remoteFilePortedPath,
                    remoteFileDeletedPath
            );

            LOGGER.info("[syncBDDPortabilidadAsync] Iniciando descarga de archivos diarios...");
            ar.getArchidosDiarios();
            LOGGER.info("[syncBDDPortabilidadAsync] Descarga finalizada.");

            // ============================
            // Validar existencia/estado de archivos
            // ============================
            boolean localMode = (remoteFilePortedPath != null && remoteFilePortedPath.startsWith("file:/"));

            if (!tmpPorted.exists() || !tmpDeleted.exists() ||
                    !tmpPorted.isFile() || !tmpDeleted.isFile() ||
                    tmpPorted.length() == 0 || tmpDeleted.length() == 0) {

                String causa;
                if (!tmpPorted.exists() || !tmpDeleted.exists() ||
                        !tmpPorted.isFile() || !tmpDeleted.isFile()) {
                    causa = String.format(
                            "Sincronización ABD: Directorio/archivos de portados/cancelados no encontrados en la ruta [%s].",
                            remoteFilePortedPath
                    );
                } else {
                    causa = String.format(
                            "Sincronización ABD: Archivos de portados/cancelados encontrados en la ruta [%s], pero se detectaron vacíos.",
                            remoteFilePortedPath
                    );
                }

                LOGGER.error("[syncBDDPortabilidadAsync] {}", causa);
                bitacoraService.add(causa);

                if (status != null) {
                    status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
                    status.setTs(new Date());
                }

                enviarMailErrorABDDetalle(causa, localMode);
                return;
            }

/*
            if (tmpPorted.length() == 0 || tmpDeleted.length() == 0) {
                LOGGER.error("[syncBDDPortabilidadAsync] Algún archivo descargado está vacío. ported.length={}, deleted.length={}",
                        tmpPorted.length(), tmpDeleted.length());
                throw new FileNotFoundException(remoteFilePortedPath);
            }

 */

            // ============================
            // Procesamiento de portados
            // ============================
            LOGGER.info("[syncBDDPortabilidadAsync] Parseando ficheros a CSV...");
            ResultadoValidacionCSV res = me.parsePortabilidad(tmpPorted, status);

            // Actualizar status con resultado de parsePortabilidad
            status.setId(res.getIdEstatus());
            status.setTs(res.getTs());
            status.setEstatus(res.getEstatus());
            status.setReintentos(res.getReintentos());
            status.setPortProcesadas(res.getPortProcesadas());
            status.setPortProcesar(res.getPortProcesar());
            status.setProcesadasTs(res.getProcesadasTs());
            status.setActionDateLote(res.getActionDateLoteDate());

            LOGGER.info("[syncBDDPortabilidadAsync] Resumen detallado: Insertados={}, Actualizados={}, NoPersistidos={}, ErroresEstructura={}",
                    res.getTotalInsertados(),
                    res.getTotalActualizados(),
                    res.getTotalNoPersistidos(),
                    res.getTotalErrorEstructura());

            // ============================
            // Procesamiento de cancelados
            // status = me.parseDeleted(tmpDeleted, status);
            // ============================
            ResultadoValidacionCSV resultadocanc = me.parseDeleted(tmpDeleted, status);
            // ============================
            // Fusionar resultados de cancelados en res
            // ============================
            if (resultadocanc != null) {
                res.setTotalOrigenCanc(resultadocanc.getTotalOrigenCanc());
                res.setTotalProcesadosCanc(resultadocanc.getTotalProcesadosCanc());
                res.setTotalFallidosInsercionCanc(resultadocanc.getTotalFallidosInsercionCanc());
                res.setTotalFallidosEliminacionCanc(resultadocanc.getTotalFallidosEliminacionCanc());
                res.setTotalErrorEstructuraCanc(resultadocanc.getTotalErrorEstructuraCanc());
            }

            // ============================
            // Totales de BD
            // ============================
            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_OK);
            status.setTs(new Date());
            status.setReintentos(BigDecimal.ZERO);

            Date fechaActionDateArchivo = status.getActionDateLote();
            if (fechaActionDateArchivo == null) {
                LOGGER.warn("[syncBDDPortabilidadAsync] No se obtuvo actionDate del lote, se usa fecha actual.");
                fechaActionDateArchivo = new Date();
            }

            BigDecimal totalPort = numerosPortadosDAO.getTotalNumerosPortadosPorFecha(fechaActionDateArchivo);
            BigDecimal totalCancel = numerosCanceladosDAO.getTotalNumerosCanceladosPorFecha(fechaActionDateArchivo);

            status.setPortProcesadas(totalPort);
            status.setPortCanceladas(totalCancel);

            LOGGER.info("[syncBDDPortabilidadAsync] Totales en BD: portados={}, cancelados={}", totalPort, totalCancel);

            // ============================
            // Bitácora y correo
            // ============================
            String msg = formatMensaje(BIT_MSG_SYNC_OK, status);
            LOGGER.info("[syncBDDPortabilidadAsync] Mensaje a bitácora: {}", msg);
            bitacoraService.add(msg);

            // Determinar si estamos en modo LOCAL o FTP
            localMode = (remoteFilePortedPath != null && remoteFilePortedPath.startsWith("file:/"));

            // Llamar correo con flag - En el flujo normal NO hay causa
            enviarMailSyncOkDetalle(status, res, localMode, null);
            LOGGER.info("[syncBDDPortabilidadAsync] Sincronización ABD ejecutada en {}: {}", WeblogicNode.getName(), msg);

        } catch (FileNotFoundException e) {
            LOGGER.error("No se puede acceder al archivo: {}", e.getMessage());
            bitacoraService.add(BIT_MSG_SYNC_ERROR);
            if (status != null) {
                status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
                status.setTs(new Date());
            }
            enviarMailErrorABD("No se puede acceder al archivo: " + e.getMessage());

        } catch (FileSystemException e) {
            LOGGER.error("[syncBDDPortabilidadAsync] No se puede acceder al archivo: {}", e.getMessage(), e);
            bitacoraService.add(BIT_MSG_SYNC_ERROR);
            if (status != null) {
                status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
                status.setTs(new Date());
            }
            enviarMailErrorABD("No se puede acceder al archivo: " + e.getMessage());

        } catch (Exception e) {
            LOGGER.error("[syncBDDPortabilidadAsync] Error inesperado", e);
            bitacoraService.add(BIT_MSG_SYNC_ERROR);
            if (status != null) {
                status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
                status.setTs(new Date());
            }
            enviarMailErrorABD("Error inesperado: " + e.getMessage());

        } finally {
            LOGGER.info("[syncBDDPortabilidadAsync] Guardando status y eliminando temporales.");

            if (status != null && !FechasUtils.esHoy(status.getTs())) {
                LOGGER.warn("[syncBDDPortabilidadAsync] Forzamos update de status.TS a hoy para evitar reprocesos múltiples");
                status.setTs(new Date());
            }

            if (status != null) {
                me.saveStatus(status);
            }

            if (tmpPorted != null) {
                FileUtils.deleteQuietly(tmpPorted);
                LOGGER.info("[syncBDDPortabilidadAsync] Archivo temporal ported eliminado: {}", tmpPorted);
            }
            if (tmpDeleted != null) {
                FileUtils.deleteQuietly(tmpDeleted);
                LOGGER.info("[syncBDDPortabilidadAsync] Archivo temporal deleted eliminado: {}", tmpDeleted);
            }

            LOGGER.info("[syncBDDPortabilidadAsync] FIN");
        }
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void syncManualPortabilidadAsync() {
        LOGGER.debug("");
        EstatusSincronizacion status = getStatus();

        //Buscar archivos en rutas del servidor
        File dir = new File(paramService.getParamByName(ParametrosABD.PORTACION_MANUAL_PORTADOS));
        File[] directoryListing = dir.listFiles();

        boolean portaciones = false;
        boolean cancelados = false;

        if (directoryListing != null) {
            for (File tmpPorted : directoryListing) {
                try {
                    if (tmpPorted.length() == 0) {
                        LOGGER.debug("El archivo: "+tmpPorted.getName()+" esta vacio");//throw new FileNotFoundException("/tmp/SNS/portados");
                        moverArchivo(tmpPorted);
                    }
                    LOGGER.debug("Parseamos ficheros a CSV");
                    EstatusSincronizacion s = this.parsePortabilidadManual(tmpPorted, status);
                    status.setPortProcesadas(s.getPortProcesadas());
                    status.setPortProcesar((s.getPortProcesadas()));
                    status.setProcesadasTs(s.getProcesadasTs());

                    status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_OK);
                    status.setTs(new Date());
                    //status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada
                    status.setReintentos(BigDecimal.ZERO);

                    status.setPortProcesadas(numerosPortadosDAO.getTotalNumerosPortadosHoy());

                    String msg = formatMensaje(BIT_MSG_SYNC_OK, status);
                    LOGGER.info("+++++++++++++ " + msg);
                    bitacoraService.add(msg);
//                    enviarMailSyncOk(status);
                    portaciones = true;
                    LOGGER.info("Sincronización ABD ejecutada en {}: {}", WeblogicNode.getName(), msg);

                }catch (Exception e) {
                    LOGGER.error("Error inespedado", e);
                    bitacoraService.add(BIT_MSG_SYNC_ERROR);
                    status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
                    status.setTs(new Date());
                    //status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada
                    moverArchivo(tmpPorted);
                    enviarMailErrorABD("Error inesperado en el archivo de portaciones: "+tmpPorted.getName()+"\n" + e.getMessage());
                } finally {

                    me.saveStatus(status);

                    if (tmpPorted.exists()) {
                        FileUtils.deleteQuietly(tmpPorted);
                    }

                }

            }
        } else {
            LOGGER.info("No hay archivos portados para procesar hoy");
        }

        dir = new File(paramService.getParamByName(ParametrosABD.PORTACION_MANUAL_CANCELADOS));
        directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File tmpDeleted : directoryListing) {
                try {
                    if (tmpDeleted.length() == 0) {
                        LOGGER.debug("El archivo: "+tmpDeleted.getName()+" esta vacio");//throw new FileNotFoundException("/tmp/SNS/portados");
                        moverArchivo(tmpDeleted);
                    }

                    //EstatusSincronizacion s = me.parseDeleted(tmpDeleted, status);
                    //me.parseDeleted(tmpDeleted, status);
                    ResultadoValidacionCSV resultadocanc = me.parsePortabilidad(tmpDeleted, status);
                    /*
                    status.setPortCanceladas(s.getPortCanceladas());
                    status.setPortCancelar((s.getPortCancelar()));
                    status.setCanceladasTs(s.getCanceladasTs());
                     */

                    status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_OK);
                    status.setTs(new Date());
                    //status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada
                    status.setReintentos(BigDecimal.ZERO);

                    // Obtenemos los totales que ralmente se ha registrado en BD
                    status.setPortCanceladas((numerosCanceladosDAO.getTotalNumerosCanceladosHoy()));

                    String msg = formatMensaje(BIT_MSG_SYNC_OK, status);
                    LOGGER.info("+++++++++++++ " + msg);
                    bitacoraService.add(msg);
                    //enviarMailSyncOk(status);
                    cancelados = true;
                    LOGGER.info("Sincronización ABD ejecutada en {}: {}", WeblogicNode.getName(), msg);

                }catch (Exception e) {
                    LOGGER.error("Error inespedado", e);
                    bitacoraService.add(BIT_MSG_SYNC_ERROR);
                    status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
                    status.setTs(new Date());
                    //status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada

                    moverArchivo(tmpDeleted);
                    enviarMailErrorABD("Error inespedado en el archivo de cancelados: "+tmpDeleted.getName()+"\n" + e.getMessage());
                } finally {

                    me.saveStatus(status);

                    if (tmpDeleted.exists()) {
                        FileUtils.deleteQuietly(tmpDeleted);
                    }

                }
            }
        }else{
            LOGGER.info("No hay archivos cancelados para procesar hoy");
        }

        if(portaciones && cancelados) {
            enviarMailSyncOk(status);
        }

    }

    /**
     * Elimina todos los archivos de un directorio dado.
     * No elimina subdirectorios.
     */
    private void limpiarDirectorio(String basePath) {
        try {
            if (StringUtils.isNotEmpty(basePath)) {
                File dir = new File(basePath);
                if (dir.exists() && dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    if (files != null) {
                        for (File f : files) {
                            if (f.isFile()) {
                                if (f.delete()) {
                                    LOGGER.debug("[syncBDDPortabilidadAsync] Archivo previo eliminado: {}", f.getName());
                                } else {
                                    LOGGER.warn("[syncBDDPortabilidadAsync] No se pudo eliminar archivo previo: {}", f.getName());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("[syncBDDPortabilidadAsync] Error limpiando directorio de salida {}", basePath, ex);
        }
    }



    private void moverArchivo(File file){

        try {
            Files.move( Paths.get(file.getAbsolutePath()) , Paths.get(paramService.getParamByName(ParametrosABD.PORTACION_MANUAL_ERROR)+file.getName()));
        } catch (IOException e) {
            LOGGER.error("Ocurrio un error al mover el archivo que fallo y por lo cual se procede a eliminarlo");

            if(file.exists())
                FileUtils.deleteQuietly(file);
        }

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void syncBDDPortabilidad() throws Exception {
        LOGGER.info("[syncBDDPortabilidad] INICIO");
        syncBDDPortabilidadAsync();
        LOGGER.info("[syncBDDPortabilidad] FIN (llamó a async)");
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void syncManualPortabilidad() throws Exception {
        syncManualPortabilidadAsync();
    }

    @Override
    public void syncBDDPortabilidad(String file, TipoFicheroPort tipo, Usuario usuario)
            throws SincronizacionABDException {
        LOGGER.info("[syncBDDPortabilidad(file,tipo,usuario)] INICIO: file={}, tipo={}, usuario={}", file, tipo, usuario);
        if (checkConfig() == false) {
            bitacoraService.add(BIT_MSG_ERR_CONFIG);
            LOGGER.debug("Error configuracion Sincronización con ADB");
            return;
        }

        EstatusSincronizacion status = getStatus();
        status.clear();

        File tmp = null;
        ResultadoValidacionCSV resultado = null;

        try {
            LOGGER.debug("[syncBDDPortabilidad] Tipo de fichero recibido: {}", tipo.getCdg());

            if (tipo.getCdg().equals(TipoFicheroPort.DIARIO_PORTED)) {
                LOGGER.info("[syncBDDPortabilidad] Procesando fichero de portabilidad: {}", file);
                tmp = new File(file);
                LOGGER.debug("[syncBDDPortabilidad] Llamando a parsePortabilidad()");
                //status = me.parsePortabilidad(tmp, status);
                resultado = me.parsePortabilidad(tmp, status);
                // Sincronizamos valores de resultado -> status
                status.setId(resultado.getIdEstatus());
                status.setTs(resultado.getTs());
                status.setEstatus(resultado.getEstatus());
                status.setReintentos(resultado.getReintentos());
                status.setPortProcesadas(resultado.getPortProcesadas());
                status.setPortProcesar(resultado.getPortProcesar());
                status.setProcesadasTs(resultado.getProcesadasTs());
                status.setActionDateLote(resultado.getActionDateLoteDate());

                LOGGER.info("[syncBDDPortabilidad] parsePortabilidad() terminó correctamente.");
            } else if (tipo.getCdg().equals(TipoFicheroPort.DIARIO_DELETED)) {
                LOGGER.info("[syncBDDPortabilidad] Procesando fichero de cancelaciones: {}", file);
                tmp = new File(file);
                LOGGER.debug("[syncBDDPortabilidad] Llamando a parseDeleted()");
                //status = me.parseDeleted(tmp, status);
                resultado = me.parseDeleted(tmp, status);

                // Sincronizamos valores de resultado -> status (igual que en portados)
                status.setId(resultado.getIdEstatus());
                status.setTs(resultado.getTs());
                status.setEstatus(resultado.getEstatus());
                status.setReintentos(resultado.getReintentos());
                status.setPortCanceladas(resultado.getPortCanceladas());
                status.setPortCancelar(resultado.getPortCancelar());
                status.setCanceladasTs(resultado.getCanceladasTs());
                status.setActionDateLote(resultado.getActionDateLoteDate());
                LOGGER.info("[syncBDDPortabilidad] parseDeleted() terminó correctamente.");
            }else {
                LOGGER.warn("[syncBDDPortabilidad] Tipo de fichero NO reconocido: {}", tipo.getCdg());
            }

            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_OK);
            status.setTs(new Date());
            //status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada

            // Loguea los totales registrados en BD
            BigDecimal portProcesadas = numerosPortadosDAO.getTotalNumerosPortadosHoy();
            BigDecimal portCanceladas = numerosCanceladosDAO.getTotalNumerosCanceladosHoy();

            LOGGER.info("[syncBDDPortabilidad] Totales procesadas hoy: portProcesadas={}, portCanceladas={}", portProcesadas, portCanceladas);

            // Obtenemos los totales que ralmente se ha registrado en BD
            status.setPortProcesadas(numerosPortadosDAO.getTotalNumerosPortadosHoy());
            status.setPortCanceladas(numerosCanceladosDAO.getTotalNumerosCanceladosHoy());

            String msg = formatMensaje(BIT_MSG_SYNC_MANUAL_OK, status);
            LOGGER.info("[syncBDDPortabilidad] Mensaje bitácora OK: {}", msg);
            bitacoraService.add(usuario, msg);
        } catch (FileNotFoundException e) {
            LOGGER.error("[syncBDDPortabilidad] No se puede acceder al archivo: {}", file, e);

            bitacoraService.add(usuario, BIT_MSG_SYNC_MANUAL_ERROR);

            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
            status.setTs(new Date());
            //status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada

            throw new SincronizacionABDException(BIT_MSG_SYNC_MANUAL_ERROR);

        } catch (FileSystemException e) {
            LOGGER.error("[syncBDDPortabilidad] No se puede acceder a los archivos", e);

            bitacoraService.add(usuario, BIT_MSG_SYNC_ERROR);

            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
            status.setTs(new Date());
            //status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada

            throw new SincronizacionABDException(BIT_MSG_SYNC_ERROR);
        } catch (Exception e) {

            LOGGER.error("[syncBDDPortabilidad] Exception general", e);
            bitacoraService.add(usuario, BIT_MSG_SYNC_MANUAL_ERROR);

            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
            throw new SincronizacionABDException(BIT_MSG_SYNC_MANUAL_ERROR);
        } finally {
            LOGGER.debug("[syncBDDPortabilidad] Llamando a FicheroTemporal.delete()");
            FicheroTemporal.delete(tmp);
            status.setTs(new Date());
            //status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada
            saveStatus(status);
            LOGGER.info("[syncBDDPortabilidad] FIN bloque finally");
        }

        LOGGER.info("[syncBDDPortabilidad] FIN OK");
    }

    @Override
    public NumeroPortado findNumeroPortado(String numero) {
        return numerosPortadosDAO.get(numero);
    }

    /** Error de comunicación con el ABD. */
    @Override
    public void enviarMailErrorABD(String pErrorMsg) {
        try {

            final String subject = "SNS-Error conexión SFTP-ABD";
            String to = paramService.getParamByName(ParametrosABD.ABD_MAIL_LIST);
            StringBuilder subjectWithHost=new StringBuilder();
            subjectWithHost.append(subject).append(":Host:").append(getHostName());
            StringBuilder sbBody = new StringBuilder();
            sbBody.append(paramService.getParamByName(ParametrosABD.ABD_ERROR_SERVIDOR));
            sbBody.append("\nDescripción del error: ").append(pErrorMsg);

            mailService.sendEmail(to, subjectWithHost.toString(), sbBody.toString());
        } catch (Exception e) {
            LOGGER.error("error enviando mail", e);
        }
    }

    /**
     * Error en el fichero de portabilidades.
     * @param fichero fecha del fichero
     */
    @Override
    public void enviarMailFicheroPorted(String fichero) {
        try {
            final String subject = "SNS-Error Procesamiento archivo NumbersPorted";
            String to = paramService.getParamByName(ParametrosABD.ABD_MAIL_LIST);
            StringBuilder subjectWithHost=new StringBuilder();
            subjectWithHost.append(subject).append(":Host:").append(getHostName());
            StringBuilder sbBody = new StringBuilder();
            sbBody.append(paramService.getParamByName(ParametrosABD.ABD_ERROR_FICHERO_PORTADAS));
            sbBody.append(" Fichero indentificado como: NumbersPorted-").append(fichero).append(".xml");

            mailService.sendEmail(to, subjectWithHost.toString(), sbBody.toString());
        } catch (Exception e) {
            LOGGER.error("error enviando mail", e);
        }
    }

    /**
     * Error en el fichero de cancelaciones.
     * @param fichero fecha del fichero
     */
    @Override
    public void enviarMailFicheroDeleted(String fichero) {
        try {
            final String subject = "SNS-Error Procesamiento archivo NumbersPorted";
            String to = paramService.getParamByName(ParametrosABD.ABD_MAIL_LIST);
            StringBuilder subjectWithHost=new StringBuilder();
            subjectWithHost.append(subject).append(":Host:").append(getHostName());
            StringBuilder sbBody = new StringBuilder();
            sbBody.append(paramService.getParamByName(ParametrosABD.ABD_ERROR_FICHERO_CANCELADAS));
            sbBody.append(" Fichero indentificado como: NumbersDeleted-").append(fichero).append(".xml");

            mailService.sendEmail(to, subjectWithHost.toString(), sbBody.toString());
        } catch (Exception e) {
            LOGGER.error("error enviando mail", e);
        }
    }

    /**
     * Mail de resultado de la sincronizacion.
     * @param status status de la sincronizacion
     */
    /**
     * Mail de resultado de la sincronizacion con totales de validación.
     */
    @Override
    public void enviarMailSyncOk(EstatusSincronizacion status) {
        try {
            final String subject = "SNS-Notificación Sincronizacion ABD";
            StringBuilder subjectWithHost=new StringBuilder();
            String to = paramService.getParamByName(ParametrosABD.ABD_MAIL_LIST);
            final String body = "Número de portaciones a procesar : " + status.getPortProcesar()
                    + "\nNúmero de portaciones procesadas : " + status.getPortProcesadas()
                    + "\nNúmero de portaciones a cancelar : " + status.getPortCancelar()
                    + "\nNúmero de portaciones canceladas : " + status.getPortCanceladas();
            subjectWithHost.append(subject).append(":Host:").append(getHostName());
            mailService.sendEmail(to, subjectWithHost.toString(), body);
        } catch (Exception e) {
            LOGGER.error("error enviando mail", e);
        }
    }

    /**
     * FJAH Integrar detalle de totales procesados
     * @param status estado de la sincronizacion
     * @param res
     */
    @Override
    public void enviarMailSyncOkDetalle(EstatusSincronizacion status, ResultadoValidacionCSV res, boolean localMode, String causa) {
        final String subject = "SNS-Notificación Sincronizacion ABD (Detalle)";
        String to = paramService.getParamByName(ParametrosABD.ABD_MAIL_LIST);

        StringBuilder subjectWithHost = new StringBuilder();
        subjectWithHost.append(subject).append(":Host:").append(getHostName());

        try {
            String basePath = paramService.getParamByName("port_XMLLOG_path.portados");
            if (StringUtils.isEmpty(basePath)) {
                basePath = ".";
            }

            String fechaLogs = new SimpleDateFormat("yyyyMMdd").format(new Date());

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            String fechaXmls = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());

            // ====== Archivos PORTADOS ======
            File logEstructuraPort = new File(basePath, "port_num_Portados_XML_estructuraInvalidos_" + fechaLogs + ".log");
            File logCsvPort        = new File(basePath, "port_num_portados_CSVinvalidos_" + fechaLogs + ".log");
            File xmlFallidosPort   = new File(basePath, "NumbersPorted-" + fechaXmls + ".xml");

            int errEstructuraPort  = logEstructuraPort.exists() ? contarErroresEstructura(logEstructuraPort) : 0;
            int errCsvPort         = logCsvPort.exists() ? contarErroresCSV(logCsvPort) : 0;
            int fallidosXmlPort    = xmlFallidosPort.exists() ? contarFallidosXML(xmlFallidosPort) : 0;
            int totalFallidosPort  = errEstructuraPort + errCsvPort + fallidosXmlPort;
            int totalProcesadosPort = (res != null ? res.getTotalInsertados() + res.getTotalActualizados() : 0);
            int origenPort = totalProcesadosPort + totalFallidosPort; // <<=== calculado

            // ====== Archivos CANCELADOS ======
            File logEstructuraCanc = new File(basePath, "port_num_Cancelados_XML_estructuraInvalidos_" + fechaLogs + ".log");
            File logCsvCanc        = new File(basePath, "port_num_cancelados_CSVinvalidos_" + fechaLogs + ".log");
            File xmlFallidosCanc   = new File(basePath, "NumbersDeleted-" + fechaXmls + ".xml");

            int errEstructuraCanc  = logEstructuraCanc.exists() ? contarErroresEstructura(logEstructuraCanc) : 0;
            int errCsvCanc         = logCsvCanc.exists() ? contarErroresCSV(logCsvCanc) : 0;
            int fallidosXmlCanc    = xmlFallidosCanc.exists() ? contarFallidosXML(xmlFallidosCanc) : 0;
            int totalFallidosCanc  = errEstructuraCanc + errCsvCanc + fallidosXmlCanc;
            int totalProcesadosCanc = res != null ? res.getTotalProcesadosCanc() : 0;
            int origenCanc = totalProcesadosCanc + totalFallidosCanc; // <<=== calculado

            // ====== Construir body HTML ======
            StringBuilder body = new StringBuilder();
            body.append("<html><body style='font-family: monospace;'>");
            body.append("Resumen sincronización portados/cancelados<br><br>");

            if (causa != null && !causa.isEmpty()) {
                body.append("Causa: ").append(causa).append("<br><br>");
            }

            // ====== Totales PORTADOS ======
            body.append("<b>Totales procesados (Portados)</b><br>");
            body.append("&nbsp;&nbsp;- Origen archivo XML: ").append(origenPort).append("<br>");
            body.append("&nbsp;&nbsp;- Insertados BD: ").append(res != null ? res.getTotalInsertados() : 0).append("<br>");
            body.append("&nbsp;&nbsp;- Actualizados BD: ").append(res != null ? res.getTotalActualizados() : 0).append("<br>");
            body.append("&nbsp;&nbsp;- Procesados BD (Insertados + Actualizados): ").append(totalProcesadosPort).append("<br>");
            body.append("&nbsp;&nbsp;- Errores de estructura: ").append(errEstructuraPort).append("<br>");
            body.append("&nbsp;&nbsp;- Errores de contenido (CSV): ").append(errCsvPort).append("<br>");
            body.append("&nbsp;&nbsp;- Fallidos (no persistidos XML): ").append(fallidosXmlPort).append("<br>");
            body.append("&nbsp;&nbsp;- Fallidos global: ").append(totalFallidosPort).append("<br><br>");

            // ====== Totales CANCELADOS ======
            body.append("<b>Totales procesados (Cancelados)</b><br>");
            body.append("&nbsp;&nbsp;- Origen archivo XML: ").append(origenCanc).append("<br>");
            body.append("&nbsp;&nbsp;- Procesados BD (Cancelados efectivos): ").append(totalProcesadosCanc).append("<br>");
            body.append("&nbsp;&nbsp;- Errores de estructura: ").append(errEstructuraCanc).append("<br>");
            body.append("&nbsp;&nbsp;- Errores de contenido (CSV): ").append(errCsvCanc).append("<br>");
            body.append("&nbsp;&nbsp;- Fallidos (no persistidos XML): ").append(fallidosXmlCanc).append("<br>");
            body.append("&nbsp;&nbsp;- Fallidos global: ").append(totalFallidosCanc).append("<br><br>");

            // ====== Rutas de archivos realmente generados ======
            StringBuilder rutas = new StringBuilder();
            if (logEstructuraPort.exists()) rutas.append("&nbsp;&nbsp;- ").append(logEstructuraPort.getAbsolutePath()).append("<br>");
            if (logCsvPort.exists())        rutas.append("&nbsp;&nbsp;- ").append(logCsvPort.getAbsolutePath()).append("<br>");
            if (xmlFallidosPort.exists())   rutas.append("&nbsp;&nbsp;- ").append(xmlFallidosPort.getAbsolutePath()).append("<br>");
            if (logEstructuraCanc.exists()) rutas.append("&nbsp;&nbsp;- ").append(logEstructuraCanc.getAbsolutePath()).append("<br>");
            if (logCsvCanc.exists())        rutas.append("&nbsp;&nbsp;- ").append(logCsvCanc.getAbsolutePath()).append("<br>");
            if (xmlFallidosCanc.exists())   rutas.append("&nbsp;&nbsp;- ").append(xmlFallidosCanc.getAbsolutePath()).append("<br>");

            if (rutas.length() > 0) {
                body.append("Ruta de archivos generados logs/xml:<br>");
                body.append(rutas);
                body.append("<br>");
            }

            body.append("Fin del reporte automático.<br>");
            body.append("</body></html>");

            // ====== Archivo espejo en local ======
            if (localMode) {
                generarArchivoEspejoCorreo(subject, body.toString(), basePath, fechaLogs);
                LOGGER.info("[LocalMode] Archivo espejo generado en {}", basePath);
            } else {
                LOGGER.info("[FTPMODE] No se genera archivo espejo en PROD");
            }

            // ====== Enviar correo ======
            mailService.sendEmail(to, subjectWithHost.toString(), body.toString());
            LOGGER.info("Correo de resumen detallado enviado correctamente.");

        } catch (Exception e) {
            LOGGER.error("Error general en armado/envío de mail detallado", e);
        }
    }



    // Contar PortData en cualquier XML (para origen real)
    private int contarPortDataXML(File xmlFile) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(xmlFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("<PortData") && !trimmed.startsWith("</PortData")) {
                    count++;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("No se pudo contar PortData en {}", xmlFile.getName(), e);
        }
        return count;
    }

    // Estructura inválida: deduplicar por PortID
    private int contarErroresEstructura(File logFile) {
        Set<String> ids = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            String currentPortId = null;
            while ((line = br.readLine()) != null) {
                if (line.contains("<PortID>")) {
                    currentPortId = line.replaceAll(".*<PortID>(.*?)</PortID>.*", "$1").trim();
                }
                if (line.startsWith("Error:") && currentPortId != null) {
                    ids.add(currentPortId);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("No se pudo contar errores de estructura en {}", logFile.getName(), e);
        }
        return ids.size();
    }

    // Contenido inválido: deduplicar por PortID
    private int contarErroresCSV(File logFile) {
        Set<String> ids = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<PortID>")) {
                    String id = line.replaceAll(".*<PortID>(.*?)</PortID>.*", "$1").trim();
                    ids.add(id);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("No se pudo contar errores CSV en {}", logFile.getName(), e);
        }
        return ids.size();
    }

    // Fallidos XML: cuenta PortData
    private int contarFallidosXML(File xmlFile) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(xmlFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<PortData>")) {
                    count++;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("No se pudo contar fallidos XML en {}", xmlFile.getName(), e);
        }
        return count;
    }

    private void generarArchivoEspejoCorreo(String subject, String bodyHtml, String basePath, String fechaActualStr) {
        try {
            // Validar basePath
            if (basePath == null || basePath.trim().isEmpty()) {
                LOGGER.warn("Parametro basePath vacío o nulo, se usará directorio actual");
                basePath = ".";
            }
            basePath = basePath.trim();

            // Validar/crear directorio
            File dir = new File(basePath);
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    LOGGER.info("Directorio {} creado para archivo espejo de correo", dir.getAbsolutePath());
                } else {
                    LOGGER.warn("No se pudo crear directorio {}, se usará directorio actual", basePath);
                    dir = new File(".");
                }
            }

            // Archivo espejo en HTML
            File mailLogFile = new File(dir, "correo_sincronizacion_" + fechaActualStr + ".html");

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(mailLogFile, false))) {
                // Encabezado HTML básico
                bw.write("<!DOCTYPE html>\n");
                bw.write("<html><head><meta charset='UTF-8'></head><body style='font-family: monospace;'>\n");
                bw.write("<p><strong>Asunto:</strong> " + subject + " | Host: " + getHostName() + "</p>\n");
                // Cuerpo del correo tal cual lo armaste en enviarMailSyncOkDetalle
                bw.write(bodyHtml);
                // Cierre HTML
                bw.write("\n</body></html>");
            }

            LOGGER.info("Copia física del correo generada en: {}", mailLogFile.getAbsolutePath());

        } catch (Exception e) {
            LOGGER.error("Error generando archivo espejo del correo", e);
        }
    }

    @Override
    public void enviarMailErrorABDDetalle(String causa, boolean localMode) {
        try {
            final String subject = "SNS-Error conexión SFTP-ABD";
            String to = paramService.getParamByName(ParametrosABD.ABD_MAIL_LIST);

            StringBuilder subjectWithHost = new StringBuilder();
            subjectWithHost.append(subject).append(":Host:").append(getHostName());

            StringBuilder sbBody = new StringBuilder();
            sbBody.append(paramService.getParamByName(ParametrosABD.ABD_ERROR_SERVIDOR));
            sbBody.append("\nDescripción detallada del error: ").append(causa);

            // === Enviar correo normal
            mailService.sendEmail(to, subjectWithHost.toString(), sbBody.toString());

            // === Generar espejo solo en modo local/QA
            LOGGER.info("[LocalMode] valor localmodel: {}", localMode);
            if (localMode) {
                String basePath = paramService.getParamByName("port_XMLLOG_path.portados");
                LOGGER.info("[LocalMode] Archivo espejo ruta: {}", basePath);
                if (basePath == null || basePath.trim().isEmpty()) {
                    basePath = ".";
                }
                File dir = new File(basePath.trim());
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fechaActualStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
                File espejo = new File(dir, "correo_errorABD_" + fechaActualStr + ".txt");

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(espejo, false))) {
                    bw.write("Asunto: " + subjectWithHost.toString() + "\n\n");
                    bw.write(sbBody.toString());
                }

                LOGGER.info("[LocalMode] Archivo espejo del correo de error generado en: {}", espejo.getAbsolutePath());
            }

        } catch (Exception e) {
            LOGGER.error("Error enviando/generando correo de error detallado", e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public NumeroPortado getNumeroPortado(String numero) {
        return numerosPortadosDAO.get(numero);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public NumeroPortado update(NumeroPortado numero) {
        return numerosPortadosDAO.update(numero);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public NumeroCancelado update(NumeroCancelado numero) {
        return numerosCanceladosDAO.update(numero);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(String numero) {
        numerosPortadosDAO.delete(numero);
    }
    
    private String getHostName() {
		String hostName="";
		InetAddress host;
		try {
			host = InetAddress.getLocalHost();
			hostName=host.getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hostName;
	}

}
