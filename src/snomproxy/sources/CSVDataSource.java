package snomproxy.sources;

import com.csvreader.CsvReader;
import java.io.*;
import java.util.HashMap;
import snomproxy.data.Data;
import snomproxy.data.contacts.Contact;
import snomproxy.data.contacts.ContactList;
import snomproxy.server.Server;

/**
 * Ein lokale CSV-Datenquelle - PoC
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class CSVDataSource extends ContactSource {

    private CsvReader csvReader;
//    private String[] cols;
    private int col_name = 2;
    private int col_firstname = 3;
    private int col_phone_privat = 4;
    private int col_phone_geschaeftlich = 5;

    public CSVDataSource() {
    }

    public CSVDataSource(String file) throws FileNotFoundException, IOException {
        this.readCSV(file);
    }

    public void readCSV(String file) throws FileNotFoundException, IOException {
        contacts = new ContactList();
        this.csvReader = new CsvReader(file);

        this.csvReader.readHeaders();
//        this.cols = this.csvReader.getHeaders();

        int i = 0;
        String[] values;
        while (this.csvReader.readRecord()) {
            values = this.csvReader.getValues();
            //this.data.add(values);
            Contact contact = new Contact(String.valueOf(i++));
            contact.setFirstname(values[this.col_firstname]);
            contact.setLastname(values[this.col_name]);

            HashMap<String, String> phones = new HashMap<String, String>();
            if (!values[this.col_phone_privat].isEmpty()) {
                phones.put("privat", values[this.col_phone_privat]);
            }
            if (!values[this.col_phone_geschaeftlich].isEmpty()) {
                phones.put("geschaeft", values[this.col_phone_geschaeftlich]);
            }
            contact.setPhones(phones);

            contacts.addContact(contact);
        }

        this.csvReader.close();
    }

    public int getColFirstname() {
        return col_firstname;
    }

    public void setColFirstname(int col_firstname) {
        this.col_firstname = col_firstname;
    }

    public int getColName() {
        return col_name;
    }

    public void setColName(int col_name) {
        this.col_name = col_name;
    }

    public int getColPhoneGeschaeftlich() {
        return col_phone_geschaeftlich;
    }

    public void setColPhoneGeschaeftlich(int col_phone_geschaeftlich) {
        this.col_phone_geschaeftlich = col_phone_geschaeftlich;
    }

    public int getColPhonePrivat() {
        return col_phone_privat;
    }

    public void setColPhonePrivat(int col_phone_privat) {
        this.col_phone_privat = col_phone_privat;
    }

    @Override
    public Data request(String data) {
        HashMap<String, String> dataMap = Server.splitData(data);
        ContactList entries;

        //System.out.println("csv request; action: "+action+" data: "+data);
        ContactList out = new ContactList();
        out.addLink("Index", snomproxy.SnomProxy.getServer().getAddressString().concat("/snom/"));
        out.addLink("CSV Menü", snomproxy.SnomProxy.getServer().getAddressString().concat("/snom/?menu=csv"));
        out.addLink("Suche", snomproxy.SnomProxy.getServer().getAddressString().concat("/snom/?menu=csv_suche"));
        if (dataMap.containsKey("search") && !dataMap.get("search").isEmpty()) {
            out.setTitle("Suche \"".concat(dataMap.get("search")).concat("\""));
            out.addLink("Alles", snomproxy.SnomProxy.getServer().getAddressString().concat("/csv"));
            entries = search(dataMap.get("search"));
        } else {
            out.setTitle("Lokale Kontakte");
            entries = this.contacts;
        }
		out.setContacts(entries.getContacts());

        return out;
    }
}
