A fully functional JavaFX desktop auction/bidding application built with clean OOP architecture and file-based persistence.


## Project Structure

```
BiddingPlatform/
└── src/main/java/com/bidding/
    ├── enums/
    │   ├── UserRole.java          # BUYER / SELLER
    │   └── AuctionStatus.java     # OPEN / CLOSED
    ├── model/
    │   ├── User.java              # Serializable user entity
    │   ├── Product.java           # Auction item entity
    │   └── Bid.java               # Bid record entity
    ├── util/
    │   ├── DataStore.java         # Generic<T> in-memory store
    │   ├── IdGenerator.java       # DataInputStream/DataOutputStream IDs
    │   └── FileManager.java       # All file I/O (Ser, CSV, RAF)
    ├── service/
    │   ├── AppContext.java        # Session/logged-in user state
    │   ├── UserService.java       # Register, login, user lookup
    │   ├── ProductService.java    # Add, list, close auctions
    │   └── BiddingService.java    # Place bids (multithreaded BidTask)
    └── ui/
        ├── MainApp.java           # JavaFX entry point & navigator
        ├── WelcomeScreen.java     # Landing screen
        ├── LoginScreen.java       # User login
        ├── RegisterScreen.java    # New user registration
        ├── DashboardScreen.java   # Role-based dashboard
        ├── AddProductScreen.java  # Seller: list new product
        ├── ProductListScreen.java # Browse & manage auctions
        ├── PlaceBidScreen.java    # Buyer: place bids (async)
        └── AuctionLogScreen.java  # Full auction history & winners
```


## Technical Features Implemented

| Requirement | Implementation |
|---|---|
| Wrapper Classes | `Integer`, `Double` used in all models |
| Enums | `UserRole`, `AuctionStatus` |
| Generics | `DataStore<T>` |
| Multithreading | `BidTask implements Runnable` in `BiddingService` |
| Synchronization | `synchronized (bidLock)` in bid placement |
| Collections | `ArrayList`, `List` throughout |
| Serialization | Users saved/loaded via `ObjectOutputStream/ObjectInputStream` |
| BufferedReader/Writer | Products and Bids stored as CSV |
| DataInputStream/DataOutputStream | ID counter binary file |
| RandomAccessFile | Fast product file seek/append |
| JavaFX | All 7 screens with VBox, HBox, GridPane |

---

## How to Run

### Prerequisites
- **Java 17+** (recommended: Java 21)
- **Maven 3.8+**
- **JavaFX 21** (handled automatically via Maven)

### Option 1: Run with Maven (Recommended)

```bash
# 1. Navigate to the project root
cd BiddingPlatform

# 2. Run directly with JavaFX Maven plugin
mvn clean javafx:run
```

### Option 2: Build and run JAR

```bash
# Build fat JAR
mvn clean package

# Run the JAR (must have JavaFX on module path)
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/BiddingPlatform-1.0.0-shaded.jar
```

### Option 3: IntelliJ IDEA

1. Open IntelliJ → **File > Open** → select `BiddingPlatform/` folder
2. IntelliJ auto-detects Maven → click **Import**
3. Right-click `MainApp.java` → **Run 'MainApp.main()'**
4. If JavaFX errors appear: **Run > Edit Configurations** → add VM options:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```

### Option 4: Eclipse

1. **File > Import > Maven > Existing Maven Projects** → select folder
2. Right-click project → **Maven > Update Project**
3. Right-click `MainApp.java` → **Run As > Java Application**

---

## 🔧 JavaFX Setup (If Not Using Maven)

1. Download JavaFX SDK from https://gluonhq.com/products/javafx/
2. Extract to a known location, e.g. `C:/javafx-sdk-21/`
3. Add VM arguments wherever you run the app:
   ```
   --module-path "C:/javafx-sdk-21/lib" --add-modules javafx.controls,javafx.fxml
   ```

---

## 💾 Data Storage

All data files are created automatically in a `data/` folder in your working directory:

| File | Format | Contains |
|---|---|---|
| `data/users.ser` | Java Serialization | All registered users |
| `data/products.csv` | Pipe-separated CSV | All product listings |
| `data/bids.csv` | Pipe-separated CSV | All bid records |
| `data/id_counters.dat` | Binary (DataOutputStream) | ID counters |

> **To reset all data**, simply delete the `data/` folder and restart the app.

---

## 🎯 How to Use

### As a Seller:
1. Register → choose "Sell Items"
2. Login → Dashboard
3. Click "Add Product" → enter name, description, starting price
4. View your listings in "My Products"
5. Click "Close Auction" when ready to end — winner is auto-determined

### As a Buyer:
1. Register → choose "Bid & Buy"
2. Login → Dashboard
3. Click "Browse Auctions" → see all open products
4. Click "Place Bid" on any open auction
5. Enter an amount higher than the current bid
6. Check "Auction Log" to track outcomes

---

## Academic Notes

This project demonstrates:
- **Clean Architecture** — separated layers: model, service, util, ui
- **OOP Principles** — encapsulation, abstraction, separation of concerns
- **Thread Safety** — synchronized bidding to prevent race conditions
- **Multiple File I/O Types** — byte streams, character streams, random access
- **JavaFX Best Practices** — Platform.runLater() for thread-safe UI updates

---

*Built as an academic Java project demonstrating enterprise-level design patterns.*
