public class Utils {
    public static byte[] intTobyte(int num) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (num & 0xff);
        bytes[2] = (byte) (num >> 8 & 0xff);
        bytes[1] = (byte) (num >> 16 & 0xff);
        bytes[0] = (byte) (num >> 24 & 0xff);
        return bytes;
    }

    public static int byeToint(byte[] bytes) {
        int res = 0;
        for (int i = 0; i < bytes.length; i++) {
            res += (bytes[i] & 0xff) << ((3 - i) * 8);
        }
        return res;
    }

    public static String byteToStr(byte[] bytes){
        int length = 0;
        for(int i=0;i<bytes.length;i++){
            if (bytes[i] == 0) {
                length = i;
                break;
            }
        }
        return new String(bytes,0,length);
    }
}

//注册类型
enum commandIDS {
    NONE,
    REGREQUEST,
    REGRESPONSE,
    LOGREQUEST,
    LOGRESPONSE
}


enum STATUS {
    FAILED,
    SUCCESS
}