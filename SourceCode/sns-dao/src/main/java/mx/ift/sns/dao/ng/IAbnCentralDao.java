package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.abn.AbnCentral;

/**
 * Interfaz de definición de los métodos para base de datos para centrales de áreas de numeración.
 */
public interface IAbnCentralDao extends IBaseDAO<AbnCentral> {

    /**
     * Recupera el catálogo de centrales de áreas de numeración.
     * @return List
     */
    List<AbnCentral> findAllCentralesAbn();

    /**
     * Recupera una central por código.
     * @param pCentralId BigDecimal
     * @return AbnCentral
     */
    AbnCentral getCentralAbnById(BigDecimal pCentralId);

    /**
     * Recupera el abnCentral a partir del abn y del nir.
     * @param numAbn numAbn
     * @param idCentral idCentral
     * @return AbnCentral
     */
    AbnCentral findCentralByAbnNir(long idCentral, long numAbn);

    /**
     * Guarda un AbnCentral.
     * @param abnCentral abnCentral
     * @return AbnCentral
     */
    AbnCentral saveAbnCentral(AbnCentral abnCentral);

    /**
     * Recupera el listado de Abn Centrales a partir del abn.
     * @param codigo codigo
     * @return List<AbnCentral>
     */
    List<AbnCentral> findAllAbnCentralesByAbn(BigDecimal codigo);

}
