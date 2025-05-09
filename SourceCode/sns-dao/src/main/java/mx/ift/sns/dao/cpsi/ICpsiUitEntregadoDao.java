package mx.ift.sns.dao.cpsi;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.CpsiUitEntregado;

/**
 * @author T3500028
 */
public interface ICpsiUitEntregadoDao extends IBaseDAO<CodigoCPSI> {
    /**
     * Devuelve CPSIUITEntregado por su id de CPSI.
     * @param idCpsi idCPSI
     * @return CpsiUitEntregado CpsiUitEntregado
     * @throws Exception excepcion
     */
    CpsiUitEntregado getByIdCpsi(Integer idCpsi) throws Exception;

}
