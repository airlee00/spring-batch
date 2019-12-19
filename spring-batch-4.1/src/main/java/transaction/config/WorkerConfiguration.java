package transaction.config;

import java.util.Map;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.step.StepLocator;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.BeanFactoryStepLocator;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.integration.partition.StepExecutionRequestHandler;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

import transaction.domain.Transaction;
import transaction.domain.TransactionProcessor;
import transaction.domain.TransactionWriter;

@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
@Profile("worker")
public class WorkerConfiguration implements BeanFactoryAware {

	private final RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;


	@Autowired
	protected StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobExplorer jobExplorer;

	private BeanFactory beanFactory;

	public WorkerConfiguration(RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory) {
		this.workerStepBuilderFactory = workerStepBuilderFactory;
	}

	@Bean
	public DirectChannel requests() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow inboundFlow(ConnectionFactory connectionFactory) {
		return IntegrationFlows
				.from(Amqp.inboundAdapter(connectionFactory, "requests"))
				.channel(requests())
				.get();
	}

	@Bean
	public DirectChannel replies() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow outboundFlow(AmqpTemplate amqpTemplate) {
		return IntegrationFlows.from(replies())
				.handle(Amqp.outboundAdapter(amqpTemplate).routingKey("replies"))
				.get();
	}

	@Bean
	public IntegrationFlow workerStepFlow() {

		return IntegrationFlows.from(requests())
				.handle(stepExecutionRequestHandler(), "handle")
				.channel(replies())
				.get();
	}

	@Bean
	public StepExecutionRequestHandler stepExecutionRequestHandler() {
	    StepExecutionRequestHandler stepExecutionRequestHandler = new StepExecutionRequestHandler();
	    stepExecutionRequestHandler.setJobExplorer(jobExplorer);
	    stepExecutionRequestHandler.setStepLocator(stepLocator());
	    return stepExecutionRequestHandler;
	}

	private StepLocator stepLocator() {
		BeanFactoryStepLocator beanFactoryStepLocator = new BeanFactoryStepLocator();
		beanFactoryStepLocator.setBeanFactory(this.beanFactory);
		return beanFactoryStepLocator;
	}

	@Bean
	@ServiceActivator(inputChannel = "inboundRequests", outputChannel = "outboundStaging")
	public StepExecutionRequestHandler serviceActivator() throws Exception {
	    return stepExecutionRequestHandler();
	}

/*	@Bean
	public Step workerStep() {
		return this.workerStepBuilderFactory.get("workerStep")
				.inputChannel(requests())
				.outputChannel(replies())
				.<Transaction, Transaction>chunk(3)
				.reader(reader(null))
				.processor(processor())
				.writer(writer(null))
				.build();
	}*/

	@Bean
	public Step workerStep1() {
		return this.stepBuilderFactory.get("workerStep1")
				.<Transaction, Transaction>chunk(3)
				.reader(reader(null))
				.processor(processor())
				.writer(writer(null))
				.build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Transaction> reader(@Value("#{stepExecutionContext}") Map<String,Object> stepExecutionContext) {
		FlatFileItemReader<Transaction> reader = new FlatFileItemReader<Transaction>();
		reader.setResource(new ClassPathResource(stepExecutionContext.get("fileName").toString()));
		reader.setLineMapper(new DefaultLineMapper<Transaction>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[]{"transactionId", "merchantId", "transactionAmt"});
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<Transaction>() {{
				setTargetType(Transaction.class);
			}});
		}});
		return reader;
	}

	@Bean
	@StepScope
	public TransactionProcessor processor() {
		return new TransactionProcessor();
	}

	@Bean
	@StepScope
	public TransactionWriter writer(@Value("#{stepExecutionContext[outputFile]}") String outputFile) {
		TransactionWriter writer = new TransactionWriter();
		writer.setOutputFile(outputFile);
		return writer;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;


	}
}
