package mx.ift.sns.negocio.oficios;

import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoRangos;
import mx.ift.sns.modelo.nng.HistoricoRangoSerieNng;
import mx.ift.sns.modelo.nng.RedistribucionSolicitadaNng;
import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.EstadoRedistribucionSolicitada;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Proporciona los métodos para la generación del oficio de Solicitud de Redistribución no geográfica. */
public class OficioSolicitudRedistribucionNng extends GeneradorOficio {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioSolicitudRedistribucionNng.class);

    /** Información de la solicitud. */
    private SolicitudRedistribucionNng solicitud;

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
    public OficioSolicitudRedistribucionNng(ParametrosOficio pParametros) {
        solicitud = (SolicitudRedistribucionNng) pParametros.getSolicitud();
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
        case TipoDestinatario.RESTO_PST:
            this.cargaVariablesOficio();
            tablas.put(GeneradorOficio.EXCEL_REDIST_ANTES, this.getTablaRedistAntes(true));
            tablas.put(GeneradorOficio.EXCEL_REDIST_DESPUES, this.getTablaRedistDespues(true));
            break;
        case TipoDestinatario.PST_SOLICITANTE:
            this.cargaVariablesOficio();
            tablas.put(GeneradorOficio.EXCEL_REDIST_ANTES, this.getTablaRedistAntes(false));
            tablas.put(GeneradorOficio.EXCEL_REDIST_DESPUES, this.getTablaRedistDespues(false));
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
        variables.put(FECHA_SOLICITUD,
                FechasUtils.fechaToString(solicitud.getFechaSolicitud(), "dd 'de' MMMM 'de' yyyy"));
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
     * Crea la tabla de Información de Redistribuciones en formato Word (.docx). usando la librería Apache POI.
     * @param pRestoPsts Indica si se trata del Oficio para Resto de PST's (las columnas cambian)
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaRedistDespues(boolean pRestoPsts) {
        // Cabecera
        String[] headerTextRestoPsts = {"CLAVE SERVICIO", "SERIE", "NUM. INICIAL", "NUM. FINAL", "IDO/IDD (Antes BCD)", "IDA",
                "*FECHA DE APLICACIÓN DE LA REDISTRIBUCIÓN EN EL PLAN NACIONAL DE NUMERACIÓN"};

        String[] headerTextSolicitante = {"CLAVE SERVICIO", "SERIE", "NUM. INICIAL", "NUM. FINAL", "IDO/IDD (Antes BCD)", "IDA",
                "*FECHA DE APLICACIÓN DE LA REDISTRIBUCIÓN EN EL PLAN NACIONAL DE NUMERACIÓN", "USUARIO"};

        // Tamaño de tabla
        int tableSize = 1; // Cabecera
        for (RedistribucionSolicitadaNng redSol : solicitud.getRedistribucionesSolicitadas()) {
            if (!redSol.getEstatus().getCodigo().equals(EstadoRedistribucionSolicitada.CANCELADO)) {
                tableSize++;
            }
        }

        // Tablas
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table;
        if (pRestoPsts) {
            table = doc.createTable(tableSize, headerTextRestoPsts.length);
        } else {
            table = doc.createTable(tableSize, headerTextSolicitante.length);
        }

        try {
            // Cabecera
            if (pRestoPsts) {
                for (int i = 0; i < headerTextRestoPsts.length; i++) {
                    generaCelda(table.getRow(0).getCell(i), headerTextRestoPsts[i], 10, true);
                }
            } else {
                for (int i = 0; i < headerTextSolicitante.length; i++) {
                    generaCelda(table.getRow(0).getCell(i), headerTextSolicitante[i], 10, true);
                }
            }

            // Contenido de la tabla
            int fila = 1;
            for (RedistribucionSolicitadaNng redSol : solicitud.getRedistribucionesSolicitadas()) {
                if (!redSol.getEstatus().getCodigo().equals(EstadoRedistribucionSolicitada.CANCELADO)) {
                    // Clave de Servicio
                    generaCelda(table.getRow(fila).getCell(0), String.valueOf(redSol.getIdClaveServicio()), 10, false);

                    // Serie
                    generaCelda(table.getRow(fila).getCell(1), String.valueOf(redSol.getSna()), 10, false);

                    // Inicio de Serie
                    generaCelda(table.getRow(fila).getCell(2), redSol.getNumInicio(), 10, false);

                    // Final de Serie
                    generaCelda(table.getRow(fila).getCell(3), redSol.getNumFinal(), 10, false);

                    // ABC
                    if (redSol.getBcd() != null) {
                        generaCelda(table.getRow(fila).getCell(4), String.valueOf(redSol.getBcd()), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(4), String.valueOf(
                                redSol.getProveedorConcesionario().getBcd()), 10, false);
                    }

                    // IDA
                    if (redSol.getIda() != null) {
                        generaCelda(table.getRow(fila).getCell(5), String.valueOf(redSol.getIda()), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(5), String.valueOf(
                                redSol.getProveedorConcesionario().getBcd()), 10, false);
                    }

                    if (pRestoPsts) {
                        // Fecha de Implementación
                        generaCelda(table.getRow(fila).getCell(6),
                                FechasUtils.fechaToString(solicitud.getFechaAsignacion()), 10, false);
                    } else {
                        // Fecha de Implementación
                        generaCelda(table.getRow(fila).getCell(6),
                                FechasUtils.fechaToString(solicitud.getFechaAsignacion()), 10, false);

                        // Usuario
                        generaCelda(table.getRow(fila).getCell(7), redSol.getCliente(), 10, false);
                    }

                    fila++;
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
        }

        return table;
    }

    /**
     * Crea la tabla de Información de Redistribuciones en formato Word (.docx). usando la librería Apache POI.
     * @param pRestoPsts Indica si se trata del Oficio para Resto de PST's (las columnas cambian)
     * @return XWPFTable Tabla Word.
     */
    private XWPFTable getTablaRedistAntes(boolean pRestoPsts) {
        // Cabecera
        String[] headerTextRestoPsts = {"CLAVE SERVICIO", "SERIE", "NUM. INICIAL", "NUM. FINAL", "IDO/IDD (Antes BCD)", "IDA"};

        String[] headerTextSolicitante = {"CLAVE SERVICIO", "SERIE", "NUM. INICIAL", "NUM. FINAL", "IDO/IDD (Antes BCD)", "IDA",
                "USUARIO"};

        // Tamaño de tabla
        int tableSize = 1; // Cabecera
        for (RedistribucionSolicitadaNng redSol : solicitud.getRedistribucionesSolicitadas()) {
            if (!redSol.getEstatus().getCodigo().equals(EstadoRedistribucionSolicitada.CANCELADO)) {
                tableSize++;
            }
        }

        // Tablas
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table;
        if (pRestoPsts) {
            table = doc.createTable(tableSize, headerTextRestoPsts.length);
        } else {
            table = doc.createTable(tableSize, headerTextSolicitante.length);
        }

        try {
            // Cabecera
            if (pRestoPsts) {
                for (int i = 0; i < headerTextRestoPsts.length; i++) {
                    generaCelda(table.getRow(0).getCell(i), headerTextRestoPsts[i], 10, true);
                }
            } else {
                for (int i = 0; i < headerTextSolicitante.length; i++) {
                    generaCelda(table.getRow(0).getCell(i), headerTextSolicitante[i], 10, true);
                }
            }

            // Contenido de la tabla
            int fila = 1;
            FiltroBusquedaHistoricoRangos filtros;
            for (RedistribucionSolicitadaNng redSol : solicitud.getRedistribucionesSolicitadas()) {
                if (!redSol.getEstatus().getCodigo().equals(EstadoRedistribucionSolicitada.CANCELADO)) {
                    // Filtros para recuperar el estado anterior del rango. Buscamos el movimiento anterior
                    // al creado por la redistribución.
                    filtros = new FiltroBusquedaHistoricoRangos();
                    filtros.setUsarPaginacion(true);
                    filtros.setNumeroPagina(0);
                    filtros.setResultadosPagina(5);
                    filtros.setIdClaveServicio(redSol.getIdClaveServicio());
                    filtros.setSna(redSol.getSna());
                    filtros.setNumInicio(redSol.getNumInicio());
                    filtros.setNumFinal(redSol.getNumFinal());
                    filtros.setIdSolicitudDistinct(redSol.getSolicitudRedistribucion().getId());
                    filtros.setIdPstAsignatario(redSol.getProveedorSolicitante().getId());
                    filtros.setFechaHistoricoHasta(redSol.getSolicitudRedistribucion().getFechaAsignacion());
                    filtros.setOrderType(FiltroBusquedaHistoricoRangos.ORDEN_DESC);

                    // En el método check se comprueba que existan movimientos para poder cancelar.
                    List<HistoricoRangoSerieNng> listaMovs =
                            this.getSeriesNngService().findAllHistoricActionsFromRangos(filtros);

                    // La lista viene ordenada por ID's DESC. El primer elemento de la lista se corresponde con el
                    // último movimiento del rango afectado antes de la redistribución.
                    HistoricoRangoSerieNng hrsnng;
                    if (!listaMovs.isEmpty()) {
                        hrsnng = listaMovs.get(0);

                        // ABC aohora BCD
                        if (hrsnng.getBcd() != null) {
                            generaCelda(table.getRow(fila).getCell(4), String.valueOf(hrsnng.getBcd()), 10, false);
                        } else {
                            generaCelda(table.getRow(fila).getCell(4), String.valueOf(solicitud.getProveedorSolicitante().getBcd()), 10, false);
                        }

                        if (!pRestoPsts) {
                            // Usuario
                            generaCelda(table.getRow(fila).getCell(6), hrsnng.getCliente(), 10, false);
                        }

                    } else {
                        // ABC
                        generaCelda(table.getRow(fila).getCell(4), "", 10, false);

                        if (!pRestoPsts) {
                            // Usuario
                            generaCelda(table.getRow(fila).getCell(6), "", 10, false);
                        }
                    }

                    // Clave de Servicio
                    generaCelda(table.getRow(fila).getCell(0), String.valueOf(redSol.getIdClaveServicio()), 10, false);

                    // Serie
                    generaCelda(table.getRow(fila).getCell(1), String.valueOf(redSol.getSna()), 10, false);

                    // Inicio de Serie
                    generaCelda(table.getRow(fila).getCell(2), redSol.getNumInicio(), 10, false);

                    // Final de Serie
                    generaCelda(table.getRow(fila).getCell(3), redSol.getNumFinal(), 10, false);

                    // IDA
                    if (redSol.getIda() != null) {
                        generaCelda(table.getRow(fila).getCell(5), String.valueOf(redSol.getIda()), 10, false);
                    } else {
                        generaCelda(table.getRow(fila).getCell(5), redSol.getProveedorConcesionario().getBcd().toString(), 10, false);
                    }

                    fila++;
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
        }

        return table;
    }
}
