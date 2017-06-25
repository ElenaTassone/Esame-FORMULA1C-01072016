package it.polito.tdp.formulaone;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Driverr;
import it.polito.tdp.formulaone.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FormulaOneController {
	
	Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Constructor> boxCostruttori;

    @FXML
    private TextField textInputK;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	Constructor c = this.boxCostruttori.getValue() ;
    	if(c== null)
    		this.txtResult.setText("ERRORE: Selezionare prima un costruttore");
    	else{
    		Driverr max = model.getMax(c);
    		this.txtResult.setText("Il pilota che ha gareggiato di più per la "+c+" è "+max);
    	}
    }

    @FXML
    void doTrovaDreamTeam(ActionEvent event) {
    	Constructor c = this.boxCostruttori.getValue() ;
    	if(c== null)
    		this.txtResult.setText("ERRORE: Selezionare prima un costruttore");
    	else{
    		String s = textInputK.getText();
    	
    		try{
    			int k = Integer.parseInt(s);
    			List<Driverr> dream = model.getDreamTeam(k,c);
    			txtResult.clear();
    			txtResult.setText("Il dream team è formato da "+dream);
    		}
    		catch(NumberFormatException e){
    			txtResult.setText("ERRORE: Inserire un k");
    		}
    	}
    	
    	
    }

    @FXML
    void initialize() {
        assert boxCostruttori != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert textInputK != null : "fx:id=\"textInputK\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FormulaOne.fxml'.";

    }
    
    public void setModel(Model model){
    	this.model = model;
    	this.boxCostruttori.getItems().addAll(model.getConstructors()) ;
    	
    	
    }
}
