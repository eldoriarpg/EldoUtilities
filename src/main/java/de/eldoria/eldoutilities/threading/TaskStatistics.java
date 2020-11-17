package de.eldoria.eldoutilities.threading;

public class TaskStatistics {
	private int processedElements;
	private long time;

	public void processElement() {
		this.processedElements++;
	}

	public void addTime(long time) {
		this.time += time;
	}

	public int getProcessedElements() {
		return processedElements;
	}

	public long getTime() {
		return time;
	}
}