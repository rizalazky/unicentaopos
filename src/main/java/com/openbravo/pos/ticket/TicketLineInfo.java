//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright © 2009-2020 uniCenta
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.ticket;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.DataWrite;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.data.loader.SerializableWrite;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.util.StringUtils;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Properties;

/**
 *
 * @author adrianromero
 */
public class TicketLineInfo implements SerializableWrite, SerializableRead, Serializable {

    private static final long serialVersionUID = 6608012948284450199L;
    private String m_sTicket;
    private int m_iLine;
    private double multiply;
    private double multiplyPcs;
    private Boolean isDiambil=false;
    
    private double price;
    private TaxInfo tax;
    private Properties attributes;
    private String productid;
    private String attsetinstid;
    private Boolean updated = false;
    
    private double newprice = 0.0;
    
    private String merk;
    private String model;
    private String warna;
    private String catBocelDesc;
    private String namaPengambil;
    private String noHpPengambil;
    private String diKerjakanOleh;
    private String patahHadwareDesc;
    private String kainLunturDesc;
    private String kainPudarDesc;
    private String kainBerbercakDesc;
    private String kondisiLainDesc;
    
    /** Creates new TicketLineInfo
     * @param productid
     * @param dMultiply
     * @param dPrice
     * @param tax
     * @param props */
    public TicketLineInfo(String productid, double dMultiply,double dMultiplyPcs,String dMerk,String dModel,String dWarna,
            String catBocel,String patahHadware,String kainLuntur,String kainPudar,String kainBercak,
            String kondisiLain,Boolean isDiambil,String namaPengambil,String noHpPengambil,String diKerjakanOleh,double dPrice, TaxInfo tax, Properties props) {
        init(productid, null, dMultiply,dMultiplyPcs,dMerk,dModel,dWarna,catBocel,patahHadware,kainLuntur,kainPudar,kainBercak
                ,kondisiLain,isDiambil,namaPengambil,noHpPengambil,diKerjakanOleh,dPrice, tax, props);
    }

    /**
     *
     * @param productid
     * @param dMultiply
     * @param dPrice
     * @param tax
     */
    public TicketLineInfo(String productid, double dMultiply,double dMultiplyPcs,String dMerk,String dModel,String dWarna,
            String catBocel,String patahHadware,String kainLuntur,String kainPudar,String kainBercak,
            String kondisiLain,Boolean isDiambil,String namaPengambil,String noHpPengambil,String diKerjakanOleh, double dPrice, TaxInfo tax) {
        init(productid, null, dMultiply,dMultiplyPcs,dMerk,dModel,dWarna,catBocel,patahHadware,kainLuntur,kainPudar,kainBercak
                ,kondisiLain,isDiambil,namaPengambil,noHpPengambil,diKerjakanOleh, dPrice, tax, new Properties());
    }

    /**
     * Example: Call from script.TotalDiscount event
     * @param productid
     * @param productname
     * @param producttaxcategory
     * @param productprinter
     * @param dMultiply
     * @param dPrice
     * @param tax
     */
    public TicketLineInfo(String productid, String productname, String producttaxcategory, String productprinter, double dMultiply,double dMultiplyPcs,String dMerk,String dModel,String dWarna,
            String catBocel,String patahHadware,String kainLuntur,String kainPudar,String kainBercak,
            String kondisiLain,Boolean isDiambil,String namaPengambil,String noHpPengambil,String diKerjakanOleh, double dPrice, TaxInfo tax) {
        Properties props = new Properties();
        props.setProperty("product.name", productname);
        props.setProperty("product.taxcategoryid", producttaxcategory);
        props.setProperty("product.printer", productprinter);   //added to props as may introduce printer redirect
        
        init(productid, null, dMultiply,dMultiplyPcs,dMerk,dModel,dWarna,catBocel,patahHadware,kainLuntur,kainPudar,kainBercak
                ,kondisiLain,isDiambil,namaPengambil,noHpPengambil,diKerjakanOleh, dPrice, tax, props);
    }

