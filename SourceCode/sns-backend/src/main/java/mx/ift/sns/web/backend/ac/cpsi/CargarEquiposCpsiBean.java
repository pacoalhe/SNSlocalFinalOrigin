package mx.ift.sns.web.backend.ac.cpsi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.cpsi.DetalleImportacionEquiposCpsi;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean asociado a la importación de equipos de señalización internacional.
 */
@ManagedBean(name = "cargarEquiposCpsiBean")
@ViewScoped
/** Bean encargado de procesar la carga de equipos de señalización internacionales. */
public class CargarEquiposCpsiBean implements Serializable {
    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CargarEquiposCpsiBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_CargarEquipos";

    /** Servicio de Catalogo. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade catalogoService;

    /** Tamaño máximo permitido para el archivo. */
    private String maxTamFichero;

    /** Proveedor seleccionado. */
    private Proveedor pst;

    /** Lista de proveedores. */
    private List<Proveedor> listaProveedores;

    /** Archivo. */
    private UploadedFile archivo;

    /** Lista de avisos. */
    private List<String> listaResultados;

    /** Render de los resultados. */
    private boolean existeError = false;

    /** Indica si el archivo se ha procesado. */
    private boolean habilitarProcesado;

    /** Detalle de la importación de los equipos. */
    private DetalleImportacionEquiposCpsi detalle;

    /** Ruta temporal del fichero a tratar. */
    private static final String RUTA_TEMP = "/app/sns/temporales/";

    /**
     * PostConstruct.
     * @throws Exception error en init
     */
    @PostConstruct
    public void init() throws Exception {
        maxTamFichero = catalogoService.getParamByName("maxTamFicheroEquiposCPS");

        // Listado de proveedores de servicio
        listaProveedores = catalogoService.findAllProveedores();
        pst = new Proveedor();

        detalle = null;
        habilitarProcesado = false;
    }

    /**
     * Metodo que sube el fichero al servidor a una ruta temporal.
     * @param event evento.
     */
    public void subirArchivo(FileUploadEvent event) {
        archivo = event.getFile();

        try {
            File fichero = new File(RUTA_TEMP);
            if (!fichero.exists()) {
                fichero.mkdirs();
            } else {
                LOGGER.info("Directorios ya creados.");
            }

            FileOutputStream fos = new FileOutputStream(RUTA_TEMP + archivo.getFileName());
            fos.write(archivo.getContents());
            fos.flush();
            fos.close();

            MensajesBean.addInfoMsg(MSG_ID, "Archivo " + archivo.getFileName() + " adjuntado correctamente", "");
            habilitarProcesado = (archivo != null);

        } catch (FileNotFoundException e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
        } catch (IOException e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
        }
    }

    /** Método encargado de cargar los equipos del excel. */
    public void cargarEquipos() {
        try {
            if (archivo != null) {
                File fichero = new File(RUTA_TEMP + archivo.getFileName());

                // Procesamos el archivo
                InputStream in = archivo.getInputstream();
                OutputStream out = new FileOutputStream(fichero);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }

                in.close();
                out.flush();
                out.close();

                if (pst != null) {
                    detalle = catalogoService.procesarArchivoEquiposCpsi(fichero, pst);
                    if (detalle != null && detalle.getMsgError() != null) {
                        MensajesBean.addErrorMsg(MSG_ID, detalle.getMsgError(), "");
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, "Por favor seleccione un proveedor", "");
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Por favor adjunte un archivo a procesar", "");
            }
        } catch (FileNotFoundException e) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            LOGGER.error(e.getMessage());
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            LOGGER.error(e.getMessage());
        } finally {
            eliminarFichero();
        }

    }

    /** Método encargado de inicializar los datos de la ventana modal. */
    public void resetValues() {
        pst = new Proveedor();
        archivo = null;
        listaResultados = null;
        existeError = false;
        habilitarProcesado = false;
        detalle = null;
    }

    /**
     * Método que elimina de la ruta temporal el fichero subido y procesado.
     */
    private void eliminarFichero() {
        File fichero = new File(RUTA_TEMP + archivo.getFileName());
        if (fichero.exists()) {
            fichero.delete();
        } else {
            LOGGER.info("No se puede borrar el fichero ya que no existe.", "");
        }
        archivo = null;
        habilitarProcesado = false;
    }

    /**
     * Tamaño máximo permitido para el archivo.
     * @return String
     */
    public String getMaxTamFichero() {
        return maxTamFichero;
    }

    /**
     * Tamaño máximo permitido para el archivo.
     * @param maxTamFichero String
     */
    public void setMaxTamFichero(String maxTamFichero) {
        this.maxTamFichero = maxTamFichero;
    }

    /**
     * Proveedor seleccionado.
     * @return Proveedor
     */
    public Proveedor getPst() {
        return pst;
    }

    /**
     * Proveedor seleccionado.
     * @param pst Proveedor
     */
    public void setPst(Proveedor pst) {
        this.pst = pst;
    }

    /**
     * Lista de proveedores.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Lista de proveedores.
     * @param listaProveedores List<Proveedor>
     */
    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    /**
     * Archivo.
     * @return UploadedFile
     */
    public UploadedFile getArchivo() {
        return archivo;
    }

    /**
     * Archivo.
     * @param archivo the archivo to set
     */
    public void setArchivo(UploadedFile archivo) {
        this.archivo = archivo;
    }

    /**
     * Lista de avisos.
     * @return List<String>
     */
    public List<String> getListaResultados() {
        return listaResultados;
    }

    /**
     * Lista de avisos.
     * @param listaResultados List<String>
     */
    public void setListaResultados(List<String> listaResultados) {
        this.listaResultados = listaResultados;
    }

    /**
     * Render de los resultados.
     * @return boolean
     */
    public boolean isExisteError() {
        return existeError;
    }

    /**
     * Render de los resultados.
     * @param existeError boolean
     */
    public void setExisteError(boolean existeError) {
        this.existeError = existeError;
    }

    /**
     * Indica si el archivo se ha procesado.
     * @return boolean
     */
    public boolean isHabilitarProcesado() {
        return habilitarProcesado;
    }

    /**
     * Indica si el archivo se ha procesado.
     * @param habilitarProcesado boolean
     */
    public void setHabilitarProcesado(boolean habilitarProcesado) {
        this.habilitarProcesado = habilitarProcesado;
    }

    /**
     * Detalle de la importación de los equipos.
     * @return DetalleImportacionEquiposCpsi
     */
    public DetalleImportacionEquiposCpsi getDetalle() {
        return detalle;
    }

    /**
     * Detalle de la importación de los equipos.
     * @param detalle DetalleImportacionEquiposCpsi
     */
    public void setDetalle(DetalleImportacionEquiposCpsi detalle) {
        this.detalle = detalle;
    }

    /**
     * Comprueba si hay errores que mostrar en pantalla.
     * @return booleano que indica si hay errores a mostrar
     */
    public boolean getExisteErrores() {
        return detalle != null && detalle.getErroresValidacion() != null && !detalle.getErroresValidacion().isEmpty();
    }
}
