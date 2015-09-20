package cn.injava.my.zookeeper;

import com.netflix.curator.x.discovery.ServiceDiscovery;
import com.netflix.curator.x.discovery.ServiceInstance;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * 创建CuratorFramework 和 ServiceDiscovery两个对象，用于发现 rest 服务，
 * 这两个对象在ClientConfig类中声明
 *
 * CuratorFramework：操作zookeeper的简易类库
 * ServiceDiscovery：用于在zookeeper上发布并发现service
 *
 * Created by Green Lei on 2015/9/20 16:38.
 */
public class ClientStarter {
    public static void main( final String[] args ) throws Exception {
        try( final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext( ClientConfig.class ) ) {
            @SuppressWarnings("unchecked")
            final ServiceDiscovery< RestServiceInfo > discovery = context.getBean( ServiceDiscovery.class );
            final Client client = ClientBuilder.newClient();

            final Collection< ServiceInstance< RestServiceInfo > > services = discovery.queryForInstances( "people" );
            for( final ServiceInstance< RestServiceInfo > service: services ) {
                final String uri = service.buildUriSpec();

                final Response response = client
                        .target( uri )
                        .request( MediaType.APPLICATION_JSON )
                        .get();

                System.out.println( uri + ": " + response.readEntity( String.class ) );
                System.out.println( "API version: " + service.getPayload().getVersion() );

                response.close();
            }
        }
    }
}