    /**
     * Example: Call from script.LineDiscount event
     * @param productname
     * @param producttaxcategory
     * @param productprinter    //added to props as may introduce printer redirect
     * @param dMultiply
     * @param dPrice
     * @param tax
     */
    public TicketLineInfo(String productname, String producttaxcategory, String productprinter, double dMultiply,double dMultiplyPcs,String dMerk,String dModel,String dWarna,
            String catBocel,String patahHadware,String kainLuntur,String kainPudar,String kainBercak,
            String kondisiLain,Boolean isDiambil,String namaPengambil,String noHpPengambil,String diKerjakanOleh, double dPrice, TaxInfo tax) {

        Properties props = new Properties();
        props.setProperty("product.name", productname);
        props.setProperty("product.taxcategoryid", producttaxcategory);
        props.setProperty("product.printer", productprinter);
        
        init(null, null, dMultiply,dMultiplyPcs,dMerk,dModel,dWarna,catBocel,patahHadware,kainLuntur,kainPudar,kainBercak
                ,kondisiLain,isDiambil,namaPengambil,noHpPengambil,diKerjakanOleh, dPrice, tax, props);
    }

    /**
     *
     */
    public TicketLineInfo() {
        init(null, null, 0.0, 0.0,null,null,null,null,null,null,null,null,null,false,null,null,null,0.0, null, new Properties());
    }

    /**
     *
     * @param product
     * @param dMultiply
     * @param dPrice
     * @param tax
     * @param attributes
     */
    public TicketLineInfo(ProductInfoExt product, double dMultiply,double dMultiplyPcs,String dMerk,String dModel,String dWarna,
            String catBocel,String patahHadware,String kainLuntur,String kainPudar,String kainBercak,
            String kondisiLain,Boolean isDiambil,String namaPengambil,String noHpPengambil,String diKerjakanOleh, double dPrice, TaxInfo tax, Properties attributes) {

        String pid;

        if (product == null) {
            pid = null;
            tax = null;
        } else {
            pid = product.getID();
            attributes.setProperty("product.name", product.getName());
            attributes.setProperty("product.reference", product.getReference());
            attributes.setProperty("product.code", product.getCode());
            attributes.setProperty("product.satuan", product.getSatuan());
            attributes.setProperty("product.hargareguler", Double.toString(product.getHargaReguler()));
            attributes.setProperty("product.hargaexpress", Double.toString(product.getHargaExpress()));
            attributes.setProperty("product.isvoucher",product.isvoucher() ? "true" : "false");
            
            
            if (product.getMemoDate() == null) {
                attributes.setProperty("product.memodate", "1900-01-01 00:00:01");                
            } else {
                attributes.setProperty("product.memodate", product.getMemoDate());                
            }
         
            attributes.setProperty("product.com", product.isCom() ? "true" : "false");
            attributes.setProperty("product.constant", product.isConstant() ? "true" : "false");

            if (product.getPrinter() != null) {
                attributes.setProperty("product.printer", product.getPrinter());
            } else {
                attributes.setProperty("product.printer", "1");
            }    
            
            attributes.setProperty("product.service", product.isService() ? "true" : "false");
            attributes.setProperty("product.vprice", product.isVprice() ? "true" : "false");
            attributes.setProperty("product.verpatrib", product.isVerpatrib() ? "true" : "false");

            if (product.getTextTip() != null) {
                attributes.setProperty("product.texttip", product.getTextTip());
            }
 
            attributes.setProperty("product.warranty", product.getWarranty()? "true" : "false");        
       
            if (product.getAttributeSetID() != null) {
                attributes.setProperty("product.attsetid", product.getAttributeSetID());
            }
            
            attributes.setProperty("product.taxcategoryid", product.getTaxCategoryID());
        
            if (product.getCategoryID() != null) {
                attributes.setProperty("product.categoryid", product.getCategoryID());
            }

            if ("true".equals(attributes.getProperty("ticket.updated"))) {
                attributes.setProperty("ticket.updated", "false");                
            } else {
                attributes.setProperty("ticket.updated", "true");                
            }
            
            
        }

        init(pid, null, dMultiply,dMultiplyPcs,dMerk,dModel,dWarna,catBocel,patahHadware,kainLuntur,kainPudar,kainBercak
                ,kondisiLain,isDiambil,namaPengambil,noHpPengambil,diKerjakanOleh, dPrice, tax, attributes);
    }

    /**
     *
     * @param oProduct
     * @param dPrice
     * @param tax
     * @param attributes
     */
    public TicketLineInfo(ProductInfoExt oProduct, double dPrice, TaxInfo tax, Properties attributes) {
        this(oProduct, 1.0,1.0,null,null,null,null,null,null,null,null,null,false,null,null,null, dPrice, tax, attributes);
    }

    /**
     *
     * @param line
     */
    public TicketLineInfo(TicketLineInfo line) {
        init(line.productid, line.attsetinstid, line.multiply,line.multiplyPcs,line.merk,line.model,line.warna,line.catBocelDesc,line.patahHadwareDesc,line.kainLunturDesc,line.kainPudarDesc,
                line.kainBerbercakDesc,line.kondisiLainDesc,line.isDiambil,line.namaPengambil,line.noHpPengambil,line.diKerjakanOleh,line.price, 
            line.tax, (Properties) line.attributes.clone());
    }

