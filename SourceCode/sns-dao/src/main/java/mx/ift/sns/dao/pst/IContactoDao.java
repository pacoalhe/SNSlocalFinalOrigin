package mx.ift.sns.dao.pst;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.pst.Contacto;

/**
 * Interfaz de definición de los métodos para base de datos de Contactos de Proveedor.
 */
public interface IContactoDao {

    /**
     * Recupera la lista de contactos de un proveedor por tipo de contacto.
     * @param pTipoContacto Tipo de contacto
     * @param pIdProveedor Identificador de Proveedor
     * @return List
     */
    List<Contacto> getRepresentantesLegales(String pTipoContacto, BigDecimal pIdProveedor);

    /**
     * Recupera un contacto de un proveedor por el identificador.
     * @param pIdContacto Identificador contacto.
     * @return Contacto
     */
    Contacto getRepresentanteLegalById(BigDecimal pIdContacto);

    /**
     * Función que comprueba si el contacto está siendo referenciado en algún trámite.
     * @param contacto Contacto
     * @return boolean usado
     */
    boolean contactoNoUsado(Contacto contacto);

    /**
     * @param id del contacto.
     * @return contacto asociado al id.
     */
    Contacto getContactoById(BigDecimal id);
}
