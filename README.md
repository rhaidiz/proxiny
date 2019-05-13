# Proxiny

Just a prototype project to play with some Go and JSON-RPC.

Proxiny is a small proxy and was built as an experimental project to test JSON-RPC with a Java GUI.
The Go proxy is implemented with [elazarl/goproxy](https://github.com/elazarl/goproxy) while the JSON-RPC communication is implemented with [gorillatoolkit](https://github.com/gorilla/rpc) in Go and [arteam/simple-json-rpc](https://github.com/arteam/simple-json-rpc) in the Java.

## Installation

I leave the following instructions if someone wants to give it a try and for future me who will probably forget.

### Core

Compile the Go core:

    git clone https://github.com/rhaidiz/proxiny.git
		cd proxiny/core/
		go build server.go


### Java GUI

Import the Java GUI in IntelliJ and set the following D command-line option:
   -Dproxinycore=/path/to/core/server

