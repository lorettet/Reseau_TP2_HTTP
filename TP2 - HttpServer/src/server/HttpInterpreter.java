package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Permet d'interpr�ter les requetes HTTP
 * Fournis aussi les headers de r�ponse
 * @author tlorettefr
 *
 */
public class HttpInterpreter {

	private String requestType = "";
	private String httpVersion = "";
	private String file = "";
	private String body;
	private HashMap<String,String> head;
	private HashMap<String,String> postParam;
	private HashMap<String, String> getParam;
	
	public HttpInterpreter(Socket socket) {
		// TODO Auto-generated constructor stub
		head = new HashMap<>();
		postParam = new HashMap<>();
		getParam = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String str;
			//On lit la premi�re ligne de la requ�te et on s�pare les informations
			str = br.readLine();
			if(str==null)
				return;
			String[] split = str.split(" ");
			requestType = split[0];
			//On lit les param�tre GET
			if(split[1].contains("?"))
			{
				file = split[1].split("\\?")[0];
				String[] params = split[1].split("\\?")[1].split("&");
				for(String oneStr : params)
				{
					String key = oneStr.split("=")[0];
					String value = oneStr.split("=")[1];
					getParam.put(key, value);
				}
			}
			else
				file = split[1];
			httpVersion = split[2];
			
			 // On lit le HEAD
			str = br.readLine();
			while(!str.equals(""))
			{
				split = str.split(":");
				String key = split[0];
				String value = split[1].substring(1, split[1].length());
				head.put(key, value);
				str = br.readLine();
			}
			if(!br.ready()) // pr�sence d'un BODY?
				return;
			body="";
			while(br.ready()) // on r�cup�re le BODY
			{
				body += String.valueOf((char)br.read());
			}
			if((this.requestType.equals("GET")) || (this.requestType.equals("POST"))) //si le pody pr�sente des param�tre, on les extraits
			{
				split = body.split("&");
				for(String param : split)
				{
					postParam.put(param.split("=")[0], param.split("=")[1]);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
}
	}
	
	public String getBody() {
		return body;
	}

	public String getRequestType() {
		return requestType;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public String getFile() {
		return file;
	}
	
	/**
	 * Renvoi le param�tre du header demand� ou null si il n'existe pas
	 * @param param le nom du param�tre demand�
	 * @return la valeur du param�tre ou null s'il n'existe pas
	 */
	public String getHeadParam(String param)
	{
		return head.get(param);
	}

	/**
	 * Renvoi le param�tre GET demand� ou null si il n'existe pas
	 * @param param le nom du param�tre demand�
	 * @return la valeur du param�tre ou null s'il n'existe pas
	 */
	public String getGetParamter(String param)
	{
		if(!requestType.equals("GET"))
			return null;
		return getParam.get(param);
			
	}
	
	/**
	 * Renvoi le param�tre POST demand� ou null si il n'existe pas
	 * @param param le nom du param�tre demand�
	 * @return la valeur du param�tre ou null s'il n'existe pas
	 */
	public String getPostParamter(String param)
	{
		if(!requestType.equals("POST"))
			return null;
		return postParam.get(param);
			
	}
	
	/**
	 * Renvoie une chaine de caract�re contenant les param�tres de base d'un code 200
	 * @param contentType le type de fichier renvoy�
	 * @return la chaine format�e
	 */
	public static String getHeader200(String contentType)
	{
		StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.0 200 OK\n");
        sb.append("Content-Type: "+contentType+"\n");
        sb.append("Server: Bot\n");
        sb.append("\n");
        return sb.toString();
	}
	
	/**
	 * Renvoie une chaine de caract�re contenant les param�tres de base d'un code 404
	 * @return la chaine format�e
	 */
	public static String getHeader404()
	{
		StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.0 404 NOT_FOUND\n");
        sb.append("Content-Type: text/html\n");
        sb.append("Server: Bot\n");
        sb.append("\n");
        return sb.toString();
	}
	
	/**
	 * Renvoie une chaine de caract�re contenant les param�tres de base d'un code 200
	 * @param location le chemin o� a �t� enregistr� le fichier.
	 * @return la chaine format�e
	 */
	public static String getHeader201(String location)
	{
		StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.0 201 CREATED\n");
        sb.append("Content-Location: "+location+"\n");
        sb.append("Server: Bot\n");
        sb.append("\n");
        return sb.toString();
	}	
	
	/**
	 * Renvoie une chaine de caract�re contenant les param�tres de base d'un code 200
	 * @return la chaine format�e
	 */
	public static String getHeader204()
	{
		StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.0 204 CREATED\n");
        sb.append("Server: Bot\n");
        sb.append("\n");
        return sb.toString();
	}
	
	public String toString()
	{
		return (this.requestType + " " + this.file + " " + this.httpVersion);
	}

}
