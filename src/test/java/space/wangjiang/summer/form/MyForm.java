package space.wangjiang.summer.form;

import space.wangjiang.summer.controller.Controller;

@SuppressWarnings("unused")
public class MyForm extends Form {

    public static final String SUCCESS = "success";
    public static final String ERROR_MSG_NAME_REQUIRED = "必填项name";
    public static final String ERROR_MSG_AGE_TYPE = "输入合理的年龄";
    public static final String ERROR_MSG_URL_TYPE = "输入合理的URL";
    public static final String ERROR_MSG_URL_LENGTH = "网站长度过长";

    @Required(errorMsg = ERROR_MSG_NAME_REQUIRED)
    @Type(type = Type.TYPE.TEXT)
    private String name;

    @Type(type = Type.TYPE.NUMBER, errorMsg = ERROR_MSG_AGE_TYPE)
    private String age;

    @Type(type = Type.TYPE.URL, errorMsg = ERROR_MSG_URL_TYPE)
    @Length(max = 64, errorMsg = ERROR_MSG_URL_LENGTH)
    private String url;

    public String getName() {
        return name;
    }

    public int getAge() {
        return Integer.parseInt(age);
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void renderError(Controller controller, String errorMsg) {
        controller.renderText(errorMsg);
    }

}
