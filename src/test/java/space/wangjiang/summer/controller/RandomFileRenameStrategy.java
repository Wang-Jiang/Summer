package space.wangjiang.summer.controller;

import space.wangjiang.summer.upload.FileRenameStrategy;
import space.wangjiang.summer.util.StringUtil;

import java.io.File;
import java.io.IOException;

public class RandomFileRenameStrategy implements FileRenameStrategy {

    @Override
    public File rename(File file) {
        String name = StringUtil.getRandomString(30);
        file = new File(file.getParent(), name + ".png");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
