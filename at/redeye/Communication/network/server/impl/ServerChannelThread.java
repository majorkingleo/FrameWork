/**
 * 
 */
package at.redeye.Communication.network.server.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;


/**
 * @author Mario Mattl
 * 
 */
public class ServerChannelThread extends Thread {

	private int port;
	private SynchronousQueue<byte []> squeue;

	public ServerChannelThread(int port, SynchronousQueue<byte []> sender_queue) {
		super();
		this.port = port;
		this.squeue = sender_queue;
	}

    @Override
	public void run() {

		try {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ServerSocket ss = ssc.socket();
			java.nio.channels.Selector sel = java.nio.channels.Selector.open();

			ss.bind(new InetSocketAddress(port));
			ssc.configureBlocking(false);
			ssc.register(sel, SelectionKey.OP_ACCEPT);

			while (true) {
				int n = sel.select();

				if (n == 0)
					continue;

				Iterator<SelectionKey> it = sel.selectedKeys().iterator();

				while (it.hasNext()) {
					SelectionKey key = (SelectionKey) it.next();
					// Is a new connection coming in?
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key
								.channel();
						SocketChannel channel = server.accept();

						registerChannel(sel, channel, SelectionKey.OP_READ);

					}

					// is there data to read on this channel?
					if (key.isReadable()) {
						queueDataFromSocket(key);
					}

					// remove key from selected set, it's been handled
					it.remove();
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	// ----------------------------------------------------------

	/**
	 * Register the given channel with the given selector for the given
	 * operations of interest
	 * @throws IOException 
	 */
	protected void registerChannel(java.nio.channels.Selector selector,
			SelectableChannel channel, int ops) throws IOException  {
		
		if (channel == null) {
			return; // could happen
		}

		// set the new channel non-blocking
		channel.configureBlocking(false);

		// register it with the selector
		channel.register(selector, ops);
	}
	
	
	   protected void queueDataFromSocket (SelectionKey key) throws IOException
	      
	   {
	      SocketChannel socketChannel = (SocketChannel) key.channel();
	      ByteBuffer buffer = ByteBuffer.allocate(100);
	      int count;

	      buffer.clear();         // make buffer empty

	      // loop while data available, channel is non-blocking
	      while ((count = socketChannel.read (buffer)) > 0) {
	    	  
	         
	         int len = buffer.position();
	         buffer.flip();      // make buffer readable
	         if (len <= 0) {
	        	 System.out.println("Buffer is empty -> nothing to do\n");
	        	 continue;
	         }
	         byte [] arr = new byte [len];
	         buffer.get(arr, 0, len);
	         for (int i = 0; i < arr.length; i++) {
	        	 
	        	 System.out.print(String.format("%02x ", arr[i]));
	        	 
	         }
	         System.out.println("\n");
	         
	        
	        try {
				boolean res = squeue.offer(arr, 200, TimeUnit.MILLISECONDS);
				if (res == false) {
					System.out.println("OFFER FAILED!");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         buffer.clear();

	      }

	      if (count < 0) {
	         // close channel on EOF, invalidates the key
	         socketChannel.close();
	      }
	   }

}
