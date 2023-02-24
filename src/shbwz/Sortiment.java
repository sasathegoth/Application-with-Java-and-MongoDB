package shbwz;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mongodb.Block;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Sortiment extends JFrame{
    private JPanel mainAuswahlPanel;
    private JList pizzenList;
    private JTextField pizzaNameTextfield;
    private JTextField zutatenTextfield;
    private JButton newPizzaButton;
    private JTextField kleinPreisTextfield;
    private JTextField mittelPreisTextfield;
    private JTextField grossPreisTextfield;
    private JTextField kleinKcalTextfield;
    private JTextField mittelKcalTextfield;
    private JTextField grossKcalTextfield;
    private JTextField kleinDurchmesserTextfield;
    private JTextField mittelDurchmesserTextfield;
    private JTextField grossDurchmesserTextfield;
    private JButton backFromSortimentButton;
    private JButton deletePizzaButton;

    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("PizzaShopDB");
    MongoCollection<Document> collection = database.getCollection("pizzas");

    DefaultListModel<String> pizzenListModel = new DefaultListModel<>();

    public Sortiment() {
        setTitle("Pizza Sortiment");
        pizzenList.setModel(pizzenListModel);
        LoadPizzas();

        this.setContentPane(mainAuswahlPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        backFromSortimentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainMenu mainMenu = new MainMenu();
                mainMenu.setVisible(true);
                setVisible(false);
            }
        });

        deletePizzaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrufen des aktuellen Pizzanamen
                String pizzaName = (String) pizzenList.getSelectedValue();

                collection.deleteOne(new Document("name", pizzaName));

                // Aktualisieren des Pizza-Listenmodells
                pizzenListModel.removeElement(pizzaName);

            }
        });

        newPizzaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Werte aus den Textfeldern lesen
                String pizzaName = pizzaNameTextfield.getText();
                String zutaten = zutatenTextfield.getText();
                String preisKlein = kleinPreisTextfield.getText();
                String preisMittel = mittelPreisTextfield.getText();
                String preisGross = grossPreisTextfield.getText();
                String kcalKlein = kleinKcalTextfield.getText();
                String kcalMittel = mittelKcalTextfield.getText();
                String kcalGross = grossKcalTextfield.getText();
                String durchmesserKlein = kleinDurchmesserTextfield.getText();
                String durchmesserMittel = mittelDurchmesserTextfield.getText();
                String durchmesserGross = grossDurchmesserTextfield.getText();

                // Grössen-Dokument erstellen
                Document klein = new Document("groesse", "klein")
                        .append("preis", preisKlein)
                        .append("durchmesser", durchmesserKlein)
                        .append("kcal", kcalKlein);
                Document mittel = new Document("groesse", "mittel")
                        .append("preis", preisMittel)
                        .append("durchmesser", durchmesserMittel)
                        .append("kcal", kcalMittel);
                Document gross = new Document("groesse", "gross")
                        .append("preis", preisGross)
                        .append("durchmesser", durchmesserGross)
                        .append("kcal", kcalGross);

                List<Document> groessen = new ArrayList<>();
                groessen.add(klein);
                groessen.add(mittel);
                groessen.add(gross);

                // Pizza-Dokument erstellen
                Document pizza = new Document("name", pizzaName)
                        .append("zutaten", zutaten)
                        .append("groessen", groessen);

                // Überprüfen, ob die Pizza in der Liste schon existiert
                boolean pizzaExists = false;
                for(int i = 0; i < pizzenListModel.size(); i++) {
                    if(pizzenListModel.get(i).equals(pizzaName)) {
                        pizzaExists = true;
                        break;
                    }
                }

                // Wenn die Pizza schon existiert, wird sie aus der Liste und der Datenbank gelöscht
                if(pizzaExists) {
                    pizzenListModel.removeElement(pizzaName);
                    collection.deleteOne(new Document("name", pizzaName));
                }

                // Pizza in die Datenbank speichern
                collection.insertOne(pizza);
                pizzenListModel.addElement(pizzaName);
            }
        });
    }

    public void LoadPizzas() {
        // Alle Pizzas aus der Datenbank lesen
        FindIterable<Document> pizzas = collection.find();

        // Pizzas in JList anzeigen
        pizzas.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                pizzenListModel.addElement(document.getString("name"));
            }
        });

        // Event-Handler für die Auswahl einer Pizza in der JList
        pizzenList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Ausgewählte Pizza ermitteln
                int selectedIndex = pizzenList.getSelectedIndex();
                if (selectedIndex != -1) {
                    // Pizza-Dokument aus der Datenbank abfragen
                    Bson filter = eq("name", pizzenListModel.get(selectedIndex));
                    Document pizza = collection.find(filter).first();

                    // Textfelder füllen
                    pizzaNameTextfield.setText(pizza.getString("name"));
                    zutatenTextfield.setText(pizza.getString("zutaten"));
                    List<Document> groessen = pizza.get("groessen", List.class);
                    kleinPreisTextfield.setText(groessen.get(0).getString("preis"));
                    mittelPreisTextfield.setText(groessen.get(1).getString("preis"));
                    grossPreisTextfield.setText(groessen.get(2).getString("preis"));
                    kleinKcalTextfield.setText(groessen.get(0).getString("kcal"));
                    mittelKcalTextfield.setText(groessen.get(1).getString("kcal"));
                    grossKcalTextfield.setText(groessen.get(2).getString("kcal"));
                    kleinDurchmesserTextfield.setText(groessen.get(0).getString("durchmesser"));
                    mittelDurchmesserTextfield.setText(groessen.get(1).getString("durchmesser"));
                    grossDurchmesserTextfield.setText(groessen.get(2).getString("durchmesser"));
                }
            }
        });
    }
}
