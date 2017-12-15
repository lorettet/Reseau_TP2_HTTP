package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class WebPing {
  public static void main(String[] args) {
  
      if (args.length != 2) {
      	System.err.println("Usage java WebPing <server host name> <server port number>");
      	return;
      }	
  
   String httpServerHost = args[0];
    int httpServerPort = Integer.parseInt(args[1]);
     httpServerHost = args[0];
      httpServerPort = Integer.parseInt(args[1]);

    try {
      InetAddress addr;      
      Socket sock = new Socket(httpServerHost, httpServerPort);
      addr = sock.getInetAddress();
      System.out.println("Connected to " + addr);
      File f = new File("./lapin.jpg");
      BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
      out.write("PUT /new.txt HTTP/1.0\r\n".getBytes());
      out.write(("Content-length: "+f.length()+"\r\n").getBytes());
      out.write("\r\n".getBytes());
      int buf;
      while((buf = in.read())!=-1)
      {
    	  out.write((char)buf);
      }
      in.close();
      out.flush();
      out.close();
      sock.close();
    } catch (java.io.IOException e) {
      e.printStackTrace();
    }
  }
}