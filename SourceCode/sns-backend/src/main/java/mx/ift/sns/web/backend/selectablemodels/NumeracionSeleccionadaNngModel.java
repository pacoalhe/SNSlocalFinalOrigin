package mx.ift.sns.web.backend.selectablemodels;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;

import org.primefaces.model.SelectableDataModel;

/**
 * Data Model de NumeracionSolicitada.
 * @author X36155QU
 */
public class NumeracionSeleccionadaNngModel extends ListDataModel<NumeracionSolicitadaNng> implements
        SelectableDataModel<NumeracionSolicitadaNng>, Serializable {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param data dato
     */
    public NumeracionSeleccionadaNngModel(List<NumeracionSolicitadaNng> data) {
        super(data);
    }

    @Override
    public Object getRowKey(NumeracionSolicitadaNng object) {
        String codArrendatario = "null";
        if (object.getArrendatario() != null) {
            codArrendatario = object.getArrendatario().getCdgPst().toString();
        }
        String codConcesionario = "null";
        if (object.getConcesionario() != null) {
            codConcesionario = object.getConcesionario().getCdgPst().toString();
        }

        String sna = "null";
        if (object.getSna() != null) {
            sna = object.getSna().toString();
        }

        String numIni = "null";
        if (object.getNumeroInicial() != null) {
            numIni = object.getNumeroInicial().toString();
        }

        String numFinal = "null";
        if (object.getNumeroFinal() != null) {
            numFinal = object.getNumeroFinal().toString();
        }

        String cliente = "null";
        if (object.getCliente() != null && !object.getCliente().equals("")) {
            cliente = object.getCliente();
        }

        if( object.getAbc() == null ) {
            return object.getClaveServicio().getCodigo().toString() + "-" + ((object.getBcd() != null) ? object.getBcd().toString() : object.getConcesionario().getBcd().toString()) + "-"
                    + codArrendatario + "-" + codConcesionario + "-" + object.getCantidadSolicitada() + "-" + sna + "-"
                    + numIni + "-" + numFinal + "-" + cliente;
        }else{
            return object.getClaveServicio().getCodigo().toString() + "-" + object.getAbc().toString() + "-"
                    + codArrendatario + "-" + codConcesionario + "-" + object.getCantidadSolicitada() + "-" + sna + "-"
                    + numIni + "-" + numFinal + "-" + cliente;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public NumeracionSolicitadaNng getRowData(String rowKey) {
        List<NumeracionSolicitadaNng> numsSolicitadas = (List<NumeracionSolicitadaNng>) getWrappedData();

        for (int i = 0; i < numsSolicitadas.size(); i++) {
            String[] clave = rowKey.split("-");
            if (numsSolicitadas.get(i).getClaveServicio().getCodigo().toString().equals(clave[0])
                    && (numsSolicitadas.get(i).getAbc() != null) ? numsSolicitadas.get(i).getAbc().toString().equals(clave[1]) :(
                        (numsSolicitadas.get(i).getBcd() == null) ? numsSolicitadas.get(i).getConcesionario().getBcd().toString().equals(clave[1])
                            : numsSolicitadas.get(i).getBcd().toString().equals(clave[1])
                    )
                    && numsSolicitadas.get(i).getCantidadSolicitada().toString().equals(clave[4])) {

                boolean encontradoArrendatario = false;
                if (numsSolicitadas.get(i).getArrendatario() == null && clave[2].equals("null")) {
                    encontradoArrendatario = true;
                } else if (numsSolicitadas.get(i).getArrendatario() != null) {
                    if (numsSolicitadas.get(i).getArrendatario().getCdgPst().toString().equals(clave[2])) {
                        encontradoArrendatario = true;
                    }
                }

                boolean encontradoConcesionario = false;
                if (numsSolicitadas.get(i).getConcesionario() == null && clave[3].equals("null")) {
                    encontradoConcesionario = true;
                } else if (numsSolicitadas.get(i).getConcesionario() != null) {
                    if (numsSolicitadas.get(i).getConcesionario().getCdgPst().toString().equals(clave[3])) {
                        encontradoConcesionario = true;
                    }
                }

                boolean encontradoSna = false;
                if (numsSolicitadas.get(i).getSna() == null && clave[5].equals("null")) {
                    encontradoSna = true;
                } else if (numsSolicitadas.get(i).getSna() != null) {
                    if (numsSolicitadas.get(i).getSna().toString().equals(clave[5])) {
                        encontradoSna = true;
                    }
                }

                boolean encontradoNumIni = false;
                if (numsSolicitadas.get(i).getNumeroInicial() == null && clave[6].equals("null")) {
                    encontradoNumIni = true;
                } else if (numsSolicitadas.get(i).getNumeroInicial() != null) {
                    if (numsSolicitadas.get(i).getNumeroInicial().toString().equals(clave[6])) {
                        encontradoNumIni = true;
                    }
                }

                boolean encontradoNumFinal = false;
                if (numsSolicitadas.get(i).getNumeroFinal() == null && clave[7].equals("null")) {
                    encontradoNumFinal = true;
                } else if (numsSolicitadas.get(i).getNumeroFinal() != null) {
                    if (numsSolicitadas.get(i).getNumeroFinal().toString().equals(clave[7])) {
                        encontradoNumFinal = true;
                    }
                }

                boolean encontradoCliente = false;
                if ((numsSolicitadas.get(i).getCliente() == null || numsSolicitadas.get(i).getCliente().equals(""))
                        && clave[8].equals("null")) {
                    encontradoCliente = true;
                } else if (numsSolicitadas.get(i).getCliente() != null) {
                    if (numsSolicitadas.get(i).getCliente().equals(clave[8])) {
                        encontradoCliente = true;
                    }
                }

                if (encontradoArrendatario && encontradoConcesionario && encontradoNumIni
                        && encontradoNumFinal && encontradoSna && encontradoCliente) {
                    return numsSolicitadas.get(i);
                }
            }

        }

        return null;
    }
}
