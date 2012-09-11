/**
 * 
 */
package org.ow2.jonas.jpaas.server.xml;

/**
 * @author sellami
 *
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "task")
public class TaskXML {

    /**
     * Task id
     */
    @XmlAttribute
    private Long id;

    /**
     * Task status
     */
    @XmlAttribute
    private String status;

    /**
     *
     */
    @XmlAttribute
    private Date startTime;

    @XmlAttribute
    private Date endTime;

   // @XmlAttribute
   // private String operationName;

  //  @XmlElement
   // private List<Link> link;


    /**
     * Default constructor
     */
    public TaskXML() {
        //this.link = new ArrayList<Link>();
    }

    public Long getId() {
        return id;
    }

    
    public Date getStartTime() {
        return startTime;
    }

    public String getStatus() {
        return status;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
}
