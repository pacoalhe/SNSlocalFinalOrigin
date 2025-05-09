package mx.ift.sns.modelo.abn;

/** Clase auxiliar para actualización de Código de ABN en diferentes tablas. */
public class AbnCodeUpdater {

    /** Entidad a actualizar. */
    private String entidad;

    /** Path del código ABN en la entidad. */
    private String idAbnPath;

    /** Indentificador de la entidad en la query JPA. */
    private String entidadToken;

    /**
     * Constructor específico.
     * @param pEntidad Entidad a actualizar.
     * @param pIdAbnPath Path del código ABN en la entidad.
     */
    public AbnCodeUpdater(String pEntidad, String pIdAbnPath) {
        this.entidad = pEntidad;
        this.idAbnPath = pIdAbnPath;
        this.entidadToken = pEntidad.substring(0, 2).toLowerCase();
    }

    /**
     * Recupera la Query JPA de actualización de código de ABN.
     * @param pOldAbnParam Identificador parámetro del viejo ABN.
     * @param pNewAbnParam Identificador parámetro del nuevo ABN.
     * @return String con la query Update a ejecutar.
     */
    public String getUpdateQuery(String pOldAbnParam, String pNewAbnParam) {
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("UPDATE ").append(entidad).append(" ").append(entidadToken);
        sbQuery.append(" SET ").append(entidadToken).append(".").append(idAbnPath);
        sbQuery.append(" = :").append(pNewAbnParam);
        sbQuery.append(" WHERE ").append(entidadToken).append(".").append(idAbnPath);
        sbQuery.append(" = :").append(pOldAbnParam);
        return sbQuery.toString();
    }

    // GETTERS & SETTERS

    /**
     * Entidad a actualizar.
     * @return String
     */
    public String getEntidad() {
        return entidad;
    }

}
