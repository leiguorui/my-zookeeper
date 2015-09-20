package cn.injava.my.zookeeper.rest;

import cn.injava.my.zookeeper.config.AppConfig;
import com.netflix.curator.x.discovery.UriSpec;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * 配置服务所运行的 host 和 port
 *
 * Created by Green Lei on 2015/9/20 17:14.
 */
@ApplicationPath( RestApiApplication.APPLICATION_PATH )
public class RestApiApplication extends Application {
    public static final String APPLICATION_PATH = "api";

    @Inject
    Environment environment;

    public UriSpec getUriSpec( final String servicePath ) {
        return new UriSpec(
                String.format( "{scheme}://%s:{port}/%s/%s%s",
                        environment.getProperty( AppConfig.SERVER_HOST ),
                        AppConfig.CONTEXT_PATH,
                        APPLICATION_PATH,
                        servicePath
                ) );
    }
}