
package mx.ift.sns.negocio.port;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObtenerArchivosDiarios {
	
	/** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ObtenerArchivosDiarios.class);
	
	private String archivoPortados;
	private String archivoCancelados;
	private HashMap<String, String> connectionParams;
	private String remoteFilePortedPath;
	private String remoteFileDeletedPath;
	
	
	public ObtenerArchivosDiarios(String archivoPorta, String archivoCance,HashMap<String, String> params,String remoteFilePortedPath, String remoteFileDeletedPath){
		this.archivoPortados=archivoPorta;
		this.archivoCancelados=archivoCance;
		this.connectionParams=params;
		this.remoteFilePortedPath=remoteFilePortedPath;
		this.remoteFileDeletedPath=remoteFileDeletedPath;
				
	}



	public void getArchidosDiarios() {

		ExecutorService executor = Executors.newFixedThreadPool(2);

		// === PORTADOS ===
		if (remoteFilePortedPath != null && remoteFilePortedPath.startsWith("file:/")) {
			try {
				String localPath = remoteFilePortedPath.replace("file:/", "");
				File origen = new File(localPath);

				if (!origen.exists()) {
					LOGGER.warn("[LocalMode] Archivo PORTADOS no encontrado en {}", origen.getAbsolutePath());
					// borrar temporal para que se detecte como inexistente
					File tmp = new File(archivoPortados);
					if (tmp.exists()) {
						tmp.delete();
					}
					return; // devolvemos sin romper el flujo
				}

				Files.copy(origen.toPath(), new File(archivoPortados).toPath(), StandardCopyOption.REPLACE_EXISTING);
				LOGGER.info("[LocalMode] Archivo PORTADOS leído localmente desde {}", origen.getAbsolutePath());
			} catch (Exception e) {
				LOGGER.error("[LocalMode] Error leyendo archivo PORTADOS local", e);
			}
		} else {
			Runnable worker1 = new RunnableSFTPConection(connectionParams, this.archivoPortados, this.remoteFilePortedPath);
			executor.execute(worker1);
		}

		// === CANCELADOS ===
		if (remoteFileDeletedPath != null && remoteFileDeletedPath.startsWith("file:/")) {
			try {
				String localPath = remoteFileDeletedPath.replace("file:/", "");
				File origen = new File(localPath);

				if (!origen.exists()) {
					LOGGER.warn("[LocalMode] Archivo CANCELADOS no encontrado en {}", origen.getAbsolutePath());
					// borrar temporal para que se detecte como inexistente
					File tmp = new File(archivoCancelados);
					if (tmp.exists()) {
						tmp.delete();
					}
					return;
				}

				Files.copy(origen.toPath(), new File(archivoCancelados).toPath(), StandardCopyOption.REPLACE_EXISTING);
				LOGGER.info("[LocalMode] Archivo CANCELADOS leído localmente desde {}", origen.getAbsolutePath());
			} catch (Exception e) {
				LOGGER.error("[LocalMode] Error leyendo archivo CANCELADOS local", e);
			}
		} else {
			Runnable worker2 = new RunnableSFTPConection(connectionParams, this.archivoCancelados, this.remoteFileDeletedPath);
			executor.execute(worker2);
		}

		executor.shutdown();
		while (!executor.isTerminated()) {
			// espera que terminen los hilos SFTP
		}

		LOGGER.info("Finished all threads SFTP/Local downloads");
	}


	public String getArchivoPortados() {
		return archivoPortados;
	}

	public void setArchivoPortados(String archivoPortados) {
		this.archivoPortados = archivoPortados;
	}

	public String getArchivoCancelados() {
		return archivoCancelados;
	}

	public void setArchivoCancelados(String archivoCancelados) {
		this.archivoCancelados = archivoCancelados;
	}



	public HashMap<String, String> getConnectionParams() {
		return connectionParams;
	}



	public void setConnectionParams(HashMap<String, String> connectionParams) {
		this.connectionParams = connectionParams;
	}



	public String getRemoteFilePortedPath() {
		return remoteFilePortedPath;
	}



	public void setRemoteFilePortedPath(String remoteFilePortedPath) {
		this.remoteFilePortedPath = remoteFilePortedPath;
	}



	public String getRemoteFileDeletedPath() {
		return remoteFileDeletedPath;
	}



	public void setRemoteFileDeletedPath(String remoteFileDeletedPath) {
		this.remoteFileDeletedPath = remoteFileDeletedPath;
	}
	
	
	

}
