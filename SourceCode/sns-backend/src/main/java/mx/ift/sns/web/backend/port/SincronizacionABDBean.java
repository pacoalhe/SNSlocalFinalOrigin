package mx.ift.sns.web.backend.port;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.negocio.exceptions.SincronizacionABDException;
import mx.ift.sns.negocio.port.IPortabilidadService;
import mx.ift.sns.negocio.port.TipoFicheroPort;
import mx.ift.sns.utils.file.FicheroTemporal;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.common.session.LoginBean;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean de sincronizacion diaria de portabilidad.
 */
@ManagedBean(name = "syncABDBean")
@ViewScoped
public class SincronizacionABDBean extends Wizard {

    /** UID Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SincronizacionABDBean.class);

    /** Servicio de Portabilidada. */
    @EJB(mappedName = "PortabilidadService")
    private IPortabilidadService portService;

    /** Fichero subido. */
    private UploadedFile uploadedFile;

    /** Nombre del fichero. */
    private String fileName = "";

    /** Tamaño máximo del fichero permitido. */
    private long fileSizeLimit = 104857600L;

    /** Desactiva boton validar ficheros. */
    private boolean disableBotonValidar = true;

    /** Tipo de fichero seleccionado. */
    private TipoFicheroPort tipoFichero;

    /** Lista de ficheros disponibles. */
    private static final List<TipoFicheroPort> LISTA_TIPOS_FICHEROS =
            new ArrayList<TipoFicheroPort>() {
                private static final long serialVersionUID = 1L;

                {
                    add(TipoFicheroPort.TIPO_DIARIO_PORTED);
                    add(TipoFicheroPort.TIPO_DIARIO_DELETED);
                }
            };

    /** Login bean. */
    @ManagedProperty("#{loginBean}")
    private LoginBean loginBean;

    /**
     * Inicializacion del bean.
     */
    @PostConstruct
    public void init() {
        LOGGER.debug("");

        setWizardMessagesId("MSG_SincronizacionABD");

        setTitulo("Sincronizacion Diaria ABD");
    }

    /**
     * listener que llama al negocio para validar el archivo.
     */
    public void validarFichero() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validar ficheros listener {}", tipoFichero);
        }

        try {
            if (tipoFichero != null) {
                if(tipoFichero.getCdg().equals(tipoFichero.DIARIO_PORTED)) {
                    File targetFile = new File("/app/sns/portacionManual/portados/" + uploadedFile.getFileName());
                    FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), targetFile);
                    MensajesBean.addInfoMsg(getWizardMessagesId(), "Carga de fichero correcta.");
                }else if(tipoFichero.getCdg().equals(tipoFichero.DIARIO_DELETED)){
                    File targetFile = new File("/app/sns/portacionManual/cancelados/" + uploadedFile.getFileName());
                    FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), targetFile);
                    MensajesBean.addInfoMsg(getWizardMessagesId(), "Carga de fichero correcta.");
                }else{
                    MensajesBean.addErrorMsg(getWizardMessagesId(), "No se ha seleccionado un tipo de archivo valido.");
                }
            } else {
                MensajesBean.addErrorMsg(getWizardMessagesId(), "No se ha seleccionado el tipo de archivo.");
            }

        } catch (IOException e) {
            MensajesBean.addErrorMsg(getWizardMessagesId(), "Error en carga de fichero.");
            LOGGER.error("error ejecutando sync ABD.", e);
        }
    }

    /**
     * Upload del fichero de arrendatarios.
     * @param event evento
     */
    public void uploadFichero(FileUploadEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fichero  " + event.getFile().getFileName());
        }
        uploadedFile = event.getFile();
        fileName = event.getFile().getFileName();

        disableBotonValidar = false;

    }

    /**
     * Fichero subido.
     * @return UploadedFile
     */
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    /**
     * Fichero subido.
     * @param uploadedFile UploadedFile
     */
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    /**
     * Nombre del fichero.
     * @return String
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Nombre del fichero.
     * @param fileName String
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Tamaño máximo del fichero permitido.
     * @return long
     */
    public long getFileSizeLimit() {
        return fileSizeLimit;
    }

    /**
     * Tamaño máximo del fichero permitido.
     * @param fileSizeLimit long
     */
    public void setFileSizeLimit(long fileSizeLimit) {
        this.fileSizeLimit = fileSizeLimit;
    }

    /**
     * Desactiva boton validar ficheros.
     * @return boolean
     */
    public boolean isDisableBotonValidar() {
        return disableBotonValidar;
    }

    /**
     * Desactiva boton validar ficheros.
     * @param disableBotonValidar boolean
     */
    public void setDisableBotonValidar(boolean disableBotonValidar) {
        this.disableBotonValidar = disableBotonValidar;
    }

    /**
     * Tipo de fichero seleccionado.
     * @return the tipoFichero
     */
    public TipoFicheroPort getTipoFichero() {
        return tipoFichero;
    }

    /**
     * Tipo de fichero seleccionado.
     * @param tipoFichero the tipoFichero to set
     */
    public void setTipoFichero(TipoFicheroPort tipoFichero) {
        this.tipoFichero = tipoFichero;
    }

    /**
     * Lista de ficheros disponibles.
     * @return List<TipoFicheroPort>
     */
    public List<TipoFicheroPort> getListaTiposFichero() {
        return LISTA_TIPOS_FICHEROS;
    }

    /**
     * Lista de ficheros disponibles.
     * @param listaTiposFichero List<TipoFicheroPort>
     */
    public void setListaTiposFichero(List<TipoFicheroPort> listaTiposFichero) {

    }

    /**
     * Login bean.
     * @return LoginBean
     */
    public LoginBean getLoginBean() {
        return loginBean;
    }

    /**
     * Login bean.
     * @param loginBean LoginBean
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }
}
