

package snomproxy.xml.snom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "title",
    "prompt",
    "url",
    "inputItem"
})
@XmlRootElement(name = "SnomIPPhoneInput")
public class SnomIPPhoneInput
    extends SnomDocument
{

    @XmlElement(name = "Title", required = true)
    protected String title;
    @XmlElement(name = "Prompt", required = true)
    protected String prompt;
    @XmlElement(name = "URL", required = true)
    protected String url;
    @XmlElement(name = "InputItem", required = true)
    protected SnomIPPhoneInput.InputItem inputItem;

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String value) {
        this.prompt = value;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String value) {
        this.url = value;
    }

    public SnomIPPhoneInput.InputItem getInputItem() {
        return inputItem;
    }

    public void setInputItem(SnomIPPhoneInput.InputItem value) {
        this.inputItem = value;
    }

    public void setInputItem(String displayName, String queryStringParam, String defaultValue, String inputFlags) {
        this.inputItem = new InputItem(displayName, queryStringParam, defaultValue, inputFlags);
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "displayName",
        "queryStringParam",
        "defaultValue",
        "inputFlags"
    })
    public static class InputItem {

        @XmlElement(name = "DisplayName", required = true)
        protected String displayName;
        @XmlElement(name = "QueryStringParam", required = true)
        protected String queryStringParam;
        @XmlElement(name = "DefaultValue", required = true)
        protected String defaultValue;
        @XmlElement(name = "InputFlags", required = true)
        protected String inputFlags;

        public InputItem() {
        }

        public InputItem(String displayName, String queryStringParam, String defaultValue, String inputFlags) {
            this.displayName = displayName;
            this.queryStringParam = queryStringParam;
            this.defaultValue = defaultValue;
            this.inputFlags = inputFlags;
        }
        
        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String value) {
            this.displayName = value;
        }

        public String getQueryStringParam() {
            return queryStringParam;
        }

        public void setQueryStringParam(String value) {
            this.queryStringParam = value;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String value) {
            this.defaultValue = value;
        }

        public String getInputFlags() {
            return inputFlags;
        }

        public void setInputFlags(String value) {
            this.inputFlags = value;
        }

    }

}
