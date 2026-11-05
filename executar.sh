#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes
javac --release 8 -encoding UTF-8 -d build/classes src/smartcity/*.java
case "${1:-menu}" in
  testes) java -Dfile.encoding=UTF-8 -cp build/classes smartcity.Main --testes ;;
  demo) java -Dfile.encoding=UTF-8 -cp build/classes smartcity.Main --demo ;;
  menu) java -Dfile.encoding=UTF-8 -cp build/classes smartcity.Main ;;
  *) echo 'Use: sh executar.sh [menu|testes|demo]'; exit 1 ;;
esac
