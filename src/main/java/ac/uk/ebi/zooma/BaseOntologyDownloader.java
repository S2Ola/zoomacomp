package ac.uk.ebi.zooma;


import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

@Component
@Order(value = -1)
public class BaseOntologyDownloader implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

       loadOntology();
       //loadPrefixfile();
    }

    public static void loadOntology()  {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;

        try {
            url = new URL("https://raw.githubusercontent.com/EBISPOT/efo/efo2/src/ontology/efo-edit.owl");
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {


            }
        }
    }



}
