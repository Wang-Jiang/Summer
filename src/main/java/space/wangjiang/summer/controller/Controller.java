package space.wangjiang.summer.controller;

import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.easylogger.json.JsonUtil;
import space.wangjiang.summer.common.SummerCommon;
import space.wangjiang.summer.config.SummerConfig;
import space.wangjiang.summer.constant.ConstantConfig;
import space.wangjiang.summer.form.Form;
import space.wangjiang.summer.route.NotRoute;
import space.wangjiang.summer.upload.*;
import space.wangjiang.summer.util.FileUtil;
import space.wangjiang.summer.util.ListUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by WangJiang on 2017/9/8.
 * 最为主要的控制器
 * 风格采用的是类似于JFinal的
 * 方法名就是路由，index是默认根路由
 * Summer中Controller不是单例的，而是每个请求都会实例化一个
 */
public abstract class Controller {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String baseViewPath;//renderJsp的基础路径，其实可以直接把整个Route传入，但是目前只需要这个

    public void init(HttpServletRequest request, HttpServletResponse response, String baseViewPath) {
        this.request = request;
        this.response = response;
        this.baseViewPath = baseViewPath;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setAttr(String name, Object value) {
        request.setAttribute(name, value);
    }

    public String getMethod() {
        return request.getMethod();
    }

    //获取请求参数系列方法

    /**
     * getParaXXX(String name)这些方法不负责null的问题
     * 如果可能会出现null，请使用getParaXXX(String name, String defaultValue)
     */
    public String getPara(String name) {
        return request.getParameter(name);
    }

    public String getPara(String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) return defaultValue;
        return value;
    }

    public int getParaToInt(String name) {
        return Integer.parseInt(request.getParameter(name));
    }

