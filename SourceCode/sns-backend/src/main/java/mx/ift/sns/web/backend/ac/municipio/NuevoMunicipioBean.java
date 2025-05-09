package mx.ift.sns.web.backend.ac.municipio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.Region;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Managed Bean para nuevos municipios. */
@ManagedBean(name = "nuevoMunicipioBean")
@ViewScoped
public class NuevoMunicipioBean implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NuevoMunicipioBean.class);

    /** Servicio de Administracion de catalogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** ID mensaje. */
    private static final String MSG_ID = "MSG_NuevoMunicipio";

    /** Lista de estados. */
    private List<Estado> listaEstados;

    /** Lista regiones celular. */
    private List<Region> listaRegionesCelular;

    /** Lista regiones PCS. */
    private List<Region> listaRegionesPcs;

    /** Nuevo municipio. */
    private Municipio municipio;

    /** Lista de nuevos municipios. */
    private List<Municipio> listaMunicipios;

    /**
     * Iniciamos las listas de estado y region.
     */
    @PostConstruct
    public void init() {

        municipio = new Municipio();
        municipio.setId(new MunicipioPK());

        listaMunicipios = new ArrayList<Municipio>();

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
     * Agrega un municipio nuevo a la lista.
     */
    public void agregar() {
        if (isNumeric(municipio.getId().getCodMunicipio())) {

            if (!adminCatFacadeService.getMunicipioByEstado(municipio.getEstado().getCodEstado(), municipio.getId()
                    .getCodMunicipio())) {
                // Seteamos el nuevo municipio a uno auxiliar
                Municipio nuevoMunicipio = new Municipio();
                nuevoMunicipio.setId(new MunicipioPK());
                nuevoMunicipio.getId().setCodEstado(municipio.getEstado().getCodEstado());
                nuevoMunicipio.getId().setCodMunicipio(municipio.getId().getCodMunicipio());
                nuevoMunicipio.setEstado(municipio.getEstado());
                nuevoMunicipio.setNombre(municipio.getNombre());
                nuevoMunicipio.setRegionCelular(municipio.getRegionCelular());
                nuevoMunicipio.setRegionPcs(municipio.getRegionPcs());
                Estatus status = new Estatus();
                status.setCdg(Estatus.ACTIVO);
                nuevoMunicipio.setEstatus(status);

                listaMunicipios.add(nuevoMunicipio);

                municipio = new Municipio();
                municipio.setId(new MunicipioPK());
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        "La Clave Municipio ya está registrada para el estado seleccionado. Por favor, elija otra.");
            }
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "El campo Clave Municipio no es númerico");
        }
    }

    /**
     * Elimina un municipio de la tabla.
     * @param dato municipio
     */
    public void eliminarMunicipio(Municipio dato) {

        listaMunicipios.remove(dato);
    }

    /**
     * Guarda los nuevos municipios.
     */
    public void guardar() {
        try {

            adminCatFacadeService.guardaMunicipios(listaMunicipios);

            String nuevaLinea = "<br>";
            StringBuilder sbf = new StringBuilder("Se han guardado los siguientes municipios:");
            sbf.append(nuevaLinea);

            for (Municipio dato : listaMunicipios) {
                sbf.append("Para el estado ").append(dato.getEstado().getNombre()).append(" con codigo ")
                        .append(dato.getEstado().getCodEstado()).append(" el municipio ").append(dato.getNombre())
                        .append(" con codigo ").append(dato.getId().getCodMunicipio()).append(nuevaLinea);
            }

            MensajesBean.addInfoMsg(MSG_ID, sbf.toString());

            resetTab();

        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error al intentar guardar los municipios seleccionados");
        }
    }

    /**
     * Restea los datos.
     */
    public void resetTab() {
        municipio = new Municipio();
        municipio.setId(new MunicipioPK());

        listaMunicipios = new ArrayList<Municipio>();
    }

    /**
     * Método para saber si es numerica la cadena.
     * @param cadena cadena de entrada.
     * @return boolean true/false.
     */
    private static boolean isNumeric(String cadena) {
        return cadena.matches("\\d*");

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
     * Obtiene Lista regiones PCS.
     * @return List<Region>
     */
    public List<Region> getListaRegionesPcs() {
        return listaRegionesPcs;
    }

    /**
     * carga Lista regiones PCS.
     * @param listaRegionesPcs List<Region>
     */
    public void setListaRegionesPcs(List<Region> listaRegionesPcs) {
        this.listaRegionesPcs = listaRegionesPcs;
    }

    /**
     * Obtiene Nuevo municipio.
     * @return Municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Carga Nuevo municipio.
     * @param municipio Municipio
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Obtiene Lista de nuevos municipios.
     * @return List<Municipio>
     */
    public List<Municipio> getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Carga Lista de nuevos municipios.
     * @param listaMunicipios List<Municipio>
     */
    public void setListaMunicipios(List<Municipio> listaMunicipios) {
        this.listaMunicipios = listaMunicipios;
    }

}
