package mx.ift.sns.port.full;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Conversor de xml a sql para la importacion del full export del ABD.
 */
public class Convert {

    /** Logger de la clase. */
    private static final ConsoleLogger LOGGER = ConsoleLogger.getLogger(Convert.class);

    /** Path por defecto de ficheros XML. */
    private static final String PATH_XML = "/app/sns/temporales/archivos_portabilidad_total/";

    /** Path por defecto de ficheros SQL. */
    private static final String PATH_SQL = "/app/sns/temporales/archivos_portabilidad_total/sql/";

    /** Patron de ficheros xml. */
    private static final String PATTERN = "FullDownloadSync_[0-9]*.xml";

    /** Dir de xml. */
    private String dirXml;

    /** Dir de xml. */
    private String dirSql;

    /** Fichero a partir del cual vamos a tratar. */
    private int from = 0;

    /**
     * @param args argumentos <from> a partir de que numoer fichero convertimos
     */
    public static void main(String[] args) {

        System.out.println("\t");
        System.out.println("SNS Herramienta conversion ficheros full ABD FullDownloadSync_xx.xml");
        System.out.println("\t");
        System.out.println("\t\t<from> convierte los ficheros a partir del numero <from>");
        System.out.println("\t\t<dir xml> directorio donde se encuentran los xml");
        System.out.println("\t\t\tPor defecto: " + PATH_XML);
        System.out.println("\t\t<dir sql> directorio donde se generan los sql");
        System.out.println("\t\t\tPor defecto: " + PATH_SQL);
        System.out.println("\tUso:");
        System.out.println("\t\tjava -jar sns-full-convert.jar ");
        System.out.println("\t\tjava -jar sns-full-convert.jar <from>");
        System.out.println("\t\tjava -jar sns-full-convert.jar <dir xml> <dir sql>");
        System.out.println("\t\tjava -jar sns-full-convert.jar <dir xml> <dir sql> <from>");
        System.out.println("\t\t");

        Convert convert = new Convert();

        if ((args == null) || (args.length == 0)) {
            convert.setDirXml(PATH_XML);
            convert.setDirSql(PATH_SQL);
            convert.setFrom(0);
        } else if ((args.length == 1)) {
            convert.setDirXml(PATH_XML);
            convert.setDirSql(PATH_SQL);
            convert.setFrom(Integer.parseInt(args[0]));
        } else if ((args.length == 2)) {
            convert.setDirXml(args[0]);
            convert.setDirSql(args[1]);
            convert.setFrom(0);
        } else if ((args.length >= 3)) {
            convert.setDirXml(args[0]);
            convert.setDirSql(args[1]);
            convert.setFrom(Integer.parseInt(args[2]));
        }

        File sql = new File(convert.getDirSql());
        if (!sql.exists()) {
            sql.mkdir();
        }

        LOGGER.info("path xml=" + convert.getDirXml());
        LOGGER.info("path sql=" + convert.getDirSql());
        LOGGER.info("from=" + convert.getFrom());
        LOGGER.info("");

        convert.procesarDirectorio(new File(convert.getDirXml()));

        LOGGER.info("");
        LOGGER.info("fin");
    }

    /**
     * Procesa los xml.
     * @param folder directorio xml
     */
    public void procesarDirectorio(final File folder) {

        LOGGER.info("buscando ficheros...");
        LOGGER.info("");

        File[] files = folder.listFiles();
        if (files != null) {

            Pattern p = Pattern.compile(PATTERN);

            for (final File fileEntry : files) {
                if (fileEntry.isFile()) {

                    Matcher m = p.matcher(fileEntry.getName());
                    if (m.matches() && (ficheroValido(fileEntry.getName()))) {

                        String fileXml = fileEntry.getName();
                        String fileSql = fileXml.replace(".xml", ".sql");
                        convert(fileXml, fileSql);
                    }
                }
            }
        }
    }

    /**
     * Convierte un xml en sql para importar.
     * @param fileXml fichero xml
     * @param fileSql fichero sql
     */
    private void convert(String fileXml, String fileSql) {
        ResultadoParser res = new ResultadoParser();

        FullDownloadSyncXmlParser parser = new FullDownloadSyncXmlParser();
        parser.parse(dirXml + fileXml, new File(dirSql + fileSql), res);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fecha = sdf.format(res.getTimestamp());
        LOGGER.info(" " + fileXml + " -> " + fileSql
                + " mensajes=" + res.getMensajes()
                + " numeros=" + res.getNumeros() + " timestamp=" + fecha);

    }

    /**
     * Devuevle el numero de la cadena.
     * @param s cadena
     * @return numero
     */
    private int getNumero(String s) {

        int n = 0;
        if (s != null) {
            int i = s.indexOf("_");
            int j = s.indexOf(".");

            String nn = s.substring(i + 1, j);

            n = Integer.parseInt(nn);
        }

        return n;
    }

    /**
     * Comprueba si el fichero tiene un numero >= que el parametro from.
     * @param s cadena
     * @return true /false
     */
    private boolean ficheroValido(String s) {

        return getNumero(s) >= from;
    }

    /**
     * @return the dirXml
     */
    public String getDirXml() {
        return dirXml;
    }

    /**
     * @param dirXml the dirXml to set
     */
    public void setDirXml(String dirXml) {
        this.dirXml = dirXml;
    }

    /**
     * @return the dirSql
     */
    public String getDirSql() {
        return dirSql;
    }

    /**
     * @param dirSql the dirSql to set
     */
    public void setDirSql(String dirSql) {
        this.dirSql = dirSql;
    }

    /**
     * @return the from
     */
    public int getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(int from) {
        this.from = from;
    }
}
