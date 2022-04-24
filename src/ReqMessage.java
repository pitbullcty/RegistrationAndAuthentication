public class ReqMessage extends MessageHeader{
    private byte[] username;
    private byte[] passwd;

    ReqMessage(commandIDS command,String username,String password) throws Exception {
        super(command);
        this.username = new byte[20];
        this.passwd = new byte[30];
        byte[] Byteusername = username.getBytes();
        byte[] Bytepassword = password.getBytes();
        if(Byteusername.length>20 || Bytepassword.length>30){
            throw new Exception("密码或者账号过长！");
        }
        System.arraycopy(Byteusername,0,this.username,0,Byteusername.length);
        System.arraycopy(Bytepassword,0,this.passwd,0,Bytepassword.length);
    }

    public byte[] getPasswd() {
        return passwd;
    }

    public byte[] getUsername() {
        return username;
    }


}
