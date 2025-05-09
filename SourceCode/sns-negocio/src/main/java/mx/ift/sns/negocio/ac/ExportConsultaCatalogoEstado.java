package mx.ift.sns.negocio.ac;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Genera un fichero Excel con la información de la consulta sobre el Catálogo de Estados y Municipios. */
public class ExportConsultaCatalogoEstado implements IExportarExcel {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportConsultaCatalogoEstado.class);

    /** Servicio de Adminstración de Catalogos. */
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Lista de Estados. */
    private List<Estado> listaEstado;

    /** Cabecera del Excel de Catálogos de Estados y Municipios. */
    private String[] textosCabecera = {"Código Estado", "Nombre Estado", "Abreviatura Estado", "Capital Estado",
            "Código Municipio", "Nombre Municipio"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pListaEstado Lista de Estado
     * @param pAdminCatFacadeService facade
     */
    public ExportConsultaCatalogoEstado(List<Estado> pListaEstado, IAdminCatalogosFacade pAdminCatFacadeService) {
        listaEstado = pListaEstado;
        adminCatFacadeService = pAdminCatFacadeService;
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
        ehi.setInmovilizarCabecera(true);

        return ehi;
    }

    @Override
    public ArrayList<ExcelCeldaInfo[]> getContenido() {

        ArrayList<ExcelCeldaInfo[]> contenido = new ArrayList<ExcelCeldaInfo[]>();
        ExcelCeldaInfo[] contenidoFila = null;
        ExcelCeldaInfo celda = null;

        int contEstado = 0;
        int contMunicipio = 0;

        for (Estado estado : listaEstado) {
            // Nueva Fila
            contEstado = 0;
            contenidoFila = this.nuevaFila(contenido);

            celda = new ExcelCeldaInfo(estado.getCodEstado(), ExportarExcel.ESTILO_LG,
                    ExportarExcel.ARIAL_11_BOLD, true);
            contenidoFila[contEstado++] = celda;

            // Nombre
            celda = new ExcelCeldaInfo(estado.getNombre(), null, null, false);
            contenidoFila[contEstado++] = celda;

            // Abreviatura
            celda = new ExcelCeldaInfo(estado.getAbreviatura(), null, null, false);
            contenidoFila[contEstado++] = celda;

            // Capital
            celda = new ExcelCeldaInfo(estado.getCapital(), null, null, false);
            contenidoFila[contEstado++] = celda;

            List<Municipio> municipios;
            try {
                municipios = adminCatFacadeService.findMunicipiosByEstado(estado.getCodEstado());

                if (null != municipios && municipios.size() > 0) {
                    for (Municipio municipio : municipios) {
                        // Modelos de la Marca -> Nueva Fila
                        contMunicipio = contEstado;
                        contenidoFila = this.nuevaFila(contenido);

                        // Codigo
                        celda = new ExcelCeldaInfo(municipio.getId().getCodMunicipio(), null, null, false);
                        contenidoFila[contMunicipio++] = celda;

                        // Nombre
                        celda = new ExcelCeldaInfo(municipio.getNombre(), null, null, false);
                        contenidoFila[contMunicipio++] = celda;
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error en la recuperacion de Municipios del Estado:"
                        + estado.getCodEstado() + e.toString());
            }

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

}
