# files = all files in the src directory, assets directory and README
# destination = ex4.zip
# compress the files in the src directory, assets directory and the README files into ex4.zip
# the zip file should be created on the desktop

# create new list of files
$files = @()
$files += Get-Item -Path .\src\pepse
$files += Get-Item -Path .\assets
$files += Get-Item -Path .\README
$dest = "$env:USERPROFILE\Desktop\ex4.zip"

Compress-Archive -Path $files -DestinationPath $dest -Force