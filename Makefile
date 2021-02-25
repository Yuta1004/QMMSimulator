SRCS := $(wildcard src/*.java src/*/*.java)
JAVAFX_MODULES := javafx.controls,javafx.base,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web

ARGS =
OPTS := -p $(JAVAFX_PATH)/lib --add-modules $(JAVAFX_MODULES)
JAVA_OPTS := $(OPTS) -classpath bin
JAVAC_OPTS := $(OPTS) -sourcepath src -d bin

run: Main.class
	cp -r src/fxml bin
	java $(JAVA_OPTS) Main $(ARGS)

Main.class: $(SRCS)
	javac $(JAVAC_OPTS) src/Main.java

clean:
	rm -rf bin dist **/*.args

clean-hard:
	make clean
	rm -rf .*.*.un* .*.un* **/.*.*.un* **/.*.un* **/**/.*.*.un* **/**/.*.un*

