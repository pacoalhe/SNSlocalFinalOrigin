package mx.ift.sns.web.backend.ng.asignacion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.negocio.ng.model.AvisoValidacionCentral;
import mx.ift.sns.negocio.ng.model.ErrorValidacion;
import mx.ift.sns.negocio.ng.model.RetornoProcesaFicheroAsignacion;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * clase que realiza la carga del fichero.
 */
public class CargarFicheroTab extends TabWizard {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CargarFicheroTab.class);

    /** registros. */
    private String registros;

    /** totalAsignacion. */
    private String totalAsignacion;

    /** totalEncabezado. */
    private String totalEncabezado;

    /** Archivo subido. */
    private UploadedFile uploadedFile;

    /** Indica si el botón de cargar oficio está disponible. */
    private boolean anadirCentral = false;
    
    private boolean anadirCentralInterna=false;

    /** Indica si el botónn de procesar fichero está disponible. */
    private boolean procesarHabilitado = false;

    /**
     * objeto que contiene la lista de errores y los objetos necesarios para ir a la pantalla de Generales con los datos
     * precargados.
     */
    private RetornoProcesaFicheroAsignacion procesaFichero;

    /** ngService. */
    private INumeracionGeograficaService ngService;

    /** Solicitud con la información del oficio. */
    private SolicitudAsignacion solicitudAsignacion;

    /** Ruta del fichero de carga. */
    private String ruta;

    /** Tamaño máximo del fichero. */
    private String maxTamFichero;

    /** Lista de errores. */
    private List<ErrorValidacion> listaErrores;

    /**
     * CargarFicheroTab.
     * @param pWizard pWizard.
     * @param pNgService pNgService.
     */
    public CargarFicheroTab(Wizard pWizard, INumeracionGeograficaService pNgService) {

        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);

        solicitudAsignacion = (SolicitudAsignacion) pWizard.getSolicitud();

        ngService = pNgService;

        maxTamFichero = ngService.getParamByName("maxTamFicheroAsigAutomatica");

        listaErrores = new ArrayList<ErrorValidacion>();
    }

    /**
     * Metodo que sube el fichero al servidor a una ruta temporal.
     * @param event evento.
     */
    public void uploadAttachment(FileUploadEvent event) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("upload fichero {}", event.getFile().getFileName());
            LOGGER.debug("size fichero {}", event.getFile().getSize());
        }

        uploadedFile = event.getFile();

        ruta = "/app/sns/temporales/ficheros_asignacion/";

        try {

            File fichero = new File(ruta);
            if (!fichero.exists()) {
                fichero.mkdirs();
            } else {
                LOGGER.info("Directorios ya creados.");
            }

            FileOutputStream fos = new FileOutputStream(ruta + uploadedFile.getFileName());
            fos.write(uploadedFile.getContents());
            fos.flush();
            fos.close();

            MensajesBean.addInfoMsg("MSG_CargaFichero",
                    "Fichero adjuntado correctamente");

            // Habilitamos el botón de cargar si procese
            procesarHabilitado = (uploadedFile != null);
        } catch (FileNotFoundException e) {
            MensajesBean.addErrorMsg("MSG_CargaFichero", "Error en carga de fichero.");
        } catch (IOException e) {
            MensajesBean.addErrorMsg("MSG_CargaFichero", "Error en carga de fichero.");
        }
    }

    
    
    
    /**
     * Método para procesar el fichero.
     * @param event evento.
     */
    public void ejecutarProceso(ActionEvent event) {
    	
    	this.ejecutarProcesoInterno();
        
    }
    
    /**
     * This method is for execute internal process for the File
     * when the Centrales has been adding to the catalogo
     */
    public void ejecutarProcesoInterno(){
    	
    	if (uploadedFile != null) {
            File fichero = new File(ruta + uploadedFile.getFileName());

            try {
                InputStream in = uploadedFile.getInputstream();
                OutputStream out = new FileOutputStream(fichero);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }

                in.close();
                out.flush();
                out.close();

                listaErrores = new ArrayList<ErrorValidacion>();

                procesaFichero = ngService.validarFicheroAsignacionAutomatica(fichero);

                if (StringUtils.isEmpty(procesaFichero.getMensajeError())) {
                    if (procesaFichero.getlErrores().isEmpty()) {
                        if (procesaFichero.getlAviso().isEmpty()) {

                            // Creamos la nueva solicitud
                            solicitudAsignacion = procesaFichero.getSolicitudAsignacion();
                            List<NumeracionSolicitada> listaNumeraciones = new ArrayList<NumeracionSolicitada>(
                                    solicitudAsignacion.getNumeracionSolicitadas());
                            solicitudAsignacion.getNumeracionSolicitadas().clear();
                            // Tenemos que persisistir primero la solicitud y luego las numeraciones solicitadas.
                            solicitudAsignacion = ngService.saveSolicitudAsignacion(solicitudAsignacion);
                            for (NumeracionSolicitada numSol : listaNumeraciones) {
                                solicitudAsignacion.addNumeracionSolicitadas(numSol);
                            }
                            solicitudAsignacion = ngService.saveSolicitudAsignacion(solicitudAsignacion);
                            if(!this.anadirCentralInterna)
                            {
                            MensajesBean.addInfoMsg("MSG_CargaFichero", "Fichero sin errores");
                            }
                            // Actualizamos la solicitud general con la nueva instancia.
                            getWizard().setSolicitud(solicitudAsignacion);

                            if (procesaFichero.getlAvisoNoOblig() != null
                                    && !procesaFichero.getlAvisoNoOblig().isEmpty()) {
                                if(!anadirCentralInterna){
                            	MensajesBean.addInfoMsg("MSG_CargaFichero", "Fichero con Avisos. El proceso continúa");
                                }
                            }
                            

                        } else {
                            anadirCentral = true;
                            anadirCentralInterna=true;
                            MensajesBean.addInfoMsg("MSG_CargaFichero", "Fichero con Avisos. Debe agregar las centrales listadas para continuar");
                            
                        }
                    } else {
                        MensajesBean.addErrorMsg("MSG_CargaFichero", "Fichero con errores");
                        this.eliminarFichero();
                    }

                    // Añadimos avisos a la lista de errores
                    listaErrores.addAll(procesaFichero.getlErrores());
                    listaErrores.addAll(procesaFichero.getlAvisoNoOblig());
                    listaErrores.addAll(procesaFichero.getlAviso());

                } else {
                    MensajesBean.addErrorMsg("MSG_CargaFichero", procesaFichero.getMensajeError());
                    this.eliminarFichero();
                }

            } catch (Exception e) {
                MensajesBean.addErrorMsg("MSG_CargaFichero", "Error al procesar el fichero.");
                LOGGER.error("Error al procesar el fichero", e);
                this.eliminarFichero();
            }

        } else {
            MensajesBean.addErrorMsg("MSG_CargaFichero",
                    "No ha subido ningún fichero a procesar.");
        }
    
    }

    /**
     * Metodo que añade las centrales que existan en la lista lAviso.
     */
    public void anadirCentral() {
        try {

            for (AvisoValidacionCentral aviso : procesaFichero.getlAviso()) {
                Central central = ngService.comprobarCentral(aviso.getCentral());
                if (central.getId() == null) {
                    central.setEstatus(new Estatus());
                    central.getEstatus().setCdg(Estatus.ACTIVO);
                    ngService.saveCentralFromAsignacion(central);
                }
            }
            procesaFichero.setlErrores(null);
            //Tambien el aviso se debe poner a null
            //ya que este metodo ejecuta la insercion de Centrales
            //que deben agregarse, y una vez agregadas el aviso debe ser null
            procesaFichero.setlAviso(null);
            anadirCentral = false;
            MensajesBean.addInfoMsg("MSG_CargaFichero", "Centrales guardadas con exito");
        } catch (Exception e) {
        	e.printStackTrace();
            MensajesBean.addErrorMsg("MSG_CargaFichero", "Error: Consulte a su Administrador");
        }
    }

    @Override
    public void resetTab() {
        uploadedFile = null;
        procesaFichero = null;
        anadirCentral = false;
        anadirCentralInterna=false;
        procesarHabilitado = false;
        solicitudAsignacion = new SolicitudAsignacion();
        listaErrores = new ArrayList<ErrorValidacion>();
    }

    @Override
    public boolean isAvanzar() {
        // Si tiene id significa que ya se ha creado la solicitud, con lo cual no hay errores
        if (solicitudAsignacion.getId() != null) {
            procesaFichero.setlErrores(null);
        }
        boolean valor = (procesaFichero != null)
                && (procesaFichero.getlErrores() == null || procesaFichero.getlErrores().isEmpty())
                && (procesaFichero.getlAviso() == null || procesaFichero.getlAviso().isEmpty());

        if (!valor) {
            MensajesBean.addErrorMsg("MSG_CargaFichero",
                    "No puede ir a la siguiente pantalla sin procesar un fichero primero.");
        } else if(this.anadirCentralInterna){
        	//Se debe llamar al este metodo ya que 
        	//al existir centrales por añadir no se ejecuto
        	//en primera instancia.
        	this.ejecutarProcesoInterno();
            this.eliminarFichero();
            this.anadirCentralInterna=false;
        }else{
        	this.eliminarFichero();
        }

        return valor;
    }

    /**
     * Método que elimina de la ruta temporal el fichero subido y procesado.
     */
    private void eliminarFichero() {
        File fichero = new File(ruta + uploadedFile.getFileName());
        if (fichero.exists()) {
            fichero.delete();
        } else {
            LOGGER.info("No se puede borrar el fichero ya que no existe.");
        }
    }

    @Override
    public String getMensajeError() {

        return null;
    }

    /************************ getter y setter **************************/
    /**
     * @return the registros
     */
    public String getRegistros() {
        return registros;
    }

    /**
     * @param registros the registros to set
     */
    public void setRegistros(String registros) {
        this.registros = registros;
    }

    /**
     * @return the totalAsignacion
     */
    public String getTotalAsignacion() {
        return totalAsignacion;
    }

    /**
     * @param totalAsignacion the totalAsignacion to set
     */
    public void setTotalAsignacion(String totalAsignacion) {
        this.totalAsignacion = totalAsignacion;
    }

    /**
     * @return the totalEncabezado
     */
    public String getTotalEncabezado() {
        return totalEncabezado;
    }

    /**
     * @param totalEncabezado the totalEncabezado to set
     */
    public void setTotalEncabezado(String totalEncabezado) {
        this.totalEncabezado = totalEncabezado;
    }

    /**
     * @return the procesaFichero
     */
    public RetornoProcesaFicheroAsignacion getProcesaFichero() {
        return procesaFichero;
    }

    /**
     * @param procesaFichero the procesaFichero to set
     */
    public void setProcesaFichero(RetornoProcesaFicheroAsignacion procesaFichero) {
        this.procesaFichero = procesaFichero;
    }

    @Override
    public void actualizaCampos() {
        solicitudAsignacion = (SolicitudAsignacion) getWizard().getSolicitud();
    }

    /**
     * @return the anadirCentral
     */
    public boolean isAnadirCentral() {
        return anadirCentral;
    }

    /**
     * @param anadirCentral the anadirCentral to set
     */
    public void setAnadirCentral(boolean anadirCentral) {
        this.anadirCentral = anadirCentral;
    }

    /**
     * Indica si el botónn de procesar fichero está disponible.
     * @return boolean
     */
    public boolean isProcesarHabilitado() {
        return procesarHabilitado;
    }

    /**
     * Indica si el botónn de procesar fichero está disponible.
     * @param procesarHabilitado boolean
     */
    public void setProcesarHabilitado(boolean procesarHabilitado) {
        this.procesarHabilitado = procesarHabilitado;
    }

    /**
     * @return the maxTamFichero
     */
    public String getMaxTamFichero() {
        return maxTamFichero;
    }

    /**
     * @param maxTamFichero the maxTamFichero to set
     */
    public void setMaxTamFichero(String maxTamFichero) {
        this.maxTamFichero = maxTamFichero;
    }

    /**
     * @return the listaErrores
     */
    public List<ErrorValidacion> getListaErrores() {
        return listaErrores;
    }

    /**
     * @param listaErrores the listaErrores to set
     */
    public void setListaErrores(List<ErrorValidacion> listaErrores) {
        this.listaErrores = listaErrores;
    }
}
