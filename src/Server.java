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
            ObjectInputStream objectInputStream = new ObjectInputStream(is);
            Object o = objectInputStream.readObject();
            ResMessage resMessage = parse((ReqMessage) o);
            OutputStream os = clientSocket.getOutputStream();
            ObjectOutputStream  outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(resMessage);
            clientSocket.shutdownOutput();
            System.out.println("请求处理完毕......");
            Thread.sleep(20);
        }
    }

    public String checkfile(commandIDS command,String username,String password) throws IOException{
        DataInputStream in= new DataInputStream(new BufferedInputStream(new FileInputStream("passwd")));
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("passwd",true)));
        if(command==command.REGREQUEST){
            String tempname=null;
            while (in.available()!=0){
                tempname = in.readUTF();
                if(tempname.equals(username)){
                    in.close();
                    out.flush();
                    out.close();
                    return "0 have already registered!";
                }
                in.readInt();
            }
            out.writeUTF(username);
            out.writeInt(password.hashCode());
            in.close();
            out.flush();
            out.close();
            return "1 register success!";
        }
        else{
            String tempusr = null;
            int passwordhash;
            if(in.available()==0){
                in.close();
                out.flush();
                out.close();
                return "0 file empty!";
            }
            while (in.available()!=0){
                tempusr = in.readUTF();
                passwordhash = in.readInt();
                if(tempusr.equals(username)){
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

    public ResMessage parse(ReqMessage reqMessage) throws Exception{
        byte[] totalLength = reqMessage.getTotalLength();
        byte[] command = reqMessage.getCommandID();
        byte[] username = reqMessage.getUsername();
        byte[] passwd = reqMessage.getPasswd();
        int Length = Utils.byeToint(totalLength);
        if(Length != MessageHeader.REQLENGTH){
            throw new Exception("消息格式错误！");
        }
        commandIDS cmd = commandIDS.values()[Utils.byeToint(command)];
        if(cmd != commandIDS.REGREQUEST && cmd!=commandIDS.LOGREQUEST){
            throw new Exception("消息格式错误！");
        }
        String usrname = Utils.byteToStr(username);
        String password = Utils.byteToStr(passwd);
        String res = checkfile(cmd,usrname,password);
        int index = Integer.valueOf(res.substring(0,1));
        String description = res.substring(2);
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
