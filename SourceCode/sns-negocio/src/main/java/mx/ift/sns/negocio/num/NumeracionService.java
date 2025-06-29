package mx.ift.sns.negocio.num;

import java.math.BigDecimal;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.INirDao;
import mx.ift.sns.dao.ng.IRangoSerieDao;
import mx.ift.sns.dao.ng.ISerieDao;
import mx.ift.sns.dao.nng.IClaveServicioDao;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.ng.SeriePK;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.num.model.Numero;
import mx.ift.sns.negocio.psts.IProveedoresService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generador de plan de numeracion de ABD Presuscripcion.
 */
@Stateless(name = "NumeracionService", mappedName = "NumeracionService")
@Remote(INumeracionService.class)
public class NumeracionService implements INumeracionService {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumeracionService.class);

    /** Longitud de la numeracion nacional. */
    public static final int LENGTH_NUM = 10;

    /** Longitud de la numeracion local. */
    public static final int MAX_LENGTH_NUM_LOCAL = 8;

    /** Longitud de la numeracion local. */
    public static final int MIN_LENGTH_NUM_LOCAL = 7;

    /** Servicio de Proveedores. */
    @EJB
    private IProveedoresService pstService;

    /** DAO Rangos Serie. */
    @Inject
    private IRangoSerieDao rangoSerieDAO;

    /** DAO de Series Numéricas. */
    @Inject
    private ISerieDao serieDAO;

    /** DAO Nir. */
    @Inject
    private INirDao nirDAO;

    /** DAO Clave Servicio. */
    @Inject
    private IClaveServicioDao claveServicioDao;

    /** Servicio de Bitácora. */
    @EJB
    private IBitacoraService bitacoraService;

    /**
     * Se busca para los distintas longitudes de NIR si existen y tienen numeraciones asignadas. Se buscará
     * coincidencias para NIRs según el siguiente orden:
     * <ul>
     * <li>NIR de 2 dígitos.</li>
     * <li>NIR de 1 dígito.</li>
     * <li>NIR de 3 dígitos.</li>
     * </ul>
     * @param numero a parsear
     * @return nir si lo encontramos
     */
    private void adivinarNir(String numero, Numero num) {
        Nir nir = null;
        int[] ordenNirs = {2, 1, 3};
        boolean encontrado = false;
        String posibleNir = "";

        try {
            if (numero.length() >= 3) {

                for (int i = 0; i < ordenNirs.length && !encontrado; i++) {

                    posibleNir = numero.substring(0, ordenNirs[i]);
                    LOGGER.debug("probando nir {}", posibleNir);

                    if (nirDAO.existsNir(posibleNir)) {
                        int cdg = Integer.parseInt(posibleNir);
                        nir = nirDAO.getNirByCodigo(cdg);

                        BigDecimal sna = new BigDecimal(numero.substring(ordenNirs[i], 6));

                        // Se comprueba si existe numeración asignada para ese NIR y la serie
                        RangoSerie rango = rangoSerieDAO.getRangoPerteneceNumeracion(nir, sna, numero.substring(6, 10));
                        if (rango != null) {
                            encontrado = true;

                            Serie serie = serieDAO.getSerie(sna, nir.getId());

                            num.setNumero(numero);
                            num.setNir(nir);
                            num.setSerie(serie);
                            num.setNumeroInterno(numero.substring(6, 10));
                            num.setLocal(false);
                            num.setNacional(true);
                            num.setClave(null);
                            num.setRango(rango);
                            num.setSna(numero.substring(ordenNirs[i], 6));
                            num.setCodigoNir(String.valueOf(cdg));
                            num.setZona(String.valueOf(nir.getZona()));
                        }
                        if (rangoSerieDAO.existeNumeracionAsignada(nir, sna, numero.substring(6, 10))) {
                            encontrado = true;
                        }
                    }
                }
            }
        } catch (NumberFormatException nfe) {
            LOGGER.error("Error al intentar procesar como número un valor alfanumérico.");
        }

        LOGGER.debug("nir {}", nir);
        // return nir;
    }

    /**
     * Buscamos si la clave es de 1 digito, de 2, o al final de 3.
     * @param numero a parsear
     * @return clave si la encontramos
     */
    private ClaveServicio adivinarClave(String numero) {
        ClaveServicio clave = null;
        String posibleClave = "";

        if (numero.length() >= ClaveServicio.TAM_CLAVE) {
            posibleClave = numero.substring(0, ClaveServicio.TAM_CLAVE);

            if (claveServicioDao.exists(posibleClave)) {
                BigDecimal cdg = new BigDecimal(Integer.parseInt(posibleClave));
                clave = claveServicioDao.getClaveServicioByCodigo(cdg);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("clave {}", clave);
        }

        return clave;
    }

    /**
     * Función que parse el número nacional.
     * @param numeracion String
     * @return Numero
     */
    private Numero parseNumeracionNacional(String numeracion) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("numeracion '{}'", numeracion);
        }

        if (StringUtils.isEmpty(numeracion)) {
            throw new IllegalArgumentException("La numeracion no puede ser nula.");
        }

        if (numeracion.length() > LENGTH_NUM) {
            throw new IllegalArgumentException("La numeracion no puede tener mas de " + LENGTH_NUM + " digitos.");
        }

        if (numeracion.length() < LENGTH_NUM) {
            StringBuilder numeracionBuilder = new StringBuilder(numeracion);
            while (numeracionBuilder.length() < LENGTH_NUM) {
                numeracionBuilder.insert(0, "0");
            }
            numeracion = numeracionBuilder.toString();
        }

        Numero num = new Numero();

        ClaveServicio clave = adivinarClave(numeracion);
        if (clave == null) {
            adivinarNir(numeracion, num);
        }

        if (num.getRango() != null) {
            return num;
        }

        num.setCodigoNir(null);
        num.setSna(null);

        LOGGER.debug("numeracion '{}' long= {} codigo nir '{}' sna '{}' num '{}'", numeracion, numeracion.length(),
                null, null, null);

        num.setNumero(numeracion);
        num.setNir(null);
        num.setSerie(null);
        num.setNumeroInterno(null);
        num.setLocal(false);
        num.setNacional(true);
        num.setClave(clave);
        num.setRango(null);
        num.setZona(null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}", num);
        }

        return num;
    }

    /**
     * Parsea un numnero local de 7 digitos.
     * @param numeracion numeracion
     * @return Numero
     */
    private Numero parseNumeracionLocal(String numeracion) {

        Numero num = new Numero();
        String snaS = null;
        String numS = null;

        /* ejemplo 213 9000 */
        /* ejemplo 2131 9000 */

        if (numeracion.length() == MIN_LENGTH_NUM_LOCAL) {

            snaS = numeracion.substring(0, 3);
            numS = numeracion.substring(3, 7);

        } else if (numeracion.length() == MAX_LENGTH_NUM_LOCAL) {

            snaS = numeracion.substring(0, 4);
            numS = numeracion.substring(4, 8);

        }

        num.setCodigoNir(null);
        num.setZona(null);
        num.setSna(snaS);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("numeracion '{}' long= {} sna '{}' num '{}'", numeracion, numeracion.length(), snaS, numS);
        }

        Serie serie = new Serie();
        SeriePK idSerie = new SeriePK();

        idSerie.setSna(new BigDecimal(Objects.requireNonNull(snaS)));
        serie.setId(idSerie);

        num.setNumero(numeracion);
        num.setNir(null);
        num.setSerie(serie);
        num.setNumeroInterno(numS);

        num.setLocal(true);
        num.setNacional(false);

        LOGGER.debug("{}", num);
        return num;
    }

    @Override
    public Numero parseNumeracion(String numeracion) {
        if (StringUtils.isEmpty(numeracion)) {
            throw new IllegalArgumentException("La numeracion no puede ser nula.");
        }

        if (numeracion.length() > LENGTH_NUM) {
            throw new IllegalArgumentException("La numeracion no puede tener mas de " + LENGTH_NUM + " digitos.");
        }

        if (numeracion.length() == LENGTH_NUM) {
            return parseNumeracionNacional(numeracion);
        } else if ((numeracion.length() >= MIN_LENGTH_NUM_LOCAL) && (numeracion.length() <= MAX_LENGTH_NUM_LOCAL)) {
            return parseNumeracionLocal(numeracion);
        }

        throw new IllegalArgumentException("La numeracion incorrecta.");
    }

}
