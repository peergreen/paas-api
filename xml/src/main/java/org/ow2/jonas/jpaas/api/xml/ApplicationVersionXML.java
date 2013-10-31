package org.ow2.jonas.jpaas.api.xml;

import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.Properties;

@XmlRootElement(name = "applicationVersion")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationVersionXML {
	
	/**
	 * Application id
	 */
	@XmlAttribute
	private String appId;

    @XmlElement
    private List<String> requirements;

    /**
     * ApplicationVersionInstance sortedDeployabesLis
     */
    @XmlElement
    private List<DeployableXML> sortedDeployableList;

    /**
     * ApplicationVersionInstance capabilities
     */
    @XmlElement
    private List<Properties> capabilities;
	
	/**
	 * Application Version id
	 */
	@XmlAttribute
	private String appVerId;

	/**
	 * Application Version Label
	 */
	@XmlAttribute
	private String appVerLabel;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }


    public String getAppVerId() {
		return appVerId;
	}

	public void setAppVerId(String appVerId) {
		this.appVerId = appVerId;
	}

	public String getAppVerLabel() {
		return appVerLabel;
	}

	public void setAppVerLabel(String appVerLabel) {
		this.appVerLabel = appVerLabel;
	}

    public List<Properties> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<Properties> capabilities) {
        this.capabilities = capabilities;
    }


    public List<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }

    public List<DeployableXML> getSortedDeployableList() {
        return sortedDeployableList;
    }

    public void setSortedDeployableList(List<DeployableXML> sortedDeployableList) {
        this.sortedDeployableList = sortedDeployableList;
    }

}
