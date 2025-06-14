package mx.ift.sns.negocio.pnn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileNotFoundException;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.dao.pnn.IPlanMaestroDAO;
import mx.ift.sns.modelo.pnn.PlanMaestroDetalle;
import mx.ift.sns.modelo.pnn.PlanMaestroDetallePK;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.conf.IParametrosService;
import mx.ift.sns.negocio.not.IMailService;
import mx.ift.sns.negocio.port.ObtenerArchivosDiarios;
import mx.ift.sns.negocio.port.ParametrosABD;
import mx.ift.sns.utils.WeblogicNode;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.utils.file.FicheroTemporal;

/**
 * Servicio de planes de numeracion.
 */
@Stateless(name = "PlanMaestroService", mappedName = "PlanMaestroService")
@Remote(IPlanMaestroService.class)
public class PlanMaestroService implements IPlanMaestroService {

    /**
     * Logger de la clase.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanMaestroService.class);

    /**
     * DAO de tipos de plan.
     */
    @Inject
    private IPlanMaestroDAO planMaestroDAO;

    /**
     * Servicio configuracion.
     */
    @EJB
    private IParametrosService paramService;

    /**
     * Servicio correo.
     */
    @EJB
    private IMailService mailService;

    /**
     * Servicio de bitácora.
     */
    @EJB
    private IBitacoraService bitacoraService;

    /**
     * host del ABD.
     */
    private String host;

    /**
     * puerto del ABD.
     */
    private String port;

    /**
     * usuario del ABD.
     */
    private String user;

    /**
     * clave del ABD.
     */
    private String pwd;

    /**
     * path del ABD.
     */
    private String path;

    /**
     * la lista de mail.
     */
    private String mailList;

    private HashMap<String, String> conectionParams;

    public static final String SEPARATOR = ",";
    public static final String QUOTE = "\"";
    /**
     * Formato de fecha 1.
     */
    private static final String FORMATO_FECHA1 = "yyyyMMdd";

    /**
     * Mensaje de bitacora sync ok.
     */
    private static final String BIT_MSG_SYNC_OK = "Sincronización con PMN correcta";

    /**
     * Mensaje de bitacora sync error.
     */
    private static final String BIT_MSG_SYNC_ERROR = "Sincronización con PMN incorrecta.";

    /**
     * Mensaje de bitacora sync error.
     */
    private static final String BIT_MSG_ERR_CONFIG = "Error configuracion Sincronización con PMN.";

    /**
     * Comprueba si todos los parametros estan configurados.
     *
     * @return true si la configuracion es correcta
     */
    private boolean checkConfig() {
        boolean res = true;

        try {
            host = paramService.getParamByName(ParametrosABD.ABD_HOST);
            port = paramService.getParamByName(ParametrosABD.ABD_PORT);
            user = paramService.getParamByName(ParametrosABD.ABD_USER);
            pwd = paramService.getParamByName(ParametrosABD.ABD_PASSWORD);
            path = paramService.getParamByName(ParametrosABD.PMN_PATH);
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
            LOGGER.error("No esta definido " + ParametrosABD.PMN_PATH);
            res = false;
        }

        if (StringUtils.isEmpty(mailList)) {
            LOGGER.error("No esta definido " + ParametrosABD.ABD_MAIL_LIST);
            res = false;
        }

        if (res) {
            conectionParams = new HashMap<String, String>();
            conectionParams.put("host", host);
            conectionParams.put("port", port);
            conectionParams.put("user", user);
            conectionParams.put("pwd", pwd);
            conectionParams.put("path", path);

        }

        return res;
    }

    /**
     * Construye el nombre del archivo de numeros agregados o actualizados.
     *
     * @param d fecha
     * @return nombre
     */
    private String getNumbersAddedFileName(Date d) {
        String dateToStr = FechasUtils.fechaToString(d, FORMATO_FECHA1);
        return path + "/" + "SNSNosAdd_" + dateToStr + ".csv";
    }

    /**
     * Construye el nombre del archivo de numeros borrados.
     *
     * @param d fecha
     * @return nombre
     */
    private String getNumbersDeletedFileName(Date d) {
        String dateToStr = FechasUtils.fechaToString(d, FORMATO_FECHA1);
        return path + "/" + "SNSNosDel_" + dateToStr + ".csv";
    }

