package mx.ift.sns.web.backend.ac.municipio;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.Region;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Managed Bean de la edición de muncipios. */
@ManagedBean(name = "editarMunicipioBean")
@ViewScoped
public class EditarMunicipioBean implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditarMunicipioBean.class);

    /** Servicio de Administracion de catalogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** ID mensaje. */
    private static final String MSG_ID = "MSG_NuevoMunicipio";

    /** Lista de estados. */
    private List<Estado> listaEstados;

    /** Lista regiones celular. */
    private List<Region> listaRegionesCelular;

    /** Lista regiones CPP. */
    private List<Region> listaRegionesPcs;

    /** Municipio a editar. */
    private Municipio municipio;

    /** Id municipio anterior. */
    private MunicipioPK idMuncipioAnt;

    /**
     * Iniciamos las listas de estado y region.
     */
    @PostConstruct
    public void init() {

        municipio = new Municipio();
        municipio.setId(new MunicipioPK());

        idMuncipioAnt = new MunicipioPK();

        try {
            listaEstados = adminCatFacadeService.findAllEstados();
            listaRegionesCelular = adminCatFacadeService.findAllRegiones();
            listaRegionesPcs = adminCatFacadeService.findAllRegiones();
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }

    }

    /**
     * Guarda las modificaciones del municipio.
     */
    public void guardar() {
        try {
            // Seteamos el codigo del estado elegido al id.
            municipio.getId().setCodEstado(municipio.getEstado().getCodEstado());
            // Si no cambiamos la clave del muncipio y el estado guardamos
            if (municipio.getId().equals(idMuncipioAnt)) {
                municipio = adminCatFacadeService.saveMunicipio(municipio);
                MensajesBean.addInfoMsg(MSG_ID, "Municipio guardado correctamente", "");
            } else {
                // Si se han cambiado la clave de muncipio y el estado comprobamos si ya existe el id y que
                boolean existeId = adminCatFacadeService.getMunicipioByEstado(municipio.getEstado().getCodEstado(),
                        municipio.getId().getCodMunicipio());
                if (!existeId) {
                    municipio = adminCatFacadeService.saveMunicipio(municipio);

                    // Eliminamos el registro de municipio con el id anterior
                    Municipio municipioAnt = adminCatFacadeService.findMunicipioById(idMuncipioAnt);
                    adminCatFacadeService.removeMunicipio(municipioAnt);
                    idMuncipioAnt = municipio.getId();
                    MensajesBean.addInfoMsg(MSG_ID, "Municipio guardado correctamente", "");
                } else {
                    MensajesBean
                            .addErrorMsg(
                                    MSG_ID,
                                    "La Clave Municipio ya está registrada para el estado seleccionado."
                                            + " Por favor, elija otra o mantenga la clave de municipio y"
                                            + " el estado originales.", "");
                }

            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Restea los datos.
     */
    public void resetTab() {

        municipio = new Municipio();
        municipio.setId(new MunicipioPK());

        idMuncipioAnt = new MunicipioPK();

    }

    /**
     * Obtiene Lista de estados.
     * @return List<Estado>
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Carga Lista de estados.
     * @param listaEstados List<Estado>
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Obtiene Lista regiones celular.
     * @return List<Region>
     */
    public List<Region> getListaRegionesCelular() {
        return listaRegionesCelular;
    }

    /**
     * Carga Lista regiones celular.
     * @param listaRegionesCelular List<Region>
     */
    public void setListaRegionesCelular(List<Region> listaRegionesCelular) {
        this.listaRegionesCelular = listaRegionesCelular;
    }

    /**
     * Obtiene Lista regiones CPP.
     * @return List<Region>
     */
    public List<Region> getListaRegionesPcs() {
        return listaRegionesPcs;
    }

    /**
     * Carga Lista regiones CPP.
     * @param listaRegionesPcs List<Region>
     */
    public void setListaRegionesPcs(List<Region> listaRegionesPcs) {
        this.listaRegionesPcs = listaRegionesPcs;
    }

    /**
     * Obtiene Municipio a editar.
     * @return Municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Carga Municipio a editar.
     * @param municipio Municipio
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Obtiene Id municipio anterior.
     * @return MunicipioPK
     */
    public MunicipioPK getIdMuncipioAnt() {
        return idMuncipioAnt;
    }

    /**
     * Carga Id municipio anterior.
     * @param idMuncipioAnt MunicipioPK
     */
    public void setIdMuncipioAnt(MunicipioPK idMuncipioAnt) {
        this.idMuncipioAnt = idMuncipioAnt;
    }

}
