package cn.injava.my.zookeeper;

/**
 * rest服务器的信息
 *
 * 包括该服务的版本号、可以承受的负载等
 *
 * Created by Green Lei on 2015/9/20 16:18.
 */
public class RestServiceInfo {
    private String version;

    public RestServiceInfo() {
    }

    public RestServiceInfo( final String version ) {
        this.version = version;
    }

    public void setVersion( final String version ) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
