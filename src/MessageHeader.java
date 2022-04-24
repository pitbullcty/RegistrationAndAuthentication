public class MessageHeader{
    protected byte[] totalLength;
    protected byte[] commandID;
    protected final static int REQLENGTH = 58;
    protected final static int RESLENGTH = 73;

    MessageHeader(commandIDS command){
        if(command== commandIDS.REGREQUEST || command==commandIDS.LOGREQUEST)
            this.totalLength = Utils.intTobyte(REQLENGTH);
        else this.totalLength = Utils.intTobyte(RESLENGTH);
        this.commandID = Utils.intTobyte(command.ordinal());
    }

    public byte[] getbytes(){
        byte[] res = new byte[totalLength.length+commandID.length];
        System.arraycopy(totalLength,0,res,0,totalLength.length);
        System.arraycopy(commandID,0,res,totalLength.length,commandID.length);
        return res;
    }
}

