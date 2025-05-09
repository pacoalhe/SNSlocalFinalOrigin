package mx.ift.sns.negocio.oficios;

import java.util.Date;

import mx.ift.sns.modelo.cps.EstatusAsignacionCps;
import mx.ift.sns.modelo.cpsn.CPSNUtils;
import mx.ift.sns.modelo.cpsn.NumeracionAsignadaCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
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

/** Proporciona los métodos para la generación del oficio de Solicitud de Asignación de CPSN. */
public class OficioSolicitudAsignacionCpsn extends GeneradorOficio {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioSolicitudAsignacionCpsn.class);

    /** Información de la solicitud. */
    private SolicitudAsignacionCpsn solicitud;

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
    public OficioSolicitudAsignacionCpsn(ParametrosOficio pParametros) {
        solicitud = (SolicitudAsignacionCpsn) pParametros.getSolicitud();
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
            tablas.put(GeneradorOficio.EXCEL_ASIGANCION_CPSN, getTablaAsignacionesCPSN());
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
                FechasUtils.fechaToString(solicitud.getFechaIniUtilizacion(), "dd 'de' MMMM 'de' yyyy"));

        if (solicitud.getRepresentanteLegal() != null) {
            variables.put(REPRESENTANTE_LEGAL, solicitud.getRepresentanteLegal().getNombre());
        } else {
            variables.put(REPRESENTANTE_LEGAL, GeneradorOficio.VACIO);
        }

        if (solicitud.getRepresentanteSuplente() != null) {
            variables.put(REPRESENTANTE_LEGAL_SUPLENTE, solicitud.getRepresentanteSuplente().getNombre());
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
     * Crea la tabla de Información de Asginaciones de CPSN en formato Word (.docx). usando la librería Apache POI:
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaAsignacionesCPSN() {
        // Cabecera
        String[] headerText = {"ESTRUCTURA", "BINARIO DESDE", "BINARIO HASTA", "DEC. DESDE", "DEC. HASTA"};

        // Tamaño de Tabla
        int tableSize = 3; // Cabeceras
        for (NumeracionAsignadaCpsn numAsig : solicitud.getNumeracionAsignadas()) {
            if (!numAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.CANCELADO)) {
                tableSize++;
            }
        }

        // Tablas
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(tableSize, headerText.length);

        try {
            XWPFTableCell firstCell = table.getRow(0).getCell(0);
            XWPFParagraph cellParagraph = firstCell.getParagraphs().get(0);
            cellParagraph.setAlignment(ParagraphAlignment.CENTER);
            generaCelda(firstCell, solicitud.getProveedorSolicitante().getNombre().toUpperCase(), 12, true);

            XWPFTableCell secondCell = table.getRow(1).getCell(0);
            cellParagraph = secondCell.getParagraphs().get(0);
            cellParagraph.setAlignment(ParagraphAlignment.CENTER);
            String empresa = "CÓDIGOS DE PUNTO DE SEÑALIZACIÓN NACIONAL ASIGNADOS";
            generaCelda(secondCell, empresa, 12, true);

            // Cabecera
            for (int i = 0; i < headerText.length; i++) {
                generaCelda(table.getRow(2).getCell(i), headerText[i], 10, true);
            }

            // Contenido de la tabla
            int fila = 3; // Empezamos despúes de la cabecera
            for (NumeracionAsignadaCpsn numAsig : solicitud.getNumeracionAsignadas()) {
                if (!numAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.CANCELADO)) {
                    Integer minBloque = CPSNUtils.valorMinBloque(numAsig.getBinario());
                    Integer maxBloque = CPSNUtils.valorMaxBloque(numAsig.getBinario());

                    // Estructura
                    generaCelda(table.getRow(fila).getCell(0), numAsig.getTipoBloqueCpsn().getDescripcion(), 10, false);

                    // Binario Desde
                    generaCelda(table.getRow(fila).getCell(1), CPSNUtils.valorBinario(minBloque), 10, false);

                    // Binario Hasta
                    generaCelda(table.getRow(fila).getCell(2), CPSNUtils.valorBinario(maxBloque), 10, false);

                    // Decimal Desde
                    generaCelda(table.getRow(fila).getCell(3), String.valueOf(minBloque), 10, false);

                    // Decimal Hasta
                    generaCelda(table.getRow(fila).getCell(4), String.valueOf(maxBloque), 10, false);

                    fila++;
                }
            }

            // Aplicamos Merge a las filas de cabecera
            fusionarCeldas(table, 0, 0, headerText.length);
            fusionarCeldas(table, 1, 0, headerText.length);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e.getMessage());
        }

        return table;
    }

}
