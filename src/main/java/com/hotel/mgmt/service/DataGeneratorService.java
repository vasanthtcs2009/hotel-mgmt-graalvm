package com.hotel.mgmt.service;

import com.hotel.mgmt.entity.MenuCategory;
import com.hotel.mgmt.entity.RoomStatus;
import com.hotel.mgmt.entity.RoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
public class DataGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(DataGeneratorService.class);

    private final JdbcTemplate jdbcTemplate;
    private final CacheManager cacheManager;
    private final Random random = new Random();

    public DataGeneratorService(JdbcTemplate jdbcTemplate, CacheManager cacheManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public void generateBaseData() {
        log.info("Generating base static catalog data...");

        // 1. Clear database tables
        jdbcTemplate.execute("TRUNCATE TABLE order_items, orders, reservations, customers, rooms, menu_items, inventory_items, staff RESTART IDENTITY CASCADE");

        // 2. Generate 120 Rooms
        log.info("Inserting rooms...");
        String roomSql = "INSERT INTO rooms (room_number, room_type, price_per_night, status, bed_count, amenities) VALUES (?, ?, ?, ?, ?, ?)";
        List<Object[]> roomArgs = new ArrayList<>();
        for (int i = 1; i <= 120; i++) {
            String roomNum = String.format("R%03d", i);
            RoomType type = RoomType.values()[i % RoomType.values().length];
            BigDecimal price = BigDecimal.valueOf(100.0 + (i % 5) * 50.0);
            RoomStatus status = i % 15 == 0 ? RoomStatus.MAINTENANCE : RoomStatus.AVAILABLE;
            int beds = (i % 3) + 1;
            String amenities = "WiFi, TV, AC" + (beds > 2 ? ", Minibar, Balcony" : "");
            roomArgs.add(new Object[]{roomNum, type.name(), price, status.name(), beds, amenities});
        }
        jdbcTemplate.batchUpdate(roomSql, roomArgs);

        // 3. Generate 150 Menu Items
        log.info("Inserting menu items...");
        String menuSql = "INSERT INTO menu_items (name, description, price, category, available, prep_time_minutes) VALUES (?, ?, ?, ?, ?, ?)";
        List<Object[]> menuArgs = new ArrayList<>();
        String[] prefixes = {"Spicy", "Gourmet", "Chef's Special", "Classic", "Sweet", "Organic", "Crispy", "Grilled"};
        String[] dishes = {"Chicken", "Beef Steak", "Salmon", "Pasta", "Salad", "Burger", "Pizza", "Soup", "Cake", "Smoothie", "Wine", "Cocktail"};
        for (int i = 1; i <= 150; i++) {
            String name = prefixes[i % prefixes.length] + " " + dishes[i % dishes.length] + " " + i;
            BigDecimal price = BigDecimal.valueOf(5.99 + (i % 10) * 4.5);
            MenuCategory category = MenuCategory.values()[i % MenuCategory.values().length];
            int prepTime = 5 + (i % 6) * 5;
            menuArgs.add(new Object[]{name, "Delicious " + name + " prepared fresh.", price, category.name(), true, prepTime});
        }
        jdbcTemplate.batchUpdate(menuSql, menuArgs);

        // 4. Generate 100 Inventory Items
        log.info("Inserting inventory items...");
        String inventorySql = "INSERT INTO inventory_items (item_name, category, stock_quantity, unit, reorder_level) VALUES (?, ?, ?, ?, ?)";
        List<Object[]> invArgs = new ArrayList<>();
        String[] ingredients = {"Steak Ingredients", "Chicken Bulk", "Salmon Fillet", "Flour Bag", "Cheese Block", "Tomato Sauce", "Wine Inventory", "Veggies Mix", "Cooking Oil", "Toiletries Roll"};
        for (int i = 1; i <= 100; i++) {
            String name = ingredients[i % ingredients.length] + " " + i;
            String category = i % 2 == 0 ? "FOOD" : "BEVERAGE";
            BigDecimal stock = BigDecimal.valueOf(100.0 + random.nextInt(400));
            BigDecimal reorder = BigDecimal.valueOf(50.0);
            invArgs.add(new Object[]{name, category, stock, "KG", reorder});
        }
        // Add generic bulk items for category fallback deductions
        for (MenuCategory cat : MenuCategory.values()) {
            invArgs.add(new Object[]{cat.name() + " Ingredients Bulk", "FOOD", BigDecimal.valueOf(10000.0), "KG", BigDecimal.valueOf(500.0)});
        }
        jdbcTemplate.batchUpdate(inventorySql, invArgs);

        // 5. Generate 50 Staff members
        log.info("Inserting staff...");
        String staffSql = "INSERT INTO staff (first_name, last_name, role, salary, shift) VALUES (?, ?, ?, ?, ?)";
        List<Object[]> staffArgs = new ArrayList<>();
        String[] fNames = {"James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth"};
        String[] lNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson"};
        String[] roles = {"MANAGER", "CHEF", "WAITER", "RECEPTIONIST", "HOUSEKEEPING"};
        String[] shifts = {"MORNING", "AFTERNOON", "NIGHT"};
        for (int i = 1; i <= 50; i++) {
            staffArgs.add(new Object[]{
                    fNames[i % fNames.length],
                    lNames[i % lNames.length],
                    roles[i % roles.length],
                    BigDecimal.valueOf(2500.0 + (i % 5) * 500.0),
                    shifts[i % shifts.length]
            });
        }
        jdbcTemplate.batchUpdate(staffSql, staffArgs);

        // Evict caches to ensure fresh listings
        log.info("Evicting caches after base data reload...");
        clearCaches();
    }

    public void clearCaches() {
        Objects.requireNonNull(cacheManager.getCache("menuItems")).clear();
        Objects.requireNonNull(cacheManager.getCache("rooms")).clear();
        Objects.requireNonNull(cacheManager.getCache("roomAvailability")).clear();
    }

    @Transactional
    public void generateMillionsOfRecords(int customersCount, int reservationsCount, int ordersCount, int orderItemsCount) {
        log.info("Starting high-performance data generation for millions of records...");
        
        // Ensure base data is present
        List<Long> roomIds = jdbcTemplate.queryForList("SELECT id FROM rooms", Long.class);
        List<Long> menuItemIds = jdbcTemplate.queryForList("SELECT id FROM menu_items", Long.class);
        
        if (roomIds.isEmpty() || menuItemIds.isEmpty()) {
            generateBaseData();
            roomIds = jdbcTemplate.queryForList("SELECT id FROM rooms", Long.class);
            menuItemIds = jdbcTemplate.queryForList("SELECT id FROM menu_items", Long.class);
        }

        // 1. Generate Customers (e.g. 50,000)
        log.info("Generating {} customers...", customersCount);
        String customerSql = "INSERT INTO customers (first_name, last_name, email, phone, passport_number) VALUES (?, ?, ?, ?, ?)";
        int batchSize = 10000;
        List<Object[]> batchArgs = new ArrayList<>();
        
        for (int i = 1; i <= customersCount; i++) {
            String fName = "GuestF" + i;
            String lName = "GuestL" + i;
            String email = "guest" + i + "_" + random.nextInt(100000) + "@example.com";
            String phone = "+1555" + String.format("%07d", random.nextInt(10000000));
            String passport = "PAS" + String.format("%08d", random.nextInt(100000000));
            batchArgs.add(new Object[]{fName, lName, email, phone, passport});

            if (batchArgs.size() >= batchSize) {
                jdbcTemplate.batchUpdate(customerSql, batchArgs);
                batchArgs.clear();
            }
        }
        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(customerSql, batchArgs);
            batchArgs.clear();
        }

        List<Long> customerIds = jdbcTemplate.queryForList("SELECT id FROM customers", Long.class);
        log.info("Loaded {} customer IDs.", customerIds.size());

        // 2. Generate Reservations (e.g. 100,000)
        log.info("Generating {} reservations...", reservationsCount);
        String resSql = "INSERT INTO reservations (room_id, customer_id, check_in_date, check_out_date, total_amount, status) VALUES (?, ?, ?, ?, ?, ?)";
        LocalDate today = LocalDate.now();
        
        for (int i = 1; i <= reservationsCount; i++) {
            Long roomId = roomIds.get(random.nextInt(roomIds.size()));
            Long customerId = customerIds.get(random.nextInt(customerIds.size()));
            
            int offset = random.nextInt(365) - 180; // +/- 180 days from today
            LocalDate checkIn = today.plusDays(offset);
            LocalDate checkOut = checkIn.plusDays(random.nextInt(7) + 1);
            
            BigDecimal amount = BigDecimal.valueOf(150.0 + random.nextInt(500));
            String status = i % 10 == 0 ? "CANCELLED" : (checkOut.isBefore(today) ? "COMPLETED" : "CONFIRMED");

            batchArgs.add(new Object[]{roomId, customerId, checkIn, checkOut, amount, status});

            if (batchArgs.size() >= batchSize) {
                jdbcTemplate.batchUpdate(resSql, batchArgs);
                batchArgs.clear();
            }
        }
        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(resSql, batchArgs);
            batchArgs.clear();
        }

        // 3. Generate Orders (e.g. 200,000)
        log.info("Generating {} orders...", ordersCount);
        String orderSql = "INSERT INTO orders (room_id, table_number, status, total_amount, created_at) VALUES (?, ?, ?, ?, ?)";
        
        for (int i = 1; i <= ordersCount; i++) {
            Long roomId = random.nextBoolean() ? roomIds.get(random.nextInt(roomIds.size())) : null;
            Integer tableNum = roomId == null ? random.nextInt(40) + 1 : null;
            String status = "COMPLETED";
            BigDecimal totalAmount = BigDecimal.ZERO;
            LocalDateTime createdAt = LocalDateTime.now().minusDays(random.nextInt(180)).minusMinutes(random.nextInt(1440));

            batchArgs.add(new Object[]{roomId, tableNum, status, totalAmount, createdAt});

            if (batchArgs.size() >= batchSize) {
                jdbcTemplate.batchUpdate(orderSql, batchArgs);
                batchArgs.clear();
            }
        }
        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(orderSql, batchArgs);
            batchArgs.clear();
        }

        List<Long> orderIds = jdbcTemplate.queryForList("SELECT id FROM orders", Long.class);
        log.info("Loaded {} order IDs.", orderIds.size());

        // 4. Generate Order Items (e.g. 800,000)
        log.info("Generating {} order items...", orderItemsCount);
        String orderItemSql = "INSERT INTO order_items (order_id, menu_item_id, quantity, unit_price, comments) VALUES (?, ?, ?, ?, ?)";
        
        for (int i = 1; i <= orderItemsCount; i++) {
            Long orderId = orderIds.get(random.nextInt(orderIds.size()));
            Long menuItemId = menuItemIds.get(random.nextInt(menuItemIds.size()));
            int quantity = random.nextInt(3) + 1;
            BigDecimal price = BigDecimal.valueOf(9.99 + random.nextInt(40));
            String comment = random.nextInt(5) == 0 ? "No onions" : null;

            batchArgs.add(new Object[]{orderId, menuItemId, quantity, price, comment});

            if (batchArgs.size() >= batchSize) {
                jdbcTemplate.batchUpdate(orderItemSql, batchArgs);
                batchArgs.clear();
            }
        }
        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(orderItemSql, batchArgs);
            batchArgs.clear();
        }

        // Calculate and update Order sums
        log.info("Re-calculating order total amounts...");
        jdbcTemplate.update("""
            UPDATE orders o
            SET total_amount = COALESCE(
                (SELECT SUM(quantity * unit_price) FROM order_items WHERE order_id = o.id), 
                0
            )
        """);

        log.info("Data generation completed successfully!");
        clearCaches();
    }
}
