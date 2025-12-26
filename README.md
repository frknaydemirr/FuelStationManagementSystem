<h1>Database Diagrams</h1>
<img width="1263" height="877" alt="image" src="https://github.com/user-attachments/assets/cb757438-b06c-4500-af5e-8b085e833a9c" />

<h1>Er Diagrams</h1>
<img width="1440" height="934" alt="image" src="https://github.com/user-attachments/assets/85f80db5-f5e2-40b6-9c1c-acd5b7a92a1e" />



<h1>Mapping Diagrams</h1>
<img width="15278" height="9131" alt="Adsız-2025-11-17-0145(Mapping)" src="https://github.com/user-attachments/assets/b8cc3fe7-cc29-453b-b026-9826a1e0d6cc" />

<h1>UI Design (Pages)</h1>

<h2>Login Page</h2>
<img width="408" height="436" alt="image" src="https://github.com/user-attachments/assets/f6169deb-c66e-4a19-87f1-dc491a77e264" />

<h2>Admin Page</h2>
<img width="829" height="939" alt="image" src="https://github.com/user-attachments/assets/337294d6-81de-4d1e-a35a-5a1c0467810c" />

<h2>FuelSales Page</h2>
<img width="984" height="589" alt="image" src="https://github.com/user-attachments/assets/7e496abc-3edc-4a23-acb0-e1313f1fc0ed" />

<h2>User Account Page</h2>
<img width="1079" height="582" alt="image" src="https://github.com/user-attachments/assets/71b7d02c-f23b-4464-b998-feb5495d7e43" />

<h2>FuelTypes Page</h2>
<img width="1081" height="591" alt="image" src="https://github.com/user-attachments/assets/3ae6ae98-83ac-487a-80f3-853e747a2fcd" />

<h2>Employee's Phone Page</h2>
<img width="1085" height="586" alt="image" src="https://github.com/user-attachments/assets/7464c419-ebfe-4ab5-b55f-68083be26099" />

<h2>Employee Page</h2>
<img width="1335" height="636" alt="image" src="https://github.com/user-attachments/assets/fe1d7648-708e-4135-8bf8-005d4a419b52" />

<h2>FuelPump Page</h2>
<img width="1079" height="590" alt="image" src="https://github.com/user-attachments/assets/e5d990e9-cad4-450d-8076-78bf297f5225" />

<h2>StoreSales Page</h2>
<img width="1079" height="587" alt="image" src="https://github.com/user-attachments/assets/4acba91b-049d-4f20-8079-413ff3249897" />













<h2>2.Database  Schema (Database Scripts)</h2>
<p>Employees</p>
<img width="964" height="330" alt="image" src="https://github.com/user-attachments/assets/77c94dfa-436e-4884-8b1c-d88d9932827f" />


<p>Employeephones</p>
<img width="962" height="213" alt="image" src="https://github.com/user-attachments/assets/8ea06850-8ba7-4241-a30b-2fc24c189b2b" />

<p>FuelPumps</p>
<img width="964" height="165" alt="image" src="https://github.com/user-attachments/assets/de91cdf6-1ee9-41c3-8ebf-14513d1f1686" />

<p>FuelTypes</p>
<img width="961" height="240" alt="image" src="https://github.com/user-attachments/assets/5373b9e8-3118-4d8b-b9ea-ab9885d1fa32" />

<p>SalesTransactions</p>
<img width="965" height="468" alt="image" src="https://github.com/user-attachments/assets/84b8f870-96b7-4ac0-bcad-0d45ab98e98e" />

<p>StoreSales</p>
<img width="965" height="221" alt="image" src="https://github.com/user-attachments/assets/a33615e8-1109-463c-8105-7848c450e317" />

<p>SaleDetails</p>
<img width="964" height="279" alt="image" src="https://github.com/user-attachments/assets/7f758ee0-c696-4316-81be-37d2aa887b78" />

<p>Users</p>
<img width="962" height="260" alt="image" src="https://github.com/user-attachments/assets/e4a6648c-e163-43d9-8aae-fc25c0a3729c" />

