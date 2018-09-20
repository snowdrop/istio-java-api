package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

// Expects directory names in the form: 'istio.io/istio/mixer/adapter/dogstatsd/config'
// Extracts salient part from directory path which depends on type of target directory and creates a comma-separated line entry
func main() {
	//
	scanner := bufio.NewScanner(os.Stdin)

	const projectPkgPrefix = "me.snowdrop.istio."
	const projectJsonPrefix = "istio_"
	const sep = string(os.PathSeparator)

	if len(os.Args) != 2 {
		panic("Expecting one of 'api' | 'adapter' | 'template' as sole argument")
	}

	kind := os.Args[1]

	var pkgPrefix, jsonPrefix string
	var pkgAssemblyStrategy, jsonAssemblyStrategy func(prefix string, component string) string
	switch kind {
	case "api":
		pkgPrefix = projectPkgPrefix + "api."
		jsonPrefix = projectJsonPrefix
		pkgAssemblyStrategy = concatenate
		jsonAssemblyStrategy = concatenate
	case "adapter":
		pkgPrefix = projectPkgPrefix + "adapter."
		jsonPrefix = projectJsonPrefix + "adapter_"
		pkgAssemblyStrategy = concatenate
		jsonAssemblyStrategy = concatenate
	case "template":
		pkgPrefix = projectPkgPrefix + "mixer.template."
		jsonPrefix = projectJsonPrefix + "mixer_"
		pkgAssemblyStrategy = concatenate
		jsonAssemblyStrategy = concatenate
	}

	//istio.io/api/networking/v1alpha3
	fmt.Println("# " + kind)
	var component string
	for scanner.Scan() {
		line := scanner.Text()
		// remove any trailing separator
		if strings.HasSuffix(line, sep) {
			line = line[:len(line)-len(sep)]
		}
		elements := strings.Split(line, sep)
		for i, value := range elements {
			if value == kind {
				if kind != "api" {
					component = elements[i+1]
				} else {
					component = elements[i+1] + "." + elements[i+2]
				}
				break
			}
		}

		fmt.Println(line + "," + pkgAssemblyStrategy(pkgPrefix, component) + "," +
			jsonAssemblyStrategy(jsonPrefix, strings.Replace(component, ".", "_", -1)) + "_")
	}

	if err := scanner.Err(); err != nil {
		fmt.Fprintln(os.Stderr, "error:", err)
		os.Exit(1)
	}
}

func concatenate(prefix string, element string) string {
	return prefix + element
}

func prefixOnly(prefix string, element string) string {
	// if we want a prefix only remove any trailing . but leave _ as-is
	if strings.HasSuffix(prefix, ".") {
		prefix = prefix[:len(prefix)-1]
	}
	return prefix
}
