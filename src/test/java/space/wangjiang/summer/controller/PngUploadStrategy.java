package space.wangjiang.summer.controller;

import org.apache.commons.fileupload.FileItem;
import space.wangjiang.summer.upload.UploadStrategy;

public class PngUploadStrategy implements UploadStrategy {
    @Override
    public boolean accept(FileItem fileItem) {
        if (fileItem.getSize() > 100 * 1024) {
            //最大100Kb
            return false;
        }
        if (fileItem.getName().toLowerCase().endsWith(".png")) {
            return true;
        }
        return false;
    }
}
