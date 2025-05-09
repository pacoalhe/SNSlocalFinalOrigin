package mx.ift.sns.web.frontend.areas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.web.frontend.common.MensajesFrontBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Busca la información del Nir seleccionado en la búsqueda por ABN y renderiza las tablas correspondientes del dialog.
 * @author X51461MO
 */
@ManagedBean(name = "infoPoblacionEstadoBean")
@ViewScoped
public class InfoPoblacionEstadoBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoPoblacionEstadoBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaFront";

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /**
     * Listado de nirs de una poblacion.
     */
    private List<Nir> nirPoblacion;

    /**
     * Población seleccionada.
     */
    private Poblacion poblacionSeleccionada;

    /**
     * Lista de proveedores por población.
     */
    private List<Proveedor> proveedorPoblacion;

    /**
     * Flag que activa la tabla de iformación del área.
     */
    private boolean tablaAreaActivada = false;

    /**
     * Flag que activa la tabla dos de iformación del área.
     */
    private boolean tablaDosAreaActivada = false;

    /**
     * Abn.
     */
    private Abn abn;

    /**
     * Poblacion con mayor numeració asignada.
     */
    private Poblacion poblacionMaxNumeracion;

    /**
     * Lista de poblaciones por abn.
     */
    private List<Poblacion> poblacionesAbn;

    /**
     * Lista de municipios por abn.
     */
    private List<Municipio> municipiosAbn;

    /**
     * Lista de proveedores por Abn.
     */
    private List<Proveedor> proveedoresAbn;
    /**
     * Nir seleccionado.
     */
    private Nir nirSeleccionado;
    /**
     * Código Nir seleccionado de la lista.
     */
    private String codigoNir;

    /**
     * Numeración en formato USA.
     */
    private String numeracionPoblacion;

    /**
     * Numeración Abn en formato USA.
     */
    private String numeracionAbn;
    /**
     * Presuscripcion.
     */
    private String presuscripcion;
    /**
     * Flag de activación de la tabla detalle de población.
     */
    private boolean tablaPoblacionEstadoActivada = true;
    /**
     * Flag de activación de la lista de selección de nir.
     */
    private boolean listaDesplegableNirActivada = true;
    /**
     * Proveedores numeracion Nir.
     */
    private List<Proveedor> proveedorNumeracionNir;

    /** Poblaciones numeración asignada por Nir. */
    private List<Poblacion> poblacionesNumeracionNir;

    /** Numeración asignada Nir en formato número USA. */
    private String numeracionNirFormat;
    /**
     * Lista de municipios de un Nir.
     */
    private List<Municipio> municipiosNir;

    /** Dígitos del numero interno en función de la longitud del nir. */
    private Integer numeroDigitos;

    // ///////////////////////////////MÉTODOS///////////////////////////////////////

    /**
     * Devuelve la lista de nirs por población.
     * @param pob Poblacion
     * @return List<Nir>
     */
    public List<Nir> nirPoblacion(Poblacion pob) {

        List<Nir> listNirPoblacion = new ArrayList<Nir>();
        try {
            listNirPoblacion.addAll(ngPublicService.findAllNirByPoblacion(pob));
        } catch (Exception e) {
            LOGGER.error("Error al crea la lista de nirs poblacion" + e.getMessage());
        }
        return listNirPoblacion;

    }

    /**
     * Setea la información de la población seleccionada en la tabla de poblaciones con numeración asignada de un
     * estado.
     * @param poblacion Poblacion
     */
    public void localInfoByPoblacion(Poblacion poblacion) {
        try {
            this.setTablaDosAreaActivada(true);
            this.setTablaPoblacionEstadoActivada(true);
            this.setListaDesplegableNirActivada(false);
            this.setTablaAreaActivada(false);
            this.setPoblacionSeleccionada(poblacion);
            this.setNirPoblacion(ngPublicService.findAllNirByPoblacion(poblacion));
            this.setProveedorPoblacion(ngPublicService.findAllPrestadoresServicioBy(null, null, poblacion, null, null));
            this.setNumeracionPoblacion(this.formatoNumeracionAsignada(ngPublicService
                    .getTotalNumRangosAsignadosByPoblacion("", "", null, poblacion)));
            Abn abn = ngPublicService.getAbnByPoblacion(poblacion);
            if (abn.getPresuscripcion() != null) {
                if (abn.getPresuscripcion().compareTo("P") == 0) {
                    this.setPresuscripcion("Sí");
                } else {
                    this.setPresuscripcion("No");
                }
            } else {
                this.setPresuscripcion("No");
            }
            this.setPoblacionMaxNumeracion(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(abn));
        } catch (Exception e) {
            LOGGER.error("Error al crea la lista de nirs poblacion" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
        }

    }

    /**
     * Setea la inforamación de una area básica de numeración asociada a un nir.
     */
    public void areaInfoNir() {
        try {
            this.setTablaAreaActivada(true);
            this.setTablaPoblacionEstadoActivada(false);
            this.setListaDesplegableNirActivada(false);
            this.setTablaDosAreaActivada(false);
            if (this.codigoNir != null) {
                this.setCodigoNir(this.codigoNir);
                this.setNirSeleccionado(ngPublicService.getNirByCodigo(Integer.parseInt(this.codigoNir)));
                this.setTablaAreaActivada(true);
                this.setAbn(ngPublicService.getAbnByCodigoNir(this.codigoNir));
                if (this.abn.getPresuscripcion() != null) {
                    if (this.abn.getPresuscripcion().compareTo("P") == 0) {
                        this.setPresuscripcion("Sí");
                    } else {
                        this.setPresuscripcion("No");
                    }
                }
                this.setPoblacionMaxNumeracion(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(this.abn));
                this.setPoblacionesAbn(ngPublicService.findAllPoblacionesByAbn(this.abn));
                this.setMunicipiosAbn(ngPublicService.findAllMunicipiosByNir(this.getNirSeleccionado(), true));
                this.setNumeracionAbn(this.formatoNumeracionAsignada(ngPublicService.getTotalNumRangosAsignadosByAbn("",
                        "",
                        this.abn, null)));
                this.setProveedoresAbn(ngPublicService.findAllConcesionariosByAbn(this.abn));
                this.setCodigoNir("");
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error en la carga de datos para el nir seleccionado");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado en la carga de datos" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
        }
    }

    /**
     * Setea la inforamación de una area básica de numeración asociada a un nir.
     * @param codigoNir String
     */
    public void areaInfoByNir(String codigoNir) {
        try {
            this.setTablaAreaActivada(true);
            this.setTablaPoblacionEstadoActivada(false);
            this.setListaDesplegableNirActivada(false);
            this.setTablaDosAreaActivada(false);
            if (codigoNir != null) {
                this.setCodigoNir(codigoNir);
                this.chequeaNumeroDigitos(codigoNir);
                this.setNirSeleccionado(ngPublicService.getNirByCodigo(Integer.parseInt(codigoNir)));
                this.setTablaAreaActivada(true);
                this.setAbn(ngPublicService.getAbnByCodigoNir(codigoNir));
                if (this.abn.getPresuscripcion() != null) {
                    if (this.abn.getPresuscripcion().compareTo("P") == 0) {
                        this.setPresuscripcion("Sí");
                    } else {
                        this.setPresuscripcion("No");
                    }
                } else {
                    this.setPresuscripcion("No");
                }
                this.setPoblacionMaxNumeracion(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(this.abn));
                this.setPoblacionesNumeracionNir(ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(
                        this.getNirSeleccionado()));
                this.setMunicipiosAbn(ngPublicService.findAllMunicipiosByNir(getNirSeleccionado(), true));
                BigDecimal bg = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(this.getNirSeleccionado()));
                this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bg));
                this.setProveedorNumeracionNir(ngPublicService.findAllPrestadoresServicioBy(this.getNirSeleccionado(),
                        null,
                        null,
                        null, null));
                this.setCodigoNir("");
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error en la carga de datos para el nir seleccionado");
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado en la carga de datos" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
        }
    }

    /**
     * Convierte a formato número USA un número.
     * @param num BigDecimal
     * @return String
     */
    public String formatoNumeracionAsignada(BigDecimal num) {
        String numeroStr = "";
        try {
            NumberFormat numFormato = NumberFormat.getNumberInstance(Locale.US);
            numeroStr = numFormato.format(num);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al dar formato a la numeración asignada" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
        }

        return numeroStr;
    }

    /**
     * Busca las poblaciones con numeración asignada para un nir en concreto. Se utiliza en la vista cuando se muestra
     * la información para una población, y esta puede estar ligada a varios nir, ya que en la búsqueda no se especifica
     * uno en concreto.
     * @param nir Nir
     * @return List<Poblacion>
     */
    public List<Poblacion> getPoblacionesNumeracionAsignadaNir(Nir nir) {
        try {
            this.setPoblacionesNumeracionNir(ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(nir));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al buscar las poblaciones con numeración asignada asociadas a un nir",
                    e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
        }
        return this.getPoblacionesNumeracionNir();
    }

    /**
     * Busca la lista de proveedores que prestan servicio en un Nir. Se utiliza en la vista cuando se muestra la
     * información para una población, y esta puede estar ligada a varios nir, ya que en la búsqueda no se especifica
     * uno en concreto.
     * @param nir Nir
     * @return List<Proveedor>
     */
    public List<Proveedor> getProveedoresServicioNir(Nir nir) {
        try {
            this.setProveedorNumeracionNir(ngPublicService.findAllPrestadoresServicioBy(nir, null, null, null, null));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al buscar los proveedores con numeración asignada asociados a un nir",
                    e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
        }
        return this.getProveedorNumeracionNir();
    }

    /**
     * Busca la numeración asignada a un Nir. Se utiliza en la vista cuando se muestra la información para una
     * población, y esta puede estar ligada a varios nir, ya que en la búsqueda no se especifica uno en concreto.
     * @param nir Nir
     * @return String
     */
    public String getNumeracionAsignadaNir(Nir nir) {
        try {
            BigDecimal bg = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(nir));
            this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bg));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al buscar la numeración asignada asociada a un nir",
                    e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
        }
        return this.getNumeracionNirFormat();
    }

    /**
     * Busca la lista de municipios ligados a un Nir. Se utiliza en la vista cuando se muestra la información para una
     * población, y esta puede estar ligada a varios nir, ya que en la búsqueda no se especifica uno en concreto.
     * @param nir Nir
     * @return List<Municipio>
     */
    public List<Municipio> getMunicipioNir(Nir nir) {
        try {
            this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(nir, true));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al buscar los municipios asociado a un nir",
                    e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
        }
        return this.municipiosNir;
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
 //           this.setNumeroDigitos(9);
            this.setNumeroDigitos(10);
            break;
        case 2:
 //           this.setNumeroDigitos(8);
            this.setNumeroDigitos(10);

            break;
        case 3:
