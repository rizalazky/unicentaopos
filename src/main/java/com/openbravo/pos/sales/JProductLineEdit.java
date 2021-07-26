//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright © 2009-2020 uniCenta & previous Openbravo POS works
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.sales;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.Session;
import com.openbravo.format.Formats;
import com.openbravo.pos.admin.ResourceType;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.TokenBasedAuth;
import com.openbravo.pos.ticket.TicketLineInfo;
import com.openbravo.pos.util.AltEncrypter;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 */
public class JProductLineEdit extends javax.swing.JDialog {
    
    private TicketLineInfo returnLine;
    private TicketLineInfo m_oLine;
    private boolean m_bunitsok;
    private boolean m_bunitPcssok;
    private boolean m_bpriceok;
    private String productID;
    private Session s;
    private Connection con;  
    private String SQL;
    private PreparedStatement pstmt;
    private JsonElement je;
    private JsonElement jeListVoucher;
    private String typeMachine="";
            
    /** Creates new form JProductLineEdit */
    private JProductLineEdit(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }
    /** Creates new form JProductLineEdit */
    private JProductLineEdit(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }
    
    private TicketLineInfo init(AppView app, TicketLineInfo oLine) throws BasicException {

        initComponents();

        productID = oLine.getProductID();
        typeMachine=AppConfig.getInstance().getProperty("machine.typemachine");
        System.out.println("TYPE MACHINE==>"+typeMachine);
        if (oLine.getTaxInfo() == null) {
            throw new BasicException(AppLocal.getIntString("message.cannotcalculatetaxes"));
        }

        if (!productID.equals("xxx999_999xxx_x9x9x9")) {
            m_jBtnPriceUpdate.setVisible(AppConfig.getInstance().getBoolean("db.prodpriceupdate"));
        }else{
            m_jBtnPriceUpdate.setVisible(false);
        }        

        m_jBtnPriceUpdate.setEnabled(false);
        
        //textAreaKondisiLain.setVisible(false);
        m_jCatBocelText.setVisible(true);
        m_jPatahHadwareText.setVisible(true);
        m_jKondisiLainText.setVisible(true);
        m_jKainBerbercak.setVisible(true);
        m_jKainLunturText.setVisible(true);
        m_jKainPudarText.setVisible(true);
        m_jCatBocelText.setEnabled(app.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        m_jPatahHadwareText.setEnabled(app.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        m_jKondisiLainText.setEnabled(app.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        m_jKainBerbercak.setEnabled(app.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        m_jKainPudarText.setEnabled(app.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        m_jKainLunturText.setEnabled(app.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        
        m_oLine = new TicketLineInfo(oLine);
        m_bunitsok = true;
        m_bunitPcssok = true; 
        m_bpriceok = true;
        
        jLabel8.setText("Merk");
        jLabel9.setText("Model/Jenis");
        jLabel10.setText("Warna");
        jLabel11.setText("Cat Bocel");
        jLabel13.setText("Patah Hadware");
        jLabel14.setText("Kain Luntur");
        jLabel15.setText("Kain Pudar");
        jLabel16.setText("Kain Berbercak");
        jLabel17.setText("Kondisi Lain");
        jLabel18.setText("Nama Pengambil");
        jLabel19.setText("No hp");
        jLabel20.setText("Dikerjakan Oleh");
        jLabel21.setText("Nomer Voucher");
        
        jLabel4.setText(AppLocal.getIntString("label.unitspcs"));

//        m_jName.setEnabled(app.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        m_jUnits.setEnabled(app.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        m_jUnits1.setEnabled(app.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        //m_jPrice.setEnabled(app.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        m_jPriceTax.setEnabled(app.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        
        m_jName.setText(oLine.getProductName());        
        m_jUnits.setDoubleValue(oLine.getMultiply());
        m_jUnits1.setDoubleValue(oLine.getMultiplyPcs());
        m_jPrice.setDoubleValue(oLine.getPrice()); 
        m_jPriceTax.setDoubleValue(oLine.getPriceTax());
        m_jTaxrate.setText(oLine.getTaxInfo().getName());
        isSudahDiambil.setState(oLine.getItemSudahDiambil());
        m_jMerk.setText(oLine.getMerk());
        m_jModel.setText(oLine.getModel());
        m_jWarna.setText(oLine.getWarna());
        
        m_jCatBocelText.setText(oLine.getCatBocel());
        m_jKainBerbercak.setText(oLine.getKainBerbercak());
        m_jKainLunturText.setText(oLine.getKainLuntur());
        m_jKainPudarText.setText(oLine.getKainPudar());
        m_jKondisiLainText.setText(oLine.getKondisiLain());
        m_jPatahHadwareText.setText(oLine.getPatahHadware());
        isSudahDiambil.setState(oLine.getItemSudahDiambil());
        jTextFieldNamaPengambil.setText(oLine.getNamaPengambil());
        jTextFieldNOHpPengambil.setText(oLine.getNoHpPengambil());
        
//        set Raduio Button
        String lineCatBocel=oLine.getCatBocel();
        if(lineCatBocel != null && lineCatBocel != ""){
           
           jRadioButton1.setSelected(false);
           jRadioButton2.setSelected(true);
        }else{
           m_jCatBocelText.setVisible(false);
           jRadioButton1.setSelected(true);
           jRadioButton2.setSelected(false); 
        }
        
        String linePatahHadware=oLine.getPatahHadware();
        if(linePatahHadware != null && linePatahHadware != ""){
           
           jRadioButton3.setSelected(false);
           jRadioButton4.setSelected(true);
        }else{
           m_jPatahHadwareText.setVisible(false);
           jRadioButton3.setSelected(true);
           jRadioButton4.setSelected(false); 
        }
        
        
        
        String lineKainLuntur=oLine.getKainLuntur();
        if(lineKainLuntur != null && lineKainLuntur != ""){
           
           jRadioButton5.setSelected(false);
           jRadioButton6.setSelected(true);
        }else{
           m_jKainLunturText.setVisible(false);
           jRadioButton5.setSelected(true);
           jRadioButton6.setSelected(false); 
        }
        
        String lineKainPudar=oLine.getKainPudar();
        if(lineKainPudar != null && lineKainPudar != ""){
           
           jRadioButton7.setSelected(false);
           jRadioButton8.setSelected(true);
        }else{
           m_jKainPudarText.setVisible(false);
           jRadioButton7.setSelected(true);
           jRadioButton8.setSelected(false); 
        }
        
        String linekainBercak=oLine.getKainBerbercak();
        if(linekainBercak != null && linekainBercak != ""){
           
           jRadioButton9.setSelected(false);
           jRadioButton10.setSelected(true);
        }else{
           m_jKainBerbercak.setVisible(false);
           jRadioButton9.setSelected(true);
           jRadioButton10.setSelected(false); 
        }
        
        
        String lineKondisiLain=oLine.getKondisiLain();
        if(lineKondisiLain != null && lineKondisiLain != ""){
           
           jRadioButton11.setSelected(false);
           jRadioButton12.setSelected(true);
        }else{
           m_jKondisiLainText.setVisible(false);
           jRadioButton11.setSelected(true);
           jRadioButton12.setSelected(false); 
        }
//        repaint();
//        revalidate();
      TokenBasedAuth tokenBasedAuth=new com.openbravo.pos.forms.TokenBasedAuth(app);
      String responseString="";
      String responseStringVoucher="";
      try {
          responseString = tokenBasedAuth.getDikerjakanOleh();
          je = new JsonParser().parse(responseString);
          if(oLine.getProperty("product.isvoucher").equals("true")){
              responseStringVoucher=tokenBasedAuth.getListVoucher(productID);
              System.out.println("PRODUCTID ==>"+productID);
                //parsing response to json
                if(!responseStringVoucher.equals("null") || responseStringVoucher !=null){
                      jeListVoucher = new JsonParser().parse(responseStringVoucher);//parsing response to json


                      int lengthVoucherList=jeListVoucher.getAsJsonArray().size();
                      jTextFieldNomerVoucher.addItem(" ----- ");
                      for(int i=0;i<lengthVoucherList;i++){
                          String id=jeListVoucher.getAsJsonArray().get(i).getAsJsonObject().get("internal_id").getAsString();
                          String entity=jeListVoucher.getAsJsonArray().get(i).getAsJsonObject().get("kode_voucher").getAsString();
                          jTextFieldNomerVoucher.addItem(entity);
                      }
                }
          }
          
      } catch (IOException ex) {
          Logger.getLogger(JProductLineEdit.class.getName()).log(Level.SEVERE, null, ex);
      }
     
      System.out.println("DIKERJAKAN OLEH ==> "+responseString);
      System.out.println("VOUCHER LIST ==> "+responseStringVoucher);
      

        
        int length=je.getAsJsonArray().size();
        jComboBoxDiKerjakanOleh.addItem(" ---- ");
        for(int i=0;i<length;i++){
            int id=je.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt();
            String entity=je.getAsJsonArray().get(i).getAsJsonObject().get("entity").getAsString();
            jComboBoxDiKerjakanOleh.addItem(entity);
        }
        




        jLabel18.setVisible(isSudahDiambil.getState());
        jLabel19.setVisible(isSudahDiambil.getState());
        jTextFieldNamaPengambil.setVisible(isSudahDiambil.getState());
        jTextFieldNOHpPengambil.setVisible(isSudahDiambil.getState());
        jTextFieldNamaPengambil.repaint();
        jTextFieldNOHpPengambil.repaint();
        jLabel18.repaint();
        jLabel19.repaint();
        
        
        
        
        jRadioButton3.setSelected(true);
        jRadioButton5.setSelected(true);
        jRadioButton7.setSelected(true);
        jRadioButton9.setSelected(true);
        jRadioButton11.setSelected(true);
        
        
        
        m_jName.addPropertyChangeListener("Edition", new RecalculateName());
        m_jUnits.addPropertyChangeListener("Edition", new RecalculateUnits());
        m_jUnits1.addPropertyChangeListener("Edition", new RecalculateUnitsPcs());
        m_jPrice.addPropertyChangeListener("Edition", new RecalculatePrice());
        m_jPriceTax.addPropertyChangeListener("Edition", new RecalculatePriceTax());

        m_jName.addEditorKeys(m_jKeys);
        m_jUnits.addEditorKeys(m_jKeys);
        m_jUnits1.addEditorKeys(m_jKeys);
        m_jPrice.addEditorKeys(m_jKeys);
        m_jPriceTax.addEditorKeys(m_jKeys);
        
        if (m_jName.isEnabled()) {
            m_jName.activate();
        } else {
            m_jUnits.activate();
            m_jUnits1.activate();
        }
        
        repaint();
        
        //set View Type Kasir
        System.out.println(oLine.getProperty("product.isvoucher")+ "  IS VOUCHER");
        if(oLine.getProperty("product.isvoucher").equals("true") || !typeMachine.equals("1")){
            System.out.println("MASUK IF SET VIEW");
            jLabel8.setVisible(false);
            jLabel9.setVisible(false);
            jLabel10.setVisible(false);
            jLabel11.setVisible(false);
            jLabel13.setVisible(false);
            jLabel14.setVisible(false);
            jLabel15.setVisible(false);
            jLabel16.setVisible(false);
            jLabel17.setVisible(false);
            jLabel18.setVisible(false);
            jLabel19.setVisible(false);
            jLabel20.setVisible(false);
            
            m_jMerk.setVisible(false);
            m_jModel.setVisible(false);
            m_jWarna.setVisible(false);
            
            jRadioButton1.setVisible(false);
            jRadioButton2.setVisible(false);
            jRadioButton3.setVisible(false);
            jRadioButton4.setVisible(false);
            jRadioButton5.setVisible(false);
            jRadioButton6.setVisible(false);
            jRadioButton7.setVisible(false);
            jRadioButton8.setVisible(false);
            jRadioButton9.setVisible(false);
            jRadioButton10.setVisible(false);
            jRadioButton11.setVisible(false);
            jRadioButton12.setVisible(false);
            isSudahDiambil.setVisible(false);
            jComboBoxDiKerjakanOleh.setVisible(false);
            repaint();
        }else{
            jTextFieldNomerVoucher.setVisible(false);
            jLabel21.setVisible(false);
            repaint();
        }
        
        
        printTotals();

        getRootPane().setDefaultButton(m_jButtonOK);   
        returnLine = null;
        setVisible(true);
        
        
        
        return returnLine;
    }
    
   
    
    private void printTotals() {
        
        if (m_bunitsok && m_bpriceok) {
            m_jSubtotal.setText(m_oLine.printSubValue());
            m_jTotal.setText(m_oLine.printValue());
            
            
            m_jButtonOK.setEnabled(true);
            
       } else {
            m_jSubtotal.setText(null);
            m_jTotal.setText(null);
            m_jButtonOK.setEnabled(false);
        }
    }
    
    private class RecalculateUnitsPcs implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Double value = m_jUnits1.getDoubleValue();
            if (value == null || value == 0.0) {
                m_bunitPcssok = false;
            } else {
                m_oLine.setMultiplyPcs(value);
                m_bunitPcssok = true;                
            }

            printTotals();
        }
    }
    
    private class RecalculateUnits implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Double value = m_jUnits.getDoubleValue();
            if (value == null || value == 0.0) {
                m_bunitsok = false;
            } else {
                m_oLine.setMultiply(value);
                m_bunitsok = true;                
            }

            printTotals();
        }
    }
    
    private class RecalculatePrice implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {

            Double value = m_jPrice.getDoubleValue();
            if (value == null || value == 0.0) {
                m_bpriceok = false;
            } else {
                m_oLine.setPrice(value);
                m_jPriceTax.setDoubleValue(m_oLine.getPriceTax());
                m_bpriceok = true;
                m_jBtnPriceUpdate.setEnabled(AppConfig.getInstance().getBoolean("db.prodpriceupdate"));                
            }

            printTotals();
        }
    }    
    
    private class RecalculatePriceTax implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {

            Double value = m_jPriceTax.getDoubleValue();
            if (value == null || value == 0.0) {
                m_bpriceok = false;
            } else {
                m_oLine.setPriceTax(value);
                m_jPrice.setDoubleValue(m_oLine.getPrice());
                m_bpriceok = true;
                m_jBtnPriceUpdate.setEnabled(AppConfig.getInstance().getBoolean("db.prodpriceupdate"));                                
            }

            printTotals();
        }
    }   
    
