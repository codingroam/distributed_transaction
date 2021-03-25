package com.acme.server.db_transaction.util;

import com.acme.server.db_transaction.transactional.LbTransactionManager;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClient {

    public static String get(String url) {
        String result = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Content-type", "application/json");
            httpGet.addHeader("groupId", LbTransactionManager.getCurrentGroupId());
            httpGet.addHeader("transactionCount", String.valueOf(LbTransactionManager.getTransactionCount()));
            CloseableHttpResponse response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
            response.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
