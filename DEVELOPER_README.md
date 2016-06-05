# Developer Guide/Readme/Plans
Random notes in the unlikley event someone else needs or wants to modify this project.

##### DataTypeProperty Annotations
So you might notices these DataTypeProperty annotation things over some font/table fields
'''java@DataTypeProperty(dataType = DataTypeProperty.DataType.FIXED32)'''

I added these annotations so you don't need a seperate read/parse and write method and can just use a single line of code above the fields, makes sense right.
It works perfectly for somewhat simple cases like OS2WinMetricsTable where the table spec is just a bunch of normal data types in a row, but for more complex cases
like cmap tables the annotations fall short and currently have to use manual read/write methods,

Overall think they were just slightly more work than worth to add. But should you be adding full parsing support for currently unimplemented table types I'd recomend taking a look.

##### Bunch of specific converters vs some sort of master font adapter for conversion
Currently using specific converters for font conversion but would probabley be a better idea to parse the fonts to some master font adapter that can save arbitrarily to different formats,
however that's a lot of boring work since fonts have a ton of crap in them and it remains on my backburner.

##### Hardley any Java Docs what is this??
You may notice the lack of java docs in this project, it's on purpose. IMO java docs are an unncessaray evil that amounts to little more than duplicate code,
you modify some code and now you have to change two things instead of one. I try to name my methods and parameter names as accuratley as possible so there is no need to resort to a comment and it's quicker to figure out
what a method or class does if you don't have to read a giant block comment to understand it. See Uncle Bob's opinion on the matter.

However some public interfaces in this project still have them for presentation's sake only.

##### PDFBox/FontBox dependency
Currently this project relies on PDFBox/FontBox for parsing Type0/PostScript Composite fonts, Bare CFF fonts and the PDFFontExtractor tool. Pretty big dependency
and should be reduced to an optional one for just the PDFFontExtractor tool eventually. Bare CFF should be easy unsure about Type0.

###### Contact me at <matt.w.abboud@gmail.com> if I've vanished and you need maven creds or something.