package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.series.Nir;

/**
 * Interfaz de definición de los métodos para base de datos para NIR.
 */
public interface INirDao extends IBaseDAO<Nir> {

    /**
     * Recupera un nir.
     * @param id id del nir
     * @return el nir
     */
    Nir getNir(BigDecimal id);

    /**
     * Recupera un nir por su codigo.
     * @param cdgNir codigo nir
     * @return el nir
     */
    Nir getNirByCodigo(int cdgNir);

    List<Nir> getNirByZona(int zona);

    /**
     * Devuelve la lista de nirs a partir del ABN.
     * @param numAbn numAbn
     * @return List<Nir>
     */
    List<Nir> findAllNirByAbn(BigDecimal numAbn);

    /**
     * Recupera la lista de NIRs cuyos primeros dígitos coinciden con el código dado.
     * @param pCodigoNir Parte del Código NIR
     * @return Lista de Nirs
     */
    List<Nir> findAllNirByParteCodigo(int pCodigoNir);

    /**
     * Devuelve el número de nirs asociado a un ABN.
     * @param numAbn numAbn
     * @return int
     */
    int findAllNirByAbnCount(BigDecimal numAbn);

    /**
     * Función que obtiene el listado de Nirs Activos.
     * @return List<Nir>
     * @throws Exception exception
     */
    List<Nir> findAllNirs() throws Exception;

    /**
     * Comprueba que un NIR existe en el SNS.
     * @param nir nir
     * @return boolean
     */
    boolean existsNir(String nir);

    boolean existsZona(String zona);

    /**
     * Comprueba que un NIR existe con un ABN dado.
     * @param abn abn
     * @param nir nir
     * @return boolean
     */
    boolean existsNirWithAbn(String abn, String nir);

    /**
     * Devuelve la lista de los nirs de un municipio.
     * @param municipio String
     * @param estado String
     * @return List<Nir>
     */
    List<Nir> finAllNirInMunicipio(String municipio, String estado);

    /**
     * Método que devuelve todos los Nirs de tres cifras que cuyas dos primeras cifras coinciden con el código pasado
     * por parámetros.
     * @param cdgNir cdgNir
     * @return List<Nir>
     */
    List<Nir> findNirsByDigitos(int cdgNir);

    /**
     * Método que devuelve los nirs asociados a una población.
     * @param poblacion Poblacion
     * @return List<Nir>
     */
    List<Nir> findAllNirByPoblacion(Poblacion poblacion);

    /**
     * Método que devuelve los nirs asociados a un estado.
     * @param estado Estado
     * @return List<Nir>
     */
    List<Nir> findAllNirByEstado(Estado estado);
}
