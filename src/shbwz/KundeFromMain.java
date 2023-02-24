package shbwz;

import com.mongodb.Block;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.lang.NonNull;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.mongodb.client.model.Filters.eq;

public class KundeFromMain extends JFrame {
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
    private JButton backFromKundenButton;
    private JButton deleteKundeButton;

    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("PizzaShopDB");
    MongoCollection<Document> collection = database.getCollection("kunden");

    DefaultListModel<String> kundenListModel = new DefaultListModel<>();

    public KundeFromMain() {
        setTitle("Kundenverwaltung");
        clientList.setModel(kundenListModel);
        LoadKunden();

        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        backFromKundenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainMenu mainMenu = new MainMenu();
                mainMenu.setVisible(true);
                setVisible(false);
            }
        });

        deleteKundeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrufen des aktuellen Kunden
                String kundeName = (String) clientList.getSelectedValue();

                // Trennen des Vornamens und Nachnamens
                String[] kundeNameParts = kundeName.split(" ");
                String nachname = kundeNameParts[0];
                String vorname = kundeNameParts[1];

                collection.deleteOne(new Document("nachname", nachname).append("vorname", vorname));

                // Aktualisieren des Kunden-Listenmodells
                kundenListModel.removeElement(kundeName);
            }
        });

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

                // Überprüfen ob der Kunde in der Liste schon existiert
                boolean kundeExists = false;
                int index = -1;
                for(int i = 0; i < kundenListModel.size(); i++) {
                    if(kundenListModel.get(i).equals(nachname + " " + vorname)) {
                        kundeExists = true;
                        index = i;
                        break;
                    }
                }

                // Wenn der Kunde schon existiert, wird sie aus der Liste und der Datenbank gelöscht
                if(kundeExists) {
                    kundenListModel.removeElementAt(index);
                    collection.deleteOne(new Document("nachname", nachname).append("vorname", vorname));
                }

                collection.insertOne(kunde);
                kundenListModel.addElement(nachname +" "+ vorname);
            }
        });
    }

    public void LoadKunden() {
        // Alle Pizzas aus der Datenbank lesen
        FindIterable<Document> kunden = collection.find();

        // Kunden in JList anzeigen
        kunden.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                kundenListModel.addElement(document.getString("nachname") + " " + document.getString("vorname")); //anpassen??
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