    private void init(String productid, String attsetinstid, double dMultiply,double dMultiplyPcs,String dMerk,String dModel,String dWarna,
            String catBocel,String patahHadware,String kainLuntur,String kainPudar,String kainBercak,
            String kondisiLain,Boolean isdiambil,String namaPengambil,String noHpPengambil,String diKerjakanOleh,double dPrice, TaxInfo tax, Properties attributes) {

        this.productid = productid;
        this.attsetinstid = attsetinstid;
        multiply = dMultiply;
        multiplyPcs=dMultiplyPcs;
        merk=dMerk;
        model=dModel;
        warna=dWarna;
        catBocelDesc=catBocel;
        patahHadwareDesc=patahHadware;
        kainLunturDesc=kainLuntur;
        kainPudarDesc=kainPudar;
        kainBerbercakDesc=kainBercak;
        kondisiLainDesc=kondisiLain;
        isDiambil=isdiambil;
        namaPengambil=namaPengambil;
        noHpPengambil=noHpPengambil;
        diKerjakanOleh=diKerjakanOleh;
        price = dPrice;
        this.tax = tax;  
        this.attributes = attributes;
    
        m_sTicket = null;
        m_iLine = -1;
    }

    void setTicket(String ticket, int line) {
        m_sTicket = ticket;
        m_iLine = line;
    }

    /**
     *
     * @param dp
     * @throws BasicException
     */
    @Override
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, m_sTicket);
        dp.setInt(2, m_iLine);
        dp.setString(3, productid);
        dp.setString(4, attsetinstid);
        dp.setDouble(5, multiply);
        dp.setDouble(6, price);
        dp.setString(7, tax.getId());

        try {
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            attributes.storeToXML(o, AppLocal.APP_NAME, "UTF-8");
            dp.setBytes(8, o.toByteArray());
        } catch (IOException e) {
            dp.setBytes(8, null);
        }
        dp.setDouble(9,multiplyPcs);
        dp.setString(10, merk);
        dp.setString(11, model);
        dp.setString(12, warna);
        dp.setString(13, catBocelDesc);
        dp.setString(14, patahHadwareDesc);
        dp.setString(15, kainLunturDesc);
        dp.setString(16, kainPudarDesc);
        dp.setString(17, kainBerbercakDesc);
        dp.setString(18, kondisiLainDesc);
        dp.setBoolean(19, isDiambil);
        dp.setString(20, namaPengambil);
        dp.setString(21, noHpPengambil);
        dp.setString(22, diKerjakanOleh);
        
    }

    /**
     *
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sTicket = dr.getString(1);
        m_iLine = dr.getInt(2);
        productid = dr.getString(3);
        attsetinstid = dr.getString(4);
        multiply = dr.getDouble(5);
        price = dr.getDouble(6);
        tax = new TaxInfo(
            dr.getString(7), 
            dr.getString(8), 
            dr.getString(9), 
            dr.getString(10), 
            dr.getString(11), 
            dr.getDouble(12), 
            dr.getBoolean(13), 
            dr.getInt(14));
        attributes = new Properties();

        try {
            byte[] img = dr.getBytes(15);
            if (img != null) {
                attributes.loadFromXML(new ByteArrayInputStream(img));
            }
        } catch (IOException e) {
        }
        multiplyPcs=dr.getDouble(16);
        merk=dr.getString(17);
        model=dr.getString(18);
        warna=dr.getString(19);
        catBocelDesc=dr.getString(20);
        patahHadwareDesc=dr.getString(21);
        kainLunturDesc=dr.getString(22);
        kainPudarDesc=dr.getString(23);
        kainBerbercakDesc=dr.getString(24);
        kondisiLainDesc=dr.getString(25);
        isDiambil=dr.getBoolean(26);
        namaPengambil=dr.getString(27);
        noHpPengambil=dr.getString(28);
        diKerjakanOleh=dr.getString(29);
    }

    /**
     *
     * @return
     */
    public TicketLineInfo copyTicketLine() {
        TicketLineInfo l = new TicketLineInfo();
        l.productid = productid;
        l.attsetinstid = attsetinstid;
        l.multiply = multiply;
        l.price = price;
        l.tax = tax; 
        l.attributes = (Properties) attributes.clone();
        l.multiplyPcs=multiplyPcs;
        l.merk=merk;
        l.model=model;
        l.warna=warna;
        l.catBocelDesc=catBocelDesc;
        l.patahHadwareDesc=patahHadwareDesc;
        l.kainLunturDesc=kainLunturDesc;
        l.kainPudarDesc=kainPudarDesc;
        l.kainBerbercakDesc=kainBerbercakDesc;
        l.kondisiLainDesc=kondisiLainDesc;
        l.isDiambil=isDiambil;
        l.namaPengambil=namaPengambil;
        l.noHpPengambil=noHpPengambil;
        l.diKerjakanOleh=diKerjakanOleh;
        return l;
    }

    /**
     *
     * @return
     */
    public int getTicketLine() {
        return m_iLine;
    }
