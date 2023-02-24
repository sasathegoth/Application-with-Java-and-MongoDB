package shbwz;

import com.mongodb.Block;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.mongodb.client.model.Filters.eq;

public class Kunde extends JFrame{
    private JPanel mainPanel;
    private JList clientList;
    private JTextField nachnameTextfield;
    private JTextField vornameTextfield;
    private JTextField strasseTextfield;
    private JTextField plzTextfield;
    private JTextField ortTextfield;
    private JTextField telefonTextfield;
    private JTextField emailTextfield;
    private JTextField geburtsdatumTextfield;
    private JButton newKundeButton;
    private JButton bestaetigenButton;

    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("PizzaShopDB");
    MongoCollection<Document> collection = database.getCollection("kunden");
    MongoCollection<Document> collectionBestellung = database.getCollection("bestellungen");

    DefaultListModel<String> kundenListModel = new DefaultListModel<>();

    public Kunde(ObjectId documentId) {
        setTitle("Kunden");
        clientList.setModel(kundenListModel);
        LoadKunden();

        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        newKundeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Werte aus den Textfeldern lesen
                String nachname = nachnameTextfield.getText();
                String vorname = vornameTextfield.getText();
                String strasse = strasseTextfield.getText();
                String plz = plzTextfield.getText();
                String ort = ortTextfield.getText();
                String telefon = telefonTextfield.getText();
                String email = emailTextfield.getText();
                String geburtsdatum = geburtsdatumTextfield.getText();

                // Kunden-Dokument erstellen
                Document kunde = new Document("nachname", nachname)
                        .append("vorname", vorname)
                        .append("strasse", strasse)
                        .append("postleitzahl", plz)
                        .append("ort", ort)
                        .append("telefon", telefon)
                        .append("email", email)
                        .append("geburtsdatum", geburtsdatum);

                // Überprüfen, ob der Kunde in der Liste schon existiert
                boolean kundeExists = false;
                for(int i = 0; i < kundenListModel.size(); i++) {
                    if(kundenListModel.get(i).equals(nachname)) {
                        kundeExists = true;
                        break;
                    }
                }

                // Wenn der Kunde schon existiert, wird er aus der Liste und der Datenbank gelöscht
                if(kundeExists) {
                    kundenListModel.removeElement(nachname);
                    collection.deleteOne(new Document("nachname", nachname));
                }

                collection.insertOne(kunde);
                kundenListModel.addElement(nachname +" "+ vorname);
            }
        });

        bestaetigenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Werte aus den Textfeldern lesen
                String nachname = nachnameTextfield.getText();
                String vorname = vornameTextfield.getText();
                String strasse = strasseTextfield.getText();
                String plz = plzTextfield.getText();
                String ort = ortTextfield.getText();
                String telefon = telefonTextfield.getText();
                String email = emailTextfield.getText();
                String geburtsdatum = geburtsdatumTextfield.getText();

                // Speichern der Werte in einem Dokument
                Document textfieldValuesKunde = new Document("nachname", nachname)
                        .append("vorname", vorname)
                        .append("strasse", strasse)
                        .append("plz", plz)
                        .append("ort", ort)
                        .append("telefon", telefon)
                        .append("email", email)
                        .append("geburtsdatum", geburtsdatum);

                // Speichern der Kundendaten in derselben Bestellung wie die Pizza
                collectionBestellung.updateOne(eq("_id", documentId), new Document("$set", textfieldValuesKunde));

                Bestellungen bestellungenBestellung = new Bestellungen(documentId);
                bestellungenBestellung.setVisible(true);
                setVisible(false);

                JOptionPane.showMessageDialog(null, "Neue Bestellung aufnehmen: \n" +
                        "Fenster zur Aufnahme einer neuen Bestellung (Bestellinformationen) \n" +
                        "Geben Sie die Bestellnummer und das Bestelldatum an \n" +
                        "\u00dcberpr\u00fcfen Sie nochmal die Angaben zu Pizza und Kunde \n" +
                        "Klicken Sie zum Schluss auf Best\u00e4tigen", "Neue Bestellung - Bestellinformationen", 1);
            }
        });
    }

    public void LoadKunden() {
        // Alle Kunden aus der Datenbank lesen
        FindIterable<Document> kunden = collection.find();

        // Kunden in JList anzeigen
        kunden.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                kundenListModel.addElement(document.getString("nachname") + " " + document.getString("vorname"));
            }
        });

        // Event-Handler für die Auswahl eines Kunden in der JList
        clientList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Ausgewählter Kunde ermitteln
                int selectedIndex = clientList.getSelectedIndex();
                if (selectedIndex != -1) {
                    // Kunden-Dokument aus der Datenbank abfragen
                    Bson filter = eq("nachname", kundenListModel.get(selectedIndex).split(" ")[0]);
                    Document kunde = collection.find(filter).first();

                    // Textfelder füllen
                    nachnameTextfield.setText(kunde.getString("nachname"));
                    vornameTextfield.setText(kunde.getString("vorname"));
                    strasseTextfield.setText(kunde.getString("strasse"));
                    plzTextfield.setText(kunde.getString("postleitzahl"));
                    ortTextfield.setText(kunde.getString("ort"));
                    telefonTextfield.setText(kunde.getString("telefon"));
                    emailTextfield.setText(kunde.getString("email"));
                    geburtsdatumTextfield.setText(kunde.getString("geburtsdatum"));
                }
            }
        });
    }
}