    @Override
    public PlanMaestroDetalle getPlanMaestroDetalle(Long numeroInicial, Long numeroFinal) {
        return planMaestroDAO.getDetalleNumero(numeroInicial, numeroFinal);
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void syncPlanMaestroAsync() {
        LOGGER.debug("");

        if (!checkConfig()) {
            bitacoraService.add(BIT_MSG_ERR_CONFIG);
            return;
        }

        File tmpAdded = null;
        File tmpDeleted = null;

        try {

            // calculamos el dia de ayer
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            LOGGER.debug("Obtenemos ficheros");
            String remoteFileAddedPath = getNumbersAddedFileName(cal.getTime());
            String remoteFileDeletedPath = getNumbersDeletedFileName(cal.getTime());

            tmpAdded = FicheroTemporal.getTmpFileName();
            tmpDeleted = FicheroTemporal.getTmpFileName();

            ObtenerArchivosDiarios ar = new ObtenerArchivosDiarios(tmpAdded.getCanonicalPath(),
                    tmpDeleted.getCanonicalPath(), conectionParams, remoteFileAddedPath, remoteFileDeletedPath);
            ar.getArchidosDiarios();

            if (tmpAdded.length() == 0 || tmpDeleted.length() == 0) {
                throw new FileNotFoundException(remoteFileAddedPath);
            }

			LOGGER.debug("Se leen los archivos CSV");

            // importamos portados
            // ValidadorArchivoAgregadosPMN importAgregados = new ValidadorArchivoAgregadosPMN();

            LOGGER.info("<--Inicia el procesamiento del csv de registros nuevos o modificados del PMN {}", FechasUtils.getActualDate());

            // Cargar BD
            // importAgregados.validar(tmpAdded.getAbsolutePath());

            procesarPlanCSV(tmpAdded);

            LOGGER.info("<--finaliza el procesamiento del csv de registros nuevos o modificados del PMN {}", FechasUtils.getActualDate());

            //ValidadorArchivoEliminadosPMN importEliminados = new ValidadorArchivoEliminadosPMN();

            LOGGER.info("<--Inicia el procesamiento del csv de eliminados {}", FechasUtils.getActualDate());

            // Cargar BD
            //importEliminados.validar(tmpDeleted.getAbsolutePath());

            cancelarPlanCSV(tmpDeleted);

            LOGGER.info("<--finaliza el procesamiento del csv de eliminados {}", FechasUtils.getActualDate());

            bitacoraService.add(BIT_MSG_SYNC_OK);

            LOGGER.info("Sincronización PMN ejecutada en {}: {}", WeblogicNode.getName(), BIT_MSG_SYNC_OK);
        } catch (FileNotFoundException e) {
            LOGGER.error("No se puede acceder al archivo: {}", e.getMessage());
        } catch (FileSystemException e) {
            LOGGER.error("No se puede acceder al archivo: {}", e.getMessage());
            bitacoraService.add(BIT_MSG_SYNC_ERROR);
        } catch (Exception e) {
            LOGGER.error("Error inespedado", e);
            bitacoraService.add(BIT_MSG_SYNC_ERROR);
        } finally {
            if (tmpAdded != null) {
                FileUtils.deleteQuietly(tmpAdded);
            }
            if (tmpDeleted != null) {
                FileUtils.deleteQuietly(tmpDeleted);
            }
        }

    }

    /**
     * Método con funcionalidad parar actualizacion de Plan Maestro con uso de CSV
     *
     * @param fileUpdateSNS
     * @throws Exception
     */
    private void procesarPlanCSV(File fileUpdateSNS) throws Exception {
        List<PlanMaestroDetalle> listSyncronizados = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileUpdateSNS));
            String line = br.readLine();
            int cantidadRegistros = 0;
            while (null != line) {
                String[] datos = line.split(",");
                if (datos.length > 1) {
                    if (!(Arrays.toString(datos).contains("Ido"))) {
                        PlanMaestroDetalle pCSV = new PlanMaestroDetalle();
                        PlanMaestroDetallePK pk = new PlanMaestroDetallePK();
                        pCSV.setIdo(Integer.parseInt(datos[0])); // IDO
                        pk.setNumeroInicial(Long.parseLong(datos[1])); // NO.INICIAL
                        pk.setNumeroFinal(Long.parseLong(datos[2])); // NO.FINAL
                        pCSV.setTipoServicio(datos[3].charAt(0)); // TIPO SERVICIO
                        pCSV.setMpp(datos[4].charAt(0)); // MPP
                        pCSV.setIda(Integer.parseInt(datos[5])); // IDA
                        pCSV.setAreaServicio(Integer.parseInt(datos[6])); // AREA SERVICIO
                        pCSV.setZona(Integer.parseInt(datos[1].substring(1, 2)) == 0 ? 0 : Integer.parseInt(datos[1].substring(0, 1))); // ZONA
                        pCSV.setId(pk);
                        int cData = datos.length;
                        if (cData > 7) {
                            if (datos[7] != null && !datos[7].isEmpty()) {
                                cantidadRegistros = Integer.parseInt(datos[7]);
                            }
                        }
                        LOGGER.info("Plan Maestro Detalle en CSV: {}", pCSV.toString());
                        PlanMaestroDetalle pmd = getPlanMaestroDetalle(pCSV.getId().getNumeroInicial(), pCSV.getId().getNumeroFinal());

                        // Si el número ya existía en el plan maestro se actualizan los datos
                        if (pmd != null) {
                            LOGGER.info("Plan Maestro Detalle en BD: {}", pmd.toString());
                            if (!pmd.equals(pCSV)) {
                                LOGGER.info("Actualizar noInicial: {} y noFinal: {}", pmd.getId().getNumeroInicial(), pmd.getId().getNumeroFinal());

                                pmd.setAreaServicio(pCSV.getAreaServicio());
                                pmd.setMpp(pCSV.getMpp());
                                pmd.setTipoServicio(pCSV.getTipoServicio());
                                pmd.setIda(pCSV.getIda());
                                pmd.setIdo(pCSV.getIdo());
                                pmd.setZona(pCSV.getZona());

                                listSyncronizados.add(pmd);
                            }
                        } else {
                            // Si el número no existe en el plan maestro se agrega
                            LOGGER.info("Se registrarán en el plan maestro: {} y noFinal: {}", pCSV.getId().getNumeroInicial(), pCSV.getId().getNumeroFinal());
                            PlanMaestroDetalle pmdNvo = new PlanMaestroDetalle();
                            pmdNvo.setId(pk);
                            pmdNvo.setAreaServicio(pCSV.getAreaServicio());
                            pmdNvo.setMpp(pCSV.getMpp());
                            pmdNvo.setTipoServicio(pCSV.getTipoServicio());
                            pmdNvo.setIda(pCSV.getIda());
                            pmdNvo.setIdo(pCSV.getIdo());
                            pmdNvo.setZona(pCSV.getZona());

							listSyncronizados.add(pmdNvo);
                        }
                    }
                }

                line = br.readLine();
            }

