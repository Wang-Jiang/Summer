package space.wangjiang.summer.upload;

import java.io.File;
import java.io.IOException;

/**
 * 默认文件重命名策略，在文件名后面追加1、2
 * 参考了COS的DefaultFileRenamePolicy的实现
 */
public class DefaultFileRenameStrategy implements FileRenameStrategy {

    @Override
    public File rename(File file) {
        if (createNewFile(file)) {
            return file;
        }
        String name = file.getName();
        String body;
        String ext;
        int dot = name.lastIndexOf(".");
        if (dot != -1) {
            body = name.substring(0, dot);
            ext = name.substring(dot);  // 后缀包含 "."
        } else {
            body = name;
            ext = "";
        }
        int count = 0;
        while (!createNewFile(file) && count < 9999) {
            count++;
            String newName = body + count + ext;
            file = new File(file.getParent(), newName);
        }
        return file;
    }

    private boolean createNewFile(File file) {
        try {
            //如果文件已经存在，会直接返回false
            //只有文件不能存在并且创建完成才会返回true
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
