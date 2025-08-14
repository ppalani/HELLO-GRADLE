package org.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class trade_details {
    private String accountNumber;
    private LocalDate tradeDate;
    private String activity;
    private String description;
    private String symbol;
    private String cusip;
    private String type;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String friendlyAccountName;
    private LocalDate createDate;

    
    public trade_details() {
    }
    public trade_details(String accountNumber, LocalDate tradeDate, String activity, String description, String symbol,
            String cusip, String type, BigDecimal quantity, BigDecimal price, BigDecimal amount,
            String friendlyAccountName, LocalDate createDate) {
        this.accountNumber = accountNumber;
        this.tradeDate = tradeDate;
        this.activity = activity;
        this.description = description;
        this.symbol = symbol;
        this.cusip = cusip;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
        this.friendlyAccountName = friendlyAccountName;
        this.createDate = createDate;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public LocalDate getTradeDate() {
        return tradeDate;
    }
    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }
    public String getActivity() {
        return activity;
    }
    public void setActivity(String activity) {
        this.activity = activity;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public String getCusip() {
        return cusip;
    }
    public void setCusip(String cusip) {
        this.cusip = cusip;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getFriendlyAccountName() {
        return friendlyAccountName;
    }
    public void setFriendlyAccountName(String friendlyAccountName) {
        this.friendlyAccountName = friendlyAccountName;
    }
    public LocalDate getCreateDate() {
        return createDate;
    }
    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }
}