			planMaestroDAO.saveAll(listSyncronizados);

            LOGGER.info("Portaciones a procesar: {} cambios realizados: {}", cantidadRegistros, listSyncronizados.size());

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != br) {
                br.close();
            }
        }

    }

    public void cancelarPlanCSV(File fileDeleteSNS) {
        BufferedReader br;
        try {
            List<PlanMaestroDetalle> listCancelados = new ArrayList<>();
            LOGGER.info("Procesando deserializacion de csv: {}", fileDeleteSNS.getName());

            br = new BufferedReader(new FileReader(fileDeleteSNS));
            String line = br.readLine();
            int i = 0;
            while (null != line) {
                String[] fields = line.split(SEPARATOR);
                fields = removeTrailingQuotes(fields);
                System.out.println(Arrays.toString(fields));

                PlanMaestroDetalle pCSV = new PlanMaestroDetalle();
                PlanMaestroDetallePK pk = new PlanMaestroDetallePK();

                pCSV.setIdo(Integer.parseInt(fields[0]));
                pk.setNumeroInicial(Long.parseLong(fields[1]));
                pk.setNumeroFinal(Long.parseLong(fields[2]));
                pCSV.setTipoServicio(fields[1].charAt(0));
                pCSV.setMpp(fields[4].charAt(0));
                pCSV.setIda(Integer.parseInt(fields[5]));
                pCSV.setAreaServicio(Integer.parseInt(fields[6]));
                pCSV.setZona(Integer.parseInt(fields[1].substring(1, 2)) == 0 ? 0 : Integer.parseInt(fields[1].substring(0, 1)));
                pCSV.setId(pk);

                PlanMaestroDetalle pmd = getPlanMaestroDetalle(pk.getNumeroInicial(), pk.getNumeroFinal());
                if (pmd != null) {
                    LOGGER.info("Plan Maestro Detalle en BD: {}", pmd.toString());
                    LOGGER.info("Eliminar: {}", pmd.toString());
                    // si no son iguales sufrio cambios en el xml leido y procede a actualizar
                    listCancelados.add(pCSV);

                } else {
                    LOGGER.info("-No. encontro registros en BD con noInicial: {} y noFinal: {}", pk.getNumeroInicial(), pk.getNumeroFinal());
                }
                line = br.readLine();
                i++;
            }
			planMaestroDAO.deleteAll(listCancelados);
            LOGGER.info("Cancelados a procesar: {} eliminaciones realizadas: {}", i, listCancelados.size());

        } catch (Exception e) {
            LOGGER.error("Error exception: {}", e.getMessage());
        }
    }

    private String[] removeTrailingQuotes(String[] fields) {
        String[] result = new String[fields.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = fields[i].replaceAll("^" + QUOTE, "").replaceAll(QUOTE + "$", "");
        }
        return result;
    }
}
