package mx.ift.sns.web.backend.cpsn.asignacion;

import java.util.List;

import mx.ift.sns.modelo.cps.EstatusAsignacionCps;
import mx.ift.sns.modelo.cpsn.NumeracionSolicitadaCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Codigo solicitado' de Solicitudes de Asignación CPSN. */
public class NumeracionAsignacionCpsnTab extends TabWizard {

    /** Serial uid. */
    private static final long serialVersionUID = -6977772487921944690L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumeracionAsignacionCpsnTab.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Numeracion";

    /** Servicio de numeracion geografica. */
    private ICodigoCPSNFacade cpsnService;

    /** Lista que contiene los posibles tipo de bloque. */
    private List<TipoBloqueCPSN> listaTiposBloque;

    /** Numeración solicitada. */
    private NumeracionSolicitadaCpsn numeracionSolicitada;

    /** Campo solicitud asignación. */
    private SolicitudAsignacionCpsn solicitud;

    /** Seleccion multiple tabla resumen. */
    private NumeracionSolicitadaCpsn selectedNumeracionSolicitada;

    /** Total Bloque de 2048. Actualizar al dar añadir. */
    private long total2048;

    /** Total Bloque de 128. Actualizar al dar añadir. */
    private long total128;

    /** Total Bloque de 8. Actualizar al dar añadir. */
    private long total8;

    /** Total Bloques Individuales. Actualizar al dar añadir. */
    private long totalIndividual;

    /** Total registros. Actualizar al dar añadir. */
    private long totalReg;

    /** Habilita boton guardar. */
    private boolean habilitarGuardar = false;

