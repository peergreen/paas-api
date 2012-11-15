/**
 * JPaaS
 * Copyright 2012 Bull S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id:$
 */ 
package org.ow2.jonas.jpaas.api.task;

/**
 * Defines task status Queued: the task is in a queue, waiting its execution
 * Running: the task is running Success: the task have been successfully
 * executed Error: an error occur during the execution of the task Cancelled:
 * the task has been cancelled by a user request
 * 
 * @author Jeremy Cazaux
 */
public enum Status {
	QUEUED, RUNNING, SUCCESS, ERROR, CANCELED
}
