package org.ow2.jonas.jpaas.api.xml;

import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.Properties;

@XmlRootElement(name = "applicationVersion")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationVersionXML {
    @XmlAttribute
    private String type;

    @XmlAttribute
    private String href;

	@XmlAttribute
	private String appId;

    @XmlAttribute
    private String versionId;

    @XmlAttribute
    private String versionLabel;

    @XmlElement
    private List<String> requirements;

    @XmlElement
    private List<DeployableXML> sortedDeployableList;

    @XmlElement
    private List<Properties> capabilities;

    @XmlElement
    private List<LinkXML> links;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }


    public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getVersionLabel() {
		return versionLabel;
	}

	public void setVersionLabel(String versionLabel) {
		this.versionLabel = versionLabel;
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
        String msg = prefix + "ApplicationVersion [appId=" + appId + ", versionId=" + versionId + ", versionLabel=" + versionLabel +", href=" + href + "\n" ;

        for (LinkXML myLink :  links)    {
            msg += myLink.toString(prefix + "   ") + "\n";
        }

        msg += prefix + "]\n";

        return msg;
    }
}
