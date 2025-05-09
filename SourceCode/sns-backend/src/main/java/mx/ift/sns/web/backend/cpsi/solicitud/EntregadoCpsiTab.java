package mx.ift.sns.web.backend.cpsi.solicitud;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.cps.EstatusAsignacionCps;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.CpsiUitDocumento;
import mx.ift.sns.modelo.cpsi.CpsiUitEntregado;
import mx.ift.sns.modelo.cpsi.CpsiUitEntregadoDoc;
import mx.ift.sns.modelo.cpsi.EstatusSolicitudCpsiUit;
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.ApplicationException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Entrega Códigos' de Solicitudes de códigos CPSI a la UIT. */
public class EntregadoCpsiTab extends TabWizard {

    /** Logger de la clase. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntregadoCpsiTab.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Entregados";

    /** Valor máximo que va a tener el código. */
    private static final int VALOR_MAX_CODIGO = 255;

    /** Valor mínimo que va a tener el código. */
    private static final int VALOR_MIN_CODIGO = 0;

    /** Valor máximo que va a tener el pais. */
    private static final int VALOR_MAX_PAIS = 7;

    /** Información de la petición de Solicitud. */
    private SolicitudCpsiUit solicitud;

    /** Servicio de Códigos Nacionales. */
    private ICodigoCPSIFacade codigoCpsiFacade;

    /** Lista de Asignaciones Solicitadas. */
    private List<CpsiUitEntregado> listaCodigosEntregados;

    /** Cantidad de códigos entregados, este dato se recalcula. */
    private int numCpsiEntregados = 0;

    /** Opción de formato (Binario, Formato Decimal). */
    private String opcionFormato;

    /** Código del pais, Mexico es el 3. */
    private String pais = "3";

    /** Código. */
    private String codigo;

    /** Código binario del CPSI. */
    private String codBinario;

    /** Formato decimal del CPSI. */
    private String formatoDecimal;

    /** Indica si se se ha de habilitar el botón de 'Guardar'. */
    private boolean salvarHabilitado = false;

    /** referencia Uit. */
    private String referenciaUit;

    /** Tamaño máximo del fichero. */
    private String maxTamFichero;

    /** Archivo. */
    private UploadedFile archivo;

    /** Ruta temporal del fichero a tratar. */
    private static final String RUTA_TEMP = "/app/sns/temporales/solicitud_uit/";

    /** Indica si se se ha de habilitar el botón de 'Agregar'. */
    private boolean activarBotonAgregar = false;

    /** CPSI entregado seleccionado. */
    private CpsiUitEntregado cpsiUitEntregadoSeleccionado;

    /** Documento adicional seleccionado. */
    private CpsiUitDocumento cpsiUitDocumentoSeleccionado;

    /** Documento para descargar. */
    private StreamedContent fichDescarga;

    /** Nombre del fichero. */
    private String nombreFich = "";

    /** Indica si se ha guardado. */
    private boolean guardado = false;

    /** Indica si se se ha de habilitar el botón de 'Terminar y Crear los códigos CPSI'. */
    private boolean activarBotonTerminar = false;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Entrega de Códigos'.
     * @param pWizard Wizard
     * @param pCodigoCpsiFacade Facade de Servicios de CPSI.
     */
    public EntregadoCpsiTab(Wizard pWizard, ICodigoCPSIFacade pCodigoCpsiFacade) {
        try {
            // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
            setWizard(pWizard);
            setIdMensajes(MSG_ID);

            // Asociamos la solicitud que usaremos en todo el Wizard
            solicitud = (SolicitudCpsiUit) getWizard().getSolicitud();

            // Asociamos el Facade de servicios
            codigoCpsiFacade = pCodigoCpsiFacade;

            // Inicializaciones
            listaCodigosEntregados = new ArrayList<CpsiUitEntregado>();

            // Tamaño máximo del fichero a subir 4MB
            maxTamFichero = codigoCpsiFacade.getParamByName("maxTamFicheroOficio");

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new ApplicationException();
        }
    }

