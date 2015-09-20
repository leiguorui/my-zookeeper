package cn.injava.my.zookeeper;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.ExponentialBackoffRetry;
import com.netflix.curator.x.discovery.ServiceDiscovery;
import com.netflix.curator.x.discovery.ServiceDiscoveryBuilder;
import com.netflix.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rest客户端的配置
 *
 * 包括zookeeper的ip地址等
 *
 * Created by Green Lei on 2015/9/20 16:30.
 */
@Configuration
public class ClientConfig {
    private static final String ZK_HOST = "localhost";

    @Bean( initMethod = "start", destroyMethod = "close" )
    public CuratorFramework curator() {
        return CuratorFrameworkFactory.newClient(ZK_HOST, new ExponentialBackoffRetry(1000, 3));
    }

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
}
