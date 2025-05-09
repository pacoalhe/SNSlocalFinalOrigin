package mx.ift.sns.negocio.ng;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.IAbnDao;
import mx.ift.sns.dao.ng.INirDao;
import mx.ift.sns.dao.ng.IRangoSerieDao;
import mx.ift.sns.dao.ng.ISerieDao;
import mx.ift.sns.dao.nng.IClaveServicioDao;
import mx.ift.sns.dao.nng.ISerieNngDao;
import mx.ift.sns.dao.ot.IPoblacionDao;
import mx.ift.sns.dao.pst.IProveedorConvenioDao;
import mx.ift.sns.dao.pst.IProveedorDao;
import mx.ift.sns.modelo.lineas.LineaActivaDet;
import mx.ift.sns.modelo.lineas.LineaArrendada;
import mx.ift.sns.modelo.lineas.LineaArrendatario;
import mx.ift.sns.modelo.lineas.Reporte;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaDetNng;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.ReporteLineasActivas;
import mx.ift.sns.modelo.lineas.ReporteNng;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.ng.SolicitudLineasActivas;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.nng.SolicitudLineasActivasNng;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.ng.model.ErrorValidacion;
import mx.ift.sns.negocio.ng.model.RetornoProcesaFicheroReportes;
import mx.ift.sns.negocio.nng.RetornoProcesaFicheroReportesNng;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Validaciones para el fichero. */
@Stateless
@Remote(IValidacionFicheroReportes.class)
public class ValidadorFicheroReportes implements IValidacionFicheroReportes {

    /** proveedorDAO. */
    @Inject
    private IProveedorDao proveedorDAO;

    /** proveedorDAO. */
    @Inject
    private IClaveServicioDao claveServicioDao;

    /** convenio DAO. */
    @Inject
    private IProveedorConvenioDao convenioDAO;

    /** Rango DAO. */
    @Inject
    private IRangoSerieDao rangoDao;

    /** Serie DAO. */
    @Inject
    private ISerieDao serieDao;

    /** Serie NNG DAO. */
    @Inject
    private ISerieNngDao serieNngDao;

    /** Poblacion DAO. */
    @Inject
    private IPoblacionDao poblacionDao;

    /** ABN DAO. */
    @Inject
    private IAbnDao abnDao;

    /** NIR DAO. */
    @Inject
    private INirDao nirDao;

    /** Servicio de Reportes. */
    @EJB
    private IReportesService reportesService;

    /** retorno de la validacion del reporte NG. */
    private RetornoProcesaFicheroReportes procesarFicheroNg;

