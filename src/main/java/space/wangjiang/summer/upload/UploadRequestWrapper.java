package space.wangjiang.summer.upload;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.config.SummerConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by WangJiang on 2017/9/14.
 * 用于上传文件
 */
public class UploadRequestWrapper extends HttpServletRequestWrapper {

    private MultipartRequest multipartRequest;
    private List<UploadFile> fileList = new LinkedList<>();
    //COS中的默认的命名策略DefaultFileRenamePolicy，只要存在重名，它会在文件名后面加上1.2.3
    private static FileRenamePolicy renamePolicy = new DefaultFileRenamePolicy();

    public UploadRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        String savePath = request.getServletContext().getRealPath(SummerConfig.config.getConstantConfig().getBaseUploadPath());
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //限定所有文件的总大小，默认为1MB
        int maxPostSize = 10 * 1024 * 1024;
        //若未显式命名策略，上传同名文件，则先前的文件会被后面的文件覆盖掉
        this.multipartRequest = new MultipartRequest(request, savePath, maxPostSize, "UTF-8", renamePolicy);

        //获取文件列表
        Enumeration fileNames = multipartRequest.getFileNames();
        while (fileNames.hasMoreElements()) {
            String paramName = (String) fileNames.nextElement();
            File uploadFile = multipartRequest.getFile(paramName);
            //检查文件后缀，不能上传JSP文件或者JSPX文件
            if (uploadFile != null && isSafeFile(uploadFile)) {
                fileList.add(new UploadFile(uploadFile, paramName));
            }
        }
    }

    private boolean isSafeFile(File uploadFile) {
        String fileName = uploadFile.getName().trim().toLowerCase();
        if (fileName.endsWith(".jsp") || fileName.endsWith(".jspx")) {
            EasyLogger.warn("不能上传JSP或者JSPX文件");
            uploadFile.delete();
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<String> getParameterNames() {
        return multipartRequest.getParameterNames();
    }

    @Override
    public String getParameter(String name) {
        return multipartRequest.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        return multipartRequest.getParameterValues(name);
    }

    public List<UploadFile> getFiles() {
        return fileList;
    }
}
