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

    @Override
    public byte[] getbytes() {
        byte[] temp = super.getbytes();
        byte[] res = new byte[temp.length+username.length+passwd.length];
        System.arraycopy(temp,0,res,0,temp.length);
        System.arraycopy(username,0,res,temp.length,username.length);
        System.arraycopy(passwd,0,res,temp.length+username.length,passwd.length);
        return res;
    }

}
