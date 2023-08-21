package sn.diti3.diti3project.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import sn.diti3.diti3project.DB.DBConnexion;
import sn.diti3.diti3project.entity.Livre;
import sn.diti3.diti3project.tools.Notification;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LivreController implements Initializable {

    @FXML
    private TableColumn<Livre, String> auteurCol;

    @FXML
    private Button emprunterBtn;

    @FXML
    private Button rendreBtn;

    @FXML
    private TextField auteurTfd;

    @FXML
    private Button effacerBtn;

    @FXML
    private Button enregistrerBtn;

    @FXML
    private TableColumn<Livre, Integer> idCol;

    @FXML
    private TableColumn<Livre, String> isbnCol;

    @FXML
    private TextField isbnTfd;

    @FXML
    private TableView<Livre> livreTb;

    @FXML
    private Button modifierBtn;

    @FXML
    private TableColumn<Livre, Integer> nbPagesCol;

    @FXML
    private TextField nbPagesTfd;

    @FXML
    private Button supprimerBtn;

    @FXML
    private TableColumn<Livre, String> titreCol;

    @FXML
    private TextField titreTfd;

    private DBConnexion db = new DBConnexion();
    private int id = 0;

    public ObservableList<Livre> getLivres(){
        ObservableList<Livre> livres = FXCollections.observableArrayList();
        String sql = "SELECT * FROM livre ORDER BY auteur";
        try {
            db.initPrepar(sql);
            ResultSet rs = db.executeSelect();
            while(rs.next()){
                Livre livre = new Livre();
                livre.setId(rs.getInt("id"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setIsbn(rs.getString("isbn"));
                livre.setEtatEmprunt(rs.getInt("etat_emprunt"));
                livre.setNbPages(rs.getInt("nb_pages"));
                livres.add(livre);
            }
            db.closeConnection();
        }catch (SQLException e){
            throw new RuntimeException();
        }
        return livres;
    }

    public void loadTable(){
        ObservableList<Livre> livres = getLivres();
        livreTb.setItems(livres);
        idCol.setCellValueFactory(new PropertyValueFactory<Livre, Integer>("id"));
        titreCol.setCellValueFactory(new PropertyValueFactory<Livre, String>("titre"));
        auteurCol.setCellValueFactory(new PropertyValueFactory<Livre, String>("auteur"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<Livre, String>("isbn"));
        nbPagesCol.setCellValueFactory(new PropertyValueFactory<Livre, Integer>("nbPages"));
    }

    @FXML
    void delete(ActionEvent event) {
        String sql = "DELETE FROM livre WHERE id = ?";
        try {
            db.initPrepar(sql);
            //Passage de valeurs
            db.getPstm().setInt(1, id);
            int ok = db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            enregistrerBtn.setDisable(false);
            Notification.NotifSuccess("Succés !", "Le livre a été bien supprimé");
        }catch (SQLException e){
            throw new RuntimeException();
        }
    }

    @FXML
    void save(ActionEvent event) {
        String sql = "INSERT INTO livre(id,titre,auteur,isbn,nb_pages) VALUES(null, ?, ?, ?, ?)";
        try {
            db.initPrepar(sql);
            //Passage de valeurs
            db.getPstm().setString(1, titreTfd.getText());
            db.getPstm().setString(2, auteurTfd.getText());
            db.getPstm().setString(3, isbnTfd.getText());
            db.getPstm().setInt(4, Integer.parseInt(nbPagesTfd.getText()));
            int ok = db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succés !", "Le livre a été bien enregistré");
        }catch (SQLException e){
            throw new RuntimeException();
        }
    }

    public void clearFields(){
        titreTfd.setText("");
        auteurTfd.setText("");
        isbnTfd.setText("");
        nbPagesTfd.setText("");
    }

    @FXML
    void clear(ActionEvent event) {
        clearFields();
    }

    @FXML
    void getData(MouseEvent event) {
        Livre livre = livreTb.getSelectionModel().getSelectedItem();
        id = livre.getId();
        titreTfd.setText(livre.getTitre());
        auteurTfd.setText(livre.getAuteur());
        isbnTfd.setText(livre.getIsbn());
        nbPagesTfd.setText(String.valueOf(livre.getNbPages()));
        enregistrerBtn.setDisable(true);
        if (getLivre(id).getEtatEmprunt() == 0){
            rendreBtn.setDisable(true);
            emprunterBtn.setDisable(false);
        } else{
            rendreBtn.setDisable(false);
            emprunterBtn.setDisable(true);
       }
    }

    @FXML
    void update(ActionEvent event) {
        String sql = "UPDATE livre SET titre = ?, auteur = ?, isbn = ?, nb_pages = ? WHERE id = ?";
        try {
            db.initPrepar(sql);
            //Passage de valeurs
            db.getPstm().setString(1, titreTfd.getText());
            db.getPstm().setString(2, auteurTfd.getText());
            db.getPstm().setString(3, isbnTfd.getText());
            db.getPstm().setInt(4, Integer.parseInt(nbPagesTfd.getText()));
            db.getPstm().setInt(5, id);
            int ok = db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            enregistrerBtn.setDisable(false);
            Notification.NotifSuccess("Succés !", "Le livre a été bien modifié");
        }catch (SQLException e){
            throw new RuntimeException();
        }
    }

    public Livre getLivre(int id){
        Livre livre = null;
        String sql = "SELECT * FROM livre WHERE id = ?";
        try {
            db.initPrepar(sql);
            db.getPstm().setInt(1, id);
            ResultSet rs = db.executeSelect();
            if(rs.next()){
                livre = new Livre();
                livre.setId(rs.getInt("id"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setIsbn(rs.getString("isbn"));
                livre.setEtatEmprunt(rs.getInt("etat_emprunt"));
                livre.setNbPages(rs.getInt("nb_pages"));
            }
            db.closeConnection();
        }catch (SQLException e){
            throw new RuntimeException();
        }
        return livre;
    }

    @FXML
    void emprunt(ActionEvent event) {
            String sql = "UPDATE livre SET etat_emprunt = 1 WHERE id = ?";
            try {
                db.initPrepar(sql);
                //Passage de valeurs
                db.getPstm().setInt(1, id);
                int ok = db.executeMaj();
                db.closeConnection();
                loadTable();
                clearFields();
                enregistrerBtn.setDisable(false);
                rendreBtn.setDisable(false);
                Notification.NotifSuccess("Succés !", "Le livre a été emprunté");
            }catch (SQLException e){
                throw new RuntimeException();
            }

    }


    @FXML
    void rendre(ActionEvent event) {
        String sql = "UPDATE livre SET etat_emprunt = 0 WHERE id = ?";
            try {
                db.initPrepar(sql);
                //Passage de valeurs
                db.getPstm().setInt(1, id);
                int ok = db.executeMaj();
                db.closeConnection();
                loadTable();
                clearFields();
                enregistrerBtn.setDisable(false);
                emprunterBtn.setDisable(false);
                Notification.NotifSuccess("Succés !", "Le livre a été rendu");
            }catch (SQLException e){
                throw new RuntimeException();
            }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTable();
        enregistrerBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        modifierBtn.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
        supprimerBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        emprunterBtn.setStyle("-fx-background-color: yellow; -fx-text-fill: white;");
        rendreBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
    }
}
