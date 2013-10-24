package org.ow2.jonas.jpaas.api.task;

/**
 * Defines task status
 * Queued: the task is in a queue, waiting its execution
 * Running: the task is running
 * Success: the task have been successfully
 * executed
 * Error: an error occur during the execution of the task
 * Cancelled: the task has been cancelled by a user request
 */
public enum Status {
	QUEUED, RUNNING, SUCCESS, ERROR, CANCELED
}
