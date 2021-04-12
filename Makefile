JLINK := $(JAVA_HOME)/bin/jlink

SRCS := $(wildcard src/*.java src/*/*.java)
JAVAFX_MODULES := javafx.controls,javafx.base,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web

ARGS =
OPTS := -p $(JAVAFX_PATH)/lib --add-modules $(JAVAFX_MODULES)
JAVA_OPTS := $(OPTS) -classpath bin
JAVAC_OPTS := $(OPTS) -sourcepath src -d bin
JLINK_OPTS := --compress=2 --add-modules $(JAVAFX_MODULES) --module-path $(JAVAFX_PATH)/jmods

run: Main.class
	cp -r src/fxml bin
	java $(JAVA_OPTS) Main $(ARGS)

Main.class: $(SRCS)
	javac $(JAVAC_OPTS) src/Main.java

dist-macos: clean
	$(call gen-dist,macos,java)

dist-win: clean
	$(call gen-dist,win,java.exe)

clean:
	rm -rf bin dist **/*.args

clean-hard:
	make clean
	rm -rf .*.*.un* .*.un* **/.*.*.un* **/.*.un* **/**/.*.*.un* **/**/.*.un*

# 配布ファイル生成マクロ
define gen-dist
	# ディレクトリ整理
	mkdir -p bin dist

	# ビルド
	javac $(JAVAC_OPTS) src/Main.java
	cp -r src/fxml .
	jar cvfm dist/Quantum-$1.jar MANIFEST.MF -C bin . fxml
	rm -rf fxml 

	# JRE生成
	$(JLINK) $(JLINK_OPTS):$(JMODS_PATH) --output dist/runtime-$1

	# Makefile生成
	echo "$1:\n\tchmod +x runtime-$1/bin/*\n\truntime-$1/bin/$2 -jar Quantum-$1.jar\n\n" > dist/Makefile

	# README生成
	echo "# Quantum ($1)\n\n## HowToUse\nrun \`make\`" > dist/README.md

	# LICENCEコピー
	cp LICENCE dist	

	# .class削除
	rm -rf bin
endef
