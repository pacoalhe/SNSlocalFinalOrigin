package mx.ift.sns.web.backend.ng.arrendamientos.tabs;

import java.util.ArrayList;

import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.negocio.ng.model.FormatoError;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.arrendamientos.PnnAbdBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Pestaña con los errores en la validacion de formtato ficheros de arrendatarios y arrendadores. */
public class ErroresFormatoTab extends TabWizard {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ErroresFormatoTab.class);

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_PnnABD";

    /**
     * CargarFicheroTab.
     * @param pWizard pWizard.
     * @param pNgService pNgService.
     */
    public ErroresFormatoTab(Wizard pWizard, INumeracionGeograficaService pNgService) {

        this.setId("Planes ABD - ErroresFormatoTab");
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);
        setIdMensajes(MSG_ID);
    }

    @Override
    public boolean isAvanzar() {
        PnnAbdBean parent = (PnnAbdBean) getWizard();
        //Se valida si se tienen errores en formato
        //Si es asi no se debe permitir avanzar hasta corregir los errores
        boolean valor = parent.getListaErroresFormato().isEmpty();
        if (!valor) {
            setSummaryError("Existen errores de formato en los archivos. No es posible continuar.");
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
        parent.setListaErroresFormato(new ArrayList<FormatoError>());
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
