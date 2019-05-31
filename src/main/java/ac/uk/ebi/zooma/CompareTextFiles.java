package ac.uk.ebi.zooma;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
@Order(value = 3)
public class CompareTextFiles implements CommandLineRunner {

    private String homeDir = System.getProperty( "user.home" );
    private Logger logger = LoggerFactory.getLogger( CompareTextFiles.class );

    private String actualOntologyDir = homeDir+"/Documents/SCATLAS_FOLDER/ACTUAL_ONTOLOGY/";
    private String expectedOntologyDir = homeDir+"/Documents/SCATLAS_FOLDER/results/";

    @Autowired
    private UtilityService utilityService;

    @Override
    public void run(String... args) throws Exception {

        loadPrefixFile();

    }


    public void loadPrefixFile() throws Exception{

        String ontologyPrefix =  utilityService.parseFile(actualOntologyDir+"OntologyPrefix.txt");
        List<String> prefixes = Arrays.asList( ontologyPrefix.split( "," ) );

        for (String prefix : prefixes){

            compareFiles(prefix);
        }
    }


    public void compareFiles(String fileName) throws Exception {

        //logger.info( "Loading Ontology Prefix File for: {}", fileName);

        BufferedReader br1 = null;
        BufferedReader br2 = null;
        BufferedWriter bw3 = null;
        String sCurrentLine;
        int linelength;

        HashMap<String, Integer> expectedrecords = new HashMap<String, Integer>();
        HashMap<String, Integer> actualrecords = new HashMap<String, Integer>();



        br1 = new BufferedReader(new FileReader(actualOntologyDir+fileName+".txt"));
        br2 = new BufferedReader(new FileReader(expectedOntologyDir+fileName+"+_2.txt"));

        while ((sCurrentLine = br1.readLine()) != null) {
            if (expectedrecords.containsKey(sCurrentLine)) {
                expectedrecords.put(sCurrentLine, expectedrecords.get(sCurrentLine) + 1);
            } else {
                expectedrecords.put(sCurrentLine, 1);
            }
        }
        while ((sCurrentLine = br2.readLine()) != null) {
            if (expectedrecords.containsKey(sCurrentLine)) {
                int expectedCount = expectedrecords.get(sCurrentLine) - 1;
                if (expectedCount == 0) {
                    expectedrecords.remove(sCurrentLine);
                } else {
                    expectedrecords.put(sCurrentLine, expectedCount);
                }
            } else {
                if (actualrecords.containsKey(sCurrentLine)) {
                    actualrecords.put(sCurrentLine, actualrecords.get(sCurrentLine) + 1);
                } else {
                    actualrecords.put(sCurrentLine, 1);
                }
            }
        }

        // expected is left with all records not present in actual
        // actual is left with all records not present in expected
        bw3 = new BufferedWriter(new FileWriter(new File("/Users/dipo/desktop/SingleCellAtlasData/inEfo4.txt")));
        bw3.write("Records which are in single cell but not present in 1\n");
        for (String key : expectedrecords.keySet()) {
            for (int i = 0; i < expectedrecords.get(key); i++) {
                bw3.write(key);
                bw3.newLine();
            }
        }
        bw3 = new BufferedWriter(new FileWriter(new File("/Users/dipo/desktop/SingleCellAtlasData/not_in_efo3.txt")));
        bw3.write("Records which are not present in 2\n");
        for (String key : actualrecords.keySet()) {
            for (int i = 0; i < actualrecords.get(key); i++) {
                bw3.write(key);
                bw3.newLine();
            }
        }
        bw3.flush();
        bw3.close();
    }



}
