package snomproxy.xml.snom;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "title",
    "prompt",
    "directoryEntry"
})
@XmlRootElement(name = "SnomIPPhoneDirectory")
public class SnomIPPhoneDirectory
    extends SnomDocument
{

    @XmlElement(name = "Title", required = true)
    protected String title;
    @XmlElement(name = "Prompt", required = true)
    protected String prompt;
    @XmlElement(name = "DirectoryEntry", required = true)
    protected List<SnomIPPhoneDirectory.DirectoryEntry> directoryEntry;

    public SnomIPPhoneDirectory() {
    }

    public SnomIPPhoneDirectory(String title) {
        this.title = title;
    }

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

    public List<SnomIPPhoneDirectory.DirectoryEntry> getDirectoryEntry() {
        if (directoryEntry == null) {
            directoryEntry = new ArrayList<SnomIPPhoneDirectory.DirectoryEntry>();
        }
        return this.directoryEntry;
    }

    public void addDirectoryEntry(DirectoryEntry entry) {
        this.getDirectoryEntry().add(entry);
    }

    public void addDirectoryEntry(String name,String number) {
        this.getDirectoryEntry().add(new DirectoryEntry(name, number));
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "telephone"
    })
    public static class DirectoryEntry {

        @XmlElement(name = "Name", required = true)
        protected String name;
        @XmlElement(name = "Telephone", required = true)
        protected String telephone;

        public DirectoryEntry() {
        }

        public DirectoryEntry(String name, String telephone) {
            this.name = name;
            this.telephone = telephone;
        }

        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String value) {
            this.telephone = value;
        }

    }

}
