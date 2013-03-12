package snomproxy.xml.snom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "title",
    "text"
})
@XmlRootElement(name = "SnomIPPhoneText")
public class SnomIPPhoneText extends SnomDocument {

    @XmlElement(name = "Title", required = true)
    protected String title;
    @XmlElement(name = "Text", required = true)
    protected String text;

    public SnomIPPhoneText() {
    }

    public SnomIPPhoneText(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        this.text = value;
    }
}
