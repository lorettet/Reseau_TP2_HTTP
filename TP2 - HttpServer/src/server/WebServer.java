///A Simple Web Server (WebServer.java)

package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

	/**
	 * La page affiché par défault par le serveur
	 */
	private final static String DEFAULT_PAGE = "test.html";
	
  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 80");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
        // wait for a connection
        Socket remote;
		try {
			remote = s.accept();        
			//reading informations
	        HttpInterpreter hi = new HttpInterpreter(remote);
	        System.out.println(hi.toString());
	        switch (hi.getRequestType()) {
			case "GET":
				fetchGETFile(remote, hi);
				break;
			case "POST":
				fetchPOSTFile(remote, hi);
				break;
			case "PUT":
				putFile(remote, hi);
				break;
			case "HEAD":
				sendHead(remote, hi);
				break;
			case "DELETE":
				deleteFile(remote, hi);
				break;
			default:
				break;
			}
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
  }
  
  /**
   * DELETE : Supprime un fichier du serveur
   * @param socket le socket de connexion
   * @param hi la classe HttpInterpreter qui a décortiqué la requête
   */
  private void deleteFile(Socket socket, HttpInterpreter hi)
  {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			File f = new File("."+hi.getFile());
			
			if(f.exists())
			{
				out.write(HttpInterpreter.getHeader204());
				System.out.println("DELETE: 204");
				f.delete();
			}
			else
			{
				out.write(HttpInterpreter.getHeader404());
				System.out.println("DELETE: 404");
			}
			out.flush();
			out.close();
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
  }
  
  /**
   * HEAD : revoie uniquement le HEADER de réponse
   * @param socket le socket de connexion
   * @param hi la classe HttpInterpreter qui a décortiqué la requête
   */
  private void sendHead(Socket socket, HttpInterpreter hi)
  {
	  try
	  {
		PrintWriter out = new PrintWriter(socket.getOutputStream());
        String str = "."+hi.getFile();
        File f = new File(str);
        System.out.println(str);
        if(!f.exists())
        { 
        	// Send the response
	        // Send the headers
        	out.print(HttpInterpreter.getHeader404());
        	System.out.println("HEAD: 404");
        }
        else
        {
        	// Send the response
	        // Send the headers
        	String extension = hi.getFile().split("\\.")[1];
        	String contentType="";
        	if(extension.equals("html"))
        		contentType = "text/html";
        	else if(extension.equals("jpg"))
        		contentType="image/jpeg";
        	else if(extension.equals("mp3"))
        		contentType="audio/mpeg";
	        out.print(HttpInterpreter.getHeader200(contentType));
	        System.out.println("HEAD: 200");
	        // Send the HTML page
        }
        
        //Envoi des données
    	out.flush();
    	out.close();
    	socket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
	
  }

  /**
   * PUT : ajoute un fichier sur le serveur
   * @param socket le socket de connexion
   * @param hi la classe HttpInterpreter qui a décortiqué la requête
   */
  protected void putFile(Socket socket, HttpInterpreter hi)
  {
		try {
			BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
			File f = new File("."+hi.getFile());
			
			if(f.exists())
			{
				out.write(HttpInterpreter.getHeader201(hi.getFile()).getBytes());
				System.out.println("PUT: 201");
			}
			else
			{
				out.write(HttpInterpreter.getHeader204().getBytes());
				System.out.println("PUT: 204");
			}
			
			BufferedOutputStream outFile = new BufferedOutputStream(new FileOutputStream(f));
			outFile.write(hi.getBody().getBytes());
			outFile.flush();
			outFile.close();
			out.flush();
			out.close();
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
  }

  /**
   * GET : Renvoie le fichier demandé s'il existe
   * @param socket le socket de connexion
   * @param hi la classe HttpInterpreter qui a décortiqué la requête
   */
  protected void fetchGETFile(Socket socket, HttpInterpreter hi)
  {
	  try
	  {
		BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
		String str;
		if(hi.getFile().equals("/"))
			str = DEFAULT_PAGE;
		else
			str = "."+hi.getFile();
        File f = new File(str);
        if(!f.exists())
        { 
        	// Send the response
	        // Send the headers
        	out.write(HttpInterpreter.getHeader404().getBytes());
        	System.out.println("GET/POST: 404");
        }
        else
        {
        	// Send the response
	        // Send the headers
        	String extension = str.split("\\.")[2];
        	System.out.println(extension);
        	String contentType = "";
        	if(extension.equals("html"))
        		contentType = "text/html";
        	else if(extension.equals("jpg"))
        		contentType="image/jpeg";
        	else if(extension.equals("mp3"))
        		contentType="audio/mpeg";
        	System.out.println(contentType);
	        out.write(HttpInterpreter.getHeader200(contentType).getBytes());
	        System.out.println("GET/POST: 200");
	        // Send the HTML page
	        
	        BufferedInputStream fileBufferedReader = new BufferedInputStream(new FileInputStream(f));
	        int buf;
	        while((buf = fileBufferedReader.read())!=-1)
	        {
	        	out.write((char)buf);
	        }
	        fileBufferedReader.close();
        }
        
        //Envoi des données
    	out.flush();
    	out.close();
    	socket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  /**
   * POST : Renvoie le fichier demandé s'il existe suivi du body de la requête (Incompréhension de la consigne)
   * @param socket le socket de connexion
   * @param hi la classe HttpInterpreter qui a décortiqué la requête
   */
  protected void fetchPOSTFile(Socket socket, HttpInterpreter hi)
  {
	  try
	  {
		BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
		String str;
		if(hi.getFile().equals("/"))
			str = DEFAULT_PAGE;
		else
			str = "."+hi.getFile();
        File f = new File(str);
        if(!f.exists())
        { 
        	// Send the response
	        // Send the headers
        	out.write(HttpInterpreter.getHeader404().getBytes());
        	System.out.println("GET/POST: 404");
        }
        else
        {
        	// Send the response
	        // Send the headers
        	String extension = str.split("\\.")[2];
        	String contentType = "";
        	if(extension.equals("html"))
        		contentType = "text/html";
        	else if(extension.equals("jpg"))
        		contentType="image/jpeg";
        	else if(extension.equals("mp3"))
        		contentType="audio/mpeg";
	        out.write(HttpInterpreter.getHeader200(contentType).getBytes());
	        System.out.println("GET/POST: 200");
	        // Send the HTML page
	        
	        BufferedInputStream fileBufferedReader = new BufferedInputStream(new FileInputStream(f));
	        int buf;
	        while((buf = fileBufferedReader.read())!=-1)
	        {
	        	out.write(buf);
	        }
			out.write(hi.getBody().getBytes());
	        fileBufferedReader.close();
        }
        
        //Envoi des données
    	out.flush();
    	out.close();
    	socket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  
  /**
   * Start the application.
   * 
   * @param args
   * Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}
