package ac.uk.ebi.zooma;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


//@Component
@Order(value = 4)
public class OlsOntologyDownloader implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger( TermsInEfoSparqlQuery.class );
    private String homeDir = System.getProperty( "user.home" );
    private String scatlasFolder = homeDir+"/Documents/SCATLAS_FOLDER/";
    private String actualOntologyDir = scatlasFolder +"/ACTUAL_ONTOLOGY/";
   // private String compareDir = homeDir+ "/Documents/SCATLAS_FOLDER/RESULTS/";
    private String outCompareDir = scatlasFolder +"/COMPARED/";


    @Autowired
    private UtilityService utilityService;

    @Override
    public void run(String... args) throws Exception {

            loadPrefixFile();
    }

    /*
    Read in the ontolgoy prefixes
     */

    public void loadPrefixFile() throws Exception {

        String ontologyUrl = utilityService.parseFile( actualOntologyDir + "OntologyUrl.txt" );
        List <String> urls = (Arrays.asList( ontologyUrl.split( "," ) ));


        logger.info( "This the Ontology : " + ontologyUrl );


        /**
         * Generate the ... Files
         */

        for (String url : urls) {
            String[] temp = url.split( "/" );
            String prefix = temp[temp.length - 1];

            loadPrefixFile(prefix);

            logger.info( "This is the Ontology prefix: " + prefix );

        }
    }



    public void loadPrefixFile(String prefix) throws IOException {

        String ontologyUrl = utilityService.parseFile( actualOntologyDir + "OntologyUrl.txt" );
        List <String> urls = (Arrays.asList( ontologyUrl.split( "," ) ));

        /**
         * Generate the ... Files
         */
        for (String url : urls) {
            loadOntology();
        }

    }
        /**
         *  Download EFO file
         */






        public void loadOntology() throws IOException {

            URL website = null;
            try {
                website = new URL( "https://raw.githubusercontent.com/EBISPOT/efo/efo2/src/ontology/efo-edit.owl" );
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ReadableByteChannel rbc = Channels.newChannel( website.openStream() );
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream( scatlasFolder + "/efo.owl" );

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            fos.getChannel().transferFrom( rbc, 0, Long.MAX_VALUE );


            String efoOwl = scatlasFolder + "/efo-edit.owl";

            try {
                Files.createDirectories( Paths.get( scatlasFolder + "/RESULTS/" ) );
            } catch (Exception e) {
            }

            /**
             * Exceute Robot Command on Ontology sparql files
             */
            /*String[] urls;
            for (String url : urls) {

                String[] temp = url.split( "/" );
                String prefix = temp[temp.length - 1];

                efoOwl = scatlasFolder + "/efo.owl";
                String sparqlFile = scatlasFolder + "/SPARQL/" + prefix + ".sparql";
                String resultFile = scatlasFolder + "/results/" + prefix + "_2.txt";

                String command = "robot query --input " + efoOwl + " --query " + sparqlFile + " " + resultFile;
                utilityService.executeCommand( command );

            }*/


        }
}