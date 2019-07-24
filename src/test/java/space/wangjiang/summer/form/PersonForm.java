package space.wangjiang.summer.form;

/**
 * 测试表单继承功能
 */
public class PersonForm extends GenderForm {

    @Required(errorMsg = "姓名是必填项")
    private String name;

    @Type(type = Type.TYPE.NUMBER, errorMsg = "年龄需要是有效的数字")
    private String age;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }
}
