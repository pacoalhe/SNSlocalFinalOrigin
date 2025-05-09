package mx.ift.sns.negocio.oficios;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.nng.NumeracionAsignadaNng;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.TipoAsignacion;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Proporciona los métodos para la generación del oficio de Solicitud de Asignación no geográfica. */
public class OficioSolicitudAsignacionNng extends GeneradorOficio {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioSolicitudAsignacionNng.class);

    /** Información de la solicitud. */
    private SolicitudAsignacionNng solicitud;

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
    public OficioSolicitudAsignacionNng(ParametrosOficio pParametros) {
        solicitud = (SolicitudAsignacionNng) pParametros.getSolicitud();
        destinatario = pParametros.getTipoDestinatario();
        numeroOficio = pParametros.getNumOficio();
        fechaOficio = pParametros.getFechaOficio();
        textoAdicional = pParametros.getTextoAdicional();
    }

    @Override
    public byte[] getDocumentoOficio(byte[] pPlantilla) throws Exception {

        // Carga de Variables comunes.
        cargaVariablesComunes(solicitud, numeroOficio, fechaOficio);

        // Indicador si el documento es para el solicitante
        boolean solicitante = true;
        // Carga de Variables específicas de cada tipo de documento.
        switch (destinatario.getCdg()) {
        case TipoDestinatario.CEDULA_NOTIFICACION:
            super.cargaVariablesCedula(fechaOficio);
            break;
        case TipoDestinatario.ACTA_CIRCUNSTANCIADA:
            super.cargaVariablesActa(solicitud, fechaOficio);
            break;
        case TipoDestinatario.RESTO_PST:
            solicitante = false;
        case TipoDestinatario.PST_SOLICITANTE:
            this.cargaVariablesOficio();
            if (solicitud.getTipoAsignacion().getCdg().equals(TipoAsignacion.SERIES)) {
                tablas.put(GeneradorOficio.EXCEL_ASIGNACION, getTablaAsignacionesSerie());
            } else {
                tablas.put(GeneradorOficio.EXCEL_ASIGNACION, getTablaAsignacionesEspecifica(solicitante));
            }
            break;
        default:
            break;
        }

        // Documento de Oficio
        byte[] documentoOficio = actualizarVariablesOficio(pPlantilla, variables, tablas, header);
        return documentoOficio;
    }

    /**
     * Crea la tabla de asignados por especifica n formato Word (.docx). usando la librería Apache POI:
     * @param solicitante
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaAsignacionesEspecifica(boolean solicitante) {
        // Cabecera
        String[] headerTextSolicitante = {"CLAVE DEL SERVICIO NO GEOGRÁFICO", "NUMERO DE USUARIO", null, null,
                "USUARIO", "IDO/IDD (Antes BCD)", "IDA"};
        String[] headerTextOtros = {"CLAVE DEL SERVICIO NO GEOGRÁFICO", "NUMERO DE USUARIO", null, null, "IDO/IDD (Antes BCD)", "IDA"};

        String[] headerText = null;
        if (solicitante) {
            headerText = headerTextSolicitante;
        } else {
            headerText = headerTextOtros;
        }

        // Tablas
        XWPFDocument doc = new XWPFDocument();

        // Creamos una lista de numeraciones asignadas
        List<NumeracionAsignadaNng> listaAsignadas = new ArrayList<NumeracionAsignadaNng>();
        for (NumeracionSolicitadaNng numSol : solicitud.getNumeracionesSolicitadas()) {
            listaAsignadas.addAll(numSol.getNumeracionesAsignadas());
        }

        XWPFTable table = doc.createTable(listaAsignadas.size() + 2, headerText.length);

        try {
            // Primera cabecera de la tabla
            XWPFTableCell firstCell = table.getRow(0).getCell(0);
            XWPFParagraph cellParagraph = firstCell.getParagraphs().get(0);
            cellParagraph.setAlignment(ParagraphAlignment.CENTER);

            // Cabecera de la tabla
            for (int i = 0; i < headerText.length; i++) {
                if (!StringUtils.isEmpty(headerText[i])) {
                    generaCelda(table.getRow(0).getCell(i), headerText[i], 10, true);
                }
            }

            // Subcabecera de la tabla
            generaCelda(table.getRow(1).getCell(1), "SERIE", 10, true);
            generaCelda(table.getRow(1).getCell(2), "NÚMERO INICIAL", 10, true);
            generaCelda(table.getRow(1).getCell(3), "NÚMERO FINAL", 10, true);

            // Contenido de la tabla
            int fila = 2; // Empezamos despúes de la cabecera

            for (NumeracionAsignadaNng numeracion : listaAsignadas) {
                int col = 0;
                // Calve servicio
                generaCelda(table.getRow(fila).getCell(col++), numeracion.getClaveServicio().getCodigo().toString(), 10,
                        false);

                // SNA
                generaCelda(table.getRow(fila).getCell(col++), numeracion.getSnaAsString(), 10, false);

                // Terminacion
                generaCelda(table.getRow(fila).getCell(col++), numeracion.getInicioRango(), 10, false);
                generaCelda(table.getRow(fila).getCell(col++), numeracion.getFinRango(), 10, false);

                if (solicitante) {
                    // Cliente
                    generaCelda(table.getRow(fila).getCell(col++), numeracion.getNumeracionSolicitada().getCliente(),
                            10, false);
                }
                // ABC Se cambio por BCD
                generaCelda(
                        table.getRow(fila).getCell(col++),
                        (numeracion.getNumeracionSolicitada().getBcd() == null) ?
                                    numeracion.getNumeracionSolicitada().getConcesionario().getBcd().toString()
                        :
                                numeracion.getNumeracionSolicitada().getBcd().toString(),
                        10, false);

                // IDA
                if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                        .equals(TipoProveedor.COMERCIALIZADORA)) {
                    generaCelda(table.getRow(fila).getCell(col++), solicitud.getProveedorSolicitante().getIda()
                            .toString(), 10, false);
                } else {
		    // Se incorpora la regla adicional para considerar los casos de concesionarios o
		    // ambos y tomar el IDO como IDA
		    if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
			    .equals(TipoProveedor.CONCESIONARIO)
			    || solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
				    .equals(TipoProveedor.AMBOS)) {
			generaCelda(table.getRow(fila).getCell(col++), solicitud.getProveedorSolicitante().getIdo()
	                            .toString(), 10, false);

		    } else {
			generaCelda(table.getRow(fila).getCell(col++), numeracion.getNumeracionSolicitada().getBcd()
	                            .toString(), 10, false);
		    }
                }
                fila++;

            }

            // Aplicamos Merge
            fusionarCeldas(table, 0, 1, 4);

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
        }

        return table;
    }

    /**
     * Carga las variables a sustituir en los oficios de PST Solicititante y Resto PSTs.
     * @throws Exception error
     */
    private void cargaVariablesOficio() throws Exception {

        Integer numAsignados = 0;

        for (RangoSerieNng rango : solicitud.getRangosNng()) {
            numAsignados += rango.getNumFinalAsInt() - rango.getNumInicioAsInt() + 1;
        }
        // Cargamos textos variables segun tipo asignacion
        String texto1 = GeneradorOficio.VACIO;
        String texto2 = GeneradorOficio.VACIO;
        String texto3 = GeneradorOficio.VACIO;
        String texto4 = GeneradorOficio.VACIO;
        String texto5 = GeneradorOficio.VACIO;

        String textoOtros1 = GeneradorOficio.VACIO;

        if (solicitud.getTipoAsignacion().getCdg().equals(TipoAsignacion.SERIES)) {

            texto1 = this.getParamService().getParamByName("textoAsignacionSerieNng1");
            texto3 = this.getParamService().getParamByName("textoAsignacionSerieNng2");

            texto1 = MessageFormat.format(texto1, solicitud.getReferencia(), numAsignados);
            texto3 = MessageFormat.format(texto3,
                    FechasUtils.fechaToString(solicitud.getFechaIniUtilizacion(), "dd 'de' MMMM 'de' yyyy"));

            textoOtros1 = texto3;
        } else {
            texto1 = this.getParamService().getParamByName("textoAsignacionEspecificoNng1");
            texto2 = this.getParamService().getParamByName("textoAsignacionEspecificoNng2");
            texto3 = this.getParamService().getParamByName("textoAsignacionEspecificoNng3")
                    + this.getParamService().getParamByName("textoAsignacionEspecificoNng3c");
            texto4 = this.getParamService().getParamByName("textoAsignacionEspecificoNng4");
            texto5 = this.getParamService().getParamByName("textoAsignacionEspecificoNng5")
                    + this.getParamService().getParamByName("textoAsignacionEspecificoNng5c");

            textoOtros1 = this.getParamService().getParamByName("textoAsignacionOtrosNng")
                    + this.getParamService().getParamByName("textoAsignacionOtrosNngC");

            texto1 = MessageFormat.format(texto1, solicitud.getReferencia(), solicitud.getProveedorSolicitante()
                    .getNombre());
            texto2 = MessageFormat.format(texto2, solicitud.getProveedorSolicitante().getNombre());
        }

        variables.put(TEXTO_ASIGNACION_NNG_1, texto1);
        variables.put(TEXTO_ASIGNACION_NNG_2, texto2);
        variables.put(TEXTO_ASIGNACION_NNG_3, texto3);
        variables.put(TEXTO_ASIGNACION_NNG_4, texto4);
        variables.put(TEXTO_ASIGNACION_NNG_5, texto5);

        variables.put(TEXTO_ASIGNACION_OTROS_NNG_1, textoOtros1);

        variables.put(ATENCION, solicitud.getProveedorSolicitante().getNombre());
        variables.put(REFERENCIA_SOLICITUD, solicitud.getReferencia());

        if (solicitud.getRepresentanteLegal() != null) {
            variables.put(REPRESENTANTE_LEGAL, solicitud.getRepresentanteLegal().getNombre());
        } else {
            variables.put(REPRESENTANTE_LEGAL, VACIO);
        }

        if (solicitud.getRepresentanteSuplente() != null) {
            variables.put(REPRESENTANTE_LEGAL_SUPLENTE, solicitud.getRepresentanteSuplente()
                    .getNombre());
        } else {
            variables.put(REPRESENTANTE_LEGAL_SUPLENTE, VACIO);
        }

        variables.put(FECHA_INICIO_UTILIZACION,
                FechasUtils.fechaToString(solicitud.getFechaIniUtilizacion(), "dd 'de' MMMM 'de' yyyy"));

        if (!StringUtils.isEmpty(textoAdicional)) {
            variables.put(TEXTO_ADICIONAL, textoAdicional);
        } else {
            variables.put(TEXTO_ADICIONAL, VACIO);
        }
    }

    /**
     * Crea la tabla de Asignaciones por serie en formato Word (.docx). usando la librería Apache POI:
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaAsignacionesSerie() {
        // Cabecera
        String[] headerText = {"CLAVE DEL SERVICIO NO GEOGRÁFICO", "SERIE", "NÚMERO INICIAL", "NÚMERO FINAL", "IDO/IDD (Antes BCD)",
                "IDA"};

        // Tablas
        XWPFDocument doc = new XWPFDocument();

        // Creamos la lista de numeraciones asignadas
        List<NumeracionAsignadaNng> listaAsignadas = new ArrayList<NumeracionAsignadaNng>();
        for (NumeracionSolicitadaNng numSol : solicitud.getNumeracionesSolicitadas()) {
            listaAsignadas.addAll(numSol.getNumeracionesAsignadas());
        }

        XWPFTable table = doc.createTable(listaAsignadas.size() + 1, headerText.length);

        try {
            // Cabecera de la tabla
            XWPFTableCell firstCell = table.getRow(0).getCell(0);
            XWPFParagraph cellParagraph = firstCell.getParagraphs().get(0);
            cellParagraph.setAlignment(ParagraphAlignment.CENTER);
            for (int i = 0; i < headerText.length; i++) {
                generaCelda(table.getRow(0).getCell(i), headerText[i], 10, true);
            }

            // Contenido de la tabla
            int fila = 1; // Empezamos despúes de la cabecera
            for (NumeracionAsignadaNng numeracion : listaAsignadas) {

                // Clave servicio
                generaCelda(table.getRow(fila).getCell(0), numeracion.getClaveServicio().getCodigo().toString(), 10,
                        false);

                // SNA
                generaCelda(table.getRow(fila).getCell(1), numeracion.getSnaAsString(), 10, false);

                // NUMERACION
                generaCelda(table.getRow(fila).getCell(2), numeracion.getInicioRango(), 10, false);
                generaCelda(table.getRow(fila).getCell(3), numeracion.getFinRango(), 10, false);

                // BCD
                generaCelda(table.getRow(fila).getCell(4),
                        (numeracion.getNumeracionSolicitada().getBcd() == null) ?
                                numeracion.getNumeracionSolicitada().getConcesionario().getBcd().toString() :
                                numeracion.getNumeracionSolicitada().getBcd().toString()
                        , 10, false);

                // IDA
                if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                        .equals(TipoProveedor.COMERCIALIZADORA)) {
                    generaCelda(table.getRow(fila).getCell(5), solicitud.getProveedorSolicitante().getIda()
                            .toString(),
                            10, false);
                } else {
		    // Se incorpora la regla adicional para considerar los casos de concesionarios o
		    // ambos y tomar el IDO como IDA
		    if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
			    .equals(TipoProveedor.CONCESIONARIO)
			    || solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
				    .equals(TipoProveedor.AMBOS)) {
			generaCelda(table.getRow(fila).getCell(5), solicitud.getProveedorSolicitante().getIdo()
	                            .toString(), 10, false);

		    } else {
                        generaCelda(table.getRow(fila).getCell(5), numeracion.getNumeracionSolicitada().getBcd().toString(),
                                10, false);
		    }
                }
                fila++;

            }

        } catch (Exception e) {
            LOGGER.debug("Error inesperado", e.getMessage());
        }

        return table;
    }
}
