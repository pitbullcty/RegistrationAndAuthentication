import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void proc() throws Exception {
        while (true){
            clientSocket  = serverSocket.accept();
            System.out.println("处理客户端请求.....");
            InputStream is = clientSocket.getInputStream();
            byte[] bytes = new byte[1024];
            ResMessage resMessage = null;
            while ((is.read(bytes)!=-1)){
                resMessage = prase(bytes);
            }
            OutputStream os = clientSocket.getOutputStream();
            os.write(resMessage.getbytes());
            clientSocket.shutdownOutput();
            System.out.println("请求处理完毕......");
            Thread.sleep(20);
        }
    }

    public String checkfile(commandIDS command,String username,String password) throws IOException{
        DataInputStream in= new DataInputStream(new BufferedInputStream(new FileInputStream("passwd")));
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("passwd",true)));
        if(command==command.REGREQUEST){
            int cmdhash;
            while (in.available()!=0){
                cmdhash = in.readInt();
                if(cmdhash == username.hashCode()){
                    in.close();
                    out.flush();
                    out.close();
                    return "0 have already registered!";
                }
                in.readInt();
            }
            out.writeInt(username.hashCode());
            out.writeInt(password.hashCode());
            in.close();
            out.flush();
            out.close();
            return "1 register success!";
        }
        else{
            int userhash, passwordhash;
            if(in.available()==0){
                in.close();
                out.flush();
                out.close();
                return "0 file empty!";
            }
            while (in.available()!=0){
                userhash = in.readInt();
                passwordhash = in.readInt();
                if(userhash==username.hashCode()){
                    if(passwordhash==password.hashCode()) {
                        in.close();
                        out.flush();
                        out.close();
                        return "1 login success!";
                    }
                    else {
                        in.close();
                        out.flush();
                        out.close();
                        return "0 password error!";
                    }
                }
            }
            in.close();
            out.flush();
            out.close();
            return "0 user not found!";
        }
    }

    public ResMessage prase(byte[] bytes) throws Exception{
        byte[] totalLength = new byte[4];
        byte[] command = new byte[4];
        byte[] username = new byte[20];
        byte[] passwd = new byte[30];
        System.arraycopy(bytes,0,totalLength,0,totalLength.length);
        int Length = Utils.byeToint(totalLength);
        if(Length != MessageHeader.REQLENGTH){
            throw new Exception("消息格式错误！");
        }
        System.arraycopy(bytes,totalLength.length,command,0,command.length);
        commandIDS cmd = commandIDS.values()[Utils.byeToint(command)];
        if(cmd != commandIDS.REGREQUEST && cmd!=commandIDS.LOGREQUEST){
            throw new Exception("消息格式错误！");
        }
        System.arraycopy(bytes,totalLength.length+command.length,username,0,username.length);
        String usrname = Utils.byteToStr(username);
        System.arraycopy(bytes,totalLength.length+command.length+username.length,passwd,0,passwd.length);
        String password = Utils.byteToStr(passwd);
        String res = checkfile(cmd,usrname,password);
        Scanner reScanner = new Scanner(res);
        int index = reScanner.nextInt();
        String description = reScanner.nextLine();
        STATUS status = STATUS.values()[index];
        cmd = (cmd==commandIDS.REGREQUEST)?commandIDS.REGRESPONSE:commandIDS.LOGRESPONSE;
        return new ResMessage(cmd,status,description);
    }

    public static void main(String[] args) {
        try{
            Server server = new Server(12000);
            server.proc();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}
