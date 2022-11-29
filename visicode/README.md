## How to Debug This Extension on your vs code

1. `npm install`
2. Start Debugging! Simple as this.

## How to Use The Extension

Include noteID as a block comment in your source file

```
//!<visicode>
// [Project Endpoint]/api/note/5704134103662592
//!</visicode>
```

you can use `example.cpp`,  write down your editor or viewer ID in `settings -> visicode-alpha.viewerOrEditorId`, and then execute command "VisiCode: Render Source Code".