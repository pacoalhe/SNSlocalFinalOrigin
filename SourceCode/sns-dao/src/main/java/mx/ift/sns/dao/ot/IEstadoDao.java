package mx.ift.sns.dao.ot;

import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoArea;

/**
 * Interfaz de definición de los métodos para base de datos de estatus de estado (OT).
 */
public interface IEstadoDao extends IBaseDAO<Estado> {

    /**
     * Comprueba que un codigo de estado existe.
     * @param estado estado
     * @return boolean
     */
    Boolean existsEstadoByCodigo(String estado);

    /**
     * Devuelve estado por id.
     * @param estado id
     * @return estado
     */
    Estado getEstadoByCodigo(String estado);

    /**
     * Devuelve lista de estados.
     * @return estados
     */
    List<Estado> findEstados();

    /**
     * Obtiene un Estado por su nombre.
     * @param name del estado
     * @return Estado
     */
    Estado getEstadoByName(String name);

    /**
     * Recupera todos los estados paginados.
     * @param first primer registro
     * @param pagesize numero de registros
     * @return estados listado
     * @exception Exception exception
     */
    List<Estado> findAllEstados(int first, int pagesize) throws Exception;

    /**
     * Método para guardar el Estado.
     * @param estado estado
     * @return Estado Estado saveEstado(Estado estado);
     */

    /**
     * Recupera el número de solicitudes de asignación que cumplen los filtros dados.
     * @return número de estados
     */
    int findAllEstadosCount();

    /**
     * Busca las áreas de un estado.
     * @param estado Estado
     * @return  List<EstadoArea>
     */
    List<EstadoArea> findAllAreaEstadoByEstado(Estado estado);

}
