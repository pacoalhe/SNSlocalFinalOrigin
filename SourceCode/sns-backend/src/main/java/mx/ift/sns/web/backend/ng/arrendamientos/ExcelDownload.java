package mx.ift.sns.web.backend.ng.arrendamientos;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Descarga de excell.
 */
@ManagedBean(name = "excel")
@ViewScoped
public class ExcelDownload implements Serializable {

    /** UID Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDownload.class);

    /**
     * prepara excel.
     * @param document excel
     */
    public void preProcessXLS(Object document) {

        LOGGER.debug("download1");
    }

    /**
     * despues de generar el excel.
     * @param document excel
     */
    public void postProcessXLS(Object document) {

        LOGGER.debug("download2");

        /*
         * HSSFWorkbook wb = (HSSFWorkbook) document; HSSFSheet sheet = wb.getSheetAt(0); HSSFRow header =
         * sheet.getRow(0); HSSFCellStyle cellStyle = wb.createCellStyle();
         * cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
         * cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); for (int i = 0; i <
         * header.getPhysicalNumberOfCells(); i++) { HSSFCell cell = header.getCell(i); cell.setCellStyle(cellStyle); }
         */

    }
}
