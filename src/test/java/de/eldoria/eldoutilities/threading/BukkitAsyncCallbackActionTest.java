package de.eldoria.eldoutilities.threading;

import de.eldoria.eldoutilities.core.EldoUtilities;
import org.bukkit.entity.Player;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class BukkitAsyncCallbackActionTest {
    DataSource source;

    public BukkitAsyncAction<BigDecimal> getBalance(UUID uuid) {
        return BukkitAsyncAction.supplyAsync(EldoUtilities.getInstanceOwner(), () -> {
            try (Connection conn = source.getConnection(); PreparedStatement ps =
                    conn.prepareStatement("SELECT coin FROM coins WHERE UUID = ?")) {
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getBigDecimal("coin");
                }
            } catch (SQLException e) {
                EldoUtilities.logger().log(Level.WARNING, "Something went wrong.", e);
            }
            BigDecimal.valueOf(0.0);
            return BigDecimal.ZERO;
        });
    }

    public void sendBalance(Player player) {
        getBalance(player.getUniqueId()).queue(balance -> player.sendMessage("Balance: " + balance.doubleValue()),
                e -> {
            EldoUtilities.logger().log(Level.WARNING, "Something went wrong.", e);
            player.sendMessage("Dat ging nich");
        });
    }
}
