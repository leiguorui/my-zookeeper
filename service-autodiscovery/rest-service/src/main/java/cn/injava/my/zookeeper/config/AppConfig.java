package cn.injava.my.zookeeper.config;

import cn.injava.my.zookeeper.RestServiceInfo;
import cn.injava.my.zookeeper.rest.PeopleRestService;
import cn.injava.my.zookeeper.rest.RestApiApplication;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.ExponentialBackoffRetry;
import com.netflix.curator.x.discovery.ServiceDiscovery;
import com.netflix.curator.x.discovery.ServiceDiscoveryBuilder;
import com.netflix.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.endpoint.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Arrays;

/**
 * 服务器的配置
 *
 * 包括zookeeper的地址等
 *
 * 服务器启动之后，会跟zookeeper持续保持连接，当连接断掉之后，则zookeeper认为该服务已不可用
 *
 * Created by Green Lei on 2015/9/20 16:57.
 */
@Configuration
public class AppConfig {
    private static final String ZK_HOST = "localhost";

    public static final String SERVER_PORT = "server.port";
    public static final String SERVER_HOST = "server.host";
    public static final String CONTEXT_PATH = "rest";

    @Bean( destroyMethod = "shutdown" )
    public SpringBus cxf() {
        return new SpringBus();
    }

    @Bean @DependsOn( "cxf" )
    public Server jaxRsServer() {
        JAXRSServerFactoryBean factory = RuntimeDelegate.getInstance().createEndpoint( jaxRsApiApplication(), JAXRSServerFactoryBean.class );
        factory.setServiceBeans( Arrays.< Object >asList(peopleRestService()) );
        factory.setAddress( factory.getAddress() );
        factory.setProviders( Arrays.< Object >asList( jsonProvider() ) );
        return factory.create();
    }

    /**
     * 为了连接到zookeeper，创建一个 CuratorFramework 对象
     * @return
     */
    @Bean( initMethod = "start", destroyMethod = "close" )
    public CuratorFramework curator() {
        return CuratorFrameworkFactory.newClient(ZK_HOST, new ExponentialBackoffRetry(1000, 3));
    }

    /**
     * 创建一个 ServiceDiscovery 对象，把服务器的信息 RestServiceInfo 发布到zookeeper上，用于发现服务
     * @return
     */
    @Bean( initMethod = "start", destroyMethod = "close" )
    public ServiceDiscovery< RestServiceInfo > discovery() {
        JsonInstanceSerializer< RestServiceInfo > serializer =
                new JsonInstanceSerializer< RestServiceInfo >( RestServiceInfo.class );

        return ServiceDiscoveryBuilder.builder(RestServiceInfo.class)
                .client( curator() )
                .basePath( "services" )
                .serializer( serializer )
                .build();
    }

    @Bean
    public RestApiApplication jaxRsApiApplication() {
        return new RestApiApplication();
    }

    @Bean
    public PeopleRestService peopleRestService() {
        return new PeopleRestService();
    }

    @Bean
    public JacksonJsonProvider jsonProvider() {
        return new JacksonJsonProvider();
    }
}
