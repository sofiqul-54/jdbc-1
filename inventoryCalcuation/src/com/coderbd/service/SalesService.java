package com.coderbd.service;

import com.coderbd.connection.MySqlDbConnection;
import com.coderbd.domain.Sales;
import com.coderbd.domain.Summary;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SalesService {

    static Connection conn = MySqlDbConnection.getConnection();

    public static void createTable() {
        String sql = "create table sales(id int auto_increment primary key, productName varchar(30) not null, productCode varchar(30) not null,qty int(11) not null,unitprice double not null,  totalPrice double not null, salesdate Date not null, product_id int(11) not null, foreign key (product_id) references purchase(id))";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();
            System.out.println("Table Created!");
        } catch (SQLException ex) {
            Logger.getLogger(SalesService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void insert(Sales sales) {
        String sql = "insert into sales(productName, productCode,qty, unitprice, totalPrice, salesdate, product_id) values(?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, sales.getProductName());
            ps.setString(2, sales.getProductCode());
            ps.setInt(3, sales.getQty());
            ps.setDouble(4, sales.getUnitprice());
            ps.setDouble(5, sales.getTotalPrice());
            ps.setDate(6, new java.sql.Date(sales.getSalesdate().getTime()));
            ps.setInt(7, sales.getPurchase().getId());
            ps.executeUpdate();
            System.out.println("Data Inserted!");
        } catch (SQLException ex) {
            Logger.getLogger(SalesService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void insertForSales(Sales sales) {
        if (sales != null) {
            Summary summary = SummaryService.getSummaryByProductCode(sales.getProductCode());
            if (summary.getAvailableQty() >= sales.getQty()) {
                insert(sales);
                int soldQrt = summary.getSoldQty() + sales.getQty();
                int avilQty = summary.getAvailableQty() - sales.getQty();
                
                summary.setSoldQty(soldQrt);
                summary.setAvailableQty(avilQty);
                summary.setLastUpdate(new Date());

                SummaryService.update(summary);
            } else {
                System.out.println("You do not have sufficient Product");
            }
        }
    }

}