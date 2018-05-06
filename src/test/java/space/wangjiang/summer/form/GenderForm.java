package space.wangjiang.summer.form;

@SuppressWarnings("unused")
public class GenderForm extends Form {

    @Required(errorMsg = "必填")
    @In(in = "男,女,male,female", errorMsg = "输入有效的性别")
    public String gender;

}
