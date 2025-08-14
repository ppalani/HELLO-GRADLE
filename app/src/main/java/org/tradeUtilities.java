package org;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.model.trade_details;

import de.vandermeer.asciitable.AsciiTable;

public class tradeUtilities {

    private static final Logger logger = LogManager.getLogger(tradeUtilities.class);

    private static final BigDecimal BUY_BROKERAGE = new BigDecimal("15");
    private static final BigDecimal SELL_BROKERAGE = new BigDecimal("15");

    public static void loadAndCalculateProfit(String url, String user, String pass) {
        List<trade_details> trades = loadTradesFromDB(url, user, pass);
        if (trades != null && !trades.isEmpty()) {
            calculateProfit(trades);
        } else {
            logger.warn("No trades loaded from DB.");
        }
    }

    private static List<trade_details> loadTradesFromDB(String url, String user, String pass) {
        List<trade_details> trades = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            logger.info("Successfully connected to the database.");

            ResultSet rs = stmt.executeQuery("SELECT * FROM trade_details");

            while (rs.next()) {
                trade_details trade = new trade_details();
                trade.setAccountNumber(rs.getString("account_number"));
                trade.setTradeDate(rs.getDate("trade_date").toLocalDate());
                trade.setActivity(rs.getString("activity"));
                trade.setDescription(rs.getString("description"));
                trade.setSymbol(rs.getString("symbol"));
                trade.setCusip(rs.getString("cusip"));
                trade.setType(rs.getString("type"));
                trade.setQuantity(rs.getBigDecimal("quantity"));
                trade.setPrice(rs.getBigDecimal("price"));
                trade.setAmount(rs.getBigDecimal("amount"));
                trade.setFriendlyAccountName(rs.getString("friendly_account_name"));
                trade.setCreateDate(rs.getDate("create_date").toLocalDate());
                trades.add(trade);
            }

            logger.info("Loaded {} trades from DB.", trades.size());

        } catch (Exception e) {
            logger.error("An error occurred while loading trades from the database.", e);
        }
        return trades;
    }

    private static void calculateProfit(List<trade_details> trades) {
        if (trades == null || trades.isEmpty()) {
            logger.warn("No trades available for profit calculation.");
            return;
        }

        BigDecimal totalProfit = BigDecimal.ZERO;

        Map<String, List<trade_details>> groupedTrades = trades.stream()
                .collect(Collectors.groupingBy(t -> t.getSymbol() + "_" + t.getQuantity().abs()));

        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Symbol", "Buy Date", "Sell Date", "Qty", "Buy Price", "Sell Price", "Buy Brokerage", "Sell Brokerage", "Net Profit");
        at.addRule();

        for (Map.Entry<String, List<trade_details>> entry : groupedTrades.entrySet()) {
            List<trade_details> symbolTrades = entry.getValue();

            trade_details buyTrade = symbolTrades.stream()
                    .filter(t -> "BOUGHT".equalsIgnoreCase(t.getActivity()))
                    .min((t1, t2) -> t1.getTradeDate().compareTo(t2.getTradeDate()))
                    .orElse(null);

            trade_details sellTrade = symbolTrades.stream()
                    .filter(t -> "SOLD".equalsIgnoreCase(t.getActivity()))
                    .max((t1, t2) -> t1.getTradeDate().compareTo(t2.getTradeDate()))
                    .orElse(null);

            if (buyTrade == null) {
                logger.warn("No BUY trade found for {}", entry.getKey());
                continue;
            }

            if (sellTrade == null) {
                logger.info("No SELL trade found for {}. Assuming position still open.", entry.getKey());
                continue;
            }

            BigDecimal quantity = buyTrade.getQuantity().abs();
            BigDecimal buyAmount = buyTrade.getPrice().multiply(quantity).add(BUY_BROKERAGE);
            BigDecimal sellAmount = sellTrade.getPrice().multiply(quantity).subtract(SELL_BROKERAGE);
            BigDecimal profit = sellAmount.subtract(buyAmount);

            totalProfit = totalProfit.add(profit);

            at.addRow(
                    buyTrade.getSymbol(),
                    buyTrade.getTradeDate(),
                    sellTrade.getTradeDate(),
                    quantity,
                    buyTrade.getPrice(),
                    sellTrade.getPrice(),
                    BUY_BROKERAGE,
                    SELL_BROKERAGE,
                    profit
            );
            at.addRule();
        }

        System.out.println(at.render());
        logger.info("Total Net Profit for all trades: {}", totalProfit);
    }
}