    private class RecalculateName implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            m_oLine.setProperty("product.name", m_jName.getText());
        }
    }   
    
    private static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Frame || parent instanceof Dialog) {
            return (Window)parent;
        } else {
            return getWindow(parent.getParent());
        }
    }

    /**
     *
     * @param parent
     * @param app
     * @param oLine
     * @return
     * @throws BasicException
     */
    public static TicketLineInfo showMessage(Component parent
            , AppView app
            , TicketLineInfo oLine) throws BasicException {
         
        Window window = getWindow(parent);
        
        JProductLineEdit myMsg;
        if (window instanceof Frame) { 
            myMsg = new JProductLineEdit((Frame) window, true);
        } else {
            myMsg = new JProductLineEdit((Dialog) window, true);
        }
        return myMsg.init(app, oLine);
    }        

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel12 = new javax.swing.JLabel();
        m_jWarna2 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        m_jName = new com.openbravo.editor.JEditorString();
        m_jUnits = new com.openbravo.editor.JEditorDouble();
        m_jPrice = new com.openbravo.editor.JEditorCurrency();
        m_jPriceTax = new com.openbravo.editor.JEditorCurrency();
        m_jTaxrate = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        m_jTotal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        m_jSubtotal = new javax.swing.JLabel();
        m_jBtnPriceUpdate = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        m_jUnits1 = new com.openbravo.editor.JEditorDouble();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jLabel14 = new javax.swing.JLabel();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jLabel15 = new javax.swing.JLabel();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jLabel16 = new javax.swing.JLabel();
        jRadioButton9 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jLabel17 = new javax.swing.JLabel();
        jRadioButton11 = new javax.swing.JRadioButton();
        jRadioButton12 = new javax.swing.JRadioButton();
        m_jMerk = new javax.swing.JTextField();
        m_jModel = new javax.swing.JTextField();
        m_jWarna = new javax.swing.JTextField();
        m_jPatahHadwareText = new javax.swing.JTextField();
        m_jKainLunturText = new javax.swing.JTextField();
        m_jCatBocelText = new javax.swing.JTextField();
        m_jKainPudarText = new javax.swing.JTextField();
        m_jKondisiLainText = new javax.swing.JTextField();
        m_jKainBerbercak = new javax.swing.JTextField();
        isSudahDiambil = new java.awt.Checkbox();
        jTextFieldNamaPengambil = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTextFieldNOHpPengambil = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextFieldNomerVoucher = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jComboBoxDiKerjakanOleh = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jKeys = new com.openbravo.editor.JEditorKeys();
        jPanel1 = new javax.swing.JPanel();
        m_jButtonCancel = new javax.swing.JButton();
        m_jButtonOK = new javax.swing.JButton();

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jWarna2.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        m_jWarna2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jWarna2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jWarna2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jWarna2.setOpaque(true);
        m_jWarna2.setPreferredSize(new java.awt.Dimension(150, 25));
        m_jWarna2.setRequestFocusEnabled(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(AppLocal.getIntString("label.editline")); // NOI18N

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(400, 230));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.price")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.units")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.pricetax")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jName.setPreferredSize(new java.awt.Dimension(132, 30));

        m_jUnits.setEnabled(false);
        m_jUnits.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jUnits.setPreferredSize(new java.awt.Dimension(132, 30));

        m_jPrice.setEnabled(false);
        m_jPrice.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPrice.setPreferredSize(new java.awt.Dimension(132, 30));

        m_jPriceTax.setEnabled(false);
        m_jPriceTax.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPriceTax.setPreferredSize(new java.awt.Dimension(132, 30));

        m_jTaxrate.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        m_jTaxrate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTaxrate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jTaxrate.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jTaxrate.setOpaque(true);
        m_jTaxrate.setPreferredSize(new java.awt.Dimension(150, 25));
        m_jTaxrate.setRequestFocusEnabled(false);

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.totalcash")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jTotal.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        m_jTotal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jTotal.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jTotal.setOpaque(true);
        m_jTotal.setPreferredSize(new java.awt.Dimension(150, 25));
        m_jTotal.setRequestFocusEnabled(false);

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.subtotalcash")); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jSubtotal.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        m_jSubtotal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jSubtotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jSubtotal.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jSubtotal.setOpaque(true);
        m_jSubtotal.setPreferredSize(new java.awt.Dimension(150, 25));
        m_jSubtotal.setRequestFocusEnabled(false);

        m_jBtnPriceUpdate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jBtnPriceUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/filesave.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        m_jBtnPriceUpdate.setText(bundle.getString("button.priceupdate")); // NOI18N
        m_jBtnPriceUpdate.setFocusPainted(false);
        m_jBtnPriceUpdate.setFocusable(false);
        m_jBtnPriceUpdate.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jBtnPriceUpdate.setPreferredSize(new java.awt.Dimension(110, 45));
        m_jBtnPriceUpdate.setRequestFocusEnabled(false);
        m_jBtnPriceUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnPriceUpdateActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.units")); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jUnits1.setEnabled(false);
        m_jUnits1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jUnits1.setPreferredSize(new java.awt.Dimension(132, 30));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(110, 30));

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Tidak Ada");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Ada");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jLabel13.setPreferredSize(new java.awt.Dimension(110, 30));

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setText("Tidak Ada");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton4);
        jRadioButton4.setText("Ada");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel14.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jLabel14.setPreferredSize(new java.awt.Dimension(110, 30));

        buttonGroup3.add(jRadioButton5);
        jRadioButton5.setText("Tidak Ada");
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton5ActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioButton6);
        jRadioButton6.setText("Ada");
        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton6ActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel15.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(110, 30));

        buttonGroup4.add(jRadioButton7);
        jRadioButton7.setText("Tidak Ada");
        jRadioButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton7ActionPerformed(evt);
            }
        });

        buttonGroup4.add(jRadioButton8);
        jRadioButton8.setText("Ada");
        jRadioButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton8ActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel16.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(110, 30));

        buttonGroup5.add(jRadioButton9);
        jRadioButton9.setText("Tidak Ada");
        jRadioButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton9ActionPerformed(evt);
            }
        });

        buttonGroup5.add(jRadioButton10);
        jRadioButton10.setText("Ada");
        jRadioButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton10ActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel17.setText(AppLocal.getIntString("label.tax")); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(110, 30));

        buttonGroup6.add(jRadioButton11);
        jRadioButton11.setText("Tidak Ada");
        jRadioButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton11ActionPerformed(evt);
            }
        });

        buttonGroup6.add(jRadioButton12);
        jRadioButton12.setText("Ada");
        jRadioButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton12ActionPerformed(evt);
            }
        });

        m_jMerk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jMerkActionPerformed(evt);
            }
        });

        m_jModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jModelActionPerformed(evt);
            }
        });

        m_jWarna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jWarnaActionPerformed(evt);
            }
        });

        m_jPatahHadwareText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPatahHadwareTextActionPerformed(evt);
            }
        });

        m_jKainLunturText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKainLunturTextActionPerformed(evt);
            }
        });

        m_jCatBocelText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCatBocelTextActionPerformed(evt);
            }
        });

        m_jKainPudarText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKainPudarTextActionPerformed(evt);
            }
        });

        m_jKondisiLainText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKondisiLainTextActionPerformed(evt);
            }
        });

        m_jKainBerbercak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKainBerbercakActionPerformed(evt);
            }
        });

        isSudahDiambil.setLabel("Sudah Diambil");
        isSudahDiambil.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                isSudahDiambilItemStateChanged(evt);
            }
        });
        isSudahDiambil.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                isSudahDiambilPropertyChange(evt);
            }
        });

        jLabel18.setText("Nama Pengambil");

        jLabel19.setText("No Hp");

        jLabel20.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel20.setText("Dikerjakan Oleh");

        jLabel21.setText("jLabel21");

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        m_jKeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKeysActionPerformed(evt);
            }
        });
        jPanel4.add(m_jKeys);

        jPanel3.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        m_jButtonCancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/cancel.png"))); // NOI18N
        m_jButtonCancel.setText(AppLocal.getIntString("button.cancel")); // NOI18N
        m_jButtonCancel.setFocusPainted(false);
        m_jButtonCancel.setFocusable(false);
        m_jButtonCancel.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonCancel.setPreferredSize(new java.awt.Dimension(110, 45));
        m_jButtonCancel.setRequestFocusEnabled(false);
        m_jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonCancelActionPerformed(evt);
            }
        });
        jPanel1.add(m_jButtonCancel);

        m_jButtonOK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jButtonOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/ok.png"))); // NOI18N
        m_jButtonOK.setText(AppLocal.getIntString("button.OK")); // NOI18N
        m_jButtonOK.setFocusPainted(false);
        m_jButtonOK.setFocusable(false);
        m_jButtonOK.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonOK.setPreferredSize(new java.awt.Dimension(110, 45));
        m_jButtonOK.setRequestFocusEnabled(false);
        m_jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonOKActionPerformed(evt);
            }
        });
        jPanel1.add(m_jButtonOK);

        jPanel3.add(jPanel1, java.awt.BorderLayout.SOUTH);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(m_jBtnPriceUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(m_jPriceTax, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(m_jUnits1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(m_jUnits, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(m_jTaxrate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(m_jSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBoxDiKerjakanOleh, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel21))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jWarna, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jModel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jMerk, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldNomerVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(m_jPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jRadioButton5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton6))
                                    .addComponent(m_jKainLunturText)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jRadioButton3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton4)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(m_jPatahHadwareText)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jKainPudarText)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jRadioButton9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton10)
                                        .addGap(38, 174, Short.MAX_VALUE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(isSudahDiambil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(158, 158, 158))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton2))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton8))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton12))
                                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGap(116, 116, 116)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(m_jCatBocelText, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jKondisiLainText)
                                    .addComponent(m_jKainBerbercak, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldNamaPengambil, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldNOHpPengambil, javax.swing.GroupLayout.Alignment.LEADING))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRadioButton1)
                                    .addComponent(jRadioButton2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_jCatBocelText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jLabel21))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jTextFieldNomerVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jMerk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(1, 1, 1)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jModel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(1, 1, 1)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(m_jWarna, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jUnits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jUnits1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jPriceTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jTaxrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(jComboBoxDiKerjakanOleh, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(m_jBtnPriceUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(24, 24, 24))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jRadioButton3)
                                            .addComponent(jRadioButton4))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(m_jPatahHadwareText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRadioButton5)
                                    .addComponent(jRadioButton6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_jKainLunturText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRadioButton7)
                                    .addComponent(jRadioButton8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_jKainPudarText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRadioButton9)
                                    .addComponent(jRadioButton10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_jKainBerbercak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRadioButton11)
                                    .addComponent(jRadioButton12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_jKondisiLainText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)
                                .addComponent(isSudahDiambil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextFieldNamaPengambil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextFieldNOHpPengambil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(107, 107, 107))
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 644, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 38, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel5.add(jPanel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel5, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(1059, 733));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonCancelActionPerformed

        dispose();

    }//GEN-LAST:event_m_jButtonCancelActionPerformed

    private void m_jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonOKActionPerformed
        m_oLine.setMerk(m_jMerk.getText());
        m_oLine.setModel(m_jModel.getText());
        m_oLine.setWarna(m_jWarna.getText());
        m_oLine.setCatBocel(m_jCatBocelText.getText());
        m_oLine.setKainBerbercak(m_jKainBerbercak.getText());
        m_oLine.setKainLuntur(m_jKainLunturText.getText());
        m_oLine.setKainPudar(m_jKainPudarText.getText());
        m_oLine.setKondisiLain(m_jKondisiLainText.getText());
        m_oLine.setPatahHadware(m_jPatahHadwareText.getText());
        m_oLine.setNamaPengambil(jTextFieldNamaPengambil.getText());
        m_oLine.setNoHpPengambil(jTextFieldNOHpPengambil.getText());
        int index=jComboBoxDiKerjakanOleh.getSelectedIndex();
        m_oLine.setDikerjakanOleh(je.getAsJsonArray().get(index).getAsJsonObject().get("id").getAsString());
        m_oLine.setProperty("ticket.vouchernumber",jTextFieldNomerVoucher.getSelectedItem() != null ? jTextFieldNomerVoucher.getSelectedItem().toString() : "");
        int indexVoucher=jTextFieldNomerVoucher.getSelectedIndex();
        m_oLine.setProperty("ticket.voucherid",jeListVoucher != null ?jeListVoucher.getAsJsonArray().get(indexVoucher - 1 ).getAsJsonObject().get("internal_id").getAsString():"");
        
        returnLine = m_oLine;
        
        dispose();

    }//GEN-LAST:event_m_jButtonOKActionPerformed

    private void m_jKeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKeysActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jKeysActionPerformed

    private void m_jBtnPriceUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnPriceUpdateActionPerformed

        String db_password = (AppConfig.getInstance().getProperty("db.password"));

        if (AppConfig.getInstance().getProperty("db.user") != null
            && db_password != null
            && db_password.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey"
                + AppConfig.getInstance().getProperty("db.user"));
            db_password = cypher.decrypt(db_password.substring(6));
        }

        try {

            //            s = AppViewConnection.createSession();
            con = DriverManager.getConnection(
                AppConfig.getInstance().getProperty("db.URL") +
                AppConfig.getInstance().getProperty("db.schema")
                , AppConfig.getInstance().getProperty("db.user")
                , db_password);

            pstmt = con.prepareStatement(
                "UPDATE PRODUCTS SET PRICESELL = ? WHERE ID = ?");
            pstmt.setDouble(1, m_jPrice.getDoubleValue());
            pstmt.setString(2, productID);
            System.out.println(pstmt);

            pstmt.executeUpdate();

            m_jBtnPriceUpdate.setEnabled(false);

            con.close();

        } catch (SQLException e) {
            System.out.println(e);

            return;
        }

        m_oLine.setUpdated(true);
    }//GEN-LAST:event_m_jBtnPriceUpdateActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
        m_jCatBocelText.setVisible(false);
        repaint();
        revalidate();
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        // TODO add your handling code here:
        
        m_jCatBocelText.setVisible(true);
        repaint();
        revalidate();
        //m_jCatBocelText.setEnabled(true);
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        // TODO add your handling code here:
        m_jPatahHadwareText.setVisible(false);
        repaint();
        revalidate();
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        // TODO add your handling code here:
        m_jPatahHadwareText.setVisible(true);
        m_jPatahHadwareText.setEnabled(true);
        repaint();
        revalidate();
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton5ActionPerformed
        // TODO add your handling code here:
        m_jKainLunturText.setVisible(false);
        repaint();
        revalidate();
    }//GEN-LAST:event_jRadioButton5ActionPerformed

    private void jRadioButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton6ActionPerformed
        // TODO add your handling code here:
        m_jKainLunturText.setVisible(true);
        m_jKainLunturText.setEnabled(true);
        repaint();
        revalidate();
    }//GEN-LAST:event_jRadioButton6ActionPerformed

    private void jRadioButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton7ActionPerformed
        // TODO add your handling code here:
        m_jKainPudarText.setVisible(false);
        repaint();
        revalidate();
    }//GEN-LAST:event_jRadioButton7ActionPerformed

    private void jRadioButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton8ActionPerformed
        // TODO add your handling code here:
        m_jKainPudarText.setVisible(true);
        m_jKainPudarText.setEnabled(true);
        repaint();
        revalidate();
    }//GEN-LAST:event_jRadioButton8ActionPerformed

    private void jRadioButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton9ActionPerformed
        // TODO add your handling code here:
        m_jKainBerbercak.setVisible(false);
        repaint();
        revalidate();
    }//GEN-LAST:event_jRadioButton9ActionPerformed

    private void jRadioButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton10ActionPerformed
        // TODO add your handling code here:
        m_jKainBerbercak.setVisible(true);
        m_jKainBerbercak.setEnabled(true);
        repaint();
        revalidate();
    }//GEN-LAST:event_jRadioButton10ActionPerformed

    private void jRadioButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton11ActionPerformed
        // TODO add your handling code here:
        m_jKondisiLainText.setVisible(false);
        repaint();
        revalidate();
    }//GEN-LAST:event_jRadioButton11ActionPerformed

    private void jRadioButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton12ActionPerformed
        // TODO add your handling code here:
        m_jKondisiLainText.setVisible(true);
        m_jKondisiLainText.setEnabled(true);
        repaint();
        revalidate();
    }//GEN-LAST:event_jRadioButton12ActionPerformed

    private void m_jMerkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jMerkActionPerformed
        // TODO add your handling code here:
        String value = m_jMerk.getText();
        if (value == null || "".equals(value)) {
            m_bunitPcssok = false;
        } else {
            m_oLine.setMerk(value);
            m_bunitPcssok = true;                
        }
        
        System.out.println("MERK VALUE"+value +" --- "+m_oLine.getMerk());

        printTotals();
    }//GEN-LAST:event_m_jMerkActionPerformed

    private void m_jModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jModelActionPerformed
        // TODO add your handling code here:
        String value = m_jModel.getText();
        if (value == null || "".equals(value)) {
            m_bunitPcssok = false;
        } else {
            m_oLine.setModel(value);
            m_bunitPcssok = true;                
        }

        printTotals();
    }//GEN-LAST:event_m_jModelActionPerformed

    private void m_jWarnaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jWarnaActionPerformed
        // TODO add your handling code here:
        String value = m_jWarna.getText();
        if (value == null || "".equals(value)) {
            m_bunitPcssok = false;
        } else {
            m_oLine.setWarna(value);
            m_bunitPcssok = true;                
        }

        printTotals();
    }//GEN-LAST:event_m_jWarnaActionPerformed

    private void m_jCatBocelTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCatBocelTextActionPerformed
        // TODO add your handling code here:
        String value = m_jCatBocelText.getText();
        if (value == null || "".equals(value)) {
            m_bunitPcssok = false;
        } else {
            m_oLine.setCatBocel(value);
            m_bunitPcssok = true;                
        }

        printTotals();
    }//GEN-LAST:event_m_jCatBocelTextActionPerformed

    private void m_jPatahHadwareTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPatahHadwareTextActionPerformed
        // TODO add your handling code here:
        String value = m_jPatahHadwareText.getText();
        if (value == null || "".equals(value)) {
            m_bunitPcssok = false;
        } else {
            m_oLine.setPatahHadware(value);
            m_bunitPcssok = true;                
        }

        printTotals();
    }//GEN-LAST:event_m_jPatahHadwareTextActionPerformed

    private void m_jKainLunturTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKainLunturTextActionPerformed
        // TODO add your handling code here:
        String value = m_jKainLunturText.getText();
        if (value == null || "".equals(value)) {
            m_bunitPcssok = false;
        } else {
            m_oLine.setKainLuntur(value);
            m_bunitPcssok = true;                
        }

        printTotals();
    }//GEN-LAST:event_m_jKainLunturTextActionPerformed

    private void m_jKainPudarTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKainPudarTextActionPerformed
        // TODO add your handling code here:
        String value = m_jKainPudarText.getText();
        if (value == null || "".equals(value)) {
            m_bunitPcssok = false;
        } else {
            m_oLine.setKainPudar(value);
            m_bunitPcssok = true;                
        }

        printTotals();
    }//GEN-LAST:event_m_jKainPudarTextActionPerformed

    private void m_jKainBerbercakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKainBerbercakActionPerformed
        // TODO add your handling code here:
        String value = m_jKainBerbercak.getText();
        if (value == null || "".equals(value)) {
            m_bunitPcssok = false;
        } else {
            m_oLine.setKainBerbercak(value);
            m_bunitPcssok = true;                
        }

        printTotals();
    }//GEN-LAST:event_m_jKainBerbercakActionPerformed

    private void m_jKondisiLainTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKondisiLainTextActionPerformed
        // TODO add your handling code here:
        String value = m_jKondisiLainText.getText();
        if (value == null || "".equals(value)) {
            m_bunitPcssok = false;
        } else {
            m_oLine.setKondisiLain(value);
            m_bunitPcssok = true;                
        }

        printTotals();
    }//GEN-LAST:event_m_jKondisiLainTextActionPerformed

    private void isSudahDiambilPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_isSudahDiambilPropertyChange
        // TODO add your handling code here:
        
    }//GEN-LAST:event_isSudahDiambilPropertyChange

    private void isSudahDiambilItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_isSudahDiambilItemStateChanged
        // TODO add your handling code here:
        System.out.println(isSudahDiambil.getState());
        jLabel18.setVisible(isSudahDiambil.getState());
        jLabel19.setVisible(isSudahDiambil.getState());
        jTextFieldNamaPengambil.setVisible(isSudahDiambil.getState());
        jTextFieldNOHpPengambil.setVisible(isSudahDiambil.getState());
        repaint();
        revalidate();
        m_oLine.setItemDiambil(isSudahDiambil.getState());
    }//GEN-LAST:event_isSudahDiambilItemStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private java.awt.Checkbox isSudahDiambil;
    private javax.swing.JComboBox<String> jComboBoxDiKerjakanOleh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton12;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JTextField jTextFieldNOHpPengambil;
    private javax.swing.JTextField jTextFieldNamaPengambil;
    private javax.swing.JComboBox<String> jTextFieldNomerVoucher;
    private javax.swing.JButton m_jBtnPriceUpdate;
    private javax.swing.JButton m_jButtonCancel;
    private javax.swing.JButton m_jButtonOK;
    private javax.swing.JTextField m_jCatBocelText;
    private javax.swing.JTextField m_jKainBerbercak;
    private javax.swing.JTextField m_jKainLunturText;
    private javax.swing.JTextField m_jKainPudarText;
    private com.openbravo.editor.JEditorKeys m_jKeys;
    private javax.swing.JTextField m_jKondisiLainText;
    private javax.swing.JTextField m_jMerk;
    private javax.swing.JTextField m_jModel;
    private com.openbravo.editor.JEditorString m_jName;
    private javax.swing.JTextField m_jPatahHadwareText;
    private com.openbravo.editor.JEditorCurrency m_jPrice;
    private com.openbravo.editor.JEditorCurrency m_jPriceTax;
    private javax.swing.JLabel m_jSubtotal;
    private javax.swing.JLabel m_jTaxrate;
    private javax.swing.JLabel m_jTotal;
    private com.openbravo.editor.JEditorDouble m_jUnits;
    private com.openbravo.editor.JEditorDouble m_jUnits1;
    private javax.swing.JTextField m_jWarna;
    private javax.swing.JLabel m_jWarna2;
    // End of variables declaration//GEN-END:variables
    
}
