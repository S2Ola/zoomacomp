package ac.uk.ebi.zooma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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


@Component
@Order(value = 2)
public class QueryWriter implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger( TermsInEfoSparqlQuery.class );

    private String homeDir = System.getProperty( "user.home" );
    private String scatlasFolder = homeDir+"/Documents/SCATLAS_FOLDER";
    private String actualOntologyDir = scatlasFolder +"/ACTUAL_ONTOLOGY/";
    private String sparqlDir =  "/Documents/SCATLAS_FOLDER/SPARQL/";

    @Autowired
    private UtilityService utilityService;

    @Override
    public void run(String... args) throws Exception {

        loadPrefixFile();
    }



    public void loadPrefixFile() throws IOException {

        String ontologyUrl = utilityService.parseFile( actualOntologyDir + "OntologyUrl.txt" );
        List <String> urls = (Arrays.asList( ontologyUrl.split( "," ) ));

        /**
         * Generate the ... Files
         */
        for (String url : urls) {
            query(url);
        }


        /**
         *  Download EFO file
         */

        URL website = null;
        try {
            website = new URL("https://raw.githubusercontent.com/EBISPOT/efo/efo2/src/ontology/efo-edit.owl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(scatlasFolder +"/efo.owl");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);


        String efoOwl = scatlasFolder +"/efo-edit.owl";

        try {
            Files.createDirectories( Paths.get(scatlasFolder +"/RESULTS/"));
        }catch (Exception e){}

        /**
         * Exceute Robot Command on Ontology sparql files
         */
        for (String url : urls) {

            String[] temp = url.split("/");
            String prefix = temp[temp.length-1];

            efoOwl = scatlasFolder +"/efo.owl";
            String sparqlFile = scatlasFolder +"/SPARQL/"+prefix+".sparql";
            String resultFile = scatlasFolder +"/results/"+prefix+"_2.txt";

            String command = "robot query --input "+efoOwl+" --query "+sparqlFile+" "+resultFile;
            utilityService.executeCommand(command);

        }


    }

/****************************************************************************************
 * The generates the sparql query for all the ontologies available in the data sheet
 *
 * **************************************************************************************
 * */

    public void query(String ontologyUrl) {


        String data = "#This sparql query helps to retrieve all terms in EFO that has the ontology prefix IDs +\n" +
                "# Written by Olamidipupo Ajigboye +\n" +
                "# email: dipo@ebi.ac.uk +\n" +
                "# 15-01-2019 +\n\n" +

                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                "PREFIX dcterms: <http://purl.org/dc/terms/>\n" +
                "PREFIX dbpedia2: <http://dbpedia.org/property/>\n" +
                "PREFIX dbpedia: <http://dbpedia.org/>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n\n" +
                "SELECT DISTINCT ?terms\n" +
                "\n" +
                "WHERE {\n" +
                "   ?terms a owl:Class .\n" +
                "   bind(concat(str(?terms)) as ?n) .\n" +
                "   FILTER (regex(?n, \""+ontologyUrl+"_\", \"i\")) .\n" +
                "   }\n";

        String[] temp = ontologyUrl.split("/");
        String prefix = temp[temp.length-1];
        utilityService.writeToFile(data, sparqlDir, prefix+".sparql" );

        logger.info("*************** WRITING SPARQL FILE ************ "+ontologyUrl);

    }


}

