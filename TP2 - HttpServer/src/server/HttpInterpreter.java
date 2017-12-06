package server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class HttpInterpreter {

	private Socket socket;
	private String requeteType;
	private String httpVersion;
	private String file;
	private HashMap<String,String> head;
	private HashMap<String,String> body;
	private HashMap<String, String> getParam;
	
	public HttpInterpreter(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		head = new HashMap<>();
		body = new HashMap<>();
		getParam = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String str;
			str = br.readLine();
			String[] split = str.split(" ");
			requeteType = split[0];
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
			while(!str.equals("")) // tant qu'on est dans le HEAD
			{
				split = str.split(":");
				String key = split[0];
				String value = split[1].substring(1, split[1].length());
				head.put(key, value);
				str = br.readLine();
			}
			if(!br.ready())
			{
				br.close();
				return;
			}
			str="";
			while(br.ready())
			{
				str += String.valueOf((char)br.read());
			}
			split = str.split("&");
			for(String param : split)
			{
				body.put(param.split("=")[0], param.split("=")[1]);
			}
			
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public String getRequeteType() {
		return requeteType;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public String getFile() {
		return file;
	}

	public String getGetParamter(String param)
	{
		if(!requeteType.equals("GET"))
			return null;
		return getParam.get(param);
			
	}
	
	public String getPostParamter(String param)
	{
		if(!requeteType.equals("POST"))
			return null;
		return body.get(param);
			
	}

}
