package cn.injava.my.zookeeper.rest;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import cn.injava.my.zookeeper.RestServiceInfo;
import cn.injava.my.zookeeper.config.AppConfig;
import cn.injava.my.zookeeper.model.Person;
import org.springframework.core.env.Environment;

import com.netflix.curator.x.discovery.ServiceDiscovery;
import com.netflix.curator.x.discovery.ServiceInstance;


/**
 * 真正提供rest服务的sevice
 *
 * 在sevice初始化init()时，会把服务注册到zookeeper
 *
 * Created by Green Lei on 2015/9/20 17:18.
 */
@Path( PeopleRestService.PEOPLE_PATH )
public class PeopleRestService {
    public static final String PEOPLE_PATH = "/people";

    @Inject private RestApiApplication application;
    @Inject private ServiceDiscovery< RestServiceInfo > discovery;
    @Inject private Environment environment;

    @PostConstruct
    public void init() throws Exception {
        final ServiceInstance<RestServiceInfo> instance =
                ServiceInstance.< RestServiceInfo >builder()
                        .name( "people" )
                        .payload( new RestServiceInfo( "1.0" ) )
                        .port( environment.getProperty( AppConfig.SERVER_PORT, Integer.class ) )
                        .uriSpec( application.getUriSpec( PEOPLE_PATH ) )
                        .build();

        discovery.registerService( instance );
    }

    @Produces( { MediaType.APPLICATION_JSON } )
    @GET
    public Collection<Person> getPeople( @QueryParam( "page") @DefaultValue( "1" ) final int page ) {
        return Arrays.asList(
                new Person( "Tom", "Bombadil" ),
                new Person( "Jim", "Tommyknockers" )
        );
    }
}