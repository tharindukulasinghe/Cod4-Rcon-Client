public class RconImpl{

    public static void main(String[] args){
        Rcon server = new Rcon("209.97.168.90",28961,"scrim123",true,10000,10000);
        server.sendCommand("map mp_killhouse");
    }
}