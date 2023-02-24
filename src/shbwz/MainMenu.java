package shbwz;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    private JPanel mainMainPanel;
    private JButton auswahlFromMainButton;
    private JButton orderFromMainButton;
    private JButton clientFromMainButton;
    private JButton newOrderFromMainButton;

    public MainMenu() {
        Pizzabestellung pizza = new Pizzabestellung();
        BestellungenFromMain bestellungenFromMain = new BestellungenFromMain();
        KundeFromMain kundeFromMain = new KundeFromMain();
        Sortiment sortimentFromMain = new Sortiment();

        setTitle("MainMenu");
        this.setContentPane(mainMainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        setVisible(true);

        newOrderFromMainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pizza.setVisible(true);
                setVisible(false);
                JOptionPane.showMessageDialog(null, "Neue Bestellung aufnehmen: \n" +
                        "Fenster zur Aufnahme einer neuen Bestellung (Pizza) \n" +
                        "W\u00e4hlen Sie zuerst die vom Kunden gew\u00fcnschte Pizza aus der Liste \n" +
                        "W\u00e4hlen Sie dann die gew\u00fcnschte Gr\u00f6sse \n" +
                        "W\u00e4hlen Sie nun noch allenfalls gew\u00fcnschte Extrazutaten und klicken dann Bestellung abschliessen", "Neue Bestellung - Pizza", 1);
            }
        });

        orderFromMainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bestellungenFromMain.setVisible(true);
                setVisible(false);
                JOptionPane.showMessageDialog(null,"Bestellungsverwaltung:\n" +
                        "Fenster zur Verwaltung von vorhandenen Bestellungen \n" +
                        "Um eine neue Bestellung aufzunehmen, w\u00e4hlen Sie bitte Neue Bestellung im Hauptmen\u00fc \n" +
                        "Um eine vorhandene Bestellung zu aktualisieren, \u00e4ndern Sie die entsprechenden Daten und klicken Bestelldaten \u00e4ndern \n" +
                        "Um eine vorhandene Bestellung zu l\u00f6schen, w\u00e4hlen Sie sie in der Liste aus und klicken L\u00f6schen","Bestellungverwaltung - Informationen",1);
            }
        });

        clientFromMainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kundeFromMain.setVisible(true);
                setVisible(false);
                JOptionPane.showMessageDialog(null, "Kundenverwaltung: \n" +
                        "Fenster zur Verwaltung von Kunden \n" +
                        "Um einen neuen Kunden hinzuzuf\u00fcgen, f\u00fcllen Sie die Felder aus und klicken Neuer Kunde\n" +
                        "Um einen vorhandenen Kunden zu aktualisieren, \u00e4ndern sie die entsprechenden Felder und klicken Vorhandener Kunde aktualisieren\n" +
                        "Um einen vorhandenen Kunden zu l\u00f6schen, w\u00e4hlen Sie ihn in der Liste aus und klicken Kunde l\u00f6schen", "Kundenverwaltung - Informationen", 1);
            }
        });

        auswahlFromMainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortimentFromMain.setVisible(true);
                setVisible(false);
                JOptionPane.showMessageDialog(null, "Sortiment: \n" +
                        "Fenster zur Verwaltung von Pizzen \n" +
                        "Um eine neue Pizza hinzuzuf\u00fcgen, f\u00fcllen Sie die Felder aus und klicken Neue Pizza hinzuf\u00fcgen \n" +
                        "Um eine vorhandene Pizza zu aktualisieren, \u00e4ndern Sie die entsprechenden Felder und klicken Anpassung speichern \n" +
                        "Um eine vorhandene Pizza zu l\u00f6schen, w\u00e4hlen Sie sie in der Liste aus und klicken Pizza l\u00f6schen", "Sortiment - Informationen", 1);
            }
        });
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}
