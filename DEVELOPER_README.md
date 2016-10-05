# Developer Guide/Readme/Plans
Random notes in the unlikely event someone else needs or wants to modify this project.

#### Where OpenType calculations for metrics and stuff happen
In the OpenTypeTable subclasses they have this questionably named method called 'normalize()' that figure out stuff like HMTX's glyph advance widths and bearings (which happen to be often critical for correct rendering IIRC)


##### DataTypeProperty Annotations
So you might notices these DataTypeProperty annotation things over some font/table fields

```java
  @DataTypeProperty(dataType = DataTypeProperty.DataType.FIXED32)
```  

I added these annotations so you don't need a separate read/parse and write method and can just use a single line of code above the fields, makes sense right.
It works perfectly for somewhat simple cases like OS2WinMetricsTable where the table spec is just a bunch of normal data types in a row, but for more complex cases
like cmap tables the annotations fall short and currently have to use manual read/write methods,

Overall think they were just slightly more work than worth to add. But should you be adding full parsing support for currently unimplemented table types I'd recommend taking a look.


##### Bunch of specific converters vs some sort of master font adapter for conversion
Currently using specific converters for font conversion but would probably be a better idea to parse the fonts to some master font adapter that can save arbitrarily to different formats,
however that's a lot of boring work since fonts have a ton of crap in them and it remains on my back burner.


##### PDFBox/FontBox dependency
Currently this project relies on PDFBox/FontBox for parsing Type0/PostScript Composite fonts, Bare CFF fonts and the PDFFontExtractor tool before converting them to a more useable font format. 
Pretty big dependency and should be reduced to an optional one for just the PDFFontExtractor tool eventually. Bare CFF should be easy unsure about Type0.

###### Contact me at <maddie.abboud@gmail.com> if I've vanished and you need maven creds or something.
