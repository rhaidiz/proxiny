package proxiny.gui.JSONRPC;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Data;
import com.google.api.client.util.Key;
import proxiny.gui.ProxyServerStatus;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


public class RpcClient {
    private static RpcClient me = null;


    private static HttpRequestFactory REQ_FACTORY;

    private static HttpTransport TRANSPORT;

    private static final JsonFactory JSON_FACTORY = new JacksonFactory();


    private static HttpTransport transport() {
        if (null == TRANSPORT) {
            TRANSPORT = new NetHttpTransport();
        }
        return TRANSPORT;
    }

    private static HttpRequestFactory reqFactory() {
        if (null == REQ_FACTORY) {
            REQ_FACTORY = transport().createRequestFactory();
        }
        return REQ_FACTORY;
    }


    public static RpcClient getInstance(){
        return (me==null)?new RpcClient(): me;
    }

    private int rpcCall(String method, Object params){
        GenericUrl url = new GenericUrl("http://127.0.0.1:1234/rpc");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("method",method);
        data.put("id",0);
        Object obj[]= new Object[1];
        obj[0] = params;
        data.put("params",obj);
        HttpContent content = new JsonHttpContent(JSON_FACTORY, data);
        try {
            HttpRequest req = reqFactory().buildPostRequest(url, content);
            req.setParser(new JsonObjectParser((JSON_FACTORY)));
            GenericJson response = req.execute().parseAs(GenericJson.class);
            System.out.println(response);

            Object error = response.get("error");
            Object result = response.get("result");

            if(Data.isNull(error)){
                return 0;
            }else if(!Data.isNull(error)) {
                ProxyServerStatus.getInstance().setErrorMessage(error.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int startProxy(String IpPort){
        ProxyArgs proxyArgs = new ProxyArgs();
        proxyArgs.Ip = IpPort;
        return rpcCall("Proxy.Start", proxyArgs);
    }

    public int stopProxy(){
        return rpcCall("Proxy.Stop", null);
    }

    public static class ProxyArgs {
        @Key
        public String Ip;

    }
}
