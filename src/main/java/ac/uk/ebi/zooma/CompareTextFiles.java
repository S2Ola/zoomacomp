package ac.uk.ebi.zooma;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

@Component
@Order(value = 3)
public class CompareTextFiles implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger( TermsInEfoSparqlQuery.class );
    private String homeDir = System.getProperty( "user.home" );
    private String scatlasFolder = homeDir+"/Documents/SCATLAS_FOLDER/";
    private String actualOntologyDir = scatlasFolder +"/ACTUAL_ONTOLOGY/";
    private String compareDir = homeDir+ "/Documents/SCATLAS_FOLDER/RESULTS/";
    private String outCompareDir = scatlasFolder +"/COMPARED/";


    @Autowired
    private UtilityService utilityService;
    private List <String> list1;
    private List <String> list2;

    @Override
    public void run(String... args) throws Exception {

        loadPrefixFile();
    }


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

            uniqueFileContents(prefix);

           compareFiles( prefix );

            logger.info( "This is the Ontology prefix: " + prefix );

        }
    }

    public void uniqueFileContents (String prefix){

            Path input = Paths.get(compareDir + prefix+"_2.txt" );
            Path output = Paths.get(compareDir + prefix+"_3.txt" );

            try {
                List<String> words = getDistinctSortedWords(input);
                Files.write(output, words, UTF_8);
            } catch (IOException e) {
                //log error and/or warn user
            }
        }

        public static List<String> getDistinctSortedWords(Path path) throws IOException {
            try(Stream<String> lines = Files.lines(path, UTF_8)) {
                return lines.map(String::trim)
                        .filter(s -> !s.isEmpty()) // If keyword is not empty, collect it.
                        .distinct()
                        .sorted()
                        .collect(toList());
            }
        }

     public void compareFiles (String prefix) throws Exception {

         // br1 = curated_terms, br2 = efo_terms, br3 =    ,     br4 =
         //BufferedReader br1 = null;

         BufferedReader br1 = null;
         BufferedReader br2 = null;
         BufferedWriter bw3 = null;
         String sCurrentLine;
         int linelength;
         HashMap <String, Integer> expectedrecords = new HashMap <String, Integer>();
         HashMap <String, Integer> actualrecords = new HashMap <String, Integer>();


         try {
             Files.createDirectories( Paths.get( scatlasFolder + "/COMPARED/" ) );
         } catch (Exception e) {
         }


         br1 = new BufferedReader( new FileReader( new File( actualOntologyDir + prefix + ".txt" ) ) );
         // br1 = new BufferedReader( new FileReader( new File (actualOntologyDir+ "BTO.txt")) );
         // br2 = new BufferedReader( new FileReader( new File( compareDir+"BTO_2.txt" ) ) );
         br2 = new BufferedReader( new FileReader( new File( compareDir + prefix + "_3.txt" ) ) );

         while ((sCurrentLine = br1.readLine()) != null) {
             if (expectedrecords.containsKey( sCurrentLine )) {
                 expectedrecords.put( sCurrentLine, expectedrecords.get( sCurrentLine ) + 1 );
             } else {
                 expectedrecords.put( sCurrentLine, 1 );
             }
         }
         while ((sCurrentLine = br2.readLine()) != null) {
             if (expectedrecords.containsKey( sCurrentLine )) {
                 int expectedCount = expectedrecords.get( sCurrentLine ) - 1;
                 if (expectedCount == 0) {
                     expectedrecords.remove( sCurrentLine );
                 } else {
                     expectedrecords.put( sCurrentLine, expectedCount );
                 }
             } else {
                 if (actualrecords.containsKey( sCurrentLine )) {
                     actualrecords.put( sCurrentLine, actualrecords.get( sCurrentLine ) + 1 );
                 } else {
                     actualrecords.put( sCurrentLine, 1 );
                 }
             }
         }


         bw3 = new BufferedWriter( new FileWriter( new File( outCompareDir + prefix + "_comp_in.txt" ) ) );
         bw3.write( "Records which are in " + prefix + " but not present in EFO\n" );
         for (String key : expectedrecords.keySet()) {
             for (int i = 0; i < expectedrecords.get( key ); i++) {
                 bw3.write( key );
                 bw3.newLine();
             }
         }
        /*bw3.write( "Records which are not present in EFO\n" );
         for (String key : actualrecords.keySet()) {
             for (int i = 0; i < actualrecords.get( key ); i++) {
                 bw3.write( key );
                 bw3.newLine();
             }
         }*/
         bw3.flush();
         bw3.close();


     }
}







