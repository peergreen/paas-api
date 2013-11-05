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

	@XmlElement
	private List<NodeXML> topoListNode;

	@XmlElement
	private List<RelationshipXML> topoRelationShipList;

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

    public String toString() {
        return toString("");
    }
    public String toString(String prefix) {
        String msg = prefix + "Topology [topoListNode=" + topoListNode + ", topoRelationShipList=" + topoRelationShipList +"\n" ;

        if (topoListNode != null) {
         for (NodeXML nodeXML :  topoListNode)    {
             msg += nodeXML.toString(prefix + "   ") + "\n";
         }
        }


       if (topoRelationShipList != null) {
           for (RelationshipXML relationshipXML :  topoRelationShipList)    {
               msg += relationshipXML.toString(prefix + "   ") + "\n";
           }
       }

        msg += prefix + "]\n";


        return msg;
    }

}
