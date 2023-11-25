package com.mycompany.quickcartpos;

import java.awt.Color;
import java.awt.Container;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import static java.lang.Thread.sleep;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import java.io.File;
import java.sql.Statement;

public class Cart extends javax.swing.JFrame {

    public class QuantityButtonEditor extends AbstractCellEditor implements TableCellEditor {

        private QuantityRenderer renderer;

        public QuantityButtonEditor() {
            renderer = new QuantityRenderer();
            renderer.plusButton.setBackground(new Color(213, 190, 216));
            renderer.getPlusButton().addActionListener(e -> {
                renderer.handlePlusButton();
                fireEditingStopped();
            });

            renderer.minusButton.setBackground(new Color(213, 190, 216));
            renderer.getMinusButton().addActionListener(e -> {
                renderer.handleMinusButton();
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            renderer.setQuantity(Integer.parseInt(value.toString()));
            renderer.setText(renderer.quantityLabel.getText());
            return renderer;
        }

        @Override
        public Object getCellEditorValue() {
            return renderer.getQuantity();
        }
    }

    public class QuantityRenderer extends DefaultTableCellRenderer {

        private JLabel quantityLabel;
        private final JButton plusButton;
        private final JButton minusButton;

        public QuantityRenderer() {
            quantityLabel = new JLabel("1");
            plusButton = new JButton("+");
            minusButton = new JButton("-");
            setLayout(new BorderLayout());
            add(plusButton, BorderLayout.WEST);
            add(quantityLabel, BorderLayout.CENTER);
            add(minusButton, BorderLayout.EAST);
        }

        public void setQuantity(int quantity) {
            quantityLabel.setText(String.valueOf(quantity));
        }

        private void handlePlusButton() {
            String bcode = "";
            int selectedRow = cartTable.getSelectedRow();
            try {
                BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\hp\\Desktop\\cart.txt"));
                try {
                    for (int lineNumber = 0; lineNumber <= selectedRow; lineNumber++) {
                        bcode = reader.readLine();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
            }
            int quantity = getQuantity();
            System.out.println("Row " + selectedRow + " Quantity " + quantity);
            Object q = cartTable.getValueAt(selectedRow, 1);
            if (Integer.parseInt(q.toString()) < getActualQuantity(bcode)) {
                setQuantity(quantity + 1);
            } else {
                JOptionPane.showMessageDialog(null, "Only " + getActualQuantity(bcode) + " products are available in the inventory.");
            }
        }

        private void handleMinusButton() {
            int selectedRow = cartTable.getSelectedRow();
            int quantity = getQuantity();
            System.out.println("Row " + selectedRow + " Quantity " + quantity);
            if (quantity > 0) {
                setQuantity(quantity - 1);
            }
        }

        public int getQuantity() {
            return Integer.parseInt(quantityLabel.getText());
        }

        public JButton getPlusButton() {
            return plusButton;
        }

        public JButton getMinusButton() {
            return minusButton;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setQuantity(Integer.parseInt(value.toString()));
            setText(quantityLabel.getText());
            return this;
        }
    }

    public class payment extends JFrame {

        public payment(float ba) {
            makePayment(ba);
        }

        private void makePayment(float billamt) {
            float amount = billamt;
            JTextField email, cardNum, cardHolderName, CVV, expirationMonth, expirationYear;
            JButton confirm;
            setTitle("Credit Card Info");
            setBounds(200, 200, 480, 300);

            System.out.println(amount);
            //initializing the text fields
            email = new JTextField(30);
            cardHolderName = new JTextField(30);
            cardNum = new JTextField(16);
            CVV = new JTextField(3);
            expirationMonth = new JTextField(2);
            expirationYear = new JTextField(4);
            confirm = new JButton("Add Card");

            //key listners and action listners for constraints and buttons
            cardHolderName.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isLetter(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                        e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            email.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    String currentText = email.getText();
                    if (currentText.contains("@") && e.getKeyChar() == '@') {
                        e.consume();
                    }
                    if (currentText.contains(".com") && e.getKeyChar() == '.') {
                        e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            cardNum.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c) || cardNum.getText().length() >= 16) {
                        e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            CVV.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c) || CVV.getText().length() >= 3) {
                        e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            expirationMonth.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c) || expirationMonth.getText().length() >= 2 || (Integer.parseInt(expirationMonth.getText() + c) > 12)) {
                        e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            expirationYear.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c) || expirationYear.getText().length() >= 4) {
                        e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            confirm.addActionListener((ActionEvent e) -> {
                String us = cardHolderName.getText(), em = email.getText(), cn = cardNum.getText(), sec = CVV.getText(), exm = expirationMonth.getText(),
                        exy = expirationYear.getText();
                if (us.isEmpty() || em.isEmpty() || cn.isEmpty() || sec.isEmpty() || exm.isEmpty() || exy.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                    return; // Stop further execution if any field is empty
                }
                try {
                    Stripe.apiKey = "sk_test_51OG4jYHt3dCnRMr3YvpsxebeSJjmDdziKtFttBJF6sSbCWw8fg5jsmrVhzLWTL4Nqu3MBY7M14oPOAq7lQpaXvh300NLNObiOI";
                    Map<String, Object> customer = new HashMap<>();
                    customer.put("Email", em);
                    customer.put("Name", em);
                    
                    Map<String, Object> card = new HashMap<>();
                    card.put("Number", cn);
                    card.put("CVV", sec);
                    card.put("expiry_month", exm);
                    card.put("expiry_year", exy);
                    
                    Map<String, Object> chargeParams = new HashMap<String, Object>();
                    chargeParams.put("amount", getAmountForCardPayment());
                    chargeParams.put("currency", "eur");
                    chargeParams.put("source", "tok_visa");
                    chargeParams.put("description", "Testing charge using credit card details");
                    Charge charge = Charge.create(chargeParams);
                    
                    JOptionPane.showMessageDialog(null, "Payment Paid Successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    setVisible(false);
                } catch (Exception err) {
                    System.out.println(err.getMessage());
                }
            } //card number: 42 42 42 42 42 42 42 42
            //cvv: 123
            //expiry month: 12  (any future month)
            //expiry year: 2023 (any future year)
            );

            //Adding the components to a panel
            JPanel btnPanel = new JPanel(new FlowLayout());
            JPanel mp = new JPanel(new GridLayout(6, 2));
            mp.add(new JLabel("Enter Card Holder's Name:"));
            mp.add(cardHolderName);
            mp.add(new JLabel("Enter Email:"));
            mp.add(email);
            mp.add(new JLabel("Enter Card Number:"));
            mp.add(cardNum);
            mp.add(new JLabel("Enter Security Code:"));
            mp.add(CVV);
            mp.add(new JLabel("Enter Expiration Month:"));
            mp.add(expirationMonth);
            mp.add(new JLabel("Enter Expiration Year:"));
            mp.add(expirationYear);
            btnPanel.add(confirm);

            add(mp);
            add(btnPanel, BorderLayout.SOUTH);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setVisible(true);
        }
    }
    int endRow;
    int actualProdQuantity;
    String payment;
    float amount = 0.0f;

    public int getAmountForCardPayment() {
        DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
        int rowCount = model.getRowCount();
        int totalAmount = 0;
        for (int i = 0; i < rowCount; i++) {
            String productName = model.getValueAt(i, 0).toString();
            int quantity = Integer.parseInt(model.getValueAt(i, 1).toString());
            double price = Double.parseDouble(model.getValueAt(i, 2).toString());
            double itemTotal = quantity * price;
            totalAmount += itemTotal;
        }
        return totalAmount;
    }

    public Cart() throws InterruptedException, IOException {
        initComponents();
        deleteSheetData();
        clearCartFile();
        Container con = getContentPane();
        con.setBackground(Color.white);
        checkDatabaseForBarcode();
        paymentButtonGroup.add(cashPayment);
        cashPayment.setBackground(Color.WHITE);
        cashPayment.setOpaque(true);
        paymentButtonGroup.add(cardPayment);
        cardPayment.setBackground(Color.WHITE);
        cardPayment.setOpaque(true);
    }

    public boolean sheetIsEmpty() {
        try {
            String spreadsheetId = "1MK0dZThaOIboZmmgiHKVRT1RPwIIRAUH-s5QeP0Gx1Q";
            String range = "A:Z";

            Sheets sheetsService = getSheetsService();

            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();

            return values == null || values.isEmpty();
        } catch (IOException | GeneralSecurityException e) {
            //e.printStackTrace();
            return true;
        }
    }

    private Sheets getSheetsService() throws IOException,GeneralSecurityException {
        InputStream jsonStream = getClass().getResourceAsStream("/zeta-tracer-405617-26cc2165ac80.json");

        if (jsonStream != null) {
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(jsonStream)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
            HttpCredentialsAdapter httpCredentialsAdapter = new HttpCredentialsAdapter(credentials);
            return new Sheets.Builder(
                    com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    httpCredentialsAdapter)
                    .setHttpRequestInitializer(httpCredentialsAdapter)
                    .setApplicationName("QuickCart")
                    .build();
        } else {
            throw new IOException("Could not load the JSON file.");
        }
    }

    public String fetchSheetData() {

        try {
            InputStream jsonStream = getClass().getResourceAsStream("/zeta-tracer-405617-26cc2165ac80.json");

            if (jsonStream != null) {
                GoogleCredentials credentials = ServiceAccountCredentials.fromStream(jsonStream)
                        .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
                HttpCredentialsAdapter httpCredentialsAdapter = new HttpCredentialsAdapter(credentials);
                Sheets sheetsService = new Sheets.Builder(
                        com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        httpCredentialsAdapter)
                        .setHttpRequestInitializer(httpCredentialsAdapter)
                        .setApplicationName("QuickCart")
                        .build();
                String spreadsheetId = "1MK0dZThaOIboZmmgiHKVRT1RPwIIRAUH-s5QeP0Gx1Q";
                String lastRowRange = "B:B";
                //System.out.println("spreadsheetId = "+spreadsheetId);
                ValueRange lastRowResponse = sheetsService.spreadsheets().values()
                        .get(spreadsheetId, lastRowRange)
                        .execute();

                List<List<Object>> lastRowValues = lastRowResponse.getValues();
                if (sheetIsEmpty()) {
                    return "0";
                }
                if (lastRowValues != null && !lastRowValues.isEmpty()) {
                    int lastRow = lastRowValues.size();
                    System.out.println("End row: " + endRow + " Last row: " + lastRow);
                    if (endRow >= lastRow) {
                        endRow = lastRow;
                        return "0";
                    } else {
                        String range = "B" + lastRow;
                        ValueRange response = sheetsService.spreadsheets().values()
                                .get(spreadsheetId, range)
                                .execute();

                        List<List<Object>> values = response.getValues();

                        if (values != null && !values.isEmpty() && values.get(0) != null && !values.get(0).isEmpty()) {
                            endRow = lastRow;
                            return values.get(0).get(0).toString();
                        }

                    }
                }
                return "1";
            } else {
                System.out.println("Could not load the JSON file.");
                return "2";
            }
        } catch (IOException | GeneralSecurityException e) {
            //e.printStackTrace();
            //e.getMessage();
            return "0";
        }
    }

    public void updateTable() {
        checkDatabaseForBarcode();
    }

    public void checkDatabaseForBarcode() {
        DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
        String scannedBarcode = fetchSheetData();
        if (scannedBarcode.equals("0")) {
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\hp\\Desktop\\cart.txt"));
            String line;
            try {
                for (int lineNumber = 0; (line = reader.readLine()) != null; lineNumber++) {
                    if (scannedBarcode.equals(line)) {
                        Object n = cartTable.getModel().getValueAt(lineNumber, 1);
                        int q = Integer.parseInt(n.toString());
                        if (q < getActualQuantity(scannedBarcode)) {
                            cartTable.getModel().setValueAt(q + 1, lineNumber, 1);
                        } else {
                            JOptionPane.showMessageDialog(null, "Only " + getActualQuantity(scannedBarcode) + " products are available in the inventory.");
                        }
                        return;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(scannedBarcode);
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quickcartdb", "root", "root123");
            String query = "SELECT * FROM Inventory WHERE barcode = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, scannedBarcode);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String productName = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");

                actualProdQuantity = quantity;
                //model.setRowCount(0);
                Object[] rowData = new Object[100];
                rowData[0] = productName;
                rowData[1] = "1";
                QuantityRenderer qr = new QuantityRenderer();
                cartTable.getColumnModel().getColumn(1).setCellEditor(new QuantityButtonEditor());
                cartTable.getColumnModel().getColumn(1).setCellRenderer(qr);
                cartTable.setFocusable(false);
                rowData[2] = price;
                model.addRow(rowData);
                try (FileWriter writer = new FileWriter("C:\\Users\\hp\\Desktop\\cart.txt", true)) {
                    writer.write(scannedBarcode + "\n");
                } catch (Exception ex) {
                }
            } else {
                JOptionPane.showMessageDialog(null, "Product " + scannedBarcode + " not found in the inventory.");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (HeadlessException | SQLException e) {
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paymentButtonGroup = new javax.swing.ButtonGroup();
        AppNamePanel = new javax.swing.JPanel();
        QuickCartLabel = new javax.swing.JLabel();
        MenuPanel = new javax.swing.JPanel();
        HomeButton = new javax.swing.JLabel();
        InventoryButton = new javax.swing.JLabel();
        salesHistoryButton = new javax.swing.JLabel();
        OrdersButton = new javax.swing.JLabel();
        CartButton = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        cartTable = new javax.swing.JTable();
        billButon = new javax.swing.JButton();
        cashPayment = new javax.swing.JRadioButton();
        cardPayment = new javax.swing.JRadioButton();
        PayementMethod = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cart");

        AppNamePanel.setBackground(new java.awt.Color(174, 102, 183));

        QuickCartLabel.setFont(new java.awt.Font("Stencil", 0, 48)); // NOI18N
        QuickCartLabel.setForeground(new java.awt.Color(255, 255, 255));
        QuickCartLabel.setText("QuickCart");

        javax.swing.GroupLayout AppNamePanelLayout = new javax.swing.GroupLayout(AppNamePanel);
        AppNamePanel.setLayout(AppNamePanelLayout);
        AppNamePanelLayout.setHorizontalGroup(
            AppNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppNamePanelLayout.createSequentialGroup()
                .addGap(335, 335, 335)
                .addComponent(QuickCartLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        AppNamePanelLayout.setVerticalGroup(
            AppNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AppNamePanelLayout.createSequentialGroup()
                .addContainerGap(49, Short.MAX_VALUE)
                .addComponent(QuickCartLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );

        MenuPanel.setBackground(new java.awt.Color(213, 190, 216));

        HomeButton.setBackground(new java.awt.Color(255, 255, 255));
        HomeButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        HomeButton.setText("Home");
        HomeButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 20, 118)));
        HomeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HomeButtonMouseClicked(evt);
            }
        });

        InventoryButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        InventoryButton.setText("Inventory");
        InventoryButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 20, 118)));
        InventoryButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                InventoryButtonMouseClicked(evt);
            }
        });

        salesHistoryButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        salesHistoryButton.setText("Sales History");
        salesHistoryButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 20, 118)));

        OrdersButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        OrdersButton.setText("Orders");
        OrdersButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 20, 118)));

        CartButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CartButton.setText("Cart");
        CartButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 20, 118)));

        javax.swing.GroupLayout MenuPanelLayout = new javax.swing.GroupLayout(MenuPanel);
        MenuPanel.setLayout(MenuPanelLayout);
        MenuPanelLayout.setHorizontalGroup(
            MenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(HomeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(InventoryButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
            .addComponent(salesHistoryButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(CartButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(OrdersButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MenuPanelLayout.setVerticalGroup(
            MenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(HomeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InventoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(salesHistoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(OrdersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cartTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Name", "Quantity", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false
            };
        });
        jScrollPane1.setViewportView(cartTable);

        billButon.setText("Generate Bill");
        billButon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                billButonActionPerformed(evt);
            }
        });

        cashPayment.setText("Cash");
        cashPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashPaymentActionPerformed(evt);
            }
        });

        cardPayment.setText("Card");
        cardPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cardPaymentActionPerformed(evt);
            }
        });

        PayementMethod.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        PayementMethod.setText("Payment Method");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(MenuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cashPayment)
                    .addComponent(cardPayment)
                    .addComponent(PayementMethod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(billButon, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(84, Short.MAX_VALUE))
            .addComponent(AppNamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(AppNamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(PayementMethod)
                                .addGap(15, 15, 15)
                                .addComponent(cashPayment)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cardPayment)
                                .addGap(22, 22, 22)
                                .addComponent(billButon)))
                        .addGap(49, 49, 49))
                    .addComponent(MenuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void HomeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HomeButtonMouseClicked
        Home h = new Home();
        h.setVisible(true);
        setVisible(false);
    }//GEN-LAST:event_HomeButtonMouseClicked

    private void InventoryButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InventoryButtonMouseClicked
        ProductInfoRetrieval pir = new ProductInfoRetrieval();
        pir.setVisible(true);
        setVisible(false);
    }//GEN-LAST:event_InventoryButtonMouseClicked
    private boolean isPaymentMethodSelected() {
        Enumeration<AbstractButton> elements = paymentButtonGroup.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton button = elements.nextElement();
            if (button.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private void billButonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_billButonActionPerformed
        if (isPaymentMethodSelected()) {
            BufferedReader reader = null;
            try {
                DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
                int rowCount = model.getRowCount();
                double totalAmount = 0.0;
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = dateFormat.format(now);
                Home h = new Home();
                Font f = new Font("Segoe UI", Font.PLAIN, 18);
                String s = "*************************************************************************************************\n";
                String d = "--------------------------------------------------------------------------------------------------------------------------\n";
                StringBuilder billContent = new StringBuilder(s + "\t\t        QUICKCART\n");
                billContent.append(s).append("\nDate: ").append(formattedDateTime).append("\nPrinted by: ").append(h.name.getText()).append("\n\n").append(d).append("Name\t\tQuantity\t\tTotal\n\n");
                reader = new BufferedReader(new FileReader("C:\\Users\\hp\\Desktop\\cart.txt"));
                String bcode;
                //reader.readLine();
                int orderQuantity=0;
                
                for (int i = 0; i < rowCount; i++) {
                    String productName = model.getValueAt(i, 0).toString();
                    int quantity = Integer.parseInt(model.getValueAt(i, 1).toString());
                    double price = Double.parseDouble(model.getValueAt(i, 2).toString());
                    double itemTotal = quantity * price;
                    bcode = reader.readLine();
                    int actualQuantity = getActualQuantity(bcode);
                    int q = actualQuantity - quantity;
                    orderQuantity+=quantity;
                    System.out.println("q: " + q + " actualQuantity: " + actualQuantity + " quantity: " + quantity);
                    updateQuantityInDB(q, bcode);
                    totalAmount += itemTotal;
                    billContent.append(productName).append("\t\t").append(quantity).append(" x Rs.").append(price)
                            .append("\t\t Rs.").append(itemTotal).append("\n");
                }
                double gst;
                if(cashPayment.isSelected()){
                gst = 0.17 * totalAmount;
                totalAmount+=gst;
                billContent.append(d).append("\n\t\t\t\tGST: Rs.").append(String.format("%.2f", gst));
                }
                else if(cardPayment.isSelected()){
                }
                billContent.append("\n\t\t\t\tTotal Amount: Rs.").append(totalAmount).append("\n").append(payment);
                showBillDialog(billContent.toString());
                addOrderToTable(totalAmount,formattedDateTime,orderQuantity);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a payment method", "Payment Method Required", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_billButonActionPerformed
    private void addOrderToTable(double totalAmount,String DateTime,int quantity){
        Home home=new Home();
        
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quickcartdb", "root", "root123")) {
            String query = "INSERT INTO orders (seller, amount,products,datetime ) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, home.name.getText());
                pstmt.setDouble(2, totalAmount);
                pstmt.setInt(3, quantity);
                pstmt.setString(4, DateTime);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error adding row to the database: " + e.getMessage());
        }
    }
    private int getActualQuantity(String barcode) {
        String name;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quickcartdb", "root", "root123")) {
            String query = "SELECT name,quantity FROM Inventory WHERE barcode = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, barcode);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        name = resultSet.getString("name");
                        System.out.println("name: " + name);
                        return resultSet.getInt("quantity");
                    } else {
                        System.err.println("Barcode not found: " + barcode);
                        return -1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private void cashPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashPaymentActionPerformed
        payment = "Payment via cash";
    }//GEN-LAST:event_cashPaymentActionPerformed

    private void cardPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cardPaymentActionPerformed
        payment = "Payment via Credit Card";
        //setTotalBill();
        payment obj = new payment(amount);
    }//GEN-LAST:event_cardPaymentActionPerformed
    private void setTotalBill() {
        int rowCount = cartTable.getRowCount();
        System.out.println("Row:" + rowCount);
        for (int i = 0; i < rowCount; i++) {
            float value = (float) cartTable.getValueAt(i, 2);
            amount += value;
        }
    }

    private void showBillDialog(String billContent) {
        JTextArea billTextArea = new JTextArea(billContent);
        JButton printButton = new JButton("Print");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printToPDF(billContent);
            }
        });
        JScrollPane scrollPane = new JScrollPane(billTextArea);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(printButton, BorderLayout.SOUTH);
        //billTextArea.setEditable(false);
        JOptionPane.showMessageDialog(this, panel, "Generated Bill", JOptionPane.INFORMATION_MESSAGE);
    }

    private void printToPDF(String textToPrint) {
        textToPrint = textToPrint.replaceAll("\t\t", " ".repeat(48));
        textToPrint = textToPrint.replaceAll("\t\t\t\t", " ".repeat(304));
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("bill.pdf"));
            document.open();
            document.add(new Paragraph(textToPrint));
            document.close();
            clearCartFile();
            deleteSheetData();
            DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
            model.setRowCount(0);
            System.out.println("PDF printed successfully.");
            JOptionPane.showMessageDialog(this, "PDF printed successfully.", "Printed", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | DocumentException ex) {
            ex.printStackTrace();
        }
    }

    public static void clearCartFile() throws IOException {
        
        File file = new File("C:\\Users\\hp\\Desktop\\cart.txt");
        if (!file.exists()) {
            throw new IOException("File does not exist");
        }
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write("");
            System.out.println("clear file");
        }
        
    }

    public void deleteSheetData() {
        try {
            InputStream jsonStream = getClass().getResourceAsStream("/zeta-tracer-405617-26cc2165ac80.json");
            if (jsonStream != null) {
                GoogleCredentials credentials = ServiceAccountCredentials.fromStream(jsonStream)
                        .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
                HttpCredentialsAdapter httpCredentialsAdapter = new HttpCredentialsAdapter(credentials);
                Sheets sheetsService = new Sheets.Builder(
                        com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        httpCredentialsAdapter)
                        .setHttpRequestInitializer(httpCredentialsAdapter)
                        .setApplicationName("QuickCart")
                        .build();
                String spreadsheetId = "1MK0dZThaOIboZmmgiHKVRT1RPwIIRAUH-s5QeP0Gx1Q";
                String range = "A:Z";
                ClearValuesRequest requestBody = new ClearValuesRequest();
                sheetsService.spreadsheets().values()
                        .clear(spreadsheetId, range, requestBody)
                        .execute();
                System.out.println("Data deleted successfully.");
            } else {
                System.out.println("Could not load the JSON file.");
            }
        } catch (IOException | GeneralSecurityException e) {
            //e.printStackTrace();
        }
    }

    private void updateQuantityInDB(int newQuantity, String prodBarcode) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quickcartdb", "root", "root123")) {

            String updateQuery = "UPDATE inventory SET quantity = ? WHERE barcode = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, newQuantity);
                preparedStatement.setString(2, prodBarcode);
                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Quantity updated successfully.");
                } else {
                    System.out.println("Failed to update quantity.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
            java.util.logging.Logger.getLogger(Cart.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cart.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cart.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cart.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        java.awt.EventQueue.invokeLater(() -> {
            Cart c;
            try {
                c = new Cart();

                /*Socket socket = new Socket();
                try {
                    socket.setSoTimeout(300000);
                } catch (SocketException ex) {
                    Logger.getLogger(Cart.class
                            .getName()).log(Level.SEVERE, null, ex);
                }*/
                c.setVisible(true);
                Timer timer; // 3000 milliseconds (3 seconds)
                timer = new Timer(3000, e -> {
                    c.updateTable();
                });
                timer.start();

            } catch (InterruptedException ex) {
                Logger.getLogger(Cart.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AppNamePanel;
    private javax.swing.JLabel CartButton;
    private javax.swing.JLabel HomeButton;
    private javax.swing.JLabel InventoryButton;
    private javax.swing.JPanel MenuPanel;
    private javax.swing.JLabel OrdersButton;
    private javax.swing.JLabel PayementMethod;
    private javax.swing.JLabel QuickCartLabel;
    private javax.swing.JButton billButon;
    private javax.swing.JRadioButton cardPayment;
    private javax.swing.JTable cartTable;
    private javax.swing.JRadioButton cashPayment;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.ButtonGroup paymentButtonGroup;
    private javax.swing.JLabel salesHistoryButton;
    // End of variables declaration//GEN-END:variables
}
