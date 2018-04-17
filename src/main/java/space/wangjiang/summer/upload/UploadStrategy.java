package space.wangjiang.summer.upload;

import org.apache.commons.fileupload.FileItem;

/**
 * 上传文件的策略，一般来说，主要是上传的文件大小或者后缀
 * accept返回true表示接受该文件
 */
public interface UploadStrategy {

    /**
     * 不需要手动删除文件
     * 只需要返回true或者false就可以，临时文件的删除由UploadRequest控制
     */
    boolean accept(FileItem fileItem);

}
