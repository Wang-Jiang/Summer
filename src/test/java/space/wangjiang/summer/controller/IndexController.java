package space.wangjiang.summer.controller;

import org.apache.commons.fileupload.FileItem;
import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.model.Blog;
import space.wangjiang.summer.model.Page;
import space.wangjiang.summer.model.User;
import space.wangjiang.summer.route.UrlMapping;
import space.wangjiang.summer.upload.FileRenameStrategy;
import space.wangjiang.summer.upload.UploadFile;
import space.wangjiang.summer.upload.UploadRequest;
import space.wangjiang.summer.upload.UploadStrategy;
import space.wangjiang.summer.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
        //禁止上传非png文件
        analyzeMultipartRequest(null,
                4L * 1024 * 1024,
                new PngUploadStrategy(),
                new RandomFileRenameStrategy()
        );

        List<UploadFile> uploadFile = getFiles();
        Logger.debug("文件请求");
        for (UploadFile file : uploadFile) {
            Logger.debug(file.getParameterName() + ": " + file.getFile().getName());
        }
        Logger.debug("表单字段");
        Logger.debug("formParam: " + getPara("formParam"));

        Logger.debug("URL值");
        Logger.debug("urlParam: " + getPara("urlParam"));


        Logger.debug("ParameterMap:");
        EasyLogger.json(getRequest().getParameterMap());
        EasyLogger.json(((UploadRequest) getRequest()).getFileParameterMap());
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
