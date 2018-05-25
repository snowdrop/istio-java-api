package main

import (
	"bufio"
	"os"
	"strings"
	"fmt"
	"regexp"
)

func main() {
	// istio.io/istio/mixer/adapter/dogstatsd/config
	scanner := bufio.NewScanner(os.Stdin)

	sche

	adapterRE, _ := regexp.Compile("p([a-z]+)ch")

	for scanner.Scan() {
		ucl := strings.ToUpper(scanner.Text())

		fmt.Println(ucl)
	}

	if err := scanner.Err(); err != nil {
		fmt.Fprintln(os.Stderr, "error:", err)
		os.Exit(1)
	}
}
