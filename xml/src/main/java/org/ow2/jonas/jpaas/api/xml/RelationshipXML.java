package org.ow2.jonas.jpaas.api.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Relationship XML element
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 */
@XmlRootElement(name = "relationship")
@XmlAccessorType(XmlAccessType.FIELD)
public class RelationshipXML {

	@XmlAttribute
	private String relationShipType;

	@XmlAttribute
	private int relationShipId;

	/**
	 * Default constructor
	 */
	public RelationshipXML() {

	}

	public int getRelationShipId() {
		return relationShipId;
	}

	public void setRelationShipId(int relationShipId) {
		this.relationShipId = relationShipId;
	}

  public String getRelationShipType() {
    return relationShipType;
  }

  public void setRelationShipType(String relationShipType) {
    this.relationShipType = relationShipType;
  }


    public String toString() {
        return toString("");
    }
    public String toString(String prefix) {
        String msg = prefix + "Relationship [id=" + relationShipId + ", type=" + relationShipType + "]\n" ;

        return msg;
    }

}
