package org.ow2.jonas.jpaas.server.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Relationship XML element
 * @author Mohamed Sellami (Telecom SudParis)
 */
@XmlRootElement(name = "relationship")
@XmlAccessorType(XmlAccessType.FIELD)
public class RelationshipXML {
	// TODO to be defined
    /**
     * Node Current size
     */
	@XmlAttribute
    private int relationShipID;
    
    /**
     * Default constructor
     */
    public RelationshipXML() {    	
    
    }

	public int getRelationShipID() {
		return relationShipID;
	}

	public void setRelationShipID(int relationShipID) {
		this.relationShipID = relationShipID;
	}
   
}
