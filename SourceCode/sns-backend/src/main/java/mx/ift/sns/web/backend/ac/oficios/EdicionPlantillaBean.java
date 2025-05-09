package mx.ift.sns.web.backend.ac.oficios;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.oficios.Plantilla;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bean de Edición de Plantillas de Oficio. */
@ManagedBean(name = "edicionPlantillaBean")
@ViewScoped
public class EdicionPlantillaBean implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EdicionPlantillaBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_EdicionPlantilla";

    /** Facade de Catálogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Plantilla seleccionada para edición. */
    private Plantilla plantilla;

    /** Indica si estamos editando una plantilla o creando una nueva. */
    private boolean modoEdicion = false;

    /** Fichero seleccionado para actualizar. */
    private UploadedFile ficheroSubido = null;

    /** Indica si el botón de actualizar plantilla está disponible. */
    private boolean actualizacionDisponible = true;

    /** Indica si es posible descargar el documento. */
    private boolean descargaDisponible = false;

    /** Tamaño máximo de fichero para plantillas. */
    private String plantillaMaxSize = "";

    /** Ruta del fichero seleccionado para actualizar la plantilla. */
    private String pathFichero = "";

    /** Constructor vacío. */
    public EdicionPlantillaBean() {
    }

    /** Inicialización de Variables. JSR260, Método invocado al cargar terminar de instanciar el bean. */
    @PostConstruct
    public void init() {
        try {
            plantilla = new Plantilla();
            plantillaMaxSize = adminCatFacadeService.getParamByName(Parametro.FICH_OFICIO_SIZE);
        } catch (Exception e) {
            LOGGER.debug("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Carga una plantilla dada para edición.
     * @param pPlantilla Información de la plantilla.
     */
    public void cargarPlantilla(Plantilla pPlantilla) {
        plantilla = pPlantilla;
        modoEdicion = true;
        actualizacionDisponible = true;
        descargaDisponible = true;
    }

    /** Método invocado al cerrar la modal de Edición de Plantillas. */
    public void resetValues() {
        this.plantilla = new Plantilla();
        this.modoEdicion = false;
        this.ficheroSubido = null;
        this.actualizacionDisponible = true;
        this.descargaDisponible = false;
        this.pathFichero = "";
    }

    /**
     * Fichero seleccionado para actualizar la plantilla.
     * @param pEvent FileUploadEvent
     */
    public void cargarFichero(FileUploadEvent pEvent) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Fichero seleccionado: " + pEvent.getFile().getFileName());
            }

            // El chequeo se hace en el View mendiante métodos de PrimeFaces
            ficheroSubido = (pEvent.getFile());
            actualizacionDisponible = true;

            StringBuilder sbFile = new StringBuilder();
            sbFile.append(ficheroSubido.getFileName()).append(" (").append(ficheroSubido.getSize());
            sbFile.append(" bytes)");
            pathFichero = sbFile.toString();

            // Fichero cargado, se puede actualizar
            MensajesBean.addInfoMsg(MSG_ID, "Fichero cargado correctamente", "");

        } catch (Exception e) {
            LOGGER.debug("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            ficheroSubido = null;
            pathFichero = "";
        }
    }

    /** Método invocado al pulsar sobre el botón de Guardar. */
    public void guardar() {
        try {
            if (modoEdicion) {
                // Edición de Plantilla Existente.
                if (ficheroSubido != null) {
                    plantilla.setPlantilla(ficheroSubido.getContents());
                }
                plantilla = adminCatFacadeService.savePlantilla(plantilla);
                MensajesBean.addInfoMsg(MSG_ID, "Plantilla guardada correctamente", "");

                // Habilitamos controles
                actualizacionDisponible = true;
                descargaDisponible = true;
                ficheroSubido = null;
                pathFichero = "";

            } else {
                // Nueva Plantilla
                StringBuilder sbValidaciones = new StringBuilder();
                if (StringUtils.isEmpty(plantilla.getDescripcion())) {
                    sbValidaciones.append("Es necesario indicar una descripción para la plantilla.<br>");
                }
                if (plantilla.getTipoDestinatario() == null) {
                    sbValidaciones.append("Es necesario indicar un tipo de destinatario.<br>");
                }
                if (plantilla.getTipoSolicitud() == null) {
                    sbValidaciones.append("Es necesario indicar un tipo de trámite.<br>");
                }

                // Comprobamos que la plantilla no existe.
                if (plantilla.getTipoDestinatario() != null && plantilla.getTipoSolicitud() != null) {
                    Plantilla plantillaExistente = adminCatFacadeService.getPlantilla(
                            plantilla.getTipoSolicitud(), plantilla.getTipoDestinatario());
                    if (plantillaExistente != null) {
                        sbValidaciones.append("La plantilla ya existe "
                                + "para el tipo de solicitud y destinatario seleccionado.<br>");
                    }
                }

                if (ficheroSubido == null) {
                    sbValidaciones.append("Es necesario cargar un documento para la plantilla.<br>");
                }

                if (StringUtils.isEmpty(sbValidaciones.toString())) {
                    plantilla.setPlantilla(ficheroSubido.getContents());
                    plantilla = adminCatFacadeService.savePlantilla(plantilla);
                    MensajesBean.addInfoMsg(MSG_ID, "Plantilla guardada correctamente", "");

                    // Habilitamos controles
                    actualizacionDisponible = true;
                    descargaDisponible = true;
                    ficheroSubido = null;
                    pathFichero = "";
                    modoEdicion = true;
                } else {
                    MensajesBean.addErrorMsg(MSG_ID, sbValidaciones.toString(), "");
                }
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
            ficheroSubido = null;
            pathFichero = "";
            actualizacionDisponible = true;

        } catch (Exception e) {
            LOGGER.debug("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            ficheroSubido = null;
            pathFichero = "";
            actualizacionDisponible = true;
        }
    }

    /** Método invocado al pulsar sobre el botón de Actualizar. */
    public void actualizarPlantilla() {
        actualizacionDisponible = false;
        ficheroSubido = null;
        pathFichero = "";
    }

    /**
     * Método invocado al pulsar sobre el botón 'Descargar Plantilla'.
     * @return StreamedContent Fichero a Descargar
     */
    public StreamedContent getDescargarPlantilla() {
        try {
            InputStream stream = new ByteArrayInputStream(plantilla.getPlantilla());
            StringBuffer docName = new StringBuffer();
            docName.append("Plantilla_").append(plantilla.getTipoSolicitud().getCdg()).append("_");
            docName.append(plantilla.getTipoDestinatario().getCdg()).append(".docx");

            // Word Mime Type
            String wordMimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            StreamedContent downFile = new DefaultStreamedContent(stream, wordMimeType, docName.toString());
            return downFile;

        } catch (Exception e) {
            LOGGER.debug("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            ficheroSubido = null;
        }
        return null;
    }

    // GETTERS & SETTERS

    /**
     * Plantilla seleccionada para edición.
     * @return Plantilla
     */
    public Plantilla getPlantilla() {
        return plantilla;
    }

    /**
     * Plantilla seleccionada para edición.
     * @param plantilla Plantilla
     */
    public void setPlantilla(Plantilla plantilla) {
        this.plantilla = plantilla;
    }

    /**
     * Indica si estamos editando una plantilla o creando una nueva.
     * @return boolean
     */
    public boolean isModoEdicion() {
        return modoEdicion;
    }

    /**
     * Indica si el botón de actualizar plantilla está disponible.
     * @return boolean
     */
    public boolean isActualizacionDisponible() {
        return actualizacionDisponible;
    }

    /**
     * Indica si es posible descargar el documento.
     * @return boolean
     */
    public boolean isDescargaDisponible() {
        return descargaDisponible;
    }

    /**
     * Tamaño máximo de fichero para plantillas.
     * @return String
     */
    public String getPlantillaMaxSize() {
        return plantillaMaxSize;
    }

    /**
     * Ruta del fichero seleccionado para actualizar la plantilla.
     * @return String
     */
    public String getPathFichero() {
        return pathFichero;
    }

    /**
     * Ruta del fichero seleccionado para actualizar la plantilla.
     * @param pathFichero String
     */
    public void setPathFichero(String pathFichero) {
        this.pathFichero = pathFichero;
    }

}