    /** Boolean que comprueba si se ha guardado. */
    private boolean guardado;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Codigo solicitado'.
     * @param pBeanPadre bean del padre
     * @param cpsnService servicio
     */
    public NumeracionAsignacionCpsnTab(Wizard pBeanPadre, ICodigoCPSNFacade cpsnService) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pBeanPadre);
        setIdMensajes(MSG_ID);

        // Asociamos la solicitud que usaremos en todo el Wizard
        solicitud = (SolicitudAsignacionCpsn) getWizard().getSolicitud();

        // Asociamos el Facade de servicios
        this.cpsnService = cpsnService;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Cargamos el combo de tipos de bloque
            listaTiposBloque = cpsnService.findAllTiposBloqueCPSN();

            // Iniciamos valores
            numeracionSolicitada = new NumeracionSolicitadaCpsn();
            selectedNumeracionSolicitada = new NumeracionSolicitadaCpsn();
            habilitarGuardar = false;
            guardado = false;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
        }
    }

    @Override
    public void resetTab() {
        if (this.isIniciado()) {
            numeracionSolicitada = new NumeracionSolicitadaCpsn();
            selectedNumeracionSolicitada = new NumeracionSolicitadaCpsn();
            setMensajeError(null);
            habilitarGuardar = false;
            guardado = false;
        }
    }

    /**
     * Método que realiza el guardado de la numeración solicitada.
     * @return true/false
     */
    private boolean guardarCambios() {
        try {
            // Guardamos los cambios
            solicitud = cpsnService.saveSolicitudAsignacion(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);
            guardado = true;

            limpiarDatosNumeracion();
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
            guardado = false;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            guardado = false;
        }
        return guardado;
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

    // @Override
    // public String getMensajeError() {
    // return MensajesBean.getTextoResource("manual.generales.necesario.guardar");
    // }

    @Override
    public boolean isAvanzar() {
        boolean res = false;
        if (this.isTabHabilitado()) {
            if (isValido()) {
                if (habilitarGuardar) {
                    if (guardado) {
                        return true;
                    } else {
                        this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0008));
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                this.setSummaryError("No hay numeraciones seleccionadas");
                // MensajesBean.addErrorMsg(MSG_ID, "No hay numeraciones seleccionadas", "");
                return false;
            }
        } else {
            res = true;
        }
        return res;
    }

    /**
     * Método que válida si hay numeración solicitada.
     * @return boolean valida
     */
    public boolean isValido() {
        return solicitud.getNumeracionSolicitadas().size() > 0;
    }

    @Override
    public void actualizaCampos() {
        solicitud = (SolicitudAsignacionCpsn) getWizard().getSolicitud();
        habilitarGuardar = false;
        // Si la solicitud ya tiene numeraciones solicitadas.
        if (solicitud.getNumeracionSolicitadas().size() > 0) {
            // habilitarGuardar = true;
            guardado = false;
        }
        // Cargamos los totales de la tabla
        sumTotales(solicitud.getNumeracionSolicitadas());
    }

    /**
     * Método que elimina una numeracion seleccionada a la tabla Resumen.
     */
    public void eliminarBloque() {
        if (this.selectedNumeracionSolicitada != null) {
            if (solicitud.getNumeracionAsignadas() != null && !solicitud.getNumeracionAsignadas().isEmpty()) {
                boolean encontrado = false;
                int iter = 0;
                while (!encontrado && iter < solicitud.getNumeracionAsignadas().size()) {
                    if (solicitud.getNumeracionAsignadas().get(iter).getTipoBloqueCpsn().getId()
                            .equals(selectedNumeracionSolicitada.getTipoBloque().getId())
                            && (solicitud.getNumeracionAsignadas().get(iter).getEstatus().getCodigo()
                                    .equals(EstatusAsignacionCps.ASIGNADO)
                            || solicitud.getNumeracionAsignadas().get(iter).getEstatus().getCodigo()
                                    .equals(EstatusAsignacionCps.PENDIENTE))) {
                        encontrado = true;
                    } else {
                        encontrado = false;
                        iter++;
                    }
                }
                if (encontrado) {
                    MensajesBean
                            .addErrorMsg(
                                    MSG_ID,
                                    "No se puede eliminar ya que existen registros con ese "
                                            + "tipo de bloque en estado Asignado o Pendiente", "");
                } else {
                    solicitud.getNumeracionSolicitadas().remove(this.selectedNumeracionSolicitada);
                }

            } else {
                solicitud.getNumeracionSolicitadas().remove(this.selectedNumeracionSolicitada);
            }
        }
        try {
            // Guardamos los cambios y generamos los nuevos registros
            solicitud = cpsnService.saveSolicitudAsignacion(solicitud);
            guardado = false;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
        sumTotales(solicitud.getNumeracionSolicitadas());
        guardado = true;
    }

    /**
     * Método que agrega una numeración seleccionada a la tabla Resumen.
     */
    public void agregarBloque() {
        guardado = false;
        if (validarNumeracion()) {
            numeracionSolicitada.setSolicitudAsignacion(solicitud);
            NumeracionSolicitadaCpsn numSoliAux = new NumeracionSolicitadaCpsn();
            numSoliAux = numeracionSolicitada;
            numSoliAux.setId(null);
            solicitud.getNumeracionSolicitadas().add(numSoliAux);
            sumTotales(solicitud.getNumeracionSolicitadas());

            limpiarDatosNumeracion();

            habilitarGuardar = true;
        }
    }

    /**
     * Método que valida una numeracion seleccionada.
     * @return true/false
     */
    private boolean validarNumeracion() {
        boolean valido = true;

        if (numeracionSolicitada.getTipoBloque() == null) {
            MensajesBean.addErrorMsg(MSG_ID, "El campo 'Tipo Bloque' es obligatorio", "");
            valido = false;
        }
        if (numeracionSolicitada.getCantSolicitada() == null) {
            MensajesBean.addErrorMsg(MSG_ID, "El campo 'Cantidad de códigos' es obligatorio", "");
            valido = false;
        }
        return valido;
    }

    /**
     * Método que realiza la suma de los totales de la tabla.
     * @param numeracionSolicitadas List<NumeracionSolicitada>
     */
    private void sumTotales(List<NumeracionSolicitadaCpsn> numeracionSolicitadas) {
        total2048 = 0;
        total128 = 0;
        total8 = 0;
        totalIndividual = 0;
        totalReg = 0;

        totalReg = numeracionSolicitadas.size();

        for (int i = 0; i < numeracionSolicitadas.size(); i++) {
            NumeracionSolicitadaCpsn dato = numeracionSolicitadas.get(i);
            if (dato.getTipoBloque().getId().equals(TipoBloqueCPSN.BLOQUE_2048)) {
                total2048 = total2048 + dato.getCantSolicitada().longValue();
            } else if (dato.getTipoBloque().getId().equals(TipoBloqueCPSN.BLOQUE_128)) {
                total128 = total128 + dato.getCantSolicitada().longValue();
            } else if (dato.getTipoBloque().getId().equals(TipoBloqueCPSN.BLOQUE_8)) {
                total8 = total8 + dato.getCantSolicitada().longValue();
            } else if (dato.getTipoBloque().getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
                totalIndividual = totalIndividual + dato.getCantSolicitada().longValue();
            }

        }

    }

    /**
     * Metodo encargado de limpiar los filtros de edicion de la numeracion.
     */
    private void limpiarDatosNumeracion() {
        // Limpiamos los datos
        numeracionSolicitada = new NumeracionSolicitadaCpsn();
        selectedNumeracionSolicitada = new NumeracionSolicitadaCpsn();
    }

    /**
     * Indica si activa o desactiva el botón guardar.
     * @return boolean
     */
    public boolean isHabilitarGuardar() {
        return habilitarGuardar;
    }

    /**
     * Indica si activa o desactiva el botón guardar.
     * @param habilitarGuardar boolean
     */
    public void setHabilitarGuardar(boolean habilitarGuardar) {
        this.habilitarGuardar = habilitarGuardar;
    }

    /**
     * Identificador de los tipos de bloques.
     * @return List<TipoBloqueCPSN>
     */
    public List<TipoBloqueCPSN> getListaTiposBloque() {
        return listaTiposBloque;
    }

    /**
     * Identificador de los tipos de bloques.
     * @param listaTiposBloque List<TipoBloqueCPSN>
     */
    public void setListaTiposBloque(List<TipoBloqueCPSN> listaTiposBloque) {
        this.listaTiposBloque = listaTiposBloque;
    }

    /**
     * Identificador de la numeración solicitada.
     * @return NumeracionSolicitadaCpsn
     */
    public NumeracionSolicitadaCpsn getNumeracionSolicitada() {
        return numeracionSolicitada;
    }

    /**
     * Identificador de la numeración solicitada.
     * @param numeracionSolicitada NumeracionSolicitadaCpsn
     */
    public void setNumeracionSolicitada(NumeracionSolicitadaCpsn numeracionSolicitada) {
        this.numeracionSolicitada = numeracionSolicitada;
    }

    /**
     * Información de la solicitud.
     * @return SolicitudAsignacionCpsn
     */
    public SolicitudAsignacionCpsn getSolicitud() {
        return solicitud;
    }

    /**
     * Identificador de la numeración solicitada seleccionada.
     * @return NumeracionSolicitadaCpsn
     */
    public NumeracionSolicitadaCpsn getSelectedNumeracionSolicitada() {
        return selectedNumeracionSolicitada;
    }

    /**
     * Identificador de la numeración solicitada seleccionada.
     * @param selectedNumeracionSolicitada NumeracionSolicitadaCpsn
     */
    public void setSelectedNumeracionSolicitada(NumeracionSolicitadaCpsn selectedNumeracionSolicitada) {
        this.selectedNumeracionSolicitada = selectedNumeracionSolicitada;
    }

    /**
     * Identificador de la cantidad de bloques de tipo 2048.
     * @return long
     */
    public long getTotal2048() {
        return total2048;
    }

    /**
     * Identificador de la cantidad de bloques de tipo 2048.
     * @param total2048 long
     */
    public void setTotal2048(long total2048) {
        this.total2048 = total2048;
    }

    /**
     * Identificador de la cantidad de bloques de tipo 128.
     * @return long
     */
    public long getTotal128() {
        return total128;
    }

    /**
     * Identificador de la cantidad de bloques de tipo 128.
     * @param total128 long
     */
    public void setTotal128(long total128) {
        this.total128 = total128;
    }

    /**
     * Identificador de la cantidad de bloques de tipo 8.
     * @return long
     */
    public long getTotal8() {
        return total8;
    }

    /**
     * Identificador de la cantidad de bloques de tipo 8.
     * @param total8 long
     */
    public void setTotal8(long total8) {
        this.total8 = total8;
    }

    /**
     * Identificador de la cantidad de bloques de tipo individual.
     * @return long
     */
    public long getTotalIndividual() {
        return totalIndividual;
    }

    /**
     * Identificador de la cantidad de bloques de tipo individual.
     * @param totalIndividual long
     */
    public void setTotalIndividual(long totalIndividual) {
        this.totalIndividual = totalIndividual;
    }

    /**
     * Identificador de la cantidad total de registros.
     * @return long
     */
    public long getTotalReg() {
        return totalReg;
    }

    /**
     * Identificador de la cantidad total de registros.
     * @param totalReg long
     */
    public void setTotalReg(long totalReg) {
        this.totalReg = totalReg;
    }

    /**
     * Identificador de guardado.
     * @return boolean
     */
    public boolean isGuardado() {
        return guardado;
    }

    /**
     * Identificador de guardado.
     * @param guardado boolean
     */
    public void setGuardado(boolean guardado) {
        this.guardado = guardado;
    }

}
