///A Simple Web Server (WebServer.java)

package server;

import java.io.BufferedReader;
import java.io.File;
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
			case "POST":
				fetchFile(remote, hi);
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
	        out.print(HttpInterpreter.getHeader200(hi.getFile().split("\\.")[1].equals("html")));
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

  protected void putFile(Socket socket, HttpInterpreter hi)
  {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			File f = new File("."+hi.getFile());
			
			if(f.exists())
			{
				out.write(HttpInterpreter.getHeader201(hi.getFile()));
				System.out.println("PUT: 201");
			}
			else
			{
				out.write(HttpInterpreter.getHeader204());
				System.out.println("PUT: 204");
			}
			
			FileWriter fw = new FileWriter(f);
			fw.write(hi.getBody());
			fw.flush();
			fw.close();
			out.flush();
			out.close();
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
  }

  protected void fetchFile(Socket socket, HttpInterpreter hi)
  {
	  try
	  {
		PrintWriter out = new PrintWriter(socket.getOutputStream());
        String str = "."+hi.getFile();
        File f = new File(str);
        if(!f.exists())
        { 
        	// Send the response
	        // Send the headers
        	out.print(HttpInterpreter.getHeader404());
        	System.out.println("GET/POST: 404");
        }
        else
        {
        	// Send the response
	        // Send the headers
	        out.print(HttpInterpreter.getHeader200(hi.getFile().split("\\.")[1].equals("html")));
	        System.out.println("GET/POST: 200");
	        // Send the HTML page
	        
	        BufferedReader fileBufferedReader = new BufferedReader(new FileReader(f));
	        String buf = fileBufferedReader.readLine();
	        while(buf!=null)
	        {
	        	out.println(buf);
	        	buf=fileBufferedReader.readLine();
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
