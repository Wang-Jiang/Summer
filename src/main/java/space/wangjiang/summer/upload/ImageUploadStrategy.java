package space.wangjiang.summer.upload;

import org.apache.commons.fileupload.FileItem;
import space.wangjiang.summer.util.FileUtil;

public class ImageUploadStrategy implements UploadStrategy {

    @Override
    public boolean accept(FileItem fileItem) {
        return FileUtil.isImageName(fileItem.getName());
    }

}
