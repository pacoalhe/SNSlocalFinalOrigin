package mx.ift.sns.dao.oficios;

import java.util.List;

import mx.ift.sns.modelo.oficios.TipoDestinatario;

/**
 * Interfaz de definición de los métodos para base de datos de Tipos de Destinatario.
 */
public interface ITipoDestinatarioDao {

    /**
     * Recupera el catálogo de tipos de destinatario.
     * @return List
     */
    List<TipoDestinatario> findAllTiposDestinatario();

    /**
     * Recupera un Tipo Destinatario por su código.
     * @param pCdgDestinatario Código de Tipo de Destinatario
     * @return TipoDestinatario
     */
    TipoDestinatario getTipoDestinatarioByCdg(String pCdgDestinatario);
}
