import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private Scanner input;

    Client(InetAddress ip, int port) throws IOException {
        socket = new Socket(ip, port);
        input = new Scanner(System.in);
    }

    public void lauch() throws Exception {
        System.out.println("请输入对应的数字选择注册或者登陆(1：注册 2：登陆 3:退出):");
        int op = input.nextInt();
        input.nextLine();
        if (op == 1) {
            register();
        } else if (op == 2) {
            login();
        } else if (op == 3) {
            socket.close();
        } else {
            throw new Exception("输入操作码错误！");
        }
    }

    public void register() throws Exception {
        System.out.println("请输入用户名");
        String username = input.nextLine();
        System.out.println("请输入密码");
        String password = input.nextLine();
        ReqMessage reqMessage = new ReqMessage(commandIDS.REGREQUEST, username, password);
        OutputStream os = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
        objectOutputStream.writeObject(reqMessage);
        socket.shutdownOutput();
        System.out.println("正在注册......");
        InputStream is = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(is);
        Object o = objectInputStream.readObject();
        String res = prase((ResMessage) o);
        System.out.println(res);
    }

    public void login() throws Exception {
        System.out.println("请输入用户名");
        String username = input.nextLine();
        System.out.println("请输入密码");
        String password = input.nextLine();
        ReqMessage reqMessage = new ReqMessage(commandIDS.LOGREQUEST, username, password);
        OutputStream os = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
        objectOutputStream.writeObject(reqMessage);
        socket.shutdownOutput();
        System.out.println("正在登陆......");
        InputStream is = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(is);
        Object o = objectInputStream.readObject();
        String res = prase((ResMessage)o);
        System.out.println(res);
    }

    public String prase(ResMessage resMessage) throws Exception {
        byte[] totalLength = resMessage.getTotalLength();
        byte[] command = resMessage.getCommandID();
        byte[] status = resMessage.getStatus();
        byte[] description = resMessage.getDescription();
        int Length = Utils.byeToint(totalLength);
        if (Length != MessageHeader.RESLENGTH) {
            throw new Exception("消息格式错误！");
        }
        commandIDS cmd = commandIDS.values()[Utils.byeToint(command)];
        if (cmd != commandIDS.REGRESPONSE && cmd != commandIDS.LOGRESPONSE) {
            throw new Exception("消息格式错误！");
        }
        if (status[0] != 0 && status[0]!=1) {
            throw new Exception("消息格式错误！");
        }
        return Utils.byteToStr(description);
    }

    public static void main(String[] args) {
        try {
            Client client = new Client(Inet4Address.getLocalHost(), 12000);
            client.lauch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
