package space.wangjiang.summer.upload;

import java.io.File;

/**
 * Created by WangJiang on 2017/9/14.
 * 上传文件的封装
 */
public class UploadFile {

    private String parameterName;
    private String fileName; //上传的原始文件名，因为重名策略，实际的文件名可能会变动
    private File file;

    public UploadFile(String parameterName, String fileName, File file) {
        this.parameterName = parameterName;
        this.fileName = fileName;
        this.file = file;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        return file;
    }
}
