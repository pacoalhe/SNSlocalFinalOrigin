package mx.ift.sns.negocio.oficios;

import java.util.Date;

import mx.ift.sns.modelo.ng.NumeracionRedistribuida;
import mx.ift.sns.modelo.ng.RedistribucionSolicitadaNg;
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.EstadoRedistribucionSolicitada;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Proporciona los métodos para la generación del oficio de Solicitud de Redistribución. */
public class OficioSolicitudRedistribucionNg extends GeneradorOficio {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioSolicitudRedistribucionNg.class);

    /** Información de la solicitud. */
    private SolicitudRedistribucionNg solicitud;

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
    public OficioSolicitudRedistribucionNg(ParametrosOficio pParametros) {
        solicitud = (SolicitudRedistribucionNg) pParametros.getSolicitud();
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
            tablas.put(GeneradorOficio.EXCEL_REDIST_DESPUES, this.getTablaRedistDespues());
            tablas.put(GeneradorOficio.EXCEL_REDIST_ANTES, this.getTablaRedistAntes());
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
        variables.put(FECHA_INICIO_UTILIZACION,
                FechasUtils.fechaToString(solicitud.getFechaAsignacion(), "dd 'de' MMMM 'de' yyyy"));

        if (solicitud.getRepresentanteLegal() != null) {
            variables.put(REPRESENTANTE_LEGAL, solicitud.getRepresentanteLegal().getNombre());
        } else {
            variables.put(REPRESENTANTE_LEGAL, VACIO);
        }

        if (solicitud.getRepresentanteSuplente() != null) {
            variables.put(REPRESENTANTE_LEGAL_SUPLENTE,
                    solicitud.getRepresentanteSuplente().getNombre());
        } else {
            variables.put(REPRESENTANTE_LEGAL_SUPLENTE, VACIO);
        }

        if (!StringUtils.isEmpty(textoAdicional)) {
            variables.put(TEXTO_ADICIONAL, textoAdicional);
        } else {
            variables.put(TEXTO_ADICIONAL, GeneradorOficio.VACIO);
        }
    }

    /**
     * Crea la tabla de Información de Redistribuciones en formato Word (.docx). usando la librería Apache POI:
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaRedistDespues() {
        // Cabecera
        String[] headerText = {"INEGI", "ABN", "POBLACIÓN", "CENTRAL", "NIR", "SNA", "NUM. INICIAL",
                "NUM. FINAL", "CANTIDAD", "IDO", "IDA", "MODALIDAD"};

        // Tamaño de tabla
        int tableSize = 2; // Cabeceras
        for (RedistribucionSolicitadaNg redSol : solicitud.getRedistribucionesSolicitadas()) {
            if (!redSol.getEstado().getCodigo().equals(EstadoRedistribucionSolicitada.CANCELADO)) {
                tableSize++;
            }
        }

        // Tablas
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(tableSize, headerText.length);

        try {
            // Primera cabecera de la tabla
            XWPFTableCell firstCell = table.getRow(0).getCell(0);
            XWPFParagraph cellParagraph = firstCell.getParagraphs().get(0);
            cellParagraph.setAlignment(ParagraphAlignment.CENTER);
            generaCelda(firstCell, "NUMERACIÓN REDISTRIBUIDA", 12, true);

            // Cabecera
            for (int i = 0; i < headerText.length; i++) {
                generaCelda(table.getRow(1).getCell(i), headerText[i], 10, true);
            }

            // Contenido de la tabla
            int fila = 2; // Empezamos despúes de la cabecera
            for (RedistribucionSolicitadaNg redSol : solicitud.getRedistribucionesSolicitadas()) {
                if (!redSol.getEstado().getCodigo().equals(EstadoRedistribucionSolicitada.CANCELADO)) {
                    // Clave Censal
                    generaCelda(table.getRow(fila).getCell(0), redSol.getPoblacion().getInegi(), 10, false);

                    // ABN
                    generaCelda(table.getRow(fila).getCell(1), redSol.getIdAbn().toString(), 10, false);

                    // Población
                    generaCelda(table.getRow(fila).getCell(2), redSol.getPoblacion().getNombre(), 10, false);

                    // Central
                    generaCelda(table.getRow(fila).getCell(3), redSol.getCentralOrigen().getNombre(), 10, false);

                    // Código NIR
                    generaCelda(table.getRow(fila).getCell(4), String.valueOf(redSol.getCdgNir()), 10, false);

                    // Identificador de Serie
                    generaCelda(table.getRow(fila).getCell(5), redSol.getSna().toString(), 10, false);

                    // Inicio de Serie
                    generaCelda(table.getRow(fila).getCell(6), redSol.getNumInicio(), 10, false);

                    // Final de Serie
                    generaCelda(table.getRow(fila).getCell(7), redSol.getNumFinal(), 10, false);

                    // Cantidad
                    int cantidad = (redSol.getNumFinalAsInt() - redSol.getNumInicioAsInt()) + 1;
                    generaCelda(table.getRow(fila).getCell(8), String.valueOf(cantidad), 10, false);

                    // IDO_PNN
                    if (redSol.getIdoPnn() != null) {
                        generaCelda(table.getRow(fila).getCell(9), String.valueOf(redSol.getIdoPnn()), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(9), "", 10, false);
                    }

                    // IDA_PNN
                    if (redSol.getIdaPnn() != null) {
                        generaCelda(table.getRow(fila).getCell(10), String.valueOf(redSol.getIdaPnn()), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(10), "", 10, false);
                    }

                    // Tipo de Red
                    String tipoRed = getTipoRed(redSol.getTipoRed(), redSol.getTipoModalidad());
                    generaCelda(table.getRow(fila).getCell(11), tipoRed, 10, false);

                    fila++;
                }
            }

            // Aplicamos Merge a las filas de cabecera
            fusionarCeldas(table, 0, 0, headerText.length);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
        }

        return table;
    }

    /**
     * Crea la tabla de Información de Redistribuciones en formato Word (.docx). usando la librería Apache POI:
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaRedistAntes() {
        // Cabecera
        String[] headerText = {"INEGI", "ABN", "POBLACIÓN", "CENTRAL", "NIR", "SNA", "NUM. INICIAL",
                "NUM. FINAL", "CANTIDAD", "IDO", "IDA", "MODALIDAD"};

        // Tamaño de tabla
        int tableSize = solicitud.getNumeracionesRedistribuidas().size() + 2;

        // Tablas
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(tableSize, headerText.length);

        try {
            // Primera cabecera de la tabla
            XWPFTableCell firstCell = table.getRow(0).getCell(0);
            XWPFParagraph cellParagraph = firstCell.getParagraphs().get(0);
            cellParagraph.setAlignment(ParagraphAlignment.CENTER);
            generaCelda(firstCell, "NUMERACIÓN A REDISTRIBUIR", 12, true);

            // Cabecera
            for (int i = 0; i < headerText.length; i++) {
                generaCelda(table.getRow(1).getCell(i), headerText[i], 10, true);
            }

            // Contenido de la tabla
            int fila = 2; // Empezamos despúes de la cabecera
            for (NumeracionRedistribuida numRed : solicitud.getNumeracionesRedistribuidas()) {
                // Clave Censal
                generaCelda(table.getRow(fila).getCell(0), numRed.getPoblacion().getInegi(), 10, false);

                // ABN
                generaCelda(table.getRow(fila).getCell(1),
                        String.valueOf(numRed.getRedistribucionSolicitada().getIdAbn()), 10, false);

                // Población
                generaCelda(table.getRow(fila).getCell(2), numRed.getPoblacion().getNombre(), 10, false);

                // Central
                generaCelda(table.getRow(fila).getCell(3), numRed.getCentralOrigen().getNombre(), 10, false);

                // Código NIR
                generaCelda(table.getRow(fila).getCell(4),
                        String.valueOf(numRed.getRedistribucionSolicitada().getCdgNir()), 10, false);

                // Identificador de Serie
                generaCelda(table.getRow(fila).getCell(5),
                        String.valueOf(numRed.getRedistribucionSolicitada().getSna()), 10, false);

                // Inicio de Serie
                generaCelda(table.getRow(fila).getCell(6), numRed.getInicioRango(), 10, false);

                // Final de Serie
                generaCelda(table.getRow(fila).getCell(7), numRed.getFinRango(), 10, false);

                // Cantidad
                int cantidad = (numRed.getNumFinalAsInt() - numRed.getNumInicioAsInt()) + 1;
                generaCelda(table.getRow(fila).getCell(8), String.valueOf(cantidad), 10, false);

                // IDO_PNN
                if (numRed.getRedistribucionSolicitada().getIdoPnn() != null) {
                    generaCelda(table.getRow(fila).getCell(9),
                            String.valueOf(numRed.getRedistribucionSolicitada().getIdoPnn()), 10, false);
                } else {
                    generaCelda(table.getRow(fila).getCell(9), "", 10, false);
                }

                // IDA_PNN
                if (numRed.getRedistribucionSolicitada().getIdaPnn() != null) {
                    generaCelda(table.getRow(fila).getCell(10),
                            String.valueOf(numRed.getRedistribucionSolicitada().getIdaPnn()), 10, false);
                } else {
                    generaCelda(table.getRow(fila).getCell(10), "", 10, false);
                }

                // Tipo de Red
                String tipoRed = getTipoRed(numRed.getTipoRed(), numRed.getTipoModalidad());
                generaCelda(table.getRow(fila).getCell(11), tipoRed, 10, false);

                fila++;
            }

            // Aplicamos Merge a las filas de cabecera
            fusionarCeldas(table, 0, 0, headerText.length);

        } catch (Exception e) {
            LOGGER.error("Error inesperado: ", e);
        }

        return table;
    }
}
