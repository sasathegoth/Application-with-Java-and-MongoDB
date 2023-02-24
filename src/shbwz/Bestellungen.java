package shbwz;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Bestellungen extends JFrame {
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
    private JTextArea pizzenList;
    private JButton bestaetigenButton;

    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("PizzaShopDB");
    MongoCollection<Document> collectionBestellung = database.getCollection("bestellungen");

    private ObjectId documentId;
    DefaultListModel<String> bestellungenListModel = new DefaultListModel<>();

    public Bestellungen(ObjectId documentId) {
        setTitle("Bestellungen");
        this.documentId = documentId;
        LoadPizzaValues(this.documentId);
        LoadKundeValues(this.documentId);
        LoadBestellungen();

        orderList.setModel(bestellungenListModel);
        orderList.setEnabled(false);

        pizzenList.setEditable(false);
        preisTextfield.setEditable(false);
        stuckTextfield.setEditable(false);

        nachnameTextfield.setEditable(false);
        vornameTextfield.setEditable(false);
        strasseTextfield.setEditable(false);
        plzTextfield.setEditable(false);
        ortTextfield.setEditable(false);
        telefonTextfield.setEditable(false);
        emailTextfield.setEditable(false);
        geburtsdatumTextfield.setEditable(false);

        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        bestaetigenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Werte aus den Textfeldern lesen
                String bestellnr = bestellNrTextfield.getText();
                String bestelldatum = bestelldatumTextfield.getText();

                //Speichern der Werte in einem Dokument
                Document textfieldValuesBestellung = new Document("bestellnr", bestellnr)
                        .append("bestelldatum", bestelldatum);

                // Speichern der Bestelldaten in derselben Bestellung wie die Pizza und der Kunde
                collectionBestellung.updateOne(eq("_id", documentId), new Document("$set", textfieldValuesBestellung));

                JOptionPane.showMessageDialog(null,"Bestellung erfolgreich","Bestellbestätigung",1);
                setVisible(false);
                MainMenu menu = new MainMenu();
                menu.setVisible(true);
            }
        });
    }

    public void LoadBestellungen() {
        // Alle Bestellungen aus der Datenbank lesen
        FindIterable<Document> bestellungen = collectionBestellung.find();

        // Bestellungen in JList anzeigen
        bestellungen.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                bestellungenListModel.addElement(document.getString("bestellnr") + " || " + document.getString("bestelldatum"));
            }
        });
    }

    public void LoadPizzaValues(ObjectId documentId) {
        // Abrufen des Dokuments aus der Datenbank
        Document textfieldValues = collectionBestellung.find(new Document("_id", documentId)).first();

        // Setzen der Werte in die Textfelder
        String pizzaName = textfieldValues.getString("pizzaName");
        String extrazutaten = textfieldValues.getString("extrazutaten");

        String pizza = pizzaName + " mit\n" + extrazutaten;
        pizzenList.setText(pizza);

        preisTextfield.setText(textfieldValues.getString("preis"));
        stuckTextfield.setText("1");
    }

    public void LoadKundeValues(ObjectId documentId) {
        // Abrufen des Dokuments aus der Datenbank
        Document textfieldValuesKunde = collectionBestellung.find(new Document("_id", documentId)).first();

        //Setzen der Werte in die Textfelder
        nachnameTextfield.setText(textfieldValuesKunde.getString("nachname"));
        vornameTextfield.setText(textfieldValuesKunde.getString("vorname"));
        strasseTextfield.setText(textfieldValuesKunde.getString("strasse"));
        plzTextfield.setText(textfieldValuesKunde.getString("plz"));
        ortTextfield.setText(textfieldValuesKunde.getString("ort"));
        telefonTextfield.setText(textfieldValuesKunde.getString("telefon"));
        emailTextfield.setText(textfieldValuesKunde.getString("email"));
        geburtsdatumTextfield.setText(textfieldValuesKunde.getString("geburtsdatum"));
    }
}
