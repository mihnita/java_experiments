Small application to test the SWT `StyledText` abilities.

And especially the clipboard functionality.

This should be enough to build and run:
```
mvn test
```

Then select the text, copy to clipboard (using keyboard shortcuts),
and paste in your application of choice.  
Most interesting is to do it in some app that supports styled text.

Some ideas: MS Word, PowerPoint, MS Write, OpenOffice, LibreOffice, TextEdit (macos)  
Or web based: GMail, Google Docs, Google Slides, Office 360, etc.

"Dump Clipboard" is used for debugging.
Creates a `clipdump` folder under `target` and dumps the content of all the types found in the clipboard.
The `Descript.ion` file contains the mapping from the file name to the clipboard flavor mime type.
