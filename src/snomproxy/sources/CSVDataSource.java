package snomproxy.sources;

import com.csvreader.CsvReader;
import java.io.*;
import java.util.ArrayList;
import snomproxy.xml.snom.SnomDocument;
import snomproxy.xml.snom.SnomIPPhoneDirectory;

/**
 * Ein lokale CSV-Datenquelle - PoC
 *
 * @author Fabian Dillmeier <fabian at dillmeier.de>
 */
public class CSVDataSource implements DataSource {

    private CsvReader csvReader;
    private ArrayList<String[]> data;
    private String[] cols;
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
        this.data = new ArrayList<String[]>();
        this.csvReader = new CsvReader(file);

        this.csvReader.readHeaders();
        this.cols = this.csvReader.getHeaders();

        while (this.csvReader.readRecord()) {
            this.data.add(this.csvReader.getValues());
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
    public SnomDocument request(String data) {
        //System.out.println("csv request; action: "+action+" data: "+data);
        SnomDocument out;
        data=data.replaceFirst("param=", "");
        out = new SnomIPPhoneDirectory("Suche " + data);
        out.addSoftKeyItem("index","Index", snomproxy.SnomProxy.getServer().getAddressString().concat("/"));
        out.addSoftKeyItem("csv_menu","CSV Menü", snomproxy.SnomProxy.getServer().getAddressString().concat("/?menu=csv"));
        out.addSoftKeyItem("csv_suche","Suche", snomproxy.SnomProxy.getServer().getAddressString().concat("/?menu=csv_suche"));
        if (!data.isEmpty()){
            out.addSoftKeyItem("taste1", "Alles", snomproxy.SnomProxy.getServer().getAddressString().concat("/csv"));
        }
        if (this.data != null && this.data.size() > 0) {
            for (int i = 0; i < this.data.size(); i++) {
                for (int j = 0; j < this.data.get(i).length; j++) {
                    if (this.data.get(i)[j].contains(data)) {
                        String name = this.data.get(i)[this.col_name].concat(((this.col_firstname<0 || this.data.get(i)[this.col_firstname].isEmpty()) ? "" : ", ".concat(this.data.get(i)[this.col_firstname])));
                        if (!this.data.get(i)[this.col_phone_privat].isEmpty()) {
                            ((SnomIPPhoneDirectory) out).addDirectoryEntry(name + " (privat)", this.data.get(i)[this.col_phone_privat]);
                        }
                        if (!this.data.get(i)[this.col_phone_geschaeftlich].isEmpty()) {
                            ((SnomIPPhoneDirectory) out).addDirectoryEntry(name + " (gesch.)", this.data.get(i)[this.col_phone_geschaeftlich]);
                        }
                        j = this.data.get(i).length;
                    }
                }
            }
        }
        return out;
    }
}
