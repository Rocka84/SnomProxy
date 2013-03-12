

package snomproxy.xml.snom;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "locationX",
    "locationY",
    "data"
})
@XmlRootElement(name = "SnomIPPhoneImage")
public class SnomIPPhoneImage
    extends SnomDocument
{

    @XmlElement(name = "LocationX", required = true, defaultValue = "0")
    protected BigInteger locationX;
    @XmlElement(name = "LocationY", required = true, defaultValue = "0")
    protected BigInteger locationY;
    @XmlElement(name = "Data", required = true)
    protected SnomIPPhoneImage.Data data;

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

    public SnomIPPhoneImage.Data getData() {
        return data;
    }

    public void setData(SnomIPPhoneImage.Data value) {
        this.data = value;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Data {

        @XmlValue
        protected byte[] value;
        @XmlAttribute
        protected String encoding;

        public byte[] getValue() {
            return value;
        }

        public void setValue(byte[] value) {
            this.value = ((byte[]) value);
        }

        public String getEncoding() {
            if (encoding == null) {
                return "base64";
            } else {
                return encoding;
            }
        }

        public void setEncoding(String value) {
            this.encoding = value;
        }

    }

}
