import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Rcon {
    private InetAddress ipAddress;

    private int port;
    private int receiveTimeout;
    private int sleepTimer;

    private boolean returnsData;

    private String password;
    private String command;
    private String retStr;

    private DatagramPacket dataPacketOut;
    private DatagramPacket dataPacketIn;

    public Rcon(String ip, int port, String password, boolean returnsData, int receiveTimeout, int sleepTimer)
            throws IOException {
        this.port = port;
        this.password = password;
        this.returnsData = returnsData;
        this.receiveTimeout = receiveTimeout;
        this.sleepTimer = sleepTimer;
        parseAddress(ip);
    }

    public void sleeper() throws InterruptedException {
        Thread.sleep(sleepTimer);
    }

    private void parseAddress(String ip) throws IOException {
        ipAddress = InetAddress.getByName(ip);
        System.out.println(ipAddress);
    }

    public DatagramPacket buildPacket(String rconCommand) throws IOException {

        command = "xxxx" + "rcon " + password + " " + rconCommand;

        byte[] commandBytes = command.getBytes();

        commandBytes[0] = (byte) 0xff;
        commandBytes[1] = (byte) 0xff;
        commandBytes[2] = (byte) 0xff;
        commandBytes[3] = (byte) 0xff;

        dataPacketOut = new DatagramPacket(commandBytes, commandBytes.length, ipAddress, port);

        return dataPacketOut;
    }

    public String sendCommand(String rconCommand) throws InterruptedException {
        DatagramSocket dataSocket = new DatagramSocket(null);
        try {

            dataSocket.connect(ipAddress, port);

            dataSocket.setSoTimeout(receiveTimeout);

            dataSocket.send(buildPacket(rconCommand));

            if (returnsData) {

                byte[] buffer = new byte[4000];

                dataPacketIn = new DatagramPacket(buffer, buffer.length);

                dataSocket.receive(dataPacketIn);

                retStr = new String(dataPacketIn.getData(), 0, dataPacketIn.getLength());
            } else {
                retStr = new String("Command sent on source port: " + dataSocket.getLocalPort());
            }

            sleeper();

        } catch (IOException ex) {
            retStr = new String(ex.getMessage());
        }
        finally {
            dataSocket.close();
        }

        return retStr;

    }
}