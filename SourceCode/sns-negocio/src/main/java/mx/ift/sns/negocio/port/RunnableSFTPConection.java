package mx.ift.sns.negocio.port;

import java.util.HashMap;

import org.apache.commons.vfs2.FileSystemException;

public class RunnableSFTPConection implements Runnable {

	private Thread t;
	private HashMap<String, String> connectionParams;
	private String localFile;
	private String remoteFile;
	
	public RunnableSFTPConection(HashMap<String, String> params,String localfile,String remoteFile) 
	{
		this.connectionParams=params;
		this.localFile=localfile;
		this.remoteFile=remoteFile;
	}
	
	@Override
	public void run() {
		String host=this.connectionParams.get("host");
		String port=this.connectionParams.get("port"); 
		String user=this.connectionParams.get("user");
		String pwd=this.connectionParams.get("pwd");
		ConexionSFTP conn=new ConexionSFTP();
		try {
			conn.download(host, port, user, pwd, this.localFile, this.remoteFile);
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	public void start () {
	      
	      if (t == null) {
	         t = new Thread (this);
	         t.start ();
	      }
	   }

	public HashMap<String, String> getConnectionParams() {
		return connectionParams;
	}

	public void setConnectionParams(HashMap<String, String> connectionParams) {
		this.connectionParams = connectionParams;
	}

	

	public String getLocalFile() {
		return localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

	public String getRemoteFile() {
		return remoteFile;
	}

	public void setRemoteFile(String remoteFile) {
		this.remoteFile = remoteFile;
	}
	

}
