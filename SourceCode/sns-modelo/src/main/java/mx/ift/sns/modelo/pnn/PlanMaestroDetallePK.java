package mx.ift.sns.modelo.pnn;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Tipo de plan de numeracion.
 */
/**
 * The primary key class for the PNN_DETALLE database table.
 */
@Embeddable
public class PlanMaestroDetallePK implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    @Column(name = "NUMERO_INICIAL")
    private Long numeroInicial;

    @Column(name = "NUMERO_FINAL")
    private Long numeroFinal;

    /**
     * @return the numeroInicial
     */
    public Long getNumeroInicial() {
	return numeroInicial;
    }

    /**
     * @param numeroInicial the numeroInicial to set
     */
    public void setNumeroInicial(Long numeroInicial) {
	this.numeroInicial = numeroInicial;
    }

    /**
     * @return the numeroFinal
     */
    public Long getNumeroFinal() {
	return numeroFinal;
    }

    /**
     * @param numeroFinal the numeroFinal to set
     */
    public void setNumeroFinal(Long numeroFinal) {
	this.numeroFinal = numeroFinal;
    }

    @Override
    public String toString() {
	StringBuilder b = new StringBuilder();
	b.append("numeroInicial=");
	b.append(numeroInicial);
	b.append(" numeroFinal=");
	b.append(numeroFinal);

	return b.toString();
    }

    @Override
    public boolean equals(Object other) {
	if (this == other) {
	    return true;
	}
	if (!(other instanceof PlanMaestroDetallePK)) {
	    return false;
	}
	PlanMaestroDetallePK castOther = (PlanMaestroDetallePK) other;
	return this.numeroInicial.equals(castOther.numeroInicial) && this.numeroFinal.equals(castOther.numeroFinal);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int hash = 17;
	hash = hash * prime + this.numeroInicial.hashCode();
	hash = hash * prime + this.numeroFinal.hashCode();

	return hash;
    }
}
