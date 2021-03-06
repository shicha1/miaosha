package org.example.response;

public class CommonReturnType {

    //表明对应请求的返回处理结果（success/fail）
    private String status;

    //若status为success，则data内返回前端需要的json数据
    //若status为fail，则data内使用通用的错误码格式（也就是能让前端获得有意义的错误信息）
    private Object data;



    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }

    public static CommonReturnType create(Object result, String status){
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
