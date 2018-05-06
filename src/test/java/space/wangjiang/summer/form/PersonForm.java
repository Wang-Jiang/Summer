package space.wangjiang.summer.form;

/**
 * 测试表单继承功能
 */
public class PersonForm extends GenderForm {

    public static final String TAG = "TAG";

    @Required(errorMsg = "姓名是必填项")
    private String name;

    @Type(type = Type.TYPE.NUMBER, errorMsg = "年龄需要时数字")
    private String age;

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }
}
