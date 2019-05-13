package main

import (
		"os"
		"log"
		"fmt"
		"bytes"
		"regexp"
		"errors"
		"context"
		"strings"
    "net/http"
		"io/ioutil"

		"github.com/gorilla/rpc"
		"github.com/gorilla/mux"
    "github.com/elazarl/goproxy"
		"github.com/gorilla/rpc/json"
		"github.com/gorilla/rpc/v2/json2"
)



var requests int
var responses int
var proxy *goproxy.ProxyHttpServer
var srv *http.Server
var gui = false


type Proxy struct {}
type ProxyArgs struct {
		Ip string
}
type ProxyReply int

// a type representing the counter of reqs and resps
type reqrespcounter struct {
		req int
		resp int
}

// global variable storing the counter
var counter = reqrespcounter{0,0};

/*
* JSON call to start the RPC-JSON server
*/
func (h *Proxy) Start(r *http.Request, args *ProxyArgs, reply *ProxyReply) error {
		// first things first, let's validate the IP address
		if !(validIP4port(args.Ip)) {
			//fmt.Printf("Error ip format");
			return errors.New("Error IP:PORT format")
		}
		// create a new HTTP proxy handler
    proxy = goproxy.NewProxyHttpServer()

		// create an HTTP server and give it the proxy handler
		srv = &http.Server{
			Addr:           args.Ip,
			Handler:        proxy,
		}

		// define the function to execute when a request is received by the proxy handler
		proxy.OnRequest().DoFunc(
    func(r *http.Request,ctx *goproxy.ProxyCtx)(*http.Request,*http.Response) {
			if r != nil {
				// I'm counting the requests ... obviously I'm missing HTTPS content
				// but it doesn't matter, this is just an experiment
				requests = requests + 1; //r.ContentLength
				// random log text
				//fmt.Printf("Req counter: %d\n", requests)
				counter.req = requests;
				sendRpcMsg("updateRequestsCounter", [1]int{requests});
			}
      return r,nil
    })

		// define the function to execute when a response is received by the proxy handler
		proxy.OnResponse().DoFunc(
			func(r *http.Response, ctx *goproxy.ProxyCtx)*http.Response{
				if r != nil {
					// I'm counting the responses ... obviously I'm missing HTTPS content
					// but it doesn't matter, this is just an experiment
					responses = responses + 1;// r.ContentLength
					// random log text
					//fmt.Printf("Rep size: %d\n",responses)
					counter.resp = responses;
					sendRpcMsg("updateResponsesCounter", [1]int{responses});
				}
				return r
		})
		// some random log text
		fmt.Printf("Starting proxy\n")

		// start the HTTP proxy
    go srv.ListenAndServe()

		// the response to the JSON call
		*reply = ProxyReply(0)
    return nil
	}

	/*
	* JSON call to stop the JSON-RPC server
	*/
func (h *Proxy) Stop(r *http.Request, args *ProxyArgs, reply *ProxyReply) error {
		// some random log text
		fmt.Printf("Stopping proxy\n")

		// shutdown the HTTP proxy
		srv.Shutdown(context.Background())
		
		// the response to the JSON call
		*reply = ProxyReply(0)
    return nil
	}

func validIP4port(ipAddress string) bool {
       ipAddress = strings.Trim(ipAddress, " ")
				// this regexp validates the string has the format IP:PORT
       re, _ := regexp.Compile(`^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):(?:6553[0-5]|655[0-2][0-9]|65[0-4][0-9][0-9]|6[0-4][0-9][0-9][0-9]|[1-5]?[0-9]?[0-9]?[0-9]?[0-9])?$`)
       if re.MatchString(ipAddress) {
               return true
       }
       return false
}


	/*
	* Initialize the JSON-RPC server
	*/
func initRpcServer(){
	s := rpc.NewServer()
	s.RegisterCodec(json.NewCodec(), "application/json")
	s.RegisterCodec(json.NewCodec(), "application/json;charset=UTF-8")
	arith := new(Proxy)
	s.RegisterService(arith, "")
	r := mux.NewRouter()
	r.Handle("/rpc", s)
	if e := http.ListenAndServe("127.0.0.1:1234", r); e != nil{
		if strings.Contains(e.Error(),"already in use") && gui {
			sendRpcMsg("fatal",[1]string{"RPC port 1234 in use, terminate it and try again"})
		}else{
			log.Fatal(e)
		}
	}
}

/*
* This method sends a JSON-RPC message
*/
func sendRpcMsg(method string, params interface{}){ // note-to-self: params if an empty go interface -> https://tour.golang.org/methods/14
	// the address of the JSON-RPC server of the GUI
	url := "http://127.0.0.1:1235/proxinygui"

	// create a JSON encod of the message to send
	message, err := json2.EncodeClientRequest(method, params)
	
	if err != nil {
		log.Fatalf("%s", err)
	}

	// create a new HTTP request
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(message))
	if err != nil {
		log.Fatalf("%s", err)
	}
	// add header to the HTTP request
	req.Header.Set("Content-Type", "application/json")

	// perform the HTTP request
	client := new(http.Client)
	resp, err := client.Do(req)
	if err != nil {
		log.Fatalf("Error in sending request to %s. %s", url, err)
	}
	defer resp.Body.Close()
	body, err := ioutil.ReadAll(resp.Body)

	// just some random test decoding the response
	// var result rpcexample.Result
	// err = json.DecodeClientResponse(resp.Body, &result)
	// if err != nil {
	// 	log.Fatalf("Couldn't decode response. %s", err)
	// }
	//fmt.Printf(" -> %s", body)
}

func main() {

	// the gui param means the proxy was spawned by its java gui
	if len(os.Args) > 1 && os.Args[1] == "gui" {
		gui = true
	}
	initRpcServer();

}


