

package snomproxy.xml.snom;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SnomDocument", propOrder = {
    "softKeyItem",
    "fetch"
})
@XmlSeeAlso({
    SnomIPPhoneText.class,
    SnomIPPhoneImageFile.class,
    SnomIPPhoneImage.class,
    SnomIPPhoneDirectory.class,
    SnomIPPhoneInput.class,
    SnomIPPhoneMenu.class
})
public abstract class SnomDocument {

    @XmlElement(name = "SoftKeyItem")
    protected List<SnomDocument.SoftKeyItem> softKeyItem;
    protected SnomDocument.Fetch fetch;

    public List<SnomDocument.SoftKeyItem> getSoftKeyItem() {
        if (softKeyItem == null) {
            softKeyItem = new ArrayList<SnomDocument.SoftKeyItem>();
        }
        return this.softKeyItem;
    }
    
    public void addSoftKeyItem(SnomDocument.SoftKeyItem item){
        this.getSoftKeyItem().add(item);
    }

    public void addSoftKeyItem(String name, String label, String url){
        this.getSoftKeyItem().add(new SoftKeyItem(name,label,url));
    }

    public SnomDocument.Fetch getFetch() {
        return fetch;
    }

    public void setFetch(String url,int mil) {
        this.setFetch(new Fetch(url,mil));
    }

    public void setFetch(SnomDocument.Fetch value) {
        this.fetch = value;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Fetch {

        @XmlValue
        protected String value;
        @XmlAttribute(required = true)
        protected int mil;

        public Fetch() {
        }

        public Fetch(String value, int mil) {
            this.value = value;
            this.mil = mil;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getMil() {
            return mil;
        }

        public void setMil(int value) {
            this.mil = value;
        }

    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "label",
        "url"
    })
    public static class SoftKeyItem {

        @XmlElement(name = "Name", required = true)
        protected String name;
        @XmlElement(name = "Label", required = true)
        protected String label;
        @XmlElement(name = "URL", required = true)
        protected String url;

        public SoftKeyItem() {
        }

        public SoftKeyItem(String name, String label, String url) {
            this.name = name;
            this.label = label;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String value) {
            this.label = value;
        }

        public String getURL() {
            return url;
        }

        public void setURL(String value) {
            this.url = value;
        }

    }

}
