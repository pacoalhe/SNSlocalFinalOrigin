package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.ng.NumeracionRedistribuida;

/**
 * Definición DAO para NumeracionRedistribuida.
 * @author X53490DE
 */
public interface INumeracionRedistribuidaDAO extends IBaseDAO<NumeracionRedistribuida> {

    /**
     * Recupera la lista de Numeraciones Redistribuidas asociadas a una Solicitud de Redistribución.
     * @param pIdRedistSol Identificador de la Solicitud de Redistribución.
     * @return List<NumeracionRedistribuida>
     */
    List<NumeracionRedistribuida> getNumeracionRedistribuidaById(BigDecimal pIdRedistSol);

}
