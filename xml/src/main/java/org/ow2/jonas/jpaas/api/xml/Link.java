/**
 * JPaaS
 * Copyright 2012 Bull S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id:$
 */

package org.ow2.jonas.jpaas.api.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Link XML element
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Link {

	@XmlAttribute
	private String rel;

	@XmlAttribute
	private String type;

	@XmlAttribute
	private String href;

	public String getRel() {
		return rel;
	}

	public String getType() {
		return type;
	}

	public String getHref() {
		return href;
	}

	public void setRel(final String rel) {
		this.rel = rel;
	}

	public void setHref(final String href) {
		this.href = href;
	}

	public void setType(final String type) {
		this.type = type;
	}

    public String toString() {
        return toString("");
    }
    public String toString(String prefix) {
      return prefix + "Link[rel=" + rel + ", type=" + type + ", href=" +href +"]" + "\\n";
    }
}
