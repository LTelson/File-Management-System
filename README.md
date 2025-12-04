--File Management System--

**Specifications:**
Language: Java (+ Swing)

**Core Functionality:**
Create:
  New File
  New Folder
Read:
  Browse directories (up and down)
  View file contents in editor window
Delete:
  Remove files and directories 
  (Recursive delete)
Rename:
  Rename files and directories

**Navigation:**
Directory Tree:
  Double click to open directories/files
  "Up" button to move to parent directory
  Current path display

**Text File Viewer / Editor**
- Full text preview in right window
- Editable text
- Save button for changes

**Feedback**
- Bottom status bar for operation result
- Error message through dialog window



**SANDBOX SAFETY**
  Upon startup, a new dedicated directory will be created under <user.home>/FileSystemSandbox
  All operations are permitted within this directory.

**How to run?**
Requirements:
 - Java 17+
 - Maven
 - IntelliJ IDEA (not strict, but was my IDE of choice)

1. Clone the repository
2. Open project
3. Build using Maven
4. Run "App.main()"
