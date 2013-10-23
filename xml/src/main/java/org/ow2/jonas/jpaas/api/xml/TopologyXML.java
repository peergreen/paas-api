package org.ow2.jonas.jpaas.api.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Topology XML element
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 */
@XmlRootElement(name = "topology")
@XmlAccessorType(XmlAccessType.FIELD)
public class TopologyXML {

	/**
	 * Topology NodeList
	 */
	@XmlElement
	private List<NodeXML> topoListNode;

	/**
	 * Topology RelationShipList
	 */
	@XmlElement
	private List<RelationshipXML> topoRelationShipList;

	/**
	 * Default constructor
	 */
	public TopologyXML() {

	}

	public List<NodeXML> getTopoListNode() {
		return topoListNode;
	}

	public void setTopoListNode(List<NodeXML> topoListNode) {
		this.topoListNode = topoListNode;
	}

	public List<RelationshipXML> getTopoRelationShipList() {
		return topoRelationShipList;
	}

	public void setTopoRelationShipList(
			List<RelationshipXML> topoRelationShipList) {
		this.topoRelationShipList = topoRelationShipList;
	}

}
