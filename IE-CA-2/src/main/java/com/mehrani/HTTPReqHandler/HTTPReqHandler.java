package com.mehrani.HTTPReqHandler;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HTTPReqHandler {
    public String httpGetRequest(String url) throws Exception {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet getRequest = new HttpGet(url);
            CloseableHttpResponse httpResponse = httpClient.execute(getRequest);
            HttpEntity httpEntity = httpResponse.getEntity();
            String data = "";
            if (httpEntity != null)
                data = EntityUtils.toString(httpEntity);
            return data;
        }
    }
}
