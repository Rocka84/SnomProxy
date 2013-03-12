package snomproxy.xml.snom;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "title",
    "menuItem"
})
@XmlRootElement(name = "SnomIPPhoneMenu")
public class SnomIPPhoneMenu extends SnomDocument {

    
    @XmlElement(name = "Title", required = true)
    protected String title;
    @XmlElement(name = "MenuItem", required = true)
    protected List<SnomIPPhoneMenu.MenuItem> menuItem;

    public SnomIPPhoneMenu() {
    }

    public SnomIPPhoneMenu(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public List<SnomIPPhoneMenu.MenuItem> getMenuItem() {
        if (menuItem == null) {
            menuItem = new ArrayList<SnomIPPhoneMenu.MenuItem>();
        }
        return this.menuItem;
    }
    
    public void addMenuItem(MenuItem item){
        this.getMenuItem().add(item);
    }

    public void addMenuItem(String name,String url){
        this.addMenuItem(new MenuItem(name, url));
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "url"
    })
    public static class MenuItem {

        @XmlElement(name = "Name", required = true)
        protected String name;
        @XmlElement(name = "URL", required = true)
        protected String url;

        public MenuItem() {
        }

        public MenuItem(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public String getURL() {
            return url;
        }

        public void setURL(String value) {
            this.url = value;
        }
    }
}
