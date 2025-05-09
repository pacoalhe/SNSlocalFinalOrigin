package mx.ift.sns.modelo.port;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Tabla de Numeros Portados.
 */
@Entity
@Table(name = "PORT_NUM_PORTADO")
public class NumeroPortado extends Numero implements Serializable {

    /** Serial id. */
    private static final long serialVersionUID = 1L;

    /** Constructor. */
    public NumeroPortado() {
        super();
    }

    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();

        b.append("portid ");
        b.append(getPortId());

        b.append(" porttype ");
        b.append(getPortType());

        b.append(" action ");
        b.append(getAction());

        b.append(" actiondate ");
        DateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
        String date = formatter.format(getActionDate());
        b.append(date);

        b.append(" dcr ");
        b.append(getDcr());

        b.append(" dida ");
        b.append(getDida());

        b.append(" ismpp ");
        b.append(getIsMpp());

        b.append(" numberfrom ");
        b.append(getNumberFrom());

        b.append(" numberto ");
        b.append(getNumberTo());

        b.append(" porttype ");
        b.append(getPortType());

        b.append(" rcr ");
        b.append(getRcr());

        b.append(" rida ");
        b.append(getRida());

        return b.toString();
    }
}
