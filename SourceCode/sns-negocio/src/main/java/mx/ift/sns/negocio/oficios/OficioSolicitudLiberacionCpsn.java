package mx.ift.sns.negocio.oficios;

import java.util.Date;

import mx.ift.sns.modelo.cpsn.CPSNUtils;
import mx.ift.sns.modelo.cpsn.LiberacionSolicitadaCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudLiberacionCpsn;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Proporciona los métodos para la generación del oficio de Solicitud de Liberación de CPSN. */
public class OficioSolicitudLiberacionCpsn extends GeneradorOficio {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioSolicitudLiberacionCpsn.class);

    /** Información de la solicitud. */
    private SolicitudLiberacionCpsn solicitud;

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
    public OficioSolicitudLiberacionCpsn(ParametrosOficio pParametros) {
        solicitud = (SolicitudLiberacionCpsn) pParametros.getSolicitud();
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
        variables.put(REPRESENTANTE_LEGAL, solicitud.getRepresentanteLegal().getNombre());

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
     * Crea la tabla de Información de Liberaciones en formato Word (.docx). usando la librería Apache POI:
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaLiberaciones() {
        // Cabecera
        String[] headerText = {"ESTRUCTURA", "BIN. DESDE", "BIN. HASTA", "DEC. DESDE", "DEC. HASTA",
                "*FECHA DE LIBERACIÓN EN LA BASE DE DATOS DE SEÑALIZACIÓN"};

        // Tamaño de tabla
        int tableSize = 1; // Cabecera
        for (LiberacionSolicitadaCpsn libSol : solicitud.getLiberacionesSolicitadas()) {
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
            for (LiberacionSolicitadaCpsn libSol : solicitud.getLiberacionesSolicitadas()) {
                if (!libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.CANCELADO)) {
                    Integer minBloque = CPSNUtils.valorMinBloque(libSol.getBinario());
                    Integer maxBloque = CPSNUtils.valorMaxBloque(libSol.getBinario());

                    // Estructura
                    generaCelda(table.getRow(fila).getCell(0), libSol.getTipoBloqueCpsn().getDescripcion(), 10, false);

                    // Binario Desde
                    generaCelda(table.getRow(fila).getCell(1), CPSNUtils.valorBinario(minBloque), 10, false);

                    // Binario Hasta
                    generaCelda(table.getRow(fila).getCell(2), CPSNUtils.valorBinario(maxBloque), 10, false);

                    // Decimal Desde
                    generaCelda(table.getRow(fila).getCell(3), String.valueOf(minBloque), 10, false);

                    // Decimal Hasta
                    generaCelda(table.getRow(fila).getCell(4), String.valueOf(maxBloque), 10, false);

                    // Fecha Actualización/Fin Cuarentena
                    if (libSol.getFechaFinCuarentena() != null) {
                        generaCelda(table.getRow(fila).getCell(5),
                                FechasUtils.fechaToString(libSol.getFechaFinCuarentena()), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(5),
                                FechasUtils.fechaToString(libSol.getFechaImplementacion()), 10, false);
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
