package com.jinan.ladongjiguan.djj8plus.publicClass;

public class DataTransformation {
    /**
    * 字节转16进制
    * */
    public static String bytesToHex(byte[] b) {
        StringBuilder a = new StringBuilder();
        for (byte aB : b) {
            String hex = Integer.toHexString(aB & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            a.append(hex);
        }

        return a.toString();
    }
    /**
     * 16进制转10进制
     * */
    public static String hexToInt(String Hex) {

        int d = Integer.valueOf(Hex,16);
        return String.valueOf(d);
    }
    /**
     * 16进制转10进制
     * */
    public static String hexToIntForDJJ(String Hex) {

        double d = Integer.valueOf(Hex,16)/10.0;
        return String.valueOf(d);
    }
    /**
     * D916进制转10进制
     * */
    public static String hexToIntForDJJD9(String Hex) {

        double d = Integer.valueOf(Hex,16)/1000.0;
        return String.valueOf(d);
    }
    /**
     * Dc16进制转10进制
     * */
    public static String hexToIntForDJJDc(String Hex) {

        double d = Integer.valueOf(Hex,16)/100.0;
        return String.valueOf(d);
    }
    /**
     * 字节转10进制
     */
    public static int byte2Int(byte b){
        return (int) b;
    }
    /**
     * 10进制转字节
     */
    public static byte int2Byte(int i){
        return (byte) i;
    }
    /**
     * 字符转换为字节
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 16进制字符串转字节数组
     */
    public static byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))){
            return null;
        }
        else if (hex.length()%2 != 0){
            return null;
        }
        else{
            hex = hex.toUpperCase();
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }

    }

    /**
     * 字节数组转字符串
     */
    public static String bytes2String(byte[] b) throws Exception {
        return new String (b,"UTF-8");
    }
    /**
     * 字符串转字节数组
     */
    public static byte[] string2Bytes(String s){
        return s.getBytes();
    }

}
