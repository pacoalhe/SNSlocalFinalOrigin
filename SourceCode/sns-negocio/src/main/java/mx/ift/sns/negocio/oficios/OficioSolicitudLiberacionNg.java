package mx.ift.sns.negocio.oficios;

import java.util.Date;

import mx.ift.sns.modelo.ng.LiberacionSolicitadaNg;
import mx.ift.sns.modelo.ng.SolicitudLiberacionNg;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Proporciona los métodos para la generación del oficio de Solicitud de Liberación Geográfica. */
public class OficioSolicitudLiberacionNg extends GeneradorOficio {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioSolicitudLiberacionNg.class);

    /** Información de la solicitud. */
    private SolicitudLiberacionNg solicitud;

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
    public OficioSolicitudLiberacionNg(ParametrosOficio pParametros) {
        solicitud = (SolicitudLiberacionNg) pParametros.getSolicitud();
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
        for (LiberacionSolicitadaNg libSol : solicitud.getLiberacionesSolicitadas()) {
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
        String[] headerText = {"Cve. CENSAL", "POBLACIÓN", "EDO", "NIR", "SNA", "Desde el número", "Hasta el número",
                "TIPO", "IDO", "IDA"};

        // Tamaño de tabla
        int tableSize = 1; // Cabecera
        for (LiberacionSolicitadaNg libSol : solicitud.getLiberacionesSolicitadas()) {
            if (!libSol.getEstado().getCodigo().equals(EstadoLiberacionSolicitada.CANCELADO)) {
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
            for (LiberacionSolicitadaNg libSol : solicitud.getLiberacionesSolicitadas()) {
                if (!libSol.getEstado().getCodigo().equals(EstadoLiberacionSolicitada.CANCELADO)) {
                    // Clave Censal
                    generaCelda(table.getRow(fila).getCell(0), libSol.getPoblacion().getInegi(), 10, false);

                    // Población
                    generaCelda(table.getRow(fila).getCell(1), libSol.getPoblacion().getNombre(), 10, false);

                    // Abreviatura de Estado
                    generaCelda(table.getRow(fila).getCell(2),
                            libSol.getPoblacion().getMunicipio().getEstado().getAbreviatura(), 10, false);

                    // Código NIR
                    generaCelda(table.getRow(fila).getCell(3), String.valueOf(libSol.getCdgNir()), 10, false);

                    // Identificador de Serie
                    generaCelda(table.getRow(fila).getCell(4), libSol.getSna().toString(), 10, false);

                    // Inicio de Serie
                    generaCelda(table.getRow(fila).getCell(5), libSol.getNumInicio(), 10, false);

                    // Final de Serie
                    generaCelda(table.getRow(fila).getCell(6), libSol.getNumFinal(), 10, false);

                    // Tipo de Red
                    String tipoRed = getTipoRed(libSol.getTipoRed(), libSol.getTipoModalidad());
                    generaCelda(table.getRow(fila).getCell(7), tipoRed, 10, false);

                    // IDO_PNN
                    if (libSol.getIdoPnn() != null) {
                        generaCelda(table.getRow(fila).getCell(8), libSol.getIdoPnn().toString(), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(8), "", 10, false);
                    }

                    // IDA_PNN
                    if (libSol.getIdaPnn() != null) {
                        generaCelda(table.getRow(fila).getCell(9), libSol.getIdaPnn().toString(), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(9), "", 10, false);
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
