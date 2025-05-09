package mx.ift.sns.web.backend.ng.arrendamientos.tabs;

import java.util.ArrayList;

import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.negocio.ng.model.RangoNoAsignadoError;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.arrendamientos.PnnAbdBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pestaña con los errores en la validacion de los ficheros de arrendatarios y arrendadores.
 */
public class ErroresComparacionTab extends TabWizard {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ErroresComparacionTab.class);

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_PnnABD";

    /**
     * CargarFicheroTab.
     * @param pWizard pWizard.
     * @param pNgService pNgService.
     */
    public ErroresComparacionTab(Wizard pWizard, INumeracionGeograficaService pNgService) {

        this.setId("Generación plan ABD - Comparación");
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);
        setIdMensajes(MSG_ID);
    }

    @Override
    public boolean isAvanzar() {
        PnnAbdBean parent = (PnnAbdBean) getWizard();

        boolean valor = parent.getListaErroresComparacion().isEmpty();
        if (!valor) {
            setSummaryError("Existen errores en la comparación de los archivos. No es posible continuar.");
        }

        LOGGER.debug("avanzar {}", valor);
        return valor;
    }

    @Override
    public void actualizaCampos() {
    }

    @Override
    public void resetTab() {
        PnnAbdBean parent = (PnnAbdBean) getWizard();
        parent.setListaErroresRangoPnn(new ArrayList<RangoNoAsignadoError>());
    }

    /**
     * Getter del número de registros por página a mostrar.
     * @return registroPorPagina a mostrar
     */
    public int getRegistroPorPagina() {
        return registroPorPagina;
    }

    /**
     * Setter del número de registros por página a mostrar.
     * @param registroPorPagina a establecer
     */
    public void setRegistroPorPagina(int registroPorPagina) {
        this.registroPorPagina = registroPorPagina;
    }

}