//            this.setNumeroDigitos(7);
            this.setNumeroDigitos(10);

            break;
        }
    }

    /**
     * Devuelve el número de dígitos según el nir.
     * @param nir Nir
     * @return Integer
     */
    public Integer getDigitos(Nir nir) {
        try {
            String codigoNir = String.valueOf(nir.getCodigo());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Nir: {} y length {}", codigoNir, codigoNir.length());
            }
            switch (codigoNir.length()) {
            case 1:
            //    this.setNumeroDigitos(9);
                this.setNumeroDigitos(10);
                break;
            case 2:
 //               this.setNumeroDigitos(8);
                this.setNumeroDigitos(10);

                break;
            case 3:
 //               this.setNumeroDigitos(7);
                this.setNumeroDigitos(10);

                break;

            }
        } catch (Exception e) {
            LOGGER.error("Error al setear el número de dígitos del numero interno" + e.getMessage());
        }
        return this.getNumeroDigitos();
    }

    // /////////////////////////////////////////GETTERS AND SETTERS/////////////////////////////////
    /**
     * Lista de municipios de un Nir.
     * @return the municipiosNir
     */
    public List<Municipio> getMunicipiosNir() {
        return municipiosNir;
    }

    /**
     * Lista de municipios de un Nir.
     * @param municipiosNir the municipiosNir to set
     */
    public void setMunicipiosNir(List<Municipio> municipiosNir) {
        this.municipiosNir = municipiosNir;
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
     * Flag que activa la tabla dos de iformación del área.
     * @return the tablaDosAreaActivada
     */
    public boolean isTablaDosAreaActivada() {
        return tablaDosAreaActivada;
    }

    /**
     * Flag que activa la tabla dos de iformación del área.
     * @param tablaDosAreaActivada the tablaDosAreaActivada to set
     */
    public void setTablaDosAreaActivada(boolean tablaDosAreaActivada) {
        this.tablaDosAreaActivada = tablaDosAreaActivada;
    }

    /**
     * Flag de activación de la lista de selección de nir.
     * @return the listaDesplegableNirActivada
     */
    public boolean isListaDesplegableNirActivada() {
        return listaDesplegableNirActivada;
    }

    /**
     * Flag de activación de la lista de selección de nir.
     * @param listaDesplegableNirActivada the listaDesplegableNirActivada to set
     */
    public void setListaDesplegableNirActivada(boolean listaDesplegableNirActivada) {
        this.listaDesplegableNirActivada = listaDesplegableNirActivada;
    }

    /**
     * Flag de activación de la tabla detalle de población.
     * @return the tablaPoblacionEstadoActivada
     */
    public boolean isTablaPoblacionEstadoActivada() {
        return tablaPoblacionEstadoActivada;
    }

    /**
     * Flag de activación de la tabla detalle de población.
     * @param tablaPoblacionEstadoActivada the tablaPoblacionEstadoActivada to set
     */
    public void setTablaPoblacionEstadoActivada(boolean tablaPoblacionEstadoActivada) {
        this.tablaPoblacionEstadoActivada = tablaPoblacionEstadoActivada;
    }

    /**
     * Presuscripcion.
     * @return the presuscripcion
     */
    public String getPresuscripcion() {
        return presuscripcion;
    }

    /**
     * Presuscripcion.
     * @param presuscripcion the presuscripcion to set
     */
    public void setPresuscripcion(String presuscripcion) {
        this.presuscripcion = presuscripcion;
    }

    /**
     * Numeración Abn en formato USA.
     * @return the numeracionAbn
     */
    public String getNumeracionAbn() {
        return numeracionAbn;
    }

    /**
     * Numeración Abn en formato USA.
     * @param numeracionAbn the numeracionAbn to set
     */
    public void setNumeracionAbn(String numeracionAbn) {
        this.numeracionAbn = numeracionAbn;
    }

    /**
     * Numeración en formato USA.
     * @return the numeracionPoblacion
     */
    public String getNumeracionPoblacion() {
        return numeracionPoblacion;
    }

    /**
     * Numeración en formato USA.
     * @param numeracionPoblacion the numeracionPoblacion to set
     */
    public void setNumeracionPoblacion(String numeracionPoblacion) {
        this.numeracionPoblacion = numeracionPoblacion;
    }

    /**
     * Código Nir seleccionado de la lista.
     * @return the codigoNir
     */
    public String getCodigoNir() {
        return codigoNir;
    }

    /**
     * Código Nir seleccionado de la lista.
     * @param codigoNir the codigoNir to set
     */
    public void setCodigoNir(String codigoNir) {
        this.codigoNir = codigoNir;
    }

    /**
     * Nir seleccionado.
     * @return the nirSeleccionado
     */
    public Nir getNirSeleccionado() {
        return nirSeleccionado;
    }

    /**
     * Nir seleccionado.
     * @param nirSeleccionado the nirSeleccionado to set
     */
    public void setNirSeleccionado(Nir nirSeleccionado) {
        this.nirSeleccionado = nirSeleccionado;
    }

    /**
     * Lista de proveedores por Abn.
     * @return the proveedoresAbn
     */
    public List<Proveedor> getProveedoresAbn() {
        return proveedoresAbn;
    }

    /**
     * Lista de proveedores por Abn.
     * @param proveedoresAbn the proveedoresAbn to set
     */
    public void setProveedoresAbn(List<Proveedor> proveedoresAbn) {
        this.proveedoresAbn = proveedoresAbn;
    }

    /**
     * Lista de municipios por abn.
     * @return the municipiosAbn
     */
    public List<Municipio> getMunicipiosAbn() {
        return municipiosAbn;
    }

    /**
     * Lista de municipios por abn.
     * @param municipiosAbn the municipiosAbn to set
     */
    public void setMunicipiosAbn(List<Municipio> municipiosAbn) {
        this.municipiosAbn = municipiosAbn;
    }

    /**
     * Lista de poblaciones por abn.
     * @return the poblacionesAbn
     */
    public List<Poblacion> getPoblacionesAbn() {
        return poblacionesAbn;
    }

    /**
     * Lista de poblaciones por abn.
     * @param poblacionesAbn the poblacionesAbn to set
     */
    public void setPoblacionesAbn(List<Poblacion> poblacionesAbn) {
        this.poblacionesAbn = poblacionesAbn;
    }

    /**
     * Poblacion con mayor numeració asignada.
     * @return the poblacionMaxNumeracion
     */
    public Poblacion getPoblacionMaxNumeracion() {
        return poblacionMaxNumeracion;
    }

    /**
     * Poblacion con mayor numeració asignada.
     * @param poblacionMaxNumeracion the poblacionMaxNumeracion to set
     */
    public void setPoblacionMaxNumeracion(Poblacion poblacionMaxNumeracion) {
        this.poblacionMaxNumeracion = poblacionMaxNumeracion;
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
     * Flag que activa la tabla de iformación del área.
     * @return the tablaAreaActivada
     */
    public boolean isTablaAreaActivada() {
        return tablaAreaActivada;
    }

    /**
     * Flag que activa la tabla de iformación del área.
     * @param tablaAreaActivada the tablaAreaActivada to set
     */
    public void setTablaAreaActivada(boolean tablaAreaActivada) {
        this.tablaAreaActivada = tablaAreaActivada;
    }

    /**
     * Lista de proveedores por población.
     * @return the proveedorPoblacion
     */
    public List<Proveedor> getProveedorPoblacion() {
        return proveedorPoblacion;
    }

    /**
     * Lista de proveedores por población.
     * @param proveedorPoblacion the proveedorPoblacion to set
     */
    public void setProveedorPoblacion(List<Proveedor> proveedorPoblacion) {
        this.proveedorPoblacion = proveedorPoblacion;
    }

    /**
     * Población seleccionada.
     * @return the poblacionSeleccionada
     */
    public Poblacion getPoblacionSeleccionada() {
        return poblacionSeleccionada;
    }

    /**
     * Población seleccionada.
     * @param poblacionSeleccionada the poblacionSeleccionada to set
     */
    public void setPoblacionSeleccionada(Poblacion poblacionSeleccionada) {
        this.poblacionSeleccionada = poblacionSeleccionada;
    }

    /**
     * Listado de nirs de una poblacion.
     * @return the nirPoblacion
     */
    public List<Nir> getNirPoblacion() {
        return nirPoblacion;
    }

    /**
     * Listado de nirs de una poblacion.
     * @param nirPoblacion the nirPoblacion to set
     */
    public void setNirPoblacion(List<Nir> nirPoblacion) {
        this.nirPoblacion = nirPoblacion;
    }

    /**
     * Dígitos del numero interno en función de la longitud del nir.
     * @return the numeroDigitos
     */
    public Integer getNumeroDigitos() {
        return numeroDigitos;
    }

    /**
     * Dígitos del numero interno en función de la longitud del nir.
     * @param numeroDigitos the numeroDigitos to set
     */
    public void setNumeroDigitos(Integer numeroDigitos) {
        this.numeroDigitos = numeroDigitos;
    }

}
