package serveur_cmd;
import java.awt.Color;
import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;


public class Serveur{
	private ServerSocket ss ;
	private Socket s;
	private Thread t ;
	private int listenPort ;
	private ArrayList<Socket> outputStreams = new ArrayList<Socket>();
	
	public Serveur(int p) {
		this.listenPort=p;
		try {
			ss = new ServerSocket(listenPort);
			System.out.println("Server "+ss+" listening on "+p);
			this.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start(){
		while(true){
			try {
				s = ss.accept();
				new ServeurThread(this, s);
				System.out.println("Connection from : "+s);
				PrintWriter dout =new PrintWriter(s.getOutputStream());
				outputStreams.add(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}
	
	public void sendToAll(String m,Socket s){
		synchronized (outputStreams) {
			for(Socket soc : outputStreams){
				
				PrintWriter dout;
				if(!soc.equals(s)){
					try {
						dout = new PrintWriter(soc.getOutputStream());
						dout.println(m);
						dout.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
		}
	}

	public void ferme(Socket socket) {
		try {
			outputStreams.remove(socket);
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void echo(int i, Socket s) {
		BufferedReader din;
		String rslt = "";
		char[] buffer = new char[i];
		int octetlu=0;
		while(octetlu<i){
			try {
				din = new BufferedReader(new InputStreamReader(s.getInputStream()));
				octetlu += din.read(buffer);
				rslt += new String(buffer);
				System.out.println(octetlu);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sendToAll(rslt, s);
		sendAck(s,""+i);
	}


	public void ack(int i, Socket s) {
		int octlu=0;
		BufferedReader din;
		String rslt = "";
		while(octlu<i)
			try {
				din = new BufferedReader(new InputStreamReader(s.getInputStream()));
				rslt+=(char) din.read();
				octlu++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		sendAck(s,""+i);
	}

	
	private void sendAck(Socket s2,String m) {
		PrintWriter dout;
		try {
			dout = new PrintWriter(s.getOutputStream());
			dout.println("[OK "+m+" ]");
			dout.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
