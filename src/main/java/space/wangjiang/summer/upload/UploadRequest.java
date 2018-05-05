package space.wangjiang.summer.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.config.SummerConfig;
import space.wangjiang.summer.constant.ConstantConfig;
import space.wangjiang.summer.util.FileUtil;
import space.wangjiang.summer.util.ListUtil;
import space.wangjiang.summer.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class UploadRequest extends HttpServletRequestWrapper {

    private String baseUploadPath; //设置上传的绝对路径
    private long sizeMax;  //总请求最大限制，-1表示没有限制
    private long fileSizeMax; //单个文件的最大限制

    private boolean isMultipart;
    private Set<String> parameterNames = new HashSet<>(); //所有参数的字段
    private Map<String, List<String>> strParameterMap = new HashMap<>();
    private Map<String, List<UploadFile>> fileParameterMap = new HashMap<>();
    private UploadStrategy uploadStrategy;
    private FileRenameStrategy renameStrategy;

    public UploadRequest(HttpServletRequest request) throws Exception {
        this(request, null, new DefaultFileRenameStrategy());
    }

    public UploadRequest(HttpServletRequest request, UploadStrategy uploadStrategy) throws Exception {
        this(request, getConstant().getBaseUploadPath(), -1, getConstant().getUploadFileSizeMax(), uploadStrategy, new DefaultFileRenameStrategy());
    }

    public UploadRequest(HttpServletRequest request, UploadStrategy uploadStrategy, FileRenameStrategy renameStrategy) throws Exception {
        this(request, getConstant().getBaseUploadPath(), -1, getConstant().getUploadFileSizeMax(), uploadStrategy, renameStrategy);
    }

    /**
     * 包含所有参数
     *
     * @param request        原始请求
     * @param baseUploadPath 基础上传路径，支持Web相对路径和绝对磁盘路径
     * @param sizeMax        总请求大小
     * @param fileSizeMax    单个文件最大大小
     * @param uploadStrategy 上传策略
     * @param renameStrategy 重名策略
     */
    public UploadRequest(HttpServletRequest request, String baseUploadPath, long sizeMax, long fileSizeMax, UploadStrategy uploadStrategy, FileRenameStrategy renameStrategy) throws Exception {
        super(request);
        this.sizeMax = sizeMax;
        this.fileSizeMax = fileSizeMax;
        this.uploadStrategy = uploadStrategy;
        this.renameStrategy = renameStrategy;
        if (baseUploadPath == null) {
            baseUploadPath = getConstant().getBaseUploadPath();
        } else if (FileUtil.isRelativePath(baseUploadPath)) {
            //相对路径需要改为绝对路径
            baseUploadPath = request.getServletContext().getRealPath("/" + baseUploadPath);
        }
        this.baseUploadPath = baseUploadPath;
        this.isMultipart = ServletFileUpload.isMultipartContent(request); //请求是否是multipart类型
        if (isMultipart) {
            processMultipartRequest(request);
        }
    }

    /**
     * 处理文件请求
     */
    private void processMultipartRequest(HttpServletRequest request) throws Exception {
        //创建临时文件目录路径
        File tempFileDir = new File(getConstant().getUploadTempFileDir());
        if (!tempFileDir.exists()) {
            tempFileDir.mkdirs();
        }
        //保存的文件夹是否存在
        File uploadDir = new File(baseUploadPath);
        if (uploadDir.exists() && uploadDir.isFile()) {
            uploadDir.delete();
        }
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(getConstant().getUploadSizeThreshold());  //设置缓冲区大小
        factory.setRepository(tempFileDir);  //设置上传的临时文件存放路径

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(sizeMax);  //设置总请求的最大限制
        upload.setFileSizeMax(fileSizeMax); //单个文件最大大小

        analyzeFormParameterMap(upload.parseParameterMap(request)); //解析表单
        analyzeUrlParameterMap(); //解析URL参数
    }

    /**
     * 处理表单字段
     */
    private void analyzeFormParameterMap(Map<String, List<FileItem>> map) throws Exception {
        String encoding = SummerConfig.config.getConstantConfig().getEncoding();
        for (Map.Entry<String, List<FileItem>> entry : map.entrySet()) {
            String name = entry.getKey();
            List<String> strValues = new ArrayList<>();
            List<UploadFile> fileValues = new ArrayList<>();
            for (FileItem item : entry.getValue()) {
                if (item.isFormField()) {
                    //普通的表单字段，如果不设置编码，默认是ISO-8859-1
                    strValues.add(item.getString(encoding));
                    continue;
                }
                //文件表单
                String fileName = item.getName(); //原始文件名
                if (StringUtil.isEmpty(fileName)) {
                    continue;
                }
                //不同的浏览器提交的文件名不一样，有的带有路径的，可以参见item.getName()的源码注释
                //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                if (isSafeFile(fileName) && (uploadStrategy == null || uploadStrategy.accept(item))) {
                    //重名策略
                    File saveFile = renameFile(new File(baseUploadPath, fileName));
                    Logger.debug("文件保存位置：" + saveFile.getAbsolutePath());
                    item.write(saveFile);
                    fileValues.add(new UploadFile(name, fileName, saveFile));
                }
                item.delete();//删除上传时生成的临时文件
            }
            parameterNames.add(name);
            if (ListUtil.isNotEmpty(strValues)) {
                strParameterMap.put(name, strValues);
            }
            if (ListUtil.isNotEmpty(fileValues)) {
                fileParameterMap.put(name, fileValues);
            }
        }
    }

    /**
     * 重名策略，当存在重名文件时，应该返回一个新创建的文件(需要调用createNewFile)
     */
    private File renameFile(File file) throws IOException {
        if (renameStrategy == null) {
            //如果没有设置重名策略，直接创建文件
            file.createNewFile();
            return file;
        }
        return renameStrategy.rename(file);
    }

    /**
     * URL参数不在表单中，需要从原始的request中获取
     */
    private void analyzeUrlParameterMap() {
        Enumeration<String> urlParams = super.getParameterNames();
        while (urlParams.hasMoreElements()) {
            String name = urlParams.nextElement();
            String[] values = super.getParameterValues(name);
            List<String> list = strParameterMap.get(name);
            if (list != null) {
                list.addAll(Arrays.asList(values));
            } else {
                strParameterMap.put(name, Arrays.asList(values));
            }
            parameterNames.add(name);
        }
    }

    private boolean isSafeFile(String fileName) {
        fileName = fileName.trim().toLowerCase();
        if (fileName.endsWith(".jsp") || fileName.endsWith(".jspx")) {
            EasyLogger.warn("不能上传JSP或者JSPX文件");
            return false;
        }
        return true;
    }

    //正常获取表单

    @Override
    public Enumeration<String> getParameterNames() {
        if (!isMultipart) {
            return super.getParameterNames();
        }
        return Collections.enumeration(parameterNames);
    }

    @Override
    public String getParameter(String name) {
        if (!isMultipart) {
            return super.getParameter(name);
        }
        List<String> values = strParameterMap.get(name);
        if (ListUtil.isNotEmpty(values)) {
            return values.get(0);
        }
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        if (!isMultipart) {
            return super.getParameterValues(name);
        }
        List<String> values = strParameterMap.get(name);
        if (ListUtil.isNotEmpty(values)) {
            values.toArray(new String[0]);
        }
        return null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (!isMultipart) {
            return super.getParameterMap();
        }
        Map<String, String[]> map = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : strParameterMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue().toArray(new String[0]));
        }
        return map;
    }

    /**
     * 返回文件
     */
    public Map<String, List<UploadFile>> getFileParameterMap() {
        return fileParameterMap;
    }

    public List<UploadFile> getFiles() {
        List<UploadFile> files = new ArrayList<>();
        for (Map.Entry<String, List<UploadFile>> entry : fileParameterMap.entrySet()) {
            files.addAll(entry.getValue());
        }
        return files;
    }

    public List<UploadFile> getFiles(String fieldName) {
        return fileParameterMap.get(fieldName);
    }

    private static ConstantConfig getConstant() {
        return SummerConfig.config.getConstantConfig();
    }

}
