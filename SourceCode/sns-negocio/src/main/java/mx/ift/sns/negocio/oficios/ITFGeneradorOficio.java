package mx.ift.sns.negocio.oficios;

import mx.ift.sns.negocio.conf.IParametrosService;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIService;
import mx.ift.sns.negocio.nng.ISeriesNngService;

/**
 * Interfaz común para la generación de documentos de Oficio.
 */
public interface ITFGeneradorOficio {

    /**
     * Genera el Documento final de Oficio.
     * @param pPlantilla Plantilla (Machote) para el Oficio.
     * @return Documento Serializado.
     * @throws Exception en caso de error.
     */
    byte[] getDocumentoOficio(byte[] pPlantilla) throws Exception;

    /**
     * Asocia el Servicio de parámetros necesario para la obtención de textos personalizados.
     * @param paramService Instancia del servicio de parámetros.
     */
    void setParamService(IParametrosService paramService);

    /**
     * Asocia el Servicio de Series para oficios de Numeración No Geográfica.
     * @param seriesNngService Instancia del servicio de series de numeración no geográfica.
     */
    void setSeriesNngService(ISeriesNngService seriesNngService);

    /**
     * Asocia el Servicio de Códigos CPSI para oficios de Solicitudes UIT.
     * @param codigosCpsiService Instancia del servicio de códigos CPSI.
     */
    void setCodigosCpsiService(ICodigoCPSIService codigosCpsiService);

}
