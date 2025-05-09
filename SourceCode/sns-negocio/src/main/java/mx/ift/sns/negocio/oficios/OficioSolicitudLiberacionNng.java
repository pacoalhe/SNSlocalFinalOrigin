package mx.ift.sns.negocio.oficios;

import java.util.Date;

import mx.ift.sns.modelo.nng.LiberacionSolicitadaNng;
import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Proporciona los métodos para la generación del oficio de Solicitud de Liberación No Geográfica. */
public class OficioSolicitudLiberacionNng extends GeneradorOficio {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioSolicitudLiberacionNng.class);

    /** Información de la solicitud. */
    private SolicitudLiberacionNng solicitud;

    /** Información del destinatario. */
    private TipoDestinatario destinatario;

    /** Número de oficio asignado. */
    private String numeroOficio;

    /** Fecha de Oficio seleccionada. */
    private Date fechaOficio;

    /** Información Adicional. */
    private String textoAdicional;

    /**
     * Constructor específico con la información requerida.
     * @param pParametros Información del oficio.
     */
    public OficioSolicitudLiberacionNng(ParametrosOficio pParametros) {
        solicitud = (SolicitudLiberacionNng) pParametros.getSolicitud();
        destinatario = pParametros.getTipoDestinatario();
        numeroOficio = pParametros.getNumOficio();
        fechaOficio = pParametros.getFechaOficio();
        textoAdicional = pParametros.getTextoAdicional();
    }

    @Override
    public byte[] getDocumentoOficio(byte[] pPlantilla) throws Exception {

        // Carga de Variables comunes.
        cargaVariablesComunes((Solicitud) solicitud, numeroOficio, fechaOficio);

        // Carga de Variables específicas de cada tipo de documento.
        switch (destinatario.getCdg()) {
        case TipoDestinatario.CEDULA_NOTIFICACION:
            super.cargaVariablesCedula(fechaOficio);
            break;
        case TipoDestinatario.ACTA_CIRCUNSTANCIADA:
            super.cargaVariablesActa(solicitud, fechaOficio);
            break;
        case TipoDestinatario.RESTO_PST: // Mismas que PST Solicitante
        case TipoDestinatario.PST_SOLICITANTE:
            this.cargaVariablesOficio();
            tablas.put(GeneradorOficio.EXCEL_LIBERACIONES, getTablaLiberaciones());
            break;
        default:
            break;
        }

        // Documento de Oficio
        byte[] documentoOficio = actualizarVariablesOficio(pPlantilla.clone(), variables, tablas, header);
        return documentoOficio;
    }

    /**
     * Carga las variables a sustituir en los oficios de PST Solicititante y Resto PSTs.
     */
    private void cargaVariablesOficio() {
        variables.put(REFERENCIA_SOLICITUD, solicitud.getReferencia());

        if (solicitud.getRepresentanteLegal() != null) {
            variables.put(REPRESENTANTE_LEGAL, solicitud.getRepresentanteLegal().getNombre());
        } else {
            variables.put(REPRESENTANTE_LEGAL, VACIO);
        }

        if (solicitud.getRepresentanteSuplente() != null) {
            variables.put(REPRESENTANTE_LEGAL_SUPLENTE, solicitud.getRepresentanteSuplente().getNombre());
        } else {
            variables.put(REPRESENTANTE_LEGAL_SUPLENTE, VACIO);
        }

        // Fecha de Implementación. Cogemos la fecha más alta de todas las liberaciones.
        Date fImplementacion = solicitud.getLiberacionesSolicitadas().get(0).getFechaLiberacion();
        for (LiberacionSolicitadaNng libSol : solicitud.getLiberacionesSolicitadas()) {
            if (libSol.getFechaLiberacion().after(fImplementacion)) {
                fImplementacion = libSol.getFechaLiberacion();
            }
        }
        variables.put(FECHA_IMPLEMENTACION, FechasUtils.fechaToString(fImplementacion, "dd 'de' MMMM 'de' yyyy"));

        if (!StringUtils.isEmpty(textoAdicional)) {
            variables.put(TEXTO_ADICIONAL, textoAdicional);
        } else {
            variables.put(TEXTO_ADICIONAL, GeneradorOficio.VACIO);
        }
    }

    /**
     * Crea la tabla de Información de Liberaciones en formato Word (.docx). usando la librería Apache POI:
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaLiberaciones() {
        // Cabecera
        String[] headerText = {"CLAVE DE SERVICIO", "SERIE", "NUM. INICIAL", "NUM. FINAL", "IDO/IDD (Antes BCD)", "IDA"};

        // Tamaño de tabla
        int tableSize = 1; // Cabecera
        for (LiberacionSolicitadaNng libSol : solicitud.getLiberacionesSolicitadas()) {
            if (!libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.CANCELADO)) {
                tableSize++;
            }
        }

        // Tablas
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(tableSize, headerText.length);

        try {
            // Cabecera
            for (int i = 0; i < headerText.length; i++) {
                generaCelda(table.getRow(0).getCell(i), headerText[i], 10, true);
            }

            // Contenido de la tabla
            int fila = 1; // Empezamos despúes de la cabecera
            for (LiberacionSolicitadaNng libSol : solicitud.getLiberacionesSolicitadas()) {
                if (!libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.CANCELADO)) {
                    // Clave de Servicio
                    generaCelda(table.getRow(fila).getCell(0), libSol.getIdClaveServicio().toString(), 10, false);

                    // Identificador de Serie
                    generaCelda(table.getRow(fila).getCell(1), libSol.getSna().toString(), 10, false);

                    // Inicio de Serie
                    generaCelda(table.getRow(fila).getCell(2), libSol.getNumInicio(), 10, false);

                    // Final de Serie
                    generaCelda(table.getRow(fila).getCell(3), libSol.getNumFinal(), 10, false);

                    // ABC
                    if (libSol.getBcd() != null) {
                        generaCelda(table.getRow(fila).getCell(4), String.valueOf(libSol.getBcd()), 10, false);
                    } else if (libSol.getProveedorCesionario().getBcd() != null){
                        generaCelda(table.getRow(fila).getCell(4), String.valueOf(libSol.getProveedorCesionario().getBcd()), 10, false);
                    }else if(libSol.getProveedorConcesionario().getBcd() != null){
                        generaCelda(table.getRow(fila).getCell(4), String.valueOf(libSol.getProveedorConcesionario().getBcd()), 10, false);
                    }else{
                        generaCelda(table.getRow(fila).getCell(4), "", 10, false);
                    }

                    // IDA
                    if (libSol.getIda() != null) {
                        generaCelda(table.getRow(fila).getCell(5), String.valueOf(libSol.getIda()), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(5), String.valueOf(libSol.getProveedorCesionario().getBcd()), 10, false);
                    }

                    fila++;
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e.getMessage());
        }

        return table;
    }
}
