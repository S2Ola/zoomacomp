package ac.uk.ebi.zooma;



import com.oracle.tools.packager.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;





@Component
@Order(value = 4)
public class RunBashInJava implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger( RunBashInJava.class );
    private String homeDir = System.getProperty( "user.home" );

    @Override
    public void run(String... args) {



    }

/*public class RunBashInJava()

    {
       Runtime runtime = Runtime.getRuntime();
        Process process;

        {
            try {
                process = runtime.exec( "script.sh" );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }*/
}
