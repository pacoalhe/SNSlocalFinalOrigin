package mx.ift.sns.web.backend.ng.arrendamientos.tabs;

import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resumen de los ficheros.
 */
public class ResumenTab extends TabWizard {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResumenTab.class);

    /**
     * CargarFicheroTab.
     * @param pWizard pWizard.
     * @param pNgService pNgService.
     */
    public ResumenTab(Wizard pWizard, INumeracionGeograficaService pNgService) {

        this.setId("Generación plan ABD - Resumen");

        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);
    }

    @Override
    public boolean isAvanzar() {
        boolean valor = true;

        LOGGER.debug("completado {}", valor);
        return valor;
    }

    @Override
    public void actualizaCampos() {
    }

    @Override
    public void resetTab() {
    }
}
