# FontVerter
Java library for converting fonts. 

# Currently supports
Bare CFF -> OpenType/OTF (CFF flavored). (Bare CFFs are found in PDF files and can be used as an OTF's CFF table with extra conversion work)

Bare CFF -> WOFF 1.0 & 2.0

OTF -> WOFF 1.0 & 2.0

# Usage
##### Converting a font to OTF
  ```java
  FontAdapter font = FontVerter.convertFont(inputFontFile, FontVerter.FontFormat.OTF);
  FileUtils.writeByteArrayToFile(new File("MyFont.otf"), font.getData());
  ```
##### Parsing an arbitrary font file
  ```java
  File file = new File("FontVerter+SimpleTestFont.otf");
  FontAdapter font = FontVerter.readFont(file);
  ```  

#### Maven
    <dependencies>
		<dependency>
			<groupId>org.fontverter</groupId>
			<artifactId>FontVerter</artifactId>
			<version>1.0</version>
		</dependency>
    </dependencies>

    <repositories>
        <repository>
            <id>bintray-m-abboud-fontverter</id>
            <name>bintray</name>
            <url>https://dl.bintray.com/m-abboud/maven/</url>
        </repository>
    </repositories>