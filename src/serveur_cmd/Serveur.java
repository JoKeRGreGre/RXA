package serveur_cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.sun.corba.se.impl.ior.ByteBuffer;

public class Serveur {
	private ServerSocket ss;
	private Socket s;
	private Thread t;
	private int listenPort;
	private ArrayList<Socket> outputStreams = new ArrayList<Socket>();

	public Serveur(int p) {
		this.listenPort = p;
		try {
			ss = new ServerSocket(listenPort);
			System.out.println("Server " + ss + " listening on " + p);
			this.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		while (true) {
			try {
				s = ss.accept();
				new ServeurThread(this, s);
				System.out.println("Connection from : " + s);
				PrintWriter dout = new PrintWriter(s.getOutputStream());
				outputStreams.add(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void sendToAll(String m, Socket s) {
		synchronized (outputStreams) {
			for (Socket soc : outputStreams) {
				PrintWriter dout;
				if (!soc.equals(s)) {
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

	public void sendToAll(String m) {
		for (Socket soc : outputStreams) {
			PrintWriter dout;
			try {
				soc.getOutputStream().write(m.getBytes());
				soc.getOutputStream().flush();
				/*
				dout = new PrintWriter(soc.getOutputStream());
				dout.println(m);
				dout.flush();
				*/
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void ferme(Socket socket) {
		System.out.println("Connection terminé : " + s);
		try {
			outputStreams.remove(socket);
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void echo(long nbOct, int tailleBloc, Socket socket) {
		System.out.println("************echo Bloc*****************");
		long nbTours = nbOct/tailleBloc;
		byte[] b = new byte[tailleBloc];
		for(long j =0 ;j<nbTours;j++){
				try {
					socket.getInputStream().read(b);
					/*
					for(int i=0;i<tailleBloc;i++) 
						System.out.println((char)b[i]);
					*/	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("NBTOUR="+j + " == NBTOUR="+nbOct/tailleBloc);
				sendToSocket(b,socket);
		}
		
		System.out.println("************echo Bloc*****************");
	}
	
	public void echo(int nbOct, Socket socket) {
		byte[] b = new byte[nbOct];
		System.out.println("************echo*****************");
			try {
				for(int i=0;i<nbOct;i++)
					b[i]=(byte) socket.getInputStream().read();
				//socket.getInputStream().read(nbOct);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		System.out.println("************echo*****************");
		sendToSocket(b,socket);
	}

	private void sendToSocket(byte[] b,Socket socket) {

			try {
				socket.getOutputStream().write(b);
				socket.getOutputStream().flush();
				/*
				dout = new PrintWriter(soc.getOutputStream());
				dout.println(m);
				dout.flush();
				*/
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void ack(int i, Socket s) {
		int octlu = 0;
		BufferedReader din;
		String rslt = "";
		while (octlu < i)
			try {
				din = new BufferedReader(new InputStreamReader(
						s.getInputStream()));
				rslt += (char) din.read();
				octlu++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		sendAck(s, "" + i);
	}

	private void sendAck(Socket s2, String m) {
		PrintWriter dout;
		try {
			dout = new PrintWriter(s2.getOutputStream());
			dout.println("[OK " + m + " ]");
			dout.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void compute(int i1, String s, int i2, Socket socket) {
		float rslt=0 ;
		if(s.equals("+"))
			rslt = i1+i2;
		else if(s.equals("*"))
			rslt = i1*i2;
		else if(s.equals("/"))
			rslt = i1/i2;
		else if(s.equals("-"))
			rslt = i1-i2;
		sendAck(socket, ""+rslt);
	}

}
