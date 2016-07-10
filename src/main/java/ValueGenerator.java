import org.json.JSONObject;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Random;

/**
 * Created by rasika on 7/10/16.
 */
class ValueGenerator  {

    String[] keys,file;
    String[][] values;
    int numberOfKeys;

    ValueGenerator() throws IOException {

        Integer lines = 0;
        String currentLine;
        BufferedReader reader     = null,
                       fileReader = null;
        LineNumberReader lineNumberReader = null;

        try {
            reader = new BufferedReader(new FileReader("/home/user/properties")); // /path/to/keys file

            currentLine  = reader.readLine(); //read the first line to get number of keys
            numberOfKeys = Integer.parseInt(currentLine);

            keys   = new String[numberOfKeys];
            file   = new String[numberOfKeys];
            values = new String[numberOfKeys][];

            for(int count = 0; count < numberOfKeys; count++) {

                keys[count] = reader.readLine();  //get the name of the key or nested key, to be modified
                file[count] = reader.readLine();  //location of the file from which values for the keys will be read

                fileReader = new BufferedReader(new FileReader(file[count]));

                lineNumberReader = new LineNumberReader(new FileReader(file[count]));

                //getting the number of lines present in the file
                lineNumberReader.skip(Long.MAX_VALUE);
                lines = lineNumberReader.getLineNumber();
                lineNumberReader.close();

                //create this array with size as the number of lines
                //plus one to store this number, because we need it to use it later
                values[count] = new String[lines + 1];
                values[count][0] = lines.toString();

                //store all possible values of the key in the array
                for(int lineNumber = 1; lineNumber <= lines; lineNumber++) {
                    values[count][lineNumber] = fileReader.readLine();
                }
            }
        } finally {
            lineNumberReader.close();
            fileReader.close();
            reader.close();
        }
    }

    public JSONObject generateValue(JSONObject jsonObj)
    {
        Random randomizer = new Random();
        int counter, length, previousIndex, maxValues, random;
        JSONObject newObj = null;
        String subKey = "";

        try {

            String value = "";

            for(int count = 0; count < numberOfKeys; count++) {

                newObj = jsonObj;
                previousIndex = 0;
                counter = 0;

                length= keys[count].length();

                while(counter != length) {
                    counter = keys[count].indexOf('.',previousIndex);

                    //extract objects from the parent JSON object, depending on the key given
                    //for example, the key can be home.directory.subdirectory
                    if(counter != -1) {
                        subKey = keys[count].substring(previousIndex,counter);
                        newObj = newObj.getJSONObject(subKey);
                        counter++;
                        previousIndex = counter;
                    } else {
                        subKey  = keys[count].substring(previousIndex,length);
                        counter = length;
                    }
                }
                //get a random value from the list and set it in the object
                maxValues = Integer.parseInt(values[count][0]);
                random = randomizer.nextInt(maxValues);
                value = values[count][random + 1];

                newObj.put(subKey, value);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }
}