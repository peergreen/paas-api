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

	/**
	 * Node Current size
	 */
	@XmlAttribute
	private int nodeCurrentSize;

	/**
	 * Node ID
	 */
	@XmlAttribute
	private String nodeID;

	/**
	 * Node max size
	 */
	@XmlAttribute
	private int nodeMaxSize;

	/**
	 * Node min size
	 */
	@XmlAttribute
	private int nodeMinSize;

	/**
	 * Node Name
	 */
	@XmlAttribute
	private String nodeName;

  /**
	 * Node type
	 */
	@XmlAttribute
	private String nodeType;

	/**
	 * Default constructor
	 */
	public NodeXML() {

	}

	public int getNodeCurrentSize() {
		return nodeCurrentSize;
	}

	public void setNodeCurrentSize(int nodeCurrentSize) {
		this.nodeCurrentSize = nodeCurrentSize;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
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

}
