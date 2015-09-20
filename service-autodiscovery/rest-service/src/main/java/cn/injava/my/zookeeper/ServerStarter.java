package cn.injava.my.zookeeper;

import cn.injava.my.zookeeper.config.AppConfig;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


/**
 * Server的启动程序
 *
 * 一台主机上可以会部署多个server，所以启动时要指定参数
 *
 * Created by Green Lei on 2015/9/20 16:54.
 */
public class ServerStarter {
    public static void main( final String[] args ) throws Exception {
        if( args.length != 1 ) {
            System.out.println( "Please provide port number" );
            return;
        }

        final int port = Integer.valueOf( args[ 0 ] );
        final Server server = new Server( port );

        System.setProperty( AppConfig.SERVER_PORT, Integer.toString( port ) );
        System.setProperty( AppConfig.SERVER_HOST, "localhost" );

        // Register and map the dispatcher servlet
        final ServletHolder servletHolder = new ServletHolder( new CXFServlet() );
        final ServletContextHandler context = new ServletContextHandler();
        context.setContextPath( "/" );
        context.addServlet( servletHolder, "/" + AppConfig.CONTEXT_PATH + "/*" );
        context.addEventListener( new ContextLoaderListener() );

        context.setInitParameter( "contextClass", AnnotationConfigWebApplicationContext.class.getName() );
        context.setInitParameter( "contextConfigLocation", AppConfig.class.getName() );

        server.setHandler( context );
        server.start();
        server.join();
    }
}
