package com.github.ford;


import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 启动类
 *
 * @author ford
 */
public class Launcher {

    private static final String CONTEXT = "/";
    private static int PORT = 8080;
    private static String DEFAULT_WEBAPP_PATH = "src/main/webapp";
    private static Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        logger.info("server start...");
        if (args != null && args.length == 2) {
            try {
                DEFAULT_WEBAPP_PATH = args[0];
                PORT = Integer.parseInt(args[1]);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                System.exit(-1);
            }
        }
        new Launcher().startJetty();
    }

    /**
     * 创建用于开发运行调试的Jetty Server, 以src/main/webapp为Web应用目录.
     */
    public static Server createServerInSource(int port, String contextPath) {
        //最大连接数
        QueuedThreadPool queuedThreadPool=new QueuedThreadPool(3000);
        Server server = new Server(queuedThreadPool);
        server.setStopAtShutdown(true);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        connector.setReuseAddress(false);
        server.setConnectors(new Connector[] { connector });
        WebAppContext webContext = new WebAppContext(DEFAULT_WEBAPP_PATH, contextPath);
        webContext.setDescriptor(DEFAULT_WEBAPP_PATH + "/WEB-INF/web.xml");
        webContext.setResourceBase(DEFAULT_WEBAPP_PATH);
        webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        server.setHandler(webContext);
        return server;
    }

    /**
     * 启动jetty服务
     */
    public void startJetty() {
        try {
            final Server server = Launcher.createServerInSource(PORT, CONTEXT);
//            server.stop();
            server.start();
            server.join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(-1);
        }
    }

}
