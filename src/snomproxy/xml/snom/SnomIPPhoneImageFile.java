

package snomproxy.xml.snom;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "locationX",
    "locationY",
    "url"
})
@XmlRootElement(name = "SnomIPPhoneImageFile")
public class SnomIPPhoneImageFile
    extends SnomDocument
{

    @XmlElement(name = "LocationX", required = true, defaultValue = "0")
    protected BigInteger locationX;
    @XmlElement(name = "LocationY", required = true, defaultValue = "0")
    protected BigInteger locationY;
    @XmlElement(name = "URL", required = true)
    protected String url;

    public BigInteger getLocationX() {
        return locationX;
    }

    public void setLocationX(BigInteger value) {
        this.locationX = value;
    }

    public BigInteger getLocationY() {
        return locationY;
    }

    public void setLocationY(BigInteger value) {
        this.locationY = value;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String value) {
        this.url = value;
    }

}
