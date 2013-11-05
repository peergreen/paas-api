package org.ow2.jonas.jpaas.api.xml;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "application")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationXML {

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String href;

	@XmlAttribute
	private String appId;

	@XmlAttribute
	private String appName;

    @XmlElement
    private List<LinkXML> links;


    public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
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
        String msg = prefix + "Application [appId=" + appId + ", name=" + appName + ", href=" + href + "\n" ;

        for (LinkXML myLink :  links)    {
            msg += myLink.toString(prefix + "   ") + "\n";
        }

        msg += prefix + "]\n";

        return msg;
    }

}
