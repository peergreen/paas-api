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

import java.util.Date;
import java.util.List;

import javax.resource.spi.work.Work;

import org.ow2.jonas.jpaas.core.server.xml.Link;

/**
 * Represent a Work task
 * 
 * @author Jeremy Cazaux
 */
public interface IWorkTask extends Work {

	/**
	 * @return the Id of the task
	 */
	Long getId();

	/**
	 * @return the task
	 */
	ITask getTask();

	/**
	 * @return the status of the task
	 */
	Status getStatus();

	/**
	 * @param status
	 *            Update the status with the given value
	 */
	void updateStatus(final Status status);

	/**
	 * @return the date which the execution of the task has been started
	 */
	Date getStartTime();

	/**
	 * @return the date that task has been executed
	 */
	Date getEndTime();

	/**
	 * @return the XML representation of the server
	 */
	String getEnvironmentXML();

	/**
	 * @return the klink's list associated to a Task
	 */
	List<Link> getLinkList();

}