// These are the Lookups   
    public String getProductID() {
        return productid;
    }
    
    public Boolean isItemDiambil(){
        return isDiambil;
    }
    
    
    public String getProductCategoryID() {
        return (attributes.getProperty("product.categoryid"));
    }
    public String getProductSatuan() {
        return (attributes.getProperty("product.satuan"));
    }
    public String getProductAttSetId() {
        return attributes.getProperty("product.attsetid");
    }
    public String getProductAttSetInstId() {
        return attsetinstid;
    }    
    public String getProductAttSetInstDesc() {
        return attributes.getProperty("product.attsetdesc", "");
    }
    public String getProductTaxCategoryID() {
        return (attributes.getProperty("product.taxcategoryid"));
    }
    public String getTicketUpdated() {
        return (attributes.getProperty("ticket.updated"));
    }
    public TaxInfo getTaxInfo() {
        return tax;
    }    
    public void setTaxInfo(TaxInfo oTaxInfo) {
        tax = oTaxInfo;
    }     
        
// These appear on Printed TicketLine
    public String getProductName() {
        return attributes.getProperty("product.name");
    }
    
    public String getReference() {
        return attributes.getProperty("product.reference");
    }
    public String getProductMemoDate() {
        return attributes.getProperty("product.memodate");
    }
    public double getPrice() {
        return Math.ceil(price/100)*100;
    }
    public double getMultiply() {
        return multiply;
    }
    public double getMultiplyPcs() {
        return multiplyPcs;
    }
    public String getMerk() {
        return merk;
    }
    public String getModel() {
        return model;
    }
    public String getWarna() {
        return warna;
    }
    public String getCatBocel() {
        return catBocelDesc;
    }
    public String getPatahHadware() {
        return patahHadwareDesc;
    }
    public String getKainLuntur() {
        return kainLunturDesc;
    }
    public String getKainPudar() {
        return kainPudarDesc;
    }
    public String getKainBerbercak() {
        return kainBerbercakDesc;
    }
    public String getKondisiLain() {
        return kondisiLainDesc;
    }
    public String getNamaPengambil() {
        return namaPengambil;
    }
    public String getNoHpPengambil() {
        return noHpPengambil;
    }
    public String getDiKerjakanOleh() {
        return diKerjakanOleh;
    }
    public double getTaxRate() {
        return tax == null ? 0.0 : tax.getRate();
    }
    public double getNewPrice() {
        newprice = price * (1.0 + getTaxRate());
        return Math.ceil(price/100)*100;
    }
    public String getProductPrinter() {
        return attributes.getProperty("product.printer");
    }    

// These are the Summaries    
    public double getPriceTax() {
        return Math.ceil((price * (1.0 + getTaxRate()))/100)*100;
    }

    public Properties getProperties() {
        return attributes;
    }
    public String getProperty(String key) {
        return attributes.getProperty(key);
    }
    public String getProperty(String key, String defaultvalue) {
        return attributes.getProperty(key, defaultvalue);
    }

// These are Ticket Totals    
    public double getTax() {
        return Math.ceil((price * multiply * getTaxRate())/100)*100;
    }
    public double getValue() {
        return Math.ceil((price * multiply * (1.0 + getTaxRate()))/100)*100;
    }
    public double getSubValue() {
        return Math.ceil((price * multiply)/100)*100;
    }

    public Boolean getItemSudahDiambil() {
        return isDiambil;
    }

