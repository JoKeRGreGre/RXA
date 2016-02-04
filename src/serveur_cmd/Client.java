package serveur_cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

public class Client {

	public Socket s;
	public OutputStream ops;
	public InputStream ips;
	public long nbOct;
	
	public Client(InetAddress inetAddress , int p) {
		try {
			this.nbOct = 1000000;
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
	
	private void quiter() {
		try {
			ops.write("quit\n".getBytes());
			ops.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			Client c = new Client(InetAddress.getLocalHost(), 1025);
			ArrayList<Float> list = new ArrayList<>();
			long beg = System.currentTimeMillis();
			c.send1Moctet();
			long end = System.currentTimeMillis();
			list.add(((float)(end-beg))/1000f);
			beg = System.currentTimeMillis();
			c.send1Moctet();
			end = System.currentTimeMillis();
			list.add(((float)(end-beg))/1000f);
			beg = System.currentTimeMillis();
			c.send1Moctet();
			end = System.currentTimeMillis();
			list.add(((float)(end-beg))/1000f);
			beg = System.currentTimeMillis();
			c.send1Moctet();
			end = System.currentTimeMillis();
			list.add(((float)(end-beg))/1000f);
			System.out.println(average(list));
			
			c.quiter();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static float average(ArrayList<Float> list) {
		float rslt=0;
		for(Float f : list){
			rslt+=f;
		}
		return rslt/list.size();
		
	}

}
