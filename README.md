# JMeter Test Plan for CartController Load Testing

This guide describes how to set up and run a JMeter load test for a CartController REST API. It covers test data preparation, JMeter configuration, test execution, and result analysis—including handling special API responses during load testing.

---

## 📝 Overview

This JMeter test plan simulates concurrent user activity on a CartController's REST API endpoints. It helps identify performance bottlenecks and ensures the application can handle expected loads.

---

## 🧩 Covered API Endpoints

The following endpoints are included in the test plan:

- **GET `/api/cart/{userId}`**  
  Fetches the details of a user's shopping cart.

- **POST `/api/cart/{userId}/products`**  
  Adds a product to a user's cart.

- **DELETE `/api/cart/{userId}/products/{productCode}`**  
  Removes a specific product from a user's cart.

- **DELETE `/api/cart/{userId}`**  
  Clears all items from a user's cart.

---


## 🛠️ 0. Download and Set Up JMeter

Before starting the test plan configuration, you need to have Apache JMeter installed on your system.

### a. Download JMeter
- Go to the [Apache JMeter Downloads page](https://jmeter.apache.org/download_jmeter.cgi).
- Download the latest version of JMeter (zip or tgz archive).

### b. Extract and Set Up JMeter
- Extract the downloaded archive to a preferred location on your computer.
- No installation is required; JMeter runs directly from the extracted folder.

### c. Start JMeter
- Navigate to the `bin` directory inside the extracted JMeter folder.
- On Windows: Double-click `jmeter.bat` to launch the JMeter GUI.
- On Mac/Linux: Run `./jmeter` from a terminal.

---




## 📂 1. Prepare Test Data CSV File

Create a file named `cart_test_data.csv` in the same directory as your JMeter `.jmx` file. This file provides dynamic data for your test requests.

**cart_test_data.csv**
```
userId,productCode,quantity
1,P001,2
2,P003,1
3,P005,4
4,P002,1
5,P004,3
```

---

## 🧪 2. JMeter Test Plan Configuration

### a. Create Thread Group

- **Right-click on Test Plan → Add → Threads (Users) → Thread Group**
- Name: `Cart API Load Test`
- Configure:
  - **Number of Threads (users):** 10
  - **Ramp-Up Period (seconds):** 10
  - **Loop Count:** 5

This simulates 10 users making 5 requests each over 10 seconds.

### b. Add CSV Data Set Config

- **Right-click on Thread Group → Add → Config Element → CSV Data Set Config**
- Configure:
  - **Filename:** cart_test_data.csv
  - **Variable Names:** userId,productCode,quantity
  - **Delimiter:** `,`
  - **Ignore first line:** `True`
  - **Recycle on EOF:** `True`
  - **Stop thread on EOF:** `False`
  - **Sharing mode:** `All threads`

### c. Add HTTP Request Defaults

- **Right-click on Thread Group → Add → Config Element → HTTP Request Defaults**
- Set:
  - **Server Name or IP:** `localhost`
  - **Port Number:** `7411`
  - **Protocol:** `http`

### d. Add HTTP Samplers

#### 1. GET Cart Summary

- **Right-click on Thread Group → Add → Sampler → HTTP Request**
- **Name:** GetCart
- **Method:** GET
- **Path:** `/api/cart/${userId}`

#### 2. POST Add Product to Cart

- **Right-click on Thread Group → Add → Sampler → HTTP Request**
- **Name:** AddToCart
- **Method:** POST
- **Path:** `/api/cart/${userId}/products`
- **Parameters:**
  - `productCode` = `${productCode}`
  - `quantity` = `${quantity}`

#### 3. DELETE Product From Cart

- **Right-click on Thread Group → Add → Sampler → HTTP Request**
- **Name:** RemoveFromCart
- **Method:** DELETE
- **Path:** `/api/cart/${userId}/products/${productCode}`

#### 4. DELETE Clear Cart

- **Right-click on Thread Group → Add → Sampler → HTTP Request**
- **Name:** ClearCart
- **Method:** DELETE
- **Path:** `/api/cart/${userId}`

---

### 💡 Special Handling for "Cart not found" (JSR223 PostProcessor)

Sometimes, after clearing the cart or similar actions, `GetCart` might return a `400 Bad Request` with `"Cart not found"`. This is a valid state, not a bug.

To prevent JMeter from marking these as failures:

- **Right-click on GetCart (or any relevant sampler) → Add → Post Processors → JSR223 PostProcessor**
- **Language:** Groovy
- **Script:**
  ```groovy
  // Mark "Cart not found" 400 responses as success
  if (prev.getResponseCode() == "400" && prev.getResponseDataAsString().contains("Cart not found")) {
      prev.setSuccessful(true)
  }
  ```

**Outcome:** These responses will be marked as successful in JMeter reports.

---

### e. Add Listeners for Reports

- **Right-click on Thread Group → Add → Listener → [choose one/more]:**
  - View Results Tree
  - Summary Report
  - Aggregate Report
  - Graph Results
  - Response Time Graph

---

## 🔥 3. Run the Test

- Save your test plan (e.g., `cart_test.jmx`).
- Click the ▶️ Start button (green play icon) in JMeter.
- Observe results in the Listener tabs.

---

## 📊 4. Analyze Results

After running the test, examine the Listener reports for:

| Metric          | Description                                                               |
|-----------------|---------------------------------------------------------------------------|
| **Avg. Response**   | Average time for each request                                           |
| **Min/Max**         | Fastest and slowest response times                                      |
| **Throughput**      | Requests processed per second                                           |
| **Error %**         | Percentage of requests with errors (excluding valid 400s as above)      |
| **90/95/99% Percentile** | Time under which 90%, 95%, or 99% of requests finished             |

---

## 🧰 Optional: CLI Run with HTML Report

For advanced reporting, run JMeter from the command line:

```sh
jmeter -n -t cart_test.jmx -l results.jtl -e -o report
```

- `-n`: Non-GUI mode
- `-t cart_test.jmx`: Test plan file
- `-l results.jtl`: Results file
- `-e`: Generate HTML report
- `-o report`: Output directory for HTML report

**Example (Windows):**
```
jmeter.bat -n -t "D:\Post-New-Learnings\cart_perform_test\cart_with_redis\Cart API Load Test.jmx" -l "D:\Post-New-Learnings\cart_perform_test\cart_with_redis\results.jtl" -e -o "D:\Post-New-Learnings\cart_perform_test\cart_with_redis\report"
```

After the test, open `report/index.html` in your browser to view the dashboard.

---

## 🛠️ Tips

- Adjust the number of threads, ramp-up, and loop count to simulate different load scenarios.
- Keep `cart_test_data.csv` updated with realistic data.
- Use listeners wisely—too many can slow down test execution.
- Always analyze percentiles, not just average response times, for real user experience insights.

---


![Screenshot (164)](https://github.com/user-attachments/assets/aa007f35-8fd2-4058-87e1-261710a6b4bb)



