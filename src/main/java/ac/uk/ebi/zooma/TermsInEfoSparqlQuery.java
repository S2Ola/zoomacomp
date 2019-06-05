package ac.uk.ebi.zooma;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(value = 2)
public class TermsInEfoSparqlQuery<OntologyPrefix> implements CommandLineRunner {

    private String homeDir = System.getProperty( "user.home" );
    private Logger logger = LoggerFactory.getLogger( TermsInEfoSparqlQuery.class );
    private String actualOntologyDir = homeDir + "/Documents/ACTUAL_ONTOLOGY/";
    private String sparqlDir = homeDir + "/Documents/ACTUAL_ONTOLOGY/Sparql/";


    @Autowired
    private UtilityService utilityService;

    @Override
    public void run(String... args) throws Exception {

        loadPrefixFile();
        OntologyPrefixCount();

    }

    public void loadPrefixFile() throws Exception {

        String ontologyPrefix = utilityService.parseFile( actualOntologyDir + "OntologyUrl.txt" );


        List <String> prefixes = (Arrays.asList( ontologyPrefix.split( "," ) ));

        for (String prefix : prefixes) {

        }

        logger.info( "1 loading: {}", prefixes );
    }

    private void OntologyPrefixCount() {

        String homeDir = System.getProperty( "user.home" );
        String actualOntologyDir = homeDir + "/Documents/ACTUAL_ONTOLOGY/";

        int count = 0;

        File file = new File( actualOntologyDir + "OntologyUrl.txt" );
        FileInputStream fis = null;
        try {
            fis = new FileInputStream( file );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] bytesArray = new byte[(int) file.length()];
        try {
            fis.read( bytesArray );
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = new String( bytesArray );
        String[] data = s.split( "," );
        for (int i = 0; i < data.length; i++) {
            count++;
        }


        logger.info( "Number of prefixes in the given file are " + count );
    }
}







