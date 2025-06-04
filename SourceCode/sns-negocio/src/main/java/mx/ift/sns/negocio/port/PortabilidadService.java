package mx.ift.sns.negocio.port;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    //FECHA PROCESO
    Date FECHA_PROCESO = FechasUtils.getFechaHoy("dd.MM.yyyy");
    //termina Fecha Proceso}


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
            status.setTs(FECHA_PROCESO);
            //Termina fecha inicial
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
    public EstatusSincronizacion parsePortabilidad(File tmpPorted, EstatusSincronizacion status) throws Exception {
        LOGGER.info("[parsePortabilidad] valor recibido del status hash: {}", System.identityHashCode(status));
        File tmpPortedCSV = null;

        try {
            ResultadoParser res = new ResultadoParser();

            tmpPortedCSV = FicheroTemporal.getTmpFileName();
            LOGGER.debug("Parseamos a CSV fichero: " + tmpPortedCSV);
            // parseamos portabilidades
            NumerosPortadosXmlParser parserPort = new NumerosPortadosXmlParser();
            parserPort.parse(tmpPorted.getCanonicalPath(), tmpPortedCSV, res);
            status.setPortProcesadas(res.getNumerosOk());
            status.setPortProcesar(res.getNumerosTotal());
            status.setProcesadasTs(new Timestamp(res.getTimestamp().getTime()));
            LOGGER.info("***-************- status:"+status.toString());
            LOGGER.debug("Validamos fichero: " + tmpPortedCSV+" longitud"+tmpPortedCSV.length());
            // importamos portados
            //FJAH 24052025
            //ValidadorArchivoPortadosCSV importPortados = new ValidadorArchivoPortadosCSV();

            LOGGER.info("<--Inicia el procesamiento del csv de portados {}",FechasUtils.getActualDate());

//            Cargar BD
            //importPortados.validar(tmpPortedCSV.getAbsolutePath());
            //validadorArchivoPortadosCSV.validar(tmpPortedCSV.getAbsolutePath());
            LOGGER.info("[parsePortabilidad] Llamando a validar para el archivo CSV: {}", tmpPortedCSV.getAbsolutePath());
            ResultadoValidacionCSV resultadoValidacionCSV = validadorArchivoPortadosCSV.validar(tmpPortedCSV.getAbsolutePath());
            LOGGER.info("[parsePortabilidad] Valor recibido de actionDateLote en resultadoValidacionCSV: '{}'", resultadoValidacionCSV.getActionDateLote());
            if (resultadoValidacionCSV.getActionDateLote() != null) {
                Date fechaActionDateLote = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // sin .S // formato correcto
                    fechaActionDateLote = sdf.parse(resultadoValidacionCSV.getActionDateLote());
                    LOGGER.info("[parsePortabilidad] Fecha parseada de actionDateLote: {}", fechaActionDateLote);
                } catch (ParseException e) {
                    LOGGER.error("[parsePortabilidad] Error al parsear actionDateLote: '{}'. Exception: {}", resultadoValidacionCSV.getActionDateLote(), e.getMessage());
                }
                LOGGER.info("[parsePortabilidad] ANTES de asignar valor al actiondate en el status hash: {}", System.identityHashCode(status));
                status.setActionDateLote(fechaActionDateLote);
                LOGGER.info("[parsePortabilidad] Se asignó actionDateLote en status: {}", fechaActionDateLote);
                LOGGER.info("[parsePortabilidad] DESPUES de recibir actiondate status hash: {}", System.identityHashCode(status));
            }else{
                LOGGER.warn("[parsePortabilidad] No se obtuvo actionDateLote del lote (valor nulo o vacío). Usando FECHA_PROCESO como fallback.");
                status.setActionDateLote(FECHA_PROCESO); // o el valor de fallback que uses
            }

            LOGGER.info("<--finaliza el procesamiento del csv de portados {}", FechasUtils.getActualDate());

            LOGGER.info("[parsePortabilidad] status hash: {}", System.identityHashCode(status));
            LOGGER.info("[parsePortabilidad] VALOR status.getActionDateLote EN EL RETURN status: {}", status.getActionDateLote());
            return status;

        } catch (Exception e) {
            LOGGER.info("ERROR INESPERADO: \n" + e.getMessage());
            throw e;
        } finally {
//            if (tmpPorted != null) {
//                FileUtils.deleteQuietly(tmpPorted);
//            }
//
//            if (tmpPortedCSV != null) {
//                FileUtils.deleteQuietly(tmpPortedCSV);
//            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EstatusSincronizacion parsePortabilidadManual(File tmpPorted, EstatusSincronizacion status) throws Exception {

        File tmpPortedCSV = null;

        try {
            ResultadoParser res = new ResultadoParser();

            tmpPortedCSV = FicheroTemporal.getTmpFileName();
            LOGGER.debug("Parseamos a CSV fichero: " + tmpPortedCSV);
            // parseamos portabilidades
            NumerosPortadosXmlParser parserPort = new NumerosPortadosXmlParser();
            parserPort.parse(tmpPorted.getCanonicalPath(), tmpPortedCSV, res);
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

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EstatusSincronizacion parseDeletedManual(File tmpDeleted, EstatusSincronizacion status) throws Exception {

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

        if (checkConfig() == false) {
            bitacoraService.add(BIT_MSG_ERR_CONFIG);
            LOGGER.warn("[syncBDDPortabilidadAsync] Configuración inválida, abortando.");
            return;
        }

        EstatusSincronizacion status = getStatus();
        LOGGER.info("[syncBDDPortabilidadAsync] Estatus inicial: {}", status);

        if (FechasUtils.esHoy(status.getTs())) {
            LOGGER.info("[syncBDDPortabilidadAsync] El status es de hoy.");
            if ((status.getEstatus() != null) && status.getEstatus().equals(EstatusSincronizacion.ESTATUS_PORT_OK)) {
                LOGGER.info("[syncBDDPortabilidadAsync] Ya fue ejecutado hoy con OK, no hace nada.");
                return;
            } else {
                int r = status.getReintentos().compareTo(MAX_REINTENTOS);
                LOGGER.info("[syncBDDPortabilidadAsync] Reintentos actuales: {}", status.getReintentos());
                if (r > 0) {
                    LOGGER.warn("[syncBDDPortabilidadAsync] Máximo de reintentos alcanzado, abortando.");
                    LOGGER.debug("maximo reintentos.");
                    return;
                } else {
                    LOGGER.debug("aumentamos reintentos");
                    LOGGER.info("[syncBDDPortabilidadAsync] Aumentando reintentos.");
                    status.setReintentos(status.getReintentos().add(BigDecimal.ONE));
                }
            }
        }

        File tmpPorted = null;
        File tmpDeleted = null;

        try {

            // calculamos el dia de ayer
            Calendar cal = Calendar.getInstance();
            //cal.add(Calendar.DATE, -1);
            cal.setTime(FECHA_PROCESO);   // FJAH 28.05.2025 Refactorizada
            cal.add(Calendar.DATE, -1);

            LOGGER.info("[syncBDDPortabilidadAsync] Fecha de proceso: {}", cal.getTime());
            LOGGER.info("[syncBDDPortabilidadAsync] Obteniendo nombres de archivos remotos...");
            String remoteFilePortedPath = getNumbersPortedFileName(cal.getTime());
            String remoteFileDeletedPath = getNumbersDeletedFileName(cal.getTime());
            LOGGER.info("[syncBDDPortabilidadAsync] nombre archivo PORTADO remoto obtenido ..." + remoteFilePortedPath);
            LOGGER.info("[syncBDDPortabilidadAsync] nombre archivo CANCELADO remoto obtenido ..." + remoteFileDeletedPath);

            tmpPorted = FicheroTemporal.getTmpFileName();
            tmpDeleted = FicheroTemporal.getTmpFileName();
            LOGGER.info("[syncBDDPortabilidadAsync] Archivos temporales: ported={}, deleted={}", tmpPorted, tmpDeleted);

            //ConexionSFTP con = new ConexionSFTP();

            //con.download(host, port, user, pwd, tmpPorted.getCanonicalPath(), remoteFilePortedPath);

            //con.download(host, port, user, pwd, tmpDeleted.getCanonicalPath(), remoteFileDeletedPath);

            ObtenerArchivosDiarios ar=new ObtenerArchivosDiarios(tmpPorted.getCanonicalPath(),
            													tmpDeleted.getCanonicalPath(),
            													conectionParams,
            													remoteFilePortedPath,
            													remoteFileDeletedPath);
            LOGGER.info("[syncBDDPortabilidadAsync] Iniciando descarga de archivos diarios...");
            ar.getArchidosDiarios();
            LOGGER.info("[syncBDDPortabilidadAsync] Descarga finalizada.");

            if(tmpPorted.length()==0 || tmpDeleted.length()==0) {
                LOGGER.error("[syncBDDPortabilidadAsync] Algún archivo descargado está vacío. ported.length={}, deleted.length={}", tmpPorted.length(), tmpDeleted.length());
            	throw new FileNotFoundException(remoteFilePortedPath);
            }
            LOGGER.info("[syncBDDPortabilidadAsync] Parseando ficheros a CSV...");
            //EstatusSincronizacion s = me.parsePortabilidad(tmpPorted, status);
            //LOGGER.info("[syncBDDPortabilidadAsync] Resultado parsePortabilidad en s: {}", s);
            LOGGER.info("[syncBDDPortabilidadAsync] ANTES de llamar a parsePortabilidad status hash: {}", System.identityHashCode(status));
            status = me.parsePortabilidad(tmpPorted, status);
            LOGGER.info("[syncBDDPortabilidadAsync] status DESPUES de llamar a parsePortabilidad status hash: {}", System.identityHashCode(status));
            LOGGER.info("[syncBDDPortabilidadAsync] Resultado parseDeleted: {}", status);
            LOGGER.info("[syncBDDPortabilidadAsync] Valor del actionDateLote en status: {}", status.getActionDateLote());
            /*
            status.setPortProcesadas(s.getPortProcesadas());
            status.setPortProcesar(s.getPortProcesadas());
            status.setProcesadasTs(s.getProcesadasTs());
            */
//            EstatusSincronizacion s =me.parseDeleted(tmpDeleted, status);
            LOGGER.info("[syncBDDPortabilidadAsync] ANTES de parseDeleted status hash: {}", System.identityHashCode(status));
            status = me.parseDeleted(tmpDeleted, status);
            LOGGER.info("[syncBDDPortabilidadAsync] DESPUES de parseDeleted status hash: {}", System.identityHashCode(status));
            LOGGER.info("[syncBDDPortabilidadAsync] Resultado parseDeleted: {}", status);
            LOGGER.info("[syncBDDPortabilidadAsync] Valor del actionDateLote en status: {}", status.getActionDateLote());
            /*
            status.setPortCanceladas(s.getPortCanceladas());
            status.setPortCancelar(s.getPortCancelar());
            status.setCanceladasTs(s.getCanceladasTs());
             */


            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_OK);
            //status.setTs(new Date());
            status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada
            status.setReintentos(BigDecimal.ZERO);

            //FJAH 27.05.2025 Refactorización fecha actiondate del XML
            // Obtenemos los totales realmente registrados en BD
            //BigDecimal totalPort = numerosPortadosDAO.getTotalNumerosPortadosHoy();
            //BigDecimal totalCancel = numerosCanceladosDAO.getTotalNumerosCanceladosHoy();

            // *** FJAH: Obtiene actionDate real del archivo procesado ***
            LOGGER.info("[syncBDDPortabilidadAsync] status hash: {}", System.identityHashCode(status));
            LOGGER.info("[syncBDDPortabilidadAsync] Valor del actionDateLote en status: {}", status.getActionDateLote());
            Date fechaActionDateArchivo = status.getActionDateLote();
            LOGGER.info("[syncBDDPortabilidadAsync] Se asignó fechaActionDateArchivo: {}", fechaActionDateArchivo);
            if (fechaActionDateArchivo == null) {
                LOGGER.warn("[syncBDDPortabilidadAsync] No se obtuvo actionDate del lote, usando FECHA_PROCESO como fallback");
                fechaActionDateArchivo = FECHA_PROCESO;
            }

            BigDecimal totalPort = numerosPortadosDAO.getTotalNumerosPortadosPorFecha(fechaActionDateArchivo);
            BigDecimal totalCancel = numerosCanceladosDAO.getTotalNumerosCanceladosPorFecha(fechaActionDateArchivo);

            // Obtenemos los totales que ralmente se ha registrado en BD
            status.setPortProcesadas(totalPort);
            status.setPortCanceladas(totalCancel);
            //status.setPortProcesadas(numerosPortadosDAO.getTotalNumerosPortadosHoy());
            //status.setPortCanceladas(numerosCanceladosDAO.getTotalNumerosCanceladosHoy());
            LOGGER.info("[syncBDDPortabilidadAsync] Totales en BD: portados={}, cancelados={}", totalPort, totalCancel);

            String msg = formatMensaje(BIT_MSG_SYNC_OK, status);
            LOGGER.info("[syncBDDPortabilidadAsync] Mensaje a bitácora: {}", msg);
            bitacoraService.add(msg);

            enviarMailSyncOk(status);
            LOGGER.info("[syncBDDPortabilidadAsync] Sincronización ABD ejecutada en {}: {}", WeblogicNode.getName(), msg);
        } catch (FileNotFoundException e) {
            LOGGER.error("No se puede acceder al archivo: " + e.getMessage());
            bitacoraService.add(BIT_MSG_SYNC_ERROR);

            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
            //status.setTs(new Date());
            status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada

            enviarMailErrorABD("No se puede acceder al archivo: " + e.getMessage());
        } catch (FileSystemException e) {
            LOGGER.error("[syncBDDPortabilidadAsync] No se puede acceder al archivo: {}", e.getMessage(), e);
            bitacoraService.add(BIT_MSG_SYNC_ERROR);

            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);

            //status.setTs(new Date());
            status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada
            enviarMailErrorABD("No se puede acceder al archivo: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("[syncBDDPortabilidadAsync] Error inesperado", e);
            bitacoraService.add(BIT_MSG_SYNC_ERROR);
            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);

            //status.setTs(new Date());
            status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada

            enviarMailErrorABD("Error inespedado: " + e.getMessage());
        } finally {
            LOGGER.info("[syncBDDPortabilidadAsync] Guardando status y eliminando temporales.");

            me.saveStatus(status);

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
                    //status.setTs(new Date());
                    status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada
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
                    //status.setTs(new Date());
                    status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada
                    moverArchivo(tmpPorted);
                    enviarMailErrorABD("Error inespedado en el archivo de portaciones: "+tmpPorted.getName()+"\n" + e.getMessage());
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
                    me.parseDeleted(tmpDeleted, status);
                    /*
                    status.setPortCanceladas(s.getPortCanceladas());
                    status.setPortCancelar((s.getPortCancelar()));
                    status.setCanceladasTs(s.getCanceladasTs());
                     */

                    status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_OK);
                    //status.setTs(new Date());
                    status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada
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
                    //status.setTs(new Date());
                    status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada

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

        try {
            LOGGER.debug("[syncBDDPortabilidad] Tipo de fichero recibido: {}", tipo.getCdg());
            if (tipo.getCdg().equals(TipoFicheroPort.DIARIO_PORTED)) {
                LOGGER.info("[syncBDDPortabilidad] Procesando fichero de portabilidad: {}", file);

                tmp = new File(file);
                LOGGER.debug("[syncBDDPortabilidad] Llamando a parsePortabilidad()");
                status = me.parsePortabilidad(tmp, status);
                LOGGER.info("[syncBDDPortabilidad] parsePortabilidad() terminó correctamente.");
            } else if (tipo.getCdg().equals(TipoFicheroPort.DIARIO_DELETED)) {
                LOGGER.info("[syncBDDPortabilidad] Procesando fichero de cancelaciones: {}", file);
                tmp = new File(file);
                LOGGER.debug("[syncBDDPortabilidad] Llamando a parseDeleted()");
                status = me.parseDeleted(tmp, status);
                LOGGER.info("[syncBDDPortabilidad] parseDeleted() terminó correctamente.");
            }else {
                LOGGER.warn("[syncBDDPortabilidad] Tipo de fichero NO reconocido: {}", tipo.getCdg());
            }

            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_OK);
            //status.setTs(new Date());
            status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada

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
            //status.setTs(new Date());
            status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada

            throw new SincronizacionABDException(BIT_MSG_SYNC_MANUAL_ERROR);

        } catch (FileSystemException e) {
            LOGGER.error("[syncBDDPortabilidad] No se puede acceder a los archivos", e);

            bitacoraService.add(usuario, BIT_MSG_SYNC_ERROR);

            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
            //status.setTs(new Date());
            status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada

            throw new SincronizacionABDException(BIT_MSG_SYNC_ERROR);
        } catch (Exception e) {

            LOGGER.error("[syncBDDPortabilidad] Exception general", e);
            bitacoraService.add(usuario, BIT_MSG_SYNC_MANUAL_ERROR);

            status.setEstatus(EstatusSincronizacion.ESTATUS_PORT_ERR_COMM);
            throw new SincronizacionABDException(BIT_MSG_SYNC_MANUAL_ERROR);
        } finally {
            LOGGER.debug("[syncBDDPortabilidad] Llamando a FicheroTemporal.delete()");
            FicheroTemporal.delete(tmp);
            //status.setTs(new Date());
            status.setTs(FECHA_PROCESO); // FJAH 28.05.2025 Refactorizada
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
