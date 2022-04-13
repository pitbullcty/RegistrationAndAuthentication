import java.io.Serializable;

public class MessageHeader implements Serializable {
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

    public byte[] getTotalLength() {
        return totalLength;
    }

    public byte[] getCommandID() {
        return commandID;
    }

}