    public int getParaToInt(String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value == null) return defaultValue;
        return Integer.parseInt(value);
    }

    public boolean getParaToBool(String name) {
        return Boolean.parseBoolean(request.getParameter(name));
    }

    public boolean getParaToBool(String name, boolean defaultValue) {
        String value = request.getParameter(name);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }

    public String[] getParaValues(String name) {
        return request.getParameterValues(name);
    }

    //getHeader方法
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    //操作cookie系列方法
    public Cookie getCookieObject(String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public String getCookie(String name, String defaultValue) {
        Cookie cookie = getCookieObject(name);
        return cookie != null ? cookie.getValue() : defaultValue;
    }

    public String getCookie(String name) {
        return getCookie(name, null);
    }

    public Integer getCookieToInt(String name, Integer defaultValue) {
        String result = getCookie(name);
        return result != null ? Integer.parseInt(result) : defaultValue;
    }

    public Integer getCookieToInt(String name) {
        return getCookieToInt(name, null);
    }

    public Long getCookieToLong(String name, Long defaultValue) {
        String result = getCookie(name);
        return result != null ? Long.parseLong(result) : defaultValue;
    }

    public Long getCookieToLong(String name) {
        return getCookieToLong(name, null);
    }

    public Float getCookieToFloat(String name, Float defaultValue) {
        String result = getCookie(name);
        return result != null ? Float.parseFloat(result) : defaultValue;
    }

    public Float getCookieToFloat(String name) {
        return getCookieToFloat(name, null);
    }

    public Double getCookieToDouble(String name, Double defaultValue) {
        String result = getCookie(name);
        return result != null ? Double.parseDouble(result) : defaultValue;
    }

    public Double getCookieToDouble(String name) {
        return getCookieToDouble(name, null);
    }


    public Boolean getCookieToBool(String name, Boolean defaultValue) {
        String result = getCookie(name);
        return result != null ? Boolean.parseBoolean(result) : defaultValue;
    }

    public Boolean getCookieToBool(String name) {
        return getCookieToBool(name, null);
    }

    /**
     * 最为核心的设置cookie的方法
     *
     * @param name     cookie字段名称
     * @param value    cookie的值，支持Object类型，使用其toString()的值
     * @param maxAge   最大生存时间，单位秒，-1表示关闭浏览器失效，0表示立即失效
     * @param path     路径
     * @param domain   域名
     * @param httpOnly 是否是httpOnly的，即是否是javascript不可读的
     */
    public void setCookie(String name, Object value, int maxAge, String path, String domain, boolean httpOnly) {
        Cookie cookie = new Cookie(name, String.valueOf(value));
        cookie.setMaxAge(maxAge);
        if (path == null) {
            path = "/";
        }
        cookie.setPath(path);
        if (domain != null) {
            cookie.setDomain(domain);
        }
        cookie.setHttpOnly(httpOnly);
        this.response.addCookie(cookie);
    }

    //下面的这些方法是对上面的一个封装

    /**
     * 设置cookie，默认会话有效
     */
    public void setCookie(String name, Object value) {
        setCookie(name, value, -1, null, null, false);
    }

    public void setCookie(String name, Object value, boolean httpOnly) {
        setCookie(name, value, -1, null, null, httpOnly);
    }


    public void setCookie(String name, Object value, int maxAge) {
        setCookie(name, value, maxAge, null, null, false);
    }

    public void setCookie(String name, Object value, int maxAge, boolean httpOnly) {
        setCookie(name, value, maxAge, null, null, httpOnly);
    }

    /**
     * 删除Cookie
     */
    public void removeCookie(String name) {
        setCookie(name, null, 0, null, null, false);
    }

    //获取URL路径参数系列方法

    /**
     * 获取URL路径参数
     * 其实本质还是到Attribute查找
     * 这个就不存在默认值的问题了
     * /user//blog/36这种URL本事就有问题，就没必要处理了，直接当做404处理
     */
    public String getPathPara(String name) {
        Object value = request.getAttribute(name);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    /**
     * @see #getPathPara(String)
     */
    public Integer getPathParaToInt(String name) {
        Object value = request.getAttribute(name);
        if (value != null) {
            return Integer.valueOf(value.toString());
        }
        return null;
    }

    //文件上传下载

    /**
     * 解析文件请求
     */
    @NotRoute
    public void analyzeMultipartRequest() {
        ConstantConfig constant = SummerConfig.config.getConstantConfig();
        analyzeMultipartRequest(constant.getBaseUploadPath(), -1, constant.getUploadFileSizeMax(), null, new DefaultFileRenameStrategy());
    }

    public void analyzeMultipartRequest(String baseUploadPath, long fileSizeMax, UploadStrategy uploadStrategy, FileRenameStrategy renameStrategy) {
        analyzeMultipartRequest(baseUploadPath, -1, fileSizeMax, uploadStrategy, renameStrategy);
    }

    /**
     * 解析文件请求，你可以传入一些参数去控制上传的文件
     *
     * @param baseUploadPath 基础上传路径，支持Web相对路径和绝对磁盘路径
     * @param sizeMax        总请求的大小
     * @param fileSizeMax    单个文件最大大小
     * @param uploadStrategy 上传策略
     * @param renameStrategy 重名策略
     */
    public void analyzeMultipartRequest(String baseUploadPath, long sizeMax, long fileSizeMax, UploadStrategy uploadStrategy, FileRenameStrategy renameStrategy) {
        if (request instanceof UploadRequest) {
            return;
        }
        try {
            request = new UploadRequest(request, baseUploadPath, sizeMax, fileSizeMax, uploadStrategy, renameStrategy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取所有的文件列表
     *
     * @see UploadRequest
     */
    public List<UploadFile> getFiles() {
        analyzeMultipartRequest();
        return ((UploadRequest) request).getFiles();
    }

    public UploadFile getFile() {
        List<UploadFile> fileList = getFiles();
        if (fileList.size() > 0) return fileList.get(0);
        return null;
    }

    public UploadFile getFile(String name) {
        List<UploadFile> files = getFiles(name);
        if (ListUtil.isNotEmpty(files)) {
            return files.get(0);
        }
        return null;
    }

    /**
     * 当一个字段包含多个文件(multiple)
     */
    public List<UploadFile> getFiles(String name) {
        analyzeMultipartRequest();
        return ((UploadRequest) request).getFiles(name);
    }

    /**
     * 下载文件
     */
    public void renderFile(File file) {
        renderFile(file, file.getName());
    }

    public void renderFile(File file, String downloadName) {
        if (!file.exists()) {
            render404();
            return;
        }
        //JDK7语法，try-with-resources
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            //设置响应头，控制浏览器下载该文件
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(downloadName, "UTF-8"));
            byte buffer[] = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                //输出缓冲区的内容到浏览器，实现文件下载
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //获取属性系列方法
    @SuppressWarnings({"unchecked"})
    public <T> T getAttr(String name) {
        return (T) request.getAttribute(name);
    }

    public Object getAttr(String name, Object defaultValue) {
        Object value = request.getAttribute(name);
        if (value == null) return defaultValue;
        return value;
    }

    /**
     * 获取表单
     */
    @SuppressWarnings({"unchecked"})
    public <T extends Form> T getForm(Class<T> formClass) {
        return Form.getNewForm(this, formClass);
    }

    /**
     * 获取Attribute中的表单
     * 这个是搭配@CheckForm使用的，
     * CheckForm表单验证通过之后会把表单放到Attribute中
     *
     * @see space.wangjiang.summer.form.CheckForm
     * @see space.wangjiang.summer.route.Route#invoke(HttpServletRequest, HttpServletResponse)
     */
    @SuppressWarnings("unchecked")
    public <T> T getForm() {
        Object form = request.getAttribute(Form.ATTRIBUTE_FORM_NAME);
        if (form == null) {
            EasyLogger.error("无法获取表单，请检查是否在方法上标记了@CheckForm");
            return null;
        }
        return (T) form;
    }

    public HttpSession getSession() {
        return request.getSession();
    }

    @SuppressWarnings({"unchecked"})
    public <T> T getSessionAttr(String name) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (T) session.getAttribute(name);
        }
        return null;
    }

    public void setSessionAttr(String name, Object value) {
        HttpSession session = request.getSession();
        session.setAttribute(name, value);
    }

    //操作Session系列方法
    public void removeSessionAttr(String name) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(name);
        }
    }

    @NotRoute
    public void removeSessionAllAttrs() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Enumeration<String> names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                session.removeAttribute(name);
            }
        }
    }

    //重定向
    public void redirect(String url) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //渲染

    public void renderText(String content) {
        response.setContentType("text/text");
        print(content);
    }

    public void renderHtml(String html) {
        response.setContentType("text/html");
        print(html);
    }

    /**
     * 如果是/index.jsp，则会直接去项目目录找index.jsp
     * 如果是 index.jsp，则会先获取baseViewPath，例如是/WEB-INF/user/，则最终访问/WEB-INF/user/index.jsp
     */
    public void renderJsp(String jspPath) {
        if (baseViewPath != null && jspPath != null && jspPath.length() > 0 && jspPath.charAt(0) != '/') {
            jspPath = baseViewPath + jspPath;
        }
        try {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(jspPath);
            requestDispatcher.forward(request, response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void renderJson(String json) {
        response.setContentType("application/json");
        print(json);
    }

    public void renderJson(Object object) {
        response.setContentType("application/json");
        print(JsonUtil.toJson(object));
    }

    public void renderImg(File imgFile) {
        if (!imgFile.exists()) {
            render404();
            return;
        }
        try (FileInputStream in = new FileInputStream(imgFile);
             OutputStream out = response.getOutputStream()) {
            //控制浏览器不要缓存
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("image/" + FileUtil.getExtNameWithoutPoint(imgFile.getName()));
//            response.setContentType("image/*");   //这种头，Chrome会直接当做文件下载
            byte buffer[] = new byte[1024 * 100];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //统一的输出内容方法
    private void print(String content) {
        try (PrintWriter out = response.getWriter()) {
            out.print(content);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotRoute
    public void render404() {
        renderError(404);
    }

    @NotRoute
    public void render405() {
        renderError(405);
    }

    @NotRoute
    public void render500() {
        renderError(500);
    }

    public void renderError(int errorCode) {
        response.setStatus(errorCode);
        ConstantConfig constant = SummerConfig.config.getConstantConfig();
        String errorPage = constant.getErrorPage(errorCode);
        if (errorPage != null) {
            //使用用户自定义的页面输出
            renderJsp(errorPage);
            return;
        }
        //没有自定义错误页面，找Summer内置的错误页面
        String errorHtml = SummerCommon.getBaseErrorHtml(errorCode);
        if (errorHtml != null) {
            renderHtml(errorHtml);
            return;
        }
        //Summer中没有这个错误页面，使用服务器默认的
        try {
            response.sendError(errorCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void renderError(int errorCode, String page) {
        response.setStatus(errorCode);
        renderJsp(page);
    }

}
