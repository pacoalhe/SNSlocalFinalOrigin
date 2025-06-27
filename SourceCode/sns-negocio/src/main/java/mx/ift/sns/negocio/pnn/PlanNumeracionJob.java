package mx.ift.sns.negocio.pnn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.swing.*;

import mx.ift.sns.modelo.ng.DetalleRangoAsignadoNg;
import mx.ift.sns.modelo.ng.DetalleReporteAbd;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.DetalleRangoAsignadoNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.pnn.DetallePlanAbdPresuscripcion;
import mx.ift.sns.modelo.pnn.Plan;
import mx.ift.sns.modelo.pnn.TipoPlan;
import mx.ift.sns.modelo.pst.DetalleProveedor;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.ng.ISeriesService;
import mx.ift.sns.negocio.nng.ISeriesNngService;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;
import mx.ift.sns.negocio.psts.IProveedoresService;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.utils.file.FicheroTemporal;
import mx.ift.sns.utils.number.IdentificadoresPST;
import mx.ift.sns.utils.number.OficioUtils;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de planes de numeracion.
 */
@Stateless(name = "PlanNumeracionJob", mappedName = "PlanNumeracionJob")
@Remote(IPlanNumeracionJob.class)
public class PlanNumeracionJob implements IPlanNumeracionJob {

    /** Constante string vacio. */
    private static final String VACIO = "";
    
    /**Constante String de modalidad FIJO para
     * asignaciones con tipo de red FIJO 
     */
    private static final String MODALIDAD_FIJO="FIJO";

    /** Constante del 0 como string. */
    private static final String CERO = "0";

