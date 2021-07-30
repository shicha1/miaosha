package org.example.error;

public interface CommenError {
    public int getErrCode();
    public String getErrMsg();
    public CommenError setErrMsg(String errMsg);
}
