package transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application  {

	private static Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        List<String> finalArgs = new ArrayList<>();
        finalArgs.add("inputFiles=/transaction*.txt");
        finalArgs.addAll(Arrays.asList(args));
       SpringApplication.run(Application.class, finalArgs.toArray(new String[finalArgs.size()]));

    }

}