    /** Constante para la coma. */
    private static final String COMA = ",";

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanNumeracionJob.class);

    /** Tamaño de los bloques a procesar. */
    private static final int BLOQUE = 5000;

    /** New line. */
    private static final String NL = "\r\n";

    /** Cabecera del reporte ABD. */
    private static final String CABECERA_REPORTE_ABD = "ParticipantID, IDA, ASL, NumberFrom, NumberTo, IsMPP, Type, v_null" + NL;

    /** Cabecera del reporte ABD. */
    private static final String CABECERA_REPORTE_ABD_NUEVO = "ParticipantID, IDA, ZONA, NumberFrom, NumberTo, IsMPP, Type, v_null" + NL;

    /** Cabecera del plan ABD. */
    private static final String CABECERA_PLAN_NUMERACION_ABD =
            "NIR_SERIE, NUMERACION_INICIAL, NUMERACION_FINAL, CLAVE_CENSAL, "
                    + "POBLACION, ESTADO, ACCESS_PROVIDER_ID_CODE, PST, ACTIVATION_DATE, ABN, TIPO_RED, IDA"
                    + NL;

    /** Cabecera del plan NG para PST. */
    private static final String CABECERA_PLAN_NUMERACION_GEOGRAFICA_PST = "CLAVE_CENSAL, POBLACION, MUNICIPIO, ESTADO, "
            + "CENTRAL, PRESUSCRIPCION, REGION, ASL, NIR, SERIE, NUMERACION_INICIAL, NUMERACION_FINAL, "
            + "OCUPACION, TIPO_RED, MODALIDAD, ESTATUS, RAZON_SOCIAL, NOMBRE_CORTO, ID_OPERADOR, OFICIO_RESERVA, "
            + "FECHA_RESERVA, OFICIO_ASIGNACION, FECHA_ASIGNACION, FECHA_CONSOLIDACION, FECHA_MIGRACION, NIR_ANTERIOR, "
            + "PUNTO_ENTREGA, IDO, IDA" + NL;

    /** Cabecera del plan NG para PST Nuevo. */
    private static final String CABECERA_PLAN_NUMERACION_GEOGRAFICA_PST_NUEVO = "ZONA, NUMERACION_INICIAL, NUMERACION_FINAL, "
            + "OCUPACION, MODALIDAD, ESTATUS, RAZON_SOCIAL, NOMBRE_CORTO, ID_OPERADOR, OFICIO_ASIGNACION, FECHA_ASIGNACION, IDO, IDA" + NL;

    /** Cabecera del plan NG publico. */
    private static final String CABECERA_PLAN_NUMERACION_GEOGRAFICA_PUBLICO = "CLAVE_CENSAL, POBLACION, MUNICIPIO, "
            + "ESTADO, PRESUSCRIPCION, REGION, ASL, NIR, SERIE, NUMERACION_INICIAL, NUMERACION_FINAL, "
            + "OCUPACION, TIPO_RED, MODALIDAD, RAZON_SOCIAL, FECHA_ASIGNACION, FECHA_CONSOLIDACION, "
            + "FECHA_MIGRACION, NIR_ANTERIOR" + NL;

    /** Cabecera del plan NG publico Nuevo. */
    private static final String CABECERA_PLAN_NUMERACION_GEOGRAFICA_PUBLICO_NUEVO = "ZONA, NUMERACION_INICIAL, NUMERACION_FINAL, "
            + "OCUPACION, MODALIDAD, RAZON_SOCIAL, FECHA_ASIGNACION" + NL;

    /** Cabecera del plan NNG especifico. */
    private static final String CABECERA_PLAN_NNG_ESPECIFICO = "ParticipanId, IDA, No_Geographic_Number, "
            + "Assignment_Date, Assignment_ID"
            + NL;

    /** Cabecera del plan NNG especifico PST. */
    private static final String CABECERA_PLAN_NNG_ESPECIFICO_PST = "ParticipanId, IDA, Non-Geographic_Number, Assignment_Date, "
    		+ "Assignment_ID"
            + NL;

    /** Cabecera del plan NNG especifico PST. */
    private static final String CABECERA_PLAN_NNG_ESPECIFICO_IFT = "CLAVE_NO_GEOGRAFICA, SERIE, "
            + "NUMERO_USUARIO, PST, USUARIO, OFICIO_ASIGNACION, FECHA_ASIGNACION, IDO/IDD, IDA"
            + NL;

    /** Cabecera del plan NNG para PST. */
    private static final String CABECERA_PLAN_NNG_PST = "PST, CVE_SERVICIO, SERIE, "
            + "ESTATUS, IDO/IDD, IDA"
            + NL;

    /** Cabecera del plan NNG publico. */
    private static final String CABECERA_PLAN_NNG_PUBLICO = "CVE_SERVICIO, SERIE, "
            + "NUMERO_INICIAL, NUMERO_FINAL, PST, ESTATUS"
            + NL;

    /** Cabecera del plan NNG para IFT. */
    private static final String CABECERA_PLAN_NNG_IFT = "PST, CVE_SERVICIO, SERIE, "
            + "NUMERO_INICIAL, NUMERO_FINAL, OCUPACION, IDO/IDD, IDA, FECHA_ASIGNACION, OFICIO_ASIGNACION"
            + NL;

    /** Cabecera del plan NNG para IFT Nuevo. */
    private static final String CABECERA_PLAN_NNG_IFT_NUEVO = "PST, CVE_SERVICIO, "
            + "NUMERO_INICIAL, NUMERO_FINAL, OCUPACION, IDO/IDD, IDA, FECHA_ASIGNACION, OFICIO_ASIGNACION"
            + NL;

    /** Cabecera identificadores de operadores. */
    private static final String CABECERA_IDENTIFICADOR_OPERADOR = "PST, IDO, IDA, IDO/IDD"
            + NL;

    /** Cabecera del plan NNG para IFT. */
    private static final String CABECERA_PLAN_IFT = "CLAVE_CENSAL, POBLACION, MUNICIPIO, ESTADO,"
            + " ABN, NIR/CLAVE_NO_GEOGRAFICO, SERIE, NUMERO_INICIAL, NUMERO_FINAL, OCUPACION,"
            + " TIPO_RED, MODALIDAD, ESTATUS ,RAZON_SOCIAL, NOMBRE CORTO, OFICIO_DE_ASIGNACION,"
            + " FECHA_DE_ASIGNACION, IDO, IDA"
            + NL;

    /** Servicio series. */
    @EJB(mappedName = "PlanNumeracionService")
    private IPlanNumeracionService planNumeracionService;

    /** Servicio series NG. */
    @EJB
    private ISeriesService seriesServiceNg;

    /** Servicio series NNG. */
    @EJB
    private ISeriesNngService seriesServiceNng;

    /** Servicio proveedores. */
    @EJB
    private IProveedoresService proveedoresService;

    /** Servicio de Organizacion Territorial. */
    @EJB
    private IOrganizacionTerritorialService otService;

    /** Servicio bitacora. */
    @EJB(mappedName = "BitacoraService")
    private IBitacoraService bitacora;

    /**
     * Persiste un plan a partir de los ficheros temporales ya creados.
     * @param tmp file
     * @param tmp2 file
     * @param tpPlan tipo Plan
     * @param clave servicio
     * @throws ArchiveException error
     * @throws IOException error
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void crearPlan(File tmp, File tmp2, String tpPlan, ClaveServicio clave)
            throws ArchiveException, IOException {
        TipoPlan tipoPlan = planNumeracionService.getTipoPlanbyId(tpPlan);
        Plan plan = new Plan();

        Calendar c = Calendar.getInstance();
        plan.setFechaGeneracion(c.getTime());

        String nombrePlan = planNumeracionService.getNombre(tipoPlan, c);
        if (clave != null) {
            nombrePlan = nombrePlan.replace("CCC", clave.getCodigo().toString());
        }
        plan.setClaveServicio(clave);
        plan.setNombre(nombrePlan);
        plan.setTipoPlan(tipoPlan);

        /* Create Output Stream that will have final zip files */
        OutputStream zipOut = new FileOutputStream(tmp2);

        /* Create Archive Output Stream that attaches File Output Stream / and specifies type of compression */
        ArchiveOutputStream logicalZip =
                new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, zipOut);

        String nombreCSV = plan.getNombre().replace(".zip", ".csv");

        nombreCSV = nombreCSV.replaceAll("/", "_");

        /* Create Archieve entry - write header information */
        logicalZip.putArchiveEntry(new ZipArchiveEntry(nombreCSV));

        /* Copy input file */
        IOUtils.copy(new FileInputStream(tmp), logicalZip);

        /* Close Archieve entry, write trailer information */
        logicalZip.closeArchiveEntry();

        /* Finish addition of entries to the file */
        logicalZip.finish();

        /* Close output stream, our files are zipped */
        zipOut.close();

        byte[] archivo = FileUtils.readFileToByteArray(tmp2);

        plan.setFichero(archivo);

        planNumeracionService.create(plan);

        String msg = "generacion plan '" + plan.getTipoPlan().getDescripcion() + "' '" + plan.getNombre() + "'";
        LOGGER.info("Generado plan {}  ", nombreCSV);

        if (bitacora != null) {
            bitacora.add(msg);
        }

    }

    /**
    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarReporteABDPortabilidad() {

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());
            fileOutput.write(CABECERA_REPORTE_ABD.getBytes());

            long numRegistros = seriesServiceNg.getDetalleReporteAbdCount();

            if (numRegistros > 0) {
                long bloques = (numRegistros / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleReporteAbd> lista = seriesServiceNg.getDetalleReporteAbd(i * BLOQUE, BLOQUE);

                    for (DetalleReporteAbd detalle : lista) {
                        // ParticipantID, IDA, ASL, NumberFrom, NumberTo, IsMPP, Type

                        StringBuilder b = new StringBuilder();
                        b.append(detalle.getIdo() != null ? detalle.getIdo().toString() : "");
                        b.append(COMA);

                        b.append(detalle.getIda() != null ? detalle.getIda().toString() : "");
                        b.append(COMA);

                        b.append(detalle.getIdAbn().toString());
                        b.append(COMA);

                        b.append(detalle.getId());
                        b.append(COMA);

                        b.append(detalle.getNumFinal());
                        b.append(COMA);

                        b.append(detalle.getIdTipoModalidad() != null ? detalle.getIdTipoModalidad() : "");
                        b.append(COMA);

                        b.append(detalle.getIdTipoRed());

                        b.append(NL);

                        fileOutput.write(b.toString().getBytes());
                    }
                }
            }

            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_ABD_PORTABILIDAD, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando reporte ABD");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando reporte ABD");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando reporte ABD");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }
    */
    
    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarReporteABDPortabilidad() {

        File tmp = null;
        File tmp2 = null;
        File tmp3 = null;
        File tmp4 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();
            tmp3 = FicheroTemporal.getTmpFileName();
            tmp4 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());
            FileOutputStream fileOutput2 = new FileOutputStream(tmp3.getCanonicalPath());

            fileOutput.write(CABECERA_REPORTE_ABD.getBytes());
            fileOutput2.write(CABECERA_REPORTE_ABD_NUEVO.getBytes());

            long numRegistros = seriesServiceNg.getDetalleReporteAbdCount();

            if (numRegistros > 0) {
                long bloques = (numRegistros / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleReporteAbd> lista = seriesServiceNg.getDetalleReporteAbd(i * BLOQUE, BLOQUE);

                    for (DetalleReporteAbd detalle : lista) {
                        /* ParticipantID, IDA, ASL, NumberFrom, NumberTo, IsMPP, Type */

                        StringBuilder b = new StringBuilder();
                        StringBuilder sb = new StringBuilder();

                        b.append(addQuotes(detalle.getIdo() != null ? 
                        		IdentificadoresPST.BCDIDOIDAto30Charecters(detalle.getIdo().toString()) :
                        			IdentificadoresPST.BCDIDOIDAto30Charecters(VACIO)));
                        b.append(COMA);

                        sb.append(addQuotes(detalle.getIdo() != null ?
                        		IdentificadoresPST.BCDIDOIDAto30Charecters(detalle.getIdo().toString()) :
                        			IdentificadoresPST.BCDIDOIDAto30Charecters(VACIO)));
                        sb.append(COMA);

                        b.append(addQuotes(detalle.getIda() != null ? IdentificadoresPST.BCDIDOIDAto30Charecters(detalle.getIda().toString()) : IdentificadoresPST.BCDIDOIDAto30Charecters(VACIO)));
                        b.append(COMA);

                        sb.append(addQuotes(detalle.getIda() != null ? IdentificadoresPST.BCDIDOIDAto30Charecters(detalle.getIda().toString()) : IdentificadoresPST.BCDIDOIDAto30Charecters(VACIO)));
                        sb.append(COMA);

                        b.append(addQuotes(detalle.getIdAbn().toString()));
                        b.append(COMA);

                        sb.append(addQuotes(String.valueOf(detalle.getId().charAt(0))));
                        sb.append(COMA);

                        b.append(addQuotes(detalle.getId()));
                        b.append(COMA);

                        sb.append(addQuotes(detalle.getId()));
                        sb.append(COMA);

                        b.append(addQuotes(detalle.getNumFinal()));
                        b.append(COMA);

                        sb.append(addQuotes(detalle.getNumFinal()));
                        sb.append(COMA);

                        b.append(addQuotes(detalle.getIdTipoModalidad() != null ? detalle.getIdTipoModalidad() : ""));
                        b.append(COMA);

                        sb.append(addQuotes(detalle.getIdTipoModalidad() != null ? detalle.getIdTipoModalidad() : ""));
                        sb.append(COMA);

                        b.append(addQuotes(detalle.getIdTipoRed()));
                        b.append(COMA);
                        b.append(addQuotes(""));

                        sb.append(addQuotes(detalle.getIdTipoRed()));
                        sb.append(COMA);
                        sb.append(addQuotes(""));

                        b.append(NL);
                        sb.append(NL);

                        fileOutput.write(b.toString().getBytes());
                        fileOutput2.write(sb.toString().getBytes());
                    }
                }
            }

            fileOutput.close();
            fileOutput2.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_ABD_PORTABILIDAD, null);
            crearPlan(tmp3, tmp4, TipoPlan.TIPO_PLAN_ABD_PORTABILIDAD_NUEVO, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando reporte ABD");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando reporte ABD");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando reporte ABD");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
            FileUtils.deleteQuietly(tmp3);
            FileUtils.deleteQuietly(tmp4);
        }
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanNumeracionABDPresuscripcion() {

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_NUMERACION_ABD.getBytes());

            int n = seriesServiceNg.findAllRangosAsignadosFijosCount();

            if (n > 0) {
                int bloques = (n / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<RangoSerie> lista = seriesServiceNg.findAllRangosAsignadosFijos(i * BLOQUE, BLOQUE);

                    for (RangoSerie rango : lista) {

                        StringBuilder b = new StringBuilder();

                        // NIR_SERIE
                        b.append(rango.getSerie().getNir().getCodigo());
                        b.append(rango.getSerie().getSnaAsString());
                        b.append(COMA);

                        // NUMERACION
                        b.append(String.format("%04d", rango.getNumInicioAsInt())).append(COMA);
                        b.append(String.format("%04d", rango.getNumFinalAsInt())).append(COMA);

                        // Clave censal
                        b.append(rango.getPoblacion().getInegi());
                        b.append(COMA);

                        // Poblacion.
                        b.append(addQuotes(rango.getPoblacion().getNombre()));
                        b.append(COMA);

                        // Estado
                        b.append(addQuotes(rango.getPoblacion().getMunicipio().getEstado().getNombre()));
                        b.append(COMA);

                        // IDO
                        b.append(rango.getIdoPnnAsString());
                        b.append(COMA);

                        // PST
                        b.append(addQuotes(rango.getAsignatario().getNombre()));
                        b.append(COMA);

                        // Activation date
                        if (rango.getFechaAsignacion() != null) {
                            b.append(FechasUtils.fechaToString(rango.getFechaAsignacion()));
                        } else {
                            b.append(" ");
                        }
                        b.append(COMA);

                        // ABN
                        b.append(rango.getSerie().getNir().getAbn().getCodigoAbn());
                        b.append(COMA);

                        // Tipo red
                        b.append(rango.getTipoRed().getDescripcion());
                        b.append(COMA);

                        // IDA
                        b.append(rango.getIdaPnnAsString());

                        b.append(NL);

                        fileOutput.write(b.toString().getBytes());
                    }
                }
            }
            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_ABD_PRESUSCRIPCION, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion ABD presuscripcion");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion ABD presuscripcion");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion ABD presuscripcion");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanNumeracionABDPresuscripcionD() {

        LOGGER.debug("generando plan numeracion abd presuscripcion");

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_NUMERACION_ABD.getBytes());

            int n = seriesServiceNg.findAllRangosAsignadosFijosCount();

            if (n > 0) {
                int bloques = (n / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetallePlanAbdPresuscripcion> lista = seriesServiceNg.findAllRangosAsignadosFijosD(i * BLOQUE,
                            BLOQUE);

                    for (DetallePlanAbdPresuscripcion detalle : lista) {

                        StringBuilder b = new StringBuilder();

                        // NIR_SERIE
                        b.append(detalle.getCodigoNir());
                        b.append(detalle.getSnaAsString());
                        b.append(COMA);

                        // NUMERACION
                        b.append(String.format("%04d", Integer.valueOf(detalle.getNumInicio()))).append(COMA);
                        b.append(String.format("%04d", Integer.valueOf(detalle.getNumFinal()))).append(COMA);

                        // Clave censal
                        b.append(detalle.getInegi());
                        b.append(COMA);

                        // Poblacion.
                        b.append(addQuotes(detalle.getNomPoblacion()));
                        b.append(COMA);

                        // Estado
                        b.append(addQuotes(detalle.getNomEstado()));
                        b.append(COMA);

                        // IDO
                        b.append(detalle.getIdoPnnAsString());
                        b.append(COMA);

                        // PST
                        b.append(addQuotes(detalle.getNomProveedor()));
                        b.append(COMA);

                        // Activation date
                        if (detalle.getFechaAsignacion() != null) {
                            b.append(FechasUtils.fechaToString(detalle.getFechaAsignacion()));
                        } else {
                            b.append(" ");
                        }
                        b.append(COMA);

                        // ABN
                        b.append(detalle.getCodigoAbn());
                        b.append(COMA);

                        // Tipo red
                        b.append(detalle.getDescTipoRed());
                        b.append(COMA);

                        // IDA
                        b.append(detalle.getIdaPnnAsString());

                        b.append(NL);

                        fileOutput.write(b.toString().getBytes());
                    }
                }
            }
            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_ABD_PRESUSCRIPCION, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion ABD presuscripcion");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion ABD presuscripcion");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion ABD presuscripcion");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }

    /**
     * Añade comillas al dato.
     * @param nombre dato
     * @return datos
     */
    private String addQuotes(String nombre) {

        return "\"" + nombre + "\"";
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanNumeracionGeograficaPST() {
        LOGGER.debug("generando plan numeracion geografica pst. ");

        File tmp = null;
        File tmp2 = null;
        File tmp3 = null;
        File tmp4 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();
            tmp3 = FicheroTemporal.getTmpFileName();
            tmp4 = FicheroTemporal.getTmpFileName();

            // if (LOGGER.isDebugEnabled()) {
            // LOGGER.debug("tmp {} tmp2 {}", tmp.getAbsolutePath(), tmp2.getAbsolutePath());
            // }

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());
            FileOutputStream fileOutput2 = new FileOutputStream(tmp3.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_NUMERACION_GEOGRAFICA_PST.getBytes());
            fileOutput2.write(CABECERA_PLAN_NUMERACION_GEOGRAFICA_PST_NUEVO.getBytes());

            int n = seriesServiceNg.getNumRangosAsignados().intValue();

            if (n > 0) {

                int bloques = (n / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleRangoAsignadoNg> lista = seriesServiceNg.getRangosAsignadosD(i * BLOQUE, BLOQUE);
                    for (DetalleRangoAsignadoNg detalle : lista) {

                        StringBuilder b = new StringBuilder();
                        StringBuilder sb = new StringBuilder();
                        StringBuilder numeracion = new StringBuilder();

                        /* CLAVE_CENSAL */
                        b.append(addQuotes(detalle.getInegi()));
                        b.append(COMA);

                        /* POBLACION */
                        b.append(addQuotes(detalle.getNombrePoblacion()));
                        b.append(COMA);

                        /* MUNICIPIO */
                        b.append(addQuotes(detalle.getNombreMunicipio()));
                        b.append(COMA);

                        /* ESTADO */
                        b.append(addQuotes(detalle.getNombreEstado()));
                        b.append(COMA);

                        /* CENTRAL */
                        b.append(detalle.getNombreCentralOrigen() != null ? addQuotes(detalle.getNombreCentralOrigen())
                                : VACIO);
                        b.append(COMA);

                        /* PRESUSCRIPCION */
                        b.append(detalle.getPresuscripcion() != null ? detalle.getPresuscripcion() : VACIO);
                        b.append(COMA);

                        /* REGION */
                        b.append(detalle.getDescRegion() != null ? detalle.getDescRegion() : VACIO);
                        b.append(COMA);

                        sb.append(detalle.getDescRegion() != null ? detalle.getDescRegion() : VACIO);
                        sb.append(COMA);

                        /* ASL */
                        b.append(detalle.getIdAbn());
                        b.append(COMA);

                        /* NIR */
                        b.append(detalle.getCodigoNir());
                        b.append(COMA);

                        /* SERIE */
                        b.append(detalle.getSnaAsString());
                        b.append(COMA);

                        /* NUMERACION INICIAL */
                        b.append(detalle.getNumInicio());
                        b.append(COMA);

                        sb.append(detalle.getCodigoNir()).append(detalle.getSnaAsString()).append(StringUtils.leftPad(detalle.getNumInicio(), 4, "0"));
                        sb.append(COMA);

                        /* NUMERACION FINAL */
                        b.append(detalle.getNumFinal());
                        b.append(COMA);

                        sb.append(detalle.getCodigoNir()).append(detalle.getSnaAsString()).append(StringUtils.leftPad(detalle.getNumFinal(), 4, "0"));
                        sb.append(COMA);

                        /* OCUPACION */
                        b.append((Integer.parseInt(detalle.getNumFinal())
                                - Integer.parseInt(detalle.getNumInicio())) + 1);
                        b.append(COMA);

                        sb.append((Integer.parseInt(detalle.getNumFinal())
                                - Integer.parseInt(detalle.getNumInicio())) + 1);
                        sb.append(COMA);

                        /* TIPO DE RED */
                        b.append(detalle.getDescTipoRed());
                        b.append(COMA);

                        /* MODALIDAD */
                        b.append(TipoRed.MOVIL.equals(detalle.getCodTipoRed()) ? detalle.getIdTipoModalidad() : MODALIDAD_FIJO);
                        b.append(COMA);

                        sb.append(TipoRed.MOVIL.equals(detalle.getCodTipoRed()) ? detalle.getIdTipoModalidad() : MODALIDAD_FIJO);
                        sb.append(COMA);

                        /* ESTATUS */
                        b.append(detalle.getCodEstatusRango());
                        b.append(COMA);

                        sb.append(detalle.getCodEstatusRango());
                        sb.append(COMA);

                        /* RAZON SOCIAL */
                        b.append(addQuotes(detalle.getNombrePst()));
                        b.append(COMA);

                        sb.append(addQuotes(detalle.getNombrePst()));
                        sb.append(COMA);

                        /* NOMBRE_CORTO */
                        b.append(detalle.getNombreCortoPst());
                        b.append(COMA);

                        sb.append(detalle.getNombreCortoPst());
                        sb.append(COMA);

                        /* ID OPERADOR */
                        //b.append(detalle.getCodigoPst());
                        //Se cambia para ajustar el plan
                        b.append(detalle.getIdOperador());
                        b.append(COMA);

                        sb.append(detalle.getIdOperador());
                        sb.append(COMA);

                        /* OFICIO RESERVA */
                        b.append(detalle.getOficioReserva() != null ? detalle.getOficioReserva() : VACIO);
                        b.append(COMA);

                        /* FECHA RESERVA */
                        b.append(FechasUtils.fechaToString(detalle.getFechaReserva()));
                        b.append(COMA);

                        /* OFICIO ASIGNACION */
                        b.append(detalle.getOficioSolAsig() != null ? detalle.getOficioSolAsig() : VACIO);
                        b.append(COMA);

                        sb.append(detalle.getOficioSolAsig() != null ? detalle.getOficioSolAsig() : VACIO);
                        sb.append(COMA);

                        /* FECHA ASIGNACION */
                        b.append(FechasUtils.fechaToString(detalle.getFechaSolAsig()));
                        b.append(COMA);

                        sb.append(FechasUtils.fechaToString(detalle.getFechaSolAsig()));
                        sb.append(COMA);

                        /* FECHA CONSOLIDACION */
                        b.append(FechasUtils.fechaToString(detalle.getFechaConsolidacion()));
                        b.append(COMA);

                        /* FECHA MIGRACION */
                        if (null != detalle.getFechaMigracion()) {
                            b.append(FechasUtils.fechaToString(detalle.getFechaMigracion()));
                            b.append(COMA);
                        } else {
                            b.append(VACIO);
                            b.append(COMA);
                        }

                        /* NIR ANTERIOR */
                        if (null != detalle.getIdNirAnterior()) {
                            b.append(detalle.getIdNirAnterior());
                        } else {
                            b.append(VACIO);
                        }
                        b.append(COMA);

                        /* PUNTO ENTREGA */
                        b.append(detalle.getNombreCentralDestino() != null
                                ? addQuotes(detalle.getNombreCentralDestino())
                                : VACIO);
                        b.append(COMA);

                        /* IDO */
                        b.append(detalle.getIdoPnnAsString());
                        b.append(COMA);

                        sb.append(detalle.getIdoPnnAsString());
                        sb.append(COMA);

                        /* IDA */
                        b.append(detalle.getIdaPnnAsString());
                        b.append(COMA);

                        sb.append(detalle.getIdaPnnAsString());
                        sb.append(COMA);

                        b.append(NL);
                        sb.append(NL);

                        fileOutput.write(b.toString().getBytes());
                        fileOutput2.write(sb.toString().getBytes());
                    }
                }
            }
            fileOutput.close();
            fileOutput2.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_NG_PST, null);
            crearPlan(tmp3, tmp4, TipoPlan.TIPO_PLAN_NG_PST_NUEVO, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion geografica pst.");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion geografica pst.");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion geografica pst.");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
            FileUtils.deleteQuietly(tmp3);
            FileUtils.deleteQuietly(tmp4);
        }

    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanNumeracionGeograficaPublico() {

        File tmp = null;
        File tmp2 = null;
        File tmp3 = null;
        File tmp4 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();
            tmp3 = FicheroTemporal.getTmpFileName();
            tmp4 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());
            FileOutputStream fileOutput2 = new FileOutputStream(tmp3.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_NUMERACION_GEOGRAFICA_PUBLICO.getBytes());
            fileOutput2.write(CABECERA_PLAN_NUMERACION_GEOGRAFICA_PUBLICO_NUEVO.getBytes());

            int n = seriesServiceNg.getNumRangosAsignados().intValue();

            if (n > 0) {
                int bloques = (n / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleRangoAsignadoNg> lista = seriesServiceNg.getRangosAsignadosD(i * BLOQUE, BLOQUE);
                    for (DetalleRangoAsignadoNg detalle : lista) {

                        StringBuilder b = new StringBuilder();
                        StringBuilder sb = new StringBuilder();

                        /* CLAVE_CENSAL */
                        b.append(addQuotes(detalle.getInegi()));
                        b.append(COMA);

                        /* POBLACION */
                        b.append(addQuotes(detalle.getNombrePoblacion()));
                        b.append(COMA);

                        /* MUNICIPIO */
                        b.append(addQuotes(detalle.getNombreMunicipio()));
                        b.append(COMA);

                        /* ESTADO */
                        b.append(addQuotes(detalle.getNombreEstado()));
                        b.append(COMA);

                        /* PRESUSCRIPCION */
                        b.append(detalle.getPresuscripcion() != null ? detalle.getPresuscripcion() : VACIO);
                        b.append(COMA);

                        /* REGION */
                        b.append(detalle.getDescRegion() != null ? detalle.getDescRegion() : VACIO);
                        b.append(COMA);

                        sb.append(detalle.getDescRegion() != null ? detalle.getDescRegion() : VACIO);
                        sb.append(COMA);

                        /* ASL */
                        b.append(detalle.getIdAbn());
                        b.append(COMA);

                        /* NIR */
                        b.append(detalle.getCodigoNir());
                        b.append(COMA);

                        /* SERIE */
                        b.append(detalle.getSnaAsString());
                        b.append(COMA);

                        /* NUMERACION INICIAL */
                        b.append(detalle.getNumInicio());
                        b.append(COMA);

                        sb.append(detalle.getCodigoNir()).append(detalle.getSnaAsString()).append(StringUtils.leftPad(detalle.getNumInicio(), 4, "0"));
                        sb.append(COMA);

                        /* NUMERACION FINAL */
                        b.append(detalle.getNumFinal());
                        b.append(COMA);

                        sb.append(detalle.getCodigoNir()).append(detalle.getSnaAsString()).append(StringUtils.leftPad(detalle.getNumFinal(), 4, "0"));
                        sb.append(COMA);

                        /* OCUPACION */
                        b.append((Integer.parseInt(detalle.getNumFinal())
                                - Integer.parseInt(detalle.getNumInicio())) + 1);
                        b.append(COMA);

                        sb.append((Integer.parseInt(detalle.getNumFinal())
                                - Integer.parseInt(detalle.getNumInicio())) + 1);
                        sb.append(COMA);

                        /* TIPO DE RED */
                        b.append(detalle.getDescTipoRed());
                        b.append(COMA);

                        /* MODALIDAD */
                        b.append(TipoRed.MOVIL.equals(detalle.getCodTipoRed()) ? detalle.getIdTipoModalidad() : MODALIDAD_FIJO);
                        b.append(COMA);

                        sb.append(TipoRed.MOVIL.equals(detalle.getCodTipoRed()) ? detalle.getIdTipoModalidad() : MODALIDAD_FIJO);
                        sb.append(COMA);

                        /* RAZON SOCIAL */
                        b.append(addQuotes(detalle.getNombrePst()));
                        b.append(COMA);

                        sb.append(addQuotes(detalle.getNombrePst()));
                        sb.append(COMA);

                        /* FECHA ASIGNACION */
                        b.append(FechasUtils.fechaToString(detalle.getFechaSolAsig()));
                        b.append(COMA);

                        sb.append(FechasUtils.fechaToString(detalle.getFechaSolAsig()));
                        sb.append(COMA);

                        /* FECHA CONSOLIDACION */
                        b.append(FechasUtils.fechaToString(detalle.getFechaConsolidacion()));
                        b.append(COMA);

                        /* FECHA MIGRACION */
                        if (null != detalle.getFechaMigracion()) {
                            b.append(FechasUtils.fechaToString(detalle.getFechaMigracion()));
                        } else {
                            b.append(VACIO);
                        }
                        b.append(COMA);

                        /* NIR ANTERIOR */
                        if (null != detalle.getIdNirAnterior()) {
                            b.append(detalle.getIdNirAnterior());
                        } else {
                            b.append(VACIO);
                        }
                        b.append(COMA);

                        b.append(NL);
                        sb.append(NL);

                        fileOutput.write(b.toString().getBytes());
                        fileOutput2.write(sb.toString().getBytes());
                    }
                }
            }
            fileOutput.close();
            fileOutput2.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_NG_PUBLICO, null);
            crearPlan(tmp3, tmp4, TipoPlan.TIPO_PLAN_NG_PUBLICO_NUEVO, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion geografica publico.");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion geografica publico.");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion geografica publico.");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanNngEspecifica() {

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_NNG_ESPECIFICO.getBytes());

            int n = seriesServiceNng.findAllRangosAsignadosEspecificoCount();

            if (n > 0) {
                int bloques = (n / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleRangoAsignadoNng> lista = seriesServiceNng.findAllRangosAsignadosEspecificoD(i * BLOQUE,
                            BLOQUE);

                    for (DetalleRangoAsignadoNng detalle : lista) {

                        for (int j = Integer.valueOf(detalle.getNumInicio()); j <= Integer
                                .valueOf(detalle.getNumFinal()); j++) {
                            StringBuilder b = new StringBuilder();
                            // ParticipantID
                            b.append(detalle.getBcdAsString());
                            b.append(COMA);
                            // IDA
                            b.append(
                                    detalle.getCodTipoPst().equals(TipoProveedor.COMERCIALIZADORA) ? detalle.getIda()
                                            : detalle.getBcdAsString()).append(COMA);
                            // No_Geographic_Number
                            b.append(detalle.getIdClaveServicio().toString())
                                    .append(detalle.getSnaAsString())
                                    .append(String.format("%04d", j));
                            b.append(COMA);
                            // Assignment_Date .
                            b.append(FechasUtils.fechaToString(detalle.getFechaSolAsig()));
                            b.append(COMA);
                            // Assignment_ID
                            b.append(detalle.getOficioSolAsig() != null ? detalle.getOficioSolAsig() : VACIO);
                            b.append(NL);
                            fileOutput.write(b.toString().getBytes());
                        }
                    }
                }
            }
            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_NNG_ESPECIFICA, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }

    /**
    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanNngEspecificaPst() {

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_NNG_ESPECIFICO_PST.getBytes());

            int n = seriesServiceNng.findAllRangosAsignadosEspecificoCount();

            if (n > 0) {
                int bloques = (n / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleRangoAsignadoNng> lista = seriesServiceNng.findAllRangosAsignadosEspecificoD(i * BLOQUE,
                            BLOQUE);

                    for (DetalleRangoAsignadoNng detalle : lista) {

                        for (int j = Integer.valueOf(detalle.getNumInicio()); j <= Integer
                                .valueOf(detalle.getNumFinal()); j++) {
                            StringBuilder b = new StringBuilder();

                            
                            //ParticipanId
                            b.append(detalle.getAbcAsString());
                            b.append(COMA);
                            
                            // IDA
                            b.append(
                                    detalle.getCodTipoPst().equals(TipoProveedor.COMERCIALIZADORA) ? detalle
                                            .getIdaAsString()
                                            : detalle.getAbcAsString());
                            
                            // Non-Geographic_Number
                            b.append(detalle.getIdClaveServicio().toString())
                                    .append(detalle.getSnaAsString())
                                    .append(String.format("%04d", j));
                            b.append(COMA);

                            // Assignment_Date .
                            b.append(FechasUtils.fechaToString(detalle.getFechaSolAsig()));
                            b.append(COMA);
                            
                            // Assignment_ID
                            b.append(detalle.getOficioSolAsig() != null ? detalle.getOficioSolAsig() : VACIO);
                            b.append(COMA);

                            b.append(NL);

                            fileOutput.write(b.toString().getBytes());
                        }
                    }
                }
            }
            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_NNG_ESPECIFICA_PST, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica PST");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica PST");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica PST");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }
    
    */
    
    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanNngEspecificaPst() {

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_NNG_ESPECIFICO_PST.getBytes());

             int n = seriesServiceNng.findAllRangosAsignadosEspecificoCount();

            if (n > 0) {
                int bloques = (n / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleRangoAsignadoNng> lista = seriesServiceNng.findAllRangosAsignadosEspecificoD(i * BLOQUE,
                            BLOQUE);

                    for (DetalleRangoAsignadoNng detalle : lista) {

                        for (int j = Integer.valueOf(detalle.getNumInicio()); j <= Integer
                                .valueOf(detalle.getNumFinal()); j++) {
                            StringBuilder b = new StringBuilder();

                            
                            //ParticipanId
                            b.append(addQuotes( detalle.getBcd() != null ? detalle.getBcdAsString() : ""));
                            b.append(COMA);

                            // IDA
                            b.append(addQuotes(detalle.getCodTipoPst().equals(TipoProveedor.COMERCIALIZADORA) ?
                                    IdentificadoresPST.BCDIDOIDAto30Charecters(detalle.getIdaAsString())
                                            : IdentificadoresPST.BCDIDOIDAto30Charecters(detalle.getBcdAsString())));
                            b.append(COMA);
                            
                            // Non-Geographic_Number
                            StringBuilder noGeoNumber=new StringBuilder();
                            noGeoNumber.append(detalle.getIdClaveServicio().toString()).append(detalle.getSnaAsString()).append(String.format("%04d", j));
                            b.append(addQuotes(noGeoNumber.toString()));
                            b.append(COMA);

                            // Assignment_Date .
                            b.append(addQuotes(FechasUtils.fechaToString(detalle.getFechaSolAsig(),"YYYYMMdd")));
                            b.append(COMA);
                            
                            // Assignment_ID
                            b.append(addQuotes(detalle.getOficioSolAsig() != null 
                            		? OficioUtils.OficioFormat30Charecters(detalle.getOficioSolAsig()) : 
                            			OficioUtils.OficioFormat30Charecters(VACIO)));
                           
                            b.append(NL);

                            fileOutput.write(b.toString().getBytes());
                        }
                    }
                }
            }
            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_NNG_ESPECIFICA_PST, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica PST");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica PST");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica PST");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }
    
    

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanNngPst() {

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_NNG_PST.getBytes());

            int n = seriesServiceNng.findAllRangosAsignadosCount();

            if (n > 0) {
                int bloques = (n / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleRangoAsignadoNng> lista = seriesServiceNng.findAllRangosAsignadosD(i * BLOQUE, BLOQUE);

                    for (DetalleRangoAsignadoNng detalle : lista) {

                        StringBuilder b = new StringBuilder();

                        // PST
                        b.append(addQuotes(detalle.getNombrePst()));
                        b.append(COMA);

                        // CVE_SERVICIO
                        b.append(detalle.getIdClaveServicio().toString());
                        b.append(COMA);

                        // SERIE
                        b.append(detalle.getSnaAsString());
                        b.append(COMA);

                        // ESTATUS
                        b.append(detalle.getCodEstatusRango());
                        b.append(COMA);

                        // BCD
                        b.append(detalle.getIdaAsString() != null && !detalle.getIdaAsString().equals("") ? 
                		detalle.getIdaAsString() : detalle.getBcdAsString());
                        b.append(COMA);

                        // IDA
                        b.append(
                                detalle.getCodTipoPst().equals(TipoProveedor.COMERCIALIZADORA) ? detalle
                                        .getIdaAsString() : detalle.getBcdAsString());

                        b.append(NL);

                        fileOutput.write(b.toString().getBytes());
                    }
                }
            }
            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_NNG_PST, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG para PST");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG para PST");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG para PST");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanNngPublico(ClaveServicio clave) {

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_NNG_PUBLICO.getBytes());

            int n = seriesServiceNng.findAllRangosAsignadosByClaveCount(clave);

            if (n > 0) {
                int bloques = (n / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleRangoAsignadoNng> lista = seriesServiceNng
                            .findAllRangosAsignadosD(clave.getCodigo(), i * BLOQUE, BLOQUE);

                    for (DetalleRangoAsignadoNng detalle : lista) {

                        StringBuilder b = new StringBuilder();

                        // CVE_SERVICIO
                        b.append(detalle.getIdClaveServicio());
                        b.append(COMA);

                        // SERIE
                        b.append(detalle.getSnaAsString());
                        b.append(COMA);

                        // NUMERO_INICIAL
                        b.append(detalle.getNumInicio());
                        b.append(COMA);

                        // NUMERO_FINAL
                        b.append(detalle.getNumFinal());
                        b.append(COMA);

                        // PST
                        b.append(addQuotes(detalle.getNombrePst()));
                        b.append(COMA);

                        // ESTATUS
                        b.append(detalle.getCodEstatusRango());

                        b.append(NL);

                        fileOutput.write(b.toString().getBytes());
                    }
                }
            }
            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_NNG_PUBLICO, clave);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }

    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanNngIFT() {

        File tmp = null;
        File tmp2 = null;
        File tmp3 = null;
        File tmp4 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();
            tmp3 = FicheroTemporal.getTmpFileName();
            tmp4 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());
            FileOutputStream fileOutput2 = new FileOutputStream(tmp2.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_NNG_IFT.getBytes());
            fileOutput2.write(CABECERA_PLAN_NNG_IFT_NUEVO.getBytes());

            int n = seriesServiceNng.findAllRangosAsignadosCount();

            if (n > 0) {
                int bloques = (n / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleRangoAsignadoNng> lista = seriesServiceNng.findAllRangosAsignadosD(i * BLOQUE, BLOQUE);

                    for (DetalleRangoAsignadoNng detalle : lista) {

                        StringBuilder b = new StringBuilder();
                        StringBuilder sb = new StringBuilder();

                        // PST
                        b.append(addQuotes(detalle.getNombrePst()));
                        b.append(COMA);

                        sb.append(addQuotes(detalle.getNombrePst()));
                        sb.append(COMA);

                        // CVE_SERVICIO
                        b.append(detalle.getIdClaveServicio().toString());
                        b.append(COMA);

                        sb.append(detalle.getIdClaveServicio().toString());
                        sb.append(COMA);

                        // SERIE
                        b.append(detalle.getSnaAsString());
                        b.append(COMA);

                        // NUMERO_INICIAL
                        b.append(detalle.getNumInicio());
                        b.append(COMA);

                        sb.append(detalle.getIdClaveServicio().toString()).
                                append(StringUtils.leftPad(detalle.getSnaAsString(), 3, "0")).
                                append(StringUtils.leftPad(detalle.getNumInicio(), 4, "0"));
                        sb.append(COMA);

                        // NUMERO_FINAL
                        b.append(detalle.getNumFinal());
                        b.append(COMA);

                        sb.append(detalle.getIdClaveServicio().toString()).
                                append(StringUtils.leftPad(detalle.getSnaAsString(), 3, "0")).
                                append(StringUtils.leftPad(detalle.getNumFinal(), 4, "0"));
                        sb.append(COMA);

                        //OCUPACION
                        b.append((Integer.parseInt(detalle.getNumFinal())
                                - Integer.parseInt(detalle.getNumInicio())) + 1);
                        b.append(COMA);

                        sb.append((Integer.parseInt(detalle.getNumFinal())
                                - Integer.parseInt(detalle.getNumInicio())) + 1);
                        sb.append(COMA);

                        // BCD
                        b.append(detalle.getIdaAsString() != null && !detalle.getIdaAsString().equals("") ? 
                		detalle.getIdaAsString() : detalle.getBcdAsString());
                        b.append(COMA);

                        sb.append(detalle.getIdaAsString() != null && !detalle.getIdaAsString().equals("") ?
                		detalle.getIdaAsString() : detalle.getBcdAsString());
                        sb.append(COMA);

                        // IDA
                        b.append(
                                detalle.getCodTipoPst().equals(TipoProveedor.COMERCIALIZADORA) ? detalle
                                        .getIdaAsString() : detalle.getBcdAsString()).append(COMA);

                        sb.append(
                                detalle.getCodTipoPst().equals(TipoProveedor.COMERCIALIZADORA) ? detalle
                                        .getIdaAsString() : detalle.getBcdAsString()).append(COMA);

                        // FECHA_ASIGNACION
                        b.append(FechasUtils.fechaToString(detalle.getFechaSolAsig()));
                        b.append(COMA);

                        sb.append(FechasUtils.fechaToString(detalle.getFechaSolAsig()));
                        sb.append(COMA);

                        // OFICIO_ASIGNACION
                        b.append(detalle.getOficioSolAsig() != null ? detalle.getOficioSolAsig() : VACIO);
                        sb.append(detalle.getOficioSolAsig() != null ? detalle.getOficioSolAsig() : VACIO);

                        b.append(NL);
                        sb.append(NL);

                        fileOutput.write(b.toString().getBytes());
                        fileOutput2.write(sb.toString().getBytes());
                    }
                }
            }
            fileOutput.close();
            fileOutput2.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_NNG_IFT, null);
            crearPlan(tmp3, tmp4, TipoPlan.TIPO_PLAN_NNG_IFT_NUEVO, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG IFT");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG IFT");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG IFT");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
            FileUtils.deleteQuietly(tmp3);
            FileUtils.deleteQuietly(tmp4);
        }
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarIdentificadoresOperadores() {

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());

            fileOutput.write(CABECERA_IDENTIFICADOR_OPERADOR.getBytes());

            List<DetalleProveedor> lista = proveedoresService.findAllProveedoresActivosD();

            for (DetalleProveedor detalle : lista) {

                StringBuilder b = new StringBuilder();

                // PST
                b.append(addQuotes(detalle.getNombre()));
                b.append(COMA);

                // IDO
                b.append(detalle.getIdo() != null ? detalle.getIdoAsString() : VACIO);
                b.append(COMA);

                // IDA
                b.append(detalle.getIda() != null ? detalle.getIdaAsString() : VACIO);
                b.append(COMA);

                // BCD
                b.append(detalle.getBcd() != null ? detalle.getBcdAsString() : VACIO);
                b.append(COMA);

                b.append(NL);

                fileOutput.write(b.toString().getBytes());
            }

            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_ID_OPERADORES, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando generando identificadores PST");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando generando identificadores PST");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando generando identificadores PST");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanIFT() {

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_IFT.getBytes());

            int ng = seriesServiceNg.getNumRangosAsignados().intValue();
            int nng = seriesServiceNng.findAllRangosAsignadosCount();

            // Cargamos los rangos de num. geografica
            if (ng > 0) {
                int bloques = (ng / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<RangoSerie> lista = seriesServiceNg.getRangosAsignados(i * BLOQUE, BLOQUE);
                    for (RangoSerie rango : lista) {

                        StringBuilder b = new StringBuilder();

                        // CLAVE_CENSAL
                        b.append(addQuotes(rango.getPoblacion().getInegi()));
                        b.append(COMA);

                        // POBLACION
                        b.append(addQuotes(rango.getPoblacion().getNombre()));
                        b.append(COMA);

                        // MUNICIPIO
                        b.append(addQuotes(rango.getPoblacion().getMunicipio().getNombre()));
                        b.append(COMA);

                        // ESTADO
                        b.append(addQuotes(rango.getPoblacion().getMunicipio().getEstado().getNombre()));
                        b.append(COMA);

                        // ABN
                        b.append(rango.getSerie().getNir().getAbn().getCodigoAbn());
                        b.append(COMA);

                        // NIR
                        b.append(rango.getSerie().getNir().getCodigo());
                        b.append(COMA);

                        // SERIE
                        b.append(rango.getSerie().getSnaAsString());
                        b.append(COMA);

                        // NUMERACION INICIAL
                        b.append(rango.getNumInicio());
                        b.append(COMA);

                        // NUMERACION FINAL
                        b.append(rango.getNumFinal());
                        b.append(COMA);

                        // OCUPACION
                        b.append(rango.getNumFinalAsInt() - rango.getNumInicioAsInt() + 1);
                        b.append(COMA);

                        // TIPO DE RED
                        b.append(rango.getTipoRed().getDescripcion());
                        b.append(COMA);

                        // MODALIDAD
                        b.append(rango.getTipoRed().getCdg().equals(TipoRed.MOVIL) ? rango.getTipoModalidad()
                                .getDescripcion() : MODALIDAD_FIJO);
                        b.append(COMA);
                        
                        // ESTATUS
                        b.append(rango.getEstadoRango().getDescripcion());
                        b.append(COMA);

                        // RAZON SOCIAL
                        b.append(addQuotes(rango.getAsignatario().getNombre()));
                        b.append(COMA);

                        // NOMBRE CORTO
                        b.append(rango.getAsignatario().getNombreCorto());
                        b.append(COMA);

                        // OFICIO_DE_ASIGNACION
                        b.append(rango.getOficioAsignacion() != null ? rango.getOficioAsignacion() : VACIO);
                        b.append(COMA);

                        // FECHA_DE_ASIGNACION
                        b.append(FechasUtils.fechaToString(rango.getFechaAsignacion()));
                        b.append(COMA);

                        // IDO
                        b.append(rango.getIdoPnnAsString());
                        b.append(COMA);

                        // IDA
                        b.append(rango.getIdaPnnAsString());
                        b.append(COMA);

                        // ABC
                        //b.append(CERO);

                        b.append(NL);

                        fileOutput.write(b.toString().getBytes());
                    }
                }
            }

            // Cargamos los rangos de num. no geografica
            if (nng > 0) {
                int bloques = (nng / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<RangoSerieNng> lista = seriesServiceNng.findAllRangosAsignados(i * BLOQUE, BLOQUE);
                    for (RangoSerieNng rango : lista) {

                        StringBuilder b = new StringBuilder();

                        // CLAVE_CENSAL
                        b.append(CERO);
                        b.append(COMA);

                        // POBLACION
                        b.append(CERO);
                        b.append(COMA);

                        // MUNICIPIO
                        b.append(CERO);
                        b.append(COMA);

                        // ESTADO
                        b.append(CERO);
                        b.append(COMA);

                        // ABN
                        b.append(CERO);
                        b.append(COMA);

                        // CLAVE_NO_GEOGRAFICO
                        b.append(rango.getClaveServicio().getCodigo().toString());
                        b.append(COMA);

                        // SERIE
                        b.append(rango.getSerie().getSnaAsString());
                        b.append(COMA);

                        // NUMERACION INICIAL
                        b.append(rango.getNumInicio());
                        b.append(COMA);

                        // NUMERACION FINAL
                        b.append(rango.getNumFinal());
                        b.append(COMA);

                        // OCUPACION
                        b.append(rango.getNumFinalAsInt() - rango.getNumInicioAsInt() + 1);
                        b.append(COMA);

                        // TIPO DE RED
                        b.append(CERO);
                        b.append(COMA);

                        // MODALIDAD
                        b.append(CERO);
                        b.append(COMA);

                        // ESTATUS
                        b.append(rango.getEstatus().getDescripcion());
                        b.append(COMA);

                        // RAZON SOCIAL
                        b.append(addQuotes(rango.getAsignatario().getNombre()));
                        b.append(COMA);

                        // NOMBRE CORTO
                        b.append(rango.getAsignatario().getNombreCorto());
                        b.append(COMA);

                        // OFICIO_DE_ASIGNACION
                        b.append(rango.getOficioAsignacion() != null ? rango.getOficioAsignacion() : VACIO);
                        b.append(COMA);

                        // FECHA_DE_ASIGNACION
                        b.append(FechasUtils.fechaToString(rango.getFechaAsignacion()));
                        b.append(COMA);

                        // IDO
                        b.append(rango.getConcesionario().getIdoAsString());
                        b.append(COMA);

                        // IDA
                        b.append(
                                rango.getAsignatario().getTipoProveedor().getCdg()
                                        .equals(TipoProveedor.COMERCIALIZADORA) ? rango.getAsignatario()
                                        .getIdaAsString() : rango.getBcdAsString()).append(COMA);

                        // ABC
                        //b.append();

                        b.append(NL);

                        fileOutput.write(b.toString().getBytes());
                    }
                }
            }
            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_IFT, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan IFT");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan IFT");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan IFT");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanIFTD() {

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_IFT.getBytes());

            int ng = seriesServiceNg.getNumRangosAsignados().intValue();
            int nng = seriesServiceNng.findAllRangosAsignadosCount();

            // Cargamos los rangos de num. geografica
            if (ng > 0) {
                int bloques = (ng / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleRangoAsignadoNg> lista = seriesServiceNg.getRangosAsignadosD(i * BLOQUE, BLOQUE);

                    for (DetalleRangoAsignadoNg detalle : lista) {

                        StringBuilder b = new StringBuilder();

                        // CLAVE_CENSAL
                        b.append(addQuotes(detalle.getInegi()));
                        b.append(COMA);

                        // POBLACION
                        b.append(addQuotes(detalle.getNombrePoblacion()));
                        b.append(COMA);

                        // MUNICIPIO
                        b.append(addQuotes(detalle.getNombreMunicipio()));
                        b.append(COMA);

                        // ESTADO
                        b.append(addQuotes(detalle.getNombreEstado()));
                        b.append(COMA);

                        // ABN
                        b.append(detalle.getIdAbn());
                        b.append(COMA);

                        // NIR
                        b.append(detalle.getCodigoNir());
                        b.append(COMA);

                        // SERIE
                        b.append(detalle.getSnaAsString());
                        b.append(COMA);

                        // NUMERACION INICIAL
                        b.append(detalle.getNumInicio());
                        b.append(COMA);

                        // NUMERACION FINAL
                        b.append(detalle.getNumFinal());
                        b.append(COMA);

                        // OCUPACION
                        b.append((Integer.parseInt(detalle.getNumFinal())
                                - Integer.parseInt(detalle.getNumInicio())) + 1);
                        b.append(COMA);

                        // TIPO DE RED
                        b.append(detalle.getDescTipoRed());
                        b.append(COMA);

                        // MODALIDAD
                        b.append(detalle.getIdTipoModalidad() != null ? detalle.getIdTipoModalidad() : MODALIDAD_FIJO);
                        b.append(COMA);

                        // ESTATUS
                        b.append(detalle.getCodEstatusRango());
                        b.append(COMA);

                        // RAZON SOCIAL
                        b.append(addQuotes(detalle.getNombrePst()));
                        b.append(COMA);

                        // NOMBRE CORTO
                        b.append(detalle.getNombreCortoPst());
                        b.append(COMA);

                        // OFICIO_DE_ASIGNACION
                        b.append(detalle.getOficioSolAsig() != null ? detalle.getOficioSolAsig() : VACIO);
                        b.append(COMA);

                        // FECHA_DE_ASIGNACION
                        b.append(FechasUtils.fechaToString(detalle.getFechaSolAsig()));
                        b.append(COMA);

                        // IDO
                        b.append(detalle.getIdoPnnAsString());
                        b.append(COMA);

                        // IDA
                        b.append(detalle.getIdaPnnAsString());
                        b.append(COMA);

                        // ABC
                        //b.append(CERO);

                        b.append(NL);

                        fileOutput.write(b.toString().getBytes());
                    }
                }
            }

            // Cargamos los rangos de num. no geografica
            if (nng > 0) {
                int bloques = (nng / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleRangoAsignadoNng> lista = seriesServiceNng.findAllRangosAsignadosD(i * BLOQUE, BLOQUE);
                    for (DetalleRangoAsignadoNng detalle : lista) {

                        StringBuilder b = new StringBuilder();

                        // CLAVE_CENSAL
                        b.append(CERO);
                        b.append(COMA);

                        // POBLACION
                        b.append(CERO);
                        b.append(COMA);

                        // MUNICIPIO
                        b.append(CERO);
                        b.append(COMA);

                        // ESTADO
                        b.append(CERO);
                        b.append(COMA);

                        // ABN
                        b.append(CERO);
                        b.append(COMA);

                        // CLAVE_NO_GEOGRAFICO
                        b.append(detalle.getIdClaveServicio().toString());
                        b.append(COMA);

                        // SERIE
                        b.append(detalle.getSnaAsString());
                        b.append(COMA);

                        // NUMERACION INICIAL
                        b.append(detalle.getNumInicio());
                        b.append(COMA);

                        // NUMERACION FINAL
                        b.append(detalle.getNumFinal());
                        b.append(COMA);

                        // OCUPACION
                        b.append((Integer.parseInt(detalle.getNumFinal())
                                - Integer.parseInt(detalle.getNumInicio())) + 1);
                        b.append(COMA);

                        // TIPO DE RED
                        b.append(CERO);
                        b.append(COMA);

                        // MODALIDAD
                        b.append(CERO);
                        b.append(COMA);

                        // ESTATUS
                        b.append(detalle.getCodEstatusRango());
                        b.append(COMA);

                        // RAZON SOCIAL
                        b.append(addQuotes(detalle.getNombrePst()));
                        b.append(COMA);

                        // NOMBRE CORTO
                        b.append(detalle.getNombreCortoPst());
                        b.append(COMA);

                        // OFICIO_DE_ASIGNACION
                        b.append(detalle.getOficioSolAsig() != null ? detalle.getOficioSolAsig() : VACIO);
                        b.append(COMA);

                        // FECHA_DE_ASIGNACION
                        b.append(FechasUtils.fechaToString(detalle.getFechaSolAsig()));
                        b.append(COMA);

                        // IDO
                        b.append(detalle.getBcdAsString());
                        b.append(COMA);

                        // IDA
                        b.append(
                                detalle.getCodTipoPst().equals(TipoProveedor.COMERCIALIZADORA) ? detalle
                                        .getIdaAsString()
                                        : detalle.getBcdAsString()).append(COMA);

                        // ABC
                        //b.append(detalle.getBcd() != null ? detalle.getBcdAsString() : CERO);

                        b.append(NL);

                        fileOutput.write(b.toString().getBytes());
                    }
                }
            }
            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_IFT, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan IFT");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan IFT");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan IFT");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generarPlanNngEspecificaIFT() {

        File tmp = null;
        File tmp2 = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            tmp2 = FicheroTemporal.getTmpFileName();

            FileOutputStream fileOutput = new FileOutputStream(tmp.getCanonicalPath());

            fileOutput.write(CABECERA_PLAN_NNG_ESPECIFICO_IFT.getBytes());

            int n = seriesServiceNng.findAllRangosAsignadosEspecificoCount();

            if (n > 0) {
                int bloques = (n / BLOQUE) + 1;

                for (int i = 0; i < bloques; i++) {

                    List<DetalleRangoAsignadoNng> lista = seriesServiceNng.findAllRangosAsignadosEspecificoD(i * BLOQUE,
                            BLOQUE);

                    for (DetalleRangoAsignadoNng detalle : lista) {

                        for (int j = Integer.parseInt(detalle.getNumInicio()); j <= Integer.parseInt(detalle
                                .getNumFinal()); j++) {
                            StringBuilder b = new StringBuilder();

                            // CLAVE_NO_GEOGRAFICA
                            b.append(detalle.getIdClaveServicio().toString());
                            b.append(COMA);

                            // SERIE
                            b.append(detalle.getSnaAsString());
                            b.append(COMA);

                            // NUMERO_USUARIO
                            b.append(String.format("%04d", j));
                            b.append(COMA);

                            // PST
                            b.append(addQuotes(detalle.getNombrePst()));
                            b.append(COMA);

                            // CLIENTE
                            b.append(addQuotes(detalle.getCliente()));
                            b.append(COMA);

                            // OFICIO_ASIGNACION
                            b.append(detalle.getOficioSolAsig() != null ? detalle.getOficioSolAsig() : VACIO);
                            b.append(COMA);

                            // FECHA_ASIGNACION.
                            b.append(FechasUtils.fechaToString(detalle.getFechaSolAsig()));
                            b.append(COMA);

                            // BCD
                            b.append(detalle.getBcd() != null ? detalle.getBcdAsString() : VACIO);
                            b.append(COMA);

                            // IDA
                            b.append(detalle.getIda() != null ? detalle.getIdaAsString() : detalle.getBcdAsString());
                            b.append(COMA);

                            b.append(NL);

                            fileOutput.write(b.toString().getBytes());
                        }
                    }
                }
            }
            fileOutput.close();

            crearPlan(tmp, tmp2, TipoPlan.TIPO_PLAN_NNG_ESPECIFICA_IFT, null);

        } catch (ArchiveException e) {
            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica PST");
            }
            LOGGER.error("error", e);
        } catch (IOException e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica PST");
            }
        } catch (Exception e) {
            LOGGER.error("error", e);

            if (bitacora != null) {
                bitacora.add("Error generando plan numeracion NNG especifica PST");
            }
        } finally {
            FileUtils.deleteQuietly(tmp);
            FileUtils.deleteQuietly(tmp2);
        }
    }
}
