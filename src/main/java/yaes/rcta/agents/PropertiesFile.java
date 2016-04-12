package yaes.rcta.agents;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import yaes.ui.text.TextUi;

public class PropertiesFile {

    private HashMap<String, String> prop = new HashMap<String, String>();

    public PropertiesFile() {

    }

    public String getProperty(String key) {
        return prop.get(key);
    }

    public void readProperties(String filename) {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = this.getClass().getResourceAsStream(
                    "/properties/" + filename + ".properties");
            /*
             * input = this.getClass() .getResourceAsStream(filename +
             * ".properties");
             */
            prop.load(input);

            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                this.prop.put(key, value);
                //TextUi.println("Key : " + key + ", Value : " + value);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) {

        PropertiesFile prop = new PropertiesFile();
        prop.readProperties("HR-1");
        TextUi.println(prop.getProperty("name"));
        // TextUi.println(DefaultOrientation.valueOf(2));

    }
}
