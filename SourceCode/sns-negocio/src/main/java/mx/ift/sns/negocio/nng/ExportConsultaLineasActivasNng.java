package mx.ift.sns.negocio.nng;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.lineas.DetalleReporteNng;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información de la consulta sobre lineas activas. */
public class ExportConsultaLineasActivasNng implements IExportarExcel {

    /** Lista de detalles de reportes. */
    private List<DetalleReporteNng> listaDetalle;

    /** Cabecera del Excel de Consulta de lineas activas. */
    private String[] textosCabecera = {"Consecutivo", "PST", "Clave Servicio", "Fecha Reporte", "Total Asignadas",
            "Total Activas", "% Activas"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pListaDetalle reporte
     */
    public ExportConsultaLineasActivasNng(List<DetalleReporteNng> pListaDetalle) {

        this.listaDetalle = pListaDetalle;
        workBook = new SXSSFWorkbook(5000);
        workBook.setCompressTempFiles(true);
    }

    @Override
    public SXSSFWorkbook getLibroExcel() {
        return workBook;
    }

    @Override
    public ExcelCabeceraInfo getCabecera() {
        ExcelCabeceraInfo ehi = new ExcelCabeceraInfo();
        ehi.setTitulos(textosCabecera);
        ehi.setCellFont(ExportarExcel.ARIAL_12_BOLD);
        ehi.setCellStyle(ExportarExcel.ESTILO_GENERAL_CENTRADO);
        ehi.setInmovilizarCabecera(true);

        return ehi;
    }

    @Override
    public ArrayList<ExcelCeldaInfo[]> getContenido() {

        ArrayList<ExcelCeldaInfo[]> contenido = new ArrayList<ExcelCeldaInfo[]>();
        ExcelCeldaInfo[] contenidoFila = null;
        ExcelCeldaInfo celda = null;

        for (DetalleReporteNng detalle : listaDetalle) {
            // Nueva Fila
            int contSerie = 0;
            contenidoFila = this.nuevaFila(contenido);

            celda = new ExcelCeldaInfo(detalle.getConsecutivo().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getProveedor().getNombre(), ExportarExcel.ESTILO_GENERAL_LEFT, null,
                    true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getClaveServicio().getCodigo().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(FechasUtils.fechaToString(detalle.getFechaReporte()), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(
                    detalle.getTotalAsignadas() != null ? detalle.getTotalAsignadas().toString() : "",
                    null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getTotalLineasActivas() != null ? detalle.getTotalLineasActivas()
                    .toString() : "", null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getPorcentajeUso(), null, null, true);
            contenidoFila[contSerie++] = celda;

        }

        return contenido;
    }

    /**
     * Agrega una nueva fila al contenedor de filas.
     * @param pConteneder Contenedor de filas
     * @return nueva fila para ser editada
     */
    private ExcelCeldaInfo[] nuevaFila(ArrayList<ExcelCeldaInfo[]> pConteneder) {
        ExcelCeldaInfo[] contenidoFila = new ExcelCeldaInfo[textosCabecera.length];
        pConteneder.add(contenidoFila);
        return contenidoFila;
    }

    /**
     * @return the listaDetalle
     */
    public List<DetalleReporteNng> getListaDetalle() {
        return listaDetalle;
    }

    /**
     * @param listaDetalle the listaDetalle to set
     */
    public void setListaDetalle(List<DetalleReporteNng> listaDetalle) {
        this.listaDetalle = listaDetalle;
    }
}
