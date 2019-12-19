package transaction.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import static java.lang.System.exit;



@Component
@Profile("master")
public class BatchCommandLineRunner implements CommandLineRunner {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job transactionProcessingJob;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Starting Batch Job with Unique Parameter");

        JobParameters param = new JobParametersBuilder().addString("JobID",
                String.valueOf(System.currentTimeMillis())).toJobParameters();
        try {
            jobLauncher.run(transactionProcessingJob, param);
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
            exit(1);
        } catch (JobRestartException e) {
            e.printStackTrace();
            exit(1);
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
            exit(1);
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
            exit(1);
        }

        exit(0);

    }
}