package mx.ift.sns.negocio.port;

import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileNotFoundException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conexion SFTP para descarga de ficheros.
 */
public class ConexionSFTP {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConexionSFTP.class);

    /**
     * Crea la cadena de conexion.
     * @param hostName host
     * @param port puerto
     * @param username usuario
     * @param password clave
     * @param remoteFilePath fichero en el servidor
     * @return cadena de conexion.
     */
    public String createConnectionString(String hostName, String port, String username, String password,
            String remoteFilePath) {
        // ejemplo result: "sftp://user:123456@domainname.com/resume.pdf

        StringBuilder b = new StringBuilder();
        b.append("sftp://");
        b.append(username);
        b.append(":");
        b.append(password);
        b.append("@");
        b.append(hostName);
        b.append(":");
        b.append(port);
        b.append(remoteFilePath);

        String cadena = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}", b.toString());
        }

        return cadena;
    }

    /**
     * @return opciones de ftp
     * @throws FileSystemException error
     */
    public FileSystemOptions createDefaultOptions() throws FileSystemException {
        // Create SFTP options
        FileSystemOptions opts = new FileSystemOptions();

        // SSH Key checking
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

        // Root directory set to user home
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

        // Timeout is count by Milliseconds
        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 20 * 1000);

        return opts;
    }

    /**
     * Descarga el fichero.
     * @param hostName host
     * @param port puerto
     * @param username usuario
     * @param password clave
     * @param localFilePath fichero local
     * @param remoteFilePath fichero remoto
     * @throws FileSystemException error
     */
    public void download(String hostName, String port, String username, String password, String localFilePath,
            String remoteFilePath) throws FileSystemException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("download {} a {}", remoteFilePath, localFilePath);
        }
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            // Create local file object
            FileObject localFile = manager.resolveFile(localFilePath);

            // Create remote file object
            FileObject remoteFile = manager.resolveFile(
                    createConnectionString(hostName, port, username, password, remoteFilePath),
                    createDefaultOptions());

            boolean existe = remoteFile.exists();

            if (existe == false) {
                throw new FileNotFoundException(remoteFile);
            }

            // Copy local file to sftp server
            localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);

            LOGGER.debug("File {} download success",remoteFilePath);
        } catch (FileNotFoundException e) {
            LOGGER.debug("no se encuentra el fichero");
            throw e;
        } catch (FileNotFolderException e) {
            LOGGER.debug("no se encuentra el folder");
            throw e;
        } catch (FileSystemException e) {
            throw e;
        } finally {
            manager.close();
        }
    }
}
