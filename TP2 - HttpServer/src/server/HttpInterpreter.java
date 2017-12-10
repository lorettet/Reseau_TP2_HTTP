package server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class HttpInterpreter {

	private String requestType;
	private String httpVersion;
	private String file;
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
			str = br.readLine();
			String[] split = str.split(" ");
			requestType = split[0];
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
			
			str = br.readLine();
			while(!str.equals("")) // On lit le HEAD
			{
				split = str.split(":");
				String key = split[0];
				String value = split[1].substring(1, split[1].length());
				head.put(key, value);
				str = br.readLine();
			}
			if(!br.ready()) // présence d'un BODY?
				return;
			body="";
			while(br.ready()) // on récupère le BODY
			{
				body += String.valueOf((char)br.read());
			}
			if((this.requestType.equals("GET")) || (this.requestType.equals("POST"))) //si le pody présente des paramètre, on les extraits
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

	public String getGetParamter(String param)
	{
		if(!requestType.equals("GET"))
			return null;
		return getParam.get(param);
			
	}
	
	public String getPostParamter(String param)
	{
		if(!requestType.equals("POST"))
			return null;
		return postParam.get(param);
			
	}
	
	public static String getHeader200(boolean htmlContent)
	{
		StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.0 200 OK\n");
        if(htmlContent)
        	sb.append("Content-Type: text/html\n");
        sb.append("Server: Bot\n");
        sb.append("\n");
        return sb.toString();
	}
	
	public static String getHeader404()
	{
		StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.0 404 NOT_FOUND\n");
        sb.append("Content-Type: text/html\n");
        sb.append("Server: Bot\n");
        sb.append("\n");
        return sb.toString();
	}
	
	public static String getHeader201(String location)
	{
		StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.0 201 CREATED\n");
        sb.append("Content-Location: "+location+"\n");
        sb.append("Server: Bot\n");
        sb.append("\n");
        return sb.toString();
	}	
	
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
