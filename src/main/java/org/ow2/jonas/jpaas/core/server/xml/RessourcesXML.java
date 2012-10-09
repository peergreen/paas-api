/**
 * 
 */
package org.ow2.jonas.jpaas.core.server.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.ow2.jonas.jpaas.core.server.xml.adapter.HashMapTest2.MapEntry;

/**
 * @author sellami
 *
 */


@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)

public class RessourcesXML {

	public Map<String, Integer> map = new HashMap<String, Integer>();
	

    public RessourcesXML(Map<String, Integer> map) {
		this.map = new HashMap<String, Integer>();
		this.map.putAll(map);
	}

    @XmlElement(name = "ressource")
    public MapEntry[] getMap() {
        List<MapEntry> list = new ArrayList<MapEntry>();
        for (Entry<String, Integer> entry : map.entrySet()) {
            MapEntry mapEntry =new MapEntry();
            mapEntry.key = entry.getKey();
            mapEntry.value = entry.getValue();
            list.add(mapEntry);
        }
        return list.toArray(new MapEntry[list.size()]);
    }
    
    public void setMap(MapEntry[] arr) {
        for(MapEntry entry : arr) {
            this.map.put(entry.key, entry.value);
        }
    }


    public static class MapEntry {
        @XmlAttribute
        public String key;
        @XmlValue
        public Integer value;
    }

}