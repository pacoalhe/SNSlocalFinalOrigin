package mx.ift.sns.negocio;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.bitacora.IBitacoraDAO;
import mx.ift.sns.modelo.bitacora.Bitacora;
import mx.ift.sns.modelo.filtros.FiltroBusquedaBitacoraLog;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.negocio.usu.UsuariosService;
import mx.ift.sns.utils.WeblogicNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de Bitacora.
 */
@Stateless(name = "BitacoraService", mappedName = "BitacoraService")
@Remote(IBitacoraService.class)
public class BitacoraService implements IBitacoraService {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BitacoraService.class);

    /** DAO de bitacora. */
    @Inject
    private IBitacoraDAO bitacoraDAO;

    /** Servicio de usuarios. */
    @EJB
    private IUsuariosService usuariosService;

    /** Usuario SNS por defecto para Bitácora. */
    private Usuario userSns;

    /** Inicialización de la Bitácora. */
    @PostConstruct
    public void init() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("BITACORA {}: Init", WeblogicNode.getName());
        }

        // Cargamos el usuario SNS por defecto.
        System.out.println("Cargamos el usuario SNS por defecto.");
        userSns = usuariosService.findUsuario(UsuariosService.DEFAULT_USER);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("BITACORA {}: Default User: {}", WeblogicNode.getName(), userSns);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void add(String msg) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("BITACORA {}: {}", WeblogicNode.getName(), msg);
        }

        Bitacora b = new Bitacora();
        b.setUsuario(userSns);
        b.setFecha(new Date());
        b.setDescripcion(msg);
        bitacoraDAO.save(b);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void add(Solicitud sol, String msg) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("BITACORA {}: {}", WeblogicNode.getName(), msg);
        }

        Bitacora b = new Bitacora();
        // Usuario usu = usuariosService.findUsuario("sns");
        b.setUsuario(userSns);
        b.setFecha(new Date());
        b.setDescripcion(msg);
        bitacoraDAO.save(b);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void add(Usuario usuario, String msg) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("BITACORA {}: {} {}", WeblogicNode.getName(), usuario.getUserid(), msg);
        }

        Bitacora b = new Bitacora();
        b.setUsuario(usuario);
        b.setFecha(new Date());
        b.setDescripcion(msg);
        bitacoraDAO.save(b);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Bitacora> findBitacora(FiltroBusquedaBitacoraLog filtro) {
        return bitacoraDAO.findBitacora(filtro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public int findBitacoraCount(FiltroBusquedaBitacoraLog filtro) {
        return bitacoraDAO.findBitacoraCount(filtro);
    }
}
