package mx.ift.sns.dao.usu;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.modelo.usu.Rol;
import mx.ift.sns.modelo.usu.Usuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion de UsuarioDAO.
 */
@Named
public class UsuarioDAOImpl extends BaseDAO<Usuario> implements IUsuarioDAO {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioDAOImpl.class);

    @Override
    public Usuario findUsuario(String uid) {
        try {
            LOGGER.debug("Buscando Usuario {}", uid);
            final String query = "SELECT u FROM Usuario u WHERE FUNCTION('LOWER', u.userid) = :uid";

            TypedQuery<Usuario> tQuery = this.getEntityManager().createQuery(query, Usuario.class);
            tQuery.setParameter("uid", uid.trim().toLowerCase());

            Usuario u = tQuery.getSingleResult();
            LOGGER.debug("Usuario Encontrado: {}", u);
            return u;

        } catch (NoResultException e) {
            LOGGER.error("Usuario {} no encontrado: {}", uid, e.getMessage());
            return null;
        }catch (Exception e) {
        	LOGGER.error("Error no controlado", e.getMessage());
        	throw e;
		}
    }

    @Override
    public Usuario findUsuarioById(String idUsuario) {
        try {
            LOGGER.debug("Buscando Usuario {}", idUsuario);
            final String query = "SELECT u FROM Usuario u WHERE u.id = :idUsuario";

            TypedQuery<Usuario> tQuery = this.getEntityManager().createQuery(query, Usuario.class);
            BigDecimal idUsuarioAux = new BigDecimal(idUsuario);
            tQuery.setParameter("idUsuario", idUsuarioAux);

            Usuario u = tQuery.getSingleResult();
            LOGGER.debug("Usuario Encontrado: {}", u);
            return u;

        } catch (Exception e) {
            LOGGER.error("Usuario {} no encontrado: {}", idUsuario, e.getMessage());
            throw e;
        }
    }

    @Override
    public Long existeUsuario(String idUsuario) {
        try {
            final String query = "SELECT count(u.id) FROM Usuario u where upper(u.userid) = upper(:idUsuario)";
            Query q = getEntityManager().createQuery(query).setParameter("idUsuario", idUsuario);
            Long numUsuarios = (Long) q.getSingleResult();
            return numUsuarios;
        } catch (NoResultException nre) {
            return 0L;
        }
    }

    @Override
    public List<Usuario> findAllUsuarios() {
        String sql = "SELECT u FROM Usuario u order by u.userid";
        TypedQuery<Usuario> q = getEntityManager().createQuery(sql, Usuario.class);
        return q.getResultList();
    }

    @Override
    public List<Usuario> findAllAnalistas() {
        final String sql = "SELECT u FROM Usuario u, Rol r where r.nombre = :rol order by u.userid";
        TypedQuery<Usuario> q = getEntityManager().createQuery(sql, Usuario.class);
        q.setParameter("rol", Rol.ANALISTA);
        return q.getResultList();
    }

    @Override
    public boolean existeEmailUsuario(String idUsuario, String email) {

        boolean result = false;
        StringBuffer sbQuery = new StringBuffer("SELECT u FROM Usuario u WHERE ");
        sbQuery.append("upper(u.userid) = upper(:idUsuario)").append(" AND ");
        sbQuery.append("u.email = :email");

        TypedQuery<Usuario> query = getEntityManager().createQuery(sbQuery.toString(), Usuario.class);
        query.setParameter("idUsuario", idUsuario);
        query.setParameter("email", email);

        Usuario usuario = null;
        try {
            usuario = query.getSingleResult();
            if (usuario != null) {
                result = true;
            } else {
                result = false;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Usuario {} encontrado validado", idUsuario);
            }
        } catch (NoResultException nre) {
            LOGGER.error("Usuario {} no encontrado. Email {}", idUsuario, email);
        }

        return result;
    }
}
