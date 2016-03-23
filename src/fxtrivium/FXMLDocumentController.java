/*
 * Copyright (C) 2016 srey
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package fxtrivium;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Formatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import jtrivium.cipher.JTrivium;
import jtrivium.utils.FileEncrypt;

/**
 *
 * @author srey
 */
public class FXMLDocumentController implements Initializable {
    
    private Label label;
    @FXML
    private AnchorPane apn_mainContainer;
    @FXML
    private TitledPane tpn_configContainer;
    @FXML
    private AnchorPane apn_configContainer;
    @FXML
    private GridPane gpn_configContainer;
    @FXML
    private Label lbl_key;
    @FXML
    private Label lbl_vi;
    @FXML
    private Label lbl_text;
    @FXML
    private Label lbl_inputFile;
    @FXML
    private TextField txt_key;
    @FXML
    private TextField txt_vi;
    @FXML
    private TextArea txta_text;
    @FXML
    private CheckBox ckb_text;
    @FXML
    private TextField txt_inputFile;
    @FXML
    private Button btn_inputFile;
    @FXML
    private TextArea txta_output;
    @FXML
    private ButtonBar bpn_buttonContainer;
    @FXML
    private Button btn_clear;
    @FXML
    private Button btn_encript;
    @FXML
    private Button btn_save;
    @FXML
    private Label lbl_output;
    @FXML
    private Pane pn_radioButtonConatiner;
    @FXML
    private RadioButton rb_hex;
    @FXML
    private RadioButton rb_base64;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.btn_clear.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ERASER));
        this.btn_encript.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LOCK));
        this.btn_inputFile.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE));
        this.btn_save.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SAVE));
        
        this.ckb_text.selectedProperty().addListener((observ, oldValue, newValue) -> {
            this.txt_inputFile.setDisable(newValue);
            this.btn_inputFile.setDisable(newValue);
            
            this.txta_text.setDisable(!newValue);
        });
    }    
    
    // section of private methods
    
    private void clear(boolean all) {
        if (all) {
            this.txt_inputFile.clear();
            this.txt_key.clear();
            this.txt_vi.clear();
            this.txta_text.clear();
            
            this.ckb_text.setSelected(true);
        }
        
        this.txta_output.clear();
    }
    
    private boolean validateParams() {
        if (this.txt_key.getText().isEmpty()) {
            DialogUtil.showWarning("Faltan Parametros", 
                    "Debe de ingresar la contraceña");
            
            this.txt_key.requestFocus();
            return false;
        } 
        
        if (this.txt_vi.getText().isEmpty()) {
            DialogUtil.showWarning("Faltan Parametros", 
                    "Debe de ingresar el vector de incialización");
            
            this.txt_vi.requestFocus();
            return false;
        }
        
        if (this.ckb_text.isSelected() && this.txta_text.getText().isEmpty()) {
            DialogUtil.showWarning("Faltan Parametros", 
                    "Debe de ingresar el texto a cifrar");
            
            this.txta_text.requestFocus();
            return false;
        } else if (!this.ckb_text.isSelected() && this.txt_inputFile.getText().isEmpty()) {
            DialogUtil.showWarning("Faltan Parametros", 
                    "Debe de ingresar la ruta que contiene el archivo de texto que desea cifrar");
            
            this.txt_inputFile.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private Appendable hexEncode(byte buf[], Appendable sb)    {   
        final Formatter formatter = new Formatter(sb);   

        for (int i = 0; i < buf.length; i++) {   
            try {
                int low = buf[i] & 0xF;
                int high = (buf[i] >> 8) & 0xF;

                sb.append(Character.forDigit(high, 16)).
                        append(Character.forDigit(low, 16)).
                        append(" ");
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    
        return sb;   
    }
    
    private String encrypt(String text, JTrivium cipher) {
        StringBuilder result= new StringBuilder();
        
        try (DataInputStream dataInput= new DataInputStream(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)))) {
            byte[] bufferd= new byte[dataInput.available()];
            int readBytes= 0;
            
            do {
                readBytes= dataInput.read(bufferd);
                
                for (int i=0; i < readBytes; i++)
                    bufferd[i] ^= cipher.getKeyByte();
                
                if (readBytes > 0) {
                    if (this.rb_hex.isSelected())
                        this.hexEncode(bufferd, result);
                    
                    else
                        result.append(Base64.getMimeEncoder().encodeToString(bufferd));
                }
                
            } while (readBytes > 0) ;
            
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            
            DialogUtil.showException("Error al cerrar Stream", ex);
        }
        
        return result.toString();
    }
    
    @FXML
    private void handleBtn_clear(ActionEvent evt) {
        this.clear(true);
    }
    
    @FXML
    private void handleBtn_cipher(ActionEvent evt) {
        if (this.validateParams()) {
            this.clear(false);
            
            StringBuilder sb= new StringBuilder();
            
            byte[] key= null;
            byte[] iv= null;
            
            key= this.hexEncode(this.txt_key.getText().getBytes(), sb).toString().getBytes();
            
            sb.delete(0, sb.length());
            
            iv= this.hexEncode(this.txt_vi.getText().getBytes(), sb).toString().getBytes();
            
            JTrivium cipher= new JTrivium(key, iv, false);
            
            if (this.ckb_text.isSelected()) 
                this.txta_output.setText(this.encrypt(this.txta_text.getText(), cipher));
            
            else {
                try {
                    Path tmpFile= Files.createTempFile("trivium", null);
                    Path inputFile= Paths.get(this.txt_inputFile.getText());
                    
                    if (inputFile != null && Files.exists(inputFile)) {
                        try (FileEncrypt fileEncryp= new FileEncrypt(inputFile.toString(), tmpFile.toString(), cipher, 512)) {
                            fileEncryp.encrypt(this.rb_hex.isSelected() ? 
                                    FileEncrypt.TypeEncode.HEX : FileEncrypt.TypeEncode.BASE64);
                            
                            Files.readAllLines(tmpFile).
                                    forEach(s -> this.txta_output.appendText(s + System.getProperty("line.separator")));
                        }
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    
                    DialogUtil.showException("Error al intentar cifrar el texto", ex);
                }
            }
        }
    }
    
    @FXML
    private void handleBtn_inpuFile(ActionEvent evt) {
        FileChooser fc= new FileChooser();
        
        fc.setTitle("Abrir archivo de Texto");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Text (*.txt)", "*.txt"));
        
        File selected= fc.showOpenDialog(this.lbl_inputFile.getScene().getWindow());
        
        if (selected != null && selected.isFile())
            this.txt_inputFile.setText(selected.toPath().toString());
    }
    
    @FXML
    private void handleBtn_save(ActionEvent evt) {
        FileChooser fc= new FileChooser();
        
        fc.setTitle("Guardar Texto Cifrado");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Text (*.txt)", "*.txt"));
                
        File selected= fc.showSaveDialog(this.lbl_inputFile.getScene().getWindow());
        
        if (selected != null) {
            
            try (FileWriter fw= new FileWriter(selected)) {
                fw.write(this.txta_output.getText());
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    
                    DialogUtil.showException("Error al guardar el texto cifrado", ex);
            }
        }
    }
}
