package mx.ift.sns.negocio.ng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import mx.ift.sns.dao.ng.INirDao;
import mx.ift.sns.dao.ng.IRangoSerieDao;
import mx.ift.sns.dao.ng.ISerieArrendadaDAO;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.reporteabd.SerieArrendada;
import mx.ift.sns.modelo.reporteabd.SerieArrendadaPadre;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.ng.model.ComparacionRangoError;
import mx.ift.sns.negocio.ng.model.FormatoError;
import mx.ift.sns.negocio.ng.model.RangoNoAsignadoError;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.negocio.num.INumeracionService;
import mx.ift.sns.negocio.num.model.Numero;
import mx.ift.sns.negocio.psts.IProveedoresService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

/** Generador de plan de numeracion de ABD con los archivos de arrendador y arrendatario. */

@Stateless(mappedName = "planNumeracionABDService")
@Remote(IPlanNumeracionABDService.class)
public class PlanNumeracionABDService implements IPlanNumeracionABDService {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanNumeracionABDService.class);
    /** Escenario 0. */
    private static final int ESCENARIO_0 = 0;
    /** Escenario 1. */
    private static final int ESCENARIO_1 = 1;
    /** Escenario 2. */
    private static final int ESCENARIO_2 = 2;
    /** Escenario 3. */
    private static final int ESCENARIO_3 = 3;

    /** Servicio series. */
    @EJB
    private INumeracionService numeracionService;

    /** DAO de Nirs. */
    @Inject
    private INirDao nirDao;

    /** DAO Rangos Serie. */
    @Inject
    private IRangoSerieDao rangoSerieDAO;

    /** Servicio series. */
    @EJB
    private ISeriesService seriesService;

    /** Servicio proveedores. */
    @EJB
    private IProveedoresService proveedoresService;

    /** DAO Solicitudes de linea activas. */
    @Inject
    private ISerieArrendadaDAO serieArrendadaDAO;

    /** Tamaño de la cabecera de los archivos. */
    private static final int TAM_CABECERA = 4;

    /** Delimitador del fichero CSV. */
    private static final char DELIMITADOR = ',';

    /** Cabecera del archivo de arrendatario. */
    private static final String[] CABECERA_ARRENDATARIO = {"numberfrom", "numberto", "arrendador", "arrendatario"};

    /** Cabecera del archivo de arrendador. */
    private static final String[] CABECERA_ARRENDADOR = {"numberfrom", "numberto", "arrendatario", "arrendador"};

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ResultadoValidacionArrendamiento procesarFicherosAbd(String arrendadorUrl, String arrendatarioUrl,
            String arrendadorNombre, String arrendatarioNombre) {

        List<String[]> datosArrendatario = null;
        List<String[]> datosArrendador = null;
        boolean terminarProcesamiento = false;
        ResultadoValidacionArrendamiento resultadoProceso = new ResultadoValidacionArrendamiento();

        /**
         * 1. Se obtienen los datos de cada uno de los ficheros y se almacenan en un listado para no tener que abrir el
         * mismo archivo n veces para las validaciones.
         */

        datosArrendatario = getDatosFichero(arrendatarioUrl);
        if (datosArrendatario == null) {
            resultadoProceso.setErrorArrendatario(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            resultadoProceso.setErrorArrendatarioTxt(ResultadoValidacionArrendamiento.ERROR_FICHERO_TXT);
            terminarProcesamiento = true;
        }

        datosArrendador = getDatosFichero(arrendadorUrl);
        if (datosArrendador == null) {
            resultadoProceso.setErrorArrendador(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            resultadoProceso.setErrorArrendadorTxt(ResultadoValidacionArrendamiento.ERROR_FICHERO_TXT);
            terminarProcesamiento = true;
        }

        if (terminarProcesamiento) {
            return resultadoProceso;
        }

        if (datosArrendatario.size() <= 1) {
            resultadoProceso.setErrorArrendatario(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            resultadoProceso.setErrorArrendatarioTxt(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO_TXT);
            terminarProcesamiento = true;
        }

        if (datosArrendador.size() <= 1) {
            resultadoProceso.setErrorArrendador(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            resultadoProceso.setErrorArrendadorTxt(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO_TXT);
            terminarProcesamiento = true;
        }

        if (terminarProcesamiento) {
            return resultadoProceso;
        }

        /**
         * 2. Se comprueba para cada archivo que las cabeceras sean correctas y que los datos tengan el número de campos
         * correcto.
         */
        resultadoProceso.setListaErroresFormato(validarFormatoDatos(datosArrendatario, CABECERA_ARRENDATARIO,
                resultadoProceso, arrendatarioNombre, true));
        resultadoProceso.getListaErroresFormato().addAll(
                validarFormatoDatos(datosArrendador, CABECERA_ARRENDADOR, resultadoProceso,
                        arrendadorNombre, false));

        if (!resultadoProceso.getListaErroresFormato().isEmpty()) {
            terminarProcesamiento = true;
        }

        if (terminarProcesamiento) {
            return resultadoProceso;
        }

        /**
         * 3. Si ha pasado las validaciones de formato, se procede a validar los dos ficheros entre sí.
         */
        for (int i = 1; i < datosArrendador.size(); i++) {
            String[] arrendadorArray = datosArrendador.get(i);
            if (validarArrendadorVsArrendatario(arrendadorArray, datosArrendatario, resultadoProceso)) {
                /**
                 * Si pasa la validación entre archivos se procede a validar contra PNN en caso de no existir algún
                 * error de validación previo para no procesar registros innecesariamente.
                 */
                if (resultadoProceso.getListaErroresComparacion().isEmpty()) {
                    if (validaPnn(arrendadorArray, resultadoProceso)) {
                        // Si no ha habido error, se guarda el resgitro correspondiente al escenario a
                        // aplicar posteriormente
                        // if (resultadoProceso.getListaRangosNoAsignados().isEmpty()) {
                        prepararRegistro(arrendadorArray, resultadoProceso);
                        // }
                    }
                    // else {
                    // terminarProcesamiento = true;
                    // }
                }
            } else {
                terminarProcesamiento = true;
            }
        }

        // Si ha habido errores en la comparación de los archivos
        if (terminarProcesamiento) {
            return resultadoProceso;
        }
        LOGGER.info("Número de registros con error: " + resultadoProceso.getListaRangosNoAsignados().size());
        // Si ha pasado todas las validaciones sin error se guardan los registros preparados
        // en la tabla de series arrendadas abd
        // if (resultadoProceso.getListaRangosNoAsignados().isEmpty()) {
        generarRegistros(resultadoProceso);
        // }

        return resultadoProceso;
    }

    /**
     * Método encargado de obtener los datos del fichero y guardarlos en un listado.
     * @param nombreFichero a leer
     * @return listado de datos del fichero leido
     */
    private List<String[]> getDatosFichero(String nombreFichero) {
        List<String[]> datos = new ArrayList<String[]>();

        LOGGER.info("Inicio de lectura del fichero {}.", nombreFichero);

        if (StringUtils.isEmpty(nombreFichero)) {
            LOGGER.error("Nombre del fichero vacio.");
            return null;
        }

        File fichero = new File(nombreFichero);
        if (!fichero.exists()) {
            LOGGER.error("El fichero {} no existe", nombreFichero);
            return null;
        }

        FileReader freader = null;
        CsvReader cvsReader = null;

        try {
            freader = new FileReader(nombreFichero);
            cvsReader = new CsvReader(freader, DELIMITADOR);

            String datosFila = null;
            String[] valores = null;

            String[] headers = null;
            if (cvsReader.readHeaders()) {
                headers = cvsReader.getHeaders();
            }
            datos.add(headers);

            while (cvsReader.readRecord()) {
                datosFila = cvsReader.getRawRecord();
                valores = datosFila.split("" + DELIMITADOR);
                if (valores.length != 0) {
                    datos.add(valores);
                }
            }

        } catch (FileNotFoundException fnfe) {
            return null;
        } catch (IOException e) {
            return null;
        }

        return datos;
    }

    /**
     * Método encargado de validar que las cabeceras sean correctas y que el número de campos de los datos sean iguales
     * a los de la cabecera.
     * @param datos listado de datos leidos
     * @param cabecera listado de campos de la cabecera
     * @param resultadoProceso objeto resultado de las validaciones
     * @param nombreFichero de carga
     * @param arrendatario flag de arrendatario
     * @return listado de errores
     */
    private List<FormatoError> validarFormatoDatos(List<String[]> datos, String[] cabecera,
            ResultadoValidacionArrendamiento resultadoProceso, String nombreFichero, boolean arrendatario) {
        List<FormatoError> erroresFormato = new ArrayList<FormatoError>();

        String[] datosCabecera = datos.get(0);

        if (datosCabecera.length != TAM_CABECERA) {
            erroresFormato.add(generaErrorFormato(datosCabecera, nombreFichero, arrendatario));
        } else {
            if (!(StringUtils.equalsIgnoreCase(datosCabecera[0], cabecera[0])
                    && StringUtils.equalsIgnoreCase(datosCabecera[1], cabecera[1])
                    && StringUtils.equalsIgnoreCase(datosCabecera[2], cabecera[2])
                    && StringUtils.equalsIgnoreCase(datosCabecera[3], cabecera[3]))) {
                if (arrendatario) {
                    erroresFormato.add(new FormatoError(datosCabecera[0], datosCabecera[1], datosCabecera[3],
                            datosCabecera[2], nombreFichero));
                } else {
                    erroresFormato.add(generaErrorFormato(datosCabecera, nombreFichero, arrendatario));
                }
            }
        }

        // Para cada fila de datos del archivo se valida que tenga el número de campos de la cabecera
        // y que sean valores numéricos
        for (int i = 1; i < datos.size(); i++) {
            String[] fila = datos.get(i);
            if (fila.length != TAM_CABECERA) {
                erroresFormato.add(generaErrorFormato(fila, nombreFichero, arrendatario));
            } else {
                // Si algún campo es nulo o vacío se devuelve error del registro
                if (fila[Numero.POS_NUMBER_FROM] == null || "".equals(fila[Numero.POS_NUMBER_FROM])
                        || fila[Numero.POS_NUMBER_TO] == null || "".equals(fila[Numero.POS_NUMBER_TO])
                        || fila[Numero.POS_IDO_ARRTARIO_ARRDOR] == null
                        || "".equals(fila[Numero.POS_IDO_ARRTARIO_ARRDOR])
                        || fila[Numero.POS_IDO_ARRTARIO_ARRTARIO] == null
                        || "".equals(fila[Numero.POS_IDO_ARRTARIO_ARRTARIO])) {
                    erroresFormato.add(generaErrorFormato(fila, nombreFichero, arrendatario));
                } else {
                    // Si algún campo no es numérico se devuelve error del registro
                    if (!StringUtils.isNumeric(fila[Numero.POS_NUMBER_FROM])
                            || !StringUtils.isNumeric(fila[Numero.POS_NUMBER_TO])
                            || !StringUtils.isNumeric(fila[Numero.POS_IDO_ARRTARIO_ARRDOR])
                            || !StringUtils.isNumeric(fila[Numero.POS_IDO_ARRTARIO_ARRTARIO])) {
                        erroresFormato.add(generaErrorFormato(fila, nombreFichero, arrendatario));
                    } else {
                        // Si los campos numberFron y NumberTo no son de 10 dígitos o los campos de ido
                        // de arrendatario y arrendador no son como máximo de 3 dígitos se devuelve error del registro
                        if (fila[Numero.POS_NUMBER_FROM].length() != 10
                                || fila[Numero.POS_NUMBER_TO].length() != 10
                                || fila[Numero.POS_IDO_ARRTARIO_ARRDOR].length() > 3
                                || fila[Numero.POS_IDO_ARRTARIO_ARRTARIO].length() > 3) {
                            erroresFormato.add(generaErrorFormato(fila, nombreFichero, arrendatario));
                        }
                    }
                }
            }
        }

        return erroresFormato;
    }

    /**
     * Método encargado de generar un error de formato con los datos recibidos.
     * @param datos del error
     * @param nombreFichero nombre del archivo que contiene el error
     * @param arrendatario flag que indica si el fichero es de arrendatario o no
     * @return error de formato
     */
    private FormatoError generaErrorFormato(String[] datos, String nombreFichero, boolean arrendatario) {
        FormatoError error = new FormatoError();
        int tamDatos = datos.length;

        if (tamDatos >= Numero.POS_NUMBER_FROM + 1) {
            error.setNumberFrom(datos[Numero.POS_NUMBER_FROM]);
        }

        if (tamDatos >= Numero.POS_NUMBER_TO + 1) {
            error.setNumberTo(datos[Numero.POS_NUMBER_TO]);
        }

        if (arrendatario) {
            if (tamDatos >= Numero.POS_IDO_ARRDOR_ARRTARIO + 1) {
                error.setIdoArrendador(datos[Numero.POS_IDO_ARRDOR_ARRTARIO]);
            }

            if (tamDatos >= Numero.POS_IDO_ARRTARIO_ARRTARIO + 1) {
                error.setIdoArrendatario(datos[Numero.POS_IDO_ARRTARIO_ARRTARIO]);
            }
        } else {
            if (tamDatos >= Numero.POS_IDO_ARRTARIO_ARRDOR + 1) {
                error.setIdoArrendatario(datos[Numero.POS_IDO_ARRTARIO_ARRDOR]);
            }

            if (tamDatos >= Numero.POS_IDO_ARRDOR_ARRDOR + 1) {
                error.setIdoArrendador(datos[Numero.POS_IDO_ARRDOR_ARRDOR]);
            }
        }

        error.setNombreFichero(nombreFichero);

        return error;
    }

    /**
     * Método que comprueba para cada registro del arrendador si existe su correspondencia en el fichero de
     * arrendatario.
     * @param datosArrendatario para la validación
     * @param datosArrendador para la validación
     * @param resultadoProceso objeto resultado de las validaciones
     * @return true si existe el registro de arrendador en el archivo de arrendatario. false en caso contrario
     */
    private boolean validarArrendadorVsArrendatario(String[] datosArrendador, List<String[]> datosArrendatario,
            ResultadoValidacionArrendamiento resultadoProceso) {
        boolean existe = false;
        String[] arrendatario = null;

        for (int j = 1; j < datosArrendatario.size(); j++) {
            arrendatario = datosArrendatario.get(j);

            if (StringUtils.equalsIgnoreCase(datosArrendador[Numero.POS_NUMBER_FROM],
                    arrendatario[Numero.POS_NUMBER_FROM])) {
                // Se coincide el campo numberFrom se valida si coincide el resto de campos
                if (!(StringUtils.equalsIgnoreCase(datosArrendador[Numero.POS_NUMBER_TO],
                        arrendatario[Numero.POS_NUMBER_TO])
                        && StringUtils.equalsIgnoreCase(datosArrendador[Numero.POS_IDO_ARRDOR_ARRTARIO],
                                arrendatario[Numero.POS_IDO_ARRTARIO_ARRTARIO])
                        && StringUtils.equalsIgnoreCase(datosArrendador[Numero.POS_IDO_ARRDOR_ARRDOR],
                        arrendatario[Numero.POS_IDO_ARRTARIO_ARRDOR]))) {
                    ComparacionRangoError error = addErrorNoCoinciden(datosArrendador, arrendatario);
                    resultadoProceso.getListaErroresComparacion().add(error);
                } else {
                    existe = true;
                    break;
                }
            }
        }

        if (!existe) {
            ComparacionRangoError error = addErrorNoExisteEnArrendatario(datosArrendador, arrendatario);
            resultadoProceso.getListaErroresComparacion().add(error);
        }

        return existe;
    }

    /**
     * Método que crea un error de comparación de los archivos.
     * @param valoresArrendador del registro validado
     * @param valoresArrendatario del registro encontrado
     * @return error generado
     */
    private ComparacionRangoError addErrorNoExisteEnArrendatario(String[] valoresArrendador,
            String[] valoresArrendatario) {

        ComparacionRangoError error = new ComparacionRangoError();
        error.setNumeroFromArrendador(valoresArrendador[0]);
        error.setNumeroToArrendador(valoresArrendador[1]);
        error.setArrendador(valoresArrendador[2]);
        error.setIdoArrendador(valoresArrendador[3]);

        return error;
    }

    /**
     * Método que genera el error asociado a encontrar el numberFrom en el fichero de arrendamiento pero el resto de
     * campos no coinciden.
     * @param valoresArrendador del fichero de arrendador
     * @param valoresArrendatario del fichero de arrendatario
     * @return error generado
     */
    private ComparacionRangoError addErrorNoCoinciden(String[] valoresArrendador,
            String[] valoresArrendatario) {

        ComparacionRangoError error = new ComparacionRangoError();
        error.setNumeroFromArrendador(valoresArrendador[0]);
        error.setNumeroToArrendador(valoresArrendador[1]);
        error.setIdoArrendador(valoresArrendador[3]);
        error.setArrendador(valoresArrendador[2]);

        error.setNumeroFromArrendatario(valoresArrendatario[0]);
        error.setNumeroToArrendatario(valoresArrendatario[1]);
        error.setArrendatario(valoresArrendatario[2]);
        error.setIdoArrendatario(valoresArrendatario[3]);

        return error;
    }

    /**
     * Método encargado de validar el registro contra Pnn.
     * @param valoresArrendador a validar
     * @param resultadoProceso objeto resultado de las validaciones
     * @return true si ha pasado la validación contra el pnn
     */
    private boolean validaPnn(String[] valoresArrendador, ResultadoValidacionArrendamiento resultadoProceso) {
        int escenario;

        /**
         * 1. Se obtiene el NIR y la SERIE asociada al número a validar
         */
        Numero numInicial = parseNumero(valoresArrendador[Numero.POS_NUMBER_FROM]);
        Numero numFinal = parseNumero(valoresArrendador[Numero.POS_NUMBER_TO]);

        if (numInicial != null && numFinal != null) {
            if (numInicial.getCodigoNir().equals(numFinal.getCodigoNir())
                    && numInicial.getSna().equals(numFinal.getSna())) {
                // Se obtiene el listado de rangos para la serie y asignatario
                List<RangoSerie> listado = rangoSerieDAO
                        .getRangoAsignados(numInicial.getCodigoNir(), numInicial.getSna());

                if (listado == null || listado.isEmpty()) {
                    resultadoProceso.add(addErrorRango(valoresArrendador, numInicial,
                            RangoNoAsignadoError.ERROR_1));
                } else {
                    List<RangoSerie> rangoAsignatario = new ArrayList<RangoSerie>();
                    for (RangoSerie rango : listado) {
                        if (valoresArrendador[Numero.POS_IDO_ARRDOR_ARRDOR].equals(rango.getAsignatario().getIdo()
                                .toString())) {
                            rangoAsignatario.add(rango);
                        }
                    }
                    Collections.sort(rangoAsignatario,RangoSerie.numInicioComparator);

                    // Se validan los rangos encontrados para ver el escenario a aplicar
                    escenario = obtenerEscenario(numInicial.getNumeroInterno(), numFinal.getNumeroInterno(),
                            rangoAsignatario, resultadoProceso);
                    if (ESCENARIO_0 == escenario) {
                        resultadoProceso.add(addErrorRango(valoresArrendador, numInicial,
                                RangoNoAsignadoError.ERROR_2));
                    } else {
                        resultadoProceso.setEscenario(escenario);
                        return true;
                    }
                }
            }
        } else {
            resultadoProceso.add(addErrorRango(valoresArrendador, numInicial, RangoNoAsignadoError.ERROR_1));
        }

        return false;
    }

    /**
     * Método que separa el string asociado a un número en sus campos nir, serie y número interno.
     * @param numero a separar
     * @return número separado
     */
    private Numero parseNumero(String numero) {
    	
    	int[] ordenNirs = {1, 2, 3};
        boolean encontrado = false;
        String posibleNir = null;
        Numero num = null;

        try {
            for (int i = 0; i < ordenNirs.length && !encontrado; i++) {
                posibleNir = numero.substring(0, ordenNirs[i]);

                if (nirDao.existsNir(posibleNir)) 
                {
                	//Posible Serie
                	Nir nir = nirDao.getNirByCodigo(Integer.parseInt(posibleNir));
                	String serie=numero.substring(ordenNirs[i], 6);
                	//Existe Asignacion en el posible NIR y posible Serie
                	List<RangoSerie> rango=rangoSerieDAO.getRangoAsignados(String.valueOf(nir.getCodigo()), serie.toString());
                	if(rango!=null && rango.size()>0)
                	{
                		num = new Numero();
                        num.setNir(nir);
                        num.setCodigoNir(posibleNir);
                        num.setSna(numero.substring(ordenNirs[i], 6));
                        num.setNumeroInterno(numero.substring(6, 10));
                        encontrado = true;
                	}
                	
                }
            }
        } catch (Exception e) {
            return null;
        }

        return num;
    }

    /**
     * Método encargado de revisar el escenario a aplicar.
     * @param numInicial del rango
     * @param numFinal del rango
     * @param rangos a validar
     * @param resultadoProceso objeto de resultado de las validaciones
     * @return escenario a aplicar
     */
	private int obtenerEscenario(String numInicial, String numFinal,
			List<RangoSerie> rangos,
			ResultadoValidacionArrendamiento resultadoProceso) {
		int escenario = ESCENARIO_0;

		try {

			// Evalua el escenario 1
			if (obtenerEscenario1(rangos, numInicial, numFinal,
					resultadoProceso)) {
				escenario = ESCENARIO_1;
			}
			// Evalua el escenario 2
			else if (obtenerEscenario2(rangos, numInicial, numFinal,
					resultadoProceso)) {
				escenario = ESCENARIO_2;
			}
			// Evalua el escenario 3
			if (obtenerEscenario3(rangos, numInicial, numFinal,
					resultadoProceso)) {
				escenario = ESCENARIO_3;
			}

		} catch (Exception e) {
		}

		return escenario;
	}
    
    /**
     * Este metodo valida si la serie informada
     * como rentada cumple con el escenario 1: Ver caso de uso
     *
     */
    private Boolean obtenerEscenario1(List<RangoSerie> rangos,String numInicialRenta, String numFinalRenta, 
    		ResultadoValidacionArrendamiento resultadoProceso){
    	 Boolean respuestaEscenario=Boolean.TRUE;	
    	 int escenario=ESCENARIO_0;
    	 List<RangoSerie> rangosImplicados = new ArrayList<RangoSerie>();
    	 //Recoger las rangos asignados en la Serie y Nir
    	 //de la numeracion informada como rentada
    	 for (int i = 0; i < rangos.size(); i++) {
             RangoSerie rango = rangos.get(i);

             // Si el rango es igual al leido se aplica el escenario 1
             if (StringUtils.leftPad(rango.getNumInicio(), 4, '0').equals(numInicialRenta)
                     && StringUtils.leftPad(rango.getNumFinal(), 4, '0').equals(numFinalRenta)) {
                 escenario = ESCENARIO_1;
                 //rangosImplicados.clear();
                 rangosImplicados.add(rango);
             }
             }
    	 resultadoProceso.setRangosEscenario(rangosImplicados);
    	 if(String.valueOf(escenario).equals("0"))
    	 {
    		 respuestaEscenario=Boolean.FALSE;
    	 }
    	 return respuestaEscenario;
    }
    
    /**
     * Este metodo valida si la serie informada
     * como rentada cumple con el escenario 2: Ver caso de uso
     *
     */
    private Boolean obtenerEscenario2(List<RangoSerie> rangos,String numInicialRenta, String numFinalRenta, 
    		ResultadoValidacionArrendamiento resultadoProceso){
    	Boolean respuestaEscenario=Boolean.TRUE;
    	int nInicioRenta = Integer.parseInt(numInicialRenta);
        int nFinalRenta = Integer.parseInt(numFinalRenta);
    	int escenario=ESCENARIO_0;
    	 List<RangoSerie> rangosImplicados = new ArrayList<RangoSerie>();
    	 //Recoger las rangos asignados en la Serie y Nir
    	 //de la numeracion informada como rentada
    	 for (int i = 0; i < rangos.size(); i++) {
             RangoSerie rango = rangos.get(i);

             try {
				if (rango.getNumInicioAsInt() <= nInicioRenta && rango.getNumFinalAsInt() >= nFinalRenta) {
				     // Si el rango engloba al leido se aplica el escenario 2
				     escenario = ESCENARIO_2;
				     rangosImplicados.add(rango);
				     break;
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}
    	 }
    	 resultadoProceso.setRangosEscenario(rangosImplicados);
    	 if(String.valueOf(escenario).equals("0"))
    	 {
    		 respuestaEscenario=Boolean.FALSE;
    	 }
    	 return respuestaEscenario;
    }
    
    /**
     * Este metodo valida si la serie informada
     * como rentada cumple con el escenario 3: Ver caso de uso
     *
     */
	private Boolean obtenerEscenario3(List<RangoSerie> rangos,
			String numInicialRenta, String numFinalRenta,
			ResultadoValidacionArrendamiento resultadoProceso) {
		Boolean respuestaEscenario = Boolean.FALSE;
		int nInicioRenta = Integer.parseInt(numInicialRenta);
		int nFinalRenta = Integer.parseInt(numFinalRenta);
		List<RangoSerie> rangosImplicados = new ArrayList<RangoSerie>();
		//Primero validamos que el PST una asignacion
		//con el nFinalRenta 
		List<String> numerosFinalesRango=new ArrayList<String>();
		for (RangoSerie rangoSerie : rangos) {
			numerosFinalesRango.add(rangoSerie.getNumFinal());
		}
		if(numerosFinalesRango.contains(numFinalRenta))
		{
			// Recoger las rangos asignados en la Serie y Nir
			// de la numeracion informada como rentada
			for (int i = 0; i < rangos.size(); i++) {
				RangoSerie rango = rangos.get(i);
				try {
					if (nInicioRenta < nFinalRenta
							&& rango.getNumInicioAsInt() <= nInicioRenta
							&& rango.getNumFinalAsInt() >= nInicioRenta) {
						nInicioRenta = rango.getNumFinalAsInt() + 1;
						rangosImplicados.add(rango);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			resultadoProceso.setRangosEscenario(rangosImplicados);
			if (rangosImplicados.size()>0) {
				respuestaEscenario = Boolean.TRUE;
			}
		}
		
		
		return respuestaEscenario;
	}
    

    /**
     * Método encargado de añadir al listado de registros a guardar en BBDD el nuevo registro validado.
     * @param registro con los datos del arrendador
     * @param resultadoProceso objeto de resultado de las validaciones
     */
    private void prepararRegistro(String[] registro, ResultadoValidacionArrendamiento resultadoProceso) {

        for (RangoSerie rango : resultadoProceso.getRangosEscenario()) {
            SerieArrendada serie = new SerieArrendada();

            serie.getId().setIdPstAsignatario(rango.getAsignatario().getId());
            serie.getId().setNumberFrom(
                    registro[Numero.POS_NUMBER_FROM].substring(0, 6).concat(
                            StringUtils.leftPad(rango.getNumInicio(), 4, '0')));
            serie.getId().setNumberTo(
                    registro[Numero.POS_NUMBER_TO].substring(0, 6).concat(
                            StringUtils.leftPad(rango.getNumFinal(), 4, '0')));
            serie.getId().setNumberFromFile(registro[Numero.POS_NUMBER_FROM]);
            serie.getId().setNumberToFile(registro[Numero.POS_NUMBER_TO]);

            serie.setAsignatario(rango.getAsignatario());
            serie.setCodigoAbn(rango.getSerie().getNir().getAbn().getCodigoAbn());
            serie.setTipoRed(rango.getTipoRed());
            serie.setTipoModalidad(rango.getTipoModalidad());
            serie.setEscenario(resultadoProceso.getEscenario());
            serie.setIdRangoSerie(rango.getId().getId());
            serie.setIdNir(rango.getId().getIdNir());
            serie.setSna(rango.getId().getSna());

            resultadoProceso.getSeriesPreparadas().add(serie);
        }
    }

    /**
     * Método encargado de guardar los registros procesados en BBDD para su utilización en la generación de la tabla de
     * padres e hijos.
     * @param resultadoProceso objeto de resultado de las validaciones
     */
    private void generarRegistros(ResultadoValidacionArrendamiento resultadoProceso) {

        // Borramos los registros si los hubiese de la tabla
        seriesService.deleteAllSeriesArrendadas();

        for (SerieArrendada serie : resultadoProceso.getSeriesPreparadas()) {
            seriesService.create(serie);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void generarRegistrosAbd() {
        HashMap<String, SerieArrendadaPadre> padresMap = new HashMap<String, SerieArrendadaPadre>();

        try {
            List<SerieArrendada> list = serieArrendadaDAO.findAll();
            for (SerieArrendada serieArrendada : list) {
                SerieArrendadaPadre padre = null;

                if (Long.parseLong(serieArrendada.getId().getNumberFrom()) < Long.parseLong(serieArrendada
                        .getId().getNumberFromFile())) {
                    padre = generaPadreInicio(serieArrendada);
                    procesaSolapamientos(padre, padresMap);
                    
                }

                if (Long.parseLong(serieArrendada.getId().getNumberTo()) > Long.parseLong(serieArrendada
                        .getId().getNumberToFile())) {
                    padre = generaPadreFinal(serieArrendada);
                    procesaSolapamientos(padre, padresMap);
                }
                
                padre = generaPadreCuerpo(serieArrendada);
                procesaSolapamientos(padre, padresMap);

                for (SerieArrendadaPadre serie : padresMap.values()) {
                    serieArrendadaDAO.create(serie);
                }
            }
        } catch (NoResultException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(" no hay series arrenadadas");
            }
        }
    }

    /**
     * Método encargado de crear el registro padre para el inicio del rango.
     * @param serie sobre la que se hace la caomprobación.
     * @return padre generado
     */
    private SerieArrendadaPadre generaPadreInicio(SerieArrendada serie) {
        SerieArrendadaPadre padre = new SerieArrendadaPadre();

        padre.setAsignatario(serie.getAsignatario());
        padre.getId().setIdPstAsignatario(serie.getAsignatario().getId());
        padre.getId().setNumberFrom(serie.getId().getNumberFrom());
        padre.setNumberTo(String.valueOf(Long.parseLong(serie.getId().getNumberFromFile()) - 1));

        padre.getId().setIdAbn(serie.getCodigoAbn());

        padre.setIdTipoRed(serie.getTipoRed().getCdg());
        if (serie.getTipoModalidad() == null || !TipoModalidad.MPP.equals(serie.getTipoModalidad().getCdg())) {
            padre.setMpp(false);
        } else {
            padre.setMpp(true);
        }

        padre.setIdRangoSerie(serie.getIdRangoSerie());
        padre.setIdNir(serie.getIdNir());
        padre.setSna(serie.getSna());

        return padre;
    }

    /**
     * Método encargado de crear el registro padre para el final del rango.
     * @param serie sobre la que se hace la caomprobación.
     * @return padre generado
     */
    private SerieArrendadaPadre generaPadreFinal(SerieArrendada serie) {
        SerieArrendadaPadre padre = new SerieArrendadaPadre();

        padre.setAsignatario(serie.getAsignatario());
        padre.getId().setIdPstAsignatario(serie.getAsignatario().getId());
        padre.getId().setNumberFrom(String.valueOf(Long.parseLong(serie.getId().getNumberToFile()) + 1));
        padre.setNumberTo(serie.getId().getNumberTo());

        padre.getId().setIdAbn(serie.getCodigoAbn());

        padre.setIdTipoRed(serie.getTipoRed().getCdg());
        if (serie.getTipoModalidad() == null || !TipoModalidad.MPP.equals(serie.getTipoModalidad().getCdg())) {
            padre.setMpp(false);
        } else {
            padre.setMpp(true);
        }

        padre.setIdRangoSerie(serie.getIdRangoSerie());
        padre.setIdNir(serie.getIdNir());
        padre.setSna(serie.getSna());

        return padre;
    }

    /**
     * Método encargado de crear el registro padre para el cuerpo del rango.
     * @param serie sobre la que se hace la caomprobación.
     * @return padre generado
     */
    private SerieArrendadaPadre generaPadreCuerpo(SerieArrendada serie) {
        SerieArrendadaPadre padre = new SerieArrendadaPadre();

        String inicio = Long.parseLong(serie.getId().getNumberFromFile()) >= Long.parseLong(serie.getId()
                .getNumberFrom()) ? serie.getId().getNumberFromFile() : serie.getId().getNumberFrom();

        String fin = Long.parseLong(serie.getId().getNumberToFile()) <= Long.parseLong(serie.getId().getNumberTo())
                ? serie.getId().getNumberToFile() : serie.getId().getNumberTo();

        padre.setAsignatario(serie.getAsignatario());
        padre.getId().setIdPstAsignatario(serie.getAsignatario().getId());
        padre.getId().setNumberFrom(inicio);
        padre.setNumberTo(fin);

        padre.getId().setIdAbn(serie.getCodigoAbn());
        setTipoRed(serie, padre);

        padre.setIdRangoSerie(serie.getIdRangoSerie());
        padre.setIdNir(serie.getIdNir());
        padre.setSna(serie.getSna());

        return padre;
    }

    /**
     * Método encargado de revisar los posibles solapamientos entre registros.
     * @param padre a validar
     * @param padresMap registros ya procesados pendientes de persistir
     */
    private void procesaSolapamientos(SerieArrendadaPadre padre, HashMap<String, SerieArrendadaPadre> padresMap) {
        String numberFrom = null;
        
        if (Long.parseLong(padre.getId().getNumberFrom()) > Long.parseLong(padre.getNumberTo())) {
            return;
        }

        if (padresMap.isEmpty() || !padresMap.containsKey(padre.getId().getNumberFrom())) {
            padresMap.put(padre.getId().getNumberFrom(), padre);
        } else {
            SerieArrendadaPadre padreMap = (SerieArrendadaPadre) padresMap.get(padre.getId().getNumberFrom());
            if (Long.parseLong(padreMap.getNumberTo()) > Long.parseLong(padre.getNumberTo())) {
                String numberTo = padreMap.getNumberTo();
                padreMap.setNumberTo(padre.getNumberTo());

                if (TipoRed.FIJA.equals(padreMap.getIdTipoRed())) {
                    padreMap.setIdTipoRed(padre.getIdTipoRed());
                    padreMap.setMpp(padre.getMpp());
                }

                padresMap.put(padreMap.getId().getNumberFrom(), padreMap);

                long num = Long.parseLong(padre.getNumberTo()) + 1;
                numberFrom = String.valueOf(num);

                SerieArrendadaPadre serieTemp = (SerieArrendadaPadre) padresMap.get(numberFrom);
                if (serieTemp == null) {
                    serieTemp = new SerieArrendadaPadre();

                    serieTemp.setAsignatario(padreMap.getAsignatario());
                    serieTemp.getId().setIdPstAsignatario(padreMap.getAsignatario().getId());
                    serieTemp.getId().setNumberFrom(numberFrom);
                    serieTemp.setNumberTo(numberTo);

                    serieTemp.getId().setIdAbn(padreMap.getId().getIdAbn());
                    serieTemp.setIdTipoRed(padreMap.getIdTipoRed());
                    serieTemp.setMpp(padreMap.getMpp());
                    serieTemp.setIdRangoSerie(padreMap.getIdRangoSerie());
                    serieTemp.setIdNir(padreMap.getIdNir());
                    serieTemp.setSna(padreMap.getSna());
                    padresMap.put(serieTemp.getId().getNumberFrom(), serieTemp);
                }

            } else if (Long.parseLong(padreMap.getNumberTo()) == Long.parseLong(padre.getNumberTo())) {
                if (TipoRed.FIJA.equals(padreMap.getIdTipoRed()) && TipoRed.MOVIL.equals(padre.getIdTipoRed())) {
                    padresMap.put(padre.getId().getNumberFrom(), padre);
                }
            } else {
                if (TipoRed.FIJA.equals(padreMap.getIdTipoRed()) && TipoRed.MOVIL.equals(padre.getIdTipoRed())) {
                    padreMap.setIdTipoRed(padre.getIdTipoRed());
                    padreMap.setMpp(padre.getMpp());
                    padresMap.put(padreMap.getId().getNumberFrom(), padreMap);
                }

                long num = Long.parseLong(padreMap.getNumberTo()) + 1;
                numberFrom = String.valueOf(num);

                padre.getId().setNumberFrom(numberFrom);
                procesaSolapamientos(padre, padresMap);
            }
        }
    }

    /**
     * Método encargado de establecer el tipo de red y la modalidad de la serie arrendada.
     * @param serie sobre la que hacer la validación
     * @param padre a setear.
     */
    private void setTipoRed(SerieArrendada serie, SerieArrendadaPadre padre) {
        if (TipoRed.FIJA.equals(serie.getTipoRed().getCdg())) {
            padre.setIdTipoRed(TipoRed.MOVIL);
            padre.setMpp(true);
        } else {
            padre.setIdTipoRed(serie.getTipoRed().getCdg());
            padre.setMpp(TipoModalidad.MPP.equals(serie.getTipoModalidad().getCdg()));
        }
    }

    /**
     * Método que genera un error de rango.
     * @param datosArrendador en array
     * @param numInicial del rango
     * @param descripcion del error
     * @return el error generado
     */
    private RangoNoAsignadoError addErrorRango(String[] datosArrendador, Numero numInicial, String descripcion) {

        RangoNoAsignadoError error = new RangoNoAsignadoError();

        error.setNumeroInicial1(datosArrendador[Numero.POS_NUMBER_FROM]);
        error.setNumeroFinal1(datosArrendador[Numero.POS_NUMBER_TO]);
        error.setIdo1(datosArrendador[Numero.POS_IDO_ARRDOR_ARRDOR]);
        error.setDescripcion(descripcion);

        return error;
    }
}
