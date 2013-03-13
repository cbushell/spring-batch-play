package foo.bar.baz;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

public class SimpleBatchFlow {

	private JobLauncher jobLauncher;

	private JobRepository jobRepository;

	private PlatformTransactionManager transactionManager;

	public static void main(String[] args)
			throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		FileSystemXmlApplicationContext applicationContext = new FileSystemXmlApplicationContext(
				"src/main/java/applicationContext.xml");
		SimpleBatchFlow simpleBatchFlow = (SimpleBatchFlow) applicationContext
				.getBean("simpleBatchFlow");
		simpleBatchFlow.run();
	}

	public SimpleBatchFlow(JobLauncher jobLauncher,
			JobRepository jobRepository,
			PlatformTransactionManager transactionManager) {
		this.jobLauncher = jobLauncher;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
	}

	private void run() throws JobExecutionAlreadyRunningException,
			JobRestartException, JobInstanceAlreadyCompleteException,
			JobParametersInvalidException {
		SimpleJob job = createJob();
		JobParameters jobParameters = new JobParametersBuilder()
				.toJobParameters();
		jobLauncher.run(job, jobParameters);
	}

	private SimpleJob createJob() {
		Tasklet tasklet1 = new FileCopyTask();
		TaskletStep taskletStep1 = new TaskletStep("my first step");
		taskletStep1.setTransactionManager(transactionManager);
		taskletStep1.setJobRepository(jobRepository);
		taskletStep1.setTasklet(tasklet1);

		Tasklet tasklet2 = new FileCopyTask();
		TaskletStep taskletStep2 = new TaskletStep("my second step");
		taskletStep2.setTransactionManager(transactionManager);
		taskletStep2.setJobRepository(jobRepository);
		taskletStep2.setTasklet(tasklet2);

		List<Step> steps = Arrays.asList(new Step[] { taskletStep1,
				taskletStep2 });

		SimpleJob job = new SimpleJob("my first job");
		job.setJobRepository(jobRepository);
		job.setSteps(steps);

		return job;
	}
}