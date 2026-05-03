#!/usr/bin/env bash
# Installs all bundled JARs (those not available on Maven Central) into
# the local Maven repository so that pom.xml can reference them without
# system scope.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
EXT_DIR="$PROJECT_DIR/src/at/redeye/FrameWork/ext_resources"
OPT_DIR="$EXT_DIR/optional"
DB_DIR="$PROJECT_DIR/src/at/redeye/SqlDBInterface/jdbc_driver"

mvn_install() {
    local groupId="$1" artifactId="$2" version="$3" jar="$4"
    echo "Installing $groupId:$artifactId:$version ..."
    mvn install:install-file \
        -Dfile="$jar" \
        -DgroupId="$groupId" \
        -DartifactId="$artifactId" \
        -Dversion="$version" \
        -Dpackaging=jar \
        -DgeneratePom=true \
        -q
}

# proxy-vole (no suitable Central version matches the bundled API)
mvn_install "com.github.markusbernhardt" "proxy-vole"         "1.0-local"  "$EXT_DIR/proxy-vole.jar"

# NetBeans AbsoluteLayout
mvn_install "org.netbeans.external"      "AbsoluteLayout"     "1.0-local"  "$OPT_DIR/AbsoluteLayout.jar"

# jshortcut
mvn_install "com.mjtoolbox"             "jshortcut"           "0.4-local"  "$EXT_DIR/jshortcut-0_4_repacked.jar"

# Oracle JDBC (no public Maven artifact)
mvn_install "com.oracle"                "ojdbc14"             "10.2.0.4"   "$DB_DIR/oracle/ojdbc14.jar"

# RedEye plugins
mvn_install "at.redeye.plugin"          "CommonsLangPlugin"   "1.0-local"  "$OPT_DIR/CommonsLangPlugin.jar"
mvn_install "at.redeye.plugin"          "JerichoHtmlPlugin"   "1.0-local"  "$OPT_DIR/JerichoHtmlPlugin.jar"
mvn_install "at.redeye.plugin"          "DetectDomainPlugin"  "1.0-local"  "$OPT_DIR/DetectDomainPlugin.jar"

# Dongle JARs
mvn_install "at.redeye"                 "Dongle-full-multi"   "1.0-local"  "$OPT_DIR/Dongle-full-multi.jar"
mvn_install "at.redeye"                 "Dongle-full-single"  "1.0-local"  "$OPT_DIR/Dongle-full-single.jar"
mvn_install "at.redeye"                 "Dongle-demo-multi"   "1.0-local"  "$OPT_DIR/Dongle-demo-multi.jar"
mvn_install "at.redeye"                 "Dongle-demo-single"  "1.0-local"  "$OPT_DIR/Dongle-demo-single.jar"

# JDatePicker (bundled builds differ from the sourceforge Maven artifact)
mvn_install "org.jdatepicker"           "JDatePicker"         "1.0-local"  "$OPT_DIR/JDatePicker.jar"
mvn_install "org.jdatepicker"           "JDatePickerPlugin"   "1.0-local"  "$OPT_DIR/JDatePickerPlugin.jar"

# ShellExec
mvn_install "com.jezhumble"             "ShellExec"           "1.0-local"  "$OPT_DIR/ShellExec.jar"

echo ""
echo "All local JARs installed successfully."
