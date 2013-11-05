package org.ow2.jonas.jpaas.api.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Environment XML element
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 */
@XmlRootElement(name = "environment")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentXML {

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String href;

    @XmlAttribute
	private String envId;

	@XmlAttribute
	private String envName;

	@XmlAttribute
	private String envDesc;

	@XmlAttribute
	private int envState;

	@XmlElement
	private TopologyXML envTopology;

    @XmlElement
    private List<LinkXML> links;


    public EnvironmentXML() {

	}

	public String getEnvId() {
		return envId;
	}

	public void setEnvId(String envId) {
		this.envId = envId;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public String getEnvDesc() {
		return envDesc;
	}

	public void setEnvDesc(String envDesc) {
		this.envDesc = envDesc;
	}

	public int getEnvState() {
		return envState;
	}

	public void setEnvState(int envState) {
		this.envState = envState;
	}

	public TopologyXML getEnvTopology() {
		return envTopology;
	}

	public void setEnvTopology(TopologyXML envTopology) {
		this.envTopology = envTopology;
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
        String msg = prefix + "Environment [envId=" + envId + ", envName=" + envName + ", href=" + href + "\n" ;

        if (envTopology != null) {
            msg += envTopology.toString(prefix + "   ") + "\n";
        }

        for (LinkXML myLink :  links)    {
            msg += myLink.toString(prefix + "   ") + "\n";
        }

        msg += prefix + "]\n";


        return msg;
    }


}
