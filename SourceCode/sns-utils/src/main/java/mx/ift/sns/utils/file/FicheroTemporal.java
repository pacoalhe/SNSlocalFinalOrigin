package mx.ift.sns.utils.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creacion de nombres de fichero temporal.
 */
public final class FicheroTemporal {

    /** Prefijo. */
    private static final String PREFIJO = "sns-";

    /** Sufijo. */
    private static final String SUFIJO = ".tmp";

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FicheroTemporal.class);

    /**
     * Constructor privado.
     */
    private FicheroTemporal() {

    }

    /**
     * Crea un nombre de fichero temporal.
     * @param prefijo del fichero
     * @param sufijo del fichero
     * @return nombre
     * @throws IOException error
     */
    public static File getTmpFileName(String prefijo, String sufijo) throws IOException {
        File tmpFile = File.createTempFile(prefijo, sufijo, null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("tmp file {}", tmpFile.getAbsolutePath());
        }

        return tmpFile;
    }

    /**
     * Crea un nombre de fichero temporal.
     * @param sufijo del fichero
     * @return nombre
     * @throws IOException error
     */
    public static File getTmpFileName(String sufijo) throws IOException {
        return getTmpFileName(PREFIJO, sufijo);
    }

    /**
     * Crea un nombre de fichero temporal.
     * @return nombre del fichero
     * @throws IOException error
     */
    public static File getTmpFileName() throws IOException {
        return getTmpFileName(PREFIJO, SUFIJO);
    }

    /**
     * Copia el fichero en un temporal.
     * @param originalFile fichero original
     * @return el fichero temporal
     * @throws IOException error
     */
    public static File copyTmpFile(InputStream originalFile) throws IOException {

        OutputStream fileOutput = null;

        LOGGER.debug("copiando en temporal");
        File tmp = null;
        try {
            tmp = FicheroTemporal.getTmpFileName();
            fileOutput = new FileOutputStream(tmp.getCanonicalPath());
            IOUtils.copy(originalFile, fileOutput);

            return tmp;

        } catch (IOException e) {
            FileUtils.deleteQuietly(tmp);

            throw e;
        } finally {
            IOUtils.closeQuietly(fileOutput);
            IOUtils.closeQuietly(originalFile);
        }
    }

    /**
     * Borra el fichero temporal.
     * @param tmpFile fichero temporal
     */
    public static void delete(File tmpFile) {
        if (tmpFile != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("borrando {}", tmpFile.getAbsoluteFile());
            }

            FileUtils.deleteQuietly(tmpFile);
        }
    }
}
