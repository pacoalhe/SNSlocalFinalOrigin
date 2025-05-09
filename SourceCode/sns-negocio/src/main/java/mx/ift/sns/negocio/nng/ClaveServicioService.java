package mx.ift.sns.negocio.nng;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.INirDao;
import mx.ift.sns.dao.nng.IClaveServicioDao;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.filtros.FiltroBusquedaClaveServicio;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.negocio.usu.IUsuariosService;

/**
 * Servicio para las Claves de Servicio.
 * @author X23016PE
 */
@Stateless(name = "ClaveServicioService", mappedName = "ClaveServicioService")
@Remote(IClaveServicioService.class)
public class ClaveServicioService implements IClaveServicioService {

    /**
     * Dao estatus.
     */
    @Inject
    private IClaveServicioDao claveServicioDao;

    /** Dao del Nir. */
    @Inject
    private INirDao nirDao;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    @Override
    public List<ClaveServicio> findAllClaveServicio(FiltroBusquedaClaveServicio pfiltros) throws Exception {
        return claveServicioDao.findAllClaveServicio(pfiltros);
    }

    @Override
    public int findAllClaveServicioCount(FiltroBusquedaClaveServicio pFiltros) throws Exception {
        return claveServicioDao.findAllClaveServicioCount(pFiltros);
    }

    @Override
    public ClaveServicio validaClaveServicio(ClaveServicio claveServicio, boolean modoEdicion) {
        claveServicio.setErrorValidacion(null);

        if (!modoEdicion) {
            if (claveServicioDao.getClaveServicioByCodigo(claveServicio.getCodigo()) != null) {
                claveServicio.setErrorValidacion("catalogo.clave.servicio.error.codigo");
                return claveServicio;
            } else if (nirDao.getNirByCodigo(claveServicio.getCodigo().intValue()) != null) {
                claveServicio.setErrorValidacion("catalogo.clave.servicio.error.nir");
                return claveServicio;
            }
        }

        return claveServicio;
    }

    @Override
    public ClaveServicio guardarClaveServicio(ClaveServicio claveServicio, boolean modoEdicion) {
        if (!modoEdicion) {
            Estatus estatus = new Estatus();
            estatus.setCdg(Estatus.ACTIVO);
            claveServicio.setEstatus(estatus);
        }
        // Se hace la Auditoría del registro
        claveServicio.updateAuditableValues(usuariosService.getCurrentUser());

        return claveServicioDao.saveOrUpdate(claveServicio);
    }

    @Override
    public List<ClaveServicio> findAllClaveServicio() {
        return claveServicioDao.findAllClaveServicio();
    }

    @Override
    public List<ClaveServicio> findAllClaveServicioActivas() {

        return claveServicioDao.findAllClaveServicioActivas();
    }

    @Override
    public ClaveServicio getClaveServicioByCodigo(BigDecimal codigo) {

        return claveServicioDao.getClaveServicioByCodigo(codigo);
    }

    @Override
    public List<ClaveServicio> findAllClaveServicioActivasWeb() {
        return claveServicioDao.findAllClaveServicioActivasWeb();
    }
}
