package mx.ift.sns.web.frontend.areas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.web.frontend.common.MensajesFrontBean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean que controla la información mostrada al realizar una consulta en la vista del mapa de detalle de un estado.
 * @author X51461MO
 */
@ManagedBean(name = "abnInfoBean")
@ViewScoped
public class AbnInfoBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;
    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaFront";
    /** Bean AreasGeoNumeracionMainBean. */
    @ManagedProperty(value = "#{areasGeoNumeracionMainBean}")
    private AreasGeoNumeracionMainBean areasGeoNumeracionMainBean;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbnInfoBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Campo población buscada. */
    private Poblacion poblacion;

    /** Lista de poblaciones. */
    private List<Poblacion> poblaciones;

    /** Flag comprobacion input poblacion activado. */
    private boolean populationInputActivated = true;

    /** Flag comprobacion boton formulario activado. */
    private boolean formButtonActivated = false;

    /** Flag para renderizado de tabla poblacion. */
    private boolean populationsTableActivated = false;

    /** Abn. */
    private Abn abn;

    /**
     * Lista de municipios del abn.
     */
    private List<Municipio> municipiosAbn;

    /**
     * Poblacion con mayor numeracion asignada.
     */
    private Poblacion poblacionAbnMaxNumAsignada;
    /**
     * Nir seleccionado de la lista.
     */
    private String nirSelected;

    /**
     * Flag para activar la tabla de info municipio.
     */
    private boolean tableMunicipioActivated;

    /**
     * Flag de activación de la tabla e proveedores/estado.
     */
    private boolean tablaProvEstadoActivated = false;
    /**
     * Flag de activación de la tabla de proveedores/ abn.
     */
    private boolean tablaProvAbnActivated = false;

    /**
     * Flag de activación de la tabla población / municipio.
     */
    private boolean tablaPoblacionesMunicipioActivated = false;
    /**
     * Flag de activación de la tabla población / estado.
     */
    private boolean tablaPoblacionesEstadoActivated = false;
    /**
     * Flag de activación de la tabla poblacionAbn.
     */
    private boolean tablaPoblacionesAbnActivated = false;
    /**
     * Flag de activación de la tabla InfoArea.
     */
    private boolean tablaInfoAreaActivated = false;
    /**
     * Flag de activacion de boton activar búsqueda abn.
     */
    private boolean btnActiveAbn = false;
    /**
     * Flag de activacion de boton activar búsqueda municipio.
     */
    private boolean btnActiveMunicipio = false;
    /**
     * Flag para activar el botón submit 1.
     */
    private boolean submit1Activate = true;
    /**
     * Flag para activar el botón submit 2.
     */
    private boolean submit2Activate = true;

    /**
     * Presuscripción en formato String Sí o No.
     */
    private String presuscripcionAbnFormat;
    /**
     * Activa la tabla de búsqueda por nir del mapa.
     */
    private boolean tablaInfoNirMapa;
    /**
     * Proveedores numeracion Nir.
     */
    private List<Proveedor> proveedorNumeracionNir;

    /** Poblaciones numeración asignada por Nir. */
    private List<Poblacion> poblacionesNumeracionNir;

    /** Numeración asignada Nir en formato número USA. */
    private String numeracionNirFormat;

    /** Número de dígitos de número interno en función de la longitud del NIR. */
    private Integer numeroDigitos;

    /** Municipio buscado. **/
    private Municipio municipio;

    /** Lista de proveedores que prestan servicio en el municipio buscado. */
    private List<Proveedor> proveedorMunicipio;

    /** Lista de poblaciones con numeración asignada del municipio buscado. */
    private List<Poblacion> poblacionesMunicipio;

    /** Numeración asignada en el municipio buscado y en formato número USA. */
    private String numeracionMunicipioFormat;

    /** Inegi Municipio. */
    private String inegiMunicipio;

    /**
     * Nir seleccionado.
     */
    private Nir nir;

    // ///////////////////////MÉTODOS////////////////////////

    /** Comprueba si algun campo esta relleno y deshabilita o habilita inputs y boton. */

    public void checkFormStatus() {
        try {
            if (poblacion != null && poblacion.getNombre() != null) {
                if (StringUtils.isNotEmpty(poblacion.getNombre())) {
                    setPopulationInputActivated(true);
                    setFormButtonActivated(true);
                }

            }
            if ((poblacion != null && poblacion.getNombre() != null
                    && poblacion.getNombre().equals("") || poblacion != null
                    && poblacion.getNombre() == null)) {
                setPopulationInputActivated(true);
                setPopulationsTableActivated(false);
                setPopulationsTableActivated(false);
                setFormButtonActivated(false);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }

    }

    /**
     * Método que se ejecuta al hacer click en el botón buscar de la búsqueda por ABN. Setea la información relacionada
     * con el nir buscado para mostrarlo en dialog correspondiente.
     */

    public void searchLocalInfoWithNir() {
        try {
            areasGeoNumeracionMainBean.setSubmit1Activate(true);
            this.setBtnActiveAbn(false);
            String codigoNirSeleccionado = "";
            codigoNirSeleccionado = areasGeoNumeracionMainBean.getNirSelected();
            if (codigoNirSeleccionado != null) {
                this.setMunicipio(areasGeoNumeracionMainBean.getMunicipio());
                this.setProveedorMunicipio(areasGeoNumeracionMainBean.getProveedorMunicipio());
                this.setPoblacionesMunicipio(areasGeoNumeracionMainBean.getPoblacionesMunicipio());
                this.setNumeracionMunicipioFormat(areasGeoNumeracionMainBean.getNumeracionMunicipioFormat());
                this.setInegiMunicipio(areasGeoNumeracionMainBean.getInegiMunicipio());
                this.setNirSelected(codigoNirSeleccionado);
                this.chequeaNumeroDigitos(codigoNirSeleccionado);
                this.setTablaInfoAreaActivated(true);
                this.setTablaProvAbnActivated(true);
                this.setTableMunicipioActivated(false);
                this.setTablaProvEstadoActivated(false);
                this.setTablaInfoNirMapa(false);
                Nir nirSeleccionado = ngPublicService.getNirByCodigo(Integer.valueOf(codigoNirSeleccionado));
                this.setNir(nirSeleccionado);
                this.setAbn(ngPublicService.getAbnByCodigoNir(codigoNirSeleccionado));
                if (this.abn.getPresuscripcion() != null) {
                    if (this.abn.getPresuscripcion().compareTo("P") == 0) {
                        this.setPresuscripcionAbnFormat("Sí");
                    } else {
                        this.setPresuscripcionAbnFormat("No");
                    }
                } else {
                    this.setPresuscripcionAbnFormat("No");
                }
                this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(this
                        .getAbn()));
                this.setPoblacionesNumeracionNir(
                        ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(nirSeleccionado));
                BigDecimal bgNum = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(nirSeleccionado));
                this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bgNum));
                this.setProveedorNumeracionNir(ngPublicService.findAllPrestadoresServicioBy(nirSeleccionado, null, null,
                        null, null));
                this.setMunicipiosAbn(ngPublicService.findAllMunicipiosByNir(nirSeleccionado, true));
            }
        } catch (Exception e) {
            LOGGER.error("Error en la búsqueda de datos" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
    }

    /**
     * Método que se ejecuta al hacer click en el botón buscar de la búsqueda por Municipio. Setea la información
     * relacionada con el nir buscado para mostrarlo en dialog correspondiente.
     */
    public void searchNirMunicipio() {
        areasGeoNumeracionMainBean.setSubmit2Activate(true);
        this.searchLocalInfoWithNir();
        this.setBtnActiveMunicipio(false);
        this.setTableMunicipioActivated(true);

    }

    /**
     * Crea la clave inegi de un municipio a partir de su ID. Añade ceros delante en función de la longitud de su id.
     * @param idMunicipio MunicipioPK
     * @return String
     */
    public String createInegiMunicipio(MunicipioPK idMunicipio) {
        String codMunicipio = "";
        String codEstado = "";
        String inegiMunicipio = "";
        try {
            if (idMunicipio != null) {
                codEstado = idMunicipio.getCodEstado();
                codMunicipio = idMunicipio.getCodMunicipio();

                codEstado = (codEstado.length() == 1) ? "0" + codEstado : codEstado;

                if (codMunicipio.length() == 1) {
                    codMunicipio = "00" + codMunicipio;
                } else if (codMunicipio.length() == 2) {
                    codMunicipio = "0" + codMunicipio;
                }
                inegiMunicipio = codEstado + codMunicipio;
            }
        } catch (Exception e) {
            LOGGER.error("Error al crear el inegi del municipio" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }

        return inegiMunicipio;

    }

    /**
     * Activa la tabla de poblacipones/estado.
     */
    public void activateTablePoblacionesEstado() {
        this.setTablaPoblacionesEstadoActivated(true);
        this.setTablaPoblacionesMunicipioActivated(false);
        this.setTablaPoblacionesAbnActivated(false);

    }

    /**
     * Convierte a formato número USA un número. Ejemplo de formato USA: 1,000,000.
     * @param num BigDecimal
     * @return String
     */
    public String formatoNumeracionAsignada(BigDecimal num) {
        String numeroStr = "";
        try {
            NumberFormat numFormato = NumberFormat.getNumberInstance(Locale.US);
            numeroStr = numFormato.format(num);
        } catch (Exception e) {
            LOGGER.error("Error al crear el formato de la numeración asginada" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return numeroStr;
    }

    /**
     * Setea la información del nir correspondiente a la área del mapa de detalle de un estado donde se hace click.
     * @param idNir String
     */
    public void mapaNir(String idNir) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Pinchando en el area con nir{}", idNir);
        }
        BigDecimal bg = new BigDecimal(idNir);
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Seteando la información a mostrar");
            }
            Nir nirArea = ngPublicService.getNirById(bg);
            if (nirArea != null) {
                this.setNir(nirArea);
                this.setNirSelected(String.valueOf(nirArea.getCodigo()));
                this.chequeaNumeroDigitos(this.getNirSelected());
                this.setTablaInfoNirMapa(true);
                this.setTablaInfoAreaActivated(false);
                this.setTableMunicipioActivated(false);
                this.setAbn(ngPublicService.getAbnByCodigoNir(this.getNirSelected()));
                if (this.abn.getPresuscripcion() != null) {
                    if (this.abn.getPresuscripcion().compareTo("P") == 0) {
                        this.setPresuscripcionAbnFormat("Sí");
                    } else {
                        this.setPresuscripcionAbnFormat("No");
                    }
                } else {
                    this.setPresuscripcionAbnFormat("No");
                }
                this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(this
                        .getAbn()));
                this.setPoblacionesNumeracionNir(
                        ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(nirArea));
                this.setMunicipiosAbn(ngPublicService.findAllMunicipiosByNir(nirArea, true));
                BigDecimal bgNum = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(nirArea));
                this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bgNum));
                this.setProveedorNumeracionNir(ngPublicService.findAllPrestadoresServicioBy(nirArea, null, null,
                        null, null));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Toda  la información ha sido seteada con éxito");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error en la consulta de datos al pinchar sobre un área del mapa" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error al buscar la información en la base de datos");
        }

    }

    /**
     * Devuelve el número de dígitos según el nir.
     * @param codigoNir String
     */
    private void chequeaNumeroDigitos(String codigoNir) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Nir: {} y length {}", codigoNir, codigoNir.length());
        }
        switch (codigoNir.length()) {
        case 1:
            this.setNumeroDigitos(9);
            break;
        case 2:
       //     this.setNumeroDigitos(8);
            this.setNumeroDigitos(10);
            break;
        case 3:
       //     this.setNumeroDigitos(7);
            this.setNumeroDigitos(10);
            break;
        }
    }

    // /////////////// GETTERS Y SETTERS ///////////////////////

    /**
     * Flag de activacion de boton activar búsqueda abn.
     * @return the btnActiveAbn
     */
    public boolean isBtnActiveAbn() {
        return btnActiveAbn;
    }

    /**
     * Flag de activacion de boton activar búsqueda abn.
     * @param btnActiveAbn the btnActiveAbn to set
     */
    public void setBtnActiveAbn(boolean btnActiveAbn) {
        this.btnActiveAbn = btnActiveAbn;
    }

    /**
     * Proveedores numeracion Nir.
     * @return the proveedorNumeracionNir
     */
    public List<Proveedor> getProveedorNumeracionNir() {
        return proveedorNumeracionNir;
    }

    /**
     * Proveedores numeracion Nir.
     * @param proveedorNumeracionNir the proveedorNumeracionNir to set
     */
    public void setProveedorNumeracionNir(List<Proveedor> proveedorNumeracionNir) {
        this.proveedorNumeracionNir = proveedorNumeracionNir;
    }

    /**
     * Poblaciones numeración asignada por Nir.
     * @return the poblacionesNumeracionNir
     */
    public List<Poblacion> getPoblacionesNumeracionNir() {
        return poblacionesNumeracionNir;
    }

    /**
     * Poblaciones numeración asignada por Nir.
     * @param poblacionesNumeracionNir the poblacionesNumeracionNir to set
     */
    public void setPoblacionesNumeracionNir(List<Poblacion> poblacionesNumeracionNir) {
        this.poblacionesNumeracionNir = poblacionesNumeracionNir;
    }

    /**
     * Numeración asignada Nir en formato número USA.
     * @return the numeracionNirFormat
     */
    public String getNumeracionNirFormat() {
        return numeracionNirFormat;
    }

    /**
     * Numeración asignada Nir en formato número USA.
     * @param numeracionNirFormat the numeracionNirFormat to set
     */
    public void setNumeracionNirFormat(String numeracionNirFormat) {
        this.numeracionNirFormat = numeracionNirFormat;
    }

    /**
     * Activa la tabla de búsqueda por nir del mapa.
     * @return the tablaInfoNirMapa
     */
    public boolean isTablaInfoNirMapa() {
        return tablaInfoNirMapa;
    }

    /**
     * Activa la tabla de búsqueda por nir del mapa.
     * @param tablaInfoNirMapa the tablaInfoNirMapa to set
     */
    public void setTablaInfoNirMapa(boolean tablaInfoNirMapa) {
        this.tablaInfoNirMapa = tablaInfoNirMapa;
    }

    /**
     * Presuscripción en formato String Sí o No.
     * @return the presuscripcionAbnFormat
     */
    public String getPresuscripcionAbnFormat() {
        return presuscripcionAbnFormat;
    }

    /**
     * Presuscripción en formato String Sí o No.
     * @param presuscripcionAbnFormat the presuscripcionAbnFormat to set
     */
    public void setPresuscripcionAbnFormat(String presuscripcionAbnFormat) {
        this.presuscripcionAbnFormat = presuscripcionAbnFormat;
    }

    /**
     * @return the areasGeoNumeracionMainBean
     */
    public AreasGeoNumeracionMainBean getAreasGeoNumeracionMainBean() {
        return areasGeoNumeracionMainBean;
    }

    /**
     * @param areasGeoNumeracionMainBean the areasGeoNumeracionMainBean to set
     */
    public void setAreasGeoNumeracionMainBean(AreasGeoNumeracionMainBean areasGeoNumeracionMainBean) {
        this.areasGeoNumeracionMainBean = areasGeoNumeracionMainBean;
    }

    /**
     * Flag para activar el botón submit 1.
     * @return the submit1Activate
     */
    public boolean isSubmit1Activate() {
        return submit1Activate;
    }

    /**
     * Flag para activar el botón submit 1.
     * @param submit1Activate the submit1Activate to set
     */
    public void setSubmit1Activate(boolean submit1Activate) {
        this.submit1Activate = submit1Activate;
    }

    /**
     * Flag para activar el botón submit 2.
     * @return the submit2Activate
     */
    public boolean isSubmit2Activate() {
        return submit2Activate;
    }

    /**
     * Flag para activar el botón submit 2.
     * @param submit2Activate the submit2Activate to set
     */
    public void setSubmit2Activate(boolean submit2Activate) {
        this.submit2Activate = submit2Activate;
    }

    /**
     * Flag de activacion de boton activar búsqueda municipio.
     * @return the btnActiveMunicipio
     */
    public boolean isBtnActiveMunicipio() {
        return btnActiveMunicipio;
    }

    /**
     * Flag de activacion de boton activar búsqueda municipio.
     * @param btnActiveMunicipio the btnActiveMunicipio to set
     */
    public void setBtnActiveMunicipio(boolean btnActiveMunicipio) {
        this.btnActiveMunicipio = btnActiveMunicipio;
    }

    /**
     * Flag de activación de la tabla poblacionAbn.
     * @return the tablaPoblacionesAbnActivated
     */
    public boolean isTablaPoblacionesAbnActivated() {
        return tablaPoblacionesAbnActivated;
    }

    /**
     * Flag de activación de la tabla InfoArea.
     * @return the tablaInfoAreaActivated
     */
    public boolean isTablaInfoAreaActivated() {
        return tablaInfoAreaActivated;
    }

    /**
     * Flag de activación de la tabla InfoArea.
     * @param tablaInfoAreaActivated the tablaInfoAreaActivated to set
     */
    public void setTablaInfoAreaActivated(boolean tablaInfoAreaActivated) {
        this.tablaInfoAreaActivated = tablaInfoAreaActivated;
    }

    /**
     * Flag de activación de la tabla poblacionAbn.
     * @param tablaPoblacionesAbnActivated the tablaPoblacionesAbnActivated to set
     */
    public void setTablaPoblacionesAbnActivated(boolean tablaPoblacionesAbnActivated) {
        this.tablaPoblacionesAbnActivated = tablaPoblacionesAbnActivated;
    }

    /**
     * Flag de activación de la tabla población / municipio.
     * @return the tablaPoblacionesMunicipioActivated
     */
    public boolean isTablaPoblacionesMunicipioActivated() {
        return tablaPoblacionesMunicipioActivated;
    }

    /**
     * Flag de activación de la tabla población / municipio.
     * @param tablaPoblacionesMunicipioActivated the tablaPoblacionesMunicipioActivated to set
     */
    public void setTablaPoblacionesMunicipioActivated(boolean tablaPoblacionesMunicipioActivated) {
        this.tablaPoblacionesMunicipioActivated = tablaPoblacionesMunicipioActivated;
    }

    /**
     * Flag de activación de la tabla población / estado.
     * @return the tablaPoblacionesEstadoActivated
     */
    public boolean isTablaPoblacionesEstadoActivated() {
        return tablaPoblacionesEstadoActivated;
    }

    /**
     * Flag de activación de la tabla población / estado.
     * @param tablaPoblacionesEstadoActivated the tablaPoblacionesEstadoActivated to set
     */
    public void setTablaPoblacionesEstadoActivated(boolean tablaPoblacionesEstadoActivated) {
        this.tablaPoblacionesEstadoActivated = tablaPoblacionesEstadoActivated;
    }

    /**
     * Flag de activación de la tabla e proveedores/estado.
     * @return the tablaProvEstadoActivated
     */
    public boolean isTablaProvEstadoActivated() {
        return tablaProvEstadoActivated;
    }

    /**
     * Flag de activación de la tabla e proveedores/estado.
     * @param tablaProvEstadoActivated the tablaProvEstadoActivated to set
     */
    public void setTablaProvEstadoActivated(boolean tablaProvEstadoActivated) {
        this.tablaProvEstadoActivated = tablaProvEstadoActivated;
    }

    /**
     * Flag de activación de la tabla de proveedores/ abn.
     * @return the tablaProvAbnActivated
     */
    public boolean isTablaProvAbnActivated() {
        return tablaProvAbnActivated;
    }

    /**
     * Flag de activación de la tabla de proveedores/ abn.
     * @param tablaProvAbnActivated the tablaProvAbnActivated to set
     */
    public void setTablaProvAbnActivated(boolean tablaProvAbnActivated) {
        this.tablaProvAbnActivated = tablaProvAbnActivated;
    }

    /**
     * Flag para activar la tabla de info municipio.
     * @return the tableMunicipioActivate
     */
    public boolean isTableMunicipioActivated() {
        return tableMunicipioActivated;
    }

    /**
     * Flag para activar la tabla de info municipio.
     * @param tableMunicipioActivated the tableMunicipioActivated to set
     */
    public void setTableMunicipioActivated(boolean tableMunicipioActivated) {
        this.tableMunicipioActivated = tableMunicipioActivated;
    }

    /**
     * Nir seleccionado de la lista.
     * @return the nirSelected
     */
    public String getNirSelected() {
        return nirSelected;
    }

    /**
     * Nir seleccionado de la lista.
     * @param nirSelected the nirSelected to set
     */
    public void setNirSelected(String nirSelected) {
        this.nirSelected = nirSelected;
    }

    /**
     * Campo población buscada.
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Campo población buscada.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Lista de poblaciones.
     * @return the poblaciones
     */
    public List<Poblacion> getPoblaciones() {
        return poblaciones;
    }

    /**
     * Lista de poblaciones.
     * @param poblaciones the poblaciones to set
     */
    public void setPoblaciones(List<Poblacion> poblaciones) {
        this.poblaciones = poblaciones;
    }

    /**
     * Flag comprobacion input poblacion activado.
     * @return the populationInputActivated
     */
    public boolean isPopulationInputActivated() {
        return populationInputActivated;
    }

    /**
     * Flag comprobacion input poblacion activado.
     * @param populationInputActivated the populationInputActivated to set
     */
    public void setPopulationInputActivated(boolean populationInputActivated) {
        this.populationInputActivated = populationInputActivated;
    }

    /**
     * Flag comprobacion boton formulario activado.
     * @return the formButtonActivated
     */
    public boolean isFormButtonActivated() {
        return formButtonActivated;
    }

    /**
     * Flag comprobacion boton formulario activado.
     * @param formButtonActivated the formButtonActivated to set
     */
    public void setFormButtonActivated(boolean formButtonActivated) {
        this.formButtonActivated = formButtonActivated;
    }

    /**
     * Flag para renderizado de tabla poblacion.
     * @return the populationsTableActivated
     */
    public boolean isPopulationsTableActivated() {
        return populationsTableActivated;
    }

    /**
     * Flag para renderizado de tabla poblacion.
     * @param populationsTableActivated the populationsTableActivated to set
     */
    public void setPopulationsTableActivated(boolean populationsTableActivated) {
        this.populationsTableActivated = populationsTableActivated;
    }

    /**
     * Abn.
     * @return the abn
     */
    public Abn getAbn() {
        return abn;
    }

    /**
     * Abn.
     * @param abn the abn to set
     */
    public void setAbn(Abn abn) {
        this.abn = abn;
    }

    /**
     * Lista de municipios del abn.
     * @return the municipiosAbn
     */
    public List<Municipio> getMunicipiosAbn() {
        return municipiosAbn;
    }

    /**
     * Lista de municipios del abn.
     * @param municipiosAbn the municipiosAbn to set
     */
    public void setMunicipiosAbn(List<Municipio> municipiosAbn) {
        this.municipiosAbn = municipiosAbn;
    }

    /**
     * Poblacion con mayor numeracion asignada.
     * @return the poblacionAbnMaxNumAsignada
     */
    public Poblacion getPoblacionAbnMaxNumAsignada() {
        return poblacionAbnMaxNumAsignada;
    }

    /**
     * Poblacion con mayor numeracion asignada.
     * @param poblacionAbnMaxNumAsignada the poblacionAbnMaxNumAsignada to set
     */
    public void setPoblacionAbnMaxNumAsignada(Poblacion poblacionAbnMaxNumAsignada) {
        this.poblacionAbnMaxNumAsignada = poblacionAbnMaxNumAsignada;
    }

    /**
     * Número de dígitos de número interno en función de la longitud del NIR.
     * @return the numeroDigitos
     */
    public Integer getNumeroDigitos() {
        return numeroDigitos;
    }

    /**
     * Número de dígitos de número interno en función de la longitud del NIR.
     * @param numeroDigitos the numeroDigitos to set
     */
    public void setNumeroDigitos(Integer numeroDigitos) {
        this.numeroDigitos = numeroDigitos;
    }

    /**
     * Municipio buscado.
     * @return the municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Municipio buscado.
     * @param municipio the municipio to set
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Lista de proveedores que prestan servicio en el municipio buscado.
     * @return the proveedorMunicipio
     */
    public List<Proveedor> getProveedorMunicipio() {
        return proveedorMunicipio;
    }

    /**
     * Lista de proveedores que prestan servicio en el municipio buscado.
     * @param proveedorMunicipio the proveedorMunicipio to set
     */
    public void setProveedorMunicipio(List<Proveedor> proveedorMunicipio) {
        this.proveedorMunicipio = proveedorMunicipio;
    }

    /**
     * Lista de poblaciones con numeración asignada del municipio buscado.
     * @return the poblacionesMunicipio
     */
    public List<Poblacion> getPoblacionesMunicipio() {
        return poblacionesMunicipio;
    }

    /**
     * Lista de poblaciones con numeración asignada del municipio buscado.
     * @param poblacionesMunicipio the poblacionesMunicipio to set
     */
    public void setPoblacionesMunicipio(List<Poblacion> poblacionesMunicipio) {
        this.poblacionesMunicipio = poblacionesMunicipio;
    }

    /**
     * Numeración asignada en el municipio buscado y en formato número USA.
     * @return the numeracionMunicipioFormat
     */
    public String getNumeracionMunicipioFormat() {
        return numeracionMunicipioFormat;
    }

    /**
     * Numeración asignada en el municipio buscado y en formato número USA.
     * @param numeracionMunicipioFormat the numeracionMunicipioFormat to set
     */
    public void setNumeracionMunicipioFormat(String numeracionMunicipioFormat) {
        this.numeracionMunicipioFormat = numeracionMunicipioFormat;
    }

    /**
     * Inegi municipio.
     * @return the inegiMunicipio
     */
    public String getInegiMunicipio() {
        return inegiMunicipio;
    }

    /**
     * Inegi municipio.
     * @param inegiMunicipio the inegiMunicipio to set
     */
    public void setInegiMunicipio(String inegiMunicipio) {
        this.inegiMunicipio = inegiMunicipio;
    }

    /**
     * Nir seleccionado.
     * @return the nir
     */
    public Nir getNir() {
        return nir;
    }

    /**
     * Nir seleccionado.
     * @param nir the nir to set
     */
    public void setNir(Nir nir) {
        this.nir = nir;
    }

}
