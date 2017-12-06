///A Simple Web Server (WebServer.java)

package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.plaf.synth.SynthToggleButtonUI;

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
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        HttpInterpreter hi = new HttpInterpreter(remote);
        System.out.println(hi.getRequeteType());
        System.out.println(hi.getHttpVersion());
        System.out.println(hi.getFile());
        System.out.println(hi.getPostParamter("f"));
        String str = "."+hi.getFile();
        // Send the response
        // Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
        // Send the HTML page
        File f = new File(str);
        BufferedReader fileBufferedReader = new BufferedReader(new FileReader(f));
        String buf = fileBufferedReader.readLine();
        while(buf!=null)
        {
        	out.println(buf);
        	buf=fileBufferedReader.readLine();
        }
        fileBufferedReader.close();
        System.out.println("Envoi des données");
        out.flush();
        out.close();
        remote.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}
