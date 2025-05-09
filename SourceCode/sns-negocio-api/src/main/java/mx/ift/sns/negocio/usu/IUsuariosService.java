package mx.ift.sns.negocio.usu;

import java.io.InputStream;
import java.util.List;

import mx.ift.sns.modelo.usu.Usuario;

/**
 * Interfaz del servicio de Usuarios.
 */
public interface IUsuariosService {

    /**
     * Busca al usuario con el uid.
     * @param uid a buscar
     * @return usuario
     */
    Usuario findUsuario(String uid);

    /**
     * El usuario crea una session.
     * @param usuario usuario
     * @return usuario modificado
     */
    Usuario login(Usuario usuario);

    /**
     * El usuario ha cerrado session.
     * @param usuario usuario
     */
    void logout(Usuario usuario);

    /**
     * Busca al usuario con el uid.
     * @param idUsuario a buscar
     * @return boolean existe
     */
    boolean existeUsuario(String idUsuario);

    /**
     * Función que valida si la contraseña recibida cumple los requisitos de seguridad.
     * @param password String
     * @return boolean valida
     */
    boolean validaContrasenna(String password);

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
     * Envía un email con la contraseña nueva recuperada y devuelve un int para la validación.
     * @param idUsuario String
     * @param email String
     * @param strem InputStream
     * @return int
     */
    int recuperaContrasena(String idUsuario, String email, InputStream strem);

    /**
     * Valida la clave de usuario del front.
     * @param clave String
     * @return boolean
     */
    boolean validaClave(String clave);

    /**
     * Crea una nueva clave si es el primer ingreso.
     * @param clave String
     * @param claveRep String
     * @param pUsuario Usuario
     * @return int
     */
    int crearNuevaClave(String clave, String claveRep, Usuario pUsuario);

    /**
     * Recupera el usuario logeado.
     * @return Usuario
     */
    Usuario getCurrentUser();

    /**
     * Método encargado de encriptar la contraseña recibida como parámetro.
     * @param pass a encriptar
     * @return password encriptada
     */
    String encriptaContrasena(String pass);

    /**
     * Busca al usuario con el idUsuario.
     * @param idUsuario a buscar
     * @return usuario
     */
    Usuario findUsuarioById(String idUsuario);

}
