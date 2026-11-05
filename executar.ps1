param([ValidateSet('menu', 'testes', 'demo')][string]$Modo = 'menu')
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
[Console]::InputEncoding = New-Object System.Text.UTF8Encoding
[Console]::OutputEncoding = New-Object System.Text.UTF8Encoding
New-Item -ItemType Directory -Force -Path 'build/classes' | Out-Null
$fontes = Get-ChildItem -LiteralPath 'src/smartcity' -Filter '*.java' | ForEach-Object FullName
& javac --release 8 -encoding UTF-8 -d build/classes $fontes
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação. Instale um JDK 9 ou superior.' }
$argumentos = @('-Dfile.encoding=UTF-8', '-cp', 'build/classes', 'smartcity.Main')
if ($Modo -eq 'testes') { $argumentos += '--testes' }
if ($Modo -eq 'demo') { $argumentos += '--demo' }
& java @argumentos
if ($LASTEXITCODE -ne 0) { throw 'A aplicação retornou erro.' }