<h3>Stored Procedure Builds:</h3>
<img width="969" height="813" alt="image" src="https://github.com/user-attachments/assets/fb9f6479-9dd2-4777-9071-8242ab960b49" />
<img width="961" height="126" alt="image" src="https://github.com/user-attachments/assets/63ce5832-5e4a-437f-9168-a335cae7b2cb" />


## 3. Database Operations Scripts

### Fuel Types
```sql
INSERT INTO fueltypes (FuelName, CurrentPricePerLiter, CurrentStockLiters, LastUpdated) VALUES (?, ?, ?, CURRENT_TIMESTAMP);
SELECT * FROM fueltypes WHERE FuelTypeID = ?;
SELECT * FROM fueltypes;
UPDATE fueltypes SET FuelName = ?, CurrentPricePerLiter = ?, CurrentStockLiters = ?, LastUpdated = CURRENT_TIMESTAMP WHERE FuelTypeID = ?;
DELETE FROM fueltypes WHERE FuelTypeID = ?;
```

### Employees
```sql
INSERT INTO employees (FirstName, LastName, Position, TC_KimlikNo, HireDate, Salary, Shift, Email) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
SELECT * FROM employees WHERE EmployeeID = ?;
SELECT * FROM employees;
UPDATE employees SET FirstName = ?, LastName = ?, Position = ?, Salary = ?, Shift = ?, Email = ? WHERE EmployeeID = ?;
DELETE FROM employees WHERE EmployeeID = ?;
```

### Sales Transactions
```sql
INSERT INTO salestransactions (TransactionDate, EmployeeID, CustomerID, FuelTypeID, PumpID, LitersSold, PriceAtSale, TotalAmount, PaymentMethod, DiscountApplied) VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?, ?);
SELECT * FROM salestransactions WHERE EmployeeID = ?;
SELECT * FROM salestransactions WHERE PumpID = ?;
DELETE FROM salestransactions WHERE TransactionID = ?;
SELECT * FROM salestransactions WHERE house_id = ? AND status = 'ACTIVE' AND end_date >= CURRENT_DATE();
```

### Fuel Pumps
```sql
INSERT INTO fuelpumps (FuelTypeCapacity, LastMaintenanceDate, Status, FuelTypeID) VALUES (?, ?, ?, ?);
SELECT * FROM fuelpumps WHERE PumpID = ?;
SELECT * FROM fuelpumps;
UPDATE fuelpumps SET FuelTypeCapacity = ?, LastMaintenanceDate = ?, Status = ?, FuelTypeID = ? WHERE PumpID = ?;
DELETE FROM fuelpumps WHERE PumpID = ?;
```

### Users
```sql
INSERT INTO users (EmployeeID, Username, Password, IsActive) VALUES (?, ?, ?, ?);
SELECT * FROM users WHERE UserID = ?;
SELECT * FROM users WHERE Username = ? AND Password = ? AND IsActive = 1;
UPDATE users SET Username = ?, Password = ?, IsActive = ? WHERE UserID = ?;
DELETE FROM users WHERE UserID = ?;
```

### Employee Phones
```sql
INSERT INTO employeephones (EmployeeID, PhoneNumber) VALUES (?, ?);
SELECT * FROM employeephones WHERE EmployeeID = ?;
SELECT * FROM employeephones;
UPDATE employeephones SET PhoneNumber = ? WHERE PhoneID = ?;
DELETE FROM employeephones WHERE PhoneID = ?;
```

### Store Sales
```sql
INSERT INTO storesales (SaleDate, EmployeeID, TotalAmount) VALUES (CURRENT_TIMESTAMP, ?, ?);
SELECT * FROM storesales WHERE StoreID = ?;
SELECT * FROM storesales WHERE EmployeeID = ?;
UPDATE storesales SET TotalAmount = ?, SaleDate = ? WHERE StoreID = ?;
DELETE FROM storesales WHERE StoreID = ?;
```

### Sale Details
```sql
INSERT INTO saledetails (StoreID, ProductID, Quantity, PriceAtSale) VALUES (?, ?, ?, ?);
SELECT * FROM saledetails WHERE StoreID = ?;
SELECT * FROM saledetails WHERE DetailID = ?;
UPDATE saledetails SET ProductID = ?, Quantity = ?, PriceAtSale = ? WHERE DetailID = ?;
DELETE FROM saledetails WHERE DetailID = ?;
```



















