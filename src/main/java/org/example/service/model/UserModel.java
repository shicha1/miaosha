package org.example.service.model;


import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

//DO(dataobject仅仅只是对数据库的映射，usermodel才是处理业务逻辑的核心层面）
public class UserModel implements Serializable {
    private Integer id;
    @NotBlank(message = "用户名不能为空")
    private String name;
    @NotNull(message = "性别必须填写")
    private Byte gender;
    @NotNull(message = "年龄必须填写")
    @Min(value = 0, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄必须小于150")
    private Integer age;
    @NotBlank(message = "手机号不能为空")
    private String telphone;
    private String registerMode;
    private String thirdPartyId;
    @NotBlank(message = "密码不能为空")
    private String encrptPassword;

    public String getEncrptPassword() {
        return encrptPassword;
    }

    public void setEncrptPassword(String encrptPassword) {
        this.encrptPassword = encrptPassword;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }


    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public void setRegisterMode(String registerMode) {
        this.registerMode = registerMode;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Byte getGender() {
        return gender;
    }


    public String getTelphone() {
        return telphone;
    }

    public String getRegisterMode() {
        return registerMode;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }
}
