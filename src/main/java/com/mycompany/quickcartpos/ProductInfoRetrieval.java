package com.mycompany.quickcartpos;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public final class ProductInfoRetrieval extends javax.swing.JFrame {

    private static final String jdbcUrl = "jdbc:mysql://localhost:3306/quickcartdb";
    private static final String usernameDB = "root";
    private static final String passwordDB = "root123";
    private Connection connection;
    private DefaultTableModel tableModel;

    public ProductInfoRetrieval() {
        initComponents();
        Container con = getContentPane();
        getContentPane().setBackground(Color.white);
        connectToDatabase();
        tableModel = (DefaultTableModel) productsTable.getModel();
        deleteSheetData();
        showProductsTable();
        fetchSheetData();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(jdbcUrl, usernameDB, passwordDB);
            System.out.println("Connected to the database");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }

    private void initTable() {
        String[] columnNames = {"ID", "Name", "Barcode", "Quantity", "Price"};
        tableModel = new DefaultTableModel(null, columnNames);
        productsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane);
    }

    private void showProductsTable() {
        tableModel.setRowCount(0);
        String sql = "SELECT id, name, barcode, quantity, price FROM inventory";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String barcode = resultSet.getString("barcode");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");
                tableModel.addRow(new Object[]{id, name, barcode, quantity, price});
                //fetchSheetData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    int endRow;

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

                if (lastRowValues != null && !lastRowValues.isEmpty()) {
                    int lastRow = lastRowValues.size();
                    System.out.println("End row: " + endRow + " Last row: " + lastRow);
                    if (endRow > lastRow) {
                        endRow = lastRow;
                        return "0";
                    } else {
                        String range = "B2";
                        ValueRange response = sheetsService.spreadsheets().values()
                                .get(spreadsheetId, range)
                                .execute();

                        List<List<Object>> values = response.getValues();

                        if (values != null && !values.isEmpty() && values.get(0) != null && !values.get(0).isEmpty()) {
                            endRow = lastRow;
                            searchBarcodeField.setText(values.get(0).get(0).toString());
                            System.out.println("code: "+searchBarcodeField.getText());
                            return values.get(0).get(0).toString();
                        }
                    }
                }
               // fetchSheetData();
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
    public void displayBarcode(){
        String scannedBarcode = fetchSheetData();
        System.out.println("Bcode: "+scannedBarcode);
        /*if (!scannedBarcode.equals("1"))*/{
        searchBarcodeField.setText("123");}
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
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AppNamePanel = new javax.swing.JPanel();
        QuickCartLabel = new javax.swing.JLabel();
        MenuPanel = new javax.swing.JPanel();
        HomeButton = new javax.swing.JLabel();
        InventoryButton = new javax.swing.JLabel();
        salesHistoryButton = new javax.swing.JLabel();
        OrdersButton = new javax.swing.JLabel();
        CartButton = new javax.swing.JLabel();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        searchBarcodeLabel = new javax.swing.JLabel();
        searchBarcodeField = new javax.swing.JTextField();
        searchProductNameLabel = new javax.swing.JLabel();
        searchProductNameField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        productsTable = new javax.swing.JTable();
        SearchPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        codeLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        quantityLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        priceLabel = new javax.swing.JLabel();
        searchBarcode = new javax.swing.JButton();
        seachProductName = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        updateButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        AppNamePanel.setBackground(new java.awt.Color(174, 102, 183));

        QuickCartLabel.setFont(new java.awt.Font("Stencil", 0, 48)); // NOI18N
        QuickCartLabel.setForeground(new java.awt.Color(255, 255, 255));
        QuickCartLabel.setText("QuickCart");

        javax.swing.GroupLayout AppNamePanelLayout = new javax.swing.GroupLayout(AppNamePanel);
        AppNamePanel.setLayout(AppNamePanelLayout);
        AppNamePanelLayout.setHorizontalGroup(
            AppNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppNamePanelLayout.createSequentialGroup()
                .addGap(376, 376, 376)
                .addComponent(QuickCartLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        AppNamePanelLayout.setVerticalGroup(
            AppNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AppNamePanelLayout.createSequentialGroup()
                .addContainerGap(54, Short.MAX_VALUE)
                .addComponent(QuickCartLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
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

        editButton.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        editButton.setText("Edit");
        editButton.setFocusCycleRoot(true);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        deleteButton.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        addButton.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        searchBarcodeLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        searchBarcodeLabel.setText("Search Barcode");

        searchBarcodeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBarcodeFieldActionPerformed(evt);
            }
        });

        searchProductNameLabel.setFont(new java.awt.Font("Segoe UI Historic", 0, 16)); // NOI18N
        searchProductNameLabel.setText("Search Product Name");

        searchProductNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchProductNameFieldActionPerformed(evt);
            }
        });

        productsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "ID", "Name", "Barcode", "Quantity", "Price"
            }
        ));
        productsTable.setSelectionBackground(new java.awt.Color(204, 204, 204));
        productsTable.setSelectionForeground(new java.awt.Color(51, 0, 51));
        productsTable.setShowGrid(false);
        jScrollPane1.setViewportView(productsTable);

        SearchPanel.setBackground(new java.awt.Color(239, 225, 239));
        SearchPanel.setBackground(new java.awt.Color(239, 225, 239));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Code");

        codeLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        codeLabel.setText(" ");
        codeLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Name");

        nameLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        nameLabel.setText(" ");
        nameLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Quantity");

        quantityLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        quantityLabel.setText(" ");
        quantityLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Price");

        priceLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        priceLabel.setText(" ");
        priceLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout SearchPanelLayout = new javax.swing.GroupLayout(SearchPanel);
        SearchPanel.setLayout(SearchPanelLayout);
        SearchPanelLayout.setHorizontalGroup(
            SearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(codeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(quantityLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(priceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        SearchPanelLayout.setVerticalGroup(
            SearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(SearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(codeLabel)
                    .addComponent(jLabel3)
                    .addComponent(nameLabel)
                    .addComponent(jLabel5)
                    .addComponent(quantityLabel)
                    .addComponent(jLabel7)
                    .addComponent(priceLabel))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        searchBarcode.setText("->");
        searchBarcode.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        searchBarcode.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        searchBarcode.setIconTextGap(0);
        searchBarcode.setMargin(new java.awt.Insets(2, 5, 3, 5));
        searchBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBarcodeActionPerformed(evt);
            }
        });

        seachProductName.setText("->");
        seachProductName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        seachProductName.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        seachProductName.setIconTextGap(0);
        seachProductName.setMargin(new java.awt.Insets(2, 5, 3, 5));
        seachProductName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seachProductNameActionPerformed(evt);
            }
        });

        refreshButton.setText("O");
        refreshButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        refreshButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        refreshButton.setIconTextGap(0);
        refreshButton.setMargin(new java.awt.Insets(2, 5, 3, 5));
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        updateButton1.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        updateButton1.setText("Update");
        updateButton1.setFocusCycleRoot(true);
        updateButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(MenuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(20, 20, 20))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchBarcodeLabel)
                        .addGap(18, 18, 18)
                        .addComponent(searchBarcodeField, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchBarcode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                        .addComponent(searchProductNameLabel)
                        .addGap(18, 18, 18)
                        .addComponent(searchProductNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(seachProductName)
                        .addGap(38, 38, 38))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(refreshButton)
                        .addGap(18, 18, 18)
                        .addComponent(addButton)
                        .addGap(18, 18, 18)
                        .addComponent(deleteButton)
                        .addGap(18, 18, 18)
                        .addComponent(editButton)
                        .addGap(18, 18, 18)
                        .addComponent(updateButton1)
                        .addGap(27, 27, 27))))
            .addComponent(AppNamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(AppNamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MenuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchBarcodeLabel)
                            .addComponent(searchBarcodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchProductNameLabel)
                            .addComponent(searchProductNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchBarcode)
                            .addComponent(seachProductName))
                        .addGap(10, 10, 10)
                        .addComponent(SearchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addButton)
                            .addComponent(deleteButton)
                            .addComponent(editButton)
                            .addComponent(refreshButton)
                            .addComponent(updateButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void HomeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HomeButtonMouseClicked
        Home h = new Home();
        h.setVisible(true);
        setVisible(false);
    }//GEN-LAST:event_HomeButtonMouseClicked

    private void InventoryButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InventoryButtonMouseClicked

    }//GEN-LAST:event_InventoryButtonMouseClicked

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        if (productsTable.getSelectedRow() >= 0) {
            int selectedIndex = productsTable.getSelectedRow();
            int id = (Integer) tableModel.getValueAt(selectedIndex, 0);
            String newName = JOptionPane.showInputDialog(null, "Enter New Name: ");
            String newBarcode = JOptionPane.showInputDialog(null, "Enter New Barcode: ");
            int newQuantity = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter New Quantity: "));
            BigDecimal newPrice = new BigDecimal(JOptionPane.showInputDialog(null, "Enter New Price: "));

            updateItemInDatabase(id, newName, newBarcode, newQuantity, newPrice);
            showProductsTable();
            tableModel.setValueAt(newName, selectedIndex, 1);
            tableModel.setValueAt(newBarcode, selectedIndex, 2);
            tableModel.setValueAt(newQuantity, selectedIndex, 3);
            tableModel.setValueAt(newPrice, selectedIndex, 4);

            JOptionPane.showMessageDialog(null, "Item Updated Successfully!");
        }
    }//GEN-LAST:event_editButtonActionPerformed
    private void updateItemInDatabase(int id, String name, String barcode, int quantity, BigDecimal price) {
        try {
            String query = "UPDATE Inventory SET name = ?, barcode = ?, quantity = ?, price = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, barcode);
                pstmt.setInt(3, quantity);
                pstmt.setBigDecimal(4, price);
                pstmt.setInt(5, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating item in the database: " + e.getMessage());
        }
    }
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int deletedRow = productsTable.getSelectedRow();
        System.out.println("drow: " + deletedRow);
        if (productsTable.getSelectedRow() >= 0) {
            int id = (Integer) tableModel.getValueAt(deletedRow, 0);
            tableModel.removeRow(deletedRow);
            deleteItemFromDatabase(id);
            //showProductsTable();
        } else {
            JOptionPane.showMessageDialog(null, "Select a row to delete product", "error", ERROR_MESSAGE);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed
    private void deleteItemFromDatabase(int id) {
        try {
            String query = "DELETE FROM Inventory WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting item from the database: " + e.getMessage());
        }
    }
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        JTextField nameField = new JTextField();
        JTextField barcodeField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Barcode:"));
        inputPanel.add(barcodeField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);

        int result = JOptionPane.showConfirmDialog(null, inputPanel, "Enter Item Information", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String barcode = barcodeField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            BigDecimal price = new BigDecimal(priceField.getText());

            if (name.isEmpty() || barcode.isEmpty() || quantity < 0 || price.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(null, "Please enter valid information.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            addRowToDatabase(name, barcode, quantity, price);
            showProductsTable();
            JOptionPane.showMessageDialog(null, "Item Added Successfully");
        }
    }//GEN-LAST:event_addButtonActionPerformed
    private void addRowToDatabase(String name, String barcode, int quantity, BigDecimal price) {
        try {
            String query = "INSERT INTO Inventory (name, barcode, quantity, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setString(2, barcode);
                pstmt.setInt(3, quantity);
                pstmt.setBigDecimal(4, price);
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Generated ID: " + generatedId);
                    } else {
                        System.err.println("Failed to retrieve generated ID.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding row to the database: " + e.getMessage());
        }
    }
    private void searchBarcodeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBarcodeFieldActionPerformed

    }//GEN-LAST:event_searchBarcodeFieldActionPerformed

    private void searchProductNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchProductNameFieldActionPerformed

    }//GEN-LAST:event_searchProductNameFieldActionPerformed

    private void searchBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBarcodeActionPerformed
        String scannedBarcode = searchBarcodeField.getText();
        String query = "SELECT * FROM inventory WHERE barcode = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, scannedBarcode);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    codeLabel.setText(resultSet.getString("name"));
                    nameLabel.setText(resultSet.getString("barcode"));
                    quantityLabel.setText(resultSet.getString("quantity"));
                    priceLabel.setText(String.valueOf(resultSet.getDouble("price")));
                    deleteSheetData();
                } else {
                    JOptionPane.showMessageDialog(null, "Product not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                    codeLabel.setText("");
                    nameLabel.setText("");
                    quantityLabel.setText("");
                    priceLabel.setText("");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductInfoRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_searchBarcodeActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        showProductsTable();
        codeLabel.setText(" ");
        nameLabel.setText(" ");
        quantityLabel.setText(" ");
        priceLabel.setText(" ");
        searchBarcodeField.setText("");
        searchProductNameField.setText("");
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void updateButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButton1ActionPerformed
        if (productsTable.getSelectedRow() >= 0) {
            int selectedIndex = productsTable.getSelectedRow();
            try {
                String itemsSoldString = JOptionPane.showInputDialog(null, "Enter number of Items sold");
                String itemsAddedString = JOptionPane.showInputDialog(null, "Enter number of Items you want to add");
                int itemsSold = Integer.parseInt(itemsSoldString);
                int itemsAdded = Integer.parseInt(itemsAddedString);
                int currentQuantity = (int) tableModel.getValueAt(selectedIndex, 3);
                int updatedQuantity = currentQuantity + itemsAdded - itemsSold;
                tableModel.setValueAt(updatedQuantity, selectedIndex, 3);

                String itemId = (String) tableModel.getValueAt(selectedIndex, 0);
                updateQuantityInDatabase(itemId, updatedQuantity);

                JOptionPane.showMessageDialog(null, "Item Quantity Updated Successfully!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid integer for Items sold and Items added.");
            }
        }
    }//GEN-LAST:event_updateButton1ActionPerformed

    private void seachProductNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seachProductNameActionPerformed
        tableModel.setRowCount(0);
        String query = "SELECT * FROM inventory WHERE name LIKE ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + searchProductNameField.getText() + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Object row[] = new Object[6];
                    row[0] = resultSet.getInt("id");
                    row[1] = resultSet.getString("name");
                    row[2] = resultSet.getString("barcode");
                    row[3] = resultSet.getInt("quantity");
                    row[4] = resultSet.getDouble("price");
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductInfoRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_seachProductNameActionPerformed
    private void updateQuantityInDatabase(String id, int quantity) {
        try {
            String query = "UPDATE Inventory SET quantity = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, quantity);
                pstmt.setInt(2, Integer.parseInt(id));
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating quantity in the database: " + e.getMessage());
        }
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
            java.util.logging.Logger.getLogger(ProductInfoRetrieval.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProductInfoRetrieval.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProductInfoRetrieval.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProductInfoRetrieval.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
       // ProductInfoRetrieval pir = new ProductInfoRetrieval();
        java.awt.EventQueue.invokeLater(() -> {
            new ProductInfoRetrieval().setVisible(true);
        /*Timer timer = new Timer(10000, e -> {
                pir.displayBarcode();
            });
        timer.start();*/
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AppNamePanel;
    private javax.swing.JLabel CartButton;
    private javax.swing.JLabel HomeButton;
    private javax.swing.JLabel InventoryButton;
    private javax.swing.JPanel MenuPanel;
    private javax.swing.JLabel OrdersButton;
    private javax.swing.JLabel QuickCartLabel;
    private javax.swing.JPanel SearchPanel;
    private javax.swing.JButton addButton;
    private javax.swing.JLabel codeLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JTable productsTable;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JLabel salesHistoryButton;
    private javax.swing.JButton seachProductName;
    private javax.swing.JButton searchBarcode;
    private javax.swing.JTextField searchBarcodeField;
    private javax.swing.JLabel searchBarcodeLabel;
    private javax.swing.JTextField searchProductNameField;
    private javax.swing.JLabel searchProductNameLabel;
    private javax.swing.JButton updateButton1;
    // End of variables declaration//GEN-END:variables
}
