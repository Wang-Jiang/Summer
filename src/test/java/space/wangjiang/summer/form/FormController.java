package space.wangjiang.summer.form;

import space.wangjiang.summer.aop.Before;
import space.wangjiang.summer.aop.POST;
import space.wangjiang.summer.controller.Controller;
import space.wangjiang.summer.lab.AnalyzeFile;
import space.wangjiang.summer.form.GenderForm;
import space.wangjiang.summer.form.MyForm;
import space.wangjiang.summer.upload.UploadFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表单的测试
 */
@SuppressWarnings("unused")
public class FormController extends Controller {

    @CheckForm(MyForm.class)
    public void myForm() {
        MyForm form = getForm();
        renderText(MyForm.SUCCESS);
    }

    @Before({POST.class, AnalyzeFile.class})
    @CheckForm(MyForm.class)
    public void file() {
        List<String> list = new ArrayList<>();
        for (UploadFile file : getFiles()) {
            list.add(file.getParameterName() + ":" + file.getFile().getName());
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("file", list);
        map.put("form", getForm());
        renderJson(map);
    }

    @CheckForm(GenderForm.class)
    public void gender() {
        renderText(MyForm.SUCCESS);
    }

    @CheckForm(PersonForm.class)
    public void person() {
        PersonForm form = getForm();
        Map<String, String> map=new HashMap<>();
        map.put("gender", form.gender);
        map.put("age", form.getAge());
        map.put("name", form.getName());
        renderJson(map);
    }

}
