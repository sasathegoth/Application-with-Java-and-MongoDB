package shbwz;

import com.mongodb.Block;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class BestellungenFromMain extends JFrame {
    private JPanel mainPanel;
    private JList orderList;
    private JTextField bestellNrTextfield;
    private JTextField bestelldatumTextfield;
    private JTextField preisTextfield;
    private JTextField stuckTextfield;
    private JTextField nachnameTextfield;
    private JTextField vornameTextfield;
    private JTextField strasseTextfield;
    private JTextField plzTextfield;
    private JTextField ortTextfield;
    private JTextField telefonTextfield;
    private JTextField emailTextfield;
    private JTextField geburtsdatumTextfield;
    private JList pizzenList;
    private JButton loeschenButton;
    private JButton changeBestelldatenButton;
    private JButton backBestellungenButton;

    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("PizzaShopDB");

    MongoCollection<Document> collection = database.getCollection("bestellungen");

    DefaultListModel<String> bestellungenListModel = new DefaultListModel<>();

    public BestellungenFromMain() {
        setTitle("Bestellungsverwaltung");
        orderList.setModel(bestellungenListModel);
        LoadBestellungen();

        bestellNrTextfield.setEditable(false);
        bestelldatumTextfield.setEditable(false);

        pizzenList.setEnabled(false);
        preisTextfield.setEditable(false);
        stuckTextfield.setEditable(false);

        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
    }

    public void LoadBestellungen() {
        // Alle Bestellungen aus der Datenbank lesen
        FindIterable<Document> bestellungen = collection.find();

        // Bestellungen in JList anzeigen
        bestellungen.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                bestellungenListModel.addElement(document.getString("bestellnr") + " || " + document.getString("bestelldatum"));
            }
        });

        backBestellungenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainMenu mainMenu = new MainMenu();
                mainMenu.setVisible(true);
                setVisible(false);
            }
        });

        loeschenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrufen des aktuellen Bestelldatensatzes
                String bestellDaten = (String) orderList.getSelectedValue();

                // Trennen der Bestellnr und des Bestelldatums
                String[] bestellDatenParts = bestellDaten.split(" \\|\\| ");
                String bestellnr = bestellDatenParts[0];
                String bestelldatum = bestellDatenParts[1];

                // Löschen des Bestelldatensatzes aus der Datenbank
                collection.deleteOne(new Document("bestellnr", bestellnr).append("bestelldatum", bestelldatum));

                // Aktualisieren des Bestellungen-Listenmodells
                bestellungenListModel.removeElement(bestellDaten);
            }
        });

        changeBestelldatenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // Event-Handler für die Auswahl eine Bestellung in der JList
        orderList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Ausgewählte Bestellung ermitteln
                int selectedIndex = orderList.getSelectedIndex();
                if (selectedIndex != -1) {
                    // Bestell-Dokument aus der Datenbank abfragen
                    Bson filter = eq("bestellnr", bestellungenListModel.get(selectedIndex).split(" ")[0]);
                    Document bestellungen = collection.find(filter).first();

                    // Setzen der Werte in die Textfelder
                    List<String> pizzas = new ArrayList<>();
                    String pizzaName = bestellungen.getString("pizzaName");
                    String extrazutaten = bestellungen.getString("extrazutaten");

                    String pizza = pizzaName + " mit\n" + extrazutaten;
                    pizzas.add(pizza);
                    pizzenList.setListData(pizzas.toArray());

                    // Textfelder füllen
                    bestellNrTextfield.setText(bestellungen.getString("bestellnr"));
                    bestelldatumTextfield.setText(bestellungen.getString("bestelldatum"));
                    preisTextfield.setText(bestellungen.getString("preis"));
                    nachnameTextfield.setText(bestellungen.getString("nachname"));
                    vornameTextfield.setText(bestellungen.getString("vorname"));
                    strasseTextfield.setText(bestellungen.getString("strasse"));
                    plzTextfield.setText(bestellungen.getString("postleitzahl"));
                    ortTextfield.setText(bestellungen.getString("ort"));
                    telefonTextfield.setText(bestellungen.getString("telefon"));
                    emailTextfield.setText(bestellungen.getString("email"));
                    geburtsdatumTextfield.setText(bestellungen.getString("geburtsdatum"));
                }
            }
        });
    }
}
