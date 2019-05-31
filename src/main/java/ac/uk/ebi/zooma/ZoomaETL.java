package ac.uk.ebi.zooma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@Order(value = 1)
public class ZoomaETL implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger( ZoomaETL.class );
    private String homeDir = System.getProperty( "user.home" );

    @Override
    public void run(String... args) {

        loadDataFile();

    }

    @Autowired
    private UtilityService utilityService;


    public void loadDataFile(){

        String fileName = "/Users/dipo/desktop/SingleCellAtlasData/atlas_zooma.tsv";


        Map<String, List<String>> data = utilityService.serializeMergedData( fileName, "SEMANTIC_TAG");


        String mapKey = "";
        String prefixes = "";
        String destination = "/Documents/SCATLAS_FOLDER/ACTUAL_ONTOLOGY/";

        for (Map.Entry<String, List<String>> entry : data.entrySet()) {

            mapKey = entry.getKey();

            logger.info( mapKey );


            String[] temp = mapKey.split("/");
            String fileData = temp[temp.length-1];

            List<String> dData = data.get( mapKey );

            String report = "";
            for (String iri : dData){


                if (iri.contains("|")){
                    iri = String.join( "\n", iri.split("\\|"));
                }

                 report += iri+"\n";
            }


            prefixes += mapKey+",";

            utilityService.writeToFile( report, destination, fileData+".txt" );
        }

        utilityService.writeToFile( prefixes, destination,"OntologyUrl.txt" );

    }

}
