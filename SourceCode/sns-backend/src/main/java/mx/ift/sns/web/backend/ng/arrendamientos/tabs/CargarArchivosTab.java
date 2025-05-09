package mx.ift.sns.web.backend.ng.arrendamientos.tabs;

import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.arrendamientos.PnnAbdBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Carga de los ficheros de arrendadores y arrendatarios.
 */
public class CargarArchivosTab extends TabWizard {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CargarArchivosTab.class);

    /**
     * CargarFicheroTab.
     * @param pWizard pWizard.
     * @param pNgService pNgService.
     */
    public CargarArchivosTab(Wizard pWizard, INumeracionGeograficaService pNgService) {

        this.setId("Generación plan ABD - Carga archivos");
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);
    }

    @Override
    public void resetTab() {
        PnnAbdBean parent = (PnnAbdBean) getWizard();
        parent.setFileNameArrendador("");
        parent.setFileNameArrendatario("");

        parent.setUploadedFileArrendador(null);
        parent.setUploadedFileArrendatario(null);
        parent.setValidados(false);
    }

    @Override
    public boolean isAvanzar() {

        PnnAbdBean parent = (PnnAbdBean) getWizard();
        boolean valor = (parent.getUploadedFileArrendador() != null) && (parent.getFileNameArrendatario() != null)
                && parent.isValidados();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("isCompletado  {}", valor);
        }

        if (valor == false) {
            setSummaryError("Necesita cargar los dos ficheros y validarlos");
            LOGGER.debug("error");
        }

        return valor;
    }

    @Override
    public void actualizaCampos() {

    }
}
