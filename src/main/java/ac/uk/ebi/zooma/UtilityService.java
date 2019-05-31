package ac.uk.ebi.zooma;


import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


@Service
public class UtilityService {


    private String homeDir = System.getProperty( "user.home" );
    private final static Logger log = LoggerFactory.getLogger( UtilityService.class );
    private ObjectMapper mapper = new ObjectMapper();


    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";



    public void csvReader(String csvUrl) {

        try {

            CSVReader reader = new CSVReader( new FileReader(csvUrl), ',', '"', 0 );

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine != null) {
                    //Verifying the read data here
                    System.out.println( Arrays.toString( nextLine ) );
                }
            }

        }catch (Exception e){}

    }



    public List<Map<String, String>> serializeCSVORTSVToMaps(String csvFile, String fileType) {


        /*************************************************************************************************************
         *     INITIALIZE PARAMETERS         *
         ************************************/

        int row = 0;
        String[] rowDataArr;
        List<String> tableHead = new ArrayList<>();
        List<Map<String, String>> csvMap = new ArrayList<>();

        char separator = ',';

        if (fileType.equals( "tsv" )){
            separator = '\t';
        }


        /*************************************************************************************************************
         *    LOAD CSV FIRST ROW AS TABLE-HEAD, OTHER ROWS AS DATA, & LOAD TO MAP        *
         *******************************************************************************/
        try {

            CSVReader reader = new CSVReader( new FileReader(csvFile), separator);

            while ( (rowDataArr = reader.readNext()) != null ) {

                if (rowDataArr != null) {

                    int column = 0;

                    if (row == 0) {

                        for (column = 0; column < rowDataArr.length; column++) {

                            tableHead.add( rowDataArr[column] );
                        }
                    } else {

                        Map<String, String> rowMap = new HashMap();
                        for (String columnHead : tableHead) {

                            rowMap.put( columnHead.trim(), rowDataArr[column].trim() );
                            column++;
                        }
                        csvMap.add( rowMap );
                    }
                    row++;


                }
            }
        } catch (Exception e) {
        }

        return csvMap;

    }


    public List<Map<String, String>> serializeDataToMaps(String fileName) {


        String fileExtension = getFileExtension( fileName );

        List<Map<String, String>> csvMaps = (fileExtension.equals( "csv" )) ? serializeCSVORTSVToMaps( fileName,"csv" ) : serializeJSONToMaps( fileName );

        return csvMaps;
    }


    public Map<String, List<String>> serializeMergedData(String fileName, String groupColumn) {


        String fileExtension = getFileExtension( fileName );

        List<Map<String, String>> csvMaps = new ArrayList<>();

        switch (fileExtension) {

            case "csv":
                csvMaps = serializeCSVORTSVToMaps( fileName, "csv" );
                break;

            case "tsv":
                csvMaps = serializeCSVORTSVToMaps( fileName, "tsv" );
                break;

            case "json":
                csvMaps = serializeJSONToMaps( fileName );
                break;
        }

        Map<String, List<String>> groupedMap = new HashMap<>();

        int count = 0;
        for (Map<String, String> rowData : csvMaps) {

            List<String> tempList = new ArrayList<>();

            String rowKey = rowData.get( groupColumn );
/*
            String[] temp = rowKey.split("/");
            String ontologyName = temp[temp.length-1].split("_")[0];*/

            String ontologyName = rowKey.split("_")[0];

           // log.info( ++count+".)"+rowKey );


            if (groupedMap.get( ontologyName ) == null) {

                tempList.add( rowKey );
                groupedMap.put( ontologyName, tempList );
            } else {

                tempList = groupedMap.get( ontologyName );
                tempList.add( rowKey );

                groupedMap.put( ontologyName, tempList );
            }

        }
        return groupedMap;
    }


    public List<Map<String, String>> serializeJSONToMaps(String jsonFile, String jsonKey) {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = readJsonLocal( jsonFile );

        Map<String, Object> json = mapper.convertValue( node, Map.class );

        List<Map<String, String>> data = (List) json.get( jsonKey );

        return data;
    }


    public List<Map<String, String>> serializeJSONToMaps(String jsonFile) {

        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, String>> data;

        JsonNode node = readJsonLocal( jsonFile );
        try {
            data = mapper.convertValue( node, List.class );

        } catch (Exception e) {

            Map<String, Object> json = mapper.convertValue( node, Map.class );

            String jsonKey = "";
            for (Map.Entry<String, Object> entry : json.entrySet()) {      // GET THE JSON KEY
                jsonKey = entry.getKey();
            }

            data = (List) json.get( jsonKey );
        }

        return data;
    }


    public List<List<String>> serializeCSVToArrayList(String dataFile) {

        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream( dataFile );
        } catch (Exception e) {
        }
        DataInputStream myInput = new DataInputStream( fileStream );


        String thisLine;
        int i = 0;
        ArrayList lineList = null;
        List<List<String>> dataArrayList = new ArrayList<>();

        try {

            while ((thisLine = myInput.readLine()) != null) {
                lineList = new ArrayList();
                String strar[] = thisLine.split( "," );
                for (int j = 0; j < strar.length; j++) {
                    lineList.add( strar[j] );
                }
                dataArrayList.add( lineList );
                System.out.println();
                i++;
            }

        } catch (Exception e) {
        }


        return dataArrayList;
    }


    public void writeCsvFile(List<Map<String, String>> dataList, List<String> csvHead, String fileName) {

        FileWriter fileWriter = null;

        try {

            String destination = homeDir + "/Downloads/" + fileName;
            fileWriter = new FileWriter( destination );

            //Write the CSV file header
            fileWriter.append( String.join( COMMA_DELIMITER, csvHead ) );

            //Add a new line separator after the header
            fileWriter.append( NEW_LINE_SEPARATOR );


            for (Map<String, String> data : dataList) {

                for (String dKey : csvHead) {

                    fileWriter.append( String.valueOf( data.get( dKey ) ) );
                    fileWriter.append( COMMA_DELIMITER );
                }
                fileWriter.append( NEW_LINE_SEPARATOR );

            }

            log.info( "CSV file was created successfully !!!" );

        } catch (Exception e) {
            log.info( "Error in CsvFileWriter !!!" );
            e.printStackTrace();
        } finally {

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                log.info( "Error while flushing/closing fileWriter !!!" );
                e.printStackTrace();
            }

        }
    }



    public JsonNode readJsonURL(String apiLink) {

        JsonNode jsonNode = null;
        ObjectMapper mapper = new ObjectMapper();

        try {

            URL url = new URL( apiLink );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod( "GET" );
            conn.setRequestProperty( "Accept", "application/json" );

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException( "Failed : HTTP error code : " + conn.getResponseCode() );
            }

            BufferedReader br = new BufferedReader( new InputStreamReader( (conn.getInputStream()) ) );

            jsonNode = mapper.readTree( br );
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonNode;

    }


    public JsonNode readJsonLocal(String jsonFileLink) {

        JsonNode jsonNode = null;
        ObjectMapper mapper = new ObjectMapper();

        try {

            BufferedReader br = new BufferedReader( new FileReader( jsonFileLink ) );
            jsonNode = mapper.readTree( br );

        } catch (Exception e) {
        }

        return jsonNode;
    }


    public void writeToFile(String data, String directory, String fileName) {

        try {
            Files.createDirectories(Paths.get(homeDir+directory));
        }catch (Exception e){
            log.error( "Something is wrong with directory "+ homeDir+directory );
        }


        fileName = homeDir+directory+fileName;

        // Write to the file using BufferedReader and FileWriter
        try {

            BufferedWriter writer = new BufferedWriter( new FileWriter( fileName, false ) );
            writer.append( data );
            writer.close();

        } catch (Exception e) {
        }

    }


    public Boolean deleteFile(String name) {

        String fileURL = homeDir + "/Documents/" + name;

        Boolean report = false;
        try {

            Path path = Paths.get( fileURL );
            Files.deleteIfExists( path );

            report = true;
        } catch (Exception e) {
        }

        return report;
    }


    public String parseURL(String urlStr) {

        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL( urlStr );
            BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append( inputLine );
            }
            in.close();
        } catch (Exception e) {
            log.error( "Unable to read from URL " + urlStr, e );
        }
        return sb.toString();
    }


    public String parseFile(String path) {

        StringBuilder sb = new StringBuilder();

        try {
            Stream<String> stream = Files.lines( Paths.get( path ) );

            Iterator itr = stream.iterator();
            while (itr.hasNext()) {
                sb.append( itr.next() );
            }
        } catch (Exception e) {
            log.error( "Failed to load file " + path, e );
        }
        return sb.toString();
    }


    public String splitText(String data, String delim, String seperator) {

        String result = "";

        String[] splits = data.split( delim );

        for (String split : splits) {

            result += split.trim() + seperator;
        }

        return result;
    }


    public String getFileExtension(String fileName) {

        String[] check = fileName.split( "\\." );
        String fileExtension = check[check.length - 1];

        return fileExtension;
    }


    public void executeCommand(String command){

        log.info("Executing command ... "+command);

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        }catch (Exception e){ }
    }

}

