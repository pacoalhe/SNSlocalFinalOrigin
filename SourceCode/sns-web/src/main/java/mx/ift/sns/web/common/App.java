package mx.ift.sns.web.common;

import java.io.Serializable;

import javax.faces.application.Application;
import javax.faces.application.ProjectStage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import mx.ift.sns.utils.Version;
import mx.ift.sns.utils.WeblogicNode;

/**
 * Contexto de Aplicacion.
 */
@ManagedBean(name = "app", eager = true)
@ApplicationScoped
public class App implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Devuelve la version.
     * @return version
     */
    public String getVersion() {
        return Version.getVersion();
    }

    /**
     * @return projectstage
     */
    public ProjectStage getProjectStage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        Application application = facesContext.getApplication();

        ProjectStage p = application.getProjectStage();

        return p;
    }

    /**
     * @return projectstage name
     */
    public String getProjectStageName() {

        ProjectStage p = getProjectStage();

        return p.name();
    }

    /**
     * @return combinacion de projectstage
     */
    public String getTokens() {
        StringBuilder b = new StringBuilder();

        b.append(getVersion());

        if (!getProjectStage().equals(ProjectStage.Production)) {
            b.append(" ");
            b.append(getProjectStageName());
        }

        // TODO esto va dentro del if, lo pongo aqui para probar
        b.append(" ");
        b.append(WeblogicNode.getName());

        return b.toString();
    }

    /**
     * @return true si esta en modo desarrollo
     */
    public boolean isDevMode() {
        ProjectStage p = getProjectStage();
        return p.equals(ProjectStage.Development);
    }
}
