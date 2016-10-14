package test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import test.service.UserService;
import test.service.UserServiceImpl;
import test.servlet.UserServlet;

/**
 * Created by nyapparova on 11.10.2016.
 */
public class App {
    public static void main(String[] args) throws Exception {
        UserService us = new UserServiceImpl();
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new UserServlet(us)), "/*");
        server.start();
        server.join();
    }
}
