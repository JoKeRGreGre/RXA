package serveur_cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public Socket s;
	public OutputStream ops;
	public InputStream ips;
	public long nbOct;
	
	public Client(InetAddress inetAddress , int p) {
		try {
			this.nbOct = 10;
			this.s= new Socket(inetAddress,p);
			ops = s.getOutputStream();
			ips = s.getInputStream();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send1Moctet(){
		for(int i=0;i<nbOct;i++)
			try {
				ops.write("b\n".getBytes());
				ops.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void sendEcho() {
		try {
			ops.write(  ("/echo "+nbOct+"\n").getBytes());
			ops.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	private void quiter() {
		try {
			ops.write("quit\n".getBytes());
			ops.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void compute() {
		try {
			ops.write("/compute 5 * 9\n".getBytes());
			ops.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			Client c = new Client(InetAddress.getLocalHost(), 1025);
			c.sendEcho();
			c.compute();
			c.send1Moctet();
			c.quiter();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
