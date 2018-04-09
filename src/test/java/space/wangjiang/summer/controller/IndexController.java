package space.wangjiang.summer.controller;

import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.model.Page;
import space.wangjiang.summer.route.UrlMapping;
import space.wangjiang.summer.model.Blog;
import space.wangjiang.summer.model.User;
import space.wangjiang.summer.upload.UploadFile;

import java.io.File;

@SuppressWarnings("unused")
public class IndexController extends Controller {

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
        UploadFile uploadFile = getFile();
        EasyLogger.debug(uploadFile.getParameterName());
        renderText("上传完成");
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
