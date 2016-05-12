# FontVerter
Java library for converting fonts. 

# Currently supports
Bare CFF -> OpenType/OTF (CFF flavored). (Bare CFFs are found in PDF files and can be used as an OTF's CFF table with extra conversion work)

Bare CFF -> WOFF 1.0

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

