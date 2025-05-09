package mx.ift.sns.web.backend.selectablemodels;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import mx.ift.sns.modelo.ng.NumeracionSolicitada;

import org.primefaces.model.SelectableDataModel;

/**
 * Data Model de NumeracionSolicitada.
 * @author X36155QU
 */
public class NumeracionSeleccionadaNgModel extends ListDataModel<NumeracionSolicitada> implements
        SelectableDataModel<NumeracionSolicitada>, Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param data dato
     */
    public NumeracionSeleccionadaNgModel(List<NumeracionSolicitada> data) {
        super(data);
    }

    @Override
    public Object getRowKey(NumeracionSolicitada object) {
        String codArrendatario = "null";
        if (object.getArrendatario() != null) {
            codArrendatario = object.getArrendatario().getCdgPst().toString();
        }
        String codConcesionario = "null";
        if (object.getConcesionario() != null) {
            codConcesionario = object.getConcesionario().getCdgPst().toString();
        }
        String tipoModalidad = "null";
        if (object.getTipoModalidad() != null) {
            tipoModalidad = object.getTipoModalidad().getCdg();
        }

        return object.getTipoRed().getCdg() + "-" + object.getCantSolicitada().toString() + "-"
                + object.getPoblacion().getInegi() + "-" + object.getCentralOrigen().getId().toString() + "-"
                + object.getCentralDestino().getId().toString() + "-" + codArrendatario + "-" + codConcesionario
                + "-" + tipoModalidad;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NumeracionSolicitada getRowData(String rowKey) {
        List<NumeracionSolicitada> numsSolicitadas = (List<NumeracionSolicitada>) getWrappedData();

        for (int i = 0; i < numsSolicitadas.size(); i++) {
            String[] clave = rowKey.split("-");
            if (numsSolicitadas.get(i).getTipoRed().getCdg().equals(clave[0])
                    && numsSolicitadas.get(i).getCantSolicitada().toString().equals(clave[1])
                    && numsSolicitadas.get(i).getPoblacion().getInegi().equals(clave[2])
                    && numsSolicitadas.get(i).getCentralOrigen().getId().toString().equals(clave[3])
                    && numsSolicitadas.get(i).getCentralDestino().getId().toString().equals(clave[4])) {
                boolean encontradoArrendatario = false;
                if (numsSolicitadas.get(i).getArrendatario() == null && clave[5].equals("null")) {
                    encontradoArrendatario = true;
                } else if (numsSolicitadas.get(i).getArrendatario() != null) {
                    if (numsSolicitadas.get(i).getArrendatario().getCdgPst().toString().equals(clave[5])) {
                        encontradoArrendatario = true;
                    }
                }
                boolean encontradoConcesionario = false;
                if (numsSolicitadas.get(i).getConcesionario() == null && clave[6].equals("null")) {
                    encontradoConcesionario = true;
                } else if (numsSolicitadas.get(i).getConcesionario() != null) {
                    if (numsSolicitadas.get(i).getConcesionario().getCdgPst().toString().equals(clave[6])) {
                        encontradoConcesionario = true;
                    }
                }

                boolean encontradoModalidad = false;
                if (numsSolicitadas.get(i).getTipoModalidad() == null && clave[7].equals("null")) {
                    encontradoModalidad = true;
                } else if (numsSolicitadas.get(i).getTipoModalidad() != null) {
                    if (numsSolicitadas.get(i).getTipoModalidad().getCdg().equals(clave[7])) {
                        encontradoModalidad = true;
                    }
                }
                if (encontradoArrendatario && encontradoConcesionario && encontradoModalidad) {
                    return numsSolicitadas.get(i);
                }
            }

        }

        return null;
    }

}
