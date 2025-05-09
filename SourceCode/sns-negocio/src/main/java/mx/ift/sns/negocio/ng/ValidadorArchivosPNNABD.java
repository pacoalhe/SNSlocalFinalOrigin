package mx.ift.sns.negocio.ng;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import mx.ift.sns.dao.ng.ISerieArrendadaDAO;
import mx.ift.sns.modelo.reporteabd.SerieArrendada;
import mx.ift.sns.negocio.ng.arrendamientos.ValidadorArchivoArrendador;
import mx.ift.sns.negocio.ng.arrendamientos.ValidadorArchivoArrendadorVsArrendatario;
import mx.ift.sns.negocio.ng.arrendamientos.ValidadorArchivoArrendatario;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;
import mx.ift.sns.negocio.num.INumeracionService;
import mx.ift.sns.negocio.num.model.Numero;
import mx.ift.sns.negocio.psts.IProveedoresService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generador de plan de numeracion de ABD Presuscripcion.
 */

@Stateless(mappedName = "validadorArchivosABD")
@Remote(IValidadorArchivosPNNABD.class)
public class ValidadorArchivosPNNABD implements IValidadorArchivosPNNABD {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidadorArchivosPNNABD.class);

    /** Servicio series. */
    @EJB
    private ISeriesService seriesService;

    /** Servicio series. */
    @EJB
    private INumeracionService numeracionService;

    /** proveedores series. */
    @EJB
    private IProveedoresService proveedorService;

    /** DAO Solicitudes de linea activas. */
    @Inject
    private ISerieArrendadaDAO serieArrendadaDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ResultadoValidacionArrendamiento validar(String ficheroArrendatario, String ficheroArrendador) {

        ResultadoValidacionArrendamiento res = new ResultadoValidacionArrendamiento();
        ResultadoValidacionCSV r1 = null;
        ResultadoValidacionCSV r2 = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("parametros ficheroArrendatario='{}' ficheroArrendador='{}'",
                    ficheroArrendatario, ficheroArrendador);
        }

        seriesService.deleteAllSeriesArrendadas();

        res.getListaRangosNoAsignados().clear();

        LOGGER.debug("validando fichero arrendatario------------------------------");

        try {

            ValidadorArchivoArrendatario v1 = new ValidadorArchivoArrendatario();

            r1 = v1.validar(ficheroArrendatario);

            res.setErrorArrendatario(r1.getError());
        } catch (Exception e) {
            LOGGER.error("Error validando archivo arrendatario {}", ficheroArrendatario, e);
        }

        LOGGER.debug("validando fichero arrendador------------------------------");

        try {

            ValidadorArchivoArrendador v2 = new ValidadorArchivoArrendador();

            r2 = v2.validar(ficheroArrendador);
            res.setErrorArrendador(r2.getError());
        } catch (Exception e) {
            LOGGER.error("Error validando archivo arrendador {}", ficheroArrendador, e);
        }

        LOGGER.debug("validando fichero arrendador -> arrendatario ------------------------------");

        try {
            ValidadorArchivoArrendadorVsArrendatario v = new ValidadorArchivoArrendadorVsArrendatario();
            v.setFileArrendatario(ficheroArrendatario);

            v.setSeriesService(seriesService);
            v.setNumeracionService(numeracionService);
            ResultadoValidacionArrendamiento r3 = new ResultadoValidacionArrendamiento();
            v.setRes(r3);
            v.validar(ficheroArrendador);
            r3 = (ResultadoValidacionArrendamiento) v.getRes();

            res.getListaErroresComparacion().addAll(r3.getListaErroresComparacion());
            res.getListaRangosNoAsignados().addAll(r3.getListaRangosNoAsignados());
        } catch (Exception e) {
            LOGGER.error("Error ", e);
        }

        LOGGER.debug("resultado validacion{}", res);

        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void generarTablas() {

        LOGGER.debug("");

        try {
            List<SerieArrendada> list = serieArrendadaDAO.findAll();
            for (SerieArrendada serieArrendada : list) {
                Numero n = numeracionService.parseNumeracion(serieArrendada.getId().getNumberFrom());
                // if (seriesService.tieneSerieCompleta(n.getNir().getId(), n.getSerie().getId().getSna(),
                // serieArrendada.getConcesionario())) {
                //
                // /* añadimos a padre e hijo lo mismo. */
                // SerieArrendadaHijo hijo = new SerieArrendadaHijo();
                // hijo.setAsignatario(serieArrendada.getConcesionario());
                // hijo.getId().setIdTipoRed(TipoRed.MOVIL);
                // hijo.getId().setMpp(true);
                // hijo.getId().setNumberFrom(serieArrendada.getId().getNumberFrom());
                // hijo.getId().setNumberTo(serieArrendada.getId().getNumberTo());
                // hijo.getId().setIdAbn(n.getNir().getAbn().getCodigoAbn());
                //
                // SerieArrendadaPadre padre = new SerieArrendadaPadre();
                // padre.setAsignatario(serieArrendada.getConcesionario());
                // padre.getId().setIdTipoRed(TipoRed.MOVIL);
                // padre.getId().setMpp(true);
                // padre.getId().setNumberFrom(serieArrendada.getId().getNumberFrom());
                // padre.getId().setNumberTo(serieArrendada.getId().getNumberTo());
                // padre.getId().setIdAbn(n.getNir().getAbn().getCodigoAbn());
                //
                // serieArrendadaDAO.create(padre);
                // serieArrendadaDAO.create(hijo);
                // } else {
                // ;
                // }
            }
        } catch (NoResultException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(" no hay series arrenadadas");
            }
        }
    }
}
