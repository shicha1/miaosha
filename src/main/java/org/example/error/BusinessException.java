package org.example.error;

//包装器业务异常类实现
public class BusinessException extends Exception implements CommenError{

    private CommenError commenError;

    //直接接收EmBusinessError的传参用于构造业务异常
    public BusinessException(CommenError commenError){
        super();
        this.commenError=commenError;
    }

    //接收自定义errmsg的方式构造业务异常
    public  BusinessException(CommenError commenError,String errMsg){
        super();
        this.commenError=commenError;
        this.commenError.setErrMsg(errMsg);
    }



    @Override
    public int getErrCode() {
        return this.commenError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commenError.getErrMsg();
    }

    @Override
    public CommenError setErrMsg(String errMsg) {
        this.commenError.setErrMsg(errMsg);
        return this;
    }
}
