import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

/**
 * Created by rasika on 7/10/16.
 */
public class RequestSimulator{

    ThreadPoolExecutor executor;
    Random randomizer;

    public RequestSimulator () {
        randomizer = new Random();

        //value of thread pool that gives the required throughput
        executor = new ThreadPoolExecutor(50, 75, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public void generateRequest (File sampleReqFile, int numRequests) throws Exception {
        // get JSON object and generate unique requests...

        double amount;
        float minX = 0.9f, maxX = 2.1f;
        String sampleRequest, line,id;
        long startTime = 0; //used for calculation of the number of requests actually generated

        ValueGenerator value = new ValueGenerator();  //setup for generation of values in the JSON object

        //read the sample JSON request
        BufferedReader reader = new BufferedReader(new FileReader(sampleReqFile));
        sampleRequest = ""; //set value to blank, because we are concatenating
        while (true) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            sampleRequest += line;
        }

        try {

            JSONObject jsonObj = new JSONObject(sampleRequest);

            startTime = Calendar.getInstance().getTimeInMillis();

            for (int count = 0; count < numRequests; count++) {

                //JSONObject jsonObj = new JSONObject(sampleRequest);
                //String id = jsonObj.getString("id");
                jsonObj = value.generateValue(jsonObj);

                id = UUID.randomUUID().toString().toUpperCase();
                jsonObj.put("id", id);

                if((count % 2000) == 0)
                {
                    amount = randomizer.nextFloat() * (maxX - minX) + minX;
                    amount = Math.ceil(amount*100) / 100;
                    jsonObj.put("click","true");
                    jsonObj.put("amount",amount);
                }

                Crawler crawlObj = new Crawler(jsonObj.toString());
                executor.execute(crawlObj);
            }
        } finally {
            reader.close();
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

            long endTime = Calendar.getInstance().getTimeInMillis();
            long diff = endTime - startTime;
            System.out.println("Final Difference is " + diff);

            System.out.println("Count : " + Crawler.counter.get()); //total number of requests executed
        }
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: RequestSimulator <Sample JSON File Path> <numRequests>");
            return;
        }

        RequestSimulator simulator = new RequestSimulator();
        File sampleReqFile = new File (args[0]);
        int numRequests = Integer.parseInt(args[1]);

        try {
            simulator.generateRequest(sampleReqFile, numRequests);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}