package main

import (
	"fmt"
	"log"
	"net/http"
)

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		traceparent := r.Header.Get("traceparent")
		if traceparent == "" {
			traceparent = "missing"
		}

		log.Printf("received request traceparent=%q", traceparent)
		w.Header().Set("Content-Type", "text/plain; charset=utf-8")
		_, _ = fmt.Fprintf(w, "backend received traceparent=%s\n", traceparent)
	})

	addr := ":8080"
	log.Printf("backend listening on %s", addr)
	log.Fatal(http.ListenAndServe(addr, nil))
}
