public class RconImpl{

    public static void main(String[] args){
        try {
            Rcon server = new Rcon("209.97.168.90",28961,"scrim123",true,10000,10000);
            String s = server.sendCommand("map mp_killhouse");
            System.out.println(s);
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
        
    }
}