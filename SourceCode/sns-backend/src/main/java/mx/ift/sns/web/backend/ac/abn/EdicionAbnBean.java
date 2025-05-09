package mx.ift.sns.web.backend.ac.abn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPoblaciones;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Backing Bean para Edición de ABNs. */
@ManagedBean(name = "edicionAbnBean")
@ViewScoped
public class EdicionAbnBean implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EdicionAbnBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_EdicionABN";

    /** Facade de Catálogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacade;

    /** Código de ABN. */
    private String codigoAbn;

    /** Presuscripción. */
    private boolean presuscripcion;

    /** Estado Seleccionado. */
    private Estado estado;

    /** Catálogo de Estados. */
    private List<Estado> listaEstados;

    /** Municipio seleccionado. */
    private SeleccionMunicipio municipio;

    /** Catálogo de Municipios. */
    private List<SeleccionMunicipio> listaMunicipios;

    /** Catálogo de Poblaciones. */
    private List<SeleccionPoblacion> listaPoblaciones;

    /** Catálogo de Poblaciones. */
    private List<Poblacion> listaPoblacionesCargadas;

    /** Código de Nir. */
    private String codNir;

    /** Lista de códigos de Nir. */
    private List<String> listaCodigosNir;

    /** Código Nir seleccionado. */
    private String codNirSeleccionado;

    /** Código de Nir seleccionado para desasociar del ABN. */
    private String codNirEliminar;

    /** Indica si es posible agregar el Nir a la edición. */
    private boolean nirValidado = false;

    /** Lista de Series seleccionadas por Nir. */
    private List<SeleccionSerie> listaSeries;

    /** Serie seleccionada. */
    private Serie serie;

    /** Fecha de consolidación. */
    private Date fechaConsolidacion;

    /** Indica si el botón de guardar está habilitado. */
    private boolean guardarHabilitado = false;

    /** Población ancla del ABN. */
    private Poblacion poblacionAncla;

    /** Contenedor de Poblaciones Seleccionadas por Municipio. */
    private HashMap<SeleccionMunicipio, ArrayList<SeleccionPoblacion>> poblacionesSeleccionadas;

    /** Contenedor de Municipios Seleccionadas por Estado. */
    private HashMap<String, ArrayList<SeleccionMunicipio>> municipiosSeleccionados;

    /** Contenedor de Series seleccionadas por Nir para crear. */
    private HashMap<String, ArrayList<SeleccionSerie>> seriesSeleccionadas;

    /** Contador de Poblaciones. */
    private Integer contadorPoblaciones;

    /** Lista de NIRs que se van a eliminar. */
    private List<Nir> listaNirsEliminar;

    /** ABN seleccionado para edición. */
    private Abn abnEdicion = null;

    /** Indica si han habido cambios en la selección de poblaciones. */
    private boolean poblacionesCambiadas = false;

    /** Inicialización de Variables. JSR260, Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            // Inicializaciones
            listaEstados = adminCatFacade.findAllEstados();
            listaMunicipios = new ArrayList<SeleccionMunicipio>(1);
            listaPoblaciones = new ArrayList<SeleccionPoblacion>(1);
            listaPoblacionesCargadas = new ArrayList<Poblacion>(1);
            poblacionesSeleccionadas = new HashMap<SeleccionMunicipio, ArrayList<SeleccionPoblacion>>(1);
            municipiosSeleccionados = new HashMap<String, ArrayList<SeleccionMunicipio>>(1);
            seriesSeleccionadas = new HashMap<String, ArrayList<SeleccionSerie>>(1);
            listaCodigosNir = new ArrayList<String>(1);
            listaSeries = new ArrayList<SeleccionSerie>();
            listaNirsEliminar = new ArrayList<Nir>();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al cerrar la modal. Resetea las variables de edición de ABN a sus valores por defecto. */
    public void resetValues() {
        this.codigoAbn = null;
        this.codNir = null;
        this.contadorPoblaciones = null;
        this.estado = null;
        this.fechaConsolidacion = null;
        this.guardarHabilitado = false;
        this.listaCodigosNir.clear();
        this.listaSeries.clear();
        this.municipio = null;
        this.codNirSeleccionado = null;
        this.nirValidado = false;
        this.poblacionAncla = null;
        this.presuscripcion = false;
        this.serie = null;
        this.listaMunicipios.clear();
        this.listaPoblaciones.clear();
        this.listaPoblacionesCargadas.clear();
        this.poblacionesSeleccionadas.clear();
        this.municipiosSeleccionados.clear();
        this.seriesSeleccionadas.clear();
        this.listaNirsEliminar.clear();
        this.abnEdicion = null;
        this.poblacionesCambiadas = false;
    }

    /**
     * Método invocado al seleccionar un Estado del combo. Carga la lista de municipios del estado seleccionado y
     * resetea la lista de poblaciones.
     */
    public void seleccionEstado() {
        try {
            if (estado != null) {
                List<Municipio> lista = adminCatFacade.findMunicipiosByEstado(estado.getCodEstado());
                listaMunicipios = this.getListaSeleccionMunicipio(lista);
            } else {
                listaMunicipios = new ArrayList<SeleccionMunicipio>(1);
            }
            municipio = null;
            listaPoblacionesCargadas.clear();

            // Si hacemos clear se puede borrar la lista de las poblaciones seleccionadas
            listaPoblaciones = new ArrayList<SeleccionPoblacion>(1);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar un Municipio en la tabla de lista de Municipios. Carga la lista de poblaciones del
     * municipio seleccionado.
     * @param event SelectEvent de PrimeFaces con la información del evento.
     */
    public void seleccionMunicipio(SelectEvent event) {
        try {
            if (municipio != null) {
                // cargamos las poblaciones sin usar caché para tener en cuenta siempre los últimos cambios.
                listaPoblacionesCargadas = adminCatFacade.findAllPoblaciones(
                        estado.getCodEstado(), municipio.getCdgMunicipio(), false);

                listaPoblaciones = this.getListaSeleccionPoblacion(listaPoblacionesCargadas, false);
                municipio.setPoblacionesMunicipio(listaPoblaciones.size());
            } else {
                listaPoblacionesCargadas.clear();
                listaPoblaciones = new ArrayList<SeleccionPoblacion>(1);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar el checkBox de Municipio en la tabla de lista de Municipios. Autoselecciona todas
     * las poblaciones del municipio que no estén asociadas a un ABN y actualiza el formulario.
     * @param pSelMun Municipio seleccionado en la tabla.
     */
    public void seleccionCheckMunicipio(SeleccionMunicipio pSelMun) {
        try {
            // Se puede pulsar sobre el Checkbox sin haber seleccionado la fila.
            if (municipio == null || (!municipio.equals(pSelMun))) {
                municipio = pSelMun;
                // cargamos las poblaciones sin usar caché para tener en cuenta siempre los últimos cambios.
                listaPoblacionesCargadas = adminCatFacade.findAllPoblaciones(
                        estado.getCodEstado(), municipio.getCdgMunicipio(), false);
            }

            // Agrupamos por Estado
            if (!municipiosSeleccionados.containsKey(estado.getCodEstado())) {
                municipiosSeleccionados.put(estado.getCodEstado(), new ArrayList<SeleccionMunicipio>());
            }

            if (pSelMun.isSeleccionado()) {
                // Agregamos el municipios si no existía en la lista de seleccionados.
                if (!municipiosSeleccionados.get(estado.getCodEstado()).contains(pSelMun)) {
                    municipiosSeleccionados.get(estado.getCodEstado()).add(pSelMun);
                }

                // Seleccionamos todas las poblaciones del municipio (sobreescribe lo que haya)
                // No todas las poblaciones de un municipio han de pertenecer al mismo ABN. Por lo tanto,
                // no todas las poblaciones estarán marcadas como seleccionadas.
                listaPoblaciones = this.getListaSeleccionPoblacion(listaPoblacionesCargadas, true);
                poblacionesSeleccionadas.put(municipio, this.getSeleccionesListaSeleccionPoblacion(listaPoblaciones));

                if (listaPoblaciones.size() != poblacionesSeleccionadas.get(municipio).size()) {
                    MensajesBean.addInfoMsg(MSG_ID,
                            "Existen poblaciones asociadas a otro ABN que no han podido ser seleccionadas.", "");
                }

            } else {
                // Eliminamos el municipio de la lista de seleccionados.
                municipiosSeleccionados.get(estado.getCodEstado()).remove(pSelMun);

                // Limipiamos las poblaciones seleccionadas del municipio.
                poblacionesSeleccionadas.get(pSelMun).clear();
                listaPoblaciones = this.getListaSeleccionPoblacion(listaPoblacionesCargadas, false);
            }

            // Actualizamos el contador de poblaciones de municipios
            pSelMun.setPoblacionesSeleccionadas(poblacionesSeleccionadas.get(pSelMun).size());
            pSelMun.setPoblacionesMunicipio(listaPoblacionesCargadas.size());

            // Actualizamos el contador de poblaciones.
            this.actualizarContadorPoblaciones();

            // Actualiza el estado del botón de guardar.
            // this.actualizarGuardar();

            // Indica que se han comprobar los cambios en las poblaciones.
            this.poblacionesCambiadas = true;

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al deseleccionar un Municipio en la tabla de lista de Municipios.
     * @param event SelectEvent
     */
    public void deSeleccionMunicipio(UnselectEvent event) {
        listaMunicipios = new ArrayList<SeleccionMunicipio>(1);
        listaPoblaciones = new ArrayList<SeleccionPoblacion>(1);
        listaPoblacionesCargadas.clear();
        municipio = null;
    }

    /**
     * Método invocado al seleccionar una Población en la tabla de lista de Poblaciones.
     * @param pselPob Objeto SeleccionPoblacion
     */
    public void seleccionCheckPoblacion(SeleccionPoblacion pselPob) {
        // Agregamos agrupación por municipio.
        if (!poblacionesSeleccionadas.containsKey(municipio)) {
            poblacionesSeleccionadas.put(municipio, new ArrayList<SeleccionPoblacion>());
        }

        if (pselPob.isSeleccionada()) {
            // Agregamos la población si no existía en la lista de seleccionadas.
            if (!poblacionesSeleccionadas.get(municipio).contains(pselPob)) {
                poblacionesSeleccionadas.get(municipio).add(pselPob);
            }
            municipio.setPoblacionesSeleccionadas(municipio.getPoblacionesSeleccionadas() + 1);
        } else {
            poblacionesSeleccionadas.get(municipio).remove(pselPob);
            municipio.setPoblacionesSeleccionadas(municipio.getPoblacionesSeleccionadas() - 1);
            if (pselPob.isPoblacionAncla()) {
                poblacionAncla = null;
                pselPob.setPoblacionAncla(false);
            }
        }

        // Actualizamos el contador de poblaciones.
        this.actualizarContadorPoblaciones();

        // Actualiza el estado del botón de guardar.
        // this.actualizarGuardar();

        // Indica que se han comprobar los cambios en las poblaciones.
        this.poblacionesCambiadas = true;
    }

    /**
     * Método invocado al pulsar sobre el botón "Validar Nir". Comprueba que: <li>El valor introducido sea un valor
     * númerico <li>El código de NIR no empiece con un '0' <li>El valor introducido sea un valor númerico positivo de
     * máximo 3 dígitos y mínimo 2 <li>El código introducido no exista como clave de servicio
     */
    public void validarNir() {
        try {
            StringBuilder sbValidaciones = new StringBuilder();
            boolean testOk = true;

            if (!StringUtils.isNumeric(codNir)) {
                testOk = false;
                sbValidaciones.append("Es necesario introducir un valor numérico para el NIR<br>");
            }

            if (codNir.startsWith("0")) {
                testOk = false;
                sbValidaciones.append("El NIR introducido no pueden empezar con '0'<br>");
            }

            if (!(codNir.length() > 1 && codNir.length() < 4)) {
                testOk = false;
                sbValidaciones.append("El NIR debe ser un valor numérico positivo de máximo 3 dígitos y mínimo 2<br>");
            }

            // Validaciones NIR de 3 dígitos
            if (codNir.length() == String.valueOf(Nir.MAX_NIR).length()) {
                // (3 dígitos) El NIR a crear no debe existir como una Clave de servicio de numeración no geográfica
                ClaveServicio claveServicio = adminCatFacade.getClaveServicioByCodigo(new BigDecimal(codNir));
                if (claveServicio != null) {
                    testOk = false;
                    sbValidaciones.append("El Código de NIR existe como Clave de Servicio");
                }
            }

            // Mensaje al usuario
            if (!testOk) {
                MensajesBean.addErrorMsg(MSG_ID, sbValidaciones.toString(), "");
            }

            // Validaciones NIR
            nirValidado = testOk;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Valida que el nuevo código de ABN introducido no exista en el catálogo de ABNs y que se haya seleccionado una
     * población ancla.
     * @return 'True' si el código Introducido es válido. 'False' en caso contrario.
     * @throws Exception en caso de error.
     */
    private boolean validarAbn() throws Exception {

        // Validación de Población Ancla
        if (poblacionAncla == null) {
            MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar una población ancla del ABN.", "");
            return false;
        }

        // Validación Código de ABN disponible.
        boolean codigoValido = true;
        if (abnEdicion != null) {
            // Edición de un ABN existente.
            if (Integer.valueOf(codigoAbn).intValue() != abnEdicion.getCodigoAbn().intValue()) {
                // Se ha modificado el código, comprobamos que el nuevo no exista.
                codigoValido = (adminCatFacade.getAbnById(new BigDecimal(codigoAbn)) == null);
            }
        } else {
            // Comprobamos no exista el código de ABN indicado.
            codigoValido = (adminCatFacade.getAbnById(new BigDecimal(codigoAbn)) == null);
        }

        if (!codigoValido) {
            MensajesBean.addErrorMsg(MSG_ID, "El código de ABN introducido ya existe en el catálogo.", "");
            return false;
        }

        return true;
    }

    /**
     * Método invocado al pulsar sobre el botón "Agregar Nir". Comprueba que el código de Nir no exista ya en el
     * catálogo de Nirs. Si el Nir no existe, crea el Nir y las series asociadas según los dígitos del código de Nir.
     */
    public void agregarNir() {
        try {
            // Indica si el NIR introducido es de 2 dígitos.
            boolean nir3Digitos = codNir.length() == String.valueOf(Nir.MAX_NIR).length();

            // Truncamos el NIR de 3 dígitos a los dos primeros.
            int cdgNir2digitos;
            if (nir3Digitos) {
                cdgNir2digitos = Integer.valueOf(codNir.substring(0, 2));
            } else {
                cdgNir2digitos = Integer.valueOf(codNir);
            }

            // Primero buscamos si existe un nir de 2 dígitos ya que pueden contener las series
            // de los Nir de 3 dígitos.
            Nir nirExistente = adminCatFacade.getNirByCodigo(cdgNir2digitos);
            if ((nirExistente == null) && (nir3Digitos)) {
                // Si no existe Nir de 2 dígitos superior, buscamos el Nir de 3 dígitos indicado.
                nirExistente = adminCatFacade.getNirByCodigo(Integer.valueOf(codNir));
            }

            if (nirExistente != null && nirExistente.getEstatus().getCdg().equals(Estatus.ACTIVO)) {
                MensajesBean.addErrorMsg(MSG_ID, "El código de NIR introducido ya existe en el sistema", "");
            } else {

                // Siempre se buscan los NIRs de dos dígitos
                List<Nir> listaNirs = adminCatFacade.findAllNirByParteCodigo(cdgNir2digitos);
                ArrayList<SeleccionSerie> listaSelSeries;

                // Agregamos los objetos SeleccionSerie con la información de las series que posteriormente
                // se van a crear asociadas al Nir introducido.
                if (listaNirs.isEmpty()) {
                    listaSelSeries = new ArrayList<SeleccionSerie>(9);
                    for (int i = 1; i < 10; i++) {
                        listaSelSeries.add(this.getSeleccionSerie(codNir, i));
                    }
                    seriesSeleccionadas.put(codNir, listaSelSeries);

                } else {
                    switch (codNir.length()) {
                    case 2:
                        // Listamos los valores finales de los Nirs de 3 dígitos cuyos 2 primeros dígitos concuerdan
                        // con el Nir introducido.
                        List<String> nirEndings = new ArrayList<String>(listaNirs.size());
                        for (Nir nir : listaNirs) {
                            char[] nirChars = String.valueOf(nir.getCodigo()).toCharArray();
                            nirEndings.add(String.valueOf(nirChars[nirChars.length - 1]));
                        }

                        // Creamos los Nirs que quedan disponibles
                        listaSelSeries = new ArrayList<SeleccionSerie>();
                        for (int i = 1; i < 10; i++) {
                            listaSelSeries.add(this.getSeleccionSerie(codNir, i));
                            if (nirEndings.contains(String.valueOf(i))) {
                                // La serie ya existe. La marcamos como no editable.
                                SeleccionSerie selSer = listaSelSeries.get((i - 1));
                                selSer.setSeleccionada(false);
                                selSer.setSeleccionable(false);
                            }
                        }

                        seriesSeleccionadas.put(codNir, listaSelSeries);
                        break;

                    case 3:
                        listaSelSeries = new ArrayList<SeleccionSerie>(9);
                        for (int i = 1; i < 10; i++) {
                            listaSelSeries.add(this.getSeleccionSerie(codNir, i));
                        }
                        seriesSeleccionadas.put(codNir, listaSelSeries);
                        break;

                    default:
                        break;
                    }
                }

                // Agregamos el Nir a la lista de la Tabla.
                listaCodigosNir.add(codNir);
                codNirSeleccionado = codNir;
                listaSeries = seriesSeleccionadas.get(codNir);
            }

            // Reseteamos el Código Introducido
            codNir = null;
            nirValidado = false;

            // Actualiza el estado del botón de guardar.
            // this.actualizarGuardar();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón "Cancelar" del panel de validación de Nir. Resetea los campos de
     * agregación de nuevo Nir a sus valores por defecto.
     */
    public void cancelarAgregarNir() {
        codNir = null;
        nirValidado = false;
    }

    /**
     * Método invocado al pulsar sobre el botón "Eliminar". de la tabla de Nirs. Comprueba que el Nir seleccionado no
     * tenga numeración asignada a un proveedor de servicios y, si no la hay, desasocia el Nir del ABN. En caso
     * contrario no permite continuar con el proceso y muestra el mensaje de error correspondiente.
     */
    public void eliminarNir() {
        try {
            // Comprobamos VL7: Para poder desasociar un NIR de una ABN no deberá existir
            // numeración asignada para un PST sobre el NIR.

            // Transformamos el cdgNir en idNir.
            Nir nir = adminCatFacade.getNirByCodigo(Integer.valueOf(codNirEliminar));
            if (nir != null) {
                // Buscamos rangos asociados al NIR
                FiltroBusquedaRangos fbr = new FiltroBusquedaRangos();
                fbr.setIdNir(nir.getId());

                if (adminCatFacade.findAllRangosCount(fbr) == 0) {
                    MensajesBean.addInfoMsg(MSG_ID, "El NIR ha sido desasociado del ABN.", "");
                    listaNirsEliminar.add(nir);
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "El NIR seleccionado tiene numeración asignada a proveedores. "
                            + "No es posible desasociar el NIR.", "");
                }
            }

            // Eliminamos el Nir de las listas
            listaSeries.clear();
            seriesSeleccionadas.remove(codNirEliminar);
            listaCodigosNir.remove(codNirEliminar);

            // Actualiza el estado del botón de guardar.
            // this.actualizarGuardar();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón "Guardar". Actualiza el ABN en edición / creación con toda la
     * información seleccionada en el formulario: <li>Población Ancla <li>Presuscripción <li>Organización territorial
     * <li>Nirs <li>Series de Nirs. <li>Cambio de Código de ABN.</li><br>
     * En el caso de que se haya modificado el código de ABN, se actualizan todas las referencias del ABN en los
     * catálogos y trámites.
     */
    public void guardar() {
        try {
            if (validarAbn()) {
                Abn abn = null;
                if (abnEdicion != null) {
                    // Edición de un ABN existente.
                    abn = abnEdicion;

                    // Modificación del Código de ABN
                    if (Integer.valueOf(codigoAbn).intValue() != abn.getCodigoAbn().intValue()) {
                        abn = adminCatFacade.changeAbnCode(abn, new BigDecimal(codigoAbn));
                    }
                } else {
                    // ABN nuevo a crear.
                    abn = new Abn();
                    abn.setCodigoAbn(new BigDecimal(codigoAbn));
                    abn.setNombre(poblacionAncla.getNombre());

                    EstadoAbn status = new EstadoAbn();
                    status.setCodigo(EstadoAbn.ACTIVO);
                    abn.setEstadoAbn(status);
                }

                // Campos editables del propia ABN
                abn.setPoblacionAncla(poblacionAncla);
                if (presuscripcion) {
                    abn.setPresuscripcion("P");
                } else {
                    abn.setPresuscripcion("");
                }

                // Actualizamos el ABN
                abn = adminCatFacade.saveAbn(abn);
                abnEdicion = abn;

                // Creamos la relación de poblaciones con el ABN
                if (poblacionesCambiadas) {
                    List<PoblacionAbn> listaPoblacionesAbn = new ArrayList<PoblacionAbn>();
                    for (ArrayList<SeleccionPoblacion> arraySelPob : poblacionesSeleccionadas.values()) {
                        for (SeleccionPoblacion selPob : arraySelPob) {
                            // Poblacion ABN
                            PoblacionAbn poblacionAbn = new PoblacionAbn();
                            poblacionAbn.setAbn(abn);
                            // Poblacion
                            Poblacion poblacion = new Poblacion();
                            poblacion.setInegi(selPob.getInegi());
                            poblacionAbn.setInegi(poblacion);

                            listaPoblacionesAbn.add(poblacionAbn);
                        }
                    }

                    // Actualizamos poblaciones
                    adminCatFacade.updatePoblacionesAbn(abn.getCodigoAbn(), listaPoblacionesAbn);
                }

                // Eliminamos los NIRs indicados. Se ha comprobado previamente que los NIRs no estén asociados a
                // Series que tengan numeración asignada en la tabla de Rangos.
                for (Nir nir : listaNirsEliminar) {
                    // Los Nirs a eliminar existen en BBDD, si no, no se hubiesen agregado a la lista.
                    // Los Nirs no se pueden desasociar de un ABN ya que el ABN es obligatorio en el modelo de datos
                    // para un NIR, y tampoco se pueden eliminar ya que hay tablas con dependencias al NIR. Lo que
                    // se hace es poner el estatus del nir a inactivo junto a sus series.
                    adminCatFacade.desactivarNir(nir);
                }
                listaNirsEliminar.clear();

                // Mensaje de Info al Usuario
                StringBuilder sbNuevosMovs = new StringBuilder();
                if (abnEdicion != null) {
                    sbNuevosMovs.append("ABN ").append(abn.getCodigoAbn()).append(" actualizado<br>");
                } else {
                    sbNuevosMovs.append("ABN ").append(abn.getCodigoAbn()).append(" creado<br>");
                }

                // Estatus activo para los NIRs y Series desactivadas
                Estatus estatusActivo = new Estatus();
                estatusActivo.setCdg(Estatus.ACTIVO);

                // Agregamos los NIRs y Series indicadas por el usuario.
                for (String cdgNirEdicion : listaCodigosNir) {
                    Nir nirExistente = adminCatFacade.getNirByCodigo(Integer.valueOf(cdgNirEdicion));
                    List<String> snas = this.getSeleccionSerieSnas(
                            cdgNirEdicion, seriesSeleccionadas.get(cdgNirEdicion));

                    if (nirExistente != null) {
                        if (nirExistente.getEstatus().getCdg().equals(Estatus.INACTIVO)) {
                            // Nir Inactivo: Activamos el Nir y agregamos las nuevas series.
                            nirExistente.setEstatus(estatusActivo);
                            nirExistente = adminCatFacade.saveNir(nirExistente);
                        }

                        // Comprobamos si existen nuevas series para el NIR
                        if (!snas.isEmpty()) {
                            for (String nuevoSna : snas) {
                                if (nirExistente != null) {
                                    // Si existía el NIR es posible que existiese la serie en estado desactivada.
                                    Serie serieNir = adminCatFacade.getSerie(
                                            new BigDecimal(nuevoSna),
                                            nirExistente.getId());

                                    if (serieNir != null) {
                                        // Activamos la serie si ya existía.
                                        serieNir.setEstatus(estatusActivo);
                                        adminCatFacade.saveSerie(serieNir);
                                    } else {
                                        // Creamos la serie si no existía.
                                        adminCatFacade.createSerie(nirExistente, new BigDecimal(nuevoSna));
                                    }
                                } else {
                                    // Si no existía el NIR se crea la serie directamente.
                                    adminCatFacade.createSerie(nirExistente, new BigDecimal(nuevoSna));
                                }
                            }
                            sbNuevosMovs.append("NIR ").append(nirExistente.getCodigo()).append(" actualizado<br>");
                        }
                    } else {
                        // Creamos el NIR de cero.
                        Nir nuevoNir = adminCatFacade.createNir(abn, Integer.valueOf(cdgNirEdicion), snas);
                        sbNuevosMovs.append("NIR ").append(nuevoNir.getCodigo()).append(" creado<br>");
                    }
                }
                MensajesBean.addInfoMsg(MSG_ID, sbNuevosMovs.toString(), "");

                // Limpiamos todos los datos una vez el ABN ha sido guardado.
                // this.resetValues();
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al cargar un ABN para edición. Actualiza el formulario de edición de ABN con toda la información
     * del ABN seleccionado.
     * @param pAbn ABN seleccionado para edición.
     * @throws Exception en caso de error.
     */
    public void cargarAbn(Abn pAbn) {
        try {
            this.abnEdicion = pAbn;
            this.codigoAbn = String.valueOf(pAbn.getCodigoAbn());
            this.poblacionAncla = pAbn.getPoblacionAncla();
            this.fechaConsolidacion = pAbn.getFechaConsolidacion();
            this.presuscripcion = (!StringUtils.isEmpty(pAbn.getPresuscripcion())
                    && pAbn.getPresuscripcion().equals("P"));

            // Poblaciones del ABN seleccionadas
            List<Poblacion> poblacionesAbn = adminCatFacade.findAllPoblacionesByAbn(pAbn, false);
            for (Poblacion poblacion : poblacionesAbn) {
                // Municipio
                SeleccionMunicipio selMun = this.getSeleccionMunicipio(poblacion.getMunicipio());
                if (!poblacionesSeleccionadas.containsKey(selMun)) {
                    poblacionesSeleccionadas.put(selMun, new ArrayList<SeleccionPoblacion>());
                }
                // Población
                SeleccionPoblacion selPob = this.getSeleccionPoblacion(poblacion);
                selPob.setSeleccionada(true);
                selPob.setSeleccionable(true);
                selPob.setPoblacionAncla(poblacionAncla != null && poblacion.equals(poblacionAncla));

                poblacionesSeleccionadas.get(selMun).add(selPob);
            }

            // Número de Poblaciones por Municipio del ABN
            List<Municipio> municipiosAbn = adminCatFacade.findAllMunicipiosByAbn(pAbn, false);
            HashMap<SeleccionMunicipio, Integer> poblacionesMunicipioCont =
                    new HashMap<SeleccionMunicipio, Integer>(municipiosAbn.size());

            for (Municipio municipio : municipiosAbn) {
                FiltroBusquedaPoblaciones fbp = new FiltroBusquedaPoblaciones();
                fbp.setMunicipio(municipio);
                fbp.setEstado(municipio.getEstado());

                SeleccionMunicipio selMun = this.getSeleccionMunicipio(municipio);
                int totalPoblaciones = adminCatFacade.findAllPoblacionesCount(fbp);
                poblacionesMunicipioCont.put(selMun, new Integer(totalPoblaciones));
            }

            // Contadores de Poblaciones por Municipio
            int counter = 0;
            for (SeleccionMunicipio selMun : poblacionesSeleccionadas.keySet()) {
                int poblaciones = poblacionesSeleccionadas.get(selMun).size();
                // Poblaciones del ABN
                selMun.setPoblacionesSeleccionadas(poblaciones);
                selMun.setPoblacionesMunicipio(poblacionesMunicipioCont.get(selMun).intValue());

                // Indica si el total de poblaciones del Abn del municipio se corresponden con todas las existentes
                // para el municipio para marcarlo en la tabla.
                if (poblaciones == (poblacionesMunicipioCont.get(selMun).intValue())) {
                    if (!municipiosSeleccionados.containsKey(selMun.getCdgEstado())) {
                        municipiosSeleccionados.put(selMun.getCdgEstado(), new ArrayList<SeleccionMunicipio>());
                    }
                    municipiosSeleccionados.get(selMun.getCdgEstado()).add(selMun);
                }

                // Contador total de poblaciones seleccionadas.
                counter += poblaciones;
            }
            contadorPoblaciones = new Integer(counter);

            // NIRs del ABN
            List<Nir> listaNirAbn = adminCatFacade.findAllNirByAbn(pAbn.getCodigoAbn());
            for (Nir nirAbn : listaNirAbn) {
                // Código NIR a la lista
                listaCodigosNir.add(String.valueOf(nirAbn.getCodigo()));
                // Series asociadas al NIR
                ArrayList<SeleccionSerie> listaSelSeriesNie = this.getListaSeleccionSerie(nirAbn);
                seriesSeleccionadas.put(String.valueOf(nirAbn.getCodigo()), listaSelSeriesNie);
            }

            // Cargamos el Estado de la Población Ancla
            estado = poblacionAncla.getMunicipio().getEstado();
            this.seleccionEstado();

            // Cargamos el Municipio de la Población Ancla
            int index = listaMunicipios.indexOf(this.getSeleccionMunicipio(poblacionAncla.getMunicipio()));
            municipio = listaMunicipios.get(index);

            // Lista de poblaciones (sin usar la caché para que se actualicen los últimos cambios en poblaciones)
            listaPoblacionesCargadas = adminCatFacade.findAllPoblaciones(
                    estado.getCodEstado(), municipio.getCdgMunicipio(), false);
            listaPoblaciones = this.getListaSeleccionPoblacion(listaPoblacionesCargadas, false);

            // Actualiza el estado del botón de guardar.
            this.actualizarGuardar();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar un Nir en la tabla de lista de Nirs. Carga la tabla de series asociada al Nir y
     * preselecciona las series que existan en el catálogo de Series.
     * @param event SelectEvent de PrimeFaces con la información del evento.
     */
    public void seleccionNir(SelectEvent event) {
        if (codNirSeleccionado != null) {
            listaSeries = seriesSeleccionadas.get(codNirSeleccionado);
        }
    }

    /**
     * Método invocado al deseleccionar un Nir en la tabla de lista de Nirs. Actualiza la tabla de series.
     * @param event UnselectEvent de PrimeFaces con la información del evento.
     */
    public void deSeleccionNir(UnselectEvent event) {
        codNirSeleccionado = null;
        listaSeries = new ArrayList<SeleccionSerie>(1);
    }

    /**
     * Método invocado al pulsar sobre el checkbox de población Ancla de la tabla de poblaciones. Asocia la población
     * selecciona al ABN y actualiza el formulario de edición de ABN.
     * @param pselPob Poblacion Seleccionada
     */
    public void seleccionPoblacionAncla(SeleccionPoblacion pselPob) {

        boolean isPoblacionAncla = pselPob.isPoblacionAncla();

        // Desmarcamos la población ancla previa.
        this.poblacionAncla = null;
        for (SeleccionPoblacion selPob : listaPoblaciones) {
            selPob.setPoblacionAncla(false);
        }

        // Marcamos la nueva población ancla.
        if (isPoblacionAncla) {
            pselPob.setPoblacionAncla(true);
            this.poblacionAncla = listaPoblacionesCargadas.get(pselPob.getIndex());

            // Si no estaba marcada la población la ponemos como seleccionada.
            if (!pselPob.isSeleccionada()) {
                // Agregamos agrupación por municipio.
                if (!poblacionesSeleccionadas.containsKey(municipio)) {
                    poblacionesSeleccionadas.put(municipio, new ArrayList<SeleccionPoblacion>());
                }

                // Agregamos la población si no existía en la lista de seleccionadas.
                if (!poblacionesSeleccionadas.get(municipio).contains(pselPob)) {
                    poblacionesSeleccionadas.get(municipio).add(pselPob);
                }
                pselPob.setSeleccionada(true);
                contadorPoblaciones++;
                municipio.setPoblacionesSeleccionadas(municipio.getPoblacionesSeleccionadas() + 1);
            }
        }

        // Actualiza el estado del botón de guardar.
        // this.actualizarGuardar();

        // Indica que se han comprobar los cambios en las poblaciones.
        this.poblacionesCambiadas = true;
    }

    /**
     * Transforma una List<Poblacion> en una List<SeleccionPoblacion> para mostrar las filas correctmante en las Tabla
     * de Poblaciones.
     * @param pListaPoblaciones Lista de Poblaciones a transformar.
     * @param pAutoCheck Indica si se han de marcar todas las poblaciones como seleccionadas por defecto.
     * @return Lista de SeleccionPoblacion
     */
    private List<SeleccionPoblacion> getListaSeleccionPoblacion(List<Poblacion> pListaPoblaciones, boolean pAutoCheck) {
        if (pListaPoblaciones != null) {
            List<SeleccionPoblacion> resultado = new ArrayList<SeleccionPoblacion>(pListaPoblaciones.size());
            int index = 0;
            for (Poblacion poblacion : pListaPoblaciones) {
                SeleccionPoblacion selPob = new SeleccionPoblacion();
                selPob.setIndex(index);
                selPob.setCdgPoblacion(poblacion.getCdgPoblacion());
                selPob.setNombre(poblacion.getNombre());
                selPob.setInegi(poblacion.getInegi());
                selPob.setMunicipio(poblacion.getMunicipio().getNombre());
                if (poblacion.getAbn() != null) {
                    selPob.setAbn(poblacion.getAbn().getCodigoAbn().toString());
                }

                // Marcamos la población como seleccionable en función de si ya
                // pertenece a un ABN (que no sea el actual).
                if (!poblacion.getPoblacionesAbn().isEmpty()) {
                    if (codigoAbn != null) {
                        // Seleccionable solo si el abn de la población es el mismo que se está editando.
                        selPob.setSeleccionable(poblacion.getAbn().getCodigoAbn().intValue() == Integer.valueOf(
                                codigoAbn).intValue());
                    } else {
                        // Si tiene ABN no es seleccionable.
                        selPob.setSeleccionable(false);
                    }
                } else {
                    // Si no tiene ABN se puede seleccionar.
                    selPob.setSeleccionable(true);
                }

                // Marcamos el checkbox de seleccionada
                if (!pAutoCheck) {
                    if (poblacionesSeleccionadas.get(municipio) != null) {
                        selPob.setSeleccionada(poblacionesSeleccionadas.get(municipio).contains(selPob));
                    }
                } else {
                    // El autocheck solamente se utiliza para cargar poblaciones autoseleccionadas antes de
                    // completar el combo de poblacionesSeleccionadas.
                    selPob.setSeleccionada(selPob.isSeleccionable());
                }

                // Marcamos el checkbox de Población ancla
                if (poblacionAncla != null) {
                    selPob.setPoblacionAncla(poblacion.equals(poblacionAncla));
                }

                resultado.add(selPob);
                index++;
            }

            return resultado;
        } else {
            return new ArrayList<SeleccionPoblacion>(1);
        }
    }

    /**
     * Recupera los objetos SeleccionPoblacion marcados como seleccionados.
     * @param pListaSeleccionPoblaciones Lista de SeleccionPoblacion marcados como seleccionados.
     * @return List
     */
    private ArrayList<SeleccionPoblacion> getSeleccionesListaSeleccionPoblacion(
            List<SeleccionPoblacion> pListaSeleccionPoblaciones) {

        ArrayList<SeleccionPoblacion> listaSelecciones = new ArrayList<SeleccionPoblacion>();

        if (pListaSeleccionPoblaciones != null) {
            for (SeleccionPoblacion selPob : pListaSeleccionPoblaciones) {
                if (selPob.isSeleccionada()) {
                    listaSelecciones.add(selPob);
                }
            }
        } else {
            return new ArrayList<SeleccionPoblacion>(1);
        }

        return listaSelecciones;
    }

    /**
     * Transforma una List<Municipio> en una List<SeleccionMunicipio> para mostrar las filas correctmante en las Tabla
     * de Municipios.
     * @param pListaMunicipios Lista de Municipios a transformar.
     * @return Lista de SeleccionMunicipio
     */
    private List<SeleccionMunicipio> getListaSeleccionMunicipio(List<Municipio> pListaMunicipios) {
        if (pListaMunicipios != null) {
            List<SeleccionMunicipio> resultado = new ArrayList<SeleccionMunicipio>(pListaMunicipios.size());
            int index = 0;
            for (Municipio municipio : pListaMunicipios) {
                SeleccionMunicipio selMun = new SeleccionMunicipio();
                selMun.setCdgEstado(municipio.getEstado().getCodEstado());
                selMun.setCdgMunicipio(municipio.getId().getCodMunicipio());
                selMun.setNombre(municipio.getNombre());
                selMun.setIndex(index);

                // Marcamos el checkbox de seleccionado
                if (municipiosSeleccionados.containsKey(municipio.getEstado().getCodEstado())) {
                    selMun.setSeleccionado(
                            municipiosSeleccionados.get(municipio.getEstado().getCodEstado()).contains(selMun));
                }

                // Número de poblaciones seleccionadas en el municipio
                if (poblacionesSeleccionadas.containsKey(selMun)) {
                    selMun.setPoblacionesSeleccionadas(poblacionesSeleccionadas.get(selMun).size());

                    // Para los municipios con poblaciones seleccionadas informamos del total de poblaciones
                    // disponibles.
                    FiltroBusquedaPoblaciones fbp = new FiltroBusquedaPoblaciones();
                    fbp.setMunicipio(municipio);
                    fbp.setEstado(municipio.getEstado());
                    selMun.setPoblacionesMunicipio(adminCatFacade.findAllPoblacionesCount(fbp));

                } else {
                    selMun.setPoblacionesSeleccionadas(0);
                }

                resultado.add(selMun);
                index++;
            }
            return resultado;
        } else {
            return new ArrayList<SeleccionMunicipio>(1);
        }
    }

    /**
     * Genera la lista de objetos SeleccionSerie del Nir indicado.
     * @param pNir Información del Nir.
     * @return Lista de SeleccionSerie
     * @throws Exception en caso de error.
     */
    private ArrayList<SeleccionSerie> getListaSeleccionSerie(Nir pNir) throws Exception {
        ArrayList<SeleccionSerie> resultado = new ArrayList<SeleccionSerie>();

        int digitosNir = String.valueOf(pNir.getCodigo()).length();
        int multiplicador = 1000; // Nir de 2 dígitos.
        if (digitosNir == String.valueOf(Nir.MAX_NIR).length()) {
            multiplicador = 100; // Nir de 3 dígitos.
        }

        for (int i = 1; i < 10; i++) {
            SeleccionSerie selSer = new SeleccionSerie();
            selSer.setCdgNir(String.valueOf(pNir.getCodigo()));

            int minSna = (multiplicador * i);
            Serie minSerie = adminCatFacade.getSerie(new BigDecimal(minSna), pNir.getId());
            if (minSerie != null) {
                selSer.setInicioSeries(minSerie.getId().getSna().toString());
            }

            int maxSna = (multiplicador * (i + 1)) - 1;
            Serie maxSerie = adminCatFacade.getSerie(new BigDecimal(maxSna), pNir.getId());
            if (maxSerie != null) {
                selSer.setFinalSeries(maxSerie.getId().getSna().toString());
            }

            if (minSerie == null
                    || maxSerie == null
                    || minSerie.getEstatus().getCdg().equals(Estatus.INACTIVO)
                    || maxSerie.getEstatus().getCdg().equals(Estatus.INACTIVO)) {
                // Serie Inexistente o inactiva.
                selSer.setInicioSeries(String.valueOf(minSna));
                selSer.setFinalSeries(String.valueOf(maxSna));
                selSer.setSeleccionable(true);
                selSer.setSeleccionada(false);
            } else {
                // Serie existente.
                selSer.setSeleccionable(false);
                selSer.setSeleccionada(true);
            }

            resultado.add(selSer);
        }

        return resultado;
    }

    /**
     * Crea un objeto SeleccionMunicipio en base a un Municipio.
     * @param pMunicipio Información del Municipio.
     * @return SeleccionMunicipio
     */
    private SeleccionMunicipio getSeleccionMunicipio(Municipio pMunicipio) {
        SeleccionMunicipio selMun = new SeleccionMunicipio();
        selMun.setCdgEstado(pMunicipio.getEstado().getCodEstado());
        selMun.setCdgMunicipio(pMunicipio.getId().getCodMunicipio());
        selMun.setNombre(pMunicipio.getNombre());
        return selMun;
    }

    /**
     * Crea un objeto SeleccionPoblacion en base a un Poblacion.
     * @param pPoblacion Información de la Población.
     * @return SeleccionPoblacion
     */
    private SeleccionPoblacion getSeleccionPoblacion(Poblacion pPoblacion) {
        SeleccionPoblacion selPob = new SeleccionPoblacion();
        selPob.setCdgPoblacion(pPoblacion.getCdgPoblacion());
        selPob.setNombre(pPoblacion.getNombre());
        selPob.setInegi(pPoblacion.getInegi());
        selPob.setMunicipio(pPoblacion.getMunicipio().getNombre());
        if (pPoblacion.getAbn() != null) {
            selPob.setAbn(pPoblacion.getAbn().getCodigoAbn().toString());
        }
        return selPob;
    }

    /**
     * Crea un nuevo Objeto SeleccionSerie para mostrar en la tabla en función de los parámetros del Nir.
     * @param pCdgNir Código de Nir.
     * @param pInicioRango Valor a partir del cual crear la Serie
     * @return Objeto SeleccionSerie
     */
    private SeleccionSerie getSeleccionSerie(String pCdgNir, int pInicioRango) {
        SeleccionSerie selSer = new SeleccionSerie();
        selSer.setCdgNir(codNir);

        StringBuilder sBuilderIni = new StringBuilder();
        StringBuilder sBuilderFin = new StringBuilder();
        if (pCdgNir.length() == String.valueOf(Nir.MAX_NIR).length()) {
            // Nir de 3 dígitos
            sBuilderIni.append(pInicioRango);
            sBuilderIni.append("00");
            sBuilderFin.append(pInicioRango);
            sBuilderFin.append("99");
        } else {
            // Nir de 2 dígitos
            sBuilderIni.append(pInicioRango);
            sBuilderIni.append("000");
            sBuilderFin.append(pInicioRango);
            sBuilderFin.append("999");
        }

        selSer.setInicioSeries(sBuilderIni.toString());
        selSer.setFinalSeries(sBuilderFin.toString());
        selSer.setSeleccionable(true);
        selSer.setSeleccionada(true);

        return selSer;
    }

    /**
     * Recupera el listado de nuevos SNA a crear seleccionados para el NIR indicado.
     * @param pCdgNir Código de Nir.
     * @param pListaSelSer Lista de objetos SeleccionSerie seleccionados por el usuario.
     * @return listado de nuevos SNA a crear
     */
    private List<String> getSeleccionSerieSnas(String pCdgNir, ArrayList<SeleccionSerie> pListaSelSer) {
        List<String> nuevosSnas = new ArrayList<String>();
        for (SeleccionSerie selSer : pListaSelSer) {
            // Agregamos a la lista las nuevas series a crear.
            if (selSer.isSeleccionada() && selSer.isSeleccionable()) {
                // X00 para Nirs de 2 dígitos, X000 para nirs de 3 dígitos.
                int inicioSeries = Integer.valueOf(selSer.getInicioSeries());
                int finalSeries = Integer.valueOf(selSer.getFinalSeries());
                for (int i = inicioSeries; i <= finalSeries; i++) {
                    nuevosSnas.add(String.valueOf(i));
                }
            }
        }
        return nuevosSnas;
    }

    /** Actualiza el contador de poblaciones. */
    private void actualizarContadorPoblaciones() {

        int counter = 0;
        for (ArrayList<SeleccionPoblacion> listaSelPob : poblacionesSeleccionadas.values()) {
            counter += listaSelPob.size();
        }

        contadorPoblaciones = new Integer(counter);
    }

    /** Actualiza el estado del botón de Guardar. */
    public void actualizarGuardar() {
        // guardarHabilitado = (!poblacionesSeleccionadas.isEmpty()
        // && !seriesSeleccionadas.isEmpty() && (codigoAbn != null));

        // Mensaje específico de Código de ABN.
        if (codigoAbn == null) {
            MensajesBean.addErrorMsg(MSG_ID, "El código de ABN no puede ser nulo.", "");
            if (abnEdicion != null) {
                codigoAbn = String.valueOf(abnEdicion.getCodigoAbn());
            }
        }

        // Solo se permite guardar existiendo ABN.
        guardarHabilitado = (codigoAbn != null);
    }

    // GETTERS & SETTERS

    /**
     * Código de ABN seleccionado para la búsqueda.
     * @return BigDecimal
     */
    public String getCodigoAbn() {
        return codigoAbn;
    }

    /**
     * Código de ABN seleccionado para la búsqueda.
     * @param codigoAbn BigDecimal
     */
    public void setCodigoAbn(String codigoAbn) {
        this.codigoAbn = codigoAbn;
    }

    /**
     * Presuscripión de ABN.
     * @return boolean
     */
    public boolean isPresuscripcion() {
        return presuscripcion;
    }

    /**
     * Presuscripión de ABN.
     * @param presuscripcion boolean
     */
    public void setPresuscripcion(boolean presuscripcion) {
        this.presuscripcion = presuscripcion;
    }

    /**
     * Lista de Estados (Organización Territorial).
     * @return List
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Lista de Municipio (Organización Territorial).
     * @return List
     */
    public List<SeleccionMunicipio> getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Lista de Municipio (Organización Territorial).
     * @return List
     */
    public List<SeleccionPoblacion> getListaPoblaciones() {
        return listaPoblaciones;
    }

    /**
     * Código de Nir.
     * @return String
     */
    public String getCodNir() {
        return codNir;
    }

    /**
     * Código de Nir.
     * @param codNir String
     */
    public void setCodNir(String codNir) {
        this.codNir = codNir;
    }

    /**
     * Indica si es posible agregar el Nir a la edición.
     * @return boolean
     */
    public boolean isNirValidado() {
        return nirValidado;
    }

    /**
     * Serie seleccionada.
     * @return Serie
     */
    public Serie getSerie() {
        return serie;
    }

    /**
     * Serie seleccionada.
     * @param serie Serie
     */
    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    /**
     * Lista de series por Nir.
     * @return List
     */
    public List<SeleccionSerie> getListaSeries() {
        return listaSeries;
    }

    /**
     * Estado (OT) Seleccionado.
     * @return Estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Estado (OT) Seleccionado.
     * @param estado Estado
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Municipio Seleccionado.
     * @return SeleccionMunicipio
     */
    public SeleccionMunicipio getMunicipio() {
        return municipio;
    }

    /**
     * Municipio Seleccionado.
     * @param municipio SeleccionMunicipio
     */
    public void setMunicipio(SeleccionMunicipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Lista de códigos de Nir.
     * @return List
     */
    public List<String> getListaCodigosNir() {
        return listaCodigosNir;
    }

    /**
     * Fecha de Consolidación.
     * @return Date
     */
    public Date getFechaConsolidacion() {
        return fechaConsolidacion;
    }

    /**
     * Fecha de Consolidación.
     * @param fechaConsolidacion Date
     */
    public void setFechaConsolidacion(Date fechaConsolidacion) {
        this.fechaConsolidacion = fechaConsolidacion;
    }

    /**
     * Indica si el botón de guardar está habilitado.
     * @return boolean
     */
    public boolean isGuardarHabilitado() {
        return guardarHabilitado;
    }

    /**
     * Contador de Poblaciones.
     * @return int
     */
    public Integer getContadorPoblaciones() {
        return contadorPoblaciones;
    }

    /**
     * Población ancla del ABN.
     * @return Poblacion
     */
    public Poblacion getPoblacionAncla() {
        return poblacionAncla;
    }

    /**
     * Código Nir seleccionado.
     * @return String
     */
    public String getCodNirSeleccionado() {
        return codNirSeleccionado;
    }

    /**
     * Código Nir seleccionado.
     * @param codNirSeleccionado String
     */
    public void setCodNirSeleccionado(String codNirSeleccionado) {
        this.codNirSeleccionado = codNirSeleccionado;
    }

    /**
     * Código de Nir seleccionado para Stringthe codNirEliminar.
     * @return String
     */
    public String getCodNirEliminar() {
        return codNirEliminar;
    }

    /**
     * Código de Nir seleccionado para desasociar del ABN.
     * @param codNirEliminar String
     */
    public void setCodNirEliminar(String codNirEliminar) {
        this.codNirEliminar = codNirEliminar;
    }

    /**
     * ABN seleccionado para edición.
     * @return the abnEdicion
     */
    public Abn getAbnEdicion() {
        return abnEdicion;
    }

}
