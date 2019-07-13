package space.wangjiang.summer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import space.wangjiang.summer.util.PathUtil;

public class MainServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8000);
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setDescriptor(PathUtil.getRootClassPath() + "/webapp/WEB-INF/web.xml");
        context.setResourceBase(PathUtil.getRootClassPath() + "/webapp");
        context.setParentLoaderPriority(true);
        server.setHandler(context);
        server.start();
        server.join();
    }

}
