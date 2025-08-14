package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.model.trade_details;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Application started.");

        String url = Config.getDbUrl();
        String user = Config.getDbUser();
        String pass = Config.getDbPass();

        logger.info("Connecting to database at URL: {}", url);

        List<trade_details> trades = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            logger.info("Successfully connected to the database.");

            String sql = "SELECT * FROM trade_details";
            logger.debug("Executing query: {}", sql);

            ResultSet rs = stmt.executeQuery(sql);

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

            if (trades.isEmpty()) {
                logger.warn("No rows found in trade_details table.");
            } else {
                for (trade_details trade : trades) {
                    logger.info(
                        "Trade Details - AccountNumber: {}, TradeDate: {}, Activity: {}, Description: {}, Symbol: {}, Cusip: {}, Type: {}, Quantity: {}, Price: {}, Amount: {}, FriendlyAccountName: {}, CreateDate: {}",
                        trade.getAccountNumber(),
                        trade.getTradeDate(),
                        trade.getActivity(),
                        trade.getDescription(),
                        trade.getSymbol(),
                        trade.getCusip(),
                        trade.getType(),
                        trade.getQuantity(),
                        trade.getPrice(),
                        trade.getAmount(),
                        trade.getFriendlyAccountName(),
                        trade.getCreateDate()
                    );
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred while accessing the database.", e);
        }
        calculateProfit(trades);

        logger.info("Application finished.");
    }

    public String getGreeting() {
        logger.debug("getGreeting() called.");
        return "Hello from App!";
    }

public static void calculateProfit(List<trade_details> trades) {
    if (trades == null || trades.isEmpty()) {
        logger.warn("No trades available for profit calculation.");
        return;
    }

    final BigDecimal BUY_BROKERAGE = new BigDecimal("15");
    final BigDecimal SELL_BROKERAGE = new BigDecimal("15");
    BigDecimal totalProfit = BigDecimal.ZERO;

    // Group trades by symbol and quantity (absolute value)
    Map<String, List<trade_details>> groupedTrades = trades.stream()
            .collect(Collectors.groupingBy(t -> t.getSymbol() + "_" + t.getQuantity().abs()));

    for (Map.Entry<String, List<trade_details>> entry : groupedTrades.entrySet()) {
        List<trade_details> symbolTrades = entry.getValue();

        // Find BUY and SELL trades
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

        // Calculate profit including brokerage
        BigDecimal quantity = buyTrade.getQuantity().abs();
        BigDecimal buyAmount = buyTrade.getPrice().multiply(quantity).add(BUY_BROKERAGE);
        BigDecimal sellAmount = sellTrade.getPrice().multiply(quantity).subtract(SELL_BROKERAGE);
        BigDecimal profit = sellAmount.subtract(buyAmount);

        totalProfit = totalProfit.add(profit);

        logger.info(
                "Profit for {}: BUY at {} on {}, SELL at {} on {}, Quantity: {}, Buy Brokerage: {}, Sell Brokerage: {}, Net Profit: {}",
                buyTrade.getSymbol(),
                buyTrade.getPrice(),
                buyTrade.getTradeDate(),
                sellTrade.getPrice(),
                sellTrade.getTradeDate(),
                quantity,
                BUY_BROKERAGE,
                SELL_BROKERAGE,
                profit
        );
    }

    logger.info("Total Net Profit for all trades: {}", totalProfit);
}


}
