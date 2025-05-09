package mx.ift.sns.negocio.pnn;

import mx.ift.sns.modelo.nng.ClaveServicio;

/**
 * Interfaz del Servicio de planes de numeracion.
 */
public interface IPlanNumeracionJob {

    /**
     * Genera el Reporte ABD Portabilidad.
     */
    void generarReporteABDPortabilidad();

    /**
     * Genera el Plan de Numeracion ABD-Presuscripción.
     */
    void generarPlanNumeracionABDPresuscripcion();

    /** Genera el Plan de Numeración ABD-Presuscripcion con la vista. */
    void generarPlanNumeracionABDPresuscripcionD();

    /**
     * Genera el Plan de numeración geográfica para PST.
     */
    void generarPlanNumeracionGeograficaPST();

    /**
     * Genera el Plan de numeración geográfica para Público.
     */
    void generarPlanNumeracionGeograficaPublico();

    /**
     * Genera el Plan NNG Especifica.
     */
    void generarPlanNngEspecifica();

    /**
     * Genera el Plan NNG Especifica PST.
     */
    void generarPlanNngEspecificaPst();

    /**
     * Genera el Plan NNG para PST.
     */
    void generarPlanNngPst();

    /**
     * Genera el Plan NNG publico.
     * @param clave servicio
     */
    void generarPlanNngPublico(ClaveServicio clave);

    /**
     * Genera el Plan NNG para IFT.
     */
    void generarPlanNngIFT();

    /**
     * Genera los identificadores de operadores.
     */
    void generarIdentificadoresOperadores();

    /**
     * Genera el Plan IFT.
     */
    void generarPlanIFT();

    /**
     * Genera el Plan IFT.
     */
    void generarPlanIFTD();

    /**
     * Genera el Plan NNG Especifica PST.
     */
    void generarPlanNngEspecificaIFT();
}