// SETTERS
    public void setPrice(double dValue) {
        price = dValue;
    }
    
    public void setItemDiambil(Boolean dValue) {
        isDiambil = dValue;
    }
    public void setPriceTax(double dValue) {
        price = dValue / (1.0 + getTaxRate());               
    }
    public void setMultiply(double dValue) {
        if(dValue < 1){
            dValue=1;
        }
        multiply = dValue;
    }
    public void setMultiplyPcs(double dValue) {
        multiplyPcs = dValue;
    }
    public void setMerk(String dValue) {
        merk = dValue;
    }
    public void setModel(String dValue) {
        model = dValue;
    }
    public void setWarna(String dValue) {
        warna = dValue;
    }
    public void setCatBocel(String dValue) {
        catBocelDesc = dValue;
    }
    
    public void setNamaPengambil(String dValue) {
        namaPengambil = dValue;
    }
    public void setNoHpPengambil(String dValue) {
        noHpPengambil = dValue;
    }
    public void setDikerjakanOleh(String dValue) {
        diKerjakanOleh = dValue;
    }
    public void setPatahHadware(String dValue) {
        patahHadwareDesc = dValue;
    }
    public void setKainLuntur(String dValue) {
        kainLunturDesc = dValue;
    }
    public void setKainPudar(String dValue) {
        kainPudarDesc = dValue;
    }
    public void setKainBerbercak(String dValue) {
        kainBerbercakDesc = dValue;
    }
    public void setKondisiLain(String dValue) {
        kondisiLainDesc = dValue;
    }
    
    public void setProperty(String key, String value) {
        attributes.setProperty(key, value);
    }    
    public void setProductTaxCategoryID(String taxID){
        attributes.setProperty("product.taxcategoryid",taxID);
    }
    public void setProductAttSetInstId(String value) {
        attsetinstid = value;
    }
    public void setProductAttSetInstDesc(String value) {
        if (value == null) {
            attributes.remove(value);
        } else {
            attributes.setProperty("product.attsetdesc", value);
        }
    }
    public void setTicketUpdated(String key, String value){
        attributes.setProperty("ticket.updated",value);
    }
    public void setProductPrinter(String value) {
        if (value == null) {
            attributes.remove(value);
        } else {
            attributes.setProperty("product.printer", value);
        }
    }    
    
    /**
     *
     * @return
     */
    // Print to actual ${ticketline
    public String printReference() {
        return StringUtils.encodeXML(attributes.getProperty("product.reference"));
    }
    
    public String printItemDiambil() {
        return isDiambil ? "Ya": "Belum";
    }
    
    public String printCode() {
        return StringUtils.encodeXML(attributes.getProperty("product.code"));
    }
    public String printName() {
        return StringUtils.encodeXML(attributes.getProperty("product.name"));
    }
    public String printProductMemoDate() {
        return StringUtils.encodeXML(attributes.getProperty("product.memodate"));
    }
    public String printProductSatuan() {
        return StringUtils.encodeXML(attributes.getProperty("product.satuan"));
    }  
    public String printPrice() {
        return Formats.CURRENCY.formatValue(getPrice());
    }
    public String printPriceTax() {
        return Formats.CURRENCY.formatValue(getPriceTax());
    }
    
    public String printMultiply() {
        BigDecimal big=new BigDecimal(Double.toString(multiply));
        big=big.setScale(1,RoundingMode.CEILING);
        return Double.toString(big.doubleValue());
//        return Formats.DOUBLE.formatValue(multiply);
    }
    public String printMultiplyPcs() {
        BigDecimal big=new BigDecimal(Double.toString(multiplyPcs));
        big=big.setScale(1,RoundingMode.CEILING);
        return Double.toString(big.doubleValue());
//        return Formats.DOUBLE.formatValue(multiply);
    }
    public String printValue() {
        return Formats.CURRENCY.formatValue(getValue());
    }
    public String printTaxRate() {
        return Formats.PERCENT.formatValue(getTaxRate());
    }
    public String printSubValue() {
        return Formats.CURRENCY.formatValue(getSubValue());
    }
    public String printTax() {
        return Formats.CURRENCY.formatValue(getTax());
    }
    public String printTextTip() {
	return attributes.getProperty("product.texttip");
    }
    public String printPrinter() {
        return StringUtils.encodeXML(attributes.getProperty("product.printer"));
    }      
    public boolean isProductCom() {
       return "true".equals(attributes.getProperty("product.com"));
    }
    public boolean isProductService() {
	return "true".equals(attributes.getProperty("product.service"));
    }
    public boolean isProductVprice() {
	return "true".equals(attributes.getProperty("product.vprice"));
    }
    public boolean isProductVerpatrib() {
	return "true".equals(attributes.getProperty("product.verpatrib"));
    }
    public boolean isProductWarranty() {
	return "true".equals(attributes.getProperty("product.warranty"));
    }    
    public boolean getUpdated() {
        return "true".equals(attributes.getProperty("ticket.updated"));
    }
    public void setUpdated(Boolean value) {
        updated = value;
    }
   
}