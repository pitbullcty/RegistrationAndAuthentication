import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private int port;
    private InetAddress ip;
    private Socket socket;
    private Scanner input;

    Client(InetAddress ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        socket = new Socket(ip,port);
        input = new Scanner(System.in);
    }

    public void lauch() throws Exception {
        while (true){
            System.out.println("请输入对应的数字选择注册或者登陆(1：注册 2：登陆 3:退出):");
            int op = input.nextInt();
            input.nextLine();
            if(op==1){
                register();
            }
            else if(op==2){
                login();
            }
            else if(op==3){
                socket.close();
                break;
            }
            else{
                throw new Exception("输入操作码错误！");
            }
            Thread.sleep(20);
        }

    }

    public void register() throws Exception{
        System.out.println("请输入用户名");
        String username = input.nextLine();
        System.out.println("请输入密码");
        String password = input.nextLine();
        ReqMessage reqMessage = new ReqMessage(commandIDS.REGREQUEST,username,password);
        OutputStream os = socket.getOutputStream();
        os.write(reqMessage.getbytes());
        socket.shutdownOutput();
        InputStream is = socket.getInputStream();
        byte[] buffer = new byte[1024];
        while (-1!= is.read(buffer)){
            String res = prase(buffer);
            System.out.println(res);
        }
    }

    public void login() throws Exception{
        System.out.println("请输入用户名");
        String username = input.nextLine();
        System.out.println("请输入密码");
        String password = input.nextLine();
        ReqMessage reqMessage = new ReqMessage(commandIDS.LOGREQUEST,username,password);
        OutputStream os = socket.getOutputStream();
        os.write(reqMessage.getbytes());
        socket.shutdownOutput();
        System.out.println("正在登陆......");
        InputStream is = socket.getInputStream();
        byte[] buffer = new byte[1024];
        while (-1!=(is.read(buffer))){
            String res = prase(buffer);
            System.out.println(res);
        }
    }

    public String prase(byte[] bytes) throws Exception{
        byte[] totalLength = new byte[4];
        byte[] command = new byte[4];
        byte[] status = new byte[1];
        byte[] description = new byte[64];
        System.arraycopy(bytes,0,totalLength,0,totalLength.length);
        int Length = Utils.byeToint(totalLength);
        if(Length != MessageHeader.RESLENGTH){
            throw new Exception("消息格式错误！");
        }
        System.arraycopy(bytes,totalLength.length,command,0,command.length);
        commandIDS cmd = commandIDS.values()[Utils.byeToint(command)];
        if(cmd != commandIDS.REGRESPONSE && cmd!=commandIDS.LOGRESPONSE){
            throw new Exception("消息格式错误！");
        }
        System.arraycopy(bytes,totalLength.length+command.length,status,0,status.length);
        if(Utils.byeToint(status)!=1 && Utils.byeToint(status)!=0){
            throw new Exception("消息格式错误！");
        }
        System.arraycopy(bytes,totalLength.length+command.length+status.length,description,0,description.length);
        return new String(description);
    }

    public static void main(String[] args){
        try{
            Client client = new Client(Inet4Address.getLocalHost(),12000);
            client.lauch();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
