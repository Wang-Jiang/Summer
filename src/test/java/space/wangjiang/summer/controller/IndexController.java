package space.wangjiang.summer.controller;

import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.aop.Before;
import space.wangjiang.summer.aop.GET;
import space.wangjiang.summer.aop.POST;
import space.wangjiang.summer.aop.Remove;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.model.Blog;
import space.wangjiang.summer.model.Page;
import space.wangjiang.summer.model.User;
import space.wangjiang.summer.route.UrlMapping;
import space.wangjiang.summer.upload.UploadFile;
import space.wangjiang.summer.upload.UploadRequest;

import java.util.List;

@Before(POST.class)
@Remove
@SuppressWarnings("unused")
public class IndexController extends Controller {

    @Before(GET.class)
    public void index() {
        renderJsp("index.jsp");
    }

    public void error() {
        throw new RuntimeException("error");
    }

    @UrlMapping(url = "/upload/")
    public void upload() {
        renderJsp("/upload.jsp");
    }

    public void uploadAction() {
        //禁止上传非png文件
//        analyzeMultipartRequest(null,
//                4L * 1024 * 1024,
//                new PngUploadStrategy(),
//                new RandomFileRenameStrategy()
//        );
        List<UploadFile> uploadFile = getFiles();
        for (UploadFile file : uploadFile) {
            Logger.debug(file.getParameterName() + ": " + file.getFile().getName());
        }
        uploadDebug("表单字段");
        uploadDebug("formParam: " + getPara("formParam"));

        uploadDebug("URL值");
        uploadDebug("urlParam: " + getPara("urlParam"));

        uploadDebug("ParameterMap:");
        EasyLogger.json(getRequest().getParameterMap());
        EasyLogger.json(((UploadRequest) getRequest()).getFileParameterMap());
        renderText("上传完成");
    }

    private void uploadDebug(String msg) {
        Logger.debug("upload", msg);
    }

    public void print() {
        renderText("print");
    }

    public void renderModel() {
        Blog blog = Blog.DAO.findFirst("select * from blog");
        renderJson(blog);
    }

    public void header() {
        renderText(getHeader("api-version"));
    }

    public void page() {
        Page<User> page = User.DAO.page(10, 1, "select *", "from user");
        renderJson(page);
    }

}
