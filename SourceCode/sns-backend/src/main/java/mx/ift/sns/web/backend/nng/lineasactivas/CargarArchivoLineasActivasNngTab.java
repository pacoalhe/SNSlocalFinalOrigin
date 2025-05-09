package mx.ift.sns.web.backend.nng.lineasactivas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import mx.ift.sns.modelo.lineas.DetalleLineaActivaDetNng;
import mx.ift.sns.modelo.lineas.DetalleLineaActivaNng;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaDetNng;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.modelo.nng.SolicitudLineasActivasNng;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.negocio.nng.RetornoProcesaFicheroReportesNng;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que carga la pestaña 'Cargar Archivo' de lineas activas no geografica.
 * @author X36155QU
 */
public class CargarArchivoLineasActivasNngTab extends TabWizard {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -8057611521842703207L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CargarArchivoLineasActivasNngTab.class);

    /** Fachada. */
    private INumeracionNoGeograficaFacade nnFacade;

    /** Tipo de reporte seleccionado. */
    private TipoReporte tipoReporte;

    /** Lista combo de tipos de reporte. */
    private List<TipoReporte> listaTiposReporte;

    /** Archivo subido. */
    private UploadedFile uploadedFile;

    /** Retorno de la validacion del archivo. */
    private RetornoProcesaFicheroReportesNng procesaFichero;

    /** Render de la tabla de errores. */
    private boolean renderTablaErrores = false;

    /** Render tabla Lineas Activas. */
    private boolean renderLineasActivas = false;

    /** Render tabla Lineas Activas Detallado. */
    private boolean renderLineasActivasDet = false;

    /** Render tabla Lineas Arrendatario. */
    private boolean renderLineasArrendatario = false;

    /** Render tabla de avisos. */
    private boolean renderAvisos = false;

    /** Indica si un archivo se ha procesado. */
    private boolean archivoProcesado = false;

    /** Lista resumen reporte lineas activas. */
    private ResumenReporteLineasActivasNngLazyModel listaResumenLineasActivas;

    /** Render tabla lineas arrendadas. */
    private boolean renderLineasArrendada = false;

    /** Ruta temporal del fichero a tratar. */
    private String ruta;

    /** Lista de avisos. */
    private List<String> listaAvisos;

    /**
     * Constructor.
     * @param wizard wizard
     * @param nnFacade INumeracionGeograficaService
     * @throws Exception error
     */
    public CargarArchivoLineasActivasNngTab(Wizard wizard, INumeracionNoGeograficaFacade nnFacade) throws Exception {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(wizard);

        this.nnFacade = nnFacade;

        procesaFichero = new RetornoProcesaFicheroReportesNng();
        procesaFichero.setProveedorRegistrado(true);

        listaTiposReporte = nnFacade.findAllTiposReporte();
        tipoReporte = null;

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

        ruta = "/app/sns/temporales/ficheros_lineas_activas/";

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

            MensajesBean.addInfoMsg("MSG_CargaFichero", "Fichero " + uploadedFile.getFileName()
                    + " adjuntado correctamente");

            archivoProcesado = (uploadedFile != null);

        } catch (FileNotFoundException e) {
            MensajesBean.addErrorMsg("MSG_CargaFichero", "Error en carga de fichero.");
        } catch (IOException e) {
            MensajesBean.addErrorMsg("MSG_CargaFichero", "Error en carga de fichero.");
        }
    }

    /**
     * Método para procesar el fichero.
     * @param event evento.
     * @throws Exception execpcion
     */
    public void ejecutarProceso(ActionEvent event) {

        if (uploadedFile != null) {
            File fichero = new File(ruta + uploadedFile.getFileName());

            try {
                // Procesamos el archivo
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

                if (tipoReporte != null) {
                    switch (tipoReporte.getCodigo()) {
                    case TipoReporte.LINEAS_ACTIVAS:
                        // Validamos archivo de lineas activas
                        setProcesaFichero(nnFacade.validarFicheroLineasActivas(fichero));
                        ReporteLineaActivaNng lineaActiva = (ReporteLineaActivaNng) procesaFichero.getReporte();

                        lineaActiva = guardaReporteLiniaActiva(lineaActiva);
                        procesaFichero.setReporte(lineaActiva);

                        listaResumenLineasActivas = new ResumenReporteLineasActivasNngLazyModel();
                        cargaTablaLineaActiva();

                        renderLineasActivas = true;
                        renderLineasActivasDet = false;
                        renderLineasArrendatario = false;
                        renderLineasArrendada = false;
                        break;
                    case TipoReporte.LINEAS_ACTIVAS_DET:
                        // Validamos archivo de lineas activas detallado
                        setProcesaFichero(nnFacade.validarFicheroLineasActivasDet(fichero));
                        ReporteLineaActivaDetNng lineaActivaDet =
                                (ReporteLineaActivaDetNng) procesaFichero.getReporte();

                        lineaActivaDet = guardaReporteLineaActivaDet(lineaActivaDet);
                        procesaFichero.setReporte(lineaActivaDet);

                        listaResumenLineasActivas = new ResumenReporteLineasActivasNngLazyModel();

                        cargaTablaLineaActiva();

                        renderLineasActivas = false;
                        renderLineasActivasDet = true;
                        renderLineasArrendatario = false;
                        renderLineasArrendada = false;
                        break;
                    case TipoReporte.LINEAS_ACTIVAS_ARRENDATARIO:
                        // Validamos archivo de lineas activas arrendatario
                        setProcesaFichero(nnFacade.validarFicheroLineasArrendatario(fichero));
                        ReporteLineaArrendatarioNng lineaArrendatario = (ReporteLineaArrendatarioNng) procesaFichero
                                .getReporte();

                        // Guardamos el reporte si procede
                        if (procesaFichero.isProveedorRegistrado() && procesaFichero.getlErrores().isEmpty()) {
                            SolicitudLineasActivasNng solicitud = nnFacade.saveSolicitudLineasActivas(lineaArrendatario
                                    .getSolicitudLineasActivas());
                            lineaArrendatario.setSolicitudLineasActivas(solicitud);

                            lineaArrendatario
                                    .setDetallesLineaArrendatario(new ArrayList<DetalleLineaArrendatarioNng>());

                            lineaArrendatario = nnFacade.saveLineaArrendatario(lineaArrendatario);
                            procesaFichero.setReporte(lineaArrendatario);

                            nnFacade
                                    .saveDetLineaArrendatario(procesaFichero.getListaDatos(),
                                            lineaArrendatario.getId());

                            MensajesBean.addInfoMsg(
                                    "MSG_CargaArchivo", MensajesBean.getTextoResource("manual.generales.consecutivo")
                                            + " " + lineaArrendatario.getId());
                        }

                        listaResumenLineasActivas = new ResumenReporteLineasActivasNngLazyModel();

                        cargaTablaLineaActiva();

                        renderLineasActivas = false;
                        renderLineasActivasDet = false;
                        renderLineasArrendatario = true;
                        renderLineasArrendada = false;
                        break;
                    case TipoReporte.LINEAS_ARRENDADAS:
                        // Validamos archivo de lineas arrendadas
                        setProcesaFichero(nnFacade.validarFicheroLineasArrendada(fichero));
                        ReporteLineaArrendadorNng lineaArrendada = (ReporteLineaArrendadorNng) procesaFichero
                                .getReporte();

                        // Guardamos el reporte si procede
                        if (procesaFichero.isProveedorRegistrado() && procesaFichero.getlErrores().isEmpty()) {
                            SolicitudLineasActivasNng solicitud = nnFacade.saveSolicitudLineasActivas(lineaArrendada
                                    .getSolicitudLineasActivas());
                            lineaArrendada.setSolicitudLineasActivas(solicitud);

                            lineaArrendada.setDetallesLineaArrendador(new ArrayList<DetalleLineaArrendadorNng>());
                            lineaArrendada = nnFacade.saveLineaArrendada(lineaArrendada);

                            nnFacade.saveDetLineaArrendada(procesaFichero.getListaDatos(), lineaArrendada.getId());

                            MensajesBean.addInfoMsg(
                                    "MSG_CargaArchivo", MensajesBean.getTextoResource("manual.generales.consecutivo")
                                            + " " + lineaArrendada.getId());
                        }

                        procesaFichero.setReporte(lineaArrendada);

                        listaResumenLineasActivas = new ResumenReporteLineasActivasNngLazyModel();
                        cargaTablaLineaActiva();

                        renderLineasActivas = false;
                        renderLineasActivasDet = false;
                        renderLineasArrendatario = false;
                        renderLineasArrendada = true;

                        break;
                    default:
                        MensajesBean
                                .addInfoMsg("MSG_CargaFichero", "Por favor seleccione un tipo de reporte a procesar");
                        break;
                    }
                    if (procesaFichero.getlErrores().size() > 0) {
                        // Si hay errores se avisa y mostramos la tabla de errores
                        MensajesBean.addErrorMsg("MSG_ArchivoProcesado", "Se han encontrado errores en el archivo", "");
                        renderTablaErrores = true;
                        archivoProcesado = false;
                        renderLineasActivas = false;
                        renderLineasActivasDet = false;
                        renderLineasArrendatario = false;
                        renderLineasArrendada = false;

                    } else {
                        renderTablaErrores = false;
                        archivoProcesado = true;
                    }
                    if (!procesaFichero.isProveedorRegistrado()) {
                        // Si el proveedor solicitante no esta registrado lanzamos un mensaje de aviso
                        MensajesBean.addWarningMsg(
                                "MSG_ArchivoProcesado",
                                "El archivo no se puede procesar hasta que el proveedor "
                                        + procesaFichero.getNombreProveedor() + " no esté registrado.", "");

                        archivoProcesado = false;
                        renderLineasActivas = false;
                        renderLineasActivasDet = false;
                        renderLineasArrendatario = false;
                        renderLineasArrendada = false;

                    }
                } else {
                    MensajesBean
                            .addInfoMsg("MSG_CargaFichero", "Por favor seleccione un tipo de reporte a procesar");
                }

            } catch (FileNotFoundException e) {
                MensajesBean.addErrorMsg("MSG_CargaFichero", Errores.ERROR_0004);
                LOGGER.error(e.getMessage());
            } catch (IOException e) {
                MensajesBean.addErrorMsg("MSG_CargaFichero", Errores.ERROR_0004);
                LOGGER.error(e.getMessage());
            } catch (Exception e) {
                MensajesBean.addErrorMsg("MSG_CargaFichero", Errores.ERROR_0004);
                LOGGER.error(e.getMessage());
            } finally {
                eliminarFichero();
            }
        }
    }

    /**
     * Guarda un reporte de linea activa detallado.
     * @param lineaActivaDet reporte
     * @return lineaActivaDet reporte
     */
    private ReporteLineaActivaDetNng guardaReporteLineaActivaDet(ReporteLineaActivaDetNng lineaActivaDet) {

        // Guardamos el reporte si procede
        if (procesaFichero.isProveedorRegistrado() && procesaFichero.getlErrores().isEmpty()) {
            SolicitudLineasActivasNng solicitud = nnFacade.saveSolicitudLineasActivas(lineaActivaDet
                    .getSolicitudLineasActivas());
            lineaActivaDet.setSolicitudLineasActivas(solicitud);
            lineaActivaDet.setDetallesLineaActivaDet(new ArrayList<DetalleLineaActivaDetNng>());
            lineaActivaDet = nnFacade.saveLineaActivaDet(lineaActivaDet);

            nnFacade.saveDetLineaActivaDet(procesaFichero.getListaDatos(), lineaActivaDet.getId());

            MensajesBean.addInfoMsg("MSG_CargaArchivo", MensajesBean.getTextoResource("manual.generales.consecutivo")
                    + " " + lineaActivaDet.getId());

        }
        return lineaActivaDet;
    }

    /**
     * Guarda un reporte de linea activa.
     * @param lineaActiva reporte
     * @return lineaActiva reporte
     */
    private ReporteLineaActivaNng guardaReporteLiniaActiva(ReporteLineaActivaNng lineaActiva) {

        if (procesaFichero.isProveedorRegistrado() && procesaFichero.getlErrores().isEmpty()) {
            LOGGER.debug("");
            SolicitudLineasActivasNng solicitud = nnFacade.saveSolicitudLineasActivas(lineaActiva
                    .getSolicitudLineasActivas());
            lineaActiva.setSolicitudLineasActivas(solicitud);

            lineaActiva.setDetallesLineaActiva(new ArrayList<DetalleLineaActivaNng>());
            lineaActiva = nnFacade.saveLineaActiva(lineaActiva);

            nnFacade.saveDetalleLineaActiva(procesaFichero.getListaDatos(), lineaActiva.getId());
            MensajesBean.addInfoMsg("MSG_CargaArchivo", MensajesBean.getTextoResource("manual.generales.consecutivo")
                    + " " + lineaActiva.getId());
        }
        return lineaActiva;
    }

    /**
     * Cargamos la tabla de lineas activas.
     */
    private void cargaTablaLineaActiva() {

        listaResumenLineasActivas.clear();
        listaResumenLineasActivas.setIdReporte(procesaFichero.getReporte().getId());
        listaResumenLineasActivas.setProveedor(procesaFichero.getReporte().getSolicitudLineasActivas()
                .getProveedorSolicitante());
        listaResumenLineasActivas.setTipoReporte(tipoReporte);
        listaResumenLineasActivas.setNumDatos(procesaFichero.getListaDatos().size());
        // listaResumenLineasActivas.setListaDatos(procesaFichero.getListaDatos());
        listaResumenLineasActivas.setFacade(nnFacade);

    }

    @Override
    public boolean isAvanzar() {

        return false;
    }

    @Override
    public void actualizaCampos() {

    }

    @Override
    public void resetTab() {
        tipoReporte = null;
        archivoProcesado = false;
        renderLineasActivas = false;
        renderLineasActivasDet = false;
        renderLineasArrendatario = false;
        renderAvisos = false;
        renderLineasArrendada = false;
        renderTablaErrores = false;
        listaResumenLineasActivas = new ResumenReporteLineasActivasNngLazyModel();
        procesaFichero = new RetornoProcesaFicheroReportesNng();
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

    /**
     * Fachada.
     * @return nnFacade
     */
    public INumeracionNoGeograficaFacade getNnFacade() {
        return nnFacade;
    }

    /**
     * Fachada.
     * @param nnFacade nnFacade to set
     */
    public void setNnFacade(INumeracionNoGeograficaFacade nnFacade) {
        this.nnFacade = nnFacade;
    }

    /**
     * Tipo de reporte seleccionado.
     * @return tipoReporte
     */
    public TipoReporte getTipoReporte() {
        return tipoReporte;
    }

    /**
     * Tipo de reporte seleccionado.
     * @param tipoReporte tipoReporte to set
     */
    public void setTipoReporte(TipoReporte tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    /**
     * Lista combo de tipos de reporte.
     * @return listaTiposReporte
     */
    public List<TipoReporte> getListaTiposReporte() {
        return listaTiposReporte;
    }

    /**
     * Lista combo de tipos de reporte.
     * @param listaTiposReporte listaTiposReporte to set
     */
    public void setListaTiposReporte(List<TipoReporte> listaTiposReporte) {
        this.listaTiposReporte = listaTiposReporte;
    }

    /**
     * Archivo subido.
     * @return uploadedFile
     */
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    /**
     * Archivo subido.
     * @param uploadedFile uploadedFile to set
     */
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    /**
     * Retorno de la validacion del archivo.
     * @return procesaFichero
     */
    public RetornoProcesaFicheroReportesNng getProcesaFichero() {
        return procesaFichero;
    }

    /**
     * Retorno de la validacion del archivo.
     * @param procesaFichero procesaFichero to set
     */
    public void setProcesaFichero(RetornoProcesaFicheroReportesNng procesaFichero) {
        this.procesaFichero = procesaFichero;
    }

    /**
     * Render de la tabla de errores.
     * @return renderTablaErrores
     */
    public boolean isRenderTablaErrores() {
        return renderTablaErrores;
    }

    /**
     * Render de la tabla de errores.
     * @param renderTablaErrores renderTablaErrores to set
     */
    public void setRenderTablaErrores(boolean renderTablaErrores) {
        this.renderTablaErrores = renderTablaErrores;
    }

    /**
     * Render tabla Lineas Activas.
     * @return renderLineasActivas
     */
    public boolean isRenderLineasActivas() {
        return renderLineasActivas;
    }

    /**
     * Render tabla Lineas Activas.
     * @param renderLineasActivas renderLineasActivas to set
     */
    public void setRenderLineasActivas(boolean renderLineasActivas) {
        this.renderLineasActivas = renderLineasActivas;
    }

    /**
     * Render tabla Lineas Activas Detallado.
     * @return renderLineasActivasDet
     */
    public boolean isRenderLineasActivasDet() {
        return renderLineasActivasDet;
    }

    /**
     * Render tabla Lineas Activas Detallado.
     * @param renderLineasActivasDet renderLineasActivasDet to set
     */
    public void setRenderLineasActivasDet(boolean renderLineasActivasDet) {
        this.renderLineasActivasDet = renderLineasActivasDet;
    }

    /**
     * Render tabla Lineas Arrendatario.
     * @return renderLineasArrendatario
     */
    public boolean isRenderLineasArrendatario() {
        return renderLineasArrendatario;
    }

    /**
     * Render tabla Lineas Arrendatario.
     * @param renderLineasArrendatario renderLineasArrendatario to set
     */
    public void setRenderLineasArrendatario(boolean renderLineasArrendatario) {
        this.renderLineasArrendatario = renderLineasArrendatario;
    }

    /**
     * Render tabla de avisos.
     * @return renderAvisos
     */
    public boolean isRenderAvisos() {
        return renderAvisos;
    }

    /**
     * Render tabla de avisos.
     * @param renderAvisos renderAvisos to set
     */
    public void setRenderAvisos(boolean renderAvisos) {
        this.renderAvisos = renderAvisos;
    }

    /**
     * Indica si un archivo se ha procesado.
     * @return archivoProcesado
     */
    public boolean isArchivoProcesado() {
        return archivoProcesado;
    }

    /**
     * Indica si un archivo se ha procesado.
     * @param archivoProcesado archivoProcesado to set
     */
    public void setArchivoProcesado(boolean archivoProcesado) {
        this.archivoProcesado = archivoProcesado;
    }

    /**
     * Lista resumen reporte lineas activas.
     * @return listaResumenLineasActivas
     */
    public ResumenReporteLineasActivasNngLazyModel getListaResumenLineasActivas() {
        return listaResumenLineasActivas;
    }

    /**
     * Lista resumen reporte lineas activas.
     * @param listaResumenLineasActivas listaResumenLineasActivas to set
     */
    public void setListaResumenLineasActivas(ResumenReporteLineasActivasNngLazyModel listaResumenLineasActivas) {
        this.listaResumenLineasActivas = listaResumenLineasActivas;
    }

    /**
     * Render tabla lineas arrendadas.
     * @return renderLineasArrendada
     */
    public boolean isRenderLineasArrendada() {
        return renderLineasArrendada;
    }

    /**
     * Render tabla lineas arrendadas.
     * @param renderLineasArrendada renderLineasArrendada to set
     */
    public void setRenderLineasArrendada(boolean renderLineasArrendada) {
        this.renderLineasArrendada = renderLineasArrendada;
    }

    /**
     * Ruta temporal del fichero a tratar.
     * @return ruta
     */
    public String getRuta() {
        return ruta;
    }

    /**
     * Ruta temporal del fichero a tratar.
     * @param ruta ruta to set
     */
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    /**
     * Lista de avisos.
     * @return listaAvisos
     */
    public List<String> getListaAvisos() {
        return listaAvisos;
    }

    /**
     * Lista de avisos.
     * @param listaAvisos listaAvisos to set
     */
    public void setListaAvisos(List<String> listaAvisos) {
        this.listaAvisos = listaAvisos;
    }

}
