package space.wangjiang.summer.upload;

import java.io.File;

public interface FileRenameStrategy {

    /**
     * 如果传入的文件是存在的，应该返回一个新创建的文件(调用File.createNewFile())
     */
    File rename(File file);

}
