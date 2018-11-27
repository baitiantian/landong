package com.jinan.ladongjiguan.anjiantong.connect;

/**
 * Created by guo on 2017/5/8.
 */
public class appDataEntity {
    private String type;
    private String username;
    private String password;
    private String uniqueMark;
    private String userId;
    private String sendTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUniqueMark() {
        return uniqueMark;
    }

    public void setUniqueMark(String uniqueMark) {
        this.uniqueMark = uniqueMark;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        try {
            this.sendTime = this.formatTime(sendTime);
        } catch (Exception e) {
            this.sendTime = sendTime;
            System.out.println(e);
        }

    }

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public String formatTime(String time) {
        String str = time.substring(0, 4) + "-" + time.substring(4, 6) + "-"
                + time.substring(6, 8) + " " + time.substring(8, 10) + ":"
                + time.substring(10, 12) + ":" + time.substring(12, 14);
        return str;
    }
}
