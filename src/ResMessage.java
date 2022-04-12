public class ResMessage extends MessageHeader{
    private byte[] status;
    private byte[] description;

    ResMessage(commandIDS command,STATUS status,String description) throws Exception {
        super(command);
        this.status = new byte[1];
        this.status[0] = (byte) (status==STATUS.SUCCESS?1:0);
        this.description = new byte[64];
        byte[] Bytediscription = description.getBytes();
        if(Bytediscription.length>64){
            throw new Exception("消息体过长！");
        }
        System.arraycopy(Bytediscription,0,this.description,0,Bytediscription.length);
    }

    @Override
    public byte[] getbytes() {
        byte[] temp = super.getbytes();
        byte[] res = new byte[temp.length+description.length+status.length];
        System.arraycopy(temp,0,res,0,temp.length);
        System.arraycopy(status,0,res,temp.length,status.length);
        System.arraycopy(description,0,res,temp.length+status.length,description.length);
        return res;
    }

}
