# OreNo Calculator (Legacy)

**Note:** This is the older version of the app, which used Maven, and this version *will not be maintained*. The current application uses Gradle instead, and can be found [here](https://github.com/Colocasian/ore-no-calculator).

Finally, after such a long time, I finally implemented a GUI version of my calculator in Java (using JavaFX).

## How to run

### Prerequisites

* Java (latest version)
* JavaFX (already included in Oracle Java)
* Apache Maven

### Instructions

From the directory you want it downloaded in, run

```sh
git clone https://github.com/Colocasian/legacy-ore-no-calculator.git
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
- [ ] Exponent representation
- [x] Variable suppport
- [x] Functions
- [ ] Custom functions
- [x] Support for multiple panels
- [ ] Detailed custom numeric type implementation

