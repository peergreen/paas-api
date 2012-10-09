package org.ow2.jonas.jpaas.core.server.xml.adapter;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.transform.stream.StreamSource;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.NONE)
public class HashMapTest2 {

    public Map<String, String> map = new HashMap<String, String>();

    @XmlElement(name = "entry")
    public MapEntry[] getMap() {
        List<MapEntry> list = new ArrayList<MapEntry>();
        for (Entry<String, String> entry : map.entrySet()) {
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
        public String value;
    }
    
    public static void main(String args[]) throws Exception {
        HashMapTest2 mp = new HashMapTest2();
        mp.map.put("key1", "value1");
        mp.map.put("key2", "value2");

        JAXBContext jc = JAXBContext.newInstance(HashMapTest2.class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(mp, System.out);
        
        Unmarshaller u = jc.createUnmarshaller();
        StringBuffer xmlStr = new StringBuffer( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><root><entry key=\"key2\">value2</entry><entry key=\"key1\">value1</entry></root>");
        HashMapTest2 mp2 = (HashMapTest2)u.unmarshal( new StreamSource( new StringReader( xmlStr.toString() ) ) );
        m.marshal(mp2, System.out);
    }
}
