package cn.guet.haojiayou.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamTool {
    public static byte[] readInputStream(InputStream inputStream) throws IOException{
    	byte [] buffer = new byte[1024];
    	ByteArrayOutputStream os = new ByteArrayOutputStream();
    	int len=0;
        while(-1!=(len=(inputStream.read(buffer)))){
    		os.write(buffer, 0, len);
    	}
        os.flush();
        os.close();
		return os.toByteArray();
    	
    }
}