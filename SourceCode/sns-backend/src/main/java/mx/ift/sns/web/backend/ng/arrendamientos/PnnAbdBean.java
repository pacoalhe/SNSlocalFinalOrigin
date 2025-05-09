package mx.ift.sns.web.backend.ng.arrendamientos;

import java.io.File;
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

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.negocio.ng.model.ComparacionRangoError;
import mx.ift.sns.negocio.ng.model.FormatoError;
import mx.ift.sns.negocio.ng.model.RangoNoAsignadoError;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.arrendamientos.tabs.CargarArchivosTab;
import mx.ift.sns.web.backend.ng.arrendamientos.tabs.ErroresArchivosTab;
import mx.ift.sns.web.backend.ng.arrendamientos.tabs.ErroresComparacionTab;
import mx.ift.sns.web.backend.ng.arrendamientos.tabs.ErroresFormatoTab;
import mx.ift.sns.web.backend.ng.arrendamientos.tabs.ResumenTab;
import mx.ift.sns.web.backend.util.PaginatorUtil;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wizard de validacion de archivos de arrendatarios y arrendadores.
 */
@ManagedBean(name = "pnnABDBean")
@ViewScoped
public class PnnAbdBean extends Wizard implements Serializable {

    /** UID Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(PnnAbdBean.class);

    /** Servicio de Numeración Geográfica. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Fichero de carga del arrendatario. */
    private UploadedFile uploadedFileArrendatario;

    /** Fichero de carga del arrendador. */
    private UploadedFile uploadedFileArrendador;

    /** Nombre del fichero de carga del arrendatario. */
    private String fileNameArrendatario = "";

    /** Nombre del fichero de carga del arrendador. */
    private String fileNameArrendador = "";

    /** Maximo tamaño de fichero. */
    private long fileSizeLimit = 1048576L;

    /** Lista errores de formato. */
    private List<FormatoError> listaErroresFormato;

    /** Lista errores fichero arrendatario. */
    private List<RangoNoAsignadoError> listaErroresRangosPnn;

    /** Lista errores comparacion. */
    private List<ComparacionRangoError> listaErroresComparacion;

    /** Desactiva boton validar ficheros. */
    private boolean disableBotonValidar = true;

    /** Desactiva boton validar generar tablas. */
    private boolean disableBotonGenerar = false;

    /** Ficheros validados. */
    private boolean validados = false;

    /** Modelo de la tabla resumen. */
    private SeriesArrendadasLazyModel resumenModelo;

    /** Tab/Pestaña de selección de archivos de carga. */
    private CargarArchivosTab tabCargarArchivos;

    /** Tab/Pestaña de errores de formato de los archivos de carga. */
    private ErroresFormatoTab tabErroresFormato;

    /** Tab/Pestaña de errores de comparación de los archivos. */
    private ErroresComparacionTab tabErroresComparacion;

    /** Tab/Pestaña de errores de los archivos. */
    private ErroresArchivosTab tabErroresArchivos;

    /** Tab/Pestaña de resumen. */
    private ResumenTab tabResumen;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_PnnABD";

    /**
     * Inicializacion del bean.
     */
    @PostConstruct
    public void init() {
        LOGGER.debug("");

        // Pestaña 'Carga de Archivos'
        tabCargarArchivos = new CargarArchivosTab(this, ngService);
        tabCargarArchivos.setId("Planes ABD - CargarArchivosTab");
        getPasosWizard().add(tabCargarArchivos);

        // Pestaña 'Errores de Formato'
        tabErroresFormato = new ErroresFormatoTab(this, ngService);
        tabErroresFormato.setId("Planes ABD - ErroresFormatoTab");
        getPasosWizard().add(tabErroresFormato);

        // Pestaña 'Errores en la comparación de los archivos'
        tabErroresComparacion = new ErroresComparacionTab(this, ngService);
        tabErroresComparacion.setId("Planes ABD - ErroresComparacionTab");
        getPasosWizard().add(tabErroresComparacion);

        // Pestaña 'Errores en los archivos'
        tabErroresArchivos = new ErroresArchivosTab(this, ngService);
        tabErroresArchivos.setId("Planes ABD - ErroresArchivosTab");
        getPasosWizard().add(tabErroresArchivos);

        // Pestaña 'Resumen'
        tabResumen = new ResumenTab(this, ngService);
        tabResumen.setId("Planes ABD - ResumenTab");
        getPasosWizard().add(tabResumen);

        int registrosPorPagina = PaginatorUtil
                .getRegistrosPorPagina(ngService, Parametro.REGISTROS_POR_PAGINA_BACK_WIZ);

        this.registroPorPagina = registrosPorPagina;

        resumenModelo = new SeriesArrendadasLazyModel();
        resumenModelo.setService(ngService);
    }

