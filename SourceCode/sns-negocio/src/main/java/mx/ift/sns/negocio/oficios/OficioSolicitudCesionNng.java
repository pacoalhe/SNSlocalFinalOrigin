package mx.ift.sns.negocio.oficios;

import java.math.BigDecimal;
import java.util.Date;

import mx.ift.sns.modelo.nng.CesionSolicitadaNng;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.oficios.TipoRol;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;
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

/** Proporciona los métodos para la generación del oficio de Solicitud de Cesión No Geográfica. */
public class OficioSolicitudCesionNng extends GeneradorOficio {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioSolicitudCesionNng.class);

    /** Información de la solicitud. */
    private SolicitudCesionNng solicitud;

    /** Información del destinatario. */
    private TipoDestinatario destinatario;

    /** TIpo de Rol. */
    private TipoRol tipoRol;

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
    public OficioSolicitudCesionNng(ParametrosOficio pParametros) {
        solicitud = (SolicitudCesionNng) pParametros.getSolicitud();
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
            this.cargaVariablesCedula();
            break;
        case TipoDestinatario.ACTA_CIRCUNSTANCIADA:
            this.cargaVariablesActa();
            break;
        case TipoDestinatario.RESTO_PST: // Mismas que PST Solicitante
        case TipoDestinatario.PST_SOLICITANTE:
            this.cargaVariablesOficio();
            tablas.put(GeneradorOficio.EXCEL_CESIONES_CEDENTE, getTablaCesionesCedente());
            tablas.put(GeneradorOficio.EXCEL_CESIONES_CESIONARIO, getTablaCesionesCesionario());
            break;
        default:
            break;
        }

        // Documento de Oficio
        byte[] documentoOficio = actualizarVariablesOficio(pPlantilla.clone(), variables, tablas, header);
        return documentoOficio;
    }

    /**
     * Carga las variables a sustituir en los documentos de cédula de notificación.
     */
    private void cargaVariablesCedula() {
        variables.put(MES, FechasUtils.fechaToString(fechaOficio, "MMMM"));
        variables.put(MES_ANIO, FechasUtils.fechaToString(fechaOficio, "MMMM yyyy"));
        // variables.put(FECHA_OFICIO, FechasUtils.fechaToString(fechaOficio, "dd 'de' MMMM 'de' yyyy"));

        if (tipoRol != null && tipoRol.getCdg().equals(TipoRol.TIPO_ROL_CESIONARIO)) {
            variables.put(CONCESIONARIO, solicitud.getProveedorCesionario().getNombre());
            variables.put(CALLE, solicitud.getProveedorCesionario().getCalle());
            variables.put(COLONIA, solicitud.getProveedorCesionario().getColonia());
            variables.put(CP, solicitud.getProveedorCesionario().getCp());
            variables.put(ENTIDAD, solicitud.getProveedorCesionario().getCiudad());
        } else {
            variables.put(CONCESIONARIO, solicitud.getProveedorSolicitante().getNombre());
            variables.put(CALLE, solicitud.getProveedorSolicitante().getCalle());
            variables.put(COLONIA, solicitud.getProveedorSolicitante().getColonia());
            variables.put(CP, solicitud.getProveedorSolicitante().getCp());
            variables.put(ENTIDAD, solicitud.getProveedorSolicitante().getCiudad());
        }
    }

    /**
     * Carga las variables a sustituir en los documentos de acta circunstanciada.
     */
    private void cargaVariablesActa() {
        // variables.put(FECHA_OFICIO, FechasUtils.fechaToString(fechaOficio, "dd 'de' MMMM 'de' yyyy"));

        if (tipoRol != null && tipoRol.getCdg().equals(TipoRol.TIPO_ROL_CESIONARIO)) {
            variables.put(CONCESIONARIO, solicitud.getProveedorCesionario().getNombre());
            variables.put(REPRESENTANTE_LEGAL, solicitud.getRepresentanteLegalCesionario().getNombre());
        } else {
            variables.put(CONCESIONARIO, solicitud.getProveedorSolicitante().getNombre());
            variables.put(REPRESENTANTE_LEGAL, solicitud.getRepresentanteLegal().getNombre());
        }
    }

    /**
     * Carga las variables a sustituir en los oficios de PST Solicititante y Resto PSTs.
     */
    private void cargaVariablesOficio() {
        variables.put(REFERENCIA_SOLICITUD, solicitud.getReferencia());
        variables.put(CEDENTE, solicitud.getProveedorSolicitante().getNombre());
        variables.put(CEDENTE_CORTO, solicitud.getProveedorSolicitante().getNombreCorto());
        variables.put(CESIONARIO, solicitud.getProveedorCesionario().getNombre());
        variables.put(CESIONARIO_CORTO, solicitud.getProveedorCesionario().getNombreCorto());

        variables.put(CALLE_CESIONARIO, solicitud.getProveedorCesionario().getCalle());
        variables.put(COLONIA_CESIONARIO, solicitud.getProveedorCesionario().getColonia());
        variables.put(ENTIDAD_CESIONARIO, solicitud.getProveedorCesionario().getCiudad());
        variables.put(NUM_EXT_CESIONARIO, solicitud.getProveedorCesionario().getNumExt());
        variables.put(CP_CESIONARIO, solicitud.getProveedorCesionario().getCp());
        variables.put(ESTADO_CESIONARIO, solicitud.getProveedorCesionario().getEstado().getNombre());

        if (solicitud.getProveedorCesionario().getNumInt() != null) {
            variables.put(NUM_INT_CESIONARIO, solicitud.getProveedorCesionario().getNumInt());
        } else {
            variables.put(NUM_INT_CESIONARIO, VACIO);
        }

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

        if (solicitud.getRepresentanteLegalCesionario() != null) {
            variables.put(REPRESENTANTE_LEGAL_CESIONARIO, solicitud.getRepresentanteLegalCesionario().getNombre());
        } else {
            variables.put(REPRESENTANTE_LEGAL_CESIONARIO, VACIO);
        }

        if (solicitud.getRepresentanteSuplenteCesionario() != null) {
            variables.put(REPRESENTANTE_LEGAL_SUPLENTE_CESIONARIO,
                    solicitud.getRepresentanteSuplenteCesionario().getNombre());
        } else {
            variables.put(REPRESENTANTE_LEGAL_SUPLENTE_CESIONARIO, VACIO);
        }

        if (!StringUtils.isEmpty(textoAdicional)) {
            variables.put(TEXTO_ADICIONAL, textoAdicional);
        } else {
            variables.put(TEXTO_ADICIONAL, GeneradorOficio.VACIO);
        }
    }

    /**
     * Crea la tabla de Información de Cesiones en formato Word (.docx). usando la librería Apache POI:
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaCesionesCedente() {
        // Cabecera
        String[] headerText = {"CLAVE DE SERVICIO", "SERIE", "NUM. INICIAL", "NUM. FINAL", "IDO/IDD (Antes BCD)", "IDA"};

        // Tamaño de tabla
        int tableSize = 3; // Cabeceras
        for (CesionSolicitadaNng cesSol : solicitud.getCesionesSolicitadas()) {
            if (!cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.CANCELADO)) {
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
            generaCelda(firstCell, "TABLA 1", 12, true);

            XWPFTableCell secondCell = table.getRow(1).getCell(0);
            cellParagraph = secondCell.getParagraphs().get(0);
            cellParagraph.setAlignment(ParagraphAlignment.CENTER);
            String empresa = "NUMERACIÓN ASIGNADA A LA EMPRESA "
                    + solicitud.getProveedorSolicitante().getNombre().toUpperCase();
            generaCelda(secondCell, empresa, 12, true);

            for (int i = 0; i < headerText.length; i++) {
                generaCelda(table.getRow(2).getCell(i), headerText[i], 10, true);
            }

            // Contenido de la tabla
            int fila = 3; // Empezamos despúes de la cabecera
            for (CesionSolicitadaNng cesSol : solicitud.getCesionesSolicitadas()) {
                if (!cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.CANCELADO)) {
                    // Clave de Servicio
                    generaCelda(table.getRow(fila).getCell(0), cesSol.getIdClaveServicio().toString(), 10, false);

                    // Identificador de Serie
                    generaCelda(table.getRow(fila).getCell(1), cesSol.getSna().toString(), 10, false);

                    // Inicio de Serie
                    generaCelda(table.getRow(fila).getCell(2), cesSol.getNumInicio(), 10, false);

                    // Final de Serie
                    generaCelda(table.getRow(fila).getCell(3), cesSol.getNumFinal(), 10, false);

                    // ABC Se cambio por el BCD del concesionario
                    if (cesSol.getProveedorCedente() != null && cesSol.getProveedorCedente().getBcd() != null){
                        generaCelda(table.getRow(fila).getCell(4), String.valueOf(cesSol.getProveedorCedente().getBcd()), 10, false);
                    }else if (cesSol.getBcd() != null){
                        generaCelda(table.getRow(fila).getCell(4), String.valueOf(cesSol.getBcd()), 10, false);
                    }else if (cesSol.getProveedorConcesionario() != null && cesSol.getProveedorConcesionario().getBcd() != null) {
                        generaCelda(table.getRow(fila).getCell(4), String.valueOf(cesSol.getProveedorConcesionario().getBcd()), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(4), "", 10, false);
                    }

                    // IDA
                    if (cesSol.getIda() != null) {
                        generaCelda(table.getRow(fila).getCell(5), String.valueOf(cesSol.getIda()), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(5),
                                String.valueOf((cesSol.getProveedorCedente().getBcd() == null) ? "" : cesSol.getProveedorCedente().getBcd()),
                                10, false);
                    }

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

    /**
     * Crea la tabla de Información de Cesiones en formato Word (.docx). usando la librería Apache POI:
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaCesionesCesionario() {
        // Cabecera
        String[] headerText = {"CLAVE DE SERVICIO", "SERIE", "NUM. INICIAL", "NUM. FINAL", "IDO/IDD (Antes BCD)", "IDA",
                "*FECHA DE APLICACIÓN DE LA CESIÓN EN EL PLAN NACIONAL DE NUMERACIÓN"};

        // Tamaño de tabla
        int tableSize = 3; // Cabeceras
        for (CesionSolicitadaNng cesSol : solicitud.getCesionesSolicitadas()) {
            if (!cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.CANCELADO)) {
                tableSize++;
            }
        }

        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(tableSize, headerText.length);
        try {
            XWPFTableCell firstCell = table.getRow(0).getCell(0);
            XWPFParagraph cellParagraph = firstCell.getParagraphs().get(0);
            cellParagraph.setAlignment(ParagraphAlignment.CENTER);
            generaCelda(firstCell, "TABLA 2", 12, true);

            XWPFTableCell secondCell = table.getRow(1).getCell(0);
            cellParagraph = secondCell.getParagraphs().get(0);
            cellParagraph.setAlignment(ParagraphAlignment.CENTER);
            String emprea = "NUMERACIÓN ASIGNADA A LA EMPRESA "
                    + solicitud.getProveedorCesionario().getNombre().toUpperCase();
            generaCelda(secondCell, emprea, 12, true);

            for (int i = 0; i < headerText.length; i++) {
                generaCelda(table.getRow(2).getCell(i), headerText[i], 10, true);
            }

            // Contenido de la tabla
            int fila = 3; // Empezamos despúes de la cabecera
            for (CesionSolicitadaNng cesSol : solicitud.getCesionesSolicitadas()) {
                if (!cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.CANCELADO)) {
                    // Clave de Servicio
                    generaCelda(table.getRow(fila).getCell(0), cesSol.getIdClaveServicio().toString(), 10, false);

                    // Identificador de Serie
                    generaCelda(table.getRow(fila).getCell(1), cesSol.getSna().toString(), 10, false);

                    // Inicio de Serie
                    generaCelda(table.getRow(fila).getCell(2), cesSol.getNumInicio(), 10, false);

                    // Final de Serie
                    generaCelda(table.getRow(fila).getCell(3), cesSol.getNumFinal(), 10, false);

                    // ABC que va a tener cuando se aplique la cesión.
                    generaCelda(table.getRow(fila).getCell(4), this.getEnrutamientoProgramado(cesSol), 10, false);

                    // IDA que va a tener cuando se aplique la cesión.
                    BigDecimal idaCesionario = cesSol.getSolicitudCesion().getProveedorCesionario().getIda();
                    if (idaCesionario != null) {
                        generaCelda(table.getRow(fila).getCell(5), String.valueOf(idaCesionario), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(5),
                                String.valueOf(cesSol.getSolicitudCesion().getProveedorCesionario().getBcd()),
                                10, false);
                    }

                    // Fecha de Implementación
                    generaCelda(table.getRow(fila).getCell(6),
                            FechasUtils.fechaToString(cesSol.getFechaCesion()), 10, false);

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

    /**
     * Indica el ABC que va a tener la numeración cuando se aplique la cesión programada.
     * @param pCesSol Información de la solicitud de cesión.
     * @return ABC futuro de la numeración.
     */
    private String getEnrutamientoProgramado(CesionSolicitadaNng pCesSol) {
        Proveedor pst = pCesSol.getProveedorCesionario();
        String bcd = "";
        try {
            // Diferenciamos entre Proveedor Cesionario Concesionario o Comercialziadora
            if (pst.getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {
                bcd = String.valueOf(pCesSol.getConvenioCesionario().getProveedorConcesionario().getBcd());

            } else if (pst.getTipoProveedor().getCdg().equals(TipoProveedor.CONCESIONARIO)) {
                bcd = String.valueOf(pCesSol.getProveedorCesionario().getBcd());

            } else {
                // Se puede utilizar el ABC Propio
                if (pCesSol.getUsarAbcCesionario().equals("S")) {
                    bcd = String.valueOf(pCesSol.getProveedorCesionario().getBcd());
                }

                // Se puede utilizar el ABC de un Concesionario con el que se tenga convenio
                if (pCesSol.getUsarConvenioCesionario().equals("S")) {
                    bcd = String.valueOf(pCesSol.getConvenioCesionario().getProveedorConcesionario().getBcd());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e.getMessage());
        }

        return bcd;
    }

}
