/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.sales;

/**
 *
 * @author LENOVO
 */
public class ItemComboBox {
        private int id;
        private String description;
//        public ItemComboBox(int id, String description)
//        {
//            
//        }

        ItemComboBox(int id, String entity) {
            this.id = id;
            this.description = description;
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    
        
 
        public int getId()
        {
            return id;
        }
 
        public String getDescription()
        {
            return description;
        }
 
        public String toString()
        {
            return description;
        }
}
