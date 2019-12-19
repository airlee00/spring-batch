package transaction.domain;

public class HotBatchJobInfo {

	private String jobId;
	private String jobName;
	private String partitionName;
	private String workerStepName;
	private String executionType;
	private int gridSize;
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getPartitionName() {
		return partitionName;
	}
	public void setPartitionName(String partitionName) {
		this.partitionName = partitionName;
	}
	public String getWorkerStepName() {
		return workerStepName;
	}
	public void setWorkerStepName(String workerStepName) {
		this.workerStepName = workerStepName;
	}
	public String getExecutionType() {
		return executionType;
	}
	public void setExecutionType(String executionType) {
		this.executionType = executionType;
	}
	public int getGridSize() {
		return gridSize;
	}
	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HotBatchJobInfo [jobId=");
		builder.append(jobId);
		builder.append(", jobName=");
		builder.append(jobName);
		builder.append(", partitionName=");
		builder.append(partitionName);
		builder.append(", workerStepName=");
		builder.append(workerStepName);
		builder.append(", executionType=");
		builder.append(executionType);
		builder.append(", gridSize=");
		builder.append(gridSize);
		builder.append("]");
		return builder.toString();
	}


}
