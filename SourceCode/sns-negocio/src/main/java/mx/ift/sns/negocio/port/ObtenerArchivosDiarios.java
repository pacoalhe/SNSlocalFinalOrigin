package mx.ift.sns.negocio.port;

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
	
	
	
	public void getArchidosDiarios(){
	
		
		ExecutorService executor = Executors.newFixedThreadPool(2);		
		
		Runnable worker1=new RunnableSFTPConection(connectionParams, this.archivoPortados, this.remoteFilePortedPath);
		executor.execute(worker1);
					
		
		Runnable worker2=new RunnableSFTPConection(connectionParams, this.archivoCancelados, this.remoteFileDeletedPath);
		executor.execute(worker2);

		executor.shutdown();
        while (!executor.isTerminated()) {
        }
        LOGGER.info("Finished all threads SFTP downloads");
		
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
