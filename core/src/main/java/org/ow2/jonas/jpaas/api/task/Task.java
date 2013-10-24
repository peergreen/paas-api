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
import java.util.concurrent.Future;

import org.ow2.jonas.jpaas.api.xml.LinkXML;
import org.ow2.jonas.jpaas.api.xml.OwnerXML;

public interface Task {

	/**
	 * @return the Id of the task
	 */
	String getId();

	/**
	 * @return the status of the task
	 */
	Status getStatus();

	/**
	 * @param status
	 * Update the status with the given value
	 */
	void setStatus(final Status status);

	/**
	 * @return the date which the execution of the task has been started
	 */
	Date getStartTime();

	/**
	 * @return the date that task has been executed
	 */
	Date getEndTime();
    void setEndTime(Date time);

    /**
     * @return the task name
     */
    String getName();

    /**
     * @return the related job
     */
    Future<?> getJob();

    OwnerXML getOwner();

    String getBaseUrl();
}
