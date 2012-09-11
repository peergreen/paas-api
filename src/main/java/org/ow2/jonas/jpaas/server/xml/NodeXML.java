package org.ow2.jonas.jpaas.server.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Node XML element
 * @author Mohamed Sellami (Telecom SudParis)
 */
@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeXML {
	
    /**
     * Node Current size
     */
	@XmlAttribute
    private int nodeCureentSize;
    
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
     * Default constructor
     */
    public NodeXML() {
    
    }

	public int getNodeCureentSize() {
		return nodeCureentSize;
	}

	public void setNodeCureentSize(int nodeCureentSize) {
		this.nodeCureentSize = nodeCureentSize;
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
   
}
