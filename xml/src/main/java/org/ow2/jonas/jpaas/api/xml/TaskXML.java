
package org.ow2.jonas.jpaas.api.xml;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "task")
public class TaskXML {

	@XmlAttribute
	private String id;

    @XmlAttribute
	private String status;

    @XmlAttribute
	private Date startTime;

	@XmlAttribute
	private Date endTime;

    @XmlAttribute
    private String operationName;

    @XmlElement
    private List<LinkXML> link;

    @XmlElement
    private OwnerXML owner;

	/**
	 * Default constructor
	 */
	public TaskXML() {
		// this.link = new ArrayList<LinkXML>();
	}

	public String getId() {
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

	public void setId(final String id) {
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

	public List<LinkXML> getLink() {
		return link;
	}

	public void setLink(List<LinkXML> link) {
		this.link = link;
	}

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public OwnerXML getOwner() {
        return owner;
    }

    public void setOwner(OwnerXML owner) {
        this.owner = owner;
    }

    public String toString() {
        return toString("");
    }
    public String toString(String prefix) {
        String msg = prefix + "Task[id=" + id + ", name=" + operationName + ", status=" + status + ", startTime=" + startTime + ", endTime=" + endTime + "\n" ;

        msg += owner.toString(prefix + "   ") + "\n";
        for (LinkXML myLink :  link)    {
            msg += myLink.toString(prefix + "   ") + "\n";
        }

        msg += "\n";

        return msg;
    }
}
