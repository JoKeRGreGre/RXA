package selector;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


public class Serveur{


	private ServerSocketChannel ss;
	private SocketChannel s;
	private int listenPort ;
	private Selector select;
	private ArrayList<SocketChannel> listecli;

	public Serveur(int p) {
		this.listenPort=p;
		this.listecli= new ArrayList<>();
		try {
			ss=ServerSocketChannel.open();
			ss.socket().bind(new InetSocketAddress(listenPort));
			ss.configureBlocking(false);

			select= Selector.open();
			ss.register(select, SelectionKey.OP_ACCEPT);

			System.out.println("Server "+ss+" listening on "+p);
			this.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() throws IOException{

		while(true){
			if(select.select()==0){
				/*Iteration sur les evennements*/
				Set<SelectionKey> selectedKeys = select.selectedKeys();
				Iterator<SelectionKey> keyIterator =
						selectedKeys.iterator();
				while (keyIterator.hasNext()) {
					SelectionKey selectionKey = keyIterator.next();

					/*evennement de connexion*/
					if(selectionKey.isAcceptable())
						connectEvent(selectionKey);
					/*evennement de lecture*/
					else if(selectionKey.isReadable())
						readEvent(selectionKey);
					/* evennement d'ecriture */
					else if(selectionKey.isWritable())
						writeEvent(selectionKey);
					
					keyIterator.remove();
				}
			}

		}


	}

	private void writeEvent(SelectionKey s) {
		SocketChannel c = (SocketChannel) s.channel();
		String r = (String) s.attachment();
		ByteBuffer buffer = ByteBuffer.wrap(r.getBytes());
		try {
			buffer.clear();
			c.write(buffer);
			c.close();
			System.out.println("Ecriture : "+r);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readEvent(SelectionKey s) {
		SocketChannel c = (SocketChannel) s.channel();
		String rslt = "";
		try {
			ByteBuffer buffer = ByteBuffer.allocate(48);
			 
			while (c.read(buffer) > 0) {
			    buffer.flip();
			    while (buffer.hasRemaining()) {
			    	rslt += (char) buffer.get();
			    }
			    buffer.clear();
			}
			System.out.println("mess : " + rslt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			writeToAll(s,rslt);
		}
	}

	private void writeToAll(SelectionKey sel ,String rslt) {
		//SocketChannel c = (SocketChannel) sel.channel();
		for(SocketChannel s : listecli)
			//if(s!=c)
				try {
					s.register(select, SelectionKey.OP_WRITE, rslt);
				} catch (ClosedChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	private void connectEvent(SelectionKey s) {
		ServerSocketChannel ssc = (ServerSocketChannel) s.channel();
		try {
			this.s = ssc.accept();
			System.out.println("Connection from "+s.toString()+" to "+ssc.toString() );
			this.s.configureBlocking(false);
			this.s.register(select, SelectionKey.OP_READ);
			this.listecli.add(this.s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

