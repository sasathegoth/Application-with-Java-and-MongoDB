package shbwz;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Pizzabestellung extends JFrame {
    private JList pizzenOrderList;
    private JCheckBox ananasCheckBox;
    private JCheckBox cherrytomatenCheckBox;
    private JCheckBox auberginenCheckBox;
    private JCheckBox artischockenCheckBox;
    private JCheckBox kapernCheckBox;
    private JCheckBox hinterschinkenCheckBox;
    private JCheckBox thonCheckBox;
    private JCheckBox olivenCheckBox;
    private JCheckBox rucolaCheckBox;
    private JCheckBox knoblauchCheckBox;
    private JCheckBox basilikumCheckBox;
    private JCheckBox mozzarellaCheckBox;
    private JCheckBox champignonsCheckBox;
    private JCheckBox lachsCheckBox;
    private JCheckBox rindfleischCheckBox;
    private JCheckBox truthahnfleischCheckBox;
    private JCheckBox pouletfleischCheckBox;
    private JCheckBox peperoniCheckBox;
    private JCheckBox zuchettiCheckBox;
    private JCheckBox zwiebelnCheckBox;
    private JCheckBox speckCheckBox;
    private JCheckBox maisCheckBox;
    private JCheckBox salamiMildCheckBox;
    private JCheckBox salamiScharfCheckBox;
    private JCheckBox sardellenCheckBox;
    private JCheckBox meeresfruchtCheckBox;
    private JCheckBox blattspinatCheckBox;
    private JCheckBox eiCheckBox;
    private JPanel mainPanel;
    private JTextField pizzaNameTextfield;
    private JTextField zutatenTextfield;
    private JTextField preisTextfield;
    private JTextField kcalTextfield;
    private JRadioButton kleinRadioButton;
    private JRadioButton mittelRadioButton;
    private JRadioButton grossRadioButton;
    private JList extrasList;
    private JButton finishOrderButton;

    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("PizzaShopDB");
    MongoCollection<Document> collectionPizza = database.getCollection("pizzas");
    MongoCollection<Document> collectionBestellung = database.getCollection("bestellungen");

    DefaultListModel<String> pizzenOrderListModel = new DefaultListModel<>();

    private ArrayList<String> extras = new ArrayList<>();


    public Pizzabestellung() {
        setTitle("Pizzas");
        pizzenOrderList.setModel(pizzenOrderListModel);
        LoadPizzasFuerBestellung();
        FillExtrasList();

        pizzaNameTextfield.setEditable(false);
        zutatenTextfield.setEditable(false);
        preisTextfield.setEditable(false);
        kcalTextfield.setEditable(false);

        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        finishOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pizzaName = pizzaNameTextfield.getText();
                String zutaten = zutatenTextfield.getText();
                String preis = preisTextfield.getText();
                String kcal = kcalTextfield.getText();

                ListModel model = extrasList.getModel();
                List<String> extrasListe = new ArrayList<>();
                for (int i = 0; i < model.getSize(); i++) {
                    extrasListe.add((String) model.getElementAt(i));
                }


                // Konvertieren der Liste in eine JSON-Zeichenfolge
                Gson gson = new Gson();
                String extrasListeJson = gson.toJson(extrasListe);

                // Speichern der Werte in einem Dokument
                Document textfieldValuesPizza = new Document("pizzaName", pizzaName)
                        .append("zutaten", zutaten)
                        .append("preis", preis)
                        .append("kcal", kcal)
                        .append("extrazutaten", extrasListeJson);

                // Speichern des Dokuments in der Datenbank
                collectionBestellung.insertOne(textfieldValuesPizza);

                ObjectId documentId = textfieldValuesPizza.getObjectId("_id");
                Kunde kundeBestellung = new Kunde(documentId);
                kundeBestellung.setVisible(true);
                setVisible(false);

                JOptionPane.showMessageDialog(null, "Neue Bestellung aufnehmen: \n" +
                        "Fenster zur Aufnahme einer neuen Bestellung (Kunde) \n" +
                        "W\u00e4hlen Sie einen vorhandenen Kunden aus der Liste aus \n" +
                        "oder erstellen Sie einen neuen Kunden \n" +
                        "M\u00fcssen Kundendaten aktualisiert werden, passen Sie die entsprechenden Daten an und klicken Aktualisieren \n" +
                        "Klicken Sie zum Schluss auf Bestellung abschliessen", "Neue Bestellung - Kunde", 1);
            }
        });
    }

    public void LoadPizzasFuerBestellung() {
        // Alle Pizzas aus der Datenbank lesen
        FindIterable<Document> pizzas = collectionPizza.find();

        // Pizzas in JList anzeigen
        pizzas.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                pizzenOrderListModel.addElement(document.getString("name"));
            }
        });

        // Event-Handler für die Auswahl einer Pizza in der JList
        pizzenOrderList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Ausgewählte Pizza ermitteln
                int selectedIndex = pizzenOrderList.getSelectedIndex();
                if (selectedIndex != -1) {
                    // Pizza-Dokument aus der Datenbank abfragen
                    Bson filter = eq("name", pizzenOrderListModel.get(selectedIndex));
                    Document pizza = collectionPizza.find(filter).first();

                    // Textfelder füllen
                    pizzaNameTextfield.setText(pizza.getString("name"));
                    zutatenTextfield.setText(pizza.getString("zutaten"));
                }
            }
        });

        // Event-Handler für die Auswahl einer Grösse mittels Radio-Button
        ActionListener sizeSelectionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = pizzenOrderList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Bson filter = eq("name", pizzenOrderListModel.get(selectedIndex));
                    Document pizza = collectionPizza.find(filter).first();
                    List<Document> groessen = pizza.get("groessen", List.class);
                    int sizeIndex;
                    if(kleinRadioButton.isSelected()) {
                        sizeIndex = 0;
                    } else if (mittelRadioButton.isSelected()) {
                        sizeIndex = 1;
                    } else {
                        sizeIndex = 2;
                    }
                    preisTextfield.setText(groessen.get(sizeIndex).getString("preis"));
                    kcalTextfield.setText(groessen.get(sizeIndex).getString("kcal"));
                }
            }
        };
        kleinRadioButton.addActionListener(sizeSelectionListener);
        mittelRadioButton.addActionListener(sizeSelectionListener);
        grossRadioButton.addActionListener(sizeSelectionListener);
    }

    public void FillExtrasList() {
        List<JCheckBox> checkBoxList = new ArrayList<>();
        checkBoxList.add(ananasCheckBox);
        checkBoxList.add(cherrytomatenCheckBox);
        checkBoxList.add(auberginenCheckBox);
        checkBoxList.add(artischockenCheckBox);
        checkBoxList.add(kapernCheckBox);
        checkBoxList.add(hinterschinkenCheckBox);
        checkBoxList.add(thonCheckBox);
        checkBoxList.add(olivenCheckBox);
        checkBoxList.add(rucolaCheckBox);
        checkBoxList.add(knoblauchCheckBox);
        checkBoxList.add(blattspinatCheckBox);
        checkBoxList.add(basilikumCheckBox);
        checkBoxList.add(mozzarellaCheckBox);
        checkBoxList.add(champignonsCheckBox);
        checkBoxList.add(eiCheckBox);
        checkBoxList.add(lachsCheckBox);
        checkBoxList.add(rindfleischCheckBox);
        checkBoxList.add(truthahnfleischCheckBox);
        checkBoxList.add(pouletfleischCheckBox);
        checkBoxList.add(peperoniCheckBox);
        checkBoxList.add(zuchettiCheckBox);
        checkBoxList.add(zwiebelnCheckBox);
        checkBoxList.add(speckCheckBox);
        checkBoxList.add(maisCheckBox);
        checkBoxList.add(salamiMildCheckBox);
        checkBoxList.add(salamiScharfCheckBox);
        checkBoxList.add(sardellenCheckBox);
        checkBoxList.add(meeresfruchtCheckBox);

        for (JCheckBox checkBox : checkBoxList) {
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String pizzaPreisText = preisTextfield.getText();
                    double pizzaPreis = Double.parseDouble(pizzaPreisText);

                    if (checkBox.isSelected()) {
                        extras.add(checkBox.getText());
                        extrasList.setListData(extras.toArray());

                        int indexPlus = checkBox.getText().lastIndexOf("+");
                        int indexSpace = checkBox.getText().lastIndexOf(" ");
                        String preisText = checkBox.getText().substring(indexPlus + 1, indexSpace);
                        double extraPreis = Double.parseDouble(preisText);
                        pizzaPreis += extraPreis;
                    } else {
                        extras.remove(checkBox.getText());
                        extrasList.setListData(extras.toArray());

                        int indexPlus = checkBox.getText().lastIndexOf("+");
                        int indexSpace = checkBox.getText().lastIndexOf(" ");
                        String preisText = checkBox.getText().substring(indexPlus + 1, indexSpace);
                        double extraPreis = Double.parseDouble(preisText);
                        pizzaPreis -= extraPreis;
                    }

                    String endPreis = String.format("%.2f", pizzaPreis);
                    preisTextfield.setText(endPreis);
                }
            });

        }
    }
}
