package Handler;

import java.io.Serializable;

public class FielMessage implements Serializable {
    String fileName;
    byte[] fileByte;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileByte() {
        return fileByte;
    }

    public void setFileByte(byte[] fileByte) {
        this.fileByte = fileByte;
    }
}

