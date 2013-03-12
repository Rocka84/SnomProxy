#!/bin/sh
xjc -d src/ -p xml.snom src/xml/snom/schemes/Snom.xsd
sed -i '/\s*\(\/\?\*\|\/\/\).*$/d' src/xml/snom/*.java
exit 0
