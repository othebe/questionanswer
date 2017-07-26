import common.JSONMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopicExtractor {
    public static void main(String[] args) throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/keyPhrases");

        httpPost.addHeader("Ocp-Apim-Subscription-Key", "9791891fb40a4f9daed90970b9e14ac1");
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Accept", "application/json");

        Map<String, Object> data = new HashMap<String, Object>();

        Map<String, String> document = new HashMap<String, String>();
        document.put("language", "en");
        document.put("id", "1");
        document.put("text", "Hello these are Red apples");

        List<Map> documents = new ArrayList<Map>();
        documents.add(document);

        data.put("documents", documents);

        httpPost.setEntity(new StringEntity(JSONMapper.INSTANCE.writeValueAsString(data)));

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                StringWriter writer = new StringWriter();
                IOUtils.copy(instream, writer);
                String result = writer.toString();

                int x = 1;
            } finally {
                instream.close();
            }
        }
    }
}