    /**
     * Método que sube el fichero al servidor a una ruta temporal.
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

        } catch (FileNotFoundException e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
        } catch (IOException e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
        }
    }

    /**
     * Método que sube el fichero al servidor a una ruta temporal y crea el objeto donde se va a almacenar la
     * información del documento.
     * @param event evento.
     */
    public void adjuntarFicheros(FileUploadEvent event) {
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

            solicitud.getCpsiUitDocumentos().add(this.getCpsiUitDocumento(archivo));

            // Después de añadirlo, se elimina de la ruta temporal
            this.eliminarFichero(archivo.getFileName());

        } catch (FileNotFoundException e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
        } catch (IOException e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
        }
    }

    /** Método invocado al pulsar el botón de 'Cancelar' en la tabla de documentos adicionales. */
    public void eliminarDocAdicional() {
        // Elimina el documento seleccionado.
        solicitud.getCpsiUitDocumentos().remove(cpsiUitDocumentoSeleccionado);

        LOGGER.debug("docname {}", cpsiUitDocumentoSeleccionado.getNombre());
    }

    /**
     * Realiza la descarga del fichero del código cpsi solicitado a la UIT.
     * @param documento documento seleccionado
     * @return StreamedContent
     * @throws Exception e
     */
    public StreamedContent descargarFicheroEntregado(CpsiUitEntregadoDoc documento) throws Exception {
        InputStream stream = new ByteArrayInputStream(documento.getDocumento());
        return this.fichDescarga = new DefaultStreamedContent(stream, "application/", documento.getNombre());
    }

    /**
     * Realiza la descarga del fichero seleccionado.
     * @param arq documento seleccionado
     * @return StreamedContent
     * @throws Exception e
     */
    public StreamedContent descargarFichero(CpsiUitDocumento arq) throws Exception {
        InputStream stream = new ByteArrayInputStream(arq.getDocumento());
        return this.fichDescarga = new DefaultStreamedContent(stream, "application/", arq.getNombre());
    }

    /**
     * Crea un objeto CpsiUitDocumento con la información de los documentos adicionales.
     * @param documento documento
     * @return CpsiUitDocumento
     */
    private CpsiUitDocumento getCpsiUitDocumento(UploadedFile documento) {
        CpsiUitDocumento cpsiDoc = new CpsiUitDocumento();
        cpsiDoc.setNombre(documento.getFileName());
        cpsiDoc.setDocumento(documento.getContents());
        cpsiDoc.setSolicitudUit(solicitud);
        return cpsiDoc;
    }

    @Override
    public void resetTab() {
        this.listaCodigosEntregados.clear();
        this.solicitud = new SolicitudCpsiUit();
        numCpsiEntregados = 0;
        salvarHabilitado = false;
        activarBotonAgregar = false;
        activarBotonTerminar = false;
        this.limpiarDatos();
        guardado = false;
    }

    /** Método que realiza la actualización de los bloques entregados. */
    private void actualizaBloquesEntregados() {
        if (solicitud.getCpsiUitEntregado() != null) {
            numCpsiEntregados = solicitud.getCpsiUitEntregado().size();
        }
    }

    @Override
    public void actualizaCampos() {
        // La solicitud del Wizard ha cambiado de instancia desde que se generó en
        // el constructor. Es necesario actualizar la referecnia en el tab.
        solicitud = (SolicitudCpsiUit) getWizard().getSolicitud();

        this.actualizaBloquesEntregados();

        // Habilitamos el Botón de Guardar
        this.habilitarSalvarBoton();

        if (solicitud.getCpsiUitEntregado() != null && !solicitud.getCpsiUitEntregado().isEmpty()) {
            for (CpsiUitEntregado cpsi : solicitud.getCpsiUitEntregado()) {
                if (cpsi.getEstatus().getCodigo().equals(EstatusSolicitudCpsiUit.PENDIENTE)) {
                    guardado = true;
                    activarBotonTerminar = true;
                    break;
                } else {
                    activarBotonTerminar = false;
                }
            }
        }

        if (solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_CANCELADA)) {
            activarBotonTerminar = false;
        }

        // Actualizamos las solicitudes pendientes
        // listaCodigosEntregados.clear();
        // if (solicitud.getCpsiUitEntregado() != null) {
        // listaCodigosEntregados.addAll(solicitud.getCpsiUitEntregado());
        //
        // // Comprobamos si existen códigos asignados pendientes.
        // if (!solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_CANCELADA)) {
        // codigosAsignados = true;
        // for (CpsiUitEntregado cpsiEnt : solicitud.getCpsiUitEntregado()) {
        // if (cpsiEnt.getEstatus().getCodigo().equals(EstatusSolicitudCpsiUit.PENDIENTE)) {
        // codigosAsignados = false;
        // break;
        // }
        // }
        // }
        // }
    }

    /** Habilita el boton de guardar. */
    public void habilitarSalvarBoton() {
        salvarHabilitado = false;
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        try {
            // Guardamos los cambios
            solicitud = codigoCpsiFacade.saveSolicitudCpsiUit(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);
            guardado = true;
            return true;

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
        guardado = false;
        return false;
    }

    /**
     * Método invocado al pulsar sobre el botón 'Guardar'.
     */
    public void guardarCambiosManual() {
        // Guardamos los cambios realizados en la solicitud.
        if (this.guardarCambios()) {
            // Mensaje de información al usuario.
            StringBuffer sBuf = new StringBuffer();
            sBuf.append(MensajesBean.getTextoResource("manual.generales.exito.guardar")).append(" ");
            sBuf.append(solicitud.getId());
            MensajesBean.addInfoMsg(MSG_ID, sBuf.toString(), "");
        }
    }

    @Override
    public boolean isAvanzar() {
        // hacemos que se pueda avanzar siempre, sin restricciones
        return true;
    }

    /**
     * Método que aplica los cambios sobre la solicitud de códigos CPSI.
     */
    public void aplicarSolicitudCpsiUit() {
        try {
            if (guardado) {
                // Aplicamos la liberaciones solicitadas y reserva de rangos si es necesario.
                SolicitudCpsiUit sol = codigoCpsiFacade.applySolicitudCpsiUit(solicitud);

                // Asociamos la nueva instancia de solicitud al Wizard
                this.getWizard().setSolicitud(sol);

                MensajesBean.addInfoMsg(MSG_ID, "Se han creado correctamente todos los códigos CPSI solicitados", "");
                this.actualizaCampos();
                activarBotonTerminar = false;
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Debe de Guardar antes de realizar esta operación", "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón Agregar. */
    public void agregarBloques() {
        if (codBinario != null && !codBinario.isEmpty()) {
            codBinario = StringUtils.rightPad(codBinario, 14, '0');
            formatoDecimal = this.getFormatoDecimalByBinario(codBinario);
        } else if (codigo != null && !codigo.isEmpty()) {
            String paisBinario = StringUtils.leftPad(Integer.toBinaryString(Integer.valueOf(pais)), 3, '0');
            String codigoBinario = StringUtils.leftPad(Integer.toBinaryString(Integer.valueOf(codigo)), 8, '0');
            codBinario = paisBinario + codigoBinario;
            codBinario = StringUtils.rightPad(codBinario, 14, '0');
            formatoDecimal = this.getFormatoDecimalByBinario(codBinario);
        }
        // Sacar el decimal ya q es el id de la tabal CAT_CPSI
        int codigoDecimal = Integer.parseInt(codBinario, 2);
        if (!compruebaCodigoCpsi(codigoDecimal)) {
            // si no existe en el catálogo ni en una solicitud en estado pendiente, el proceso continua
            try {
                if (!arrayContains(solicitud.getCpsiUitEntregado(), codigoDecimal)) {
                    solicitud.getCpsiUitEntregado().add(this.getCpsiUitEntregadoFromCodigo(codigoDecimal));
                    this.actualizaBloquesEntregados();
                    this.limpiarDatos();
                }
            } catch (Exception e) {
                LOGGER.error("Error inesperado", e);
                MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
            }
        }
        activarBotonTerminar = true;
        activarBotonAgregar = false;
        this.limpiarDatos();
    }

    /** Método que limpia los valores del formulario. */
    private void limpiarDatos() {
        codBinario = null;
        codigo = null;
        referenciaUit = null;
        opcionFormato = null;
        archivo = null;
    }

    /**
     * Método que comprueba si existe el código decimal en el catálogo de CPSI o en alguna solicitud en estado
     * Pendiente.
     * @param codigoDecimal codigoDecimal
     * @return boolean
     */
    private boolean compruebaCodigoCpsi(int codigoDecimal) {
        boolean existe = false;
        CodigoCPSI codigoCpsi;
        BigDecimal cod = new BigDecimal(codigoDecimal);
        try {
            codigoCpsi = codigoCpsiFacade.getCodigoCpsi(cod, null);
        } catch (Exception e) {
            codigoCpsi = null;
        }
        if (codigoCpsi != null) {
            existe = true;
            MensajesBean.addErrorMsg(MSG_ID, "El código ya existe en el catálogo de CPSI", "");
        } else {
            FiltroBusquedaSolicitudesCPSI filtro = new FiltroBusquedaSolicitudesCPSI();
            EstatusSolicitudCpsiUit estatus = new EstatusSolicitudCpsiUit();
            estatus.setCodigo(EstatusSolicitudCpsiUit.PENDIENTE);
            estatus.setDescripcion(EstatusSolicitudCpsiUit.TXT_PENDIENTE);
            filtro.setEstatusCpsiUit(estatus);
            CodigoCPSI codigo = new CodigoCPSI();
            codigo.setId(cod);
            filtro.setIdCpsi(codigo);
            // buscar si hay solicitudes en estado P con ese código
            int cantidad = codigoCpsiFacade.findSolicitudCpsiUitByCodAndEstatusCount(
                    filtro);
            if (cantidad > 0) {
                existe = true;
                MensajesBean.addErrorMsg(MSG_ID, "El código ya existe en una solicitud en estado Pendiente", "");
            } else {
                existe = false;
            }
        }
        return existe;
    }

    /**
     * Crea un objeto CpsiAsignado con la información de un código CPSI.
     * @param codigo entero
     * @return CpsiUitEntregado
     */
    private CpsiUitEntregado getCpsiUitEntregadoFromCodigo(int codigo) {
        CpsiUitEntregado codigoCpsi = new CpsiUitEntregado();
        codigoCpsi.setBinario(codBinario);
        codigoCpsi.setFormatoDecimal(formatoDecimal);
        codigoCpsi.setReferenciaUit(referenciaUit);
        codigoCpsi.setIdCpsi(new BigDecimal(codigo));
        codigoCpsi.setSolicitudUit(solicitud);
        EstatusSolicitudCpsiUit estatus = new EstatusSolicitudCpsiUit();
        estatus.setCodigo(EstatusSolicitudCpsiUit.PENDIENTE);
        estatus.setDescripcion(EstatusSolicitudCpsiUit.TXT_PENDIENTE);
        codigoCpsi.setEstatus(estatus);

        List<CpsiUitEntregadoDoc> listaCpsiEntregadoDoc = new ArrayList<CpsiUitEntregadoDoc>(0);
        if (archivo != null) {
            CpsiUitEntregadoDoc entregadoDocu = new CpsiUitEntregadoDoc();
            entregadoDocu.setNombre(archivo.getFileName());
            entregadoDocu.setDocumento(archivo.getContents());
            entregadoDocu.setCpsiUitEntregado(codigoCpsi);
            listaCpsiEntregadoDoc.add(entregadoDocu);
        }
        codigoCpsi.setCpsiUitEntregadoDoc(listaCpsiEntregadoDoc);

        return codigoCpsi;
    }

    /** Método invocado al pulsar el botón de Eliminar' en la tabla resumen. */
    public void eliminarEntregado() {
        try {
            PeticionCancelacion checkCancelacion = codigoCpsiFacade.cancelSolicitud(cpsiUitEntregadoSeleccionado, true);
            if (checkCancelacion.isCancelacionPosible()) {

                if (cpsiUitEntregadoSeleccionado.getEstatus().getCodigo().equals(EstatusSolicitudCpsiUit.ENTREGADO)) {
                    // Si la solicitud ya se había ejecutado marcamos la asignación como cancelada.
                    EstatusSolicitudCpsiUit estatusCancelada = new EstatusSolicitudCpsiUit();
                    estatusCancelada.setCodigo(EstatusSolicitudCpsiUit.CANCELADO);
                    // Recorrer los código Cpsi y comprobar si siguen Libres
                    cpsiUitEntregadoSeleccionado.setEstatus(estatusCancelada);
                    MensajesBean.addInfoMsg(MSG_ID,
                            "Los códigos Cpsi se ha cancelado", "");
                } else {
                    // Si la asignación estaba como "Pendiente" directamente se elimina.
                    solicitud.getCpsiUitEntregado().remove(cpsiUitEntregadoSeleccionado);
                    // Si tiene documento adjunto lo elimina de la ruta temporal
                    if (cpsiUitEntregadoSeleccionado.getCpsiUitEntregadoDoc() != null
                            && cpsiUitEntregadoSeleccionado.getCpsiUitEntregadoDoc().getNombre() != null) {
                        this.eliminarFichero(cpsiUitEntregadoSeleccionado.getCpsiUitEntregadoDoc().getNombre());
                    }
                    MensajesBean.addInfoMsg(MSG_ID, "El código Cpsi se ha eliminado correctamente", "");
                }
                this.actualizaBloquesEntregados();
                if (solicitud.getCpsiUitEntregado().size() > 0) {
                    activarBotonTerminar = true;
                } else {
                    activarBotonTerminar = false;
                }

                // Guardamos los cambios
                solicitud = codigoCpsiFacade.saveSolicitudCpsiUit(solicitud);
                getWizard().setSolicitud(solicitud);

                if (solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                    // Si todas las asignaciones están canceladas el trámite pasa a estar cancelado también.
                    boolean asignacionesCanceladas = true;
                    for (CpsiUitEntregado cpsnUitEnt : solicitud.getCpsiUitEntregado()) {
                        asignacionesCanceladas = asignacionesCanceladas
                                && (cpsnUitEnt.getEstatus().getCodigo().equals(EstatusAsignacionCps.CANCELADO));
                    }

                    // Cambiamos el estado de la solicitud
                    if (asignacionesCanceladas) {
                        EstadoSolicitud statusCancelada = new EstadoSolicitud();
                        statusCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
                        solicitud.setEstadoSolicitud(statusCancelada);

                        MensajesBean.addInfoMsg(MSG_ID,
                                "Se han cancelado todas las solicitudes de códigos CPSI a la UIT."
                                        + " El trámite pasa a estado 'Cancelado'", "");

                        // Guardamos los cambios
                        solicitud = codigoCpsiFacade.saveSolicitudCpsiUit(solicitud);
                        getWizard().setSolicitud(solicitud);
                    }
                }

                if (solicitud.getCpsiUitEntregado().isEmpty()) {
                    activarBotonAgregar = false;
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, checkCancelacion.getMensajeError(), "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /**
     * Método que elimina de la ruta temporal el fichero subido y procesado.
     * @param nombre nombre del fichero
     */
    private void eliminarFichero(String nombre) {
        File fichero = new File(RUTA_TEMP + nombre);
        if (fichero.exists()) {
            fichero.delete();
        } else {
            LOGGER.info("No se puede borrar el fichero ya que no existe.", "");
        }
        archivo = null;
    }

    /** Método que comprueba que el código no se exceda del valor máximo. */
    public void compruebaCodigo() {
        if (codigo != null && !codigo.isEmpty()) {
            if (Integer.valueOf(codigo) > VALOR_MAX_CODIGO || Integer.valueOf(codigo) < VALOR_MIN_CODIGO) {
                MensajesBean.addErrorMsg(MSG_ID, "El código debe estar comprendido entre 0 y 255", "");
                activarBotonAgregar = false;
            } else {
                activarBotonAgregar = true;
            }
        } else {
            activarBotonAgregar = false;
        }
    }

    /** Método que calcula el cps binario y el cps decimal a partir del fomato decimal. */
    public void compruebaPais() {
        if (Integer.valueOf(pais) > VALOR_MAX_PAIS || Integer.valueOf(pais) < VALOR_MIN_CODIGO) {
            MensajesBean.addErrorMsg(MSG_ID, "El pais no debe exceder del valor 7", "");
        }
    }

    /** Método que habilita o no el botón de Agregar. */
    public void habilitarBotonAgregar() {
        if (codBinario != null && !codBinario.isEmpty()) {
            activarBotonAgregar = true;
        } else {
            activarBotonAgregar = false;
        }
    }

    /**
     * Indica si una lista contiene un CPSI Asignado sin usar los métos Equals de Object.
     * @param list Lista de objetos CpsiAsignado
     * @param pCpsnId Identificador de código CPSN.
     * @return True si existe un objeto CpsiAsignado con el Id del CPSN esta en la lista
     * @throws Exception en caso de error
     */
    private boolean arrayContains(List<CpsiUitEntregado> list, int pCpsnId) throws Exception {

        // Cuando se guardan los cambios los NumeracionAsignadaCpsn tienen otra instancia diferente dada
        // por JPA y, por lo tanto, no se pueden comparar con las almacenadas en la lista interna. Es necesario
        // comparar por los campos clave para saber si nos estamos refiriendo a la misma.

        for (CpsiUitEntregado codCpsi : list) {
            if (codCpsi.getIdCpsi().intValue() == pCpsnId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Metodo que realiza la conversión de binario a formato decimal.
     * @param binario binario
     * @return String
     */
    private String getFormatoDecimalByBinario(String binario) {
        // Id de Región los 3 primers bits (0-3)
        String idRegion = binario.substring(0, CodigoCPSI.ID_REGION_BITS);

        // Id de Red los 8 siguientes bits (3-11)
        String idRed = binario.substring(CodigoCPSI.ID_REGION_BITS,
                (CodigoCPSI.ID_REGION_BITS + CodigoCPSI.ID_RED_BITS));

        // Ide de codigo CPSI los 3 siguientes bis (11-14)
        String idPunto = binario.substring((CodigoCPSI.ID_REGION_BITS + CodigoCPSI.ID_RED_BITS),
                (CodigoCPSI.ID_REGION_BITS + CodigoCPSI.ID_RED_BITS + CodigoCPSI.ID_CPSI_BITS));

        // Formato decimal
        StringBuilder sbDecimal = new StringBuilder();
        sbDecimal.append(Integer.parseInt(idRegion, 2)).append("-");
        sbDecimal.append(Integer.parseInt(idRed, 2)).append("-");
        sbDecimal.append(Integer.parseInt(idPunto, 2));

        return sbDecimal.toString();
    }

    // GETTERS & SETTERS

    /**
     * Información de la solicitud.
     * @return SolicitudCpsiUit
     */
    public SolicitudCpsiUit getSolicitud() {
        return solicitud;
    }

    /**
     * Identificador de los códigos Entregados.
     * @return List<CpsiUitEntregado>
     */
    public List<CpsiUitEntregado> getListaCodigosEntregados() {
        return listaCodigosEntregados;
    }

    /**
     * Identificador de los códigos Entregados.
     * @param listaCodigosEntregados List<CpsiUitEntregado>
     */
    public void setListaCodigosEntregados(List<CpsiUitEntregado> listaCodigosEntregados) {
        this.listaCodigosEntregados = listaCodigosEntregados;
    }

    /**
     * Identificador del número de códigos CPSI entregados.
     * @return int
     */
    public int getNumCpsiEntregados() {
        return numCpsiEntregados;
    }

    /**
     * Identificador del número de códigos CPSI entregados.
     * @param numCpsiEntregados int
     */
    public void setNumCpsiEntregados(int numCpsiEntregados) {
        this.numCpsiEntregados = numCpsiEntregados;
    }

    /**
     * Identificador del código Binario.
     * @return String
     */
    public String getCodBinario() {
        return codBinario;
    }

    /**
     * Identificador del código Binario.
     * @param codBinario the codBinario to set
     */
    public void setCodBinario(String codBinario) {
        this.codBinario = codBinario;
    }

    /**
     * Identificador del tipo de formato.
     * @return String
     */
    public String getOpcionFormato() {
        return opcionFormato;
    }

    /**
     * Identificador del tipo de formato.
     * @param opcionFormato String
     */
    public void setOpcionFormato(String opcionFormato) {
        this.opcionFormato = opcionFormato;
    }

    /**
     * Identificador del código del pais.
     * @return String
     */
    public String getPais() {
        return pais;
    }

    /**
     * Identificador del código del pais.
     * @param pais String
     */
    public void setPais(String pais) {
        this.pais = pais;
    }

    /**
     * Identificador del código.
     * @return String
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Identificador del código.
     * @param codigo String
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Identifica si se habilita el botón 'Guardar'.
     * @return boolean
     */
    public boolean isSalvarHabilitado() {
        return salvarHabilitado;
    }

    /**
     * Identifica si se habilita el botón 'Guardar'.
     * @param salvarHabilitado boolean
     */
    public void setSalvarHabilitado(boolean salvarHabilitado) {
        this.salvarHabilitado = salvarHabilitado;
    }

    /**
     * Identificador de la referencia UIT.
     * @return String
     */
    public String getReferenciaUit() {
        return referenciaUit;
    }

    /**
     * Identificador de la referencia UIT.
     * @param referenciaUit String
     */
    public void setReferenciaUit(String referenciaUit) {
        this.referenciaUit = referenciaUit;
    }

    /**
     * Identificador del tamaño máximo del fichero.
     * @return String
     */
    public String getMaxTamFichero() {
        return maxTamFichero;
    }

    /**
     * Identificador del tamaño máximo del fichero.
     * @param maxTamFichero String
     */
    public void setMaxTamFichero(String maxTamFichero) {
        this.maxTamFichero = maxTamFichero;
    }

    /**
     * Identificador del archivo.
     * @return UploadedFile
     */
    public UploadedFile getArchivo() {
        return archivo;
    }

    /**
     * Identificador del archivo.
     * @param archivo UploadedFile
     */
    public void setArchivo(UploadedFile archivo) {
        this.archivo = archivo;
    }

    /**
     * Identifica si se habilita el botón 'Agregar'.
     * @return boolean
     */
    public boolean isActivarBotonAgregar() {
        return activarBotonAgregar;
    }

    /**
     * Identifica si se habilita el botón 'Agregar'.
     * @param activarBotonAgregar boolean
     */
    public void setActivarBotonAgregar(boolean activarBotonAgregar) {
        this.activarBotonAgregar = activarBotonAgregar;
    }

    /**
     * Identifica si se habilita el botón 'Terminar'.
     * @return the activarBotonTerminar
     */
    public boolean isActivarBotonTerminar() {
        return activarBotonTerminar;
    }

    /**
     * Identifica si se habilita el botón 'Terminar'.
     * @param activarBotonTerminar the activarBotonTerminar to set
     */
    public void setActivarBotonTerminar(boolean activarBotonTerminar) {
        this.activarBotonTerminar = activarBotonTerminar;
    }

    /**
     * Identificador del formato decimal del código.
     * @return String
     */
    public String getFormatoDecimal() {
        return formatoDecimal;
    }

    /**
     * Identificador del formato decimal del código.
     * @param formatoDecimal String
     */
    public void setFormatoDecimal(String formatoDecimal) {
        this.formatoDecimal = formatoDecimal;
    }

    /**
     * Registro seleccionado en la tabla de CPSI entregados.
     * @return CpsiUitEntregado
     */
    public CpsiUitEntregado getCpsiUitEntregadoSeleccionado() {
        return cpsiUitEntregadoSeleccionado;
    }

    /**
     * Registro seleccionado en la tabla de CPSI entregados.
     * @param cpsiUitEntregadoSeleccionado CpsiUitEntregado
     */
    public void setCpsiUitEntregadoSeleccionado(CpsiUitEntregado cpsiUitEntregadoSeleccionado) {
        this.cpsiUitEntregadoSeleccionado = cpsiUitEntregadoSeleccionado;
    }

    /**
     * Registro seleccionado en la tabla de documentos.
     * @return CpsiUitDocumento
     */
    public CpsiUitDocumento getCpsiUitDocumentoSeleccionado() {
        return cpsiUitDocumentoSeleccionado;
    }

    /**
     * Registro seleccionado en la tabla de documentos.
     * @param cpsiUitDocumentoSeleccionado CpsiUitDocumento
     */
    public void setCpsiUitDocumentoSeleccionado(CpsiUitDocumento cpsiUitDocumentoSeleccionado) {
        this.cpsiUitDocumentoSeleccionado = cpsiUitDocumentoSeleccionado;
    }

    /**
     * Identificador del fichero de descarga.
     * @return StreamedContent
     */
    public StreamedContent getFichDescarga() {
        return fichDescarga;
    }

    /**
     * Identificador del fichero de descarga.
     * @param fichDescarga StreamedContent
     */
    public void setFichDescarga(StreamedContent fichDescarga) {
        this.fichDescarga = fichDescarga;
    }

    /**
     * Identificador del nombre del fichero.
     * @return String
     */
    public String getNombreFich() {
        return nombreFich;
    }

    /**
     * Identificador del nombre del fichero.
     * @param nombreFich String
     */
    public void setNombreFich(String nombreFich) {
        this.nombreFich = nombreFich;
    }

}
