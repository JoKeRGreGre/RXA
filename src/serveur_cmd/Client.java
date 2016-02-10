package serveur_cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public Socket s;
	public OutputStream ops;
	public InputStream ips;
	
	public Client(InetAddress inetAddress , int p) throws UnknownHostException {
		try {
			this.s= new Socket(inetAddress,p);
			ops = s.getOutputStream();
			ips = s.getInputStream();
		} catch (UnknownHostException e) {
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendoctet(int tr){

		for(int i=0;i<tr;i++) {
			try {
				ops.write((byte)'a');
				ops.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	private void lire(int tr) {
		byte[] b = new byte[tr];
		try {
			ips.read(b);
			for(int i=0;i<tr;i++) 
				System.out.println((char)b[i]);
		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.flush();
	}
	
	private void sendEcho(int tr) {
		PrintWriter dout = new PrintWriter(ops);
		dout.println("/echo "+tr);
		dout.flush();
		sendoctet(tr);
	}
	
	private void quitter() throws IOException {
		PrintWriter dout = new PrintWriter(ops);
		dout.println("/quit");
		dout.flush();
	}
	public void close(){
		try {
			this.ops.close();
			this.ips.close();
			System.out.println("fin connexion");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			int nboct =3000000;
			Client c = new Client(InetAddress.getLocalHost(), 1025);
			long beg = System.currentTimeMillis();
			c.sendEcho(nboct);
			c.lire(nboct);
			long end = System.currentTimeMillis();
			System.out.println( ((float)(end-beg))/1000f);
			c.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
