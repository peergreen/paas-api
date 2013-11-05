package org.ow2.jonas.jpaas.api.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Node XML element
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 */
@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeXML {

    @XmlAttribute
    private String nodeId;

    @XmlAttribute
    private String nodeName;

    @XmlAttribute
    private String nodeType;

    @XmlAttribute
	private int nodeCurrentSize;

	@XmlAttribute
	private int nodeMaxSize;

	@XmlAttribute
	private int nodeMinSize;

	public NodeXML() {

	}

	public int getNodeCurrentSize() {
		return nodeCurrentSize;
	}

	public void setNodeCurrentSize(int nodeCurrentSize) {
		this.nodeCurrentSize = nodeCurrentSize;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public int getNodeMaxSize() {
		return nodeMaxSize;
	}

	public void setNodeMaxSize(int nodeMaxSize) {
		this.nodeMaxSize = nodeMaxSize;
	}

	public int getNodeMinSize() {
		return nodeMinSize;
	}

	public void setNodeMinSize(int nodeMinSize) {
		this.nodeMinSize = nodeMinSize;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

  public String getNodeType() {
    return nodeType;
  }

  public void setNodeType(String nodeType) {
    this.nodeType = nodeType;
  }

    public String toString() {
        return toString("");
    }
    public String toString(String prefix) {
        String msg = prefix + "Node [nodeID=" + nodeId + ", nodeName=" + nodeName + ", nodeType=" + nodeType +
                ", currentSize=" + nodeCurrentSize + ", minSize=" + nodeMinSize + ", maxSize=" + nodeMaxSize + "]\n" ;

        return msg;
    }

}
