package serveur_cmd;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ServeurThread extends Thread {

	private Serveur serveur;
	private Socket socket;
	
	public ServeurThread(Serveur ss,Socket s) {
		this.serveur=ss;
		this.socket=s;
		start();
	}
	
	public void run(){
		try {
			BufferedReader din = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String mess;
			while(!socket.isClosed()){
				mess = din.readLine();
				System.out.println("Envoi message de "+socket+" : "+mess);
				analyse(mess);		
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void analyse(String mess) {
		if(mess==null)
			return;
		String[] buffer = mess.split(" ");
		if(mess.equals("quit"))
			serveur.ferme(socket);
		else if(buffer[0].equals("/echo")&&buffer.length==2)
			serveur.echo(Integer.parseInt(buffer[1]),socket);
		else if(buffer[0].equals("/ack")&&buffer.length==2)
			serveur.ack(Integer.parseInt(buffer[1]),socket);
		else if(buffer[0].equals("/compute")&&buffer.length==4)
			serveur.compute(Integer.parseInt(buffer[1]),buffer[2],Integer.parseInt(buffer[3]),socket);
		else
			serveur.sendToAll(mess,socket);
	}
	
}