    /**
     * Método encargado de ejecutar el proceso de generación de planes ABD.
     */
    public void procesarFicheros() {
        String arrendador = null;
        String arrendatario = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se inicia el procesamiento de los archivos para la generación de los planes ABD.");
        }

        disableBotonGenerar = false;

        // Se comprueba que los archivos de arrendamiento y arrendador no sean nulos
        if ((uploadedFileArrendador != null) && (uploadedFileArrendatario != null)) {
            try {
                arrendador = copyFile(uploadedFileArrendador, uploadedFileArrendador.getFileName());
                arrendatario = copyFile(uploadedFileArrendatario, uploadedFileArrendatario.getFileName());

                ResultadoValidacionArrendamiento resultado = ngService.procesarFicherosAbd(arrendador, arrendatario,
                        uploadedFileArrendador.getFileName(), uploadedFileArrendatario.getFileName());

                if (resultado.isError()) {
                    StringBuffer mensajes = new StringBuffer();

                    if (resultado.getErrorArrendatario() != ResultadoValidacionArrendamiento.VALIDACION_OK) {
                        mensajes.append("Error validando fichero arrendatario: ").append(fileNameArrendatario)
                                .append(". ").append(resultado.getErrorArrendatarioTxt()).append("<br>");
                    }

                    if (resultado.getErrorArrendador() != ResultadoValidacionArrendamiento.VALIDACION_OK) {
                        mensajes.append("Error validando fichero arrendador: ").append(fileNameArrendador)
                                .append(". ").append(resultado.getErrorArrendadorTxt()).append("<br>");
                    }

                    MensajesBean.addErrorMsg(MSG_ID, mensajes.toString(), "");
                    LOGGER.debug("error {}", mensajes.toString());
                } else {
                    validados = true;

                    listaErroresFormato = resultado.getListaErroresFormato();
                    listaErroresRangosPnn = resultado.getListaRangosNoAsignados();
                    listaErroresComparacion = resultado.getListaErroresComparacion();
                    disableBotonValidar = true;

                    if (!listaErroresFormato.isEmpty() || !listaErroresComparacion.isEmpty()) {
                        MensajesBean.addErrorMsg(MSG_ID,
                                "Ficheros validados con errores. Continúe para ver el resultado.",
                                "");
                    } else {
                        MensajesBean.addInfoMsg(MSG_ID,
                                "Ficheros validados correctamente. Continúe para ver el resultado.",
                                "");
                    }
                }
            } catch (IOException ioe) {
                LOGGER.error("Error al procesar los archivos ABD: ", ioe.getMessage());
                MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("pnnABD.validacion.error"), "");
            } finally {
                if (arrendador != null) {
                    File f = new File(arrendador);
                    f.delete();
                }

                if (arrendatario != null) {
                    File f = new File(arrendatario);
                    f.delete();
                }
            }
        } else {
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("pnnABD.validacion.numArchivos"), "");
        }

    }

    /**
     * Genera las tablas padre e hijo.
     */
    public void generarRegistrosAbd() {
        ngService.generarRegistrosAbd();
        disableBotonGenerar = true;
        MensajesBean.addInfoMsg(MSG_ID, "Proceso de generación de hijos y padres realizado correctamente.", "");
    }

    /**
     * Upload del fichero de arrendadores.
     * @param event evento
     */
    public void uploadFicheroArrendador(FileUploadEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fichero arrendador " + event.getFile().getFileName());
        }

        uploadedFileArrendador = event.getFile();
        fileNameArrendador = event.getFile().getFileName();

        if ((uploadedFileArrendador != null) && (uploadedFileArrendatario != null)) {
            disableBotonValidar = false;
        }
    }

    /**
     * Upload del fichero de arrendatarios.
     * @param event evento
     */
    public void uploadFicheroArrendatario(FileUploadEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fichero arrendado " + event.getFile().getFileName());
        }
        uploadedFileArrendatario = event.getFile();
        fileNameArrendatario = event.getFile().getFileName();

        if ((uploadedFileArrendador != null) && (uploadedFileArrendatario != null)) {
            disableBotonValidar = false;
        }
    }

    @Override
    public void resetTabs() {
        LOGGER.debug("");

        listaErroresFormato = null;
        listaErroresRangosPnn = null;
        listaErroresComparacion = null;

        super.resetTabs();
    }

    /**
     * copia, dnodne? quien borra? esto no esta terminado.
     * @param uploadedFile d
     * @param randomFilename d
     * @return nombre
     * @throws IOException error
     */
    private String copyFile(UploadedFile uploadedFile, String randomFilename) throws IOException {

        InputStream originalFile = uploadedFile.getInputstream();
        OutputStream fileOutput = null;

        try {

            File tmp = File.createTempFile("sns-", "-" + randomFilename, null);
            fileOutput = new FileOutputStream(tmp.getCanonicalPath());
            IOUtils.copy(originalFile, fileOutput);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("{} -> {}", uploadedFile.getFileName(), tmp.getCanonicalPath());
            }

            return tmp.getCanonicalPath();

        } finally {
            IOUtils.closeQuietly(fileOutput);
            IOUtils.closeQuietly(originalFile);
        }
    }

    /**
     * Fichero de carga del arrendador.
     * @return uploadedFileArrendador
     */
    public UploadedFile getUploadedFileArrendador() {
        return uploadedFileArrendador;
    }

    /**
     * Fichero de carga del arrendador.
     * @param uploadedFileArrendador UploadedFile
     */
    public void setUploadedFileArrendador(UploadedFile uploadedFileArrendador) {
        this.uploadedFileArrendador = uploadedFileArrendador;
    }

    /**
     * Maximo tamaño de fichero.
     * @return fileSizeLimit
     */
    public long getFileSizeLimit() {
        return fileSizeLimit;
    }

    /**
     * Fichero de carga del arrendatario.
     * @return uploadedFileArrendatario
     */
    public UploadedFile getUploadedFileArrendatario() {
        return uploadedFileArrendatario;
    }

    /**
     * Fichero de carga del arrendatario.
     * @param uploadedFileArrendatario UploadedFile
     */
    public void setUploadedFileArrendatario(UploadedFile uploadedFileArrendatario) {
        this.uploadedFileArrendatario = uploadedFileArrendatario;
    }

    /**
     * Lista errores de la comparación del archivo arrendador contra PNN.
     * @return listaErroresRangosPnn
     */
    public List<RangoNoAsignadoError> getListaErroresRangoPnn() {
        return listaErroresRangosPnn;
    }

    /**
     * Lista errores de la comparación del archivo arrendador contra PNN.
     * @param listaErroresRangosPnn List<RangoNoAsignadoError>
     */
    public void setListaErroresRangoPnn(List<RangoNoAsignadoError> listaErroresRangosPnn) {
        this.listaErroresRangosPnn = listaErroresRangosPnn;
    }

    /**
     * Nombre del fichero de carga del arrendatario.
     * @return fileNameArrendatario
     */
    public String getFileNameArrendatario() {
        return fileNameArrendatario;
    }

    /**
     * Nombre del fichero de carga del arrendatario.
     * @param fileNameArrendatario String
     */
    public void setFileNameArrendatario(String fileNameArrendatario) {
        this.fileNameArrendatario = fileNameArrendatario;
    }

    /**
     * Nombre del fichero de carga del arrendador.
     * @return fileNameArrendador
     */
    public String getFileNameArrendador() {
        return fileNameArrendador;
    }

    /**
     * Nombre del fichero de carga del arrendador.
     * @param fileNameArrendador String
     */
    public void setFileNameArrendador(String fileNameArrendador) {
        this.fileNameArrendador = fileNameArrendador;
    }

    /**
     * Desactiva boton validar ficheros.
     * @return disableBotonValidar
     */
    public boolean isDisableBotonValidar() {
        return disableBotonValidar;
    }

    /**
     * esactiva boton validar ficheros.
     * @param disableBotonValidar boolean
     */
    public void setDisableBotonValidar(boolean disableBotonValidar) {
        this.disableBotonValidar = disableBotonValidar;
    }

    /**
     * Ficheros validados.
     * @return validados
     */
    public boolean isValidados() {
        return validados;
    }

    /**
     * Ficheros validados.
     * @param validados boolean
     */
    public void setValidados(boolean validados) {
        this.validados = validados;
    }

    /**
     * Modelo de la tabla resumen.
     * @return resumenModelo
     */
    public SeriesArrendadasLazyModel getResumenModelo() {
        return resumenModelo;
    }

    /**
     * Modelo de la tabla resumen.
     * @param resumenModelo SeriesArrendadasLazyModel
     */
    public void setResumenModelo(SeriesArrendadasLazyModel resumenModelo) {
        this.resumenModelo = resumenModelo;
    }

    /**
     * listaErroresComparacion.
     * @return listaErroresComparacion
     */
    public List<ComparacionRangoError> getListaErroresComparacion() {
        return listaErroresComparacion;
    }

    /**
     * listaErroresComparacion.
     * @param listaErroresComparacion List<ComparacionRangoError>
     */
    public void setListaErroresComparacion(List<ComparacionRangoError> listaErroresComparacion) {
        this.listaErroresComparacion = listaErroresComparacion;
    }

    /**
     * Desactiva boton validar generar tablas.
     * @return disableBotonGenerar
     */
    public boolean isDisableBotonGenerar() {
        return disableBotonGenerar;
    }

    /**
     * Desactiva boton validar generar tablas.
     * @param disableBotonGenerar boolean
     */
    public void setDisableBotonGenerar(boolean disableBotonGenerar) {
        this.disableBotonGenerar = disableBotonGenerar;
    }

    /**
     * Lista errores de formato.
     * @return the listaErroresFormato
     */
    public List<FormatoError> getListaErroresFormato() {
        return listaErroresFormato;
    }

    /**
     * Lista errores de formato.
     * @param listaErroresFormato the listaErroresFormato to set
     */
    public void setListaErroresFormato(List<FormatoError> listaErroresFormato) {
        this.listaErroresFormato = listaErroresFormato;
    }

    /**
     * Lista errores fichero arrendatario.
     * @return the listaErroresRangosPnn
     */
    public List<RangoNoAsignadoError> getListaErroresRangosPnn() {
        return listaErroresRangosPnn;
    }

    /**
     * Lista errores fichero arrendatario.
     * @param listaErroresRangosPnn the listaErroresRangosPnn to set
     */
    public void setListaErroresRangosPnn(List<RangoNoAsignadoError> listaErroresRangosPnn) {
        this.listaErroresRangosPnn = listaErroresRangosPnn;
    }

    /**
     * @return the registroPorPagina
     */
    public int getRegistroPorPagina() {
        return registroPorPagina;
    }

    /**
     * @param registroPorPagina the registroPorPagina to set
     */
    public void setRegistroPorPagina(int registroPorPagina) {
        this.registroPorPagina = registroPorPagina;
    }
}
