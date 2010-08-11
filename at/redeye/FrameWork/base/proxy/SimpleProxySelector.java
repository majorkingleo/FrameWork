/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author martin
 */
public class SimpleProxySelector extends ProxySelector
{
    ArrayList<Proxy> proxy_list;

    public SimpleProxySelector( String host, int port )
    {
        super();
        
        Proxy proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
        
        proxy_list.add(proxy);
    }

    @Override
    public List<Proxy> select(URI uri) {
        return proxy_list;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        
    }


}
