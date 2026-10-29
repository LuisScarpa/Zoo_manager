
mkdir -p out
find src -name "*.java" > out/fontes.txt
javac -encoding UTF-8 -d out -cp "lib/*" @out/fontes.txt && echo "Compilado com sucesso."
