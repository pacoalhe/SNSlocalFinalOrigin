package mx.ift.sns.negocio.ng;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.utils.number.NumerosUtils;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información de la consulta sobre lineas activas. */
public class ExportConsultaLineasActivasNg implements IExportarExcel {

    /** Lista de detalles de reportes. */
    private List<DetalleReporte> listaDetalle;

    /** Cabecera del Excel de Consulta de lineas activas. */
    private String[] textosCabecera = {"Consecutivo", "PST", "Estado", "Municipio", "Población", "ABN", "Fecha",
            "Total Asignadas", "Total Activas", "% Activas", "Total Fijas Asignadas", "Total Fijas Activas",
            " % Fijas Asignadas", "Total Movil Asignadas", "Total Movil Activa", "% Movil Asignada", "Asignada CPP",
            "Activa CPP", "% asignada CPP", "Asignada MPP", "Activa MPP", "% Asignada MPP"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pListaDetalle reporte
     */
    public ExportConsultaLineasActivasNg(List<DetalleReporte> pListaDetalle) {

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

        for (DetalleReporte detalle : listaDetalle) {
            // Nueva Fila
            int contSerie = 0;
            contenidoFila = this.nuevaFila(contenido);

            celda = new ExcelCeldaInfo(detalle.getConsecutivo().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getProveedor().getNombre(), ExportarExcel.ESTILO_GENERAL_LEFT, null,
                    true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getPoblacion().getMunicipio().getEstado().getNombre(),
                    ExportarExcel.ESTILO_GENERAL_LEFT, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getPoblacion().getMunicipio().getNombre(),
                    ExportarExcel.ESTILO_GENERAL_LEFT, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getPoblacion().getNombre(), ExportarExcel.ESTILO_GENERAL_LEFT, null,
                    true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getPoblacion().getAbn().getCodigoAbn().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(FechasUtils.fechaToString(detalle.getFechaReporte()), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getTotalAsignadas().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getTotalLineasActivas().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(NumerosUtils.calcularPorcentajeAsString(detalle.getTotalLineasActivas(),
                    detalle.getTotalAsignadas()), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getTotalAsignadasFijas().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getTotalLineasActivasFijas().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(NumerosUtils.calcularPorcentajeAsString(detalle.getTotalLineasActivasFijas(),
                    detalle.getTotalAsignadasFijas()), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getTotalAsignadasCpp().add(detalle.getTotalAsignadasMpp()).toString(),
                    null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getTotalLineasActivasCpp().add(detalle.getTotalLineasActivasMpp())
                    .toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(NumerosUtils.calcularPorcentajeAsString(
                    detalle.getTotalLineasActivasCpp().add(detalle.getTotalLineasActivasMpp()),
                    detalle.getTotalAsignadasCpp().add(detalle.getTotalAsignadasMpp())), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getTotalAsignadasCpp().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getTotalLineasActivasCpp().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(NumerosUtils.calcularPorcentajeAsString(detalle.getTotalLineasActivasCpp(),
                    detalle.getTotalAsignadasCpp()), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getTotalAsignadasMpp().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(detalle.getTotalLineasActivasMpp().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(NumerosUtils.calcularPorcentajeAsString(detalle.getTotalLineasActivasMpp(),
                    detalle.getTotalAsignadasMpp()), null, null, true);
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
    public List<DetalleReporte> getListaDetalle() {
        return listaDetalle;
    }

    /**
     * @param listaDetalle the listaDetalle to set
     */
    public void setListaDetalle(List<DetalleReporte> listaDetalle) {
        this.listaDetalle = listaDetalle;
    }
}
