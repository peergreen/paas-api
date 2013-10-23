package org.ow2.jonas.jpaas.api.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "applicationVersion")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationVersionXML {
	
	/**
	 * Application id
	 */
	@XmlAttribute
	private String appId;
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

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



}
