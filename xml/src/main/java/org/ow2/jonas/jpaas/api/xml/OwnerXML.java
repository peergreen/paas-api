package org.ow2.jonas.jpaas.api.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OwnerXML {

    /**
     * Name of the resource instance
     */
    @XmlAttribute
    private String name;

    /**
     * Type of the resource instance
     */
    @XmlAttribute
    private String type;

    @XmlAttribute
    private String href;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getHref() {
        return href;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setHref(final String href) {
        this.href = href;
    }

    public String toString() {
        return toString("");
    }
    public String toString(String prefix) {
        return prefix + "Owner[name=" + name + ", type=" + type + ", href=" + href +"]" + "\n";
    }

}
