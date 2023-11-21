/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.quickcartpos;

/**
 *
 * @author HP
 */
public class Inventory extends javax.swing.JFrame {

    /**
     * Creates new form Inventory
     */
    public Inventory() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AppNamePanel = new javax.swing.JPanel();
        QuickCartLabel = new javax.swing.JLabel();
        MenuPanel = new javax.swing.JPanel();
        AddItemButton = new javax.swing.JLabel();
        DeleteItemButton = new javax.swing.JLabel();
        EditItemButton = new javax.swing.JLabel();
        UpdateItemQButton = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        AppNamePanel.setBackground(new java.awt.Color(174, 102, 183));

        QuickCartLabel.setFont(new java.awt.Font("Stencil", 0, 48)); // NOI18N
        QuickCartLabel.setForeground(new java.awt.Color(255, 255, 255));
        QuickCartLabel.setText("QuickCart");

        org.jdesktop.layout.GroupLayout AppNamePanelLayout = new org.jdesktop.layout.GroupLayout(AppNamePanel);
        AppNamePanel.setLayout(AppNamePanelLayout);
        AppNamePanelLayout.setHorizontalGroup(
            AppNamePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(AppNamePanelLayout.createSequentialGroup()
                .add(335, 335, 335)
                .add(QuickCartLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 281, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(394, Short.MAX_VALUE))
        );
        AppNamePanelLayout.setVerticalGroup(
            AppNamePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, AppNamePanelLayout.createSequentialGroup()
                .addContainerGap(49, Short.MAX_VALUE)
                .add(QuickCartLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(45, 45, 45))
        );

        MenuPanel.setBackground(new java.awt.Color(213, 190, 216));

        AddItemButton.setBackground(new java.awt.Color(255, 255, 255));
        AddItemButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AddItemButton.setText("Add Item");
        AddItemButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 20, 118)));
        AddItemButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AddItemButtonMouseClicked(evt);
            }
        });

        DeleteItemButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DeleteItemButton.setText("Delete Item");
        DeleteItemButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 20, 118)));
        DeleteItemButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DeleteItemButtonMouseClicked(evt);
            }
        });

        EditItemButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EditItemButton.setText("Edit Item");
        EditItemButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 20, 118)));

        UpdateItemQButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        UpdateItemQButton.setText("Update Item Quantity");
        UpdateItemQButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 20, 118)));

        org.jdesktop.layout.GroupLayout MenuPanelLayout = new org.jdesktop.layout.GroupLayout(MenuPanel);
        MenuPanel.setLayout(MenuPanelLayout);
        MenuPanelLayout.setHorizontalGroup(
            MenuPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, MenuPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(MenuPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(UpdateItemQButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(DeleteItemButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(EditItemButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(AddItemButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(43, 43, 43))
        );
        MenuPanelLayout.setVerticalGroup(
            MenuPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MenuPanelLayout.createSequentialGroup()
                .addContainerGap(53, Short.MAX_VALUE)
                .add(AddItemButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(DeleteItemButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(EditItemButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(UpdateItemQButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(141, 141, 141))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MenuPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(AppNamePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(AppNamePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(MenuPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AddItemButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AddItemButtonMouseClicked

    }//GEN-LAST:event_AddItemButtonMouseClicked

    private void DeleteItemButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DeleteItemButtonMouseClicked
        ProductInfoRetrieval pir = new ProductInfoRetrieval();
        pir.setVisible(true);
        setVisible(false);
    }//GEN-LAST:event_DeleteItemButtonMouseClicked
    private void EditItemButtonMouseClicked(java.awt.event.MouseEvent evt) {                                              
        
    } 
    private void UpdateItemQButtonMouseClicked(java.awt.event.MouseEvent evt) {                                              
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Inventory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Inventory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Inventory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Inventory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Inventory().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AddItemButton;
    private javax.swing.JPanel AppNamePanel;
    private javax.swing.JLabel DeleteItemButton;
    private javax.swing.JLabel EditItemButton;
    private javax.swing.JPanel MenuPanel;
    private javax.swing.JLabel QuickCartLabel;
    private javax.swing.JLabel UpdateItemQButton;
    // End of variables declaration//GEN-END:variables
}
