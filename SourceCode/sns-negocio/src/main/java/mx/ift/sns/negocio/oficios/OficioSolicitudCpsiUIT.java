package mx.ift.sns.negocio.oficios;

import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSI;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Proporciona los métodos para la generación del oficio de Solicitud de Códigos CPSI a la UIT. */
public class OficioSolicitudCpsiUIT extends GeneradorOficio {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioSolicitudCpsiUIT.class);

    /** Información de la solicitud. */
    @SuppressWarnings("unused")
    private SolicitudCpsiUit solicitud;

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
    public OficioSolicitudCpsiUIT(ParametrosOficio pParametros) {
        solicitud = (SolicitudCpsiUit) pParametros.getSolicitud();
        destinatario = pParametros.getTipoDestinatario();
        numeroOficio = pParametros.getNumOficio();
        fechaOficio = pParametros.getFechaOficio();
        textoAdicional = pParametros.getTextoAdicional();
    }

    @Override
    public byte[] getDocumentoOficio(byte[] pPlantilla) throws Exception {

        // Lista de Códigos CPSI del Catálogo
        FiltroBusquedaCodigosCPSI filtros = new FiltroBusquedaCodigosCPSI();
        List<CodigoCPSI> catalogoCpsi = this.getCodigosCpsiService().findAllCodigosCPSI(filtros);

        // Los oficios a UIT solo son para el Solicitante.
        switch (destinatario.getCdg()) {
        case TipoDestinatario.CEDULA_NOTIFICACION:
            break;
        case TipoDestinatario.ACTA_CIRCUNSTANCIADA:
            break;
        case TipoDestinatario.RESTO_PST:
            break;
        case TipoDestinatario.PST_SOLICITANTE:
            this.cargaVariablesOficio(catalogoCpsi);
            tablas.put(GeneradorOficio.EXCEL_ASIGANCION_CPSI, getTablaAsignacionesCpsiUIT(catalogoCpsi));
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
     * @param pCatalogoCPSI Códigos CPSI del Catálogo.
     */
    private void cargaVariablesOficio(List<CodigoCPSI> pCatalogoCPSI) {
        header.put(NUM_OFICIO, numeroOficio);
        variables.put(NUM_OFICIO, numeroOficio);
        variables.put(FECHA_OFICIO, FechasUtils.fechaToString(fechaOficio, "dd 'de' MMMM 'de' yyyy"));

        if (!StringUtils.isEmpty(textoAdicional)) {
            variables.put(TEXTO_ADICIONAL, textoAdicional);
        } else {
            variables.put(TEXTO_ADICIONAL, VACIO);
        }

        // Variables cargadas de la tabla de Parámetros
        try {
            variables.put(TITULO_FIRMANTE, this.getParamService().getParamByName(TITULO_FIRMANTE).toUpperCase());
        } catch (Exception ex) {
            variables.put(TITULO_FIRMANTE, VACIO);
        }

        try {
            variables.put(FIRMANTE_OFICIO, this.getParamService().getParamByName(FIRMANTE_OFICIO).toUpperCase());
        } catch (Exception ex) {
            variables.put(FIRMANTE_OFICIO, VACIO);
        }

        // Total de Códigos CPSI del Catálogo
        int total = pCatalogoCPSI.size();
        variables.put(CPSI_CATALOGO, String.valueOf(total));

        // Porcentaje de Asignados respecto al total de Códigos del Catálogo
        if (total == 0) {
            variables.put(CPSI_PORCENTAJE, "0%");
        } else {
            FiltroBusquedaCodigosCPSI filtros = new FiltroBusquedaCodigosCPSI();
            EstatusCPSI estatusAsignado = new EstatusCPSI();
            estatusAsignado.setId(EstatusCPSI.ASIGNADO);
            filtros.setEstatusCPSI(estatusAsignado);

            int asignados = this.getCodigosCpsiService().findAllCodigosCPSICount(filtros);
            if (asignados == 0) {
                variables.put(CPSI_PORCENTAJE, "0%");
            } else {
                int media = (asignados * 100) / total;
                variables.put(CPSI_PORCENTAJE, (media + "%"));
            }
        }
    }

    /**
     * Crea la tabla de Información de solicitudes de CPSI a la UIT en formato Word (.docx).
     * @param pCatalogoCPSI Códigos CPSI del Catálogo.
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaAsignacionesCpsiUIT(List<CodigoCPSI> pCatalogoCPSI) {
        // Cabecera
        String[] headerText = {"CÓDIGO", "FECHA SOLICITUD", "OFICIO ASIGNACIÓN", "NOMBRE"};

        // Tamaño de tabla
        int tableSize = 2; // Cabeceras
        tableSize += pCatalogoCPSI.size();

        // Tablas
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(tableSize, headerText.length);

        try {
            XWPFTableCell firstCell = table.getRow(0).getCell(0);
            XWPFParagraph cellParagraph = firstCell.getParagraphs().get(0);
            cellParagraph.setAlignment(ParagraphAlignment.CENTER);
            generaCelda(firstCell, "CÓDIGOS DE PUNTO DE SEÑALIZACIÓN INTERNACIONAL", 12, true);

            for (int i = 0; i < headerText.length; i++) {
                generaCelda(table.getRow(1).getCell(i), headerText[i], 10, true);
            }

            // Contenido de la tabla
            int fila = 2; // Empezamos despúes de la cabecera
            for (CodigoCPSI codigo : pCatalogoCPSI) {
                // Código
                generaCelda(table.getRow(fila).getCell(0), codigo.getFormatoDecimal(), 10, false);

                if (codigo.getSolicitud() != null) {
                    // Solamente debe aparecer información de Asignaciones y Cesiones de CPSI
                    if (codigo.getSolicitud().getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION_CPSI)
                            || codigo.getSolicitud().getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_CPSI)) {
                        // Fecha Solicitud
                        generaCelda(table.getRow(fila).getCell(1),
                                FechasUtils.fechaToString(codigo.getSolicitud().getFechaSolicitud()), 10, false);

                        // Oficio Asignación
                        generaCelda(table.getRow(fila).getCell(2),
                                codigo.getSolicitud().getNumOficioSolicitante(), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(1), "", 10, false);
                        generaCelda(table.getRow(fila).getCell(2), "", 10, false);
                    }
                } else {
                    generaCelda(table.getRow(fila).getCell(1), "", 10, false);
                    generaCelda(table.getRow(fila).getCell(2), "", 10, false);
                }

                // Nombre PST Asignatario
                if (codigo.getProveedor() != null) {
                    generaCelda(table.getRow(fila).getCell(3), codigo.getProveedor().getNombre(), 10, false);
                } else {
                    generaCelda(table.getRow(fila).getCell(3), "", 10, false);
                }

                fila++;
            }

            // Aplicamos Merge a las filas de cabecera
            fusionarCeldas(table, 0, 0, headerText.length);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
        }

        return table;
    }

}
