package mx.ift.sns.dao.usu;

import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.usu.Usuario;

/**
 * Interfaz del DAO de Usuarios.
 */
public interface IUsuarioDAO extends IBaseDAO<Usuario> {

    /**
     * Busca al usuario con el uid.
     * @param uid a buscar
     * @return usuario
     */
    Usuario findUsuario(String uid);

    /**
     * Graba el usuario en BBDD.
     * @param usuario usuario a grabar
     * @return Usuario Usuario update(Usuario usuario);
     */

    /**
     * Busca al usuario con el uid.
     * @param idUsuario a buscar
     * @return Long existe
     */
    Long existeUsuario(String idUsuario);

    /**
     * Devuelve todos los usuarios.
     * @return lista de usuarios
     */
    List<Usuario> findAllUsuarios();

    /**
     * Devuelve todos los usuarios analistas.
     * @return lista de usuarios
     */
    List<Usuario> findAllAnalistas();

    /**
     * Comprueba si el email corresponde al usuario introducido.
     * @param idUsuario String
     * @param email String
     * @return boolean
     */
    boolean existeEmailUsuario(String idUsuario, String email);

    /**
     * Busca al usuario con el idUsuario.
     * @param idUsuario a buscar
     * @return usuario
     */
    Usuario findUsuarioById(String idUsuario);
}
