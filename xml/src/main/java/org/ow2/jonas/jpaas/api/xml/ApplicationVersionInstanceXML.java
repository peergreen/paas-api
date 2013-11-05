package org.ow2.jonas.jpaas.api.xml;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ApplicationVersionInstance XML element
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 */
@XmlRootElement(name = "applicationVersionInstanceXML")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationVersionInstanceXML {

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String href;

	@XmlAttribute
	private String appId;

    @XmlAttribute
    private String versionId;

	@XmlAttribute
	private String instanceId;

	@XmlAttribute
	private String instanceName;

	@XmlAttribute
	private String state;

	@XmlAttribute
	private String targetEnvId;


	@XmlElement
	private Map<DeployableXML, NodeXML> deployableTopologyMapping;

	@XmlElement
	private List<String> requirements;

	@XmlElement
	private List<DeployableXML> sortedDeployableList;

	@XmlElement
	private List<URI> urlList;

	@XmlElement
	private List<Properties> capabilities;

    @XmlElement
    private List<LinkXML> links;



    public ApplicationVersionInstanceXML() {
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTargetEnvId() {
		return targetEnvId;
	}

	public void setTargetEnvId(String targetEnvId) {
		this.targetEnvId = targetEnvId;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
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


    public Map<DeployableXML, NodeXML> getDeployableTopologyMapping() {
        return deployableTopologyMapping;
    }

    public void setDeployableTopologyMapping(
            Map<DeployableXML, NodeXML> deployableTopologyMapping) {
        this.deployableTopologyMapping = deployableTopologyMapping;
    }

    public List<DeployableXML> getSortedDeployableList() {
		return sortedDeployableList;
	}

	public void setSortedDeployableList(List<DeployableXML> sortedDeployableList) {
		this.sortedDeployableList = sortedDeployableList;
	}

	public List<URI> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<URI> urlList) {
		this.urlList = urlList;
	}

    public String getType() {
        return type;
    }

    public String getHref() {
        return href;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<LinkXML> getLinks() {
        return links;
    }

    public void setLinks(List<LinkXML> links) {
        this.links = links;
    }


    public String toString() {
        return toString("");
    }
    public String toString(String prefix) {
        String msg = prefix + "ApplicationVersionInstance [appId=" + appId + ", versionId=" + versionId + ", instanceId=" + instanceId + ", instanceName=" + instanceName +", href=" + href + "\n" ;

        if (sortedDeployableList != null) {
            for (DeployableXML deployableXML :  sortedDeployableList)    {
                msg += deployableXML.toString(prefix + "   ") + "\n";
            }
        }

        for (LinkXML myLink :  links)    {
            msg += myLink.toString(prefix + "   ") + "\n";
        }


        msg += prefix + "]\n";


        return msg;
    }
}
