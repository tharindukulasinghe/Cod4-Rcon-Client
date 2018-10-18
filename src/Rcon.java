import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Rcon
{
	private InetAddress ipAddress;
	
	private int port;
	private int receiveTimeout;
	private int	sleepTimer;
	
	private boolean returnsData;
	
	private String password;
	private String command;
	private String retStr;
	
	private DatagramPacket dataPacketOut;
	private DatagramPacket dataPacketIn;
	

	public Rcon(String ip, int port, String password, boolean returnsData, int receiveTimeout, int sleepTimer) throws IOException
	{
		this.port = port;
		this.password = password;
		this.returnsData = returnsData;
		this.receiveTimeout = receiveTimeout;
		this.sleepTimer = sleepTimer;
		parseAddress(ip);
	}
	
	
	public void sleeper() throws InterruptedException
	{
		Thread.sleep(sleepTimer);
	}
	

	private void parseAddress(String ip) throws IOException
	{
			ipAddress = InetAddress.getByName(ip);
			System.out.println(ipAddress);
	}
		
	
	
	public DatagramPacket buildPacket(String rconCommand) throws IOException
	{
		// Build the command string to be sent
		// The leading Xs are place holders for out of bounds bytes that will be converted once we get the java bytes for the string
		command = "xxxx" + "rcon " + password + " " + rconCommand;
		
		// Convert the command string to bytes
		byte[] commandBytes = command.getBytes();
		
		// Replace the first 5 bytes (those leading Xs) in the commandBytes with the correct bytes
		commandBytes[0] = (byte)0xff;
		commandBytes[1] = (byte)0xff;
		commandBytes[2] = (byte)0xff;
		commandBytes[3] = (byte)0xff;
		//commandBytes[4] = (byte)0x00;

		
		// Build the UDP packet that is to be sent
		dataPacketOut = new DatagramPacket(commandBytes, commandBytes.length, ipAddress, port);
		
		return dataPacketOut;
	}
	
	

	public String sendCommand(String rconCommand) throws InterruptedException
	{
		try{

			// Create a new DatagramSocket instance
			DatagramSocket dataSocket = new DatagramSocket(null);
			
			// Connect the new datagramSocket instance to the provided ipAddress and port
			dataSocket.connect(ipAddress, port);
			
			// Set the timeout of the socket connection; TODO: parameterize the timeout value.
			dataSocket.setSoTimeout(receiveTimeout);
  		
			// Send the packet (rcon command) to the server.
			dataSocket.send(buildPacket(rconCommand));

			if (returnsData) {
				// Create a new buffer to receive any response from the rcon command
				byte[] buffer = new byte[4000];
			
				// Create the new datagram packet to house the returned results of the command
				dataPacketIn = new DatagramPacket(buffer,buffer.length);
			
				// Receive the buffer using the datagram socket.
				dataSocket.receive(dataPacketIn);
			
				retStr = new String(dataPacketIn.getData(), 0, dataPacketIn.getLength());
			}
			else
			{
				retStr = new String("Command sent on source port: " + dataSocket.getLocalPort());
			}

			sleeper();
			
		}
		catch(IOException ex){
			retStr = new String(ex.getMessage());
		}
		
		// Return the results
		return retStr;

	}
}