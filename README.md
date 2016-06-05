# FontVerter
Java library for converting fonts. 

## Currently supports
Bare CFF -> OpenType/OTF and WOFF 1.0 & 2.0
(Bare CFFs are found in PDF files and can be used as an OTF's CFF table with extra conversion work)

OpenType/OTF/TTF-> WOFF 1.0 & 2.0

WOFF 1.0 -> OpenType and WOFF 2.0

## Maven
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

## Usage
##### Converting a font (in this case to OTF)
  ```java
  FVFont font = FontVerter.convertFont(inputFontFile, FontVerter.FontFormat.OTF);
  FileUtils.writeByteArrayToFile(new File("MyFont.otf"), font.getData());
  ```
##### Reading a font file
  ```java
  File file = new File("FontVerter+SimpleTestFont.otf");
  FVFont font = FontVerter.readFont(file);
  ```  

##### PDF Font Extractor command line
    PdfFontExtractor <PDF file or directory containing PDF files>

    Options:
    -ff=<format> Font format to save the fonts as. <format> = WOFF1, WOFF2, OTF. Defaults to OTF.
    -dir=<path> Directory to extract to. <path> = font extract directory. Defaults to {current dir}/fonts/

##### PDF font extractor programatically
  ```java
  File pdf = new File("test.pdf");
  PdfFontExtractor extractor = new PdfFontExtractor();

  // extract directly to a directory
  extractor.extractFontsToDir(pdf, TestUtils.tempOutputPath);

  // or extract to list of FVFonts for further manipulation
  List<FVFont> fonts = extractor.extractToFVFonts(doc);```