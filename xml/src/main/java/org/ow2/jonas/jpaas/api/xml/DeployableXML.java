package org.ow2.jonas.jpaas.api.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Node XML element
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 */
@XmlRootElement(name = "deployable")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeployableXML {

 	@XmlAttribute
	private String deployableId;

	@XmlAttribute
	private String deployableName;

	@XmlAttribute
	private String locationURL;

	@XmlAttribute
	private String slaEnforcement;

	@XmlAttribute
	private Boolean uploaded;

	@XmlElement
	private List<String> requirements;

	public DeployableXML() {

	}

	public String getDeployableId() {
		return deployableId;
	}

	public void setDeployableId(String deployableId) {
		this.deployableId = deployableId;
	}

	public String getDeployableName() {
		return deployableName;
	}

	public void setDeployableName(String deployableName) {
		this.deployableName = deployableName;
	}

	public String getLocationURL() {
		return locationURL;
	}

	public void setLocationURL(String locationURL) {
		this.locationURL = locationURL;
	}

	public String getSlaEnforcement() {
		return slaEnforcement;
	}

	public void setSlaEnforcement(String slaEnforcement) {
		this.slaEnforcement = slaEnforcement;
	}

	public Boolean getUploaded() {
		return uploaded;
	}

	public void setUploaded(Boolean uploaded) {
		this.uploaded = uploaded;
	}

	public List<String> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

    public String toString() {
        return toString("");
    }
    public String toString(String prefix) {
        String msg = prefix + "Deployable [id=" + deployableId + ", name=" + deployableName + ", locationURL=" + locationURL + ", uploaded=" + uploaded +"] \n" ;

        return msg;
    }
}
