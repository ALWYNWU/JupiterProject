# Jupiter

# Intro

- Jupiter is a personalization based recommendation engine for events, provided an interactive web page for users to search events, update preference and view recommended events.
- The project is on the separation of front-end and backend, the backend  is Java native, includes three servlets, utilized MySQL to store user preference and event information.
- Developed a web service using (Java servlet, REST API) to fetch event data from TicketMaster API.
- Designed a cotent-based recommendation algorithm to recomment events based on the categories of user’s favorite events, and display them from near to far according to the distance from the user.
- Recommendation algorithm will calculates the weight of the categories of event that the user favorites, and preferentially recommend the category of events with high weight.
- Deploy on AWS EC2 [http://3.98.58.39/Jupiter/#](http://3.98.58.39/Jupiter/#)

**Homepage**

![Untitled](Jupiter%209a27f2c99a8d458ca37afc7a4499c48b/Untitled.png)

**Favorites**

![Untitled](Jupiter%209a27f2c99a8d458ca37afc7a4499c48b/Untitled%201.png)

**Recommendation**

![Untitled](Jupiter%209a27f2c99a8d458ca37afc7a4499c48b/Untitled%202.png)

# Project Details

## Architecture

![Untitled](Jupiter%209a27f2c99a8d458ca37afc7a4499c48b/Untitled%203.png)

## Database Design

![Untitled](Jupiter%209a27f2c99a8d458ca37afc7a4499c48b/Untitled%204.png)

### Item Table

| item_id (PRIMARY KEY) | name | rating | address | image_url | url | distance | local_date |
| --- | --- | --- | --- | --- | --- | --- | --- |
|  |  |  |  |  |  |  |  |

```java
sql = "CREATE TABLE items ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "name VARCHAR(255),"
					+ "rating FLOAT,"
					+ "address VARCHAR(255),"
					+ "image_url VARCHAR(255),"
					+ "url VARCHAR(255),"
					+ "distance FLOAT,"
					+ "local_date VARCHAR(255),"
					+ "PRIMARY KEY (item_id))";
```

### Category Table

| item_id  REFERENCES items(item_id) | category | PRIMARY KEY (item_id, category) |
| --- | --- | --- |
|  |  |  |

```java
sql = "CREATE TABLE categories ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "category VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, category),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id))";
```

### User Table

| user_id PRIMARY KEY | password | first_name | last_name |
| --- | --- | --- | --- |
|  |  |  |  |

```java
sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id))";
```

### History Table

| user_id  REFERENCES items(item_id) | item_id REFERENCES users(user_id)) | last_favor_time | PRIMARY KEY (user_id, item_id) |
| --- | --- | --- | --- |
|  |  |  |  |

```java
sql = "CREATE TABLE history ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (user_id, item_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id))";
```