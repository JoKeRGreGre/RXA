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

	public void sendBlococtet(long tr, int tailleBloc) {
		long nbTours = tr / tailleBloc;

		for (long j = 0; j < nbTours; j++) {
			byte[] buffer = new byte[tailleBloc];

			for (int i = 0; i < tailleBloc; i++)
				buffer[i] = (byte) 'a';

			try {
				ops.write(buffer);
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
			/*
			for(int i=0;i<tr;i++) 
				System.out.println((char)b[i]);
				*/
		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	private void lire(long tr,int tailleBloc) {
		
		
		long nbTours = tr/tailleBloc;
		byte[] b = new byte[tailleBloc];
		for(long j =0 ;j<nbTours;j++){
				try {
					ips.read(b);
					/*
					for(int i=0;i<tailleBloc;i++) 
						System.out.println((char)b[i]);
					*/	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	
	private void sendEcho(int tr) {
		PrintWriter dout = new PrintWriter(ops);
		dout.println("/echo "+tr);
		dout.flush();
		sendoctet(tr);
	}
		
	private void sendBlocEcho(long tr,int tailleBloc) {
		PrintWriter dout = new PrintWriter(ops);
		dout.println("/echoB "+tr+" "+tailleBloc);
		dout.flush();
		sendBlococtet(tr,tailleBloc);
	}
	
	public void close(){
		try {
			this.ops.close();
			this.ips.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			int nboctet =100000000;
			int tailleBloc = 100;
			Client c = new Client(InetAddress.getLocalHost(), 1025);
			
			//System.out.println("***********Envoi octet par octet*************");
			//sendOctParOct(c, nboctet);
			
			//System.out.println("***********Envoi bloc d'octet*************");
			//sendOctParBloc(c, nboctet,tailleBloc);
			
			//System.out.println("***********Envoi octet par octet avec fermeture*************");
			//sendOctParOctAndclose(c, nboctet);
			
			System.out.println("***********Envoi octet par bloc avec fermeture*************");
			sendOctParBlocAndclose(c, nboctet,tailleBloc);
			
			c.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void sendOctParBlocAndclose(Client c, int nboct,int tailleBloc) {
		long beg = System.currentTimeMillis();
		for(int i=0;i<nboct/tailleBloc;i++){
			c.sendBlocEcho(tailleBloc,tailleBloc);
			c.lire(tailleBloc,tailleBloc);
			try {
				try {
					c.s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				c = new Client(InetAddress.getLocalHost(), 1025);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		long end = System.currentTimeMillis();
		System.out.println( ((float)(end-beg))/1000f);
	}

	public static void sendOctParOctAndclose(Client c,int nboct){
		long beg = System.currentTimeMillis();
		for(int i=0;i<nboct;i++){
			c.sendEcho(1);
			c.lire(1);
			try {
				c = new Client(InetAddress.getLocalHost(), 1025);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println( ((float)(end-beg))/1000f);
	}
	
	public static void sendOctParOct(Client c,int nboct){
		long beg = System.currentTimeMillis();
		c.sendEcho(nboct);
		c.lire(nboct);
		long end = System.currentTimeMillis();
		System.out.println( ((float)(end-beg))/1000f);
	}
	
	public static void sendOctParBloc(Client c,long nboct,int tailleBloc){
		long beg = System.currentTimeMillis();
		c.sendBlocEcho(nboct,tailleBloc);
		c.lire(nboct,tailleBloc);
		long end = System.currentTimeMillis();
		System.out.println( ((float)(end-beg))/1000f);
	}
	

}
