/**
 * JOnAS: Java(TM) Open Application Server
 * Copyright (C) 2012 Bull S.A.S.
 * Contact: jonas-team@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.jonas.jpaas.server.task;


import javax.resource.spi.work.Work;

import org.ow2.jonas.jpaas.server.xml.EnvironmentXML;

import java.util.Date;

/**
 * Represent a Work task
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
     * @param status Update the status with the given value
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
}
