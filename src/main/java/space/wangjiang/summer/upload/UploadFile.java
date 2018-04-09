package space.wangjiang.summer.upload;

import java.io.File;

/**
 * Created by WangJiang on 2017/9/14.
 * 上传文件的封装
 */
public class UploadFile {

    private File file;
    private String parameterName;

    public UploadFile(File file, String parameterName) {
        this.file = file;
        this.parameterName = parameterName;
    }

    public File getFile() {
        return file;
    }

    public String getParameterName() {
        return parameterName;
    }
}
