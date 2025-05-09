package mx.ift.sns.negocio.oficios;

import java.util.Date;

import mx.ift.sns.modelo.ng.NumeracionAsignada;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Proporciona los métodos para la generación del oficio de Solicitud de Asignación. */
public class OficioSolicitudAsignacionNg extends GeneradorOficio {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioSolicitudAsignacionNg.class);

    /** Información de la solicitud. */
    private SolicitudAsignacion solicitud;

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
    public OficioSolicitudAsignacionNg(ParametrosOficio pParametros) {
        solicitud = (SolicitudAsignacion) pParametros.getSolicitud();
        destinatario = pParametros.getTipoDestinatario();
        numeroOficio = pParametros.getNumOficio();
        fechaOficio = pParametros.getFechaOficio();
        textoAdicional = pParametros.getTextoAdicional();
    }

    @Override
    public byte[] getDocumentoOficio(byte[] pPlantilla) throws Exception {

        // Carga de Variables comunes.
        cargaVariablesComunes(solicitud, numeroOficio, fechaOficio);

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
            tablas.put(GeneradorOficio.EXCEL_ASIGNACION, getTablaAsignaciones());
            break;
        default:
            break;
        }

        // Documento de Oficio
        byte[] documentoOficio = actualizarVariablesOficio(pPlantilla, variables, tablas, header);
        return documentoOficio;
    }

    /**
     * Carga las variables a sustituir en los oficios de PST Solicititante y Resto PSTs.
     * @throws Exception error
     */
    private void cargaVariablesOficio() throws Exception {
        variables.put(ATENCION, solicitud.getProveedorSolicitante().getNombre());
        variables.put(REFERENCIA_SOLICITUD, solicitud.getReferencia());
        variables.put(FECHA_INICIO_UTILIZACION,
                FechasUtils.fechaToString(solicitud.getFechaIniUtilizacion(), "dd 'de' MMMM 'de' yyyy"));

        if (solicitud.getRepresentanteLegal() != null) {
            variables.put(REPRESENTANTE_LEGAL, solicitud.getRepresentanteLegal().getNombre());
        } else {
            variables.put(REPRESENTANTE_LEGAL, GeneradorOficio.VACIO);
        }

        if (solicitud.getRepresentanteSuplente() != null) {
            variables.put(REPRESENTANTE_LEGAL_SUPLENTE, solicitud.getRepresentanteSuplente()
                    .getNombre());
        } else {
            variables.put(REPRESENTANTE_LEGAL_SUPLENTE, GeneradorOficio.VACIO);
        }

        if (!StringUtils.isEmpty(textoAdicional)) {
            variables.put(TEXTO_ADICIONAL, textoAdicional);
        } else {
            variables.put(TEXTO_ADICIONAL, GeneradorOficio.VACIO);
        }

        if (solicitud.getProveedorSolicitante().getTipoRed().getCdg().equals(TipoRed.FIJA)) {
            variables.put(LEYENDA, "Telefonía Fija");
        } else if (solicitud.getProveedorSolicitante().getTipoRed().getCdg().equals(TipoRed.MOVIL)) {
            variables.put(LEYENDA, "Telefonía Móvil");
        } else {
            variables.put(LEYENDA, "Telefonía Fija/Móvil");
        }

        // Cantidad de numeración asignada
        int cantidadNumAsignada = 0;

        for (NumeracionAsignada numAsig : solicitud.getNumeracionAsignadas()) {
            cantidadNumAsignada += numAsig.getFinRangoAsInt() - numAsig.getInicioRangoAsInt() + 1;
        }

        variables.put(ASIGNADOS, String.valueOf(cantidadNumAsignada));
    }

    /**
     * Crea la tabla de Asignaciones en formato Word (.docx). usando la librería Apache POI:
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaAsignaciones() {
        // Cabecera
        String[] headerText = {"Cve. CENSAL", "POBLACIÓN", "EDO", "NIR", "SNA", "Desde el número", "Hasta el número",
                "TIPO", "IDO", "IDA"};

        // Tablas
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(solicitud.getNumeracionAsignadas().size() + 2, headerText.length);

        try {
            // Primera cabecera de la tabla
            XWPFTableCell firstCell = table.getRow(0).getCell(0);
            XWPFParagraph cellParagraph = firstCell.getParagraphs().get(0);
            cellParagraph.setAlignment(ParagraphAlignment.CENTER);
            generaCelda(firstCell, "NUMERACIÓN ASIGNADA", 12, true);

            // Segunda cabecera de la tabla
            for (int i = 0; i < headerText.length; i++) {

                generaCelda(table.getRow(1).getCell(i), headerText[i], 10, true);

            }

            // Contenido de la tabla
            int fila = 2; // Empezamos despúes de la cabecera
            for (NumeracionAsignada numAsig : solicitud.getNumeracionAsignadas()) {
                // INEGI
                generaCelda(table.getRow(fila).getCell(0),
                        numAsig.getNumeracionSolicitada().getPoblacion().getInegi(), 10, false);

                // Nombre de Población
                generaCelda(table.getRow(fila).getCell(1),
                        numAsig.getNumeracionSolicitada().getPoblacion().getNombre().toUpperCase(), 10, false);

                // Abreviatura Estado (OT)
                generaCelda(table.getRow(fila).getCell(2),
                        numAsig.getNumeracionSolicitada().getPoblacion().getMunicipio().getEstado().getAbreviatura(),
                        10, false);

                // Código NIR
                generaCelda(table.getRow(fila).getCell(3), String.valueOf(numAsig.getCdgNirInicial()), 10,
                        false);

                // SNA
                //generaCelda(table.getRow(fila).getCell(4), numAsig.getIdSerieInicialAsString(), 10, false);
                generaCelda(table.getRow(fila).getCell(4), numAsig.getSnaAsString(), 10, false);

                // Inicio de Rango
                generaCelda(table.getRow(fila).getCell(5), numAsig.getInicioRango(), 10, false);

                // Final de Rango
                generaCelda(table.getRow(fila).getCell(6), numAsig.getFinRango(), 10, false);

                // Tipo de Red
                generaCelda(
                        table.getRow(fila).getCell(7),
                        getTipoRed(numAsig.getNumeracionSolicitada().getTipoRed(), numAsig.getNumeracionSolicitada()
                                .getTipoModalidad()), 10, false);

                // IDO
                if (numAsig.getNumeracionSolicitada().getIdoPnn() != null) {
                    generaCelda(table.getRow(fila).getCell(8),
                            String.valueOf(numAsig.getNumeracionSolicitada().getIdoPnn()), 10, false);
                } else {
                    generaCelda(table.getRow(fila).getCell(8), "", 10, false);
                }

                // IDA
                if (numAsig.getNumeracionSolicitada().getIdaPnn() != null) {
                    generaCelda(table.getRow(fila).getCell(9),
                            String.valueOf(numAsig.getNumeracionSolicitada().getIdaPnn()), 10, false);
                } else {
                    generaCelda(table.getRow(fila).getCell(9), "", 10, false);
                }

                fila++;
            }

            // Aplicamos Merge a las filas de cabecera
            fusionarCeldas(table, 0, 0, headerText.length);

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
        }

        return table;
    }

}