    /** retorno de la validacion del reporte NNG. */
    private RetornoProcesaFicheroReportesNng procesarFicheroNng;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public RetornoProcesaFicheroReportes validarLineasActivas(File fichero) throws Exception {

        List<List<String>> listaDatos = extraerExcel(fichero);

        listaDatos = setearCamposVacios(listaDatos, 10);

        procesarFicheroNg = new RetornoProcesaFicheroReportes();

        ReporteLineasActivas lineaActiva = new ReporteLineasActivas();

        procesarFicheroNg = procesarCabecera(listaDatos, lineaActiva);

        lineaActiva = (ReporteLineasActivas) procesarFicheroNg.getReporte();

        ErrorValidacion error = null;

        // Validamos la cabecera de lineas activas

        int col = 3;

        col++;
        String sumNumAsignada = listaDatos.get(1).get(col);

        if (isNumeric(sumNumAsignada)) {
            if (Integer.parseInt(sumNumAsignada) == totalcolumna(listaDatos, col + 1)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {

                    lineaActiva.setSumaNumAsignada(new BigDecimal(sumNumAsignada));
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error003");
                error.setDescripcion("El dato de Suma de numeración para el reporte cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumNumAsignada = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error003");
            error.setDescripcion("El dato de suma de numeración para el reporte cargado, no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumActivaFijo = (col < listaDatos.get(1).size()) ? listaDatos.get(1).get(col) : "";

        if (isNumeric(sumNumActivaFijo)) {
            if (Integer.parseInt(sumNumActivaFijo) == totalcolumna(listaDatos, col + 1)) {
                lineaActiva.setSumaLineasActivasFijo(new BigDecimal(sumNumActivaFijo));
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error004");
                error.setDescripcion("El dato de suma de líneas activas en fijo para el reporte cargado"
                        + " no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumNumActivaFijo = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error004");
            error.setDescripcion("El dato de suma de líneas activas en fijo para el reporte cargado, no es numérico..");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumActivaCpp = (col < listaDatos.get(1).size()) ? listaDatos.get(1).get(col) : "";

        if (isNumeric(sumNumActivaCpp)) {
            if (Integer.parseInt(sumNumActivaCpp) == totalcolumna(listaDatos, col + 1)) {
                lineaActiva.setSumaLineasActivasCpp(new BigDecimal(sumNumActivaCpp));
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error005");
                error.setDescripcion("El dato de suma de líneas activas en CPP para el"
                        + " reporte cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumNumActivaCpp = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error005");
            error.setDescripcion("El dato de suma de líneas activas en CPP para el reporte cargado, no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumActivaMpp = (col < listaDatos.get(1).size()) ? listaDatos.get(1).get(col) : "";

        if (isNumeric(sumNumActivaMpp)) {
            if (Integer.parseInt(sumNumActivaMpp) == totalcolumna(listaDatos, col + 1)) {
                lineaActiva.setSumaLineasActivasMpp(new BigDecimal(sumNumActivaMpp));
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error006");
                error.setDescripcion("El dato de suma de líneas activas en MPP para el "
                        + "reporte cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumNumActivaMpp = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error006");
            error.setDescripcion("El dato de suma de líneas activas en MPP para el reporte cargado, no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumTotalNumActiva = (col < listaDatos.get(1).size()) ? listaDatos.get(1).get(col) : "";

        if (isNumeric(sumTotalNumActiva)) {
            if ((Integer.parseInt(sumTotalNumActiva) == totalcolumna(listaDatos, col + 1))
                    && (Integer.parseInt(sumTotalNumActiva) == totalcolumna(listaDatos, col)
                            + totalcolumna(listaDatos, col - 1) + totalcolumna(listaDatos, col - 2))
                    && (Integer.parseInt(sumTotalNumActiva) == Integer.parseInt(sumNumActivaFijo)
                            + Integer.parseInt(sumNumActivaMpp) + Integer.parseInt(sumNumActivaCpp))) {
                lineaActiva.setSumaTotalLineasActivas(new BigDecimal(sumTotalNumActiva));
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error007");
                error.setDescripcion("El dato de suma total de líneas activas para el reporte cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumTotalNumActiva = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error007");
            error.setDescripcion("El dato de suma total de líneas activas para el reporte cargado, no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        // Procesamos el detalle del reporte de la Linea Activa

        List<String> listaDetalles = new ArrayList<String>();

        for (int fila = 3; fila < listaDatos.size(); fila++) {

            StringBuffer detalle = new StringBuffer();

            // Validamos los datos de ubicacion
            validarUbicacion(listaDatos, fila, 0);
            detalle.append(listaDatos.get(fila).get(1));

            // Procesamos los totales
            String totalNumAsignados = (col < listaDatos.get(fila).size()) ? listaDatos.get(fila).get(5) : "";
            detalle.append(":");
            detalle.append(totalNumAsignados);
            if (!isNumeric(totalNumAsignados)) {
                totalNumAsignados = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error009");
                error.setDescripcion("El dato de total de líneas activas en fijo del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);

            }

            String totalNumActivasFijo = (col < listaDatos.get(fila).size()) ? listaDatos.get(fila).get(6) : "";
            detalle.append(":");
            detalle.append(totalNumActivasFijo);
            if (isNumeric(totalNumActivasFijo)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {
                    if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante().getTipoRed().getCdg()
                            .equals(TipoRed.MOVIL) && Integer.parseInt(sumNumActivaFijo) > 0) {

                        error = new ErrorValidacion();
                        error.setCodigo("Error010");
                        error.setDescripcion("El PST no tiene configurado el servicio fijo para el registro "
                                + listaDatos.get(fila).get(0));
                        procesarFicheroNg.getlErrores().add(error);
                    }
                }
            } else {
                totalNumActivasFijo = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error010");
                error.setDescripcion("El dato de total de líneas activas en fijo del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            String totalNumActivasCpp = (col < listaDatos.get(fila).size()) ? listaDatos.get(fila).get(7) : "";
            detalle.append(":");
            detalle.append(totalNumActivasCpp);
            if (isNumeric(totalNumActivasCpp)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {
                    if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante().getTipoRed().getCdg()
                            .equals(TipoRed.FIJA) && (Integer.parseInt(sumNumActivaCpp) > 0)) {
                        error = new ErrorValidacion();
                        error.setCodigo("Error011");
                        error.setDescripcion("El PST no tiene configurado el servicio movil para el registro "
                                + listaDatos.get(fila).get(0));
                        procesarFicheroNg.getlErrores().add(error);
                    }
                }
            } else {
                totalNumActivasCpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error011");
                error.setDescripcion("El dato de total de líneas activas en CPP del registro "
                        + listaDatos.get(fila).get(0) + "no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            String totalNumActivasMpp = (col < listaDatos.get(fila).size()) ? listaDatos.get(fila).get(8) : "";
            detalle.append(":");
            detalle.append(totalNumActivasMpp);
            if (isNumeric(totalNumActivasMpp)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {
                    if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante().getTipoRed().getCdg()
                            .equals(TipoRed.FIJA) && (Integer.parseInt(sumNumActivaMpp) > 0)) {
                        error = new ErrorValidacion();
                        error.setCodigo("Error012");
                        error.setDescripcion("El PST no tiene configurado el servicio movil para el registro "
                                + listaDatos.get(fila).get(0));
                        procesarFicheroNg.getlErrores().add(error);
                    }
                }
            } else {
                totalNumActivasMpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error012");
                error.setDescripcion("El dato de total de líneas activas en MPP del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            String totalNumActivas = (col < listaDatos.get(fila).size()) ? listaDatos.get(fila).get(9) : "";
            detalle.append(":");
            detalle.append(totalNumActivas);
            if (isNumeric(totalNumActivas)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {
                    if (Integer.parseInt(totalNumActivas) != Integer.parseInt(totalNumActivasMpp)
                            + Integer.parseInt(totalNumActivasCpp) + Integer.parseInt(totalNumActivasFijo)) {

                        error = new ErrorValidacion();
                        error.setCodigo("Error013");
                        error.setDescripcion("El dato de total de líneas activas del registro "
                                + listaDatos.get(fila).get(0) + " no corresponde a la suma.");
                        procesarFicheroNg.getlErrores().add(error);
                    }
                }
            } else {
                totalNumActivas = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error013");
                error.setDescripcion("El dato de total de líneas activas del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            detalle.append(":");
            detalle.append(listaDatos.get(fila).get(0));

            listaDetalles.add(detalle.toString());

        }

        procesarFicheroNg.setListaDatos(listaDetalles);
        return procesarFicheroNg;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public RetornoProcesaFicheroReportes validarFicheroLineasActivasDet(File fichero) throws Exception {
        List<List<String>> listaDatos = extraerExcel(fichero);

        procesarFicheroNg = new RetornoProcesaFicheroReportes();

        listaDatos = setearCamposVacios(listaDatos, 22);

        LineaActivaDet lineaActivaDet = new LineaActivaDet();

        procesarFicheroNg = procesarCabecera(listaDatos, lineaActivaDet);

        lineaActivaDet = (LineaActivaDet) procesarFicheroNg.getReporte();

        ErrorValidacion error = null;

        // Validamos la cabecera de lineas activas detallada

        int col = 3;

        col++;
        String sumNumAsignada = (col < listaDatos.get(1).size()) ? listaDatos.get(1).get(col) : "";

        if (isNumeric(sumNumAsignada)) {
            if (Integer.parseInt(sumNumAsignada) == totalcolumna(listaDatos, col + 2)) {
                if (lineaActivaDet.getSolicitudLineasActivas().getProveedorSolicitante() != null) {

                    lineaActivaDet.setSumaNumAsignada(new BigDecimal(sumNumAsignada));
                }

            } else {
                sumNumAsignada = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error003");
                error.setDescripcion("El dato de Suma de numeración para el reporte cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            error = new ErrorValidacion();
            error.setCodigo("Error003");
            error.setDescripcion("El dato de suma de numeración para el reporte cargado, no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumServicio = (col < listaDatos.get(1).size()) ? listaDatos.get(1).get(col) : "";

        if (isNumeric(sumNumServicio)) {
            if (Integer.parseInt(sumNumServicio) == totalcolumna(listaDatos, 7) + totalcolumna(listaDatos, 8)
                    + totalcolumna(listaDatos, 9)) {
                lineaActivaDet.setSumaNumEnServicio(new BigDecimal(sumNumServicio));
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error014");
                error.setDescripcion("El dato de suma de numeración en servicio (Fijo+Movil) para"
                        + " el reporte cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumNumServicio = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error014");
            error.setDescripcion("El dato de suma de numeración en servicio para el reporte cargado no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumCuarentena = (col < listaDatos.get(1).size()) ? listaDatos.get(1).get(col) : "";

        if (isNumeric(sumNumCuarentena)) {
            if (Integer.parseInt(sumNumCuarentena) == totalcolumna(listaDatos, 10) + totalcolumna(listaDatos, 11)
                    + totalcolumna(listaDatos, 12)) {
                lineaActivaDet.setSumaLineasCuarentenaFijo(new BigDecimal(sumNumCuarentena));
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error015");
                error.setDescripcion("El dato de suma de numeración en cuarentena (fijo + Móvil) para el reporte"
                        + " cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumNumCuarentena = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error015");
            error.setDescripcion("El dato de suma de líneas en cuarentena (Fijo + Móvil) para el reporte cargado no "
                    + "es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumPortada = (col < listaDatos.get(1).size()) ? listaDatos.get(1).get(col) : "";

        if (isNumeric(sumNumPortada)) {
            if (Integer.parseInt(sumNumPortada) == totalcolumna(listaDatos, 13) + totalcolumna(listaDatos, 14)
                    + totalcolumna(listaDatos, 15)) {
                lineaActivaDet.setSumaLineasPortadasFijo(new BigDecimal(sumNumPortada));
            } else {
                sumNumPortada = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error016");
                error.setDescripcion("El dato de suma de líneas portadas (fijo + Móvil) para el reporte "
                        + "cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumNumPortada = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error016");
            error.setDescripcion("El dato de suma de líneas  portadas para el reporte cargado no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumUsoInterno = (col < listaDatos.get(1).size()) ? listaDatos.get(1).get(col) : "";

        if (isNumeric(sumNumUsoInterno)) {
            if (Integer.parseInt(sumNumUsoInterno) == totalcolumna(listaDatos, 16) + totalcolumna(listaDatos, 17)
                    + totalcolumna(listaDatos, 18)) {
                lineaActivaDet.setSumaLineasUsoInterno(new BigDecimal(sumNumUsoInterno));
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error017");
                error.setDescripcion("El dato de suma de líneas uso interno (fijo + Móvil) para el"
                        + " reporte cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumNumUsoInterno = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error017");
            error.setDescripcion("El dato de suma de líneas uso interno para el reporte cargado no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumPublica = (col < listaDatos.get(1).size()) ? listaDatos.get(1).get(col) : "";

        if (isNumeric(sumNumPublica)) {
            if (Integer.parseInt(sumNumPublica) == totalcolumna(listaDatos, 19) + totalcolumna(listaDatos, 20)
                    + totalcolumna(listaDatos, 21)) {
                lineaActivaDet.setSumaLineasTelPublica(new BigDecimal(sumNumPublica));
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error018");
                error.setDescripcion("El dato de suma de líneas telefonía pública (fijo + Móvil) para el reporte"
                        + " cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumNumPublica = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error018");
            error.setDescripcion("El dato de suma de líneas telefonía pública para el reporte cargado no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        // Procesamos el detalle del reporte de la Linea Activa Detallada

        List<String> listaDetalles = new ArrayList<String>();

        for (int fila = 3; fila < listaDatos.size(); fila++) {
            StringBuffer detalle = new StringBuffer();

            // Validamos los datos de ubicacion
            validarUbicacion(listaDatos, fila, 1);
            detalle.append(listaDatos.get(fila).get(2));

            String strAbn = (col < listaDatos.get(fila).size()) ? listaDatos.get(fila).get(1) : "";
            detalle.append(":");
            detalle.append(strAbn);

            if (isNumeric(strAbn) && strAbn.length() <= 3) {

                if (abnDao.existAbn(strAbn)) {
                    if (!poblacionDao.existPoblacionWithAbn(listaDatos.get(fila).get(2), strAbn)) {

                        error = new ErrorValidacion();
                        error.setCodigo("Error019");
                        error.setDescripcion("El dato de ABN del  registro " + listaDatos.get(fila).get(0)
                                + ", no cubre la población indicada.");
                        procesarFicheroNg.getlErrores().add(error);

                    }
                } else {
                    error = new ErrorValidacion();
                    error.setCodigo("Error019");
                    error.setDescripcion("El dato de ABN del  registro " + listaDatos.get(fila).get(0)
                            + ", no existe en el ABN.");
                    procesarFicheroNg.getlErrores().add(error);
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error019");
                error.setDescripcion("El dato de ABN del  registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico o no tiene la longitud requerida.");
                procesarFicheroNg.getlErrores().add(error);
            }
            col = 6;
            // Procesamos los totales
            String totalNumAsignados = (col < listaDatos.get(fila).size()) ? listaDatos.get(fila).get(col) : "";
            detalle.append(":");
            detalle.append(totalNumAsignados);
            if (!isNumeric(totalNumAsignados)) {

                totalNumAsignados = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error009");
                error.setDescripcion("El dato de total de líneas activas en fijo del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumServicioFijo = "";

            if (col < listaDatos.get(fila).size()) {
                totalNumServicioFijo = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumServicioFijo);
            if (!isNumeric(totalNumServicioFijo)) {

                totalNumServicioFijo = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error020");
                error.setDescripcion("El dato de numeración en servicio en fijo del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumServicioCpp = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumServicioCpp = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumServicioCpp);

            if (!isNumeric(totalNumServicioCpp)) {

                totalNumServicioCpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error021");
                error.setDescripcion("El dato de numeración en servicio en CPP del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumServicioMpp = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumServicioMpp = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumServicioMpp);
            if (!isNumeric(totalNumServicioMpp)) {

                totalNumServicioMpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error022");
                error.setDescripcion("El dato de numeración en servicio en MPP del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumCuarentenaFijo = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumCuarentenaFijo = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumCuarentenaFijo);
            if (!isNumeric(totalNumCuarentenaFijo)) {

                totalNumCuarentenaFijo = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error026");
                error.setDescripcion("El dato de numeración en cuarentena en fijo del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumCuarentenCpp = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumCuarentenCpp = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumCuarentenCpp);
            if (!isNumeric(totalNumCuarentenCpp)) {
                totalNumCuarentenCpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error027");
                error.setDescripcion("El dato de numeración en cuarentena en CPP del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumCuarentenaMpp = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumCuarentenaMpp = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumCuarentenaMpp);
            if (!isNumeric(totalNumCuarentenaMpp)) {
                totalNumCuarentenaMpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error028");
                error.setDescripcion("El dato de numeración en cuarentena en MPP del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumPortadoFijo = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumPortadoFijo = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumPortadoFijo);
            if (!isNumeric(totalNumPortadoFijo)) {
                totalNumPortadoFijo = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error031");
                error.setDescripcion("El dato de total numeración portados en fijo del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumPortadoCpp = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumPortadoCpp = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumPortadoCpp);
            if (!isNumeric(totalNumPortadoCpp)) {
                totalNumPortadoCpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error032");
                error.setDescripcion("El dato de total numeración portados en CPP del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumPortadoMpp = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumPortadoMpp = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumPortadoMpp);
            if (!isNumeric(totalNumPortadoMpp)) {
                totalNumPortadoMpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error033");
                error.setDescripcion("El dato de total numeración portados en MPP del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumUsoInternoFijo = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumUsoInternoFijo = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumUsoInternoFijo);
            if (!isNumeric(totalNumUsoInternoFijo)) {
                totalNumUsoInternoFijo = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error036");
                error.setDescripcion("El dato de total numeración uso interno en fijo del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumUsoInternoCpp = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumUsoInternoCpp = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumUsoInternoCpp);
            if (!isNumeric(totalNumUsoInternoCpp)) {
                totalNumUsoInternoCpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error037");
                error.setDescripcion("El dato de total numeración uso interno en CPP del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumUsoInternoMpp = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumUsoInternoMpp = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumUsoInternoMpp);
            if (!isNumeric(totalNumUsoInternoMpp)) {
                totalNumUsoInternoMpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error038");
                error.setDescripcion("El dato de total numeración telefonía pública  en MPP del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumPublicaFijo = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumPublicaFijo = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumPublicaFijo);
            if (!isNumeric(totalNumPublicaFijo)) {
                totalNumPublicaFijo = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error041");
                error.setDescripcion("El dato de total numeración telefonía pública en fijo del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumPublicaCpp = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumPublicaCpp = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumPublicaCpp);
            if (!isNumeric(totalNumPublicaCpp)) {
                totalNumPublicaCpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error042");
                error.setDescripcion("El dato de total numeración telefonía pública en CPP del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalNumPublicaMpp = "";
            if (col < listaDatos.get(fila).size()) {
                totalNumPublicaMpp = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(totalNumPublicaMpp);
            if (!isNumeric(totalNumPublicaMpp)) {
                totalNumPublicaMpp = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error043");
                error.setDescripcion("El dato de total numeración telefonía pública en MPP del registro "
                        + listaDatos.get(fila).get(0) + " no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            detalle.append(":");
            detalle.append(listaDatos.get(fila).get(0));

            listaDetalles.add(detalle.toString());
        }
        procesarFicheroNg.setListaDatos(listaDetalles);

        return procesarFicheroNg;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public RetornoProcesaFicheroReportes validarFicheroLineasArrendada(File fichero) throws Exception {

        List<List<String>> listaDatos = extraerExcel(fichero);

        listaDatos = setearCamposVacios(listaDatos, 13);

        procesarFicheroNg = new RetornoProcesaFicheroReportes();

        LineaArrendada lineaArrendada = new LineaArrendada();

        procesarFicheroNg = procesarCabecera(listaDatos, lineaArrendada);

        lineaArrendada = (LineaArrendada) procesarFicheroNg.getReporte();

        ErrorValidacion error = null;

        // Procesamos el detalle del reporte de la Linea Arrendatario Detallada

        List<String> listaDetalles = new ArrayList<String>();

        for (int fila = 3; fila < listaDatos.size(); fila++) {
            StringBuffer detalle = new StringBuffer();

            // Validamos los datos de ubicacion
            validarUbicacion(listaDatos, fila, 0);
            detalle.append(listaDatos.get(fila).get(1));

            String strAbn = "";
            if (5 < listaDatos.get(fila).size()) {
                strAbn = listaDatos.get(fila).get(5);
            }

            detalle.append(":");
            detalle.append(strAbn);
            if (isNumeric(strAbn) && strAbn.length() <= 3) {

                if (abnDao.existAbn(strAbn)) {
                    if (!poblacionDao.existPoblacionWithAbn(listaDatos.get(fila).get(1), strAbn)) {

                        error = new ErrorValidacion();
                        error.setCodigo("Error019");
                        error.setDescripcion("El dato de ABN del  registro " + listaDatos.get(fila).get(0)
                                + ", no cubre la población indicada.");
                        procesarFicheroNg.getlErrores().add(error);
                    }

                } else {
                    error = new ErrorValidacion();
                    error.setCodigo("Error019");
                    error.setDescripcion("El dato de ABN del  registro " + listaDatos.get(fila).get(0)
                            + ", no existe en el SNS.");
                    procesarFicheroNg.getlErrores().add(error);
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error019");
                error.setDescripcion("El dato de ABN del  registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico o no tiene la longitud requerida.");
                procesarFicheroNg.getlErrores().add(error);
            }

            // Procesamos datos de la numeracion rentada
            int col = 6;
            String strNir = "";
            if (col < listaDatos.get(fila).size()) {
                strNir = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            Nir nir = null;

            if (isNumeric(strNir) && strNir.length() <= 3) {
                nir = nirDao.getNirByCodigo(Integer.parseInt(strNir));
                if (nir != null) {
                    detalle.append(nir.getId().toString());
                    if (!nirDao.existsNirWithAbn(strAbn, strNir)) {

                        error = new ErrorValidacion();
                        error.setCodigo("Error051");
                        error.setDescripcion("El dato de NIR del  registro " + listaDatos.get(fila).get(0)
                                + ", no está asignado al ABN del registro.");
                        procesarFicheroNg.getlErrores().add(error);

                    }
                } else {
                    error = new ErrorValidacion();
                    error.setCodigo("Error051");
                    error.setDescripcion("El dato de NIR del  registro " + listaDatos.get(fila).get(0)
                            + " no se registra en el SNS.");
                    procesarFicheroNg.getlErrores().add(error);
                }
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error051");
                error.setDescripcion("El dato de NIR del  registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico o no tiene la longitud requerida.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String sna = "";
            if (col < listaDatos.get(fila).size()) {
                sna = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(sna);

            if (isNumeric(sna) && sna.length() <= Serie.TAM_SNA) {
                if (!serieDao.existSerieWithNir(sna, strNir)) {

                    error = new ErrorValidacion();
                    error.setCodigo("Error052");
                    error.setDescripcion("El dato de Serie del  registro " + listaDatos.get(fila).get(0)
                            + " no esta asignado al NIR del registro");
                    procesarFicheroNg.getlErrores().add(error);
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error052");
                error.setDescripcion("El dato de Serie del  registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico o no tiene la longitud requerida.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String numInicial = "";
            if (col < listaDatos.get(fila).size()) {
                numInicial = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(numInicial);
            if (!isNumeric(numInicial) || numInicial.length() > 4) {

                error = new ErrorValidacion();
                error.setCodigo("Error053");
                error.setDescripcion("El dato de Número inicial del  registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico o no tiene la longitud requerida.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String numFinal = "";
            if (col < listaDatos.get(fila).size()) {
                numFinal = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(numFinal);
            if (!isNumeric(numFinal) || numFinal.length() > 4) {

                error = new ErrorValidacion();
                error.setCodigo("Error054");
                error.setDescripcion("El dato de Número final del  registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico o no tiene la longitud requerida.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String strTipoRed = "";
            if (col < listaDatos.get(fila).size()) {
                strTipoRed = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            if (strTipoRed.equals("FIJO")) {
                detalle.append(TipoRed.FIJA);
            } else if (strTipoRed.equals("MOVIL")) {
                detalle.append(TipoRed.MOVIL);
            }

            if (isAlfabetic(strTipoRed)) {

                if (!(strTipoRed.equals("FIJO") || strTipoRed.equals("MOVIL"))) {

                    error = new ErrorValidacion();
                    error.setCodigo("Error055");
                    error.setDescripcion("El dato de Tipo de Red del  registro " + listaDatos.get(fila).get(0)
                            + " no es correcto.");
                    procesarFicheroNg.getlErrores().add(error);
                }
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error055");
                error.setDescripcion("El dato de Tipo de Red del  registro " + listaDatos.get(fila).get(0)
                        + " cargado no es alfabético.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String strTipoModalidad = "";
            if (col < listaDatos.get(fila).size()) {
                strTipoModalidad = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            if (strTipoModalidad.equals("CPP")) {
                detalle.append(TipoModalidad.CPP);
            } else if (strTipoModalidad.equals("MPP")) {
                detalle.append(TipoModalidad.MPP);
            }

            if (isAlfabetic(strTipoModalidad)) {
                if (!(strTipoModalidad.equals("CPP") || strTipoModalidad.equals("MPP")
                || strTipoModalidad.equals(""))) {
                    error = new ErrorValidacion();
                    error.setCodigo("Error056");
                    error.setDescripcion("El dato Modalidad del  registro " + listaDatos.get(fila).get(0)
                            + " no es correcto.");
                    procesarFicheroNg.getlErrores().add(error);
                } else if (strTipoRed.equals("MOVIL")) {
                    if (!(strTipoModalidad.equals("CPP") || strTipoModalidad.equals("MPP"))) {

                        error = new ErrorValidacion();
                        error.setCodigo("Error056");
                        error.setDescripcion("El dato Modalidad del  registro " + listaDatos.get(fila).get(0)
                                + " no es correcto.");
                        procesarFicheroNg.getlErrores().add(error);
                    }
                } else if (strTipoRed.equals("FIJO") && !strTipoModalidad.equals("")) {
                    error = new ErrorValidacion();
                    error.setCodigo("Error056");
                    error.setDescripcion("El dato Modalidad del  registro " + listaDatos.get(fila).get(0)
                            + " no es correcto.");
                    procesarFicheroNg.getlErrores().add(error);
                }
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error056");
                error.setDescripcion("El dato Modalidad del  registro " + listaDatos.get(fila).get(0)
                        + " cargado no es alfabético.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String strArrendatario;
            if (col < listaDatos.get(fila).size()) {
                strArrendatario = listaDatos.get(fila).get(col);
            } else {
                strArrendatario = "";
            }

            Proveedor arrendatario = null;
            if (isAlfabetic(strArrendatario)) {
                arrendatario = proveedorDAO.getProveedorByNombre(strArrendatario);
                if (arrendatario != null) {
                    detalle.append(":");
                    detalle.append(arrendatario.getId());
                    if (!convenioDAO.existConvenio(
                            lineaArrendada.getSolicitudLineasActivas().getProveedorSolicitante(),
                            arrendatario)) {

                        error = new ErrorValidacion();
                        error.setCodigo("Error063");
                        error.setDescripcion("El dato PST arrendador para el registro "
                                + listaDatos.get(fila).get(0)
                                + " no es está relacionado con el PST que reporta.");
                        procesarFicheroNg.getlErrores().add(error);
                    }

                } else {
                    error = new ErrorValidacion();
                    error.setCodigo("Error057");
                    error.setDescripcion("El dato PST arrendatario para el registro " + listaDatos.get(fila).get(0)
                            + " no está registrado.");
                    procesarFicheroNg.getlErrores().add(error);
                }
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error057");
                error.setDescripcion("El dato PST arrendatario del  registro " + listaDatos.get(fila).get(0)
                        + " cargado no es alfabético.");
                procesarFicheroNg.getlErrores().add(error);
            }

            detalle.append(":");
            detalle.append(listaDatos.get(fila).get(0));

            listaDetalles.add(detalle.toString());
        }

        procesarFicheroNg.setListaDatos(listaDetalles);

        return procesarFicheroNg;

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public RetornoProcesaFicheroReportes validarFicheroLineasArrendatario(File fichero) throws Exception {

        List<List<String>> listaDatos = extraerExcel(fichero);

        procesarFicheroNg = new RetornoProcesaFicheroReportes();

        listaDatos = setearCamposVacios(listaDatos, 14);

        LineaArrendatario lineaArrendatario = new LineaArrendatario();

        procesarFicheroNg = procesarCabecera(listaDatos, lineaArrendatario);

        lineaArrendatario = (LineaArrendatario) procesarFicheroNg.getReporte();

        ErrorValidacion error = null;

        // Validamos la cabecera de lineas arrendatario
        int col = 3;
        col++;
        String sumNumRentada = null;
        if (col < listaDatos.get(1).size()) {
            sumNumRentada = listaDatos.get(1).get(col);
        }

        if (isNumeric(sumNumRentada)) {
            if (Integer.parseInt(sumNumRentada) == rangoDao
                    .getTotalNumRangosArrendados(null,
                            lineaArrendatario.getSolicitudLineasActivas().getProveedorSolicitante()).intValue()) {
                lineaArrendatario.setSumaNumRentada(new BigDecimal(sumNumRentada));
            } else {
                sumNumRentada = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error058");
                error.setDescripcion("El dato suma de numeración rentada para el reporte cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            error = new ErrorValidacion();
            error.setCodigo("Error058");
            error.setDescripcion("El dato suma de numeración rentada para el reporte cargado no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumRentadaActivasFijo = "";
        if (col < listaDatos.get(1).size()) {
            sumNumRentadaActivasFijo = listaDatos.get(1).get(col);
        }
        if (isNumeric(sumNumRentadaActivasFijo)) {

            lineaArrendatario.setSumaNumRentadaFijo(new BigDecimal(sumNumRentadaActivasFijo));

        } else {
            sumNumRentadaActivasFijo = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error059");
            error.setDescripcion("El dato suma de líneas rentada activa fijo para el reporte cargado no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumRentadaActivasCpp = "";
        if (col < listaDatos.get(1).size()) {
            sumNumRentadaActivasCpp = listaDatos.get(1).get(col);
        }
        if (isNumeric(sumNumRentadaActivasCpp)) {

            lineaArrendatario.setSumaNumRentadaActivaCpp(new BigDecimal(sumNumRentadaActivasCpp));

        } else {
            sumNumRentadaActivasCpp = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error060");
            error.setDescripcion("El dato suma de líneas rentada activa CPP para el reporte cargado no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumRentadaActivasMpp = "";
        if (col < listaDatos.get(1).size()) {
            sumNumRentadaActivasMpp = listaDatos.get(1).get(col);
        }
        if (isNumeric(sumNumRentadaActivasMpp)) {

            lineaArrendatario.setSumaNumRentadaActivaMpp(new BigDecimal(sumNumRentadaActivasMpp));

        } else {
            sumNumRentadaActivasMpp = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error061");
            error.setDescripcion("El dato suma de líneas rentada activa MPP para el reporte cargado no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumRentadaActivaMovil = "";
        if (col < listaDatos.get(1).size()) {
            sumNumRentadaActivaMovil = listaDatos.get(1).get(col);
        }
        if (isNumeric(sumNumRentadaActivaMovil)) {
            if (Integer.parseInt(sumNumRentadaActivaMovil) == Integer.parseInt(sumNumRentadaActivasMpp)
                    + Integer.parseInt(sumNumRentadaActivasCpp)) {
                lineaArrendatario.setSumaNumRentadaActivaMovil(new BigDecimal(sumNumRentadaActivaMovil));
            } else {
                sumNumRentadaActivaMovil = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error062");
                error.setDescripcion("El dato suma de numeración rentada activa móvil"
                        + " para el reporte cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumNumRentadaActivaMovil = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error062");
            error.setDescripcion("El dato suma de numeración rentada activa móvil"
                    + " para el reporte cargado no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        col++;
        String sumNumRentadaActiva = "";
        if (col < listaDatos.get(1).size()) {
            sumNumRentadaActiva = listaDatos.get(1).get(col);
        }
        if (isNumeric(sumNumRentadaActiva)) {
            if (Integer.parseInt(sumNumRentadaActiva) == Integer.parseInt(sumNumRentadaActivasFijo)
                    + Integer.parseInt(sumNumRentadaActivaMovil)) {
                lineaArrendatario.setSumaNumRentadaActiva(new BigDecimal(sumNumRentadaActiva));
            } else {

                error = new ErrorValidacion();
                error.setCodigo("Error0xx");
                error.setDescripcion("El dato suma de numeración rentada"
                        + " activa para el reporte cargado no es correcto.");
                procesarFicheroNg.getlErrores().add(error);
            }

        } else {
            sumNumRentadaActiva = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error0xx");
            error.setDescripcion("El dato suma de numeración rentada activa para el reporte cargado no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        // Procesamos el detalle del reporte de la Linea Arrendatario Detallada

        List<String> listaDetalles = new ArrayList<String>();

        for (int fila = 3; fila < listaDatos.size(); fila++) {

            StringBuffer detalle = new StringBuffer();

            // Validamos los datos de ubicacion
            validarUbicacion(listaDatos, fila, 1);
            detalle.append(listaDatos.get(fila).get(2));

            String strAbn = listaDatos.get(fila).get(1);
            detalle.append(":");
            detalle.append(strAbn);
            if (isNumeric(strAbn) && strAbn.length() <= 3) {

                if (abnDao.existAbn(strAbn)) {
                    if (!poblacionDao.existPoblacionWithAbn(listaDatos.get(fila).get(2), strAbn)) {

                        error = new ErrorValidacion();
                        error.setCodigo("Error019");
                        error.setDescripcion("El dato de ABN del  registro " + listaDatos.get(fila).get(0)
                                + ", no cubre la población indicada.");
                        procesarFicheroNg.getlErrores().add(error);
                    }

                } else {
                    error = new ErrorValidacion();
                    error.setCodigo("Error019");
                    error.setDescripcion("El dato de ABN del  registro " + listaDatos.get(fila).get(0)
                            + ", no existe en el SNS.");
                    procesarFicheroNg.getlErrores().add(error);
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error019");
                error.setDescripcion("El dato de ABN del  registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico o no tiene la longitud requerida.");
                procesarFicheroNg.getlErrores().add(error);
            }

            // Procesamos datos de la numeracion rentada
            col = 6;
            String strNir = "";
            if (col < listaDatos.get(fila).size()) {
                strNir = listaDatos.get(fila).get(col);
            }
            detalle.append(":");

            if (isNumeric(strNir) && strNir.length() <= 3) {
                Nir nir = nirDao.getNirByCodigo(Integer.parseInt(strNir));
                if (nir != null) {
                    detalle.append(nir.getId().toString());
                    if (!nirDao.existsNirWithAbn(strAbn, strNir)) {

                        error = new ErrorValidacion();
                        error.setCodigo("Error051");
                        error.setDescripcion("El dato de NIR del  registro " + listaDatos.get(fila).get(0)
                                + ", no está asignado al ABN del registro.");
                        procesarFicheroNg.getlErrores().add(error);

                    }
                } else {
                    error = new ErrorValidacion();
                    error.setCodigo("Error051");
                    error.setDescripcion("El dato de NIR del  registro " + listaDatos.get(fila).get(0)
                            + " no se registra en el SNS.");
                    procesarFicheroNg.getlErrores().add(error);
                }
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error051");
                error.setDescripcion("El dato de NIR del  registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico o no tiene la longitud requerida.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String sna = "";
            if (col < listaDatos.get(fila).size()) {
                sna = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(sna);

            if (isNumeric(sna) && sna.length() <= Serie.TAM_SNA) {
                if (!serieDao.existSerieWithNir(sna, strNir)) {

                    error = new ErrorValidacion();
                    error.setCodigo("Error052");
                    error.setDescripcion("El dato de Serie del  registro " + listaDatos.get(fila).get(0)
                            + " no esta asignado al NIR del registro");
                    procesarFicheroNg.getlErrores().add(error);
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error052");
                error.setDescripcion("El dato de Serie del  registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico o no tiene la longitud requerida.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String numInicial = "";
            if (col < listaDatos.get(fila).size()) {
                numInicial = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(numInicial);
            if (!isNumeric(numInicial) && numInicial.length() > 4) {

                error = new ErrorValidacion();
                error.setCodigo("Error053");
                error.setDescripcion("El dato de Número inicial del  registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico o no tiene la longitud requerida.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String numFinal = "";
            if (col < listaDatos.get(fila).size()) {
                numFinal = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            detalle.append(numFinal);
            if (!isNumeric(numFinal) && numFinal.length() > 4) {
                error = new ErrorValidacion();
                error.setCodigo("Error054");
                error.setDescripcion("El dato de Número final del  registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico o no tiene la longitud requerida.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String strTipoRed = "";
            if (col < listaDatos.get(fila).size()) {
                strTipoRed = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            if (strTipoRed.equals("FIJO")) {
                detalle.append(TipoRed.FIJA);
            } else if (strTipoRed.equals("MOVIL")) {
                detalle.append(TipoRed.MOVIL);
            }
            if (isAlfabetic(strTipoRed)) {

                if (!(strTipoRed.equals("FIJO") || strTipoRed.equals("MOVIL"))) {

                    error = new ErrorValidacion();
                    error.setCodigo("Error055");
                    error.setDescripcion("El dato de Tipo de Red del  registro " + listaDatos.get(fila).get(0)
                            + " no es correcto.");
                    procesarFicheroNg.getlErrores().add(error);
                }
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error055");
                error.setDescripcion("El dato de Tipo de Red del  registro " + listaDatos.get(fila).get(0)
                        + " cargado no es alfabético.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String strTipoModalidad = "";
            if (col < listaDatos.get(fila).size()) {
                strTipoModalidad = listaDatos.get(fila).get(col);
            }
            detalle.append(":");
            if (strTipoModalidad.equals("CPP")) {
                detalle.append(TipoModalidad.CPP);
            } else if (strTipoModalidad.equals("MPP")) {
                detalle.append(TipoModalidad.MPP);
            }

            if (isAlfabetic(strTipoModalidad)) {
                if (!(strTipoModalidad.equals("CPP") || strTipoModalidad.equals("MPP")
                || strTipoModalidad.equals(""))) {
                    error = new ErrorValidacion();
                    error.setCodigo("Error056");
                    error.setDescripcion("El dato Modalidad del  registro " + listaDatos.get(fila).get(0)
                            + " no es correcto.");
                    procesarFicheroNg.getlErrores().add(error);
                } else if (strTipoRed.equals("MOVIL")) {
                    if (!(strTipoModalidad.equals("CPP") || strTipoModalidad.equals("MPP"))) {

                        error = new ErrorValidacion();
                        error.setCodigo("Error056");
                        error.setDescripcion("El dato Modalidad del  registro " + listaDatos.get(fila).get(0)
                                + " no es correcto.");
                        procesarFicheroNg.getlErrores().add(error);
                    }
                } else if (strTipoRed.equals("FIJO") && !strTipoModalidad.equals("")) {
                    error = new ErrorValidacion();
                    error.setCodigo("Error056");
                    error.setDescripcion("El dato Modalidad del  registro " + listaDatos.get(fila).get(0)
                            + " no es correcto.");
                    procesarFicheroNg.getlErrores().add(error);
                }
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error056");
                error.setDescripcion("El dato Modalidad del  registro " + listaDatos.get(fila).get(0)
                        + " cargado no es alfabético.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String totalActivados = "";
            if (col < listaDatos.get(fila).size()) {
                totalActivados = listaDatos.get(fila).get(col);
            }
            if (!isNumeric(totalActivados)) {
                error = new ErrorValidacion();
                error.setCodigo("Error064");
                error.setDescripcion("El dato total de líneas activas del registro " + listaDatos.get(fila).get(0)
                        + ", no es numérico.");
                procesarFicheroNg.getlErrores().add(error);
            }

            col++;
            String strArrendador = "";
            if (col < listaDatos.get(fila).size()) {
                strArrendador = listaDatos.get(fila).get(col);
            }
            Proveedor arrendador = null;
            if (isAlfabetic(strArrendador)) {
                arrendador = proveedorDAO.getProveedorByNombre(strArrendador);
                if (arrendador != null) {
                    detalle.append(":");
                    detalle.append(arrendador.getId());
                    if (!convenioDAO.existConvenio(arrendador, lineaArrendatario.getSolicitudLineasActivas()
                            .getProveedorSolicitante())) {

                        error = new ErrorValidacion();
                        error.setCodigo("Error063");
                        error.setDescripcion("El dato PST arrendador para el registro "
                                + listaDatos.get(fila).get(0)
                                + " no es está relacionado con el PST que reporta.");
                        procesarFicheroNg.getlErrores().add(error);
                    }

                } else {
                    error = new ErrorValidacion();
                    error.setCodigo("Error063");
                    error.setDescripcion("El dato PST arrendador para el registro " + listaDatos.get(fila).get(0)
                            + " no está registrado.");
                    procesarFicheroNg.getlErrores().add(error);
                }
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error063");
                error.setDescripcion("El dato PST arrendador del  registro " + listaDatos.get(fila).get(0)
                        + " cargado no es alfabético.");
                procesarFicheroNg.getlErrores().add(error);
            }

            detalle.append(":");
            detalle.append(totalActivados);

            detalle.append(":");
            detalle.append(listaDatos.get(fila).get(0));

            listaDetalles.add(detalle.toString());
        }

        procesarFicheroNg.setListaDatos(listaDetalles);

        return procesarFicheroNg;
    }

    /**
     * Añade campos vacios al mapa de datos.
     * @param listaDatos datos
     * @param tam tamaño
     * @return datos
     */
    private List<List<String>> setearCamposVacios(List<List<String>> listaDatos, int tam) {
        for (int i = 3; i < listaDatos.size(); i++) {
            for (int j = listaDatos.get(i).size(); j < tam; j++) {
                listaDatos.get(i).add("");
            }
        }
        return listaDatos;
    }

    /**
     * Calcula el total de una columna.
     * @param listaDatos datos
     * @param col columa
     * @return total
     */
    private Integer totalcolumna(List<List<String>> listaDatos, int col) {
        Integer total = 0;

        for (int i = 3; i < listaDatos.size(); i++) {
            if (col < listaDatos.get(i).size()) {
                if (!listaDatos.get(i).get(col).equals("") && isNumeric(listaDatos.get(i).get(col))) {
                    total += Integer.parseInt(listaDatos.get(i).get(col));
                }
            }

        }

        return total;
    }

    /**
     * Procesa la cabecera de un reporte.
     * @param listaDatos datos
     * @param reporte a procesar
     * @return RetornoProcesaFicheroReportes reporte
     * @throws ParseException error
     */
    private RetornoProcesaFicheroReportes procesarCabecera(List<List<String>> listaDatos, Reporte reporte)
            throws ParseException {

        reporte.setSolicitudLineasActivas(new SolicitudLineasActivas());
        EstadoSolicitud estadoSol = new EstadoSolicitud();
        estadoSol.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
        reporte.getSolicitudLineasActivas().setEstadoSolicitud(estadoSol);

        RetornoProcesaFicheroReportes procesarFicheroNg = new RetornoProcesaFicheroReportes();

        procesarFicheroNg.setlErrores(new ArrayList<ErrorValidacion>());

        ErrorValidacion error = null;

        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");

        Date fechaActual = new Date();
        Date fecha = new Date();
        String fechaSistema = formateador.format(fecha);

        fechaActual = formateador.parse(fechaSistema);

        String nombrePst = listaDatos.get(1).get(0);

        // Comprobamos si el proveedor existe

        procesarFicheroNg.setNombreProveedor(nombrePst);
        Proveedor pst = proveedorDAO.getProveedorByNombre(nombrePst);

        if (pst == null) {
            procesarFicheroNg.setProveedorRegistrado(false);
        } else {
            procesarFicheroNg.setProveedorRegistrado(true);
            reporte.getSolicitudLineasActivas().setProveedorSolicitante(pst);
        }

        // Validamos la fecha de reporte
        String strFechaReporte = listaDatos.get(1).get(1);
        boolean errorFecha = false;

        if (isFechaValida(strFechaReporte)) {

            Date fechaReporte = new Date();

            fechaReporte = formateador.parse(strFechaReporte);
            if (!fechaReporte.after(fechaActual)) {
                reporte.getSolicitudLineasActivas().setFechaSolicitud(fechaReporte);
            } else {
                errorFecha = true;
            }
        } else {
            errorFecha = true;
        }

        if (errorFecha) {
            error = new ErrorValidacion();
            error.setCodigo("Error001");
            error.setDescripcion("El dato de fecha de reporte para el reporte cargado no es correcto.");
            procesarFicheroNg.getlErrores().add(error);
        }
        // Validamos la referencia
        String referencia = listaDatos.get(1).get(2);
        if (referencia.length() <= 60) {
            reporte.setReferencia(referencia);
        } else {
            error = new ErrorValidacion();
            error.setCodigo("ErrorXXX");
            error.setDescripcion("El dato de número de referencia para el reporte cargado no es correcto.");
            procesarFicheroNg.getlErrores().add(error);
        }
        // Validamos el numero de registros
        String srtNumRegistros = listaDatos.get(1).get(3);
        int tamListaDatos = listaDatos.size();
        if (isNumeric(srtNumRegistros)) {
            int numRegistros = Integer.parseInt(srtNumRegistros);

            if (numRegistros == tamListaDatos - 3) {
                reporte.setTotalRegistros(new BigDecimal(numRegistros));
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error002");
                error.setDescripcion("El dato de total de registros para el reporte cargado no es correcto");
                procesarFicheroNg.getlErrores().add(error);
            }
        } else {
            error = new ErrorValidacion();
            error.setCodigo("Error002");
            error.setDescripcion("El dato de total de registro para el reporte cargado, no es numérico.");
            procesarFicheroNg.getlErrores().add(error);
        }

        procesarFicheroNg.setReporte(reporte);
        return procesarFicheroNg;
    }

    /**
     * Método que extrae los datos de un formato xlsx.
     * @param fichero fichero.
     * @return List<List<XSSFCell>> lista de listas.
     * @throws Exception exception.
     */

    @SuppressWarnings({"resource", "rawtypes"})
    private List<List<String>> extraerExcel(File fichero) throws Exception {
        List<List<String>> sheetData = new ArrayList<List<String>>();
        FileInputStream fis = null;
        XSSFWorkbook workbook = null;
        try {
            fis = new FileInputStream(fichero);

            workbook = new XSSFWorkbook(fis);
            workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();

            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator rows = sheet.rowIterator();
            while (rows.hasNext()) {
                XSSFRow row = (XSSFRow) rows.next();

                Iterator cells = row.cellIterator();
                List<String> data = new ArrayList<String>();
                while (cells.hasNext()) {
                    XSSFCell cell = (XSSFCell) cells.next();
                    if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                            || cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {

                        if (DateUtil.isCellDateFormatted(cell)) {
                            DataFormatter df = new DataFormatter();
                            data.add(df.formatCellValue(cell));
                        } else {

                            data.add(String.valueOf((int) cell.getNumericCellValue()));
                        }
                    } else {
                        data.add(cell.getStringCellValue());
                    }
                }
                sheetData.add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook = null;
            if (fis != null) {
                fis.close();
            }
        }

        int tamReal = obtenerNumFilasReales(sheetData);

        return sheetData.subList(0, tamReal);
    }

    /**
     * Obtiene el numero de filas con datos real.
     * @param sheetData mapa de datos
     * @return filas reales
     */
    private int obtenerNumFilasReales(List<List<String>> sheetData) {

        int tam = 3;
        for (int i = 3; i < sheetData.size(); i++) {
            for (int j = 0; j < sheetData.get(i).size(); j++) {
                if (!sheetData.get(i).get(j).equals("")) {
                    tam++;
                    break;
                } else if (sheetData.get(i).get(j).equals("") && j == sheetData.get(i).size() - 1) {
                    return tam;
                }
            }
        }
        return tam;
    }

    /**
     * Metodo que comprueba el formato de una fecha.
     * @param fecha fecha
     * @return boolean
     */
    private boolean isFechaValida(String fecha) {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            formatoFecha.setLenient(false);
            formatoFecha.parse(fecha);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Valida la ubicacion de cada registro.
     * @param listaDatos mapa
     * @param fila fila
     * @param col columna
     */
    private void validarUbicacion(List<List<String>> listaDatos,
            int fila, int col) {

        String inegi = listaDatos.get(fila).get(col + 1);

        boolean claveCensalOk = true;

        if (!isNumeric(inegi) || inegi.length() != 9) {
            claveCensalOk = false;
        } else {
            claveCensalOk = poblacionDao.existePoblacion(inegi);

        }

        ErrorValidacion error;
        if (!claveCensalOk) {
            error = new ErrorValidacion();
            error.setCodigo("Error008");
            error.setDescripcion("La clave censal del registro " + listaDatos.get(fila).get(0)
                    + ", no está registrada en el SNS.");
            procesarFicheroNg.getlErrores().add(error);
            claveCensalOk = false;
        }

    }

    /**
     * Compruba si una cadena de caracteres es alfanumerica.
     * @param cadena String
     * @return boolean
     */
    private boolean isAlfabetic(String cadena) {

        return !cadena.matches("([0-9])+");
    }

    @Override
    public RetornoProcesaFicheroReportesNng validarLineasActivasNng(File fichero) throws Exception {

        List<List<String>> listaDatos = extraerExcel(fichero);

        listaDatos = setearCamposVacios(listaDatos, 6);

        procesarFicheroNng = new RetornoProcesaFicheroReportesNng();

        ReporteLineaActivaNng lineaActiva = new ReporteLineaActivaNng();

        procesarFicheroNng = procesarCabeceraNng(listaDatos, lineaActiva);

        lineaActiva = (ReporteLineaActivaNng) procesarFicheroNng.getReporte();

        // Registramos la fecha actual
        lineaActiva.setFechaReporte(FechasUtils.getFechaHoy("dd/MM/yyyy HH:mm:ss"));

        ErrorValidacion error = null;

        // Validamos la cabecera de lineas activas

        int col = 3;

        col++;
        String totalNumActiva = listaDatos.get(1).get(col);

        if (isNumeric(totalNumActiva)) {
            if (Integer.parseInt(totalNumActiva) == totalcolumna(listaDatos, col + 1)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {

                    lineaActiva.setTotalNumActiva(new BigDecimal(totalNumActiva));
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error003");
                error.setDescripcion("El dato de Suma de numeración para el reporte cargado no es correcto.");
                procesarFicheroNng.getlErrores().add(error);
            }

        } else {
            totalNumActiva = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error003");
            error.setDescripcion("El dato de suma de numeración para el reporte cargado, no es numérico.");
            procesarFicheroNng.getlErrores().add(error);
        }

        // Procesamos el detalle del reporte de la Linea Activa

        List<String> listaDetalles = new ArrayList<String>();

        for (int fila = 3; fila < listaDatos.size(); fila++) {

            StringBuffer detalle = new StringBuffer();

            detalle = procesarDetalleNng(detalle, listaDatos.get(fila));

            String numActiva = listaDatos.get(fila).get(5);
            detalle.append(":");
            detalle.append(numActiva);

            if (isNumeric(numActiva)) {
                if (!(0 <= Integer.parseInt(numActiva) && 10000 >= Integer.parseInt(numActiva))) {
                    error = new ErrorValidacion();
                    error.setCodigo("Error006");
                    error.setDescripcion("La cantidad de numeración activa para el registro "
                            + listaDatos.get(fila).get(0)
                            + " en el reporte cargado no está comprendido entre 0 y 10.000.");
                    procesarFicheroNng.getlErrores().add(error);
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error006");
                error.setDescripcion("La cantidad de numeración activa para el registro " + listaDatos.get(fila).get(0)
                        + " en el reporte cargado, no es numérica.");
                procesarFicheroNng.getlErrores().add(error);
            }

            listaDetalles.add(detalle.toString());

        }

        procesarFicheroNng.setListaDatos(listaDetalles);
        return procesarFicheroNng;

    }

    /**
     * Procesa lo detalles comunes de los reportes NNG.
     * @param detalle detalle
     * @param fila detalle
     * @return detalle
     */
    private StringBuffer procesarDetalleNng(StringBuffer detalle, List<String> fila) {
        ErrorValidacion error = null;
        int col = 1;

        // Validamos la clave de servicio
        String strclave = fila.get(col++);
        detalle.append(strclave);
        detalle.append(":");
        if (isNumeric(strclave)) {

            ClaveServicio clave = claveServicioDao.getClaveServicioByCodigo(new BigDecimal(strclave));
            if (clave == null) {
                error = new ErrorValidacion();
                error.setCodigo("Error004");
                error.setDescripcion("La clave de servicio  para el registro " + fila.get(0)
                        + " en el reporte cargado no está registrada.");
                procesarFicheroNng.getlErrores().add(error);
            }

        } else {
            error = new ErrorValidacion();
            error.setCodigo("Error004");
            error.setDescripcion("La clave de servicio  para el registro " + fila.get(0)
                    + " en el reporte cargado, no es numérico.");
            procesarFicheroNng.getlErrores().add(error);
        }
        if (strclave.length() > ClaveServicio.TAM_CLAVE) {
            error = new ErrorValidacion();
            error.setCodigo("Error004");
            error.setDescripcion("La clave de servicio  para el registro " + fila.get(0)
                    + " en el reporte cargado no tiene longitud 3.");
            procesarFicheroNng.getlErrores().add(error);
        }

        // Validamos el SNA
        String sna = fila.get(col++);
        detalle.append(sna);
        detalle.append(":");
        if (isNumeric(sna)) {
            SerieNng serie = null;
            if (isNumeric(strclave)) {
                serie = serieNngDao.getSerie(new BigDecimal(strclave), new BigDecimal(sna));
            }
            if (serie == null) {
                error = new ErrorValidacion();
                error.setCodigo("Error005");
                error.setDescripcion("La serie para el registro " + fila.get(0)
                        + " en el reporte cargado no está registrada.");
                procesarFicheroNng.getlErrores().add(error);
            }

        } else {
            error = new ErrorValidacion();
            error.setCodigo("Error005");
            error.setDescripcion("La serie para el registro " + fila.get(0)
                    + " en el reporte cargado, no es numérico.");
            procesarFicheroNng.getlErrores().add(error);
        }
        if (strclave.length() > SerieNng.TAM_SNA) {
            error = new ErrorValidacion();
            error.setCodigo("Error005");
            error.setDescripcion("La serie para el registro " + fila.get(0)
                    + " en el reporte cargado no tiene longitud 3.");
            procesarFicheroNng.getlErrores().add(error);
        }

        String numIni = fila.get(col++);
        String numFinal = fila.get(col++);

        // Validamos el numero inicial
        detalle.append(numIni);
        detalle.append(":");

        if (isNumeric(numIni)) {
            if (!(0 <= Integer.parseInt(numIni) && 9999 >= Integer.parseInt(numIni))) {
                error = new ErrorValidacion();
                error.setCodigo("Error006");
                error.setDescripcion("El número inicial  para el registro " + fila.get(0)
                        + " en el reporte cargado no está dentro del rango permitido.");
                procesarFicheroNng.getlErrores().add(error);
            }
            if (Integer.parseInt(numIni) > Integer.parseInt(numFinal)) {
                error = new ErrorValidacion();
                error.setCodigo("Error006");
                error.setDescripcion("El número inicial  para el registro " + fila.get(0)
                        + " en el reporte cargado es mayor al número final.");
                procesarFicheroNng.getlErrores().add(error);
            }
        } else {
            error = new ErrorValidacion();
            error.setCodigo("Error006");
            error.setDescripcion("El número inicial  para el registro " + fila.get(0)
                    + " en el reporte cargado, no es numérico.");
            procesarFicheroNng.getlErrores().add(error);
        }

        // Validamos el numero final
        detalle.append(numFinal);

        if (isNumeric(numFinal)) {
            if (!(0 <= Integer.parseInt(numFinal) && 9999 >= Integer.parseInt(numFinal))) {
                error = new ErrorValidacion();
                error.setCodigo("Error007");
                error.setDescripcion("El número final para el registro " + fila.get(0)
                        + " en el reporte cargado no está dentro del rango permitido.");
                procesarFicheroNng.getlErrores().add(error);
            }
            if (Integer.parseInt(numIni) > Integer.parseInt(numFinal)) {
                error = new ErrorValidacion();
                error.setCodigo("Error007");
                error.setDescripcion("El número final para el registro " + fila.get(0)
                        + " en el reporte cargado es menor al número inicial.");
                procesarFicheroNng.getlErrores().add(error);
            }
        } else {
            error = new ErrorValidacion();
            error.setCodigo("Error007");
            error.setDescripcion("El número final para el registro " + fila.get(0)
                    + " en el reporte cargado, no es numérico.");
            procesarFicheroNng.getlErrores().add(error);
        }
        return detalle;
    }

    /**
     * Procesa la cabecera de reportes NNG.
     * @param listaDatos datos
     * @param reporte reporte
     * @return reporte
     * @throws ParseException error
     */
    private RetornoProcesaFicheroReportesNng procesarCabeceraNng(List<List<String>> listaDatos, ReporteNng reporte)
            throws ParseException {

        reporte.setSolicitudLineasActivas(new SolicitudLineasActivasNng());
        EstadoSolicitud estadoSol = new EstadoSolicitud();
        estadoSol.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
        reporte.getSolicitudLineasActivas().setEstadoSolicitud(estadoSol);

        RetornoProcesaFicheroReportesNng procesarFichero = new RetornoProcesaFicheroReportesNng();

        procesarFichero.setlErrores(new ArrayList<ErrorValidacion>());

        ErrorValidacion error = null;

        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");

        Date fechaActual = new Date();
        Date fecha = new Date();
        String fechaSistema = formateador.format(fecha);

        fechaActual = formateador.parse(fechaSistema);

        String nombrePst = listaDatos.get(1).get(0);

        // Comprobamos si el proveedor existe

        procesarFichero.setNombreProveedor(nombrePst);
        Proveedor pst = proveedorDAO.getProveedorByNombre(nombrePst);

        if (pst == null) {
            procesarFichero.setProveedorRegistrado(false);
        } else {
            procesarFichero.setProveedorRegistrado(true);
            reporte.getSolicitudLineasActivas().setProveedorSolicitante(pst);
        }

        // Validamos la fecha de reporte
        String strFechaReporte = listaDatos.get(1).get(1);

        if (isFechaValida(strFechaReporte)) {

            Date fechaReporte = new Date();

            fechaReporte = formateador.parse(strFechaReporte);
            if (!fechaReporte.after(fechaActual)) {
                reporte.getSolicitudLineasActivas().setFechaSolicitud(fechaReporte);
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error001");
                error.setDescripcion("La fecha del reporte es mayor a la fecha actual.");
                procesarFichero.getlErrores().add(error);
            }
        } else {
            error = new ErrorValidacion();
            error.setCodigo("Error001");
            error.setDescripcion("La fecha del reporte no cumple el formato definido.");
            procesarFichero.getlErrores().add(error);
        }

        // Validamos la referencia
        String referencia = listaDatos.get(1).get(2);
        if (!StringUtils.isEmpty(referencia)) {
            if (referencia.length() <= 60) {
                reporte.setReferencia(referencia);
            } else {
                error = new ErrorValidacion();
                error.setCodigo("ErrorXXX");
                error.setDescripcion("El dato de número de referencia para el reporte cargado no es correcto.");
                procesarFichero.getlErrores().add(error);
            }
        } else {
            reporte.setReferencia("0");
        }

        // Validamos el numero de registros
        String srtNumRegistros = listaDatos.get(1).get(3);
        int tamListaDatos = listaDatos.size();
        if (isNumeric(srtNumRegistros)) {
            int numRegistros = Integer.parseInt(srtNumRegistros);

            if (numRegistros == tamListaDatos - 3) {
                reporte.setTotalRegistros(new BigDecimal(numRegistros));
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error002");
                error.setDescripcion("El dato de total de registros para el reporte cargado no es correcto");
                procesarFichero.getlErrores().add(error);
            }
        } else {
            error = new ErrorValidacion();
            error.setCodigo("Error002");
            error.setDescripcion("El dato de total de registro para el reporte cargado, no es numérico.");
            procesarFichero.getlErrores().add(error);
        }

        procesarFichero.setReporte(reporte);
        return procesarFichero;
    }

    @Override
    public RetornoProcesaFicheroReportesNng validarLineasActivasDetNng(File fichero) throws Exception {

        List<List<String>> listaDatos = extraerExcel(fichero);

        listaDatos = setearCamposVacios(listaDatos, 9);

        procesarFicheroNng = new RetornoProcesaFicheroReportesNng();

        ReporteLineaActivaDetNng lineaActiva = new ReporteLineaActivaDetNng();

        // Validamos la cabecera de lineas activas
        procesarFicheroNng = procesarCabeceraNng(listaDatos, lineaActiva);

        lineaActiva = (ReporteLineaActivaDetNng) procesarFicheroNng.getReporte();

        // Registramos la fecha actual
        lineaActiva.setFechaReporte(FechasUtils.getFechaHoy("dd/MM/yyyy HH:mm:ss"));

        ErrorValidacion error = null;

        // Validamos los totales
        int col = 3;

        col++;
        String totalNumServicio = listaDatos.get(1).get(col);

        if (isNumeric(totalNumServicio)) {
            if (Integer.parseInt(totalNumServicio) == totalcolumna(listaDatos, col + 1)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {

                    lineaActiva.setTotalNumServicio(new BigDecimal(totalNumServicio));
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error009");
                error.setDescripcion("El total de números en servicio para el reporte cargado "
                        + "no corresponde al detalle.");
                procesarFicheroNng.getlErrores().add(error);
            }

        } else {
            totalNumServicio = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error009");
            error.setDescripcion("El total de números en servicio para el reporte cargado, no es numérico.");
            procesarFicheroNng.getlErrores().add(error);
        }

        col++;
        String totalNumCuarentena = listaDatos.get(1).get(col);

        if (isNumeric(totalNumCuarentena)) {
            if (Integer.parseInt(totalNumCuarentena) == totalcolumna(listaDatos, col + 1)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {

                    lineaActiva.setTotalNumCuarentena(new BigDecimal(totalNumCuarentena));
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error010");
                error.setDescripcion("El total de números en cuarentena para el reporte cargado "
                        + "no corresponde al detalle.");
                procesarFicheroNng.getlErrores().add(error);
            }

        } else {
            totalNumServicio = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error010");
            error.setDescripcion("El total de números en cuarentena para el reporte cargado, no es numérico.");
            procesarFicheroNng.getlErrores().add(error);
        }

        col++;
        String totalNumPortada = listaDatos.get(1).get(col);

        if (isNumeric(totalNumPortada)) {
            if (Integer.parseInt(totalNumPortada) == totalcolumna(listaDatos, col + 1)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {

                    lineaActiva.setTotalNumPortada(new BigDecimal(totalNumPortada));
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error011");
                error.setDescripcion("El total de números portada para el reporte cargado no corresponde al detalle.");
                procesarFicheroNng.getlErrores().add(error);
            }

        } else {
            totalNumServicio = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error011");
            error.setDescripcion("El total de números portada para el reporte cargado, no es numérico.");
            procesarFicheroNng.getlErrores().add(error);
        }

        col++;
        String totalNumActiva = listaDatos.get(1).get(col);

        if (isNumeric(totalNumActiva)) {
            if (Integer.parseInt(totalNumActiva) == Integer.parseInt(totalNumPortada)
                    + Integer.parseInt(totalNumCuarentena) + Integer.parseInt(totalNumServicio)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {

                    lineaActiva.setTotalNumActiva(new BigDecimal(totalNumActiva));
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error012");
                error.setDescripcion("El total de numeración activa para el reporte cargado no corresponde.");
                procesarFicheroNng.getlErrores().add(error);
            }

        } else {
            totalNumActiva = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error012");
            error.setDescripcion("El total de numeración activa para el reporte cargado, no es numérico.");
            procesarFicheroNng.getlErrores().add(error);
        }

        // Procesamos el detalle del reporte de la Linea Activa

        List<String> listaDetalles = new ArrayList<String>();

        for (int fila = 3; fila < listaDatos.size(); fila++) {

            StringBuffer detalle = new StringBuffer();

            detalle = procesarDetalleNng(detalle, listaDatos.get(fila));

            int celda = 5;

            String numServicio = listaDatos.get(fila).get(celda++);
            detalle.append(":");
            detalle.append(numServicio);

            if (!isNumeric(numServicio)) {
                numServicio = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error013");
                error.setDescripcion("La numeración en servicio para el registro " + listaDatos.get(fila).get(0)
                        + " en el reporte cargado, no es numérica.");
                procesarFicheroNng.getlErrores().add(error);
            }

            String numCuarentena = listaDatos.get(fila).get(celda++);
            detalle.append(":");
            detalle.append(numCuarentena);

            if (!isNumeric(numCuarentena)) {
                numCuarentena = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error013");
                error.setDescripcion("La numeración en cuarentena para el registro " + listaDatos.get(fila).get(0)
                        + " en el reporte cargado, no es numérica.");
                procesarFicheroNng.getlErrores().add(error);
            }

            String numPortada = listaDatos.get(fila).get(celda++);
            detalle.append(":");
            detalle.append(numPortada);

            if (!isNumeric(numPortada)) {
                numCuarentena = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error014");
                error.setDescripcion("La numeración portada para el registro " + listaDatos.get(fila).get(0)
                        + " en el reporte cargado, no es numérica.");
                procesarFicheroNng.getlErrores().add(error);
            }

            String numActiva = listaDatos.get(fila).get(celda++);
            detalle.append(":");
            detalle.append(numActiva);

            if (isNumeric(numActiva)) {
                if (Integer.parseInt(numActiva) != Integer.parseInt(numPortada)
                        + Integer.parseInt(numCuarentena) + Integer.parseInt(numServicio)) {
                    error = new ErrorValidacion();
                    error.setCodigo("Error016");
                    error.setDescripcion("La numeración activa para el registro "
                            + listaDatos.get(fila).get(0)
                            + " en el reporte cargado no corresponde.");
                    procesarFicheroNng.getlErrores().add(error);
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error016");
                error.setDescripcion("La numeración activa para el registro " + listaDatos.get(fila).get(0)
                        + " en el reporte cargado, no es numérica.");
                procesarFicheroNng.getlErrores().add(error);
            }

            listaDetalles.add(detalle.toString());

        }

        procesarFicheroNng.setListaDatos(listaDetalles);
        return procesarFicheroNng;

    }

    @Override
    public RetornoProcesaFicheroReportesNng validarFicheroLineasArrendatarioNng(File fichero) throws Exception {

        List<List<String>> listaDatos = extraerExcel(fichero);

        listaDatos = setearCamposVacios(listaDatos, 9);

        procesarFicheroNng = new RetornoProcesaFicheroReportesNng();

        ReporteLineaArrendatarioNng lineaActiva = new ReporteLineaArrendatarioNng();

        // Validamos la cabecera de lineas activas
        procesarFicheroNng = procesarCabeceraNng(listaDatos, lineaActiva);

        lineaActiva = (ReporteLineaArrendatarioNng) procesarFicheroNng.getReporte();

        // Registramos la fecha actual
        lineaActiva.setFechaReporte(FechasUtils.getFechaHoy("dd/MM/yyyy HH:mm:ss"));

        ErrorValidacion error = null;

        // Validamos los totales
        int col = 3;

        col++;
        String totalRentada = listaDatos.get(1).get(col);

        if (isNumeric(totalRentada)) {
            if (Integer.parseInt(totalRentada) == totalcolumna(listaDatos, col + 1)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {

                    lineaActiva.setTotalNumRentada(new BigDecimal(totalRentada));
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error019");
                error.setDescripcion("El total de numeración rentada para el reporte cargado "
                        + "no corresponde al detalle.");
                procesarFicheroNng.getlErrores().add(error);
            }

        } else {
            totalRentada = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error019");
            error.setDescripcion("El total de numeración rentada para el reporte cargado, no es numérico.");
            procesarFicheroNng.getlErrores().add(error);
        }

        col++;
        String totalNumActiva = listaDatos.get(1).get(col);

        if (isNumeric(totalNumActiva)) {
            if (Integer.parseInt(totalNumActiva) == totalcolumna(listaDatos, col + 1)) {
                if (lineaActiva.getSolicitudLineasActivas().getProveedorSolicitante() != null) {

                    lineaActiva.setTotalNumActiva(new BigDecimal(totalNumActiva));
                }

            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error020");
                error.setDescripcion("El total de numeración rentada para el reporte cargado "
                        + "a la suma de numeración activa en el detalle del reporte.");
                procesarFicheroNng.getlErrores().add(error);
            }

        } else {
            totalRentada = "0";
            error = new ErrorValidacion();
            error.setCodigo("Error020");
            error.setDescripcion("El total de numeración activa para el reporte cargado, no es numérico.");
            procesarFicheroNng.getlErrores().add(error);
        }

        // Procesamos el detalle del reporte de la Linea Activa

        List<String> listaDetalles = new ArrayList<String>();

        for (int fila = 3; fila < listaDatos.size(); fila++) {

            StringBuffer detalle = new StringBuffer();

            detalle = procesarDetalleNng(detalle, listaDatos.get(fila));

            int celda = 5;

            String numRentada = listaDatos.get(fila).get(celda++);
            detalle.append(":");
            detalle.append(numRentada);

            if (!isNumeric(numRentada)) {
                numRentada = "0";
                error = new ErrorValidacion();
                error.setCodigo("Error021");
                error.setDescripcion("La numeración rentada para el registro " + listaDatos.get(fila).get(0)
                        + " en el reporte cargado, no es numérica.");
                procesarFicheroNng.getlErrores().add(error);
            }

            String numActiva = listaDatos.get(fila).get(celda++);

            if (!isNumeric(numActiva)) {
                error = new ErrorValidacion();
                error.setCodigo("Error022");
                error.setDescripcion("La numeración activa para el registro " + listaDatos.get(fila).get(0)
                        + " en el reporte cargado, no es numérica.");
                procesarFicheroNng.getlErrores().add(error);
            }

            // Validamos el arrendador
            col++;
            String arrendador = listaDatos.get(fila).get(celda++);

            Proveedor pst = null;
            pst = proveedorDAO.getProveedorByNombre(arrendador);

            if (pst == null) {
                error = new ErrorValidacion();
                error.setCodigo("Error023");
                error.setDescripcion("El PST arrendador para el registro " + listaDatos.get(fila).get(0)
                        + " en el reporte cargado, no está registrado en el SNS.");
                procesarFicheroNng.getlErrores().add(error);
            } else {
                detalle.append(":");
                detalle.append(pst.getId().toString());
            }

            // Validamos el ABC/IDA
            col++;
            String strIdaAbc = listaDatos.get(fila).get(celda++);
            detalle.append(":");
            detalle.append(strIdaAbc);

            if (isNumeric(strIdaAbc)) {
                if (!proveedorDAO.existeIdaAbc(strIdaAbc)) {
                    error = new ErrorValidacion();
                    error.setCodigo("Error024");
                    error.setDescripcion("El IDA/ABC para el registro " + listaDatos.get(fila).get(0)
                            + " en el reporte cargado, no está registrado en el SNS.");
                    procesarFicheroNng.getlErrores().add(error);
                }
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error024");
                error.setDescripcion("El IDA/ABC para el registro " + listaDatos.get(fila).get(0)
                        + " en el reporte cargado no es numérico.");
                procesarFicheroNng.getlErrores().add(error);
            }
            if (pst != null) {
                if (!((pst.getAbc() != null && pst.getAbc().toString().equals(strIdaAbc)) || (pst.getIda() != null && pst
                        .getIda().toString().equals(strIdaAbc)))) {
                    error = new ErrorValidacion();
                    error.setCodigo("Error024");
                    error.setDescripcion("El IDA/ABC para el registro "
                            + listaDatos.get(fila).get(0)
                            + " en el reporte cargado, no está registrado"
                            + " en el PST arrendatario del registro en el SNS.");
                    procesarFicheroNng.getlErrores().add(error);
                }
            }

            detalle.append(":");
            detalle.append(numActiva);

            listaDetalles.add(detalle.toString());

        }

        procesarFicheroNng.setListaDatos(listaDetalles);
        return procesarFicheroNng;

    }

    @Override
    public RetornoProcesaFicheroReportesNng validarFicheroLineasArrendadaNng(File fichero) throws Exception {

        List<List<String>> listaDatos = extraerExcel(fichero);

        listaDatos = setearCamposVacios(listaDatos, 7);

        procesarFicheroNng = new RetornoProcesaFicheroReportesNng();

        ReporteLineaArrendadorNng lineaActiva = new ReporteLineaArrendadorNng();

        // Validamos la cabecera de lineas activas
        procesarFicheroNng = procesarCabeceraArrendadorNng(listaDatos, lineaActiva);

        lineaActiva = (ReporteLineaArrendadorNng) procesarFicheroNng.getReporte();

        // Registramos la fecha actual
        lineaActiva.setFechaReporte(FechasUtils.getFechaHoy("dd/MM/yyyy HH:mm:ss"));

        ErrorValidacion error = null;

        // Procesamos el detalle del reporte de la Linea Activa

        List<String> listaDetalles = new ArrayList<String>();

        for (int fila = 3; fila < listaDatos.size(); fila++) {

            StringBuffer detalle = new StringBuffer();

            detalle = procesarDetalleNng(detalle, listaDatos.get(fila));

            int celda = 5;

            // Validamos el arrendatario
            String arrendatario = listaDatos.get(fila).get(celda++);

            Proveedor pst = null;
            pst = proveedorDAO.getProveedorByNombre(arrendatario);

            if (pst == null) {
                error = new ErrorValidacion();
                error.setCodigo("Error017");
                error.setDescripcion("El PST arrendador para el registro " + listaDatos.get(fila).get(0)
                        + " en el reporte cargado, no está registrado en el SNS.");
                procesarFicheroNng.getlErrores().add(error);
            } else {
                detalle.append(":");
                detalle.append(pst.getId().toString());
            }

            // Validamos el ABC/IDA
            String strIdaAbc = listaDatos.get(fila).get(celda++);
            detalle.append(":");
            detalle.append(strIdaAbc);

            if (isNumeric(strIdaAbc)) {
                if (!proveedorDAO.existeIdaAbc(strIdaAbc)) {
                    error = new ErrorValidacion();
                    error.setCodigo("Error018");
                    error.setDescripcion("El IDA/ABC para el registro " + listaDatos.get(fila).get(0)
                            + " en el reporte cargado, no está registrado en el SNS.");
                    procesarFicheroNng.getlErrores().add(error);
                }
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error018");
                error.setDescripcion("El IDA/ABC para el registro " + listaDatos.get(fila).get(0)
                        + " en el reporte cargado no es numérico.");
                procesarFicheroNng.getlErrores().add(error);
            }

            if (pst != null) {
                if (!((pst.getAbc() != null && pst.getAbc().toString().equals(strIdaAbc)) || (pst.getIda() != null && pst
                        .getIda().toString().equals(strIdaAbc)))) {
                    error = new ErrorValidacion();
                    error.setCodigo("Error018");
                    error.setDescripcion("El IDA/ABC para el registro "
                            + listaDatos.get(fila).get(0)
                            + " en el reporte cargado, no está registrado "
                            + "en el PST arrendatario del registro en el SNS.");
                    procesarFicheroNng.getlErrores().add(error);
                }
            }

            listaDetalles.add(detalle.toString());

        }

        procesarFicheroNng.setListaDatos(listaDetalles);
        return procesarFicheroNng;

    }

    /**
     * Procesa la cabecera de un reporte de arrendador.
     * @param listaDatos a procesar
     * @param reporte a procesar
     * @return resultado del procesamiento
     * @throws ParseException en caso de error de parseo.
     */
    private RetornoProcesaFicheroReportesNng procesarCabeceraArrendadorNng(List<List<String>> listaDatos,
            ReporteLineaArrendadorNng reporte) throws ParseException {

        reporte.setSolicitudLineasActivas(new SolicitudLineasActivasNng());
        EstadoSolicitud estadoSol = new EstadoSolicitud();
        estadoSol.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
        reporte.getSolicitudLineasActivas().setEstadoSolicitud(estadoSol);

        RetornoProcesaFicheroReportesNng procesarFichero = new RetornoProcesaFicheroReportesNng();

        procesarFichero.setlErrores(new ArrayList<ErrorValidacion>());

        ErrorValidacion error = null;

        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");

        Date fechaActual = FechasUtils.getFechaHoy("dd/MM/yyyy");

        String nombrePst = listaDatos.get(1).get(0);

        // Comprobamos si el proveedor existe

        procesarFichero.setNombreProveedor(nombrePst);
        Proveedor pst = proveedorDAO.getProveedorByNombre(nombrePst);

        if (pst == null) {
            procesarFichero.setProveedorRegistrado(false);
        } else {
            procesarFichero.setProveedorRegistrado(true);
            reporte.getSolicitudLineasActivas().setProveedorSolicitante(pst);
        }

        // Validamos la fecha de reporte
        String strFechaReporte = listaDatos.get(1).get(1);

        if (isFechaValida(strFechaReporte)) {

            Date fechaReporte = new Date();

            fechaReporte = formateador.parse(strFechaReporte);
            if (!fechaReporte.after(fechaActual)) {
                reporte.getSolicitudLineasActivas().setFechaSolicitud(fechaReporte);
            } else {
                error = new ErrorValidacion();
                error.setCodigo("Error001");
                error.setDescripcion("La fecha del reporte es mayor a la fecha actual.");
                procesarFichero.getlErrores().add(error);
            }
        } else {
            error = new ErrorValidacion();
            error.setCodigo("Error001");
            error.setDescripcion("La fecha del reporte no cumple el formato definido.");
            procesarFichero.getlErrores().add(error);
        }

        procesarFichero.setReporte(reporte);
        return procesarFichero;
    }

    /**
     * Devuelve si una cadena es numerico.
     * @param dato String
     * @return boolean
     */
    private boolean isNumeric(String dato) {
        if (StringUtils.isEmpty(dato)) {
            return false;
        } else {
            return StringUtils.isNumeric(dato);
        }
    }
}
