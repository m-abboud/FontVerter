# FontVerter
[![Java CI with Maven](https://github.com/katjakarhu/FontVerter/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/katjakarhu/FontVerter/actions/workflows/maven.yml)


Java library for converting and manipulating various font formats, normalizing invalid fonts and extracting fonts from PDFs.

## Currently supports
- Bare CFF -> OpenType/OTF and WOFF 1.0 & 2.0 

- OpenType/OTF/TTF -> WOFF 1.0 & 2.0

- WOFF 1.0 -> OpenType and WOFF 2.0

- EOT (uncompressed v2 only) -> OpenType

#### With PdfFontExctractor utility
- Supports extracting PostScript Type0/Compsoite, Bare CFF and TTF fonts. 
- PdfFontExtractor will normalize TTF fonts to make sure they are valid in all major web browsers.

##### Can only convert these types with PdfFontExtractor utility
- PostScript Type0/Composite -> OpenType/TTF and WOFF 1 & 2

## Maven (On Maven Central)
    <dependencies>
		<dependency>
			<groupId>net.mabboud.fontverter</groupId>
			<artifactId>FontVerter</artifactId>
			<version>1.2.22</version>
		</dependency>
    </dependencies>

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

##### Attempt to normalize and fix an invalid font
```java
File file = new File("FontVerter+SimpleTestFont.otf");
FVFont font = FontVerter.readFont(file);

if (!font.isValid())
    font.normalize();

FileUtils.writeByteArrayToFile(new File("fixed-font.otf"), font.getData());
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
List<FVFont> fonts = extractor.extractToFVFonts(doc);
```
