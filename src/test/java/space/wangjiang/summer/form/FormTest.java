package space.wangjiang.summer.form;

import org.junit.Assert;
import org.junit.Test;
import space.wangjiang.summer.form.MyForm;
import space.wangjiang.summer.test.BaseTest;
import space.wangjiang.summer.test.Param;

public class FormTest extends BaseTest {

    private String myFormUrl = "http://127.0.0.1:8080/form/myForm";

    @Test
    public void myForm1() {
        Param param = new Param();
        param.put("name", "JACK");
        param.put("age", 12);
        param.put("url", "www.example.com");
        Assert.assertEquals(MyForm.ERROR_MSG_URL_TYPE, get(myFormUrl, param));
    }

    @Test
    public void myForm2() {
        Param param = new Param();
        param.put("name", "JACK");
        param.put("age", "abc");
        param.put("url", "www.example.com");
        Assert.assertEquals(MyForm.ERROR_MSG_AGE_TYPE, get(myFormUrl, param));
    }

    @Test
    public void myForm3() {
        Param param = new Param();
        param.put("name", "JACK");
        param.put("url", "www");
        Assert.assertEquals(MyForm.ERROR_MSG_URL_TYPE, get(myFormUrl, param));
    }

    @Test
    public void myForm4() {
        Param param = new Param();
        param.put("name", "JACK");
        Assert.assertEquals(MyForm.SUCCESS, get(myFormUrl, param));
    }

    @Test
    public void myForm5() {
        Param param = new Param();
        param.put("name", "");
        param.put("age", "12");
        Assert.assertEquals(MyForm.ERROR_MSG_NAME_REQUIRED, get(myFormUrl, param));
    }

    @Test
    public void myForm6() {
        Param param = new Param();
        param.put("name", "JACK");
        param.put("age", "+12");
        Assert.assertEquals(MyForm.ERROR_MSG_AGE_TYPE, get(myFormUrl, param));
    }

}
