import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rasika on 7/10/16.
 */
class Crawler implements Runnable {

    String json;
    public static AtomicInteger counter = new AtomicInteger();

    public Crawler (String json) {
        this.json = json;
    }

    public void run() {
        try {

            URL url = new URL ("http://localhost:8080/test/test.jsp?");

            // post request and return...
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("x-openrtb-version", "2.1");
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            DataOutputStream stream = new DataOutputStream (conn.getOutputStream());
            stream.writeBytes(json);

            stream.flush();
            stream.close();

            if(conn.getResponseCode() == 200)
            {
                int curr = counter.incrementAndGet();
                if (curr % 1000 == 0) {
                    System.out.println("Processed " + curr + " requests, response code " + conn.getResponseCode());
                }
            }
            conn.disconnect();
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
