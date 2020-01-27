# OreNo Calculator

Finally, after such a long time, I finally implemented a GUI version of my calculator in Java (using JavaFX).

## How to run

### Prerequisites

* Java (latest version)
* JavaFX (already included in Oracle Java)
* Apache Maven

### Instructions

From the directory you want it downloaded in, run

```sh
git clone https://github.com/Colocasian/ore-no-calculator.git
```

And then, after it is downloaded,

```sh
cd ore-no-calculator
mvn javafx:run
```

And it should startup.

On an update, run the following command instead,

```sh
mvn clean javafx:run
```

## TODO

- [x] Unary operators
- [ ] Variable suppport
- [ ] Functions
- [ ] Scientific (like) on-screen key layout

